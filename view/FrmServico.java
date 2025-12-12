package view;

import dao.DBConnection;
import dao.ServicoDAO;
import model.Servico;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class FrmServico extends JFrame {

    private JTextField txtId, txtNome, txtValor;
    private JTextArea txtDescricao;
    private JCheckBox chkAtivo;

    private JButton btnPrimeiro, btnAnterior, btnProximo, btnUltimo;
    private JButton btnNovo, btnEditar, btnExcluir, btnSalvar, btnCancelar;

    private ServicoDAO dao;
    private List<Servico> lista;
    private int pos = 0;

    private boolean editMode = false;
    private boolean newRecord = false;

    public FrmServico() {
        setTitle("Cadastro de Serviços");
        setSize(600, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // ==============================
        //  1) Conexão + DAO
        // ==============================
        try {
            Connection conn = DBConnection.getConnection();
            dao = new ServicoDAO(conn);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Erro ao conectar ao banco: " + e.getMessage(),
                "Erro crítico",
                JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }

        initComponents();
        loadData();

        setVisible(true);
    }

    private void initComponents() {

        JPanel panelCampos = new JPanel(new GridBagLayout());
        panelCampos.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5,5,5,5);
        c.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblId = new JLabel("ID:");
        JLabel lblNome = new JLabel("Nome:");
        JLabel lblValor = new JLabel("Valor (R$):");
        JLabel lblDescricao = new JLabel("Descrição:");
        JLabel lblAtivo = new JLabel("Ativo:");

        txtId = new JTextField(10);
        txtId.setEditable(false);

        txtNome = new JTextField(30);
        txtValor = new JTextField(10);

        txtDescricao = new JTextArea(4, 30);
        txtDescricao.setLineWrap(true);
        txtDescricao.setWrapStyleWord(true);

        chkAtivo = new JCheckBox();

        // linha 1
        c.gridx = 0; c.gridy = 0; panelCampos.add(lblId, c);
        c.gridx = 1; panelCampos.add(txtId, c);

        // linha 2
        c.gridx = 0; c.gridy = 1; panelCampos.add(lblNome, c);
        c.gridx = 1; panelCampos.add(txtNome, c);

        // linha 3
        c.gridx = 0; c.gridy = 2; panelCampos.add(lblValor, c);
        c.gridx = 1; panelCampos.add(txtValor, c);

        // linha 4
        c.gridx = 0; c.gridy = 3; panelCampos.add(lblDescricao, c);
        c.gridx = 1; 
        panelCampos.add(new JScrollPane(txtDescricao), c);

        // linha 5
        c.gridx = 0; c.gridy = 4; panelCampos.add(lblAtivo, c);
        c.gridx = 1; panelCampos.add(chkAtivo, c);

        add(panelCampos, BorderLayout.CENTER);

        // ==============================
        //  2) BOTÕES
        // ==============================
        JPanel panelBtns = new JPanel(new FlowLayout());

        btnPrimeiro = new JButton("|<");
        btnAnterior = new JButton("<");
        btnProximo = new JButton(">");
        btnUltimo = new JButton(">|");

        btnNovo = new JButton("+");
        btnEditar = new JButton("✏");
        btnExcluir = new JButton("🗑");
        btnSalvar = new JButton("💾");
        btnCancelar = new JButton("🚫");

        panelBtns.add(btnPrimeiro);
        panelBtns.add(btnAnterior);
        panelBtns.add(btnProximo);
        panelBtns.add(btnUltimo);

        panelBtns.add(btnNovo);
        panelBtns.add(btnEditar);
        panelBtns.add(btnExcluir);
        panelBtns.add(btnSalvar);
        panelBtns.add(btnCancelar);

        add(panelBtns, BorderLayout.SOUTH);

        // ações
        btnPrimeiro.addActionListener(e -> mostrarRegistro(0));
        btnAnterior.addActionListener(e -> { if (pos > 0) mostrarRegistro(pos - 1); });
        btnProximo.addActionListener(e -> { if (pos < lista.size() - 1) mostrarRegistro(pos + 1); });
        btnUltimo.addActionListener(e -> mostrarRegistro(lista.size() - 1));

        btnNovo.addActionListener(e -> novoRegistro());
        btnEditar.addActionListener(e -> editarRegistro());
        btnExcluir.addActionListener(e -> excluirRegistro());
        btnSalvar.addActionListener(e -> salvarRegistro());
        btnCancelar.addActionListener(e -> cancelarEdicao());

        setEditMode(false);
    }

    // ==============================
    // Carregar dados
    // ==============================
    private void loadData() {
        try {
            lista = dao.listar();
            if (!lista.isEmpty()) {
                mostrarRegistro(0);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar serviços: " + e.getMessage());
        }
    }

    private void mostrarRegistro(int index) {
        if (lista == null || lista.isEmpty()) return;

        pos = index;
        Servico s = lista.get(pos);

        txtId.setText(String.valueOf(s.getId()));
        txtNome.setText(s.getNome());
        txtValor.setText(String.valueOf(s.getValor()));
        txtDescricao.setText(s.getDescricao());
        //chkAtivo.setSelected(s.isAtivo());
    }

    // ==============================
    // Controle de edição
    // ==============================
    private void setEditMode(boolean flag) {
        editMode = flag;

        txtNome.setEditable(flag);
        txtValor.setEditable(flag);
        txtDescricao.setEditable(flag);
        chkAtivo.setEnabled(flag);

        btnSalvar.setEnabled(flag);
        btnCancelar.setEnabled(flag);

        btnNovo.setEnabled(!flag);
        btnEditar.setEnabled(!flag);
        btnExcluir.setEnabled(!flag);

        btnPrimeiro.setEnabled(!flag);
        btnAnterior.setEnabled(!flag);
        btnProximo.setEnabled(!flag);
        btnUltimo.setEnabled(!flag);
    }

    private void novoRegistro() {
        newRecord = true;
        setEditMode(true);

        txtId.setText("");
        txtNome.setText("");
        txtValor.setText("");
        txtDescricao.setText("");
        chkAtivo.setSelected(true);

        txtNome.requestFocus();
    }

    private void editarRegistro() {
        if (lista.isEmpty()) return;
        newRecord = false;
        setEditMode(true);
    }

    private void excluirRegistro() {
        if (lista.isEmpty()) return;

        int r = JOptionPane.showConfirmDialog(this,
                "Deseja realmente excluir este serviço?",
                "Confirmação",
                JOptionPane.YES_NO_OPTION);

        if (r == JOptionPane.YES_OPTION) {
            try {
                dao.excluir(lista.get(pos).getId());
                loadData();
                JOptionPane.showMessageDialog(this, "Serviço excluído.");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Erro ao excluir: " + e.getMessage());
            }
        }
    }

    private void salvarRegistro() {
        try {

            Servico s = new Servico();
            s.setNome(txtNome.getText());
            s.setValor(Double.parseDouble(txtValor.getText()));
            s.setDescricao(txtDescricao.getText());
            //s.setAtivo(chkAtivo.isSelected());

            if (newRecord) {
                dao.inserir(s);
                JOptionPane.showMessageDialog(this, "Serviço cadastrado!");
            } else {
                s.setId(Integer.parseInt(txtId.getText()));
                dao.atualizar(s);
                JOptionPane.showMessageDialog(this, "Serviço atualizado!");
            }

            loadData();
            setEditMode(false);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar: " + e.getMessage());
        }
    }

    private void cancelarEdicao() {
        setEditMode(false);
        if (!lista.isEmpty()) mostrarRegistro(pos);
    }

	public void carregarServico(int servicoId) {
		// TODO Auto-generated method stub
		
	}
}
