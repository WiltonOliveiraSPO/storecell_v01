package view;

import model.Servico;
import dao.ServicoDAO;
import dao.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class FrmServico extends JFrame {

    private JTextField txtId, txtNome, txtPreco;
    private JTable tabela;
    private DefaultTableModel modelo;
    private ServicoDAO dao;

    public FrmServico() {

        setTitle("Cadastro de Serviços");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setLayout(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        try {
            Connection conn = DBConnection.getConnection();
            dao = new ServicoDAO(conn);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao conectar: " + e.getMessage());
        }

        JLabel lblId = new JLabel("ID:");
        lblId.setBounds(20, 20, 30, 25);
        add(lblId);

        txtId = new JTextField();
        txtId.setBounds(60, 20, 80, 25);
        txtId.setEditable(false);
        add(txtId);

        JLabel lblNome = new JLabel("Nome:");
        lblNome.setBounds(20, 60, 80, 25);
        add(lblNome);

        txtNome = new JTextField();
        txtNome.setBounds(80, 60, 250, 25);
        add(txtNome);

        JLabel lblPreco = new JLabel("Preço:");
        lblPreco.setBounds(20, 100, 80, 25);
        add(lblPreco);

        txtPreco = new JTextField();
        txtPreco.setBounds(80, 100, 100, 25);
        add(txtPreco);

        // Botões
        JButton btnSalvar = new JButton("Salvar");
        btnSalvar.setBounds(20, 150, 90, 30);
        add(btnSalvar);

        JButton btnLimpar = new JButton("Limpar");
        btnLimpar.setBounds(120, 150, 90, 30);
        add(btnLimpar);

        JButton btnExcluir = new JButton("Excluir");
        btnExcluir.setBounds(220, 150, 90, 30);
        add(btnExcluir);

        // Tabela
        modelo = new DefaultTableModel(new Object[]{"ID", "Nome", "Preço"}, 0);
        tabela = new JTable(modelo);
        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBounds(20, 200, 640, 240);
        add(scroll);

        listarServicos();

        // Eventos
        btnSalvar.addActionListener(e -> salvar());
        btnLimpar.addActionListener(e -> limpar());
        btnExcluir.addActionListener(e -> excluir());

        tabela.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                carregarCampos();
            }
        });
    }

    private void salvar() {
        try {
            Servico s = new Servico();

            if (!txtId.getText().isEmpty()) {
                s.setId(Integer.parseInt(txtId.getText()));
            }

            s.setNome(txtNome.getText());
            s.setPreco(Double.parseDouble(txtPreco.getText()));

            if (txtId.getText().isEmpty()) {
                dao.inserir(s);
            } else {
                dao.atualizar(s);
            }

            listarServicos();
            limpar();
            JOptionPane.showMessageDialog(this, "Serviço salvo!");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
        }
    }

    private void excluir() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecione um serviço!");
            return;
        }

        try {
            dao.excluir(Integer.parseInt(txtId.getText()));
            listarServicos();
            limpar();
            JOptionPane.showMessageDialog(this, "Serviço excluído!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
        }
    }

    private void listarServicos() {
        try {
            modelo.setRowCount(0);
            List<Servico> lista = dao.listar();

            for (Servico s : lista) {
                modelo.addRow(new Object[]{s.getId(), s.getNome(), s.getPreco()});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao listar: " + e.getMessage());
        }
    }

    private void limpar() {
        txtId.setText("");
        txtNome.setText("");
        txtPreco.setText("");
    }

    private void carregarCampos() {
        int row = tabela.getSelectedRow();
        txtId.setText(modelo.getValueAt(row, 0).toString());
        txtNome.setText(modelo.getValueAt(row, 1).toString());
        txtPreco.setText(modelo.getValueAt(row, 2).toString());
    }
}
