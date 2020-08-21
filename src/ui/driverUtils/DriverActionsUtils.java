package ui.driverUtils;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import report.custom.TestReporting;
import test.DriverFactory;
import utils.CustomizeAssert;
import utils.HashMapNew;

public class DriverActionsUtils extends FindLocators {

  static Logger log = LoggerFactory.getLogger(DriverActionsUtils.class);
  protected TestReporting reporting;
  WebDriver driver;
  protected CustomizeAssert Assert;
  protected HashMapNew sTest;

  public DriverActionsUtils(TestReporting reporting,CustomizeAssert customizeAssert){
    this.reporting = DriverFactory.getTestReporting();
    this.driver = Drivers.getWebDriver();
    this.Assert = DriverFactory.getAssert();
    this.sTest = DriverFactory.getWholeTestDetails();
  }




  /**
   * Click on the passed element
   *
   * @param elementPath "PageName/LocatorName"
   * @param params
   * @return
   * @throws Exception
   */
  public void click(String elementPath, String actionName, String... params) throws Exception {
    WebElement we = getElementWhenClickable(elementPath, params);
    try {
      we.click();
      reporting.log(actionName, elementPath + " " + getAllString(params), "PASS");
    } catch (StaleElementReferenceException s) {
      javascriptClick(we,actionName);
    } catch (Exception e) {
      reporting.log("Unable to click on locator :", elementPath + " " + getAllString(params), "FAIL");
      throw new Exception(e.getMessage());
    }
  }

  /**
   * Click on the passed element
   *
   * @param locator By locator
   * @return
   * @throws Exception
   */
  public void click(By locator, String actionName) throws Exception {
    WebElement we = getClickableElement(locator);
    try {
      we.click();
      DriverFactory.getTestReporting()
          .log(actionName, locator.toString(), "PASS");
    } catch (StaleElementReferenceException s) {
      javascriptClick(we,actionName);
    } catch (Exception e) {
      reporting.log("Unable to click on locator :", locator.toString(), "FAIL");
      throw new Exception(e.getMessage());
    }
  }

  /**
   * Click on the webElement using javascript
   *
   * @param webElement
   * @param actionName
   * @return
   */
  public void javascriptClick(WebElement webElement, String actionName) throws Exception {
    try {
      ((JavascriptExecutor) Drivers.getWebDriver()).executeScript("return arguments[0].click()", webElement);
      reporting.log(actionName, "clicked using javascript", "PASS");
    } catch (WebDriverException we) {
      reporting.log("Unable to click using javascript :", "<b>" + we.getMessage() + "</b>", "FAIL");
    }
  }


  /**
   * type text in the pass locator
   *
   * @param elementPath "PageName/LocatorName"
   * @param typeText  - type text
   * @param params
   * @return
   * @throws Exception
   */
  public void sendKeys(String elementPath, String typeText, String... params) throws Exception {
    WebElement we = getElementWhenClickable(elementPath, params);
    try {
      we.click();
      we.clear();
      if(!we.getText().isEmpty()){
        we.click();
        we.clear();
      }
      we.sendKeys(typeText);
      reporting
          .log("Enter text "+typeText, elementPath + " " + getAllString(params), "PASS");
    } catch (Exception e) {
      System.out.println("Unable to get the Element of: " + elementPath);
      reporting
          .log("Unable to enter text on locator :", elementPath + " " + getAllString(params), "FAIL");
      throw new Exception(e.getMessage());
    }
  }

  public boolean verifyPresenceOfElement(String elementPath, String...params) throws Exception{
    String method = new Object(){}.getClass().getEnclosingMethod().getName();
    boolean flag = false;
    WebElement we = getElement(elementPath, params);
    try{
        if (we !=null && we.isDisplayed()){
          flag = true;
          DriverFactory.getTestReporting()
              .log(method,elementPath+" "+getAllString(params)+" is present", "Done");
        }
    }catch(Exception e){
      DriverFactory.getTestReporting()
          .log(method,elementPath+" "+getAllString(params)+" is not present", "FAIL");
      throw new Exception(elementPath+" is not present");
    }
    return flag;
  }

  /**
   * This method will verify the presence of passed Element and does not throw exception if not present
   * @author Ankit
   * @param elementPath
   * @return
   * @throws Exception
   */
  public boolean verifyPresenceOfElementOpt(String elementPath,int time, String...params) throws Exception{
    String method = new Object(){}.getClass().getEnclosingMethod().getName();
    WebElement we = getElement(elementPath,time, params);
    boolean flag = false;
    try{
      if (we !=null && we.isDisplayed()){
        if(we.isEnabled()){
          flag = true;
          DriverFactory.getTestReporting()
              .log(method,elementPath+" "+getAllString(params)+" is present", "Done");
        }
        else {
          throw new Exception("Not Enable");
        }
      }
      else {
        throw new Exception("Not Displayed");
      }
    }catch(Exception e){
      log.info("Element is not present");
      reporting
          .log(method,elementPath+" "+getAllString(params)+" is not present", "Done");
      flag = false;
    }
    return flag;
  }

