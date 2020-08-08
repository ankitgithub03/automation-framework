package ui.app.testng;

import org.testng.ITestContext;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;
import test.TestNgFeature;

public class AppTestNgFeature implements TestNgFeature {

  @Override
  @Parameters({ "browser" })
  @BeforeTest(alwaysRun = true)
  public void initializeFeature(String browser, ITestContext context) {


  }

  @Override
  @AfterTest
  public void tearDownFeature() {

  }
}
