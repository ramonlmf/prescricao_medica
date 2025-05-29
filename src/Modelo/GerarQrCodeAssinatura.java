/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

/**
 *
 * @author ramon
 */
public class GerarQrCodeAssinatura {
     /**
     * 
     * @param args 
     * @throws WriterException
     * @throws IOException
     * @throws NotFoundException
     */
    
  public static void gerarQrCode(String qrCode)  {
    String qrCodeData = "http://www.prescricaodigital.org.br/consulta-doc!sw=" + qrCode; 
    String filePath = "D:\\Tcc 2025\\Sistema_PF2\\ReceitaEletronica\\qrcode\\" + qrCode + "qrcode.png";
    String charset = "UTF-8"; // or "ISO-8859-1"
    Map<EncodeHintType, ErrorCorrectionLevel> hintMap = new HashMap<EncodeHintType, ErrorCorrectionLevel>();
    hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);

    try {        
        createQRCode(qrCodeData, filePath, charset, hintMap, 200, 200);
        System.out.println("QR Code image created successfully!");

         System.out.println("Data read from QR Code: " + readQRCode(filePath, charset, hintMap));
            
        } 
        catch (WriterException | IOException | NotFoundException  e) {
            System.out.println(e);            
        }

  }

  /***
   * 
   * @param qrCodeData
   * @param filePath
   * @param charset
   * @param hintMap
   * @param qrCodeheight
   * @param qrCodewidth
   * @throws WriterException
   * @throws IOException
   */
  public static void createQRCode(String qrCodeData, String filePath, 
      String charset, Map hintMap, int qrCodeheight, int qrCodewidth)
      throws WriterException, IOException {
    BitMatrix matrix = new MultiFormatWriter().encode(
        new String(qrCodeData.getBytes(charset), charset),
        BarcodeFormat.QR_CODE, qrCodewidth, qrCodeheight);
    MatrixToImageWriter.writeToFile(matrix, filePath.substring(filePath
        .lastIndexOf('.') + 1), new File(filePath));
  }

  /**
   * 
   * @param filePath
   * @param charset
   * @param hintMap
   * 
   * @return Qr Code value 
   * 
   * @throws FileNotFoundException
   * @throws IOException
   * @throws NotFoundException
   */
  public static String readQRCode(String filePath, String charset, Map hintMap)
      throws FileNotFoundException, IOException, NotFoundException {
    BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(
        new BufferedImageLuminanceSource(
            ImageIO.read(new FileInputStream(filePath)))));
    Result qrCodeResult = new MultiFormatReader().decode(binaryBitmap, hintMap);
    return qrCodeResult.getText();
  }
}
