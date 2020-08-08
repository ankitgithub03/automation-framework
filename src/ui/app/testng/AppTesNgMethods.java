package ui.app.testng;

import cucumber.api.testng.CucumberFeatureWrapper;
import cucumber.api.testng.PickleEventWrapper;
import cucumber.api.testng.TestNGCucumberRunner;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import test.DriverFactory;
import test.TestNgMethods;
import ui.app.android.AndroidUtility;
import ui.app.android.Androiddriver;
import utils.JavaWrappers;

public class AppTesNgMethods extends TestNgMethods {

  protected TestNGCucumberRunner testNGCucumberRunner;
  private int parallelSuitesCounter = 0;

  @Override
  @BeforeMethod
  public void initializeTest(Method method, Object[] args, ITestContext iTestContext)
      throws Exception {

    String deviceID = getAvailableDeviceID();
//    String driverType = String.valueOf(args[2]);
//    int count = Integer.parseInt(String.valueOf(args[3]));
//    String threadName = String.valueOf(args[6]);
    String testName = method.getName().trim();
    String actionName = method.getName().trim();
    String attributes = String.valueOf(args[7]);
    String featureName = String.valueOf(args[1]).replaceAll("^\"|\"$", "");
    if (args[0] != null) {
      String str = String.valueOf(args[5]).replaceAll("^\"|\"$", "");
      actionName += "_" + str.replaceAll(" ", "_");
      testName = str;
    }
    DriverFactory.setTestDetails("testCaseName", testName);
    DriverFactory.setTestDetails("featureName", featureName);
    DriverFactory.setTestDetails("udid", deviceID);
    DriverFactory
        .setTestDetails("deviceName", DriverFactory.deviceDetails.get(deviceID + "_deviceName"));
    DriverFactory.setTestDetails("androidVersion",
        DriverFactory.deviceDetails.get(deviceID + "androidVersion"));
    boolean autoGrantPermissions = false;
    boolean uninstall = false;
    boolean noReset = false;
    initializeFeatureReport(featureName);
    initializeTestReport(featureName, testName);
    if (DriverFactory.environment.get("DriverType").equalsIgnoreCase("android")
        || DriverFactory.environment.get("DriverType").equalsIgnoreCase("ios")) {
      if (attributes.trim().contains("autoGrantPermissions=")) {
        autoGrantPermissions = Boolean
            .parseBoolean(getFlagValueFromTestName(attributes, "autoGrantPermissions"));
      }
      if (attributes.trim().contains("uninstall=")) {
        uninstall = Boolean.parseBoolean(getFlagValueFromTestName(attributes, "uninstall"));
      }
      if (attributes.trim().contains("noReset=")) {
        noReset = Boolean.parseBoolean(getFlagValueFromTestName(attributes, "noReset"));
      }
      if (DriverFactory.environment.get("DriverType").equalsIgnoreCase("android")) {
        if (uninstall) {
          new AndroidUtility()
              .removeAppIfInstall(deviceID, DriverFactory.environment.get("packageName"));
        }
        new Androiddriver().createAndroidDriver(deviceID, noReset, autoGrantPermissions);
      } else {
        // iOS code for
      }
    }
    DriverFactory.setTestDetails("startTime", "" + System.currentTimeMillis());
  }

  @Override
  @AfterMethod
  public void tearDownTest(ITestResult iTestResult, Object[] args, ITestContext iTestContext)
      throws Exception {
    long completeTime = System.currentTimeMillis();
    String time = JavaWrappers
        .getTime(Long.parseLong(DriverFactory.getTestDetails("startTime")), completeTime);
//    String tagName = String.valueOf(args[4]);
    String deviceID = DriverFactory.getTestDetails("udid");
    String deviceName = DriverFactory.getTestDetails(deviceID + "_deviceName");
    String androidVersion = DriverFactory.getTestDetails(deviceID + "androidVersion");
    String testCaseName = DriverFactory.getTestDetails("testCaseName");
    testCaseName = testCaseName.replaceAll(" ", "_");
    String status = setReportStatus(iTestResult);
    DriverFactory.getFeatureReport(DriverFactory.getTestDetails("featureName"))
        .addTestToFeatureFile(deviceName + "/" + androidVersion,
            DriverFactory.getTestDetails("featureName"), testCaseName, status,
            DriverFactory.getTestDetails("testCaseReportPath"), time);
    quitDriverAndResetData();
    addDeviceID(deviceID);
  }

