package ui.driverUtils;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import java.util.HashMap;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.safari.SafariDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


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
  private static InheritableThreadLocal<AppiumDriverLocalService> appiumServerService = new InheritableThreadLocal<AppiumDriverLocalService>() {
    @Override
    protected AppiumDriverLocalService initialValue() {
      return null;
    }
  };
  private static InheritableThreadLocal<Integer> serverPort = new InheritableThreadLocal<Integer>() {
    @Override
    protected Integer initialValue() {
      return 0;
    }
  };

  private static InheritableThreadLocal<String> udid = new InheritableThreadLocal<String>() {
    @Override
    protected String initialValue() {
      return "";
    }
  };

  private static InheritableThreadLocal<HashMap<String,HashMap<String, String>>> deviceDetails = new InheritableThreadLocal<HashMap<String,HashMap<String, String>>>();


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

  public static AndroidDriver getAndroidDriver(){
    return ((AndroidDriver)driver.get());
  }

  public static void setServerPort(Integer port){
    serverPort.set(port);
  }

  public static Integer getServerPort(){
    return serverPort.get();
  }

  public static void setAppiumServerService(AppiumDriverLocalService service){
    appiumServerService.set(service);
  }

  public static AppiumDriverLocalService getAppiumServerService(){
    return appiumServerService.get();
  }

  public static void setDeviceName(String deviceID, String deviceName ){
    HashMap<String, String> map = new HashMap<>();
    map.put("deviceName",deviceName);
    HashMap<String, HashMap<String, String>> m = new HashMap<>();
    m.put(deviceID,map);
    deviceDetails.set(m);
  }

  public static String getDeviceName(String deviceID){
    return deviceDetails.get().get(deviceID).get("deviceName");
  }

  public static void setDeviceModel(String deviceID, String deviceModel ){
    HashMap<String, String> map = new HashMap<>();
    map.put("deviceModel",deviceModel);
    HashMap<String, HashMap<String, String>> m = new HashMap<>();
    m.put(deviceID,map);
    deviceDetails.set(m);
  }

  public static String getDeviceModel(String deviceID){
    return deviceDetails.get().get(deviceID).get("deviceModel");
  }

  public static void setAppVersion(String deviceID, String appVersion ){
    HashMap<String, String> map = new HashMap<>();
    map.put("appVersion",appVersion);
    HashMap<String, HashMap<String, String>> m = new HashMap<>();
    m.put(deviceID,map);
    deviceDetails.set(m);
  }

  public static String getAppVersion(String deviceID){
    return deviceDetails.get().get(deviceID).get("appVersion");
  }

  public static void setAppBuildVersion(String deviceID, String appBuildVersion ){
    HashMap<String, String> map = new HashMap<>();
    map.put("appBuildVersion",appBuildVersion);
    HashMap<String, HashMap<String, String>> m = new HashMap<>();
    m.put(deviceID,map);
    deviceDetails.set(m);
  }

  public static String getAppBuildVersion(String deviceID){
    return deviceDetails.get().get(deviceID).get("appBuildVersion");
  }

  public static void setdeviceOsVersion(String deviceID, String deviceOsVersion ){
    HashMap<String, String> map = new HashMap<>();
    map.put("deviceOsVersion",deviceOsVersion);
    HashMap<String, HashMap<String, String>> m = new HashMap<>();
    m.put(deviceID,map);
    deviceDetails.set(m);
  }

  public static String getdeviceOsVersion(String deviceID){
    return deviceDetails.get().get(deviceID).get("deviceOsVersion");
  }

  public static String getUdid(){
    return udid.get();
  }

  public static void setUdid(String deviceId){
    udid.set(deviceId);
  }




}
