package view;

import dao.DBConnection;
import dao.OrdemReparoDAO;
import dao.ClienteDAO;
import dao.ServicoDAO;
import model.OrdemReparo;
import model.Cliente;
import model.Servico;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

public class FrmOrdemReparo extends JFrame {

    private JComboBox<Cliente> cbClientes;
    private JComboBox<Servico> cbServicos;

    private JTextField txtId, txtModelo, txtValor, txtDataPrev, txtDataConc;
    private JTextArea txtProblema, txtObs;
    private JComboBox<String> cbStatus;

    private JTable tabela;
    private DefaultTableModel modelo;
    private OrdemReparoDAO dao;

    public FrmOrdemReparo() {

        setTitle("Ordens de Reparo");
        setSize(900, 650);
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        Connection conn;
        try {
            conn = DBConnection.getConnection();
            dao = new OrdemReparoDAO(conn);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao conectar: " + e.getMessage());
            return;
        }

        JLabel lblId = new JLabel("ID:");
        lblId.setBounds(20, 20, 60, 25);
        add(lblId);

        txtId = new JTextField();
        txtId.setBounds(80, 20, 80, 25);
        txtId.setEditable(false);
        add(txtId);

        JLabel lblCliente = new JLabel("Cliente:");
        lblCliente.setBounds(20, 60, 60, 25);
        add(lblCliente);

        cbClientes = new JComboBox<>();
        cbClientes.setBounds(80, 60, 300, 25);
        carregarClientes(conn);
        add(cbClientes);

        JLabel lblServico = new JLabel("Serviço:");
        lblServico.setBounds(20, 100, 60, 25);
        add(lblServico);

        cbServicos = new JComboBox<>();
        cbServicos.setBounds(80, 100, 300, 25);
        carregarServicos(conn);
        add(cbServicos);

        JLabel lblModelo = new JLabel("Modelo:");
        lblModelo.setBounds(20, 140, 80, 25);
        add(lblModelo);

        txtModelo = new JTextField();
        txtModelo.setBounds(80, 140, 200, 25);
        add(txtModelo);

        JLabel lblStatus = new JLabel("Status:");
        lblStatus.setBounds(310, 140, 60, 25);
        add(lblStatus);

        cbStatus = new JComboBox<>(new String[]{
                "Aberto", "Em Reparo", "Aguardando Peça", "Pronto", "Entregue"
        });
        cbStatus.setBounds(370, 140, 150, 25);
        add(cbStatus);

        JLabel lblValor = new JLabel("Valor:");
        lblValor.setBounds(20, 180, 60, 25);
        add(lblValor);

        txtValor = new JTextField();
        txtValor.setBounds(80, 180, 100, 25);
        add(txtValor);

        JLabel lblDataPrev = new JLabel("Previsão:");
        lblDataPrev.setBounds(200, 180, 80, 25);
        add(lblDataPrev);

        txtDataPrev = new JTextField("2025-12-10");
        txtDataPrev.setBounds(270, 180, 100, 25);
        add(txtDataPrev);

        JLabel lblDataConc = new JLabel("Conclusão:");
        lblDataConc.setBounds(380, 180, 80, 25);
        add(lblDataConc);

        txtDataConc = new JTextField();
        txtDataConc.setBounds(460, 180, 110, 25);
        add(txtDataConc);

        JLabel lblProblema = new JLabel("Problema Relatado:");
        lblProblema.setBounds(20, 220, 200, 25);
        add(lblProblema);

        txtProblema = new JTextArea();
        JScrollPane sp1 = new JScrollPane(txtProblema);
        sp1.setBounds(20, 250, 550, 80);
        add(sp1);

        JLabel lblObs = new JLabel("Observações:");
        lblObs.setBounds(20, 340, 200, 25);
        add(lblObs);

        txtObs = new JTextArea();
        JScrollPane sp2 = new JScrollPane(txtObs);
        sp2.setBounds(20, 370, 550, 80);
        add(sp2);

        JButton btnSalvar = new JButton("Salvar");
        btnSalvar.setBounds(20, 470, 100, 30);
        btnSalvar.addActionListener(e -> salvar());
        add(btnSalvar);

        JButton btnLimpar = new JButton("Limpar");
        btnLimpar.setBounds(130, 470, 100, 30);
        btnLimpar.addActionListener(e -> limparCampos());
        add(btnLimpar);

        JButton btnExcluir = new JButton("Excluir");
        btnExcluir.setBounds(240, 470, 100, 30);
        btnExcluir.addActionListener(e -> excluir());
        add(btnExcluir);

        modelo = new DefaultTableModel(new Object[]{
                "ID", "Cliente", "Serviço", "Modelo", "Status", "Valor", "Entrada"
        }, 0);

        tabela = new JTable(modelo);
        JScrollPane spTabela = new JScrollPane(tabela);
        spTabela.setBounds(580, 20, 300, 550);
        add(spTabela);

        tabela.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                carregarCamposDaTabela();
            }
        });

        listar();
    }

    private void carregarClientes(Connection conn) {
        try {
            ClienteDAO cdao = new ClienteDAO();
            for (Cliente c : cdao.listarTodos()) {
                cbClientes.addItem(c);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro clientes: " + e.getMessage());
        }
    }

    private void carregarServicos(Connection conn) {
        try {
            ServicoDAO sdao = new ServicoDAO(conn);
            for (Servico s : sdao.listar()) {
                cbServicos.addItem(s);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro serviços: " + e.getMessage());
        }
    }

    private void salvar() {
        try {
            OrdemReparo o = new OrdemReparo();

            if (!txtId.getText().isEmpty())
                o.setId(Integer.parseInt(txtId.getText()));

            Cliente cliente = (Cliente) cbClientes.getSelectedItem();
            Servico servico = (Servico) cbServicos.getSelectedItem();

            o.setClienteId(cliente.getClienteId());
            o.setServicoId(servico.getId());
            o.setDispositivoModelo(txtModelo.getText());
            o.setProblemaRelatado(txtProblema.getText());
            o.setStatus(cbStatus.getSelectedItem().toString());
            o.setValorTotal(Double.parseDouble(txtValor.getText()));
            o.setObservacoes(txtObs.getText());

            o.setDataPrevisaoEntrega(java.sql.Date.valueOf(txtDataPrev.getText()));

            if (!txtDataConc.getText().isEmpty())
                o.setDataConclusao(java.sql.Date.valueOf(txtDataConc.getText()));

            if (txtId.getText().isEmpty())
                dao.inserir(o);
            else
                dao.atualizar(o);

            listar();
            limparCampos();
            JOptionPane.showMessageDialog(this, "Ordem salva!");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro salvar: " + e.getMessage());
        }
    }

    private void excluir() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecione uma ordem!");
            return;
        }

        try {
            dao.excluir(Integer.parseInt(txtId.getText()));
            listar();
            limparCampos();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro excluir: " + e.getMessage());
        }
    }

    private void listar() {
        try {
            modelo.setRowCount(0);
            for (OrdemReparo o : dao.listar()) {
                modelo.addRow(new Object[]{
                        o.getId(), o.getClienteId(), o.getServicoId(),
                        o.getDispositivoModelo(), o.getStatus(),
                        o.getValorTotal(), o.getDataEntrada()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro listar: " + e.getMessage());
        }
    }

    private void carregarCamposDaTabela() {
        int row = tabela.getSelectedRow();

        txtId.setText(modelo.getValueAt(row, 0).toString());
        txtModelo.setText(modelo.getValueAt(row, 3).toString());
        txtValor.setText(modelo.getValueAt(row, 4).toString());
    }

    private void limparCampos() {
        txtId.setText("");
        txtModelo.setText("");
        txtValor.setText("");
        txtDataPrev.setText("");
        txtDataConc.setText("");
        txtProblema.setText("");
        txtObs.setText("");
    }
}
