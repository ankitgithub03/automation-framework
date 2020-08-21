package test;

import io.restassured.response.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import net.lightbody.bmp.BrowserMobProxy;
import report.custom.FeatureReporting;
import report.custom.TestReporting;
import utils.CustomizeAssert;
import utils.HashMapNew;
import org.openqa.selenium.Proxy;

public class DriverFactory {

  private static String env = "";


  /**
   * All Suite configuration values {@link HashMap} of type (String, String)
   */
  public static HashMapNew environment = new HashMapNew();


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
  public static List<String> offlineDevices = new ArrayList<String>();

  public static HashMap<String, Object> sTestData = new HashMap<>();

  public static TreeSet<String> tags = new TreeSet<>();


  /**
   * Common wait for Selenium and Appium {@link HashMap} of type (String, Integer)
   */
//  public static HashMap<String, Integer> waitConfigValues = new HashMap<String, Integer>();


  /**
   * Have all the data from the object repository contains as PageName, Page Element name, locators and their value.
   * of type HashMap({String, HashMap{String, HashMap{String, String}}})
   */
  public static HashMap<String, HashMap<String,HashMap<String, String>>> locatorsMapValues = new HashMap<String, HashMap<String,HashMap<String, String>>>();

  private static InheritableThreadLocal<Thread> gifThread= new InheritableThreadLocal<Thread>(){
    @Override
    protected Thread initialValue() {
      return null;
    }
  };

  private static InheritableThreadLocal<CustomizeAssert> assertThread = new InheritableThreadLocal<CustomizeAssert>(){
    @Override
    protected CustomizeAssert initialValue(){
      return null;
    }
  };

  private static InheritableThreadLocal<BrowserMobProxy> browserProxy = new InheritableThreadLocal<BrowserMobProxy>();

  private static HashMap<String, FeatureReporting> featureReport = new HashMap<>();

  private static InheritableThreadLocal<HashMapNew> testDetails = new InheritableThreadLocal<HashMapNew>(){
    @Override
    protected HashMapNew initialValue() {
      return new HashMapNew();
    }
  };

  private static InheritableThreadLocal<List<Thread>> allTestThreads = new InheritableThreadLocal<List<Thread>>(){
    @Override
    protected List<Thread> initialValue(){
      return new ArrayList<Thread>();
    }
  };


  private static InheritableThreadLocal<TestReporting> testReporting = new InheritableThreadLocal<TestReporting>() {
    @Override
    protected TestReporting initialValue() {
      return null;
    }
  };

  private static InheritableThreadLocal<Response> sResponse = new InheritableThreadLocal<Response>(){
    @Override public Response initialValue() {
      return null;
    }
  };

  private static InheritableThreadLocal<HashMap<String, Response>> sResponseMap = new InheritableThreadLocal<HashMap<String, Response>>(){
    @Override public HashMap<String, Response> initialValue() {
      return new HashMap<String, Response>();
    }
  };

  // it will has featureName -> (testCaseName report Link, status)
  public static Set<String> modules = new HashSet<>();

  public static List<HashMapNew> results = new ArrayList<>();

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
    return featureReport.get(featureName);
  }

  public static void setFeatureReport(String featureName, FeatureReporting featureReporting){
    featureReport.put(featureName,featureReporting);
  }

  public static void setTestDetails(String key, String value){
    testDetails.get().put(key,value);
  }

  public static void setTestDetails(HashMapNew map){
    testDetails.get().putAll(map);
  }

  public static String getTestDetails(String key){
      return testDetails.get().get(key).trim();
  }

  public static HashMapNew getWholeTestDetails(){
    return testDetails.get();
  }

  public static void removeTestDetails(){
    testDetails.remove();
  }

  public static void removeTestReporting(){
    testReporting.remove();
  }

  public static void setGifThread(Thread thread){ gifThread.set(thread);}
  public static Thread getGifThread(){return gifThread.get();}
  public static void setAssert(CustomizeAssert customizeAssert){assertThread.set(customizeAssert);}
  public static CustomizeAssert getAssert(){return assertThread.get();}
  public static void setBrowserMobProxy(BrowserMobProxy proxy){browserProxy.set(proxy);}
  public static BrowserMobProxy getBrowserMobProxy(){return browserProxy.get();}
  public static void setThread(Thread t){
    allTestThreads.get().add(t);
  }

  public static List<Thread> getAllThreads(){
    return allTestThreads.get();
  }

  public static Response getResponse() {
    return sResponse.get();
  }

  public static void setResponse(Response response) { sResponse.set(response); }

  public static Response getResponse(String key) {
    return sResponseMap.get().get(key);
  }

  public static void setResponse(String key, Response response) { sResponseMap.get().put(key, response); }


}
