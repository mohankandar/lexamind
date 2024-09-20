package com.lexavault.ai.service;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.util.Span;
import org.springframework.stereotype.Service;

@Service
public class NLPService {

  public void processText(String text) {
    try {
      // Get the directory path for the NER models (inside resources/nlp)
      URL resource = getClass().getResource("/nlp");
      if (resource == null) {
        throw new IllegalArgumentException("NLP directory not found in resources.");
      }

      // Convert the resource URL to a file object
      File nlpDir = new File(resource.toURI());

      // Iterate over all .bin files in the directory
      for (File file : Objects.requireNonNull(nlpDir.listFiles())) {
        if (file.getName().endsWith(".bin")) {
          System.out.println("Processing file: " + file.getName());
          // Pass the file path to the detectEntities method
          detectEntities(text, "/nlp/" + file.getName());
        }
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void detectEntities(String text, String modelPath) {
    // Load the NER model from the resources folder
    try (InputStream modelIn = getClass().getResourceAsStream(modelPath)) {
      if (modelIn == null) {
        throw new IllegalArgumentException("Model file not found in resources: " + modelPath);
      }

      TokenNameFinderModel model = new TokenNameFinderModel(modelIn);
      NameFinderME nameFinder = new NameFinderME(model);

      // Tokenize the input text
      SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
      String[] tokens = tokenizer.tokenize(text);

      // Find named entities
      Span[] names = nameFinder.find(tokens);

      // Print detected entities
      for (Span name : names) {
        StringBuilder entity = new StringBuilder();
        for (int i = name.getStart(); i < name.getEnd(); i++) {
          entity.append(tokens[i]).append(" ");
        }
        System.out.println("Entity: " + entity.toString().trim());
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
