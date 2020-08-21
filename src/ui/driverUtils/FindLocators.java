package ui.driverUtils;

import io.appium.java_client.MobileBy;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.poi.ss.formula.functions.T;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.DriverFactory;

public class FindLocators {

  static Logger log = LoggerFactory.getLogger(FindLocators.class);
  private Map<String, Method> methods = new HashMap<String, Method>();

  public FindLocators() {
    storeAllAvailableLocatorMethods();
  }

  private void storeAllAvailableLocatorMethods() {
    try {
      Method[] mobByMeths = (MobileBy.class).getMethods();
      Set<Method> methodList = new HashSet<>(Arrays.asList(mobByMeths));
      for (Method byMethod : methodList) {
        String methName = byMethod.toString();
        methName = methName.split("[(]")[0];
        int len = methName.split("[.]").length;
        methName = methName.split("[.]")[len - 1];
        methods.put(methName, byMethod);
      }
    } catch (Exception e) {
      e.printStackTrace();
      e.getMessage();
    }
  }

  /**
   * This method will give you the object of your locator {@link Method} which is available in
   * Appium
   *
   * @param locator from the {@link By} Method
   * @return
   */
  private Method getByMethod(String locator) {
    Method retMethod = null;
    try {
      if (methods.containsKey(locator)) {
        retMethod = methods.get(locator);
      } else if (methods.containsKey(locator.toLowerCase())) {
        retMethod = methods.get(locator);
      } else {
        throw new Exception(
            "Given locator: " + locator + " is not available in Selenium or Appium methods");
      }
    } catch (Exception e) {
      e.printStackTrace();
      e.getMessage();
    }
    return retMethod;
  }

  private FluentWait<WebDriver> getWait(int...time) {
      int waitTime = (time.length > 0 && time[0] > 0) ? time[0] : Integer.parseInt(DriverFactory.environment.get("explicitWait"));
      WebDriver driver = Drivers.getWebDriver();
    FluentWait<WebDriver> wait = new WebDriverWait(driver,waitTime).pollingEvery(Duration.ofMillis(Long.parseLong(DriverFactory.environment.get("pollingWait"))));
    if (DriverFactory.environment.get("projectType").equalsIgnoreCase("web") && (Boolean)((JavascriptExecutor)driver).executeScript("return window.jQuery != undefined")) {
      if ((Boolean) ((JavascriptExecutor) driver).executeScript("return window.jQuery != undefined")) {
        wait.until((ExpectedCondition<Boolean>)wd-> ((JavascriptExecutor) wd).executeScript("return document.readyState").equals("complete"));
      }
    }
    return wait;
  }

  private By getLocator(String path,String...params) throws Exception {
    String method = new Object() {
    }.getClass().getEnclosingMethod().getName();
    By by = null;
    HashMap<String, String> map = new HashMap<>();
    try {
      String page = path.split("/")[0];
      String elementPath = path.split("/")[1];
      map = DriverFactory.locatorsMapValues.get(page).get(elementPath);
      Map.Entry<String,String> entry = map.entrySet().iterator().next();
      String locatorBy = entry.getKey();
      String value = entry.getValue();
      if (!locatorBy.isEmpty() && !value.isEmpty()) {
        value = MessageFormat.format(value, (Object[]) params);
        value = value.trim();
        by = (By) getByMethod(locatorBy).invoke(null, value);
        log.info("locator for : " + path + " is :" + value);
      }
      else{
        throw new Exception("locator is not valid");
      }
    } catch (Exception e) {
      DriverFactory.getTestReporting()
          .log(method, "Given path: " + path + " is not a valid path or isn't in object repository",
              "FAIL");
      log.error("locator path is not correct ", e);
    }
    return by;
  }

  private WebElement clickableElement(String path, int waitTime, String... params)
      throws Exception {
    String method = new Object() {
    }.getClass().getEnclosingMethod().getName();
    WebElement we = getElement(path, waitTime, params);
    try {
      By by = getLocator(path,params);
      getClickableElement(by);
    } catch (Exception e) {
      DriverFactory.getTestReporting()
          .log(method, "Driver Exception: " + e.getMessage(), "Warn");
      System.out.println("Selenium Exception: " + e.getMessage());
    }
    return we;
  }

  private WebElement visibleElement(String path, int waitTime, String... params)
      throws Exception {
    String method = new Object() {
    }.getClass().getEnclosingMethod().getName();
    WebElement we = getElement(path, waitTime, params);
    try {
      By by = getLocator(path,params);
      Wait wait = getWait(waitTime);
      we = (WebElement) wait.until(ExpectedConditions.visibilityOfElementLocated(by));
    } catch (Exception e) {
      DriverFactory.getTestReporting()
          .log(method, "Driver Exception: " + e.getMessage(), "Warn");
      System.out.println("Selenium Exception: " + e.getMessage());
    }
    return we;
  }

  private WebElement element(String path, int time, String... params) throws Exception {
    String method = new Object() {
    }.getClass().getEnclosingMethod().getName();
    WebElement we = null;
    try {
      By by = getLocator(path, params);
      we = presentOfElement(by, time);
    } catch (Exception e) {
//      DriverFactory.getTestReporting()
//          .log(method, "Driver Exception: " + e.getMessage(), "Warn");
      log.info("Selenium Exception: " + e.getMessage());
    }
    return we;
  }

  public List<WebElement> getVisibilityOfAllElements(By by, int ...time){
    List<WebElement> we = new ArrayList<WebElement>();
    Wait<WebDriver> wait = getWait(time);
    we = (List<WebElement>) wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(by));
    wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(by, 0));
    return we ;
  }

  public List<WebElement> getElements(String path, String...params) throws Exception {
    String method = new Object() {
    }.getClass().getEnclosingMethod().getName();
    List<WebElement> we = new ArrayList<WebElement>();
    try {
      By by = getLocator(path, params);
      we = getVisibilityOfAllElements(by);
    } catch (Exception e) {
      DriverFactory.getTestReporting()
          .log(method, "Driver Exception: " + e.getMessage(), "Warn");
      log.info("Selenium Exception: " + e.getMessage());
    }
    return we;
  }


  public WebElement getElementWhenClickable(String path, int waitTime, String... params)
      throws Exception {
    return clickableElement(path, waitTime, params);
  }

  public WebElement getElementWhenClickable(String path, String... params)
      throws Exception {
    return clickableElement(path, 0, params);
  }


  public WebElement getElementWhenVisible(String path, String... params)
      throws Exception {
    return visibleElement(path, 0, params);
  }

  public WebElement getElement(String path, String... params) throws Exception {
    return element(path, 0, params);
  }

  public WebElement getElement(String path, int waitTime, String... params) throws Exception {
    return element(path, waitTime, params);
  }

  public WebElement presentOfElement(By by, int...time){
    Wait<WebDriver> wait = getWait(time);
    WebElement we = (WebElement) wait.until(ExpectedConditions.presenceOfElementLocated(by));
    wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(by, 0));
    return we ;
  }

  public WebElement getClickableElement(By by, int...time){
    Wait<WebDriver> wait = getWait(time);
    WebElement we = (WebElement) wait.until(ExpectedConditions.elementToBeClickable(by));
    wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(by, 0));
    return we ;
  }

  public String getAllString(String...params){
    String text ="";
    for(int i =0; i <params.length; i++){
      text = text+","+params[i];
    }
    return text;
  }


}
