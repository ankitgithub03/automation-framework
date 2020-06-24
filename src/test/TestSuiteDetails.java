package test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TestSuiteDetails {

  long testSuite_startTime = 0;
  long testSuite_completeTime = 0;
  int totalPassCount = 0;
  int totalFailCount = 0;
  int totalSkipCount = 0;

   /**
   * can be use for save test data in the whole suite level in {@link HashMap}
   * */
  HashMap<String, Object> testSuiteDataMap = new HashMap<>();

  // number of connect devices
  List<String> connectedDevices = new ArrayList<String>();

  /**
   * Have all the data from the object repository contains as PageName, Page Element name, locators and their value.
   * of type HashMap({String, HashMap{String, HashMap{String, String}}})
   */
  public static HashMap<String, HashMap<String,HashMap<String, String>>> objectRepoMapValues = new HashMap<String, HashMap<String,HashMap<String, String>>>();




}
