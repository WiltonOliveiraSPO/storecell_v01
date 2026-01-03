package util;

import com.google.zxing.*;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.awt.image.BufferedImage;

public class QRCodeUtil {

    public static BufferedImage gerarQRCode(String texto, int largura, int altura)
            throws WriterException {

        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix matrix = writer.encode(
                texto,
                BarcodeFormat.QR_CODE,
                largura,
                altura
        );

        return MatrixToImageWriter.toBufferedImage(matrix);
    }
}
