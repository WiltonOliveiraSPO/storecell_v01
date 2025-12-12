package view;

import dao.DBConnection;
import dao.OrdemReparoDAO;
import dao.ServicoDAO;
import model.Servico;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class FrmConsultaServicos extends JFrame {

    private JTextField txtBusca;
    private JTable tabela;
    private DefaultTableModel modelo;
    private ServicoDAO servicoDAO;
    private Connection conn;

    public FrmConsultaServicos() {
        setTitle("Consulta de Serviços");
        setSize(700, 480);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null);

        try {
            conn = DBConnection.getConnection();
            servicoDAO = new ServicoDAO(conn);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro de conexão: " + e.getMessage());
            return;
        }

        JLabel lblBusca = new JLabel("Nome:");
        lblBusca.setBounds(20, 20, 80, 25);
        add(lblBusca);

        txtBusca = new JTextField();
        txtBusca.setBounds(80, 20, 360, 25);
        add(txtBusca);

        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.setBounds(460, 20, 100, 25);
        add(btnBuscar);

        btnBuscar.addActionListener(e -> buscar());

        modelo = new DefaultTableModel(new Object[]{"ID", "Nome", "Valor"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabela = new JTable(modelo);
        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBounds(20, 70, 640, 360);
        add(scroll);

        tabela.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2)
					try {
						abrirServicoSelecionado();
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
            }
        });

        carregarTodos();
    }

    private void carregarTodos() {
        modelo.setRowCount(0);
        try {
            List<Servico> lista = servicoDAO.listar(); // método listar() existe no seu ServicoDAO
            for (Servico s : lista) {
                modelo.addRow(new Object[]{s.getId(), s.getNome(), s.getValor()});
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao listar serviços: " + ex.getMessage());
        }
    }

    private void buscar() {
        String nome = txtBusca.getText().trim();
        modelo.setRowCount(0);
        try {
            // se você adicionou buscarPorNome no ServicoDAO, use-o; senão, filtramos localmente
            List<Servico> lista = servicoDAO.buscarPorNome(nome); // se existir
            if (lista == null || lista.isEmpty()) {
                // fallback
                lista = servicoDAO.listar();
                for (Servico s : lista) {
                    if (s.getNome().toLowerCase().contains(nome.toLowerCase())) {
                        modelo.addRow(new Object[]{s.getId(), s.getNome(), s.getValor()});
                    }
                }
            } else {
                for (Servico s : lista) {
                    modelo.addRow(new Object[]{s.getId(), s.getNome(), s.getValor()});
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro na busca: " + ex.getMessage());
        }
    }

    private void abrirServicoSelecionado() throws SQLException {
        int row = tabela.getSelectedRow();
        if (row < 0) return;
        int servicoId = Integer.parseInt(modelo.getValueAt(row, 0).toString());

        FrmServico frm = new FrmServico();
        frm.setVisible(true);
        frm.carregarServico(servicoId); // adicione este método em FrmServicos
    }
    
    private JComboBox<String> cbStatus;
    private JButton btnGerarPdf;

    private void criarComponentesRelatorio() {
        cbStatus = new JComboBox<>(new String[] {"Aberto", "Em Reparo", "Aguardando Peça", "Pronto", "Entregue"});
        cbStatus.setBounds(20, 20, 180, 25);
        add(cbStatus);

        btnGerarPdf = new JButton("Gerar relatório (PDF)");
        btnGerarPdf.setBounds(220, 20, 180, 25);
        add(btnGerarPdf);

        btnGerarPdf.addActionListener(e -> {
            String status = cbStatus.getSelectedItem().toString();
            gerarRelatorioPorStatus(status);
        });
    }

    private void gerarRelatorioPorStatus(String status) {
        try {
            OrdemReparoDAO dao = new OrdemReparoDAO(DBConnection.getConnection());
            java.util.List<model.RelatorioOrdem> lista = dao.listarPorStatus(status);

            if (lista == null || lista.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nenhuma ordem encontrada para status: " + status);
                return;
            }

            String safeStatus = status.replaceAll("\\s+", "_");
            String output = "C:/storecell/reports/ordens_status_" + safeStatus + ".pdf";

            util.RelatorioPDFUtil.gerarOrdensPorStatusPdf(status, lista, output);

            JOptionPane.showMessageDialog(this, "Relatório gerado: " + output);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao gerar PDF: " + ex.getMessage());
        }
    }

}