  /**
   * This method will verify the non presence of Element
   * @author Ankit
   * @param elementPath
   * @return
   * @throws Exception
   */
  public boolean verifyNonPresenceOfElement(String elementPath, int time, String...params) throws Exception{
    String method = new Object(){}.getClass().getEnclosingMethod().getName();
    WebElement we = getElement(elementPath, time,params);
    boolean flag = false;
    try{
      if (we !=null && we.isDisplayed()){
        if(we.isEnabled()){
          flag = true;
        }
      }else{
        DriverFactory.getTestReporting()
            .log(method,elementPath+" "+getAllString(params)+" is not present", "Done");
      }
    }catch(Exception e){
      DriverFactory.getTestReporting()
          .log(method,elementPath+" "+getAllString(params)+" is not present", "Done");
      flag = false;
    }
    if (flag){
      DriverFactory.getTestReporting()
          .log(method,elementPath+" "+getAllString(params)+" is present", "FAIL");
    }
    return flag;
  }


  /**
   * This method will return the value passed attribute value of passed element
   * @param elementPath
   * @param attribute
   * @param params
   * @return
   * @throws Exception
   */
  public String getAttribute(String elementPath, String attribute, String... params) throws Exception {
    String method = new Object(){}.getClass().getEnclosingMethod().getName();
    String value = "";
    WebElement we = getElementWhenVisible(elementPath,params);
    if(!we.getAttribute(attribute).isEmpty()) {
      value = we.getAttribute(attribute);
      DriverFactory.getTestReporting()
          .log(attribute+" value is" + value,
              " on" + elementPath + " "+getAllString(params),"Done");
    }else{
      DriverFactory.getTestReporting()
          .log("verifyPresenceOfElement before "+method,elementPath+" "+getAllString(params), "FAIL");
      throw new Exception("Element is present but not dispaly: "+elementPath);
    }
    return value;
  }


  /**
   * Return the visible text of the given element
   * @author Ankit
   */
  public String getText(String elementPath, String... params) throws Exception {
    String method = new Object(){}.getClass().getEnclosingMethod().getName();
    String text = "";
    try{
      WebElement we = getElementWhenVisible(elementPath, params);
      try{
        text = we.getText();
        if(text.isEmpty()){
          text = we.getAttribute("text");
        }
        reporting
            .log(method+" from: "+elementPath+ " "+getAllString(params),text, "Done");
        log.info(text+" from: "+elementPath+ " "+getAllString(params));
      }catch(Exception e){
        log.info("Exception occurred while getting text from: "+elementPath+" "+e.getMessage());
        reporting
            .log(method,"Exception: "+e.getMessage()+" for "+elementPath+ " "+getAllString(params), "FAIL");
        throw new Exception("Exception occurred while getting text from: "+elementPath+" "+e.getMessage());
      }
    }catch(Exception e) {
      log.info("Unable to get the Element from: "+elementPath);
      reporting
          .log(method+" : Unable to get the elements :",elementPath+" "+getAllString(params), "FAIL");
    }
    return text;
  }

  public List<String> getAllElementsText(String elementPath, String... params) throws Exception {
    String method = new Object(){}.getClass().getEnclosingMethod().getName();
    String text = "";
    List<String> allText = new ArrayList<String>();
    try{
      List<WebElement> wes = getElements(elementPath, params);
      try{
        for(WebElement we : wes ){
          text = we.getText();
          if(text.isEmpty()){
            text = we.getAttribute("text");
          }
          if (text == null){
            reporting
                .log(method,"Text is : "+ null +" for "+elementPath+" "+getAllString(params), "WARRING");
          }
          else{
            reporting
                .log(method, "<b>"+text+"</b> from: "+elementPath+ " "+getAllString(params), "Done");
            log.info(text);
            allText.add(text);
          }
        }
      }catch(Exception e){
        System.out.println("Exception occurred while getting text from: "+elementPath+ ""+getAllString(params)+" "+e.getMessage());
        reporting
            .log(method+" :Exception occurred while getting text from: ", elementPath+" "+getAllString(params), "FAIL");
        throw new Exception("Exception occurred while getting text of: "+elementPath+" "+e.getMessage());
      }

    }catch(Exception e) {
      System.out.println("Unable to get the elements of: "+elementPath);
      reporting
          .log(method+" :Unable to get the elements : ",elementPath+" "+getAllString(params), "FAIL");
      throw new Exception(method+" Unable to get the elements :"+elementPath+e.getMessage());
    }
    return allText;
  }




  public void verifyText(String expectedText, String sameOrContains,String elementPath,String... params) throws Exception {
    String method = new Object(){}.getClass().getEnclosingMethod().getName()+" on ";
    sameOrContains = sameOrContains.toLowerCase();
    String actual = getText(elementPath,params).trim();
    verifyStrings(expectedText, sameOrContains, actual);
  }

