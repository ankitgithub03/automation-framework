package ui.driverUtils;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import io.appium.java_client.android.nativekey.PressesKey;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
import java.io.File;
import java.time.Duration;
import java.util.List;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import report.custom.TestReporting;
import test.DriverFactory;
import utils.CustomizeAssert;
import utils.ImageComparision;

public class AppActionsUtils extends DriverActionsUtils{

  static Logger log = LoggerFactory.getLogger(AppActionsUtils.class);

  public AppActionsUtils(TestReporting reporting, CustomizeAssert customizeAssert) {
    super(reporting, customizeAssert);
  }


  public void pressAnyButton(AndroidKey key) throws Exception {
    String method = (new Object() {
    }).getClass().getEnclosingMethod().getName() + " on";
    ((PressesKey)Drivers.getAndroidDriver()).pressKey((new KeyEvent()).withKey(key));
    reporting.log(method, "Key Pressed: "+key.name(), "Done");
  }

  public void swipeNTimesDown(int noOfTimes) throws Exception {
    String method = (new Object() {
    }).getClass().getEnclosingMethod().getName() + " on";
    try {
      for(int i = 1; i <= noOfTimes; ++i) {
        Dimension size = Drivers.getWebDriver().manage().window().getSize();
        int startPointx = size.width / 2;
        int startPointy = getPercentValue(size.height, 60);
        int endPointx = size.width / 2;
        int endPointy = getPercentValue(size.height, 20);
        this.swipe(Drivers.getMobileDriver(), startPointx, startPointy, endPointx, endPointy, Duration.ofMillis(2000L));
      }
    } catch (Exception var9) {
      log.error("Exception occurred while scrolling on: " + var9);
      reporting.log(method, "Exception occurred while swiping to"+ var9, "FAIL");
    }
  }

  public boolean swipeNTimesUP(int noOfTimes) throws Exception {
    String method = (new Object() {
    }).getClass().getEnclosingMethod().getName() + " on";
    boolean flag = false;
    try {
      for(int i = 1; i <= noOfTimes; ++i) {
        Dimension size = Drivers.getMobileDriver().manage().window().getSize();
        int startPointx = size.width / 2;
        int startPointy = size.height / 2;
        int endPointx = size.width / 2;
        int endPointy = this.getPercentValue(size.height, 80);
        this.swipe(Drivers.getMobileDriver(), startPointx, startPointy, endPointx, endPointy, Duration.ofMillis(2000L));
      }
      flag = true;
    } catch (Exception var10) {
      log.error("Exception occurred while scrolling on: " + var10);
      DriverFactory.getTestReporting().log(method, "Exception occurred while swiping to "+ var10, "FAIL");
    }
    return flag;
  }

  public boolean swipeDownTillElementFound(int numberOfswipes, String elementPath, String... params) throws Exception {
    boolean found = false;
    for(int i = 0; i < numberOfswipes ; ++i) {
      if(verifyPresenceOfElementOpt(elementPath, 1, params)){
        found = true;
      }
      this.swipeNTimesDown(1);
    }
    return found;
  }

  public boolean swipeUPTillElementFound(int numberOfSwipes, String elementPath, String... params)
      throws Exception {
    boolean found = false;
    for(int i = 0; i < numberOfSwipes; ++i) {
      if(verifyPresenceOfElementOpt(elementPath, 1, params)){
        found = true;
      }
      this.swipeNTimesUP(1);
    }
    return found;
  }

  public void swipeToElement(String upOrDown, int numberOfSwipes, String elementPath, String... params) throws Exception {
    if (upOrDown.contains("up")) {
      if (!this.swipeUPTillElementFound(numberOfSwipes, elementPath, params)) {
        this.swipeElementToMiddle(elementPath, params);
      } else {
        this.swipeElementToMiddle(elementPath, params);
      }
    } else if (!this.swipeDownTillElementFound(numberOfSwipes, elementPath, params)) {
      this.swipeElementToMiddle(elementPath, params);
    } else {
      this.swipeElementToMiddle(elementPath, params);
    }
  }

  public void swipeElementToMiddle(String elementPath, String... params) throws Exception {
    String method = (new Object() {
    }).getClass().getEnclosingMethod().getName() + " on";
    MobileElement we = (MobileElement)getElement(elementPath, 3, params);
    if (we != null) {
      try {
        Dimension size = Drivers.getMobileDriver().manage().window().getSize();
        int halfHeight = size.height / 2;
        int anchor = we.getCenter().getX();
        int startPoint = we.getCenter().getY();
        this.swipe(Drivers.getMobileDriver(), anchor, startPoint, anchor, halfHeight, Duration.ofMillis(2000L));
      } catch (Exception var10) {
        log.error("Exception occurred while clicking on: " + elementPath + " " + var10);
        DriverFactory.getTestReporting().log(method, "Exception occurred while swiping to "+elementPath, "FAIL");
        throw new Exception("Exception occurred while swiping to" + elementPath + " " + this.getAllString(params));
      }
    } else {
      DriverFactory.getTestReporting().log(method, "Exception occurred while swiping to "+elementPath, "FAIL");
      throw new Exception("Exception occurred while swiping to" + elementPath + " " + this.getAllString(params));
    }
  }

  public void swipe(AppiumDriver<?> appium, int startx, int starty, int endx, int endy, Duration duration) {
    (new TouchAction(appium)).press(PointOption.point(startx, starty)).waitAction(WaitOptions.waitOptions(duration)).moveTo(PointOption.point(endx, endy)).release().perform();
  }

  public int swipeRightTillEnd(String parentViewLocator, String parentViewDynamicValue, int expectedSwipes, String elementPath, String... params) throws Exception {
    int counter = 0;
    MobileElement we = (MobileElement) this.getElement(elementPath, 3, params);
    List<WebElement> wes = this.getElements(elementPath, params);
    int totalWebElements = wes.size();
    if (we != null) {
      try {
        Dimension windowSize = Drivers.getMobileDriver().manage().window().getSize();
        int startX = windowSize.width - we.getCenter().getX();
        int startY = we.getCenter().getY();
        int endX = we.getCenter().getX();
        int endY = we.getCenter().getY();

        do {
          File previousImage = null;
          if (counter >= expectedSwipes) {
            previousImage = this.getScreenshotOfElement(parentViewLocator, parentViewDynamicValue);
          }
          this.swipe(Drivers.getMobileDriver(), startX, startY, endX, endY, Duration.ofMillis(2000L));
          ++counter;
          if (counter > expectedSwipes) {
            File afterImage = this.getScreenshotOfElement(parentViewLocator, parentViewDynamicValue);
            if ((new ImageComparision()).imageComparision(previousImage, afterImage, 2)) {
              break;
            }
          }
          wes = getElements(elementPath, params);
          totalWebElements += wes.size();
        } while(counter < 15);
        return totalWebElements;
      } catch (Exception var17) {
        log.error("Exception occurred while clicking on: " + elementPath + " " + var17);
        throw new Exception("Exception occurred while clicking on: " + elementPath + " " + var17);
      }
    } else {
      throw new Exception("Element: " + elementPath + " isn't present");
    }
  }

  private int getPercentValue(int value, int percent) {
    return (value * percent / 100);
  }

  /**
   * Closing current activity or current app.
   */
  public void closeCurrentApp() throws Exception {
    String method = new Object() {}.getClass().getEnclosingMethod().getName();
    boolean flag = false;
    Drivers.getMobileDriver().closeApp();
    sleep(1);
    reporting.log(method, "Current Application closed successfully", "Pass");
  }


}
