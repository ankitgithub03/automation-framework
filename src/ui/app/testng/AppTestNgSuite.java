package ui.app.testng;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import test.DriverFactory;
import test.TestNgSuite;
import ui.app.android.AndroidUtility;
import ui.driverUtils.AppiumServer;
import utils.MachineSearch;
import utils.ReadLocatorsXmlFile;

public class AppTestNgSuite implements TestNgSuite {

  private static Logger log = LoggerFactory.getLogger(AppTestNgSuite.class);

  String finalReportFile = "";
  static int parallelSuitesCounter = 0;


  /**
   * Below are the setup done in Before Suite
   *
   * @param itx
   * 1. get the configuration file and read all values from configurations file and save
   *    into environment variable
   *2. Initialize the report folder structure
   */
  @BeforeSuite
  @Override
  public void initializeSuite(ITestContext itx) throws IOException {
    String configureFile = new MachineSearch().searchMachineForFile(projDir, "Configuration.xml");
    DriverFactory.environment = new ReadLocatorsXmlFile()
        .getXMLNodeValue(configureFile, "configuration");
    DriverFactory.setEnv(DriverFactory.environment.get("environment"));
    finalReportFile = finalReport
        .initializeFinalUIReport(itx.getCurrentXmlTest().getSuite().getName(),
            DriverFactory.environment.get("productName"));
    log.info(finalReportFile);
    if (DriverFactory.environment.get("OSType").equalsIgnoreCase("android")
        || DriverFactory.environment.get("OSType").equalsIgnoreCase("ios")) {

      new AppiumServer().startAppiumServer();
    }

  }

  @Override
  @AfterSuite
  public void tearDownSuite() {
    finalReport.completeFinalReportFileFooter();

    // send mail code
    if (DriverFactory.environment.get("OSType").equalsIgnoreCase("android")
        || DriverFactory.environment.get("OSType").equalsIgnoreCase("ios")) {
      getTotalDeviceConnected();
      reserveDevices();
      new AppiumServer().killAllAppiumServer();
    }

  }

  private void getTotalDeviceConnected() {
    if (DriverFactory.environment.get("OSType").equalsIgnoreCase("android")) {
      AndroidUtility ads = new AndroidUtility();
      DriverFactory.connectedDevices = ads.getConnectedDevices();
    } else {
      // for iOS
    }
  }

  public void reserveDevices() {
    for (int i = 0; i < DriverFactory.connectedDevices.size(); i++) {
      String deviceID = DriverFactory.connectedDevices.get(i);
      if (new AndroidUtility()
          .networkSetUp(DriverFactory.environment.get("networkTYpe"), deviceID)) {
        DriverFactory.availableDevices.add(deviceID);
        String deviceName = new AndroidUtility().getDeviceName(deviceID);
        String androidVersion = new AndroidUtility().getDeviceAndroidVersion(deviceID);
        DriverFactory.deviceDetails.put(deviceID + "_" + "deviceName", deviceName);
        DriverFactory.deviceDetails.put(deviceID + "_" + "androidVersion", androidVersion);
      }
    }
    log.info("Total connected devices are: " + DriverFactory.connectedDevices.size());
    log.info("Total available devices are: " + DriverFactory.availableDevices.size());
    System.setProperty("TestNg_threadCount", "" + DriverFactory.availableDevices.size());
    if (DriverFactory.availableDevices.size() == 0) {
      System.err.println("There is no device available to run the suite");
      System.exit(0);
    }
  }


}
