package test;

import cucumber.api.testng.CucumberFeatureWrapper;
import cucumber.api.testng.PickleEventWrapper;
import cucumber.api.testng.TestNGCucumberRunner;
import gherkin.pickles.PickleTag;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.sql.Driver;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.lightbody.bmp.core.har.Har;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.DataProvider;
import report.custom.FeatureReporting;
import report.custom.ReportsSession;
import report.custom.TestReporting;
import ui.driverUtils.Drivers;
import utils.CustomizeAssert;
import utils.ExcelUtils;
import utils.GifSequenceWriter;
import utils.HashMapNew;
import utils.JavaWrappers;

public abstract class TestNgMethods {

  protected TestNGCucumberRunner testNGCucumberRunner;


  public abstract void initializeTest(Method method,Object[] args, ITestContext context)
      throws Exception;

  public abstract void tearDownTest(ITestResult iTestResult, Object[] args, ITestContext iTestContext)
      throws Exception;

  public void setTestCaseVariables(Object[] args){
    String featureName = String.valueOf(args[1]).replaceAll("^\"|\"$", "").replaceAll(" ","_");
    String testName = String.valueOf(args[3]).replaceAll("^\"|\"$", "");
    String tagName = String.valueOf(args[2]).trim();
    DriverFactory.tags.add(tagName);
    String attributes = String.valueOf(args[args.length-1]);
    DriverFactory.setTestDetails("testCaseName", testName);
    DriverFactory.setTestDetails("featureName", featureName);
    DriverFactory.setTestDetails("tagName", tagName);
    DriverFactory.setTestDetails("attributes", attributes);
    DriverFactory.setTestDetails(DriverFactory.environment);
  }


