package view;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class FrmPrincipal extends JFrame {

    private JLabel imagemCentral;

    public FrmPrincipal() {
        setTitle("StoreCell - Sistema de PDV e Assistência Técnica");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Janela maximizada
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Menu Principal
        setJMenuBar(criarMenu());

        // Painel com imagem central
        criarImagemCentral();

        // Centralizar caso seja restaurado
        setLocationRelativeTo(null);
    }

    private JMenuBar criarMenu() {
        JMenuBar menuBar = new JMenuBar();

        // ===== MENU CADASTRO =====
        JMenu menuCadastro = new JMenu("Cadastro");

        JMenuItem itemClientes = new JMenuItem("Clientes");
        itemClientes.addActionListener(e -> new FrmClientes().setVisible(true));
        menuCadastro.add(itemClientes);

        JMenuItem itemProdutos = new JMenuItem("Produtos");
        itemProdutos.addActionListener(e -> {
            try {
                new FrmProduto().setVisible(true);
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        });
        menuCadastro.add(itemProdutos);

        JMenuItem itemServicos = new JMenuItem("Serviços");
        itemServicos.addActionListener(e -> {
			new FrmServico().setVisible(true);
		});
        menuCadastro.add(itemServicos);

        // ORDENS DE REPARO
        JMenuItem itemOrdens = new JMenuItem("Ordens de Reparo");
        itemOrdens.addActionListener(e -> new FrmOrdemReparo().setVisible(true));
        menuCadastro.add(itemOrdens);

        menuBar.add(menuCadastro);


        // ===== MENU VENDAS =====
        JMenu menuVendas = new JMenu("Vendas");

        JMenuItem itemNovaVenda = new JMenuItem("Nova Venda");
        itemNovaVenda.addActionListener(e -> new FrmNovaVenda().setVisible(true));
        menuVendas.add(itemNovaVenda);

        menuBar.add(menuVendas);


        // ===== MENU CONSULTAS =====
        JMenu menuConsultas = new JMenu("Consultas");

        JMenuItem consClientes = new JMenuItem("Clientes");
        consClientes.addActionListener(e -> new FrmConsultaClientes().setVisible(true));
        menuConsultas.add(consClientes);

        JMenuItem consProdutos = new JMenuItem("Produtos");
        consProdutos.addActionListener(e -> new FrmConsultaProdutos().setVisible(true));
        menuConsultas.add(consProdutos);

        JMenuItem consServicos = new JMenuItem("Serviços");
        consServicos.addActionListener(e -> new FrmConsultaServicos().setVisible(true));
        menuConsultas.add(consServicos);

        menuBar.add(menuConsultas);


        // ===== MENU RELATÓRIOS =====
        JMenu menuRelatorios = new JMenu("Relatórios");

        JMenuItem relVendasDia = new JMenuItem("Vendas por Dia");
        menuRelatorios.add(relVendasDia);

        JMenuItem relVendasServicos = new JMenuItem("Vendas por Serviço");
        menuRelatorios.add(relVendasServicos);

        menuBar.add(menuRelatorios);


        // ===== MENU SAIR =====
        JMenu menuSair = new JMenu("Sair");

        JMenuItem itemSair = new JMenuItem("Fechar Sistema");
        itemSair.addActionListener(e -> System.exit(0)); // FECHA TUDO
        menuSair.add(itemSair);

        menuBar.add(menuSair);

        return menuBar;
    }

    private void criarImagemCentral() {
        imagemCentral = new JLabel();
        imagemCentral.setHorizontalAlignment(SwingConstants.CENTER);

        try {
            ImageIcon img = new ImageIcon("C:\\storecell\\icons\\lojinha.jpg");
            Image imagemRedimensionada = img.getImage().getScaledInstance(550, 350, Image.SCALE_SMOOTH);
            imagemCentral.setIcon(new ImageIcon(imagemRedimensionada));
        } catch (Exception e) {
            imagemCentral.setText("Imagem não encontrada: C:\\storecell\\icons\\lojinha.jpg");
        }

        add(imagemCentral, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FrmPrincipal().setVisible(true));
    }
}