  @Override
  @DataProvider(name = "scenarios", parallel = true)
  public Object[][] dataProviderMethod() {
    testNGCucumberRunner = new TestNGCucumberRunner(this.getClass());
    List<Object[]> _scenarios = new ArrayList<Object[]>();
    Object[][] scenarios = testNGCucumberRunner.provideScenarios();
    PickleEventWrapper pickleEvent = null;
    CucumberFeatureWrapper _cucumberFeatureWrapper = null;
    int count;
    if (DriverFactory.environment.get("DriverType").isEmpty()) {
      return _scenarios.toArray(new Object[][]{});
    }
    if (DriverFactory.environment.get("DriverType").trim().equalsIgnoreCase("ANDROID")) {
      count = Math.min(DriverFactory.availableDevices.size(), scenarios.length);
    } else {
      count = !DriverFactory.environment.get("maxThreads").trim().equalsIgnoreCase("") ? (Math
          .min(Integer.parseInt(DriverFactory.environment.get("maxThreads").trim()),
              scenarios.length)) : scenarios.length;
    }
//    driverTypesReportCreationStatus = new HashMapNew();
//    driverTypesReportTimestamp = new ConcurrentHashMap<String, String>();
//    deviceAcquistionStatus = new HashMap<>();
//    copyDeviceAcquistionStatus = new HashMap<>();
    HashMap<String, Integer> repeatedScenariosCount = new HashMap<>();
//    scenarioCountPerFeaturePerDevice = new HashMap<String, Integer>();
    count = count == 0 ? 1 : count;
//    totaldevicescount = count;
    int index = 1;
    String exceptions = "";
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
        String str = String.valueOf(_cucumberFeatureWrapper).replaceAll("^\"|\"$", "");
        String scenarioName = String.valueOf(pickleEvent).replaceAll("^\"|\"$", "");
        boolean flag = true;
//        if((browser.trim().equalsIgnoreCase("ANDROID") || browser.trim().equalsIgnoreCase("CHROME") || browser.trim().equalsIgnoreCase("IOS")) && (scenarioName.trim().indexOf("carrier=") > -1 || scenarioName.trim().indexOf("network=data") > -1)) {
//          String carrierName = "any";
//          if(scenarioName.trim().indexOf("carrier=") > -1) {
//            carrierName = scenarioName.trim().substring(scenarioName.trim().indexOf("carrier=") + "carrier=".length()).trim();
//            if(carrierName.trim().contains(",")) {
//              carrierName = carrierName.substring(0, carrierName.indexOf(","));
//            }
//          }
//          String operatorName = "";
//          try {
//            operatorName = utils.getOperatorName(deviceList.get(browser.trim().toUpperCase() + String.valueOf(index)), browser.trim().toUpperCase(), devicefarm);
//          } catch (Exception e) {
//            //Do Nothing
//          }
//          if(operatorName.trim().toLowerCase().contains(carrierName.trim().toLowerCase()) || (scenarioName.trim().indexOf("network=data") > -1 && !operatorName.trim().equalsIgnoreCase(""))) {
//            flag = true;
//          } else {
//            flag = false;
//            String[] udids = listOfDevices.trim().split(",");
//            String foundUdid = "";
//            for(int j = 0; j < udids.length; j++) {
//              if(!udids[j].trim().equalsIgnoreCase(deviceList.get(browser.trim().toUpperCase() + String.valueOf(index)).trim())) {
//                try {
//                  operatorName = utils.getOperatorName(udids[j].trim(), browser.trim().toUpperCase(), devicefarm);
//                } catch (Exception e) {
//                  //Do Nothing
//                }
//                if(operatorName.trim().toLowerCase().contains(carrierName.trim().toLowerCase()) || (scenarioName.trim().indexOf("network=data") > -1 && !operatorName.trim().equalsIgnoreCase(""))) {
//                  foundUdid = udids[j].trim();
//                  flag = true;
//                  break;
//                }
//              }
//            }
//            if(flag) {
//              Set<String> keys = deviceList.keySet();
//              Iterator<String> iter = keys.iterator();
//              while(iter.hasNext()) {
//                String key = iter.next();
//                if(deviceList.get(key).trim().equalsIgnoreCase(foundUdid.trim())) {
//                  driverTypesReportCreationStatus.put(str.replaceAll(" ", "_") + "_" + key, "false");
//                  scenarioCountPerFeaturePerDevice.put(str.replaceAll(" ", "_") + "_" + key, scenarioCountPerFeaturePerDevice.get(str.replaceAll(" ", "_") + "_" + key) == null ? 1 : scenarioCountPerFeaturePerDevice.get(str.replaceAll(" ", "_") + "_" + key) + 1);
//                  Queue<String> queue;
//                  String threadName;
//                  if(devicefarmAppiumSessionStarted.get(key) ==  null) {
//                    queue = new LinkedList<>();
//                    threadName = "THREAD" + (queue.size() + 1);
//                    queue.add(key + threadName);
//                  } else {
//                    queue = devicefarmAppiumSessionStarted.get(key);
//                    threadName = "THREAD" + (queue.size() + 1);
//                    queue.add(key + threadName);
//                  }
//                  devicefarmAppiumSessionStarted.put(key, queue);
//                  _scenarios.add(new Object[]{pickleEvent, _cucumberFeatureWrapper, key, count, tagName, testName, threadName, attributes});
//                  index++;
//                  if(index == count + 1) {
//                    index = 1;
//                  }
//                  break;
//                }
//              }
//              flag = false;
//            }
//          }
//        }
//        if(flag) {
//          driverTypesReportCreationStatus.put(str.replaceAll(" ", "_") + "_" + browser + String.valueOf(index), "false");
//          scenarioCountPerFeaturePerDevice.put(str.replaceAll(" ", "_") + "_" + browser + String.valueOf(index), scenarioCountPerFeaturePerDevice.get(str.replaceAll(" ", "_") + "_" + browser + String.valueOf(index)) == null ? 1 : scenarioCountPerFeaturePerDevice.get(str.replaceAll(" ", "_") + "_" + browser + String.valueOf(index)) + 1);
//          Queue<String> queue;
//          String threadName;
//          String key = browser + String.valueOf(index);
//          if(devicefarmAppiumSessionStarted.get(key) ==  null) {
//            queue = new LinkedList<>();
//            threadName = "THREAD" + (queue.size() + 1);
//            queue.add(key + threadName);
//          } else {
//            queue = devicefarmAppiumSessionStarted.get(key);
//            threadName = "THREAD" + (queue.size() + 1);
//            queue.add(key + threadName);
//          }
//          devicefarmAppiumSessionStarted.put(key, queue);
        _scenarios.add(new Object[]{pickleEvent, _cucumberFeatureWrapper,
            DriverFactory.environment.get("OSType") + index++, count, tagName, testName,
            "THREAD" + index++, attributes});
        if (index == count + 1) {
          index = 1;
        }
      }
//      } else {
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
    }
