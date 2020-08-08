package test;

import org.testng.ITestContext;

public interface TestNgFeature {

  void initializeFeature(String driverType, ITestContext context);

  void tearDownFeature();

}
