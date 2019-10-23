/*
 * Copyright 2019 (C) Magenic, All rights Reserved
 */

package com.magenic.jmaqs.appium;

import com.magenic.jmaqs.utilities.logging.FileLogger;
import com.magenic.jmaqs.utilities.logging.LoggingConfig;
import com.magenic.jmaqs.utilities.logging.MessageType;
import io.appium.java_client.AppiumDriver;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebElement;

/**
 * Appium Utilities class.
 */
public class AppiumUtilities {

  private AppiumUtilities() {

  }

  /**
   * To capture a screenshot during execution.
   * Default parameter appendName of empty string.
   *
   * @param appiumDriver The Appium Driver
   * @param testObject   The Test Object to associate the screenshot.
   * @return True if the image was saved successfully, otherwise false.
   */
  public static boolean captureScreenshot(AppiumDriver<WebElement> appiumDriver,
      AppiumTestObject testObject) {
    return captureScreenshot(appiumDriver, testObject, "");
  }

  /**
   * To capture a screenshot during execution.
   *
   * @param appiumDriver The Appium Driver
   * @param testObject   The Test Object to associate the screenshot.
   * @param appendName   The Name to append
   * @return True if the image was saved successfully, otherwise false.
   */
  public static boolean captureScreenshot(AppiumDriver<WebElement> appiumDriver,
      AppiumTestObject testObject, String appendName) {
    try {
      // Check if we are using a file logger. If not, return false.
      if (!(testObject.getLog() instanceof FileLogger)) {
        return false;
      }

      // Calculate the file name
      String fullPath = ((FileLogger) testObject.getLog()).getFilePath();
      String directory = Paths.get(fullPath).normalize().toString();
      String fileNameWithoutExtension = testObject.getFullyQualifiedTestName() + appendName;
      captureScreenshot(appiumDriver, testObject, directory, fileNameWithoutExtension);

      testObject.getLog().logMessage(MessageType.INFORMATION, "Screenshot saved.");
      return true;
    } catch (Exception exception) {
      testObject.getLog().logMessage(MessageType.ERROR,
          String.format("Screenshot error: %s", exception.getMessage()));
      return false;
    }
  }

  /**
   * To capture a screenshot during execution.
   *
   * @param appiumDriver             The Appium Driver
   * @param testObject               The Test Object to associate the screenshot.
   * @param directory                The directory file path
   * @param fileNameWithoutExtension File Name Without Extension
   * @return Path to Screenshot.
   */
  public static String captureScreenshot(AppiumDriver<WebElement> appiumDriver,
      AppiumTestObject testObject, String directory, String fileNameWithoutExtension) {
    File tempFile = appiumDriver.getScreenshotAs(OutputType.FILE);
    String path = Paths.get(directory, fileNameWithoutExtension + ".png").normalize().toString();

    // Make sure the directory exists
    try {
      Path tempPath = new File(directory).toPath();
      if (!tempPath.toFile().isDirectory()) {
        Files.createDirectories(tempPath);
      }
    } catch (IOException exception) {
      testObject.getLog()
          .logMessage(MessageType.ERROR, "Failed to create directories: " + exception.getMessage());
    }

    // Try to copy the temporary file to desired file path
    try {
      Files.copy(tempFile.toPath(), new File(path).toPath(), StandardCopyOption.COPY_ATTRIBUTES);
    } catch (IOException exception) {
      testObject.getLog().logMessage(MessageType.ERROR,
          String.format("Screenshot error: %s", exception.getMessage()));
    }

    return path;
  }

  /**
   * To capture a page source during execution.
   * Default parameter appendName of empty string.
   *
   * @param appiumDriver The Appium Driver
   * @param testObject   The Appium Test Object
   * @return True if saving page source is successful, otherwise false
   */
  public static boolean savePageSource(AppiumDriver<WebElement> appiumDriver,
      AppiumTestObject testObject) {
    return savePageSource(appiumDriver, testObject, "");
  }

  /**
   * To capture a page source during execution.
   *
   * @param appiumDriver The Appium Driver
   * @param testObject   The Appium Test Object
   * @param appendName   Appends a name to the end of a filename
   * @return True if saving page source is successful, otherwise false
   */
  public static boolean savePageSource(AppiumDriver<WebElement> appiumDriver,
      AppiumTestObject testObject, String appendName) {
    try {
      String path = "";

      // Check if we are using a file logger.
      if (!(testObject.getLog() instanceof FileLogger)) {
        // Since this is not a file logger we will need to use a generic file name
        path = savePageSource(appiumDriver, testObject, LoggingConfig.getLogDirectory(),
            "PageSource" + appendName);
      } else {
        // Calculate the file name
        String directory = ((FileLogger) testObject.getLog()).getDirectory();
        String fileNameWithoutExtension =
            testObject.getFullyQualifiedTestName() + "_PS" + appendName;

        path = savePageSource(appiumDriver, testObject, directory, fileNameWithoutExtension);
      }

      testObject.getLog().logMessage(MessageType.INFORMATION, "Page Source saved: " + path);
      return true;
    } catch (Exception exception) {
      testObject.getLog().logMessage(MessageType.ERROR,
          String.format("Page Source error: %s", exception.getMessage()));
      return false;
    }
  }

  /**
   * To capture Page Source during execution.
   *
   * @param appiumDriver             The Appium Driver
   * @param testObject               The Appium Test Object
   * @param directory                The directory file path
   * @param fileNameWithoutExtension File Name Without Extension
   * @return Path to the log file
   */
  public static String savePageSource(AppiumDriver<WebElement> appiumDriver,
      AppiumTestObject testObject, String directory, String fileNameWithoutExtension) {
    // Save the current page source into a string
    String pageSource = appiumDriver.getPageSource();

    // Make sure the directory exists
    try {
      Path path = new File(directory).toPath();
      if (!path.toFile().isDirectory()) {
        Files.createDirectories(path);
      }
    } catch (IOException exception) {
      testObject.getLog()
          .logMessage(MessageType.ERROR, "Failed to create directories: " + exception.getMessage());
    }

    // Calculate the file name
    String path = Paths.get(directory, fileNameWithoutExtension + ".txt").normalize().toString();

    try (FileWriter writer = new FileWriter(path, false)) {
      writer.write(pageSource);

      // Flush File Writer
      writer.flush();
    } catch (IOException exception) {
      testObject.getLog().logMessage(MessageType.ERROR, "Failed to start File Writer.");
    }

    testObject.addAssociatedFile(path);
    return path;
  }

  /**
   * Make sure the driver is shut down.
   *
   * @param appiumDriver The Appium Driver
   */
  public static void killDriver(AppiumDriver<WebElement> appiumDriver) {
    try {
      appiumDriver.quit();
    } finally {
      appiumDriver.close();
    }
  }
}
