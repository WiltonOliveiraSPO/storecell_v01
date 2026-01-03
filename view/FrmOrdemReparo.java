package view;

import dao.DBConnection;
import java.util.List;

import javax.swing.text.NumberFormatter;
import java.text.DecimalFormat;

import dao.OrdemReparoDAO;
import dao.ClienteDAO;
import dao.ServicoDAO;
import model.OrdemReparo;
import model.OrdemReparoView;
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

    private JFormattedTextField txtValor;
    private JTextField txtId, txtModelo, txtDataPrev, txtDataConc;
    private JTextArea txtProblema, txtObs;
    private JComboBox<String> cbStatus;

    private JTable tabela;
    private DefaultTableModel modelo;
    private OrdemReparoDAO dao;    
    
    // ===== Controle de navegação =====
    private int indiceAtual = -1;
    private List<OrdemReparoView> listaOrdem;


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

        DecimalFormat formato = new DecimalFormat("#,##0.00");
        formato.setMinimumFractionDigits(2);
        formato.setMaximumFractionDigits(2);

        NumberFormatter formatter = new NumberFormatter(formato);
        formatter.setValueClass(Double.class);
        formatter.setAllowsInvalid(false);
        formatter.setMinimum(0.0);

        txtValor = new JFormattedTextField(formatter);
        txtValor.setBounds(80, 180, 100, 25);
        txtValor.setValue(0.00);
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

        JButton btnNovo = new JButton("Nova Ordem");
        btnNovo.setBounds(20, 470, 120, 30);
        btnNovo.addActionListener(e -> novaOrdem());
        add(btnNovo);

        JButton btnSalvar = new JButton("Salvar");
        btnSalvar.setBounds(150, 470, 100, 30);
        btnSalvar.addActionListener(e -> salvar());
        add(btnSalvar);

        JButton btnAtualizar = new JButton("Atualizar");
        btnAtualizar.setBounds(260, 470, 100, 30);
        btnAtualizar.addActionListener(e -> atualizar());
        add(btnAtualizar);

        JButton btnExcluir = new JButton("Excluir");
        btnExcluir.setBounds(370, 470, 100, 30);
        btnExcluir.addActionListener(e -> excluir());
        add(btnExcluir);

        JButton btnPdf = new JButton("Gerar PDF");
        btnPdf.setBounds(480, 470, 120, 30);
        btnPdf.addActionListener(e -> gerarPDF());
        add(btnPdf);
        
        JButton btnPrimeiro = new JButton("|<");
        JButton btnAnterior = new JButton("<");
        JButton btnProximo  = new JButton(">");
        JButton btnUltimo   = new JButton(">|");

        btnPrimeiro.addActionListener(e -> primeiro());
        btnAnterior.addActionListener(e -> anterior());
        btnProximo.addActionListener(e -> proximo());
        btnUltimo.addActionListener(e -> ultimo());

        JPanel painelBotoes = new JPanel();
        painelBotoes.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
        painelBotoes.setBounds(20, 520, 850, 40);

        painelBotoes.add(btnPrimeiro);
        painelBotoes.add(btnAnterior);
        painelBotoes.add(btnProximo);
        painelBotoes.add(btnUltimo);
        painelBotoes.add(btnNovo);
        painelBotoes.add(btnSalvar);
        painelBotoes.add(btnAtualizar);
        painelBotoes.add(btnExcluir);
        painelBotoes.add(btnPdf);

        add(painelBotoes);


        modelo = new DefaultTableModel(new Object[]{
        	    "ID", "Cliente", "Serviço", "Modelo", "Status", "Valor", "Entrada"
        	}, 0);

        tabela = new JTable(modelo);
        JScrollPane spTabela = new JScrollPane(tabela);
        spTabela.setBounds(580, 20, 300, 450);
        add(spTabela);

        tabela.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                carregarCamposDaTabela();
            }
        });

        listar();
    }

    private void novaOrdem() {

        ordemAtual = new OrdemReparo(); // 🔥 ESSENCIAL

        txtId.setText("");

        cbClientes.setSelectedIndex(0);
        cbServicos.setSelectedIndex(0);

        txtModelo.setText("");
        txtValor.setValue(0.00);

        txtDataPrev.setText("");
        txtDataConc.setText("");

        txtProblema.setText("");
        txtObs.setText("");

        cbStatus.setSelectedItem("Aberto");

        txtModelo.requestFocus();
    }
    
    private OrdemReparo ordemAtual;

    private void btnNovaOrdemActionPerformed(java.awt.event.ActionEvent evt) {

        ordemAtual = new OrdemReparo(); // 🔴 ESSENCIAL

        txtId.setText("");
        txtModelo.setText("");
        txtValor.setText("0,00");
        txtProblema.setText("");
        txtObs.setText("");
        txtDataPrev.update(null);
        txtDataConc.update(null);

        //cbStatusc.setSelectedIndex(0);
        cbClientes.setSelectedIndex(-1);
        cbServicos.setSelectedIndex(-1);
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

        if (ordemAtual == null) {
            JOptionPane.showMessageDialog(this,
                "Clique em 'Nova Ordem' antes de salvar.",
                "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Cliente cliente = (Cliente) cbClientes.getSelectedItem();
        Servico servico = (Servico) cbServicos.getSelectedItem();

        if (cliente == null || servico == null) {
            JOptionPane.showMessageDialog(this,
                "Cliente e Serviço são obrigatórios.",
                "Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }

        ordemAtual.setClienteId(cliente.getClienteId());
        ordemAtual.setServicoId(servico.getId());
        ordemAtual.setDispositivoModelo(txtModelo.getText());
        ordemAtual.setStatus(cbStatus.getSelectedItem().toString());
        ordemAtual.setValorTotal(((Number) txtValor.getValue()).doubleValue());
        ordemAtual.setProblemaRelatado(txtProblema.getText());
        ordemAtual.setObservacoes(txtObs.getText());

        try {
            dao.inserir(ordemAtual);
            JOptionPane.showMessageDialog(this, "Ordem de Serviço salva com sucesso!");
            listar();
            novaOrdem(); // limpa e prepara nova
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Erro ao salvar: " + e.getMessage(),
                "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private double parseValor(String text) {
		// TODO Auto-generated method stub
		return 0;
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
            listaOrdem = dao.listarComJoin();

            for (OrdemReparoView v : listaOrdem) {
                modelo.addRow(new Object[]{
                    v.getId(),
                    v.getCliente(),
                    v.getServico(),
                    v.getModelo(),
                    v.getStatus(),
                    String.format("R$ %.2f", v.getValor()),
                    v.getDataEntrada()
                });
            }

            indiceAtual = listaOrdem.isEmpty() ? -1 : 0;

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro listar: " + e.getMessage());
        }
    }


    private void carregarCamposDaTabela() {
        int row = tabela.getSelectedRow();

        txtId.setText(modelo.getValueAt(row, 0).toString());
        txtModelo.setText(modelo.getValueAt(row, 3).toString());
        txtValor.setText(modelo.getValueAt(row, 5).toString());

        cbStatus.setSelectedItem(modelo.getValueAt(row, 4).toString());
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
    
    private void gerarPDF() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecione uma ordem!");
            return;
        }

        try {
            OrdemReparo ordem = dao.buscarPorId(Integer.parseInt(txtId.getText()));

            Cliente cliente = (Cliente) cbClientes.getSelectedItem();
            Servico servico = (Servico) cbServicos.getSelectedItem();

            util.OrdemServicoPDF.gerar(ordem, cliente, servico);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao gerar PDF: " + e.getMessage());
        }
    }
    
    
    private void atualizar() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecione uma ordem para atualizar.");
            return;
        }

        try {
            OrdemReparo o = montarObjeto();
            o.setId(Integer.parseInt(txtId.getText()));

            dao.atualizar(o);
            listar();
            JOptionPane.showMessageDialog(this, "Ordem atualizada!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro atualizar: " + e.getMessage());
        }
    }

    private OrdemReparo montarObjeto() {
        OrdemReparo o = new OrdemReparo();

        Cliente cliente = (Cliente) cbClientes.getSelectedItem();
        Servico servico = (Servico) cbServicos.getSelectedItem();

        o.setClienteId(cliente.getClienteId());
        o.setServicoId(servico.getId());
        o.setDispositivoModelo(txtModelo.getText());
        o.setProblemaRelatado(txtProblema.getText());
        o.setStatus(cbStatus.getSelectedItem().toString());
        o.setValorTotal(((Number) txtValor.getValue()).doubleValue());
        o.setObservacoes(txtObs.getText());

        o.setDataPrevisaoEntrega(java.sql.Date.valueOf(txtDataPrev.getText()));

        if (!txtDataConc.getText().isEmpty()) {
            o.setDataConclusao(java.sql.Date.valueOf(txtDataConc.getText()));
        }

        return o;
    }


    private void primeiro() {
        if (listaOrdem.isEmpty()) return;
        indiceAtual = 0;
        selecionarLinha();
    }

    private void anterior() {
        if (indiceAtual > 0) {
            indiceAtual--;
            selecionarLinha();
        }
    }

    private void proximo() {
        if (indiceAtual < listaOrdem.size() - 1) {
            indiceAtual++;
            selecionarLinha();
        }
    }

    private void ultimo() {
        if (!listaOrdem.isEmpty()) {
            indiceAtual = listaOrdem.size() - 1;
            selecionarLinha();
        }
    }

    private void selecionarLinha() {
        tabela.setRowSelectionInterval(indiceAtual, indiceAtual);
        carregarCamposDaTabela();
    }

}
