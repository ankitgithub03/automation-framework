package api.testng;

import java.lang.reflect.Method;
import org.testng.ITestContext;
import org.testng.ITestResult;
import test.TestNgMethods;

public class ApiTesNgMethods extends TestNgMethods {

  @Override
  public void initializeTest(Method method, Object[] args, ITestContext context) throws Exception {

  }

  @Override
  public void tearDownTest(ITestResult iTestResult, Object[] args, ITestContext iTestContext)
      throws Exception {

  }

  @Override
  public Object[][] dataProviderMethod() {
    return new Object[0][];
  }
}
