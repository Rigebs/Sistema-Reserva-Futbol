//package com.cruz_sur.api.service.imp;
//
//import com.google.zxing.BarcodeFormat;
//import com.google.zxing.common.BitMatrix;
//import com.google.zxing.qrcode.QRCodeWriter;
//import com.google.zxing.client.j2se.MatrixToImageWriter;
//import org.springframework.stereotype.Service;
//
//import java.io.ByteArrayOutputStream;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.Base64;
//
//@Service
//public class QRService {
//
//    public String generateQR(String ruc) throws Exception {
//        LocalDateTime now = LocalDateTime.now();
//        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
//        String date = now.format(dateFormatter);
//        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HHmmss");
//        String time = now.format(timeFormatter);
//
//        String content = ruc + "B" + date + time;
//
//        QRCodeWriter qrCodeWriter = new QRCodeWriter();
//        BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, 300, 300);
//        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
//        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
//
//        byte[] pngData = pngOutputStream.toByteArray();
//        return Base64.getEncoder().encodeToString(pngData);
//    }
//}
