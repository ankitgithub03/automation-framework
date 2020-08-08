package ui.driverUtils;

import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.appium.java_client.service.local.flags.GeneralServerFlag;
import org.openqa.selenium.remote.DesiredCapabilities;
import utils.ExecuteCommand;
import utils.JavaWrappers;

public class AppiumServer {


  public void killAllAppiumServer() {
    String command = "killall -9 node";
    new ExecuteCommand().executeCommand(command, 1, true, false, 1);
  }

  /**
   * To initialize all the configuration for appium server
   *
   * @return appium server service {@link AppiumDriverLocalService}
   */
  private AppiumDriverLocalService initilizeAppiumServer(int port) {
    AppiumDriverLocalService service = null;
    try {
      DesiredCapabilities cap = new DesiredCapabilities();
      cap.setCapability("noReset", "true");
      AppiumServiceBuilder builder = new AppiumServiceBuilder();
//		String appiumJSPath = new MachineSearch().serachMachineForFile(System.getProperty("user.home"), "appium.js");

//		AppiumDriverLocalService service  =AppiumDriverLocalService.buildDefaultService();
//		//Build the Appium service
      builder = new AppiumServiceBuilder();

      builder.withIPAddress("127.0.0.1");
//		builder.withIPAddress("0.0.0.0");
      builder.usingPort(port);
      builder.withCapabilities(cap);
      builder.withArgument(GeneralServerFlag.LOG_LEVEL, "warn");
//		builder.usingDriverExecutable(new File("/usr/local/bin/node"));
//		builder.withAppiumJS(new File("/usr/local/lib/node_modules/appium/build/lib/appium.js"));
//		builder.withStartUpTimeOut(60, TimeUnit.SECONDS);
//		builder.withLogFile(ts.getSuiteReportingObject().logsFolder+File.separator+"appiumserverlogs.txt"));  // need to implement later for server log file
//		builder.withLogFile(new File(System.getProperty("user.home")+File.separator+"appiumserverlogs.txt"));
      service = AppiumDriverLocalService.buildService(builder);
    } catch (Exception e) {
      System.err.println("Unable to initialize Appium server on port: "+port);
      e.printStackTrace();
    }
    return service;
  }


  /**
   * It will start the Appium server and save Appium service {@link AppiumDriverLocalService} in
   *
   */
  public void startAppiumServer() {
    int port = new JavaWrappers().getAvailablePort(4999, 4000);
    AppiumDriverLocalService service = initilizeAppiumServer(port);
    Drivers.setAppiumServerService(service);
    Drivers.setServerPort(port);
    service.start();
    JavaWrappers.sleep(2);
  }


}