  public void verifyStrings(String expectedText, String sameOrContains, String actualText) throws Exception {
    String method = new Object(){}.getClass().getEnclosingMethod().getName()+" on ";
    sameOrContains = sameOrContains.toLowerCase();
    if ( (sameOrContains.equalsIgnoreCase("same") ||  sameOrContains.contains("same")) && actualText.equalsIgnoreCase(expectedText)){
      reporting
          .log("Expected text: "+expectedText,"Actual text : <b>"+actualText+"</b> are same", "Done");
    }
    else if ( (sameOrContains.equalsIgnoreCase("contains") ||  sameOrContains.contains("contain")) && actualText.contains(expectedText)){
      reporting
          .log("Actual Text: "+actualText+" contains","Expected text: "+expectedText, "Done");
    }
    else{
      reporting
          .log("Actual text : "+actualText+" is not "+sameOrContains," Expected text: "+expectedText, "FAIL");
      throw new Exception("Actual Text:"+actualText+" isn't same as expected: "+expectedText);
    }
  }

  public int getSize(String elementPath,String... params) throws Exception {
    String method = new Object(){}.getClass().getEnclosingMethod().getName();
    List<WebElement> wes = getElements(elementPath, params);
    int size = wes.size();
    reporting
        .log(method, size+" Total element present on: "+elementPath+ " "+getAllString(params), "Done");
    System.out.println("size: "+size);
    return size;
  }

  public void sizeValidation(String elementPath, int expectedSize, String moreOrEqual, String... params) throws Exception {
    String method = new Object(){}.getClass().getEnclosingMethod().getName();
    int size = getSize(elementPath, params);
    if(moreOrEqual.equalsIgnoreCase("more")) {
      if (size > expectedSize)
        reporting
            .log(method, size+" Total elements present on: "+elementPath+ " "+getAllString(params), "PASS");
      else{
        reporting
            .log(method, size+" Total elements present on: "+elementPath+ " "+getAllString(params)+" expected is more : "+expectedSize, "FAIL");
      }
    }
    else if(moreOrEqual.equalsIgnoreCase("equal")) {
      if (size == expectedSize)
        reporting
            .log(method, size+" Total elements present on: "+elementPath+ " "+getAllString(params), "PASS");
      else{
        reporting
            .log(method, size+" Total elements present on: "+elementPath+ " "+getAllString(params)+" expected is: "+expectedSize, "FAIL");
      }
    }
  }


  public static File getScreenShot(){
    TakesScreenshot tss = (TakesScreenshot) Drivers.getWebDriver();
    String path = DriverFactory.getFeatureReport(DriverFactory.getTestDetails("featureName")).getFeatureReportFolder()+File.separator+"TestCases"+File.separator+"ScreenShot";
    File newImageFile = null;
    try {
      File sourcePath = tss.getScreenshotAs(OutputType.FILE);
      String fileName = "Image_"+System.currentTimeMillis();
      newImageFile = new File(path+File.separator+fileName+".jpg");
      FileUtils.moveFile(sourcePath, newImageFile);
    }catch(org.openqa.selenium.TimeoutException e){
      log.error("Page timeout Exception occurred",e);
      newImageFile = new File(path+File.separator+"UnableToCapture"+".jpg");
    }catch (IOException e) {
      log.error("Error occurred while taking screen shot "+e.getMessage());
      newImageFile = new File(path+File.separator+"secureFlag"+".jpg");
    }
    catch(Exception e) {
      log.error("Exception occurred");
      newImageFile = new File(path+File.separator+"UnableToCapture"+".jpg");
    }
    return newImageFile;
  }

  public File getScreenshotOfElement(String parentViewLocator, String... params) {
    File newImageFile = null;
    try {
      WebElement we = this.getElement(parentViewLocator, params);
      String logsFolder = DriverFactory.getFeatureReport(DriverFactory.getTestDetails("featureName")).getFeatureReportFolder()+File.separator+"TestCases"+File.separator+"ScreenShot";
      File file = new File(logsFolder);
      File sourcePath = (File)we.getScreenshotAs(OutputType.FILE);
      String fileName = "Image_" + System.currentTimeMillis();
      newImageFile = new File(file.getAbsolutePath() + File.separator + fileName + ".jpg");
      FileUtils.moveFile(sourcePath, newImageFile);
      return newImageFile;
    } catch (TimeoutException var10) {
      log.warn("Page timeout Exception occurred -\n" + var10);
      return null;
    } catch (IOException var11) {
      log.error("Error occurred while taking screen shot " + var11);
      return null;
    } catch (Exception var12) {
      log.error("Exception occurred -\n" + var12);
      return null;
    }
  }

  public void sleep(long seconds)  {
    try {
      Thread.sleep(seconds * 1000);
    }catch (Exception e){
      e.printStackTrace();
      log.info("Threw a Exception in BaseUtil::", e);
    }
  }







}
