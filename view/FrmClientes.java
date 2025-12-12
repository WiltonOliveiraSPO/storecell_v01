package view;

import dao.ClienteDAO;
import model.Cliente;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class FrmClientes extends JFrame {

    private JTextField txtId;
    private JTextField txtNome;
    private JTextField txtTelefone;
    private JTextField txtEmail;
    private JTextField txtCpf;
    private JButton btnNovo, btnSalvar, btnEditar, btnExcluir, btnBuscar;
    private JButton btnPrimeiro, btnAnterior, btnProximo, btnUltimo;
    private JTable tabela;
    private DefaultTableModel tableModel;
    private JTextField txtBuscaNome;

    private ClienteDAO dao;
    private List<Cliente> lista;
    private int currentIndex = -1;

    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public FrmClientes() {
        setTitle("Cadastro de Clientes");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        dao = new ClienteDAO();

        initComponents();
        carregarTabela();
        atualizarFormularioParaIndice(0);
    }

    private void initComponents() {
        JPanel painelForm = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4,4,4,4);
        g.anchor = GridBagConstraints.WEST;

        g.gridx = 0; g.gridy = 0;
        painelForm.add(new JLabel("ID:"), g);
        g.gridx = 1;
        txtId = new JTextField(8);
        txtId.setEditable(false);
        painelForm.add(txtId, g);

        g.gridx = 0; g.gridy++;
        painelForm.add(new JLabel("Nome:"), g);
        g.gridx = 1; g.gridwidth = 3; g.fill = GridBagConstraints.HORIZONTAL;
        txtNome = new JTextField(40);
        painelForm.add(txtNome, g);
        g.gridwidth = 1; g.fill = GridBagConstraints.NONE;

        g.gridx = 0; g.gridy++;
        painelForm.add(new JLabel("Telefone:"), g);
        g.gridx = 1;
        txtTelefone = new JTextField(15);
        painelForm.add(txtTelefone, g);

        g.gridx = 2;
        painelForm.add(new JLabel("CPF:"), g);
        g.gridx = 3;
        txtCpf = new JTextField(14);
        painelForm.add(txtCpf, g);

        g.gridx = 0; g.gridy++;
        painelForm.add(new JLabel("Email:"), g);
        g.gridx = 1; g.gridwidth = 3; g.fill = GridBagConstraints.HORIZONTAL;
        txtEmail = new JTextField(40);
        painelForm.add(txtEmail, g);
        g.gridwidth = 1; g.fill = GridBagConstraints.NONE;

        // botões
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnNovo = new JButton("Novo");
        btnSalvar = new JButton("Salvar");
        btnEditar = new JButton("Editar");
        btnExcluir = new JButton("Excluir");

        painelBotoes.add(btnNovo);
        painelBotoes.add(btnSalvar);
        painelBotoes.add(btnEditar);
        painelBotoes.add(btnExcluir);

        // navegação
        JPanel painelNav = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnPrimeiro = new JButton("|<");
        btnAnterior = new JButton("<");
        btnProximo = new JButton(">");
        btnUltimo = new JButton(">|");
        painelNav.add(btnPrimeiro);
        painelNav.add(btnAnterior);
        painelNav.add(btnProximo);
        painelNav.add(btnUltimo);

        // busca rápida
        JPanel painelBusca = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        txtBuscaNome = new JTextField(20);
        btnBuscar = new JButton("Buscar");
        painelBusca.add(new JLabel("Buscar nome:"));
        painelBusca.add(txtBuscaNome);
        painelBusca.add(btnBuscar);

        // tabela
        tableModel = new DefaultTableModel(new Object[]{"ID", "Nome", "Telefone", "CPF", "Email", "Data Cadastro"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabela = new JTable(tableModel);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroll = new JScrollPane(tabela);

        // layout principal
        JPanel topo = new JPanel(new BorderLayout());
        topo.add(painelForm, BorderLayout.WEST);
        topo.add(painelBotoes, BorderLayout.SOUTH);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(topo, BorderLayout.NORTH);
        getContentPane().add(scroll, BorderLayout.CENTER);

        JPanel rodape = new JPanel(new BorderLayout());
        rodape.add(painelNav, BorderLayout.WEST);
        rodape.add(painelBusca, BorderLayout.EAST);
        getContentPane().add(rodape, BorderLayout.SOUTH);

        // listeners
        btnNovo.addActionListener(e -> limparFormulario());
        btnSalvar.addActionListener(e -> salvarCliente());
        btnEditar.addActionListener(e -> editarCliente());
        btnExcluir.addActionListener(e -> excluirCliente());

        btnBuscar.addActionListener(e -> buscarPorNome());

        btnPrimeiro.addActionListener(e -> atualizarFormularioParaIndice(0));
        btnAnterior.addActionListener(e -> atualizarFormularioParaIndice(currentIndex - 1));
        btnProximo.addActionListener(e -> atualizarFormularioParaIndice(currentIndex + 1));
        btnUltimo.addActionListener(e -> atualizarFormularioParaIndice(lista == null ? -1 : lista.size() - 1));

        tabela.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int linha = tabela.getSelectedRow();
                if (linha >= 0) {
                    int id = Integer.parseInt(tableModel.getValueAt(linha, 0).toString());
                    abrirClientePorId(id);
                }
            }
        });
    }

    private void carregarTabela() {
        try {
            lista = dao.listarTodos();
            preencherTabela(lista);
        } catch (SQLException ex) {
            showError("Erro ao carregar clientes: " + ex.getMessage());
        }
    }

    private void preencherTabela(List<Cliente> dados) {
        tableModel.setRowCount(0);
        if (dados != null) {
            for (Cliente c : dados) {
                String dataCad = c.getDataCadastro() == null ? "" : c.getDataCadastro().format(dtf);
                tableModel.addRow(new Object[]{
                        c.getClienteId(),
                        c.getNome(),
                        c.getTelefone(),
                        c.getCpf(),
                        c.getEmail(),
                        dataCad
                });
            }
        }
    }

    private void limparFormulario() {
        txtId.setText("");
        txtNome.setText("");
        txtTelefone.setText("");
        txtEmail.setText("");
        txtCpf.setText("");
        tabela.clearSelection();
        currentIndex = -1;
    }

    private void salvarCliente() {
        String nome = txtNome.getText().trim();
        if (nome.isEmpty()) {
            showError("Nome é obrigatório.");
            return;
        }

        Cliente c = new Cliente();
        c.setNome(nome);
        c.setTelefone(txtTelefone.getText().trim());
        c.setEmail(txtEmail.getText().trim());
        c.setCpf(txtCpf.getText().trim());

        try {
            if (txtId.getText().isEmpty()) {
                int id = dao.inserir(c);
                if (id > 0) {
                    showInfo("Cliente inserido com ID " + id);
                } else {
                    showError("Erro ao inserir cliente.");
                }
            } else {
                c.setClienteId(Integer.parseInt(txtId.getText()));
                boolean ok = dao.atualizar(c);
                if (ok) {
                    showInfo("Cliente atualizado.");
                } else {
                    showError("Atualização falhou.");
                }
            }
            carregarTabela();
            if (c.getClienteId() != 0) abrirClientePorId(c.getClienteId());
        } catch (SQLException ex) {
            showError("Erro ao salvar cliente: " + ex.getMessage());
        }
    }

    private void editarCliente() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            showError("Selecione um cliente na tabela para editar.");
            return;
        }
        int id = Integer.parseInt(tableModel.getValueAt(linha, 0).toString());
        abrirClientePorId(id);
    }

    private void excluirCliente() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            showError("Selecione um cliente na tabela para excluir.");
            return;
        }
        int id = Integer.parseInt(tableModel.getValueAt(linha, 0).toString());
        int op = JOptionPane.showConfirmDialog(this, "Confirma exclusão do cliente ID " + id + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (op != JOptionPane.YES_OPTION) return;

        try {
            boolean ok = dao.excluir(id);
            if (ok) {
                showInfo("Cliente excluído.");
                carregarTabela();
                limparFormulario();
            } else {
                showError("Não foi possível excluir o cliente.");
            }
        } catch (SQLException ex) {
            showError("Erro ao excluir cliente: " + ex.getMessage());
        }
    }

    private void buscarPorNome() {
        String nome = txtBuscaNome.getText().trim();
        try {
            if (nome.isEmpty()) {
                lista = dao.listarTodos();
            } else {
                lista = dao.buscarPorNome(nome);
            }
            preencherTabela(lista);
            atualizarFormularioParaIndice(0);
        } catch (SQLException ex) {
            showError("Erro na busca: " + ex.getMessage());
        }
    }

    private void abrirClientePorId(int id) {
        try {
            Cliente c = dao.buscarPorId(id);
            if (c != null) {
                txtId.setText(String.valueOf(c.getClienteId()));
                txtNome.setText(c.getNome());
                txtTelefone.setText(c.getTelefone());
                txtEmail.setText(c.getEmail());
                txtCpf.setText(c.getCpf());

                // atualizar currentIndex para navegação
                for (int i = 0; lista != null && i < lista.size(); i++) {
                    if (lista.get(i).getClienteId() == c.getClienteId()) {
                        currentIndex = i;
                        break;
                    }
                }
            }
        } catch (SQLException ex) {
            showError("Erro ao abrir cliente: " + ex.getMessage());
        }
    }

    private void atualizarFormularioParaIndice(int index) {
        if (lista == null || lista.isEmpty()) {
            limparFormulario();
            return;
        }
        if (index < 0) index = 0;
        if (index >= lista.size()) index = lista.size() - 1;
        Cliente c = lista.get(index);
        if (c != null) {
            txtId.setText(String.valueOf(c.getClienteId()));
            txtNome.setText(c.getNome());
            txtTelefone.setText(c.getTelefone());
            txtEmail.setText(c.getEmail());
            txtCpf.setText(c.getCpf());
            tabela.setRowSelectionInterval(index, index);
            currentIndex = index;
        }
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Erro", JOptionPane.ERROR_MESSAGE);
    }

    private void showInfo(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Informação", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new FrmClientes().setVisible(true);
        });
    }
    
    public void carregarCliente(int clienteId) {
        try {
            ClienteDAO dao = new ClienteDAO();
            Cliente c = dao.buscarPorId(clienteId);

            if (c != null) {
                txtId.setText(String.valueOf(c.getClienteId()));
                txtNome.setText(c.getNome());
                txtTelefone.setText(c.getTelefone());
                txtEmail.setText(c.getEmail());
                txtCpf.setText(c.getCpf());
                //txtDataCadastro.setText(String.valueOf(c.getDataCadastro()));

            } else {
                JOptionPane.showMessageDialog(this, "Cliente não encontrado!");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar cliente: " + e.getMessage());
        }
    }

}
