package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import dao.DBConnection;
import dao.ProdutoDAO;
import model.Produto;

import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class FrmProduto extends JFrame {

    private JTextField txtId, txtNome, txtPrecoCusto, txtPrecoVenda, txtEstoque, txtCodigoBarras;
    private JTextArea txtDescricao;
    private JTable tabela;

    private ProdutoDAO dao;

    public FrmProduto() throws SQLException {

        // Configuração da tela
        setTitle("Cadastro de Produtos");
        setSize(850, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

        // Conexão
        Connection conn = DBConnection.getConnection();
        dao = new ProdutoDAO(conn);

        // Labels & Campos
        JLabel lblId = new JLabel("ID:");
        lblId.setBounds(20, 20, 30, 25);
        add(lblId);

        txtId = new JTextField();
        txtId.setBounds(50, 20, 80, 25);
        txtId.setEditable(false);
        add(txtId);

        JLabel lblNome = new JLabel("Nome:");
        lblNome.setBounds(150, 20, 80, 25);
        add(lblNome);

        txtNome = new JTextField();
        txtNome.setBounds(210, 20, 300, 25);
        add(txtNome);

        JLabel lblPrecoCusto = new JLabel("Preço Custo:");
        lblPrecoCusto.setBounds(20, 60, 100, 25);
        add(lblPrecoCusto);

        txtPrecoCusto = new JTextField();
        txtPrecoCusto.setBounds(120, 60, 100, 25);
        add(txtPrecoCusto);

        JLabel lblPrecoVenda = new JLabel("Preço Venda:");
        lblPrecoVenda.setBounds(230, 60, 100, 25);
        add(lblPrecoVenda);

        txtPrecoVenda = new JTextField();
        txtPrecoVenda.setBounds(330, 60, 100, 25);
        add(txtPrecoVenda);

        JLabel lblEstoque = new JLabel("Estoque:");
        lblEstoque.setBounds(450, 60, 100, 25);
        add(lblEstoque);

        txtEstoque = new JTextField();
        txtEstoque.setBounds(520, 60, 80, 25);
        add(txtEstoque);

        JLabel lblCodigoBarras = new JLabel("Código de Barras:");
        lblCodigoBarras.setBounds(20, 100, 150, 25);
        add(lblCodigoBarras);

        txtCodigoBarras = new JTextField();
        txtCodigoBarras.setBounds(150, 100, 200, 25);
        add(txtCodigoBarras);

        JLabel lblDescricao = new JLabel("Descrição:");
        lblDescricao.setBounds(20, 140, 100, 25);
        add(lblDescricao);

        txtDescricao = new JTextArea();
        JScrollPane scrollDesc = new JScrollPane(txtDescricao);
        scrollDesc.setBounds(20, 170, 580, 100);
        add(scrollDesc);

        // Botões
        JButton btnNovo = new JButton("Novo");
        btnNovo.setBounds(620, 20, 120, 30);
        add(btnNovo);

        JButton btnSalvar = new JButton("Salvar");
        btnSalvar.setBounds(620, 60, 120, 30);
        add(btnSalvar);

        JButton btnExcluir = new JButton("Excluir");
        btnExcluir.setBounds(620, 100, 120, 30);
        add(btnExcluir);

        // Tabela
        tabela = new JTable();
        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBounds(20, 300, 800, 300);
        add(scroll);

        carregarTabela();

        // AÇÕES
        btnNovo.addActionListener(e -> limparCampos());

        btnSalvar.addActionListener(e -> salvar());

        btnExcluir.addActionListener(e -> excluir());

        tabela.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                carregarSelecionado();
            }
        });
    }

    private void carregarTabela() {
        List<Produto> lista = dao.listarTodos();

        DefaultTableModel modelo = new DefaultTableModel(
                new Object[]{"ID", "Nome", "Preço Venda", "Estoque"}, 0);

        for (Produto p : lista) {
            modelo.addRow(new Object[]{
                    p.getProdutoId(),
                    p.getNome(),
                    p.getPrecoVenda(),
                    p.getQuantidadeEstoque()
            });
        }

        tabela.setModel(modelo);
    }

    private void limparCampos() {
        txtId.setText("");
        txtNome.setText("");
        txtPrecoCusto.setText("");
        txtPrecoVenda.setText("");
        txtEstoque.setText("");
        txtCodigoBarras.setText("");
        txtDescricao.setText("");
    }

    private void salvar() {
        Produto p = new Produto();

        p.setNome(txtNome.getText());
        p.setDescricao(txtDescricao.getText());
        p.setPrecoCusto(Double.parseDouble(txtPrecoCusto.getText()));
        p.setPrecoVenda(Double.parseDouble(txtPrecoVenda.getText()));
        p.setQuantidadeEstoque(Integer.parseInt(txtEstoque.getText()));
        p.setCodigoBarras(txtCodigoBarras.getText());

        if (txtId.getText().isEmpty()) {
            dao.inserir(p);
            JOptionPane.showMessageDialog(this, "Produto cadastrado!");
        } else {
            p.setProdutoId(Integer.parseInt(txtId.getText()));
            dao.atualizar(p);
            JOptionPane.showMessageDialog(this, "Produto atualizado!");
        }

        carregarTabela();
        limparCampos();
    }

    private void excluir() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecione um produto!");
            return;
        }

        dao.excluir(Integer.parseInt(txtId.getText()));
        JOptionPane.showMessageDialog(this, "Produto excluído!");

        carregarTabela();
        limparCampos();
    }

    private void carregarSelecionado() {
        int row = tabela.getSelectedRow();
        if (row < 0) return;

        txtId.setText(tabela.getValueAt(row, 0).toString());

        Produto p = dao.buscarPorId(Integer.parseInt(txtId.getText()));

        txtNome.setText(p.getNome());
        txtDescricao.setText(p.getDescricao());
        txtPrecoCusto.setText(String.valueOf(p.getPrecoCusto()));
        txtPrecoVenda.setText(String.valueOf(p.getPrecoVenda()));
        txtEstoque.setText(String.valueOf(p.getQuantidadeEstoque()));
        txtCodigoBarras.setText(p.getCodigoBarras());
    }
}
