/*
 * Copyright 2019 (C) Magenic, All rights Reserved
 */

package com.magenic.jmaqs.webservices;

import com.magenic.jmaqs.base.BaseTest;

import org.testng.ITestResult;

/**
 * Base web service test class.
 */
public class BaseWebServiceTest extends BaseTest {

  /**
   * Thread local storage of web service test object.
   */
  private ThreadLocal<WebServiceTestObject> webServiceTestObject = new ThreadLocal<WebServiceTestObject>();

  /**
   * Get HttpClientWrapper.
   * 
   * @return WebDriver
   */
  public HttpClientWrapper getHttpClientWrapper() {
    return this.webServiceTestObject.get().webServiceWrapper;
  }

  /**
   * Get the webServiceTestObject for this test.
   * 
   * @return The seleniumTestObject
   */
  public WebServiceTestObject getWebServiceTestObject() {
    return this.webServiceTestObject.get();
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.magenic.jmaqs.utilities.BaseTest.BaseTest#postSetupLogging()
   */
  @Override
  protected void postSetupLogging() throws Exception {

    HttpClientWrapper wrapper = new HttpClientWrapper(WebServiceConfig.getWebServiceUri());
    webServiceTestObject.set(
        new WebServiceTestObject(wrapper, this.getLogger(), this.getFullyQualifiedTestClassName()));
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.magenic.jmaqs.utilities.BaseTest.BaseTest# beforeLoggingTeardown
   * (org.testng.ITestResult)
   */
  @Override
  protected void beforeLoggingTeardown(ITestResult resultType) {
    // There is no before logging tear-down required
  }
}
