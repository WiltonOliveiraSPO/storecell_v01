package view;

import dao.*;
import model.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
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
        setLayout(new BorderLayout());

        try {
            conn = DBConnection.getConnection();
            produtoDAO = new ProdutoDAO(conn);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao conectar: " + e.getMessage());
        }

        add(criarPainelTopo(), BorderLayout.NORTH);
        add(criarPainelCentral(), BorderLayout.CENTER);
        add(criarPainelInferior(), BorderLayout.SOUTH);
    }

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

    private JPanel criarPainelCentral() {
        modelo = new DefaultTableModel(new Object[]{"ID", "Produto", "Qtd", "Preço", "Subtotal"}, 0);
        tabela = new JTable(modelo);

        return new JPanel(new BorderLayout()) {{
            add(new JScrollPane(tabela), BorderLayout.CENTER);
        }};
    }

    private JPanel criarPainelInferior() {
        JPanel p = new JPanel(new GridLayout(2, 1));

        JPanel linha1 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        lblTotal = new JLabel("Total: R$ 0,00");
        txtDesconto = new JTextField("0", 5);
        cbPagamento = new JComboBox<>(new String[]{"Dinheiro", "Cartão", "PIX"});

        linha1.add(new JLabel("Desconto: R$"));
        linha1.add(txtDesconto);
        linha1.add(lblTotal);

        JPanel linha2 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnFinalizar = new JButton("Finalizar Venda");
        btnFinalizar.addActionListener(e -> finalizarVenda());

        linha2.add(new JLabel("Pagamento:"));
        linha2.add(cbPagamento);
        linha2.add(btnFinalizar);

        p.add(linha1);
        p.add(linha2);

        return p;
    }

    private void carregarClientes() {
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT cliente_id, nome FROM clientes");

            while (rs.next()) {
                cbClientes.addItem(rs.getInt("cliente_id") + " - " + rs.getString("nome"));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro carregando clientes");
        }
    }

    private void buscarProduto() {
        try {
            String busca = txtBuscarProd.getText();
            Produto p = produtoDAO.buscarPorNomeOuCodigo(busca);

            if (p == null) {
                JOptionPane.showMessageDialog(this, "Produto não encontrado.");
                return;
            }

            String qtdStr = JOptionPane.showInputDialog("Quantidade:");
            int qtd = Integer.parseInt(qtdStr);

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
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage());
        }
    }

    private void atualizarTotal() {
        double total = 0;

        for (int i = 0; i < modelo.getRowCount(); i++) {
            total += (double) modelo.getValueAt(i, 4);
        }

        lblTotal.setText("Total: R$ " + String.format("%.2f", total));
    }

    private void finalizarVenda() {
        try {
            double desconto = Double.parseDouble(txtDesconto.getText().replace(",", "."));

            double totalFinal = 0;
            for (ItemVenda item : itensVenda) {
                totalFinal += item.getPrecoUnitario() * item.getQuantidade();
            }
            totalFinal -= desconto;

            Venda v = new Venda();
            v.setClienteId(Integer.parseInt(cbClientes.getSelectedItem().toString().split(" - ")[0]));
            v.setDesconto(desconto);
            v.setValorTotal(totalFinal);
            v.setMetodoPagamento(cbPagamento.getSelectedItem().toString());

            VendaDAO vendaDAO = new VendaDAO(conn);
            int vendaIdGerado = vendaDAO.inserir(v);

            ItemVendaDAO itemDAO = new ItemVendaDAO(conn);
            for (ItemVenda it : itensVenda) {
                it.setVendaId(vendaIdGerado);
                itemDAO.inserir(it);
            }

            JOptionPane.showMessageDialog(this, "Venda concluída com sucesso!");
            dispose();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao finalizar venda: " + e.getMessage());
        }
    }
}
