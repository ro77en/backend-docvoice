package br.com.doc_voice.service;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

@Service
public class ImageTextExtractionService {

    public String extractText(MultipartFile file) throws IOException, TesseractException {
        File tempFile = File.createTempFile("upload", ".tmp");
        file.transferTo(tempFile);

        Mat image = opencv_imgcodecs.imread(tempFile.getAbsolutePath(), opencv_imgcodecs.IMREAD_GRAYSCALE); 
        Mat processed = new Mat();
        opencv_imgproc.GaussianBlur(image, processed, new org.bytedeco.opencv.opencv_core.Size(3, 3), 0);
        BufferedImage bufferedImage = matToBufferedImage(processed);

        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath("C:\\Users\\nilto\\Downloads"); 
        tesseract.setLanguage("por"); 

        String result = tesseract.doOCR(bufferedImage);

        // Loga o resultado no console para debug
        System.out.println("Texto extraído: [" + result + "]");

        tempFile.delete();

        // Se não extraiu nada, retorna mensagem clara
        if (result == null || result.trim().isEmpty()) {
            return "Nenhum texto foi extraído da imagem.";
        }
        return result;
    }

    private BufferedImage matToBufferedImage(Mat mat) throws IOException {
        File temp = File.createTempFile("mat", ".png");
        opencv_imgcodecs.imwrite(temp.getAbsolutePath(), mat);
        BufferedImage img = ImageIO.read(temp);
        temp.delete();
        return img;
    }
}