package com.lexavault.ai;

import com.lexavault.ai.controller.DataController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class StandaloneProcessor {

  public static void main(String[] args) {
    // Initialize Spring context
    ApplicationContext context = SpringApplication.run(LexamindApplication.class, args);

    // Get the DataController bean from Spring context
    DataController dataController = context.getBean(DataController.class);

    // Mock a file to be uploaded (for testing purposes)
    try {
      File file = new File("C:\\dev\\Contract.pdf");
      FileInputStream input = new FileInputStream(file);
      MultipartFile multipartFile = new MockMultipartFile("file", file.getName(), "application/pdf", input);

      // Call the processDocument method
      dataController.processDocument(multipartFile);

    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
