package test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import report.custom.FeatureReporting;
import report.custom.TestReporting;

public class DriverFactory {

  private static String env = "";


  /**
   * All Suite configuration values {@link HashMap} of type (String, String)
   */
  public static HashMap<String, String> environment = new HashMap<String, String>();


  /**
   * All test configuration values {@link HashMap} of type (String, String)
   */
  public static HashMap<String, String> testDataMap = new HashMap<String, String>();



  /**
   * All connected devices udid value {@link HashMap} of type (String, String)
   */
  public static List<String> connectedDevices = new ArrayList<String>();

  /**
   * All available devices udid for use {@link HashMap} of type (String, String)
   */
  public static List<String> availableDevices = new ArrayList<String>();

  /**
   * devices details test configuration values {@link HashMap} of type (String, String)
   */
  public static HashMap<String, String> deviceDetails = new HashMap<String, String>();

  /**
   * All reserve devices udid value {@link HashMap} of type (String, String)
   */
  public static List<String> reserveDevices = new ArrayList<String>();

  /**
   * All free devices udid value {@link HashMap} of type (String, String)
   */
  public static List<String> freeDevices = new ArrayList<String>();


  /**
   * Common wait for Selenium and Appium {@link HashMap} of type (String, Integer)
   */
//  public static HashMap<String, Integer> waitConfigValues = new HashMap<String, Integer>();


  /**
   * Have all the data from the object repository contains as PageName, Page Element name, locators and their value.
   * of type HashMap({String, HashMap{String, HashMap{String, String}}})
   */
  public static HashMap<String, HashMap<String,HashMap<String, String>>> objectRepoMapValues = new HashMap<String, HashMap<String,HashMap<String, String>>>();

  private static InheritableThreadLocal<String> featureReportingPath = new InheritableThreadLocal<String>() {
    @Override
    protected String initialValue() {
      return "";
    }
  };

  private static InheritableThreadLocal<FeatureReporting> featureReporting = new InheritableThreadLocal<FeatureReporting>() {
    @Override
    protected FeatureReporting initialValue() {
      return null;
    }
  };

  private static InheritableThreadLocal<HashMap<String, FeatureReporting>> featureReport = new InheritableThreadLocal<HashMap<String, FeatureReporting>>(){
    @Override
    protected HashMap<String, FeatureReporting> initialValue() {
      return new HashMap<>();
    }
  };

  private static InheritableThreadLocal<HashMap<String, String>> testDetails = new InheritableThreadLocal<HashMap<String, String>>(){
    @Override
    protected HashMap<String, String> initialValue() {
      return new HashMap<>();
    }
  };


  private static InheritableThreadLocal<TestReporting> testReporting = new InheritableThreadLocal<TestReporting>() {
    @Override
    protected TestReporting initialValue() {
      return null;
    }
  };

  public static FeatureReporting getFeatureReporting(){
    return featureReporting.get();
  }

  public static void setTestReporting(TestReporting tr){
    testReporting.set(tr);
  }

  public static TestReporting getTestReporting(){
    return testReporting.get();
  }

  public static void setEnv(String environment){
    env = environment;
  }

  public static String getEnv(){
    return env;
  }

  public static FeatureReporting getFeatureReport(String featureName){
    return featureReport.get().get(featureName);
  }

  public static void setFeatureReport(String featureName, FeatureReporting featureReporting){
    HashMap<String, FeatureReporting> map = new HashMap<>();
    map.put(featureName,featureReporting);
    featureReport.set(map);
  }

  public static void setTestDetails(String key, String value){
    HashMap<String, String> map = new HashMap<>();
    testDetails.set(map);
  }

  public static String getTestDetails(String key){
    return testDetails.get().get(key);
  }

  public static void removeTestDetails(){
    testDetails.remove();
  }

  public static void removeTestReporting(){
    testReporting.remove();
  }



}
