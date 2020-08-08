package ui.app.android;

import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.remote.MobileCapabilityType;
import java.net.URL;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.DriverFactory;
import ui.driverUtils.Drivers;
import utils.JavaWrappers;

public class Androiddriver extends Drivers {

  private static Logger log = LoggerFactory.getLogger(Androiddriver.class);

  public WebDriver createAndroidDriver(String deviceID, boolean noReset, boolean permission)
      throws Exception {
    WebDriver driver = null;
    try {
      desiredCapabilities = DesiredCapabilities.android();
      String appiumServiceUrl = Drivers.getAppiumServerService().getUrl().toString();
      log.info("Appium service url for driver is " + appiumServiceUrl);
      System.out.println("Appium service url for driver is " + appiumServiceUrl);
      int sysPort = new JavaWrappers().getAvailablePort(8200, 8299);

      desiredCapabilities.setCapability(MobileCapabilityType.NO_RESET, noReset);
      desiredCapabilities.setCapability(MobileCapabilityType.NEW_COMMAND_TIMEOUT,
          DriverFactory.environment
              .get("NEW_COMMAND_TIMEOUT"));  // driver will close if no interaction in 20 min
      desiredCapabilities
          .setCapability(MobileCapabilityType.DEVICE_NAME, Drivers.getDeviceName(deviceID));
      desiredCapabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, "Android");
      desiredCapabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION,
          Drivers.getdeviceOsVersion(deviceID));
      desiredCapabilities.setCapability(MobileCapabilityType.UDID, deviceID);
      desiredCapabilities.setCapability(MobileCapabilityType.TAKES_SCREENSHOT, true);
      desiredCapabilities.setCapability("autoGrantPermissions", permission);
      desiredCapabilities.setCapability(MobileCapabilityType.AUTOMATION_NAME, "UiAutomator2");
      desiredCapabilities.setCapability(MobileCapabilityType.APPLICATION_NAME,
          DriverFactory.environment.get("appName"));
//				capabilities.setCapability("skipDeviceInitialization", true);
//				capabilities.setCapability("skipServerInstallation", true);
//				capabilities.setCapability(CapabilityType.BROWSER_NAME, "");
//				capabilities.setCapability("clearDeviceLogsOnStart", true);
      desiredCapabilities.setCapability("autoWebview", true);
      desiredCapabilities.setCapability("printPageSourceOnFindFailure", true);
      desiredCapabilities.setCapability("appPackage", DriverFactory.environment.get("App_Package"));
      desiredCapabilities
          .setCapability("appActivity", DriverFactory.environment.get("App_Activity"));
      desiredCapabilities.setCapability("systemPort", sysPort);
      desiredCapabilities.setCapability("noSign", true);
      try {
        driver = new AndroidDriver<MobileElement>(new URL(appiumServiceUrl),
            desiredCapabilities);
        System.out.println("Android driver started");
      } catch (Exception e) {
        System.err
            .println("Unable to initiate Android Driver on: " + Drivers.getDeviceName(deviceID));
        e.printStackTrace();
      }
      Drivers.setWebDriver(driver);
    } catch (Exception e) {
      e.printStackTrace();
      throw new Exception("No Device connected " + deviceID);
    }
    return driver;
  }


}
