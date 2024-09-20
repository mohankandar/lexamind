package com.lexavault.ai.controller;

import com.lexavault.ai.service.AIModelService;
import com.lexavault.ai.service.NLPService;
import com.lexavault.ai.service.OCRService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/data")
public class DataController {

  @Autowired
  private OCRService ocrService;

  @Autowired
  private NLPService nlpService;

  @Autowired
  private AIModelService aiModelService;

  @PostMapping("/process")
  public ResponseEntity<String> processDocument(@RequestParam("file") MultipartFile file) {
    // OCR to extract text
    String extractedText = ocrService.extractTextFromFile(file);

    // NLP for text processing (e.g., named entity recognition)
    nlpService.processText(extractedText);

    // AI Model (ML) for additional learning-based tasks
    aiModelService.analyzeText(extractedText);

    System.out.println(extractedText);
    return ResponseEntity.ok("Document processed");
  }


}