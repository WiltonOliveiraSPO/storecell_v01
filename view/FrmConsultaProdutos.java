package view;

import dao.DBConnection;
import dao.ProdutoDAO;
import model.Produto;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class FrmConsultaProdutos extends JFrame {

    private JTextField txtBusca;
    private JTable tabela;
    private DefaultTableModel modelo;
    private ProdutoDAO produtoDAO;
    private Connection conn;

    public FrmConsultaProdutos() {
        setTitle("Consulta de Produtos");
        setSize(850, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null);

        try {
            conn = DBConnection.getConnection();
            produtoDAO = new ProdutoDAO(conn);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro de conexão: " + e.getMessage());
            return;
        }

        JLabel lblBusca = new JLabel("Nome / Código:");
        lblBusca.setBounds(20, 20, 120, 25);
        add(lblBusca);

        txtBusca = new JTextField();
        txtBusca.setBounds(140, 20, 420, 25);
        add(txtBusca);

        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.setBounds(570, 20, 110, 25);
        add(btnBuscar);

        btnBuscar.addActionListener(e -> buscar());

        // tabela
        modelo = new DefaultTableModel(new Object[]{"ID", "Nome", "Preço Venda", "Estoque", "Código de Barras"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tabela = new JTable(modelo);
        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBounds(20, 70, 800, 380);
        add(scroll);

        // duplo clique abre FrmProdutos carregado
        tabela.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2)
					try {
						abrirProdutoSelecionado();
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
            }
        });

        // carregar todos inicialmente
        carregarTodos();
    }

    private void carregarTodos() {
        modelo.setRowCount(0);
        try {
            List<Produto> lista = produtoDAO.listarTodos();
            for (Produto p : lista) {
                modelo.addRow(new Object[]{
                        p.getProdutoId(),
                        p.getNome(),
                        p.getPrecoVenda(),
                        p.getQuantidadeEstoque(),
                        p.getCodigoBarras()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao listar produtos: " + ex.getMessage());
        }
    }

    private void buscar() {
        String busca = txtBusca.getText().trim();
        modelo.setRowCount(0);
        try {
            if (busca.isEmpty()) {
                carregarTodos();
                return;
            }

            // tenta método no DAO que busca por nome ou código
            Produto pSingle = produtoDAO.buscarPorNomeOuCodigo(busca);
            if (pSingle != null && busca.matches("\\d+")) {
                // busca por id retornou um produto
                modelo.addRow(new Object[]{
                        pSingle.getProdutoId(),
                        pSingle.getNome(),
                        pSingle.getPrecoVenda(),
                        pSingle.getQuantidadeEstoque(),
                        pSingle.getCodigoBarras()
                });
                return;
            }

            // caso não seja apenas número, tenta buscar por nome com like
            // se o DAO tiver buscarPorNome, pode ser usado; aqui tentamos pelo método combinado
            List<Produto> lista = (List<Produto>) produtoDAO.buscarPorNomeOuCodigo(busca); // se existir
            if (lista != null && !lista.isEmpty()) {
                for (Produto p : lista) {
                    modelo.addRow(new Object[]{
                            p.getProdutoId(),
                            p.getNome(),
                            p.getPrecoVenda(),
                            p.getQuantidadeEstoque(),
                            p.getCodigoBarras()
                    });
                }
                return;
            }

            // fallback: tentar listarTodos e filtrar (seguro)
            lista = produtoDAO.listarTodos();
            for (Produto p : lista) {
                if (p.getNome().toLowerCase().contains(busca.toLowerCase())
                        || (p.getCodigoBarras() != null && p.getCodigoBarras().contains(busca))) {
                    modelo.addRow(new Object[]{
                            p.getProdutoId(),
                            p.getNome(),
                            p.getPrecoVenda(),
                            p.getQuantidadeEstoque(),
                            p.getCodigoBarras()
                    });
                }
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro na busca: " + ex.getMessage());
        }
    }

    private void abrirProdutoSelecionado() throws SQLException {
        int row = tabela.getSelectedRow();
        if (row < 0) return;
        int produtoId = Integer.parseInt(modelo.getValueAt(row, 0).toString());

        // abre FrmProdutos e chama método para preencher
        FrmProduto frm = new FrmProduto();
        frm.setVisible(true);

        // chama método que você precisa adicionar em FrmProdutos:
        // public void carregarProduto(int produtoId) { ... }
        frm.carregarProduto(produtoId);
    }
}