//    if(!exceptions.trim().equalsIgnoreCase("")) {
//      System.out.println(exceptions);
//      throw new CucumberException("Parsing error. Please see console logs");
//    }
//    if((browser.trim().equalsIgnoreCase("ANDROID") || browser.trim().equalsIgnoreCase("CHROME") || browser.trim().equalsIgnoreCase("IOS")) && !devicesConfiguration.trim().equalsIgnoreCase("")) {
//      String[] deviceTypesConfiguration = devicesConfiguration.trim().split(",");
//      for(int i = 0; i < deviceTypesConfiguration.length; i++) {
//        String[] data = deviceTypesConfiguration[i].trim().split(":");
//        String deviceType = data[0];
//        int counter = Integer.valueOf(data[1]);
//        if(deviceType.trim().equalsIgnoreCase("ANDROID"))
//          counter = androidDeviceCount;
//        else if(deviceType.trim().equalsIgnoreCase("IOS"))
//          counter = iosDeviceCount;
//        else if(deviceType.trim().equalsIgnoreCase("CHROME"))
//          counter = chromeCount;
//        for(int j = 0; j < counter; j++) {
//          DriverFactory.setMqttMessages(deviceType.trim().toUpperCase() + String.valueOf(j+1), null);
//        }
//      }
//    } else {
//      for(int i = 0; i < count; i++) {
//        DriverFactory.setMqttMessages(browser + String.valueOf(i+1), null);
//      }
//    }
    System.out.println("Total scenarios count :: " + _scenarios.size());
    return _scenarios.toArray(new Object[][]{});
  }


  public synchronized String getAvailableDeviceID() {
    while (DriverFactory.availableDevices.size() <= 0) {
      JavaWrappers.sleep(60);   // wait 1min if device is not available
      System.out.println("Device is not available");
    }
    String udid = DriverFactory.availableDevices.get(0);
    DriverFactory.availableDevices.remove(0);
    if (parallelSuitesCounter > 0) {
      JavaWrappers.sleep(80);
    }
    parallelSuitesCounter++;
    return udid;
  }

  public void addDeviceID(String udid) {
    DriverFactory.availableDevices.add(udid);
    System.out.println("Device added: " + udid);
    System.out.println(DriverFactory.availableDevices);
  }


}
