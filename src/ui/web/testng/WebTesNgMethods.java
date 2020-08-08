package ui.web.testng;

import java.lang.reflect.Method;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.DataProvider;
import test.TestNgMethods;

public class WebTesNgMethods extends TestNgMethods {


  @Override
  public void initializeTest(Method method, Object[] args, ITestContext context) throws Exception {

  }

  @Override
  public void tearDownTest(ITestResult iTestResult, Object[] args, ITestContext iTestContext)
      throws Exception {

  }

  @Override
  @DataProvider(name = "scenarios", parallel = true)
  public Object[][] dataProviderMethod() {


    return new Object[0][];
  }
}
