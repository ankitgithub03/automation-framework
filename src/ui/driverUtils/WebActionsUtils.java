package ui.driverUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import report.custom.TestReporting;
import test.DriverFactory;
import utils.CustomizeAssert;

public class WebActionsUtils extends DriverActionsUtils{

  public WebActionsUtils(TestReporting reporting, CustomizeAssert customizeAssert) {
    super(reporting, customizeAssert);
  }

  public void launchUrl(String strUrl, boolean clearCookie,int...waitAfterLaunch) throws Exception {
    try{
      if (clearCookie) {
        driver.manage().deleteAllCookies();
      }
      driver.get(strUrl);
      reporting.log("Launch: " + strUrl, " is launched successfully", "Pass");
      if(waitAfterLaunch.length >0)
      sleep(waitAfterLaunch[0]);
    } catch (Exception var5) {
      var5.printStackTrace();
      reporting.log("Launch: " + strUrl, "Exception occurred" + var5, "Fail");
      throw new Exception("Unable to launch "+strUrl);
    }
  }

  public void clearHistory(){
    JavascriptExecutor js = (JavascriptExecutor) driver;
    js.executeScript("history.go(0)");
  }

  public void navigateBack(){
    String method = new Object(){}.getClass().getEnclosingMethod().getName();
    reporting.log(method, "Current Window before back", "Done");
    driver.navigate().back();
    sleep(5);
    reporting.log(method, "After back", "PASS");
  }

  /**
   * This method will open the passed url into new tab on the same browser window
   * @author Ankit
   * @param parentWindowHandle
   * @param url
   * @return
   * @throws Exception
   */
  public String openUrlInNewTab(String parentWindowHandle, String url) throws Exception{
    ((JavascriptExecutor)driver).executeScript("window.open()");
    String currentHandle = switchToNextWindow(true,parentWindowHandle);
    launchUrl(url,false);
    return currentHandle;
  }

  public void scrollDown(int...waitAfterDown){
    String method = new Object(){}.getClass().getEnclosingMethod().getName();
    JavascriptExecutor jse = (JavascriptExecutor) driver;
    jse.executeScript("window.scrollBy(0,document.body.scrollHeight || document.documentElement.scrollHeight)", "");
    if(waitAfterDown.length>0)
      sleep(waitAfterDown[0]);
    reporting.log(method, "scroll down", "Pass");
  }

  public void scrollUp(int...waitAfterUp){
    String method = new Object(){}.getClass().getEnclosingMethod().getName();
    JavascriptExecutor jse = (JavascriptExecutor) driver;
    jse.executeScript("window.scrollBy(0,-document.body.scrollHeight || -document.documentElement.scrollHeight)", "");
    if(waitAfterUp.length>0)
      sleep(waitAfterUp[0]);
    reporting.log(method, "scroll Up", "Pass");
  }

