package ui.web.testng;

import java.io.IOException;
import org.testng.ITestContext;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import test.DriverFactory;
import test.TestNgSuite;

public class WebTestNgSuite extends TestNgSuite {


  @Override
  @BeforeSuite(alwaysRun = true)
  public void initializeSuite(ITestContext itx) throws IOException {
    setupEnvironmentAndConfig();
    initializeLocatorsFile();
    setupReport();
    int threads = Integer.parseInt(System.getProperty("maxThread") != null && !System.getProperty("maxThread").trim().equalsIgnoreCase("") ? System.getProperty("maxThread").trim() : (""+itx.getCurrentXmlTest().getSuite().getDataProviderThreadCount()).trim());
    itx.getCurrentXmlTest().getSuite().setDataProviderThreadCount(threads);
  }

  @Override
  @AfterSuite(alwaysRun = true)
  public void tearDownSuite() {
    mailReport.createConsolidateReport();
  }
}
