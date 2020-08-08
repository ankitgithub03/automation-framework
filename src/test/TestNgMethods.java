package test;

import cucumber.api.testng.PickleEventWrapper;
import gherkin.pickles.PickleTag;
import java.lang.reflect.Method;
import java.sql.Driver;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestResult;
import report.custom.FeatureReporting;
import report.custom.TestReporting;
import ui.driverUtils.Drivers;

public abstract class TestNgMethods {


  long testCaseStartTime = 0;
  long testSuiteCompleteTime = 0;
  String testStatus = "FAIL";


  public abstract void initializeTest(Method method, Object[] args, ITestContext context)
      throws Exception;

  public abstract void tearDownTest(ITestResult iTestResult, Object[] args, ITestContext iTestContext)
      throws Exception;

  public abstract Object[][] dataProviderMethod();

  public String getPriority(PickleEventWrapper pickleEvent, String defaultSeverity) {
    List<PickleTag> tags = pickleEvent.getPickleEvent().pickle.getTags();
    for(int i = 0 ; i < tags.size(); i++) {
      String tagName = tags.get(i).getName().trim();
      if(tagName.length() == 3 && tagName.startsWith("@P"))
        return tagName.substring(1).toUpperCase();
    }
    return defaultSeverity.trim().toUpperCase();
  }

  public String getFlagValueFromTestName(String testName, String flag) {
    String key = flag + "=";
    if(testName.trim().contains(key)) {
      String text = testName.trim().substring(testName.trim().indexOf(key) + key.length()).trim();
      if(text.trim().matches(".*,[A-Za-z]*=.*")) {
        Pattern pattern = Pattern.compile(",[A-Za-z]*=");
        Matcher matcher = pattern.matcher(text);
        if(matcher.find()) {
          String nextKey = matcher.group(0).trim();
          text = text.substring(0, text.indexOf(nextKey)).trim();
        }
      }
      return text;
    }
    return null;
  }

  public void initializeFeatureReport(String featureName){
    FeatureReporting featureReporting = null;
    if(DriverFactory.getFeatureReport(featureName) == null) {
      featureReporting = new FeatureReporting();
      DriverFactory.setFeatureReport(featureName,featureReporting);
      featureReporting.createFeatureFileHeader(featureName,DriverFactory.environment.get("DriverType"));
    }
    else{
      featureReporting = DriverFactory.getFeatureReport(featureName);
    }
    DriverFactory.setTestDetails("featureReportPath",featureReporting.getFeatureReportFilePath());
  }

  public void initializeTestReport(String featureName,String testCaseName){
    testCaseName = testCaseName.replaceAll(" ", "_");
    TestReporting testReporting = new TestReporting();
    testReporting.initializeTestReport(testCaseName);
    DriverFactory.setTestReporting(testReporting);
    DriverFactory.setTestDetails("testCaseReportPath",testReporting.getTestCaseReportPath());
  }

  public void quitDriverAndResetData(){
    WebDriver driver = Drivers.getWebDriver();
    if (driver != null) {
      try {
        driver.quit();
      } catch (Exception e) {
      }
    }
    Drivers.setWebDriver(null);
    DriverFactory.removeTestDetails();
    DriverFactory.removeTestReporting();
  }

  public String setReportStatus(ITestResult iTestResult) throws Exception {
    String status = "";
    if(iTestResult.getStatus() == ITestResult.SUCCESS){
      status = "Pass";
      DriverFactory.getTestReporting().log("complete report","run successfully","Done");
    }
    else if(iTestResult.getStatus() == ITestResult.SKIP){
      status = "Skip";
      DriverFactory.getTestReporting().log("complete report","test has been skipped","Skip");
    }
    else{
      status = "Fail";
      DriverFactory.getTestReporting().log("complete report","test has been failed","Fail");
    }
    return status;
  }

}
