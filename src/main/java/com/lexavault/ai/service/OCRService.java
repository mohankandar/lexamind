package com.lexavault.ai.service;

import com.lexavault.ai.utils.ImagePreprocessor;
import com.lexavault.ai.utils.SpellChecker;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Service
public class OCRService {

  public String extractTextFromFile(MultipartFile file) {
    // Initialize Tesseract OCR engine
    Tesseract tesseract = new Tesseract();
    tesseract.setPageSegMode(1);  // 1 = Automatic page segmentation with OSD
    tesseract.setDatapath("C:\\dev\\tessdata");  // Ensure this path is correct

    try {
      // Convert MultipartFile to File
      File tempFile = convertMultipartFileToFile(file);

      if (tempFile == null || !tempFile.exists()) {
        throw new IOException("The file is null or does not exist.");
      }

      // Handle PDF files separately
      if (tempFile.getName().toLowerCase().endsWith(".pdf")) {
        // Convert PDF to an image with 300 DPI
        BufferedImage image = convertPdfToImage(tempFile);
        // Save the BufferedImage to a temporary file for further processing
        File imageFile = new File(System.getProperty("java.io.tmpdir"), "convertedImage.png");
        ImageIO.write(image, "png", imageFile);

        // Preprocess the saved image file
        String preprocessedImagePath = System.getProperty("java.io.tmpdir") + "/preprocessedImage.png";
        ImagePreprocessor.preprocessImage(imageFile, preprocessedImagePath);  // Preprocess before OCR

        // Perform OCR on the preprocessed image file
        File preprocessedFile = new File(preprocessedImagePath);
        return performOcrAndSpellCheck(preprocessedFile, tesseract);
      }

      // If not a PDF, process the file directly
      // Preprocess the image directly (for non-PDF files)
      String outputPath = System.getProperty("java.io.tmpdir") + "/preprocessedImage.png";
      ImagePreprocessor.preprocessImage(tempFile, outputPath);
      File processedImageFile = new File(outputPath);
      return performOcrAndSpellCheck(processedImageFile, tesseract);

    } catch (TesseractException | IOException e) {
      e.printStackTrace();
      return "Error during OCR processing: " + e.getMessage();
    }
  }

  // Perform OCR and then spell-check the result
  private String performOcrAndSpellCheck(File imageFile, Tesseract tesseract) throws TesseractException, IOException {
    // Perform OCR
    String ocrResult = tesseract.doOCR(imageFile);
    // Use SpellChecker to correct the OCR result
    return SpellChecker.correctText(ocrResult);
  }

  // Utility method to convert MultipartFile to File
  private File convertMultipartFileToFile(MultipartFile file) throws IOException {
    if (file == null || file.isEmpty()) {
      throw new IOException("Uploaded file is empty or null.");
    }
    File convFile = new File(System.getProperty("java.io.tmpdir") + "/" + file.getOriginalFilename());
    file.transferTo(convFile);  // Save the MultipartFile to a temporary file
    return convFile;
  }

  // Method to convert PDF to BufferedImage with 300 DPI
  private BufferedImage convertPdfToImage(File pdfFile) throws IOException {
    PDDocument document = PDDocument.load(pdfFile);
    PDFRenderer pdfRenderer = new PDFRenderer(document);

    // Render the first page of the PDF at 300 DPI
    BufferedImage image = pdfRenderer.renderImageWithDPI(0, 300);  // 300 DPI for high-quality OCR
    document.close();
    return image;
  }
}