  public void scrollingToElementOfAPage(WebElement we) {
    ((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView();", new Object[]{we});
    sleep(1);
  }

  public void scrollingToElementOfAPage(String elementPath,String...params) throws Exception {
    WebElement webElement = this.getElementWhenVisible(elementPath,params);
    ((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView();", new Object[]{webElement});
    sleep(1);
  }

  public void scrollingByCoordinatesOfAPage(int x, int y) {
    ((JavascriptExecutor)driver).executeScript("window.scrollBy(" + x + "," + y + ")", new Object[0]);
    sleep(1);
  }

  public void scrollingToBottomOfAPage() {
    ((JavascriptExecutor)driver).executeScript("window.scrollTo(0, document.body.scrollHeight)", new Object[0]);
    sleep(1);
  }

  public void scrollingToTopOfAPage() {
    ((JavascriptExecutor)driver).executeScript("window.scrollTo(0, 0)", new Object[0]);
    sleep(1);
  }

  public void scrollTillElement(String elementPath,String...params) throws Exception {

    String method = new Object() {
    }.getClass().getEnclosingMethod().getName();
    boolean flag = false;
    try {
      WebElement we = getElement(elementPath, 5, params);
      ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", we);
      sleep(3);
      reporting.log(method, elementPath + " " + getAllString(params), "Pass");
    } catch (Exception e) {
      reporting.log(method,"Exception: " + e.getMessage() + " for " + elementPath + " " + getAllString(params),"Fail");
      throw new Exception("Exception occurred while scrolling: " + elementPath + " " + e.getMessage());
    }
  }

  public String getDataFromBrowserConsole(String command){
    String method = new Object(){}.getClass().getEnclosingMethod().getName();
    Object data = "";
    JavascriptExecutor jse = (JavascriptExecutor)driver;
    try {
      data = (Object) jse.executeScript("return "+command);
    }catch(Exception e) {
      e.printStackTrace();
      reporting.log(method, "Command is: "+command+ "and data is: <b>"+data.toString()+"</b>", "Warn");
    }
    return data.toString();
  }


  /**
   * @author Ankit
   */
  public void moveAndClick(String elementPath,String...params) throws Exception {
    String method = new Object(){}.getClass().getEnclosingMethod().getName()+" on ";
    Actions ac = new Actions(driver);
    int wait = Integer.parseInt(DriverFactory.environment.get("explicitWait"));
    int count =2;
    while(count > 0){
      try {
        WebElement we = getElement(elementPath,wait,params);
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].focus();", we);
        ac.moveToElement(we).click(we).build().perform();
        reporting.log(method, elementPath + " " + getAllString(params), "PASS");
        sleep(1);
      } catch (Exception e) {
        if (count == 1) {
          throw new Exception(elementPath + " " + getAllString(params) + "  not available");
        }
        reporting.log(method, "Exception: " + e.getMessage() + " for " + elementPath + " " + getAllString(params), "WARN");
        }
      count--;
      wait =5;
    }
  }

  /**
   * @author Ankit
   */
  public void moveToElement(String elementPath,String...params) throws Exception {
    String method = new Object() {
    }.getClass().getEnclosingMethod().getName() + " on ";
    Actions ac = new Actions(driver);
    int wait = Integer.parseInt(DriverFactory.environment.get("explicitWait"));
    int counter =2;
    while (counter >0 ) {
      try {
        WebElement we = getElement(elementPath,wait, params);
        moveToElement(we);
        reporting.log(method, elementPath + " " + getAllString(params), "Pass");
      } catch (Exception e) {
        if (counter == 1) {
          throw new Exception(elementPath + " " + getAllString(params) + "  not available");
        }
        reporting.log(method, "Exception: " + e.getMessage() + " for " + elementPath + " " + getAllString(params), "WARN");
      }
      counter--;
      wait =5;
    }
  }

  private void moveToElement(WebElement webElement){
    Actions ac = new Actions(driver);
    JavascriptExecutor js = (JavascriptExecutor) driver;
    js.executeScript("arguments[0].focus();", webElement);
    ac.moveToElement(webElement).build().perform();
  }

  public void moveToWebElementAndOpenAnother(WebElement webElement, String elementPath,String...params) throws Exception {
    String method = new Object(){}.getClass().getEnclosingMethod().getName()+" to";
    Actions ac = new Actions(driver);
    try {
      // adding for Firefox to focus on the action class
      JavascriptExecutor js = (JavascriptExecutor) driver;
      js.executeScript("arguments[0].focus();", webElement);
      ac.moveToElement(webElement).click().clickAndHold().click(webElement).build().perform();
      reporting.log(method+" "+elementPath+ " "+getAllString(params)," 1: Opening dropDown opts", "Pass");
      WebElement we = getElementWhenVisible(elementPath,params);
      if (we!= null){
        js.executeScript("arguments[0].focus();", webElement);
        ac.moveToElement(webElement).clickAndHold(webElement).build().perform();
        we = getElementWhenVisible(elementPath,params);
        we.click();
      }
      else {
        String mouseOverScript = "if(document.createEvent){var evObj = document.createEvent('MouseEvents');"
            + "evObj.initEvent('mouseover',true, false); arguments[0].dispatchEvent(evObj);} "
            + "else if(document.createEventObject) { arguments[0].fireEvent('onmouseover');}";
        reporting.log(method+" "+elementPath+ " "+getAllString(params)," 2: Opening dropDown opts", "PASS");
        js.executeScript(mouseOverScript, webElement);
        we = getElement(elementPath,4,params);
        ac.moveToElement(webElement).click(webElement).click(we).build().perform();
      }
    } catch (Exception e) {
      throw new Exception("Element: " + elementPath + " isn't present");
    }
  }

  public void refreshWindow(int...waitAfterRefresh){
    String method = new Object(){}.getClass().getEnclosingMethod().getName();
    String url = getCurrentUrl();
    reporting.log(method,"Refreshing the current url: "+url, "Done");
    driver.navigate().refresh();
    reporting.log(method,"Refreshed with url: "+url, "PASS");
    sleep(1);
    if(waitAfterRefresh.length >0) {
     sleep(waitAfterRefresh[0]);
    }
  }

  public String getCurrentUrl(){
    return driver.getCurrentUrl();
  }

  public String getTitle(){
    return driver.getTitle();
  }

  public String getParentWindowHandle(){
    return driver.getWindowHandle();
  }

  public void switchToWindow(String handle){
    String method = new Object(){}.getClass().getEnclosingMethod().getName();
    driver.switchTo().window(handle);
    refreshWindow();
    reporting.log(method,"Current window ", "PASS");
  }

  public void switchToIFrame(String elementPath,String...params) throws Exception {
    String method = new Object() {
    }.getClass().getEnclosingMethod().getName() + " on";
    int wait = Integer.parseInt(DriverFactory.environment.get("explicitWait"));
    for (int i = 0; i < 3; i++) {
      try {
        WebElement we = getElement(elementPath,wait, params);
        if (we != null) {
          driver.switchTo().frame(we);
          reporting.log(method, elementPath + " " + getAllString(params), "Pass");
          break;
        }
      } catch (Exception e) {
        reporting.log(method,"Exception: "+e.getMessage()+" for "+elementPath+" "+ getAllString(params),"FAIL");
      }
      wait =4;
    }
  }

  /**
   * Switch to the next window except opened window
   * @author Ankit
   * @throws Exception
   */
  public String switchToNextWindow(boolean failsIfUnableToSwitch, String...parentHandle) throws Exception{
    String method = new Object(){}.getClass().getEnclosingMethod().getName();
    String currentHandle ="";
    List<String> windowHandles = Arrays.asList(parentHandle);
    Set<String> win  = driver.getWindowHandles();
    reporting.log(method,"Total number of window tabs are: "+win.size(), "Pass");
    if(win.size() ==1){
      reporting.log(method,"Can't switch because total number of window tabs are: "+win.size(), "Warn");
      if(failsIfUnableToSwitch){
        throw new Exception("unable to switch to next window");
      }
      return "";
    }
    else {
      for (String handle : win) {
        if (!windowHandles.contains(handle)) {
          driver.switchTo().window(handle);
          currentHandle = handle;
        }
      }
    }
    refreshWindow();
    return currentHandle;
  }

  /**
   * close next window except opened window
   * @author Ankit
   * @throws Exception
   */
  public void closeNextWindow(String parentHandle) {
    String method = new Object() {
    }.getClass().getEnclosingMethod().getName();
    Set<String> win = driver.getWindowHandles();
    if (win.size() == 1) {
      reporting.log(method, "Total number of window tabs are: " + win.size() + " So can't switch","WARN");
      return;
    }
    reporting.log(method, "Total number of window tabs are: " + win.size(), "Pass");
    for (String handle : win) {
      if (!handle.equalsIgnoreCase(parentHandle)) {
        driver.switchTo().window(handle);
        reporting.log(method, "Closing current window ", "Pass");
        driver.close();
        reporting.log(method, "Closed next window, now it parent window is open", "Pass");
      }
    }
    driver.switchTo().window(parentHandle);
  }

  public void closeGivenWindow(String closingHandle, String parentHandle,boolean failsIfUnableToClose) throws Exception{
    String method = new Object(){}.getClass().getEnclosingMethod().getName();
    String currentHandle ="";
    Set<String> win  = driver.getWindowHandles();
    reporting.log(method,"Total number of window tabs are: "+win.size(), "Pass");
    if(win.size() ==1){
      if(failsIfUnableToClose){
        throw new Exception("unable to close to window "+closingHandle);
      }
      return;
    }
    for(String handle : win) {
      if (handle.equalsIgnoreCase(closingHandle)) {
        driver.switchTo().window(handle);
        driver.close();
        reporting.log(method,"Closed next window except parent window ","Pass");
      }
    }
  }

  /**
   * This method will click on the passed location and open that location link in new tab and move
   * controls to new open handles
   *
   * @author Ankit
   * @param webElement
   * @param handles
   * @return
   * @throws Exception
   */
  public String clickAndOpenInNewTab(WebElement webElement, String...handles) {
    Actions ac = new Actions(driver);
    String currentHandle ="";
    String method = new Object(){}.getClass().getEnclosingMethod().getName();
    try {
      ac.keyDown(Keys.CONTROL).click(webElement).keyUp(Keys.CONTROL).build().perform();
      currentHandle = switchToNextWindow(true, handles);
      reporting.log(method, "open in new window", "Pass");
      sleep(5);
    }catch(Exception e){
      reporting.log(method, "Unable to open in new window", "Fail");
    }
    return currentHandle;
  }



}
