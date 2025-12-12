package util;

import model.RelatorioOrdem;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class RelatorioPDFUtil {

    private static final DecimalFormat df = new DecimalFormat("#,##0.00");
    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    private static final String LOGO_PATH = "C:/storecell/icons/logo_relatorio.png";

    public static void gerarOrdensPorStatusPdf(
            String status,
            List<RelatorioOrdem> dados,
            String outputPath
    ) throws IOException {

        PDDocument doc = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        doc.addPage(page);

        PDPageContentStream cs = new PDPageContentStream(doc, page);

        float margin = 40;
        float y = page.getMediaBox().getHeight() - margin;

        // ======================
        // 1. INSERIR LOGO
        // ======================
        try {
            PDImageXObject logo = PDImageXObject.createFromFile(LOGO_PATH, doc);
            cs.drawImage(logo, margin, y - 60, 120, 60);
        } catch (Exception e) {
            // Se der erro, apenas ignora — sem logo
        }

        y -= 80;

        // ======================
        // 2. TÍTULO
        // ======================
        cs.beginText();
        cs.setFont(PDType1Font.HELVETICA_BOLD, 18);
        cs.newLineAtOffset(margin, y);
        cs.showText("Relatório de Ordens de Reparo");
        cs.endText();

        y -= 22;

        cs.beginText();
        cs.setFont(PDType1Font.HELVETICA, 14);
        cs.newLineAtOffset(margin, y);
        cs.showText("Status: " + status);
        cs.endText();

        y -= 18;

        cs.beginText();
        cs.setFont(PDType1Font.HELVETICA_OBLIQUE, 10);
        cs.newLineAtOffset(margin, y);
        cs.showText("Gerado em: " + sdf.format(new java.util.Date()));
        cs.endText();

        y -= 25;

        // ======================
        // 3. TABELA
        // ======================
        float tableWidth = page.getMediaBox().getWidth() - 2 * margin;
        float rowHeight = 18;
        float headerHeight = 20;

        // Colunas
        float[] colWidths = {40, 130, 80, 80, 60, 80};
        String[] colNames = {"ID", "Cliente", "Serviço", "Modelo", "Valor", "Entrada"};

        // Desenha fundo do cabeçalho
        cs.setNonStrokingColor(220); // cinza claro
        cs.addRect(margin, y - headerHeight, tableWidth, headerHeight);
        cs.fill();

        // Bordas do cabeçalho
        cs.setStrokingColor(Color.BLACK);
        cs.addRect(margin, y - headerHeight, tableWidth, headerHeight);
        cs.stroke();

        // Títulos das colunas
        float x = margin + 2;
        cs.setNonStrokingColor(Color.BLACK);
        for (int i = 0; i < colNames.length; i++) {
            cs.beginText();
            cs.setFont(PDType1Font.HELVETICA_BOLD, 10);
            cs.newLineAtOffset(x, y - 15);
            cs.showText(colNames[i]);
            cs.endText();
            x += colWidths[i];
        }

        y -= headerHeight;

        // ======================
        // 4. LINHAS DA TABELA
        // ======================

        double somaValores = 0;
        int totalOrdens = 0;

        for (RelatorioOrdem r : dados) {

            if (y < 80) {
                cs.close();
                page = new PDPage(PDRectangle.A4);
                doc.addPage(page);
                cs = new PDPageContentStream(doc, page);
                y = page.getMediaBox().getHeight() - margin;
            }

            // borda da linha
            cs.addRect(margin, y - rowHeight, tableWidth, rowHeight);
            cs.stroke();

            x = margin + 2;

            String[] valores = {
                    String.valueOf(r.getOrdemId()),
                    trunc(r.getClienteNome(), 25),
                    trunc(r.getServicoNome(), 15),
                    trunc(r.getDispositivoModelo(), 15),
                    "R$ " + df.format(r.getValorTotal()),
                    r.getDataEntrada() != null ? sdf.format(r.getDataEntrada()) : ""
            };

            for (int i = 0; i < valores.length; i++) {
                cs.beginText();
                cs.setFont(PDType1Font.HELVETICA, 9);
                cs.newLineAtOffset(x, y - 12);
                cs.showText(valores[i]);
                cs.endText();
                x += colWidths[i];
            }

            somaValores += r.getValorTotal();
            totalOrdens++;

            y -= rowHeight;
        }

        // ======================
        // 5. TOTALIZADOR
        // ======================

        y -= 20; // espaço extra

        cs.setFont(PDType1Font.HELVETICA_BOLD, 12);

        cs.beginText();
        cs.newLineAtOffset(margin, y);
        cs.showText("Total de Ordens: " + totalOrdens);
        cs.endText();

        y -= 15;

        cs.beginText();
        cs.newLineAtOffset(margin, y);
        cs.showText("Somatório dos valores: R$ " + df.format(somaValores));
        cs.endText();

        cs.close();

        // ======================
        // 6. SALVAR E ABRIR
        // ======================
        File file = new File(outputPath);
        file.getParentFile().mkdirs();
        doc.save(file);
        doc.close();

        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().open(file);
        }
    }

    private static String trunc(String s, int max) {
        if (s == null) return "";
        if (s.length() <= max) return s;
        return s.substring(0, max - 3) + "...";
    }
}
