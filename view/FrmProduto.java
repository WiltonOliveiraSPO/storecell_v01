package view;

import controller.ProdutoController;
import dao.DBConnection;
import dao.ProdutoDAO;
import model.Produto;

import javax.swing.*;
import java.awt.event.*;
import java.sql.Connection;

public class FrmProduto extends JFrame {

    private JTextField txtId, txtNome, txtPrecoCusto, txtPrecoVenda, txtEstoque, txtCodigoBarras;
    private JTextArea txtDescricao;
    private JTable tabela;

    private ProdutoController controller;

    public FrmProduto() {
        setTitle("Cadastro de Produtos");
        setSize(880, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null);

        try {
            Connection conn = DBConnection.getConnection();
            ProdutoDAO dao = new ProdutoDAO(conn);
            controller = new ProdutoController(dao);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro conexão: " + e.getMessage());
            dispose();
            return;
        }

        // ===== CAMPOS =====
        txtId = campo(20, 20, 80);
        txtId.setEditable(false);

        txtNome = campo(120, 20, 300);
        txtPrecoCusto = campo(20, 60, 100);
        txtPrecoVenda = campo(140, 60, 100);
        txtEstoque = campo(260, 60, 80);
        txtCodigoBarras = campo(20, 100, 200);

        txtDescricao = new JTextArea();
        JScrollPane sp = new JScrollPane(txtDescricao);
        sp.setBounds(20, 140, 580, 100);
        add(sp);

        // ===== BOTÕES =====
        JButton btnNovo = botao("+", 620, 20);
        JButton btnSalvar = botao("💾", 680, 20);
        JButton btnAtualizar = botao("✏", 740, 20);
        JButton btnExcluir = botao("🗑", 800, 20);

        JButton btnPrimeiro = botao("|<", 620, 60);
        JButton btnAnterior = botao("<", 680, 60);
        JButton btnProximo = botao(">", 740, 60);
        JButton btnUltimo = botao(">|", 800, 60);

        // ===== TABELA =====
        tabela = new JTable();
        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBounds(20, 270, 830, 330);
        add(scroll);

        controller.carregarTabela(tabela);

        // ===== AÇÕES =====
        btnNovo.addActionListener(e -> limpar());

        btnSalvar.addActionListener(e -> {
            controller.inserir(montar());
            controller.carregarTabela(tabela);
            limpar();
        });

        btnAtualizar.addActionListener(e -> {
            controller.atualizar(montar());
            controller.carregarTabela(tabela);
        });

        btnExcluir.addActionListener(e -> {
            controller.excluir(Integer.parseInt(txtId.getText()));
            controller.carregarTabela(tabela);
            limpar();
        });

        tabela.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                Produto p = controller.getProdutoSelecionado(tabela.getSelectedRow());
                carregar(p);
            }
        });

        btnPrimeiro.addActionListener(e -> carregar(controller.navegarPrimeiro()));
        btnAnterior.addActionListener(e -> carregar(controller.navegarAnterior()));
        btnProximo.addActionListener(e -> carregar(controller.navegarProximo()));
        btnUltimo.addActionListener(e -> carregar(controller.navegarUltimo()));
    }

    // ===== MÉTODOS AUX =====
    private JTextField campo(int x, int y, int w) {
        JTextField t = new JTextField();
        t.setBounds(x, y, w, 25);
        add(t);
        return t;
    }

    private JButton botao(String txt, int x, int y) {
        JButton b = new JButton(txt);
        b.setBounds(x, y, 50, 30);
        add(b);
        return b;
    }

    private Produto montar() {
        Produto p = new Produto();
        if (!txtId.getText().isEmpty())
            p.setProdutoId(Integer.parseInt(txtId.getText()));

        p.setNome(txtNome.getText());
        p.setDescricao(txtDescricao.getText());
        p.setPrecoCusto(Double.parseDouble(txtPrecoCusto.getText()));
        p.setPrecoVenda(Double.parseDouble(txtPrecoVenda.getText()));
        p.setQuantidadeEstoque(Integer.parseInt(txtEstoque.getText()));
        p.setCodigoBarras(txtCodigoBarras.getText());
        return p;
    }

    private void carregar(Produto p) {
        if (p == null) return;
        txtId.setText(String.valueOf(p.getProdutoId()));
        txtNome.setText(p.getNome());
        txtDescricao.setText(p.getDescricao());
        txtPrecoCusto.setText(String.valueOf(p.getPrecoCusto()));
        txtPrecoVenda.setText(String.valueOf(p.getPrecoVenda()));
        txtEstoque.setText(String.valueOf(p.getQuantidadeEstoque()));
        txtCodigoBarras.setText(p.getCodigoBarras());
    }

    private void limpar() {
        txtId.setText("");
        txtNome.setText("");
        txtDescricao.setText("");
        txtPrecoCusto.setText("");
        txtPrecoVenda.setText("");
        txtEstoque.setText("");
        txtCodigoBarras.setText("");
    }
}
