package ui.driverUtils;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.appmanagement.ApplicationState;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import java.io.File;
import java.util.HashMap;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.safari.SafariDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.DriverFactory;
import ui.app.android.AndroidUtility;
import utils.JavaWrappers;
import utils.OSValidator;


public class Drivers {

  private static Logger log = LoggerFactory.getLogger(Drivers.class);

  public String os = System.getProperty("os.name").toLowerCase();
  AndroidDriver androidDriver = null;
  IOSDriver iosDriver = null;
  AppiumDriver appiumDriver = null;
  WebDriver webDriver = null;
  ChromeDriver chromeDriver = null;
  FirefoxDriver firefoxDriver = null;
  SafariDriver safariDriver = null;
  InternetExplorerDriver internetExplorerDriver = null;
  public DesiredCapabilities desiredCapabilities = null;

  private static InheritableThreadLocal<WebDriver> driver = new InheritableThreadLocal<WebDriver>() {
    @Override
    protected WebDriver initialValue() {
      return null;
    }
  };
  private static InheritableThreadLocal<HashMap<Integer,AppiumDriverLocalService>> appiumServerService = new InheritableThreadLocal<HashMap<Integer,AppiumDriverLocalService>>() {
    @Override
    protected HashMap<Integer,AppiumDriverLocalService> initialValue() {
      return new HashMap<>();
    }
  };
  private static InheritableThreadLocal<String> udid = new InheritableThreadLocal<String>() {
    @Override
    protected String initialValue() {
      return "";
    }
  };

  private static InheritableThreadLocal<HashMap<String, Object>> sObjects = new InheritableThreadLocal<HashMap<String, Object>>() {
    @Override
    public HashMap<String, Object> initialValue() {
      return new HashMap<String, Object>();
    }
  };



  public static void setWebDriver(WebDriver webDriver){
    driver.set(webDriver);
  }

  public static WebDriver getWebDriver(){
    return driver.get();
  }

  public static AppiumDriver<MobileElement> getMobileDriver(){
    return (AppiumDriver<MobileElement>)getWebDriver();
  }

  public static AndroidDriver getAndroidDriver(){
    return ((AndroidDriver)getWebDriver());
  }

  public static void setAppiumServerService(Integer port, AppiumDriverLocalService service){
    appiumServerService.get().put(port, service);
  }

  public static AppiumDriverLocalService getAppiumServerService(Integer port){
    return appiumServerService.get().get(port);
  }

  public static String getUdid(){
    return udid.get();
  }

  public static void setUdid(String deviceId){
    udid.set(deviceId);
  }

  public void generateScreenshots(){
    Thread _thread = new Thread(() -> {
      int index = 1;
      while(Drivers.getWebDriver() != null){
        String screenshotPath = DriverFactory.getTestReporting().getGifFolder()+ File.separator+"screenshots_"+(index++)+".gif";
        new AndroidUtility().captureScreenshot(DriverFactory.getTestDetails("udid"),screenshotPath);
      }
    });
    DriverFactory.setGifThread(_thread);
    _thread.start();
  }


  public void waitForAppToBeInForeground(WebDriver driver, String appPackage) {
    ApplicationState state;
    int counter = 10;
    do {
      System.out.println("Waiting for app to be in foreground");
      JavaWrappers.sleep(1/2);
      state = ((AppiumDriver<?>)driver).queryAppState(appPackage);
      counter--;
    } while(!state.equals(ApplicationState.RUNNING_IN_FOREGROUND) && counter > 0);
  }



}