  public String getPriority(PickleEventWrapper pickleEvent, String defaultSeverity) {
    List<PickleTag> tags = pickleEvent.getPickleEvent().pickle.getTags();
    for(int i = 0 ; i < tags.size(); i++) {
      String tagName = tags.get(i).getName().trim();
      if(tagName.length() == 3 && tagName.startsWith("@P")) {
        if (Integer.parseInt(tagName.replaceAll("@P", "").trim()) > 2) {
          break;
        }
        return tagName.substring(1).toUpperCase();
      }
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

  public void initializeReport() throws IOException {
    initializeFeatureReport(DriverFactory.getTestDetails("featureName"));
    initializeTestReport(DriverFactory.getTestDetails("featureName"),DriverFactory.getTestDetails("testCaseName"));
  }

  private synchronized void initializeFeatureReport(String featureName){
    FeatureReporting featureReporting = null;
    if(DriverFactory.getFeatureReport(featureName) == null) {
      featureReporting = new FeatureReporting();
      DriverFactory.setFeatureReport(featureName,featureReporting);
      featureReporting.createFeatureFileHeader(featureName);
    }
    else{
      featureReporting = DriverFactory.getFeatureReport(featureName);
    }
    DriverFactory.setTestDetails("featureReportPath",featureReporting.getFeatureReportFilePath());
  }

  private void initializeTestReport(String featureName,String testCaseName) throws IOException {
    testCaseName = testCaseName.replaceAll(" ", "_");
    TestReporting testReporting = new TestReporting(DriverFactory.getWholeTestDetails());
    testReporting.initializeTestReport(featureName,testCaseName);
    DriverFactory.setTestReporting(testReporting);
    DriverFactory.setTestDetails("testCaseReportPath",testReporting.getTestCaseReportPath());
    DriverFactory.setAssert(new CustomizeAssert());
  }

  public synchronized void quitDriverAndResetData() {
    WebDriver driver = Drivers.getWebDriver();
    if (driver != null) {
      try {
        driver.quit();
      } catch (Exception e) {
      }
    }
    if (DriverFactory.environment.get("driverType").equalsIgnoreCase("android")
        || DriverFactory.environment.get("driverType").equalsIgnoreCase("ios")) {
      Drivers.getAppiumServerService(Integer.parseInt(DriverFactory.getTestDetails("port").trim())).stop();
    }
    Drivers.setWebDriver(null);
    DriverFactory.removeTestDetails();
    DriverFactory.removeTestReporting();
  }

  public void saveResultsInReport(ITestResult iTestResult){
    try {
    long completeTime = System.currentTimeMillis();
    String time = JavaWrappers.getTime(Long.parseLong(DriverFactory.getTestDetails("startTime")), completeTime);
    String deviceName = DriverFactory.getTestDetails("deviceName");
    String deviceOsVersion = DriverFactory.getTestDetails("deviceOsVersion");
    String name =  deviceOsVersion.isEmpty() ? deviceName :deviceName + "/" + deviceOsVersion;
    String tagName = DriverFactory.getTestDetails("tagName");
    String testCaseName = DriverFactory.getTestDetails("testCaseName");
    String status = setReportStatus(iTestResult);
    DriverFactory.getFeatureReport(DriverFactory.getTestDetails("featureName")).addTestToFeatureFile(name,tagName,
            DriverFactory.getTestDetails("featureName"), testCaseName, status,
            DriverFactory.getTestDetails("testCaseReportPath"), time);
    saveResults(DriverFactory.getTestDetails("featureName"),status);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private String setReportStatus(ITestResult iTestResult) throws Exception {
    String status = "";
    if(DriverFactory.environment.get("videoGifIntegration").equalsIgnoreCase("true")){
      DriverFactory.getGifThread().stop();
      String test =DriverFactory.getTestDetails("testCaseName").replaceAll(" ","_");
      Thread t = new Thread(() -> GifSequenceWriter
          .generateGif(DriverFactory.getTestReporting().getGifFolder(), "video.gif", 500));
      t.start();
      String filePath = "./"+test+"_gif/video.gif";
      DriverFactory.setTestDetails("videoGifPath",filePath);
      DriverFactory.setThread(t);
    }
    if(DriverFactory.environment.get("projectType").equalsIgnoreCase("android") || DriverFactory.environment.get("projectType").equalsIgnoreCase("ios")) {
      Thread t = new Thread(() -> DriverFactory.getTestReporting().addAppCrashLog());
      t.start();
      DriverFactory.setThread(t);
    }
    if(DriverFactory.environment.get("projectType").equalsIgnoreCase("web")) {
      Thread t = new Thread(() -> captureWebProxyLogs());
      t.start();
      DriverFactory.setThread(t);
      JavaWrappers.sleep(3);
    }
    Throwable throwable = iTestResult.getThrowable();
    if(iTestResult.getStatus() == ITestResult.SUCCESS){
      status = "Pass";
      DriverFactory.getTestReporting().log("Completed report","Run successfully","Pass");
      ReportsSession.totalPassTestCases++;
    }
    else if(iTestResult.getStatus() == ITestResult.SKIP){
      status = "Skip";
      DriverFactory.getTestReporting().log("Completed report","Test case has been skipped","Skip");
      ReportsSession.totalSkipTestCases++;
    }
    else{
      status = "Fail";
      try {
        DriverFactory.getTestReporting().log("Completed report", "Test case has been failed", "Fail");
      }catch(Exception ignored){
      }
      ReportsSession.totalFailTestCases++;
    }
    if(throwable != null){
      StackTraceElement[] trace = throwable.getStackTrace();
      DriverFactory.getTestReporting().writeStackTrace(trace);
    }
    ReportsSession.totalTestCases++;
    waitForAllThreadsToComplete();
    DriverFactory.getTestReporting().addLogFileInReport();
    return status;
  }

  public void captureWebProxyLogs(){
    try {
      Har har = DriverFactory.getBrowserMobProxy().getHar();
      DriverFactory.getBrowserMobProxy().stop();
      har.writeTo(new File(DriverFactory.getTestReporting().getBrowserNetworkLogFile()));
      OutputStream out = new FileOutputStream(DriverFactory.getTestReporting().getBrowserNetworkLogFile());
      Writer w = new OutputStreamWriter(out, "UTF-8");
      har.writeTo(w);
      String filePath = "../TestCases/Logs/"+DriverFactory.getTestReporting().getBrowserNetworkLogName();
      DriverFactory.setTestDetails("browserNetworkLogs",filePath);
    }catch(Exception e){
      e.printStackTrace();
    }
  }

  public void saveResults(String featureName,String testStatus){
    DriverFactory.modules.add(featureName);
    HashMapNew map = new HashMapNew();
    map.put("FeatureName",featureName);
    map.put("ModuleLink","./"+DriverFactory.getFeatureReport(featureName).getFeatureReportFilePath());
    map.put("TestCaseName",DriverFactory.getTestDetails("testCaseName"));
    map.put("TestCaseReportPath","./Feature/"+ featureName+"/TestCases/"+DriverFactory.getTestDetails("testCaseReportPath"));
    map.put("Priority",DriverFactory.getTestDetails("tagName"));
    map.put("Status",testStatus.toUpperCase());
    DriverFactory.results.add(map);
  }

  @DataProvider(name = "scenarios", parallel = true)
  public Object[][] dataProviderMethod() {
    testNGCucumberRunner = new TestNGCucumberRunner(this.getClass());
    List<Object[]> _scenarios = new ArrayList<Object[]>();
    Object[][] scenarios = testNGCucumberRunner.provideScenarios();
    PickleEventWrapper pickleEvent = null;
    CucumberFeatureWrapper _cucumberFeatureWrapper = null;
    if (DriverFactory.environment.get("driverType").isEmpty()) {
      return _scenarios.toArray(new Object[][]{});
    }
    HashMap<String, Integer> repeatedScenariosCount = new HashMap<>();
    String exceptions ="";
    for (int i = 0; i < scenarios.length; i++) {
      Object[] scenario = scenarios[i];
      if (scenario[0] instanceof PickleEventWrapper) {
        pickleEvent = (PickleEventWrapper) scenario[0];
        _cucumberFeatureWrapper = (CucumberFeatureWrapper) scenario[1];
        String tagName = getPriority(pickleEvent, DriverFactory.environment
            .get("defaultSeverity"));  // getting the tag name based on P0,P1,P2,P3
        String testName = String.valueOf(pickleEvent).replaceAll("^\"|\"$", "");
        String attributes = "";
        if (testName.trim().contains("dimension=") || testName.trim().contains("scenarioName=")
            || testName.trim().contains("carrier=")
            || testName.trim().contains("noReset=")
            || testName.trim().contains("threadsConfiguration=")
            || testName.trim().contains("uninstall=")) {
          if (testName.trim().contains("||")) {
            String[] obj = testName.trim().split("\\|\\|");
            testName = obj[0].trim();
            attributes = obj[1].trim();
          }
        }
        testName = testName.replaceAll("/", " or ");
        if (repeatedScenariosCount.get(testName) == null) {
          repeatedScenariosCount.put(testName, 0);
        } else {
          repeatedScenariosCount.put(testName, repeatedScenariosCount.get(testName) + 1);
        }
        if (repeatedScenariosCount.get(testName) > 0) {
          testName += "_" + repeatedScenariosCount.get(testName);
        }
        _scenarios.add(new Object[]{pickleEvent, _cucumberFeatureWrapper,tagName,testName,attributes});
      }
      try {
        pickleEvent = (PickleEventWrapper) scenario[0];
        _cucumberFeatureWrapper = (CucumberFeatureWrapper) scenario[1];
      } catch (Exception ex) {
        String msg = ex.getMessage();
        if (ex.getCause() != null) {
          msg += "\n" + ex.getCause().toString();
        }
        StackTraceElement[] trace = ex.getStackTrace();
        for (int m = 0; m < trace.length; m++) {
          msg += "\n" + trace[m].toString();
        }
        exceptions += "\n\n" + msg;
      }
      System.out.println(exceptions);
    }
    System.out.println("Total scenarios count :: " + _scenarios.size());
    return _scenarios.toArray(new Object[][]{});
  }

  private void waitForAllThreadsToComplete(){
    List<Thread> threads = DriverFactory.getAllThreads();
    int counter =0;
    if(threads != null){
      for(int i = 0 ; i < threads.size(); i++) {
        for(Thread _thread = (Thread)threads.get(i); _thread.isAlive(); ++counter) {
          JavaWrappers.sleep(1);
          if(counter >8){
            _thread.stop();
            break;
          }
          counter++;
        }
      }
    }
  }

}
