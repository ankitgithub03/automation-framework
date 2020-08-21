package ui.app.testng;

import java.lang.reflect.Method;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import test.DriverFactory;
import test.TestNgMethods;
import ui.app.android.AndroidUtility;
import ui.app.android.Androiddriver;
import ui.driverUtils.AppiumServer;
import utils.JavaWrappers;

public class AppTesNgMethods extends TestNgMethods {

  @Override
  @BeforeMethod(alwaysRun = true)
  public synchronized void initializeTest(Method method,Object[] args, ITestContext iTestContext) throws Exception {
      String deviceID = getAvailableDeviceID();
      setTestCaseVariables(args);
      String attributes = DriverFactory.getTestDetails("attributes");
      DriverFactory.setTestDetails("udid", deviceID);
      DriverFactory.setTestDetails("deviceName", ""+DriverFactory.deviceDetails.get(deviceID + "_deviceName").trim());
      DriverFactory.setTestDetails("deviceOsVersion", DriverFactory.deviceDetails.get(deviceID + "_deviceOsVersion"));
      boolean autoGrantPermissions = true;
      boolean uninstall = false;
      boolean noReset = false;
      initializeReport();
      if (DriverFactory.environment.get("driverType").equalsIgnoreCase("android")
          || DriverFactory.environment.get("driverType").equalsIgnoreCase("ios")) {
        if (attributes.trim().contains("autoGrantPermissions=")) {
          autoGrantPermissions = Boolean
              .parseBoolean(getFlagValueFromTestName(attributes, "autoGrantPermissions"));
        }
        if (attributes.trim().contains("uninstall=")) {
          uninstall = Boolean.parseBoolean(getFlagValueFromTestName(attributes, "uninstall"));
        }
        if (attributes.trim().contains("noReset=")) {
          noReset = Boolean.parseBoolean(getFlagValueFromTestName(attributes, "noReset"));
        }
        new AppiumServer().startAppiumServer();
        if (DriverFactory.environment.get("driverType").equalsIgnoreCase("android")) {
          if (uninstall) {
            new AndroidUtility()
                .removeAppIfInstall(deviceID, DriverFactory.environment.get("packageName"));
          }
          new Androiddriver().createAndroidDriver(deviceID, noReset, autoGrantPermissions);
        } else {
          // iOS code for
        }
      }
      DriverFactory.setTestDetails("startTime", "" + System.currentTimeMillis());
  }

  @Override
  @AfterMethod(alwaysRun = true)
  public synchronized void tearDownTest(ITestResult iTestResult, Object[] args, ITestContext iTestContext){
    saveResultsInReport(iTestResult);
    addDeviceID(DriverFactory.getTestDetails("udid"));
    quitDriverAndResetData();
  }


  private synchronized String getAvailableDeviceID() {
    int counter = 0;
    while (DriverFactory.availableDevices.size() <= 0) {
      JavaWrappers.sleep(60);   // wait 1min if device is not available
      System.out.println("Device is not available");
      if (counter > 15) {
        break;
      }
      counter++;
    }
    String udid = DriverFactory.availableDevices.get(0);
    DriverFactory.availableDevices.remove(0);
    if (DriverFactory.availableDevices.size() > 0) {
      JavaWrappers.sleep(5);
    }
    return udid;
  }

  private synchronized void addDeviceID(String udid) {
    if (!DriverFactory.offlineDevices.contains(udid)) {
      DriverFactory.availableDevices.add(udid);
      System.out.println("Device added: " + udid);
      System.out.println(DriverFactory.availableDevices);
    }
    else
      DriverFactory.availableDevices.remove(udid);
  }
}
