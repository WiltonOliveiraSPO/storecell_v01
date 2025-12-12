package view;

import dao.ClienteDAO;
import model.Cliente;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class FrmConsultaClientes extends JFrame {

    private JTextField txtBusca;
    private JTable tabela;
    private DefaultTableModel modelo;
    private ClienteDAO clienteDAO;

    public FrmConsultaClientes() {
        setTitle("Consulta de Clientes");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null);

        clienteDAO = new ClienteDAO();

        JLabel lblBusca = new JLabel("Nome:");
        lblBusca.setBounds(20, 20, 80, 25);
        add(lblBusca);

        txtBusca = new JTextField();
        txtBusca.setBounds(80, 20, 350, 25);
        add(txtBusca);

        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.setBounds(450, 20, 120, 25);
        add(btnBuscar);

        btnBuscar.addActionListener(e -> buscarClientes());

        // Modelo da tabela
        modelo = new DefaultTableModel(new Object[]{
                "ID", "Nome", "Telefone", "Email", "CPF", "Data Cadastro"
        }, 0);

        tabela = new JTable(modelo);
        JScrollPane scroll = new JScrollPane(tabela);
        tabela.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) { // duplo clique
                    abrirClienteSelecionado();
                }
            }

            private void abrirClienteSelecionado() {
                int linha = tabela.getSelectedRow();

                if (linha < 0) {
                    return; // nada selecionado
                }

                // Pega o ID da primeira coluna
                int clienteId = (int) tabela.getValueAt(linha, 0);

                // Abre a tela de clientes
                FrmClientes frm = new FrmClientes();
                frm.setVisible(true);

                // Chama o método que preenche a tela
                frm.carregarCliente(clienteId);
            }

        });

        scroll.setBounds(20, 70, 750, 370);
        add(scroll);
    }

    private void buscarClientes() {
        modelo.setRowCount(0); // limpa linha existente

        try {
            List<Cliente> lista = clienteDAO.buscarPorNome(txtBusca.getText());

            for (Cliente c : lista) {
                modelo.addRow(new Object[]{
                        c.getClienteId(),
                        c.getNome(),
                        c.getTelefone(),
                        c.getEmail(),
                        c.getCpf(),
                        c.getDataCadastro()
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao buscar clientes:\n" + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
