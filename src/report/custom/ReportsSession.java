package report.custom;

import java.io.File;

/**
 * @author Ankit Gupta
 *
 */
//public class ReportingSession extends SeleniumWrappers{

public class ReportsSession {

  String reportDir = System.getProperty("user.dir")+ File.separator+"Reports";
  public static String reportUrl = "";
  public static String featurePath = "";
  public static String reportFolder = "";
  public static String mailReportFile = "";
  public static String createdFolderName = "";
  public static String mailReportName = "index.html";
  public static int totalTestCases =0;
  public static int totalPassTestCases =0;
  public static int totalFailTestCases =0;
  public static int totalSkipTestCases=0;
  public static int totalPassPercentage =0;
  public static long mreportStartTime = 0;


}
