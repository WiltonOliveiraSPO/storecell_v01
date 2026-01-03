package view;

import dao.*;
import model.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class FrmNovaVenda extends JFrame {

    private JComboBox<String> cbClientes;
    private JTextField txtBuscarProd;
    private JTable tabela;
    private DefaultTableModel modelo;

    private JLabel lblTotal;
    private JTextField txtDesconto;
    private JComboBox<String> cbPagamento;

    private Connection conn;
    private ProdutoDAO produtoDAO;

    private List<ItemVenda> itensVenda = new ArrayList<>();

    public FrmNovaVenda() {
        setTitle("Nova Venda");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // 🔌 Conexão com banco
        try {
            conn = DBConnection.getConnection();
            produtoDAO = new ProdutoDAO(conn);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao conectar ao banco:\n" + e.getMessage(),
                    "Erro crítico", JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }

        add(criarPainelTopo(), BorderLayout.NORTH);
        add(criarPainelCentral(), BorderLayout.CENTER);
        add(criarPainelInferior(), BorderLayout.SOUTH);
    }

    // ================= TOPO =================
    private JPanel criarPainelTopo() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));

        cbClientes = new JComboBox<>();
        carregarClientes();

        txtBuscarProd = new JTextField(20);

        JButton btnBuscar = new JButton("Pesquisar");
        btnBuscar.addActionListener(e -> buscarProduto());

        p.add(new JLabel("Cliente:"));
        p.add(cbClientes);
        p.add(new JLabel("Produto:"));
        p.add(txtBuscarProd);
        p.add(btnBuscar);

        return p;
    }

    // ================= CENTRO =================
    private JPanel criarPainelCentral() {
        modelo = new DefaultTableModel(
                new Object[] { "ID", "Produto", "Qtd", "Preço", "Subtotal" }, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2; // SOMENTE coluna Qtd
            }
        };

        tabela = new JTable(modelo);
        tabela.setRowHeight(22);
        
        modelo.addTableModelListener(e -> {
            if (e.getType() == javax.swing.event.TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                int col = e.getColumn();

                if (col == 2) { // coluna Qtd
                    atualizarQuantidade(row);
                }
            }
        });



        return new JPanel(new BorderLayout()) {
            {
                add(new JScrollPane(tabela), BorderLayout.CENTER);
            }
        };
    }

    private void atualizarQuantidade(int row) {
        try {
            int novaQtd = Integer.parseInt(modelo.getValueAt(row, 2).toString());
            int produtoId = (int) modelo.getValueAt(row, 0);
            double preco = (double) modelo.getValueAt(row, 3);

            if (novaQtd <= 0) {
                JOptionPane.showMessageDialog(this, "Quantidade inválida.");
                restaurarQuantidade(row);
                return;
            }

            int estoque = produtoDAO.buscarEstoque(produtoId);

            if (novaQtd > estoque) {
                JOptionPane.showMessageDialog(this,
                        "Estoque insuficiente!\nDisponível: " + estoque,
                        "Erro de estoque",
                        JOptionPane.WARNING_MESSAGE);

                restaurarQuantidade(row);
                return;
            }

            // Atualiza subtotal na tabela
            double novoSubtotal = novaQtd * preco;
            modelo.setValueAt(novoSubtotal, row, 4);

            // Atualiza itemVenda correspondente
            ItemVenda item = itensVenda.get(row);
            item.setQuantidade(novaQtd);

            atualizarTotal();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao atualizar quantidade.");
            restaurarQuantidade(row);
        }
    }


    private void restaurarQuantidade(int row) {
        ItemVenda item = itensVenda.get(row);
        modelo.setValueAt(item.getQuantidade(), row, 2);
    }



	// ================= RODAPÉ =================
    private JPanel criarPainelInferior() {
        JPanel p = new JPanel(new GridLayout(2, 1));

        JPanel linha1 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        lblTotal = new JLabel("Total: R$ 0,00");
        txtDesconto = new JTextField("0", 6);

        linha1.add(new JLabel("Desconto: R$"));
        linha1.add(txtDesconto);
        linha1.add(lblTotal);

        JPanel linha2 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        cbPagamento = new JComboBox<>(new String[] { "Dinheiro", "Cartão", "PIX" });

        JButton btnFinalizar = new JButton("Finalizar Venda");
        btnFinalizar.addActionListener(e -> finalizarVenda());

        JButton btnRemoverItem = new JButton("Remover Item");
        btnRemoverItem.addActionListener(e -> removerItem());

        linha2.add(new JLabel("Pagamento:"));
        linha2.add(cbPagamento);
        linha2.add(btnFinalizar);
        linha2.add(btnRemoverItem);

        p.add(linha1);
        p.add(linha2);

        return p;
    }

    // ================= CLIENTES =================
    private void carregarClientes() {
        try (Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(
                        "SELECT cliente_id, nome FROM clientes ORDER BY nome")) {

            while (rs.next()) {
                cbClientes.addItem(
                        rs.getInt("cliente_id") + " - " + rs.getString("nome"));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao carregar clientes:\n" + e.getMessage());
        }
    }

    // ================= PRODUTOS =================
    private void buscarProduto() {
        try {
            String busca = txtBuscarProd.getText().trim();

            if (busca.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Informe o produto.");
                return;
            }

            Produto p = produtoDAO.buscarPorNomeOuCodigo(busca);

            if (p == null) {
                JOptionPane.showMessageDialog(this, "Produto não encontrado.");
                return;
            }

            // 🚫 BLOQUEIO DE ESTOQUE
            if (p.getQuantidadeEstoque() <= 0) {
                JOptionPane.showMessageDialog(this,
                        "Produto SEM ESTOQUE.\nNão é possível vender.",
                        "Estoque insuficiente",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            String qtdStr = JOptionPane.showInputDialog(
                    this,
                    "Quantidade (Disponível: " + p.getQuantidadeEstoque() + "):");

            if (qtdStr == null) return;

            int qtd = Integer.parseInt(qtdStr);

            // 🚫 Validação de quantidade
            if (qtd <= 0 || qtd > p.getQuantidadeEstoque()) {
                JOptionPane.showMessageDialog(this,
                        "Quantidade inválida.\nEstoque disponível: "
                                + p.getQuantidadeEstoque());
                return;
            }

            double subtotal = p.getPrecoVenda() * qtd;

            modelo.addRow(new Object[]{
                    p.getProdutoId(),
                    p.getNome(),
                    qtd,
                    p.getPrecoVenda(),
                    subtotal
            });

            ItemVenda item = new ItemVenda();
            item.setProdutoId(p.getProdutoId());
            item.setQuantidade(qtd);
            item.setPrecoUnitario(p.getPrecoVenda());
            itensVenda.add(item);

            atualizarTotal();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao buscar produto:\n" + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }


    // ================= TOTAL =================
    private void atualizarTotal() {
        double total = 0;

        for (int i = 0; i < modelo.getRowCount(); i++) {
            total += (double) modelo.getValueAt(i, 4);
        }

        lblTotal.setText("Total: R$ " + String.format("%.2f", total));
    }

    // ================= FINALIZAR =================
    private void finalizarVenda() {
        try {
            conn.setAutoCommit(false); // 🔒 inicia transação

            double desconto = Double.parseDouble(txtDesconto.getText().replace(",", "."));
            double totalFinal = 0;

            for (ItemVenda item : itensVenda) {
                totalFinal += item.getPrecoUnitario() * item.getQuantidade();
            }

            totalFinal -= desconto;

            Venda v = new Venda();
            v.setClienteId(Integer.parseInt(
                    cbClientes.getSelectedItem().toString().split(" - ")[0]));
            v.setDesconto(desconto);
            v.setValorTotal(totalFinal);
            v.setMetodoPagamento(cbPagamento.getSelectedItem().toString());

            VendaDAO vendaDAO = new VendaDAO(conn);
            int vendaIdGerado = vendaDAO.inserir(v);

            ItemVendaDAO itemDAO = new ItemVendaDAO(conn);
            ProdutoDAO produtoDAO = new ProdutoDAO(conn);

            // 🔹 Insere itens e baixa estoque
            for (ItemVenda it : itensVenda) {
                it.setVendaId(vendaIdGerado);
                itemDAO.inserir(it);

                produtoDAO.baixarEstoque(it.getProdutoId(), it.getQuantidade());
            }

            conn.commit(); // ✅ tudo OK

            JOptionPane.showMessageDialog(this, "Venda concluída com sucesso!");
            dispose();

        } catch (Exception e) {
            try {
                conn.rollback(); // ❌ erro → desfaz tudo
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            JOptionPane.showMessageDialog(this,
                    "Erro ao finalizar venda:\n" + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);

        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    private void removerItem() {
        int linhaSelecionada = tabela.getSelectedRow();

        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this,
                    "Selecione um item para remover.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Deseja remover o item selecionado?",
                "Confirmação",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        // Remove da lista lógica
        itensVenda.remove(linhaSelecionada);

        // Remove da tabela visual
        modelo.removeRow(linhaSelecionada);

        atualizarTotal();
    }


}
