package util;

import model.OrdemReparo;
import model.Cliente;
import model.Servico;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import java.awt.image.BufferedImage;


import java.io.File;

public class OrdemServicoPDF {

    public static void gerar(
            OrdemReparo ordem,
            Cliente cliente,
            Servico servico
    ) throws Exception {

        PDDocument doc = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        doc.addPage(page);
        
        

        PDPageContentStream cs = new PDPageContentStream(doc, page);
        
     // ===== QR CODE =====
        BufferedImage qrImage = QRCodeUtil.gerarQRCode(
                "OS-" + ordem.getId(),
                150,
                150
        );

        PDImageXObject pdImage = LosslessFactory.createFromImage(doc, qrImage);

        // posição do QR Code no PDF
        cs.drawImage(pdImage, 420, 650, 120, 120);
        escrever(cs, 420, 630, "OS Nº " + ordem.getId());


        int y = 750;

        // ===== TÍTULO =====
        cs.setFont(PDType1Font.HELVETICA_BOLD, 18);
        cs.beginText();
        cs.newLineAtOffset(180, y);
        cs.showText("ORDEM DE SERVIÇO");
        cs.endText();

        y -= 40;

        // ===== DADOS DA EMPRESA =====
        cs.setFont(PDType1Font.HELVETICA, 10);
        escrever(cs, 50, y, "StoreCell - Assistência Técnica");
        escrever(cs, 50, y - 15, "Telefone: (00) 00000-0000");

        y -= 40;

        // ===== CLIENTE =====
        cs.setFont(PDType1Font.HELVETICA_BOLD, 12);
        escrever(cs, 50, y, "Cliente");

        cs.setFont(PDType1Font.HELVETICA, 10);
        escrever(cs, 50, y - 15, "Nome: " + cliente.getNome());
        escrever(cs, 50, y - 30, "Telefone: " + cliente.getTelefone());

        y -= 60;

        // ===== SERVIÇO =====
        cs.setFont(PDType1Font.HELVETICA_BOLD, 12);
        escrever(cs, 50, y, "Serviço");

        cs.setFont(PDType1Font.HELVETICA, 10);
        escrever(cs, 50, y - 15, "Descrição: " + servico.getNome());
        escrever(cs, 50, y - 30, "Valor: R$ " + ordem.getValorTotal());

        y -= 60;

        // ===== EQUIPAMENTO =====
        cs.setFont(PDType1Font.HELVETICA_BOLD, 12);
        escrever(cs, 50, y, "Equipamento");

        cs.setFont(PDType1Font.HELVETICA, 10);
        escrever(cs, 50, y - 15, "Modelo: " + ordem.getDispositivoModelo());
        escrever(cs, 50, y - 30, "Problema: " + ordem.getProblemaRelatado());

        y -= 70;

        // ===== STATUS =====
        escrever(cs, 50, y, "Status: " + ordem.getStatus());

        y -= 60;

        // ===== ASSINATURA =====
        escrever(cs, 50, y, "__________________________________________");
        escrever(cs, 50, y - 15, "Assinatura do Cliente");

        cs.close();

        File arquivo = new File(
                "ordem_servico_" + ordem.getId() + ".pdf"
        );

        doc.save(arquivo);
        doc.close();

        // Abrir automaticamente
        java.awt.Desktop.getDesktop().open(arquivo);
    }

    private static void escrever(PDPageContentStream cs, int x, int y, String texto) throws Exception {
        cs.beginText();
        cs.newLineAtOffset(x, y);
        cs.showText(texto);
        cs.endText();
    }
}
