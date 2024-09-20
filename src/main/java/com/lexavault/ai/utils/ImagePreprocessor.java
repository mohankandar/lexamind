package com.lexavault.ai.utils;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class ImagePreprocessor {

  static {
    try {
      // Detect OS and load the corresponding native library
      String os = System.getProperty("os.name").toLowerCase();
      if (os.contains("win")) {
        loadNativeLibraryFromResource("/lib/win/opencv_java4100.dll");
      } else if (os.contains("nix") || os.contains("nux")) {
        loadNativeLibraryFromResource("/lib/linux/libopencv_java430.so");
      } else if (os.contains("mac")) {
        loadNativeLibraryFromResource("/lib/mac/libopencv_java430.dylib");
      } else {
        throw new UnsupportedOperationException("Unsupported OS: " + os);
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException("Failed to load OpenCV library");
    }
  }

  // Utility method to load a native library from resources
  private static void loadNativeLibraryFromResource(String path) throws Exception {
    InputStream in = ImagePreprocessor.class.getResourceAsStream(path);
    if (in == null) {
      throw new IllegalStateException("Native library not found in resources: " + path);
    }
    // Create a temporary file for the native library
    File tempFile = File.createTempFile("opencv", getExtension(path));
    tempFile.deleteOnExit();
    // Copy the native library from the resources to the temporary file
    Files.copy(in, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
    // Load the native library
    System.load(tempFile.getAbsolutePath());
  }

  // Helper method to get the correct file extension based on OS
  private static String getExtension(String path) {
    if (path.endsWith(".dll")) {
      return ".dll";
    }
    if (path.endsWith(".so")) {
      return ".so";
    }
    if (path.endsWith(".dylib")) {
      return ".dylib";
    }
    return "";
  }

  // Method to preprocess image from File
  public static void preprocessImage(File imageFile, String outputPath) {
    // Read the image
    Mat image = Imgcodecs.imread(imageFile.getAbsolutePath());

    // Convert the image to grayscale
    Imgproc.cvtColor(image, image, Imgproc.COLOR_BGR2GRAY);

    // Apply Gaussian blur to reduce noise
    Imgproc.GaussianBlur(image, image, new Size(3, 3), 0);

    // Apply adaptive thresholding (binarization)
    Imgproc.adaptiveThreshold(image, image, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,
        Imgproc.THRESH_BINARY, 11, 2);

    // Save the preprocessed image
    Imgcodecs.imwrite(outputPath, image);
  }
}
