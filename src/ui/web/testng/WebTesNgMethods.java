package ui.web.testng;

import java.lang.reflect.Method;
import net.lightbody.bmp.core.har.Har;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import test.DriverFactory;
import test.TestNgMethods;
import ui.driverUtils.Drivers;
import ui.web.browser.Seleniumdriver;

public class WebTesNgMethods extends TestNgMethods {


  @Override
  @BeforeMethod(alwaysRun = true)
  public void initializeTest(Method method, Object[] args, ITestContext context) throws Exception {
    setTestCaseVariables(args);
    initializeReport();
    Seleniumdriver seleniumdriver = new Seleniumdriver();
    if(DriverFactory.environment.get("driverType").equalsIgnoreCase("chrome")){
      seleniumdriver.createChromeDriver();
    }
    DriverFactory.setTestDetails("deviceName","Chrome");
    DriverFactory.setTestDetails("deviceOsVersion",((ChromeDriver)Drivers.getWebDriver()).getCapabilities().getVersion());
    DriverFactory.setTestDetails("startTime", "" + System.currentTimeMillis());
  }

  @Override
  @AfterMethod(alwaysRun = true)
  public void tearDownTest(ITestResult iTestResult, Object[] args, ITestContext iTestContext) {
    saveResultsInReport(iTestResult);
    quitDriverAndResetData();
  }



}
