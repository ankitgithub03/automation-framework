package report.custom;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import lombok.Data;
import test.DriverFactory;
import utils.JavaWrappers;


@Data
public class FeatureReporting extends ReportsSession {


  private String logsFolder = "";
  private OutputStream masterHtmlFile;
  private PrintStream masterPrintHtml;
  private int totalPassTestCase = 0;
  private int totalSkipTestCase = 0;
  private int totalFailTestCase = 0;
  private long mSuiteStartTime =0;
  private String featureFilePath = "";
  private String featureName = "";
  private String featureReportFolder = "";
  private String featureReportFilePath ="";


  /**
   * Create a header functionality for master file html
   *
   * @author Ankit
   */
  public String createFeatureFileHeader(String name) {
    try {
      totalFailTestCase = 0;
      totalPassTestCase = 0;
      totalSkipTestCase = 0;
      featureName = name;
      JavaWrappers.createDir(featurePath, name).renameTo(new File(featureFilePath, name));
      featureReportFolder = featurePath + File.separator + name;
      JavaWrappers.createDir(featureReportFolder, "Feature")
          .renameTo(new File(featureReportFolder, "Feature"));
      String testReport = featureReportFolder + File.separator + "Feature";
      String screenShot = testReport + File.separator + "ScreenShot";
      logsFolder = testReport + File.separator + "Logs";
			if (!(new File(screenShot).exists())) {
				JavaWrappers.createDir(testReport, "ScreenShot")
						.renameTo(new File(testReport, "ScreenShot"));
			}
      if (!(new File(logsFolder).exists())) {
        JavaWrappers.createDir(testReport, "Logs").renameTo(new File(testReport, "Logs"));
      }
      featureReportFilePath = "Feature" + File.separator + name + File.separator + name + ".html";
      openSuiteFile();
      createSuiteHtml();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    System.out.println(featureReportFolder);
    return featureReportFolder;
  }


  private void openSuiteFile() {
    try {
      masterHtmlFile = new FileOutputStream(
          new File(featureReportFolder + "/" + featureName + ".html"), true);
      featurePath = featureReportFolder + File.separator + featureName + ".html";
      masterPrintHtml = new PrintStream(masterHtmlFile);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }


  /**
   * Call method to add the test case into the file
   *
   * @param strPassFail
   * @param scenarioName
   * @param partialResultFileLink
   */
  public void addTestToFeatureFile(String deviceAndVersion,String moduleName,String scenarioName,String strPassFail,
      String partialResultFileLink, String totalExecutionTime) {

    masterPrintHtml.println("<tr>");
    masterPrintHtml.println("<td width='15%' bgcolor='' valign='top' align='justify' ><font color='black' face='VERDANA' size='2'>"+deviceAndVersion+"</font></td>");
    masterPrintHtml.println("<td width='20%' bgcolor='' valign='top' align='justify' ><font color='black' face='VERDANA' size='2'>"+moduleName+"</font></td>");
    masterPrintHtml.println("<td width='50%' bgcolor='' valign='top' align='justify' ><font color='black' face='VERDANA' size='2'>"+scenarioName+"</font></td>");

    String value = strPassFail.toUpperCase();
    String imageLink = "";
		if (value.equals("PASS")) {
			imageLink = "<a href=\"" + "./TestCases/" + partialResultFileLink + "\">" + value + "</a>";
		} else {
			imageLink = "<a href=\"" + "./TestCases/" + partialResultFileLink + "\"><font color='#FF7373'>"+value+"</font></a>";
		}

    if (strPassFail.toUpperCase().equalsIgnoreCase("PASS")) {
      this.totalPassTestCase = this.totalPassTestCase + 1;
      masterPrintHtml.println("<td width='5%' bgcolor='' valign='middle' align='center'><b><font color='black' face='VERDANA' size='2'>"+imageLink+"</font></b></td>");
    } else if (strPassFail.toUpperCase().equalsIgnoreCase("FAIL")) {
      this.totalFailTestCase = this.totalFailTestCase + 1;
      masterPrintHtml.println("<td width='5%' bgcolor='' valign='middle' align='center'><b><font color='red' face='VERDANA' size='2'>"+imageLink+"</font></b></td>");
    } else if (strPassFail.toUpperCase().equalsIgnoreCase("SKIP")) {
      this.totalSkipTestCase = this.totalSkipTestCase + 1;
      masterPrintHtml.println("<td width='5%' bgcolor='' valign='middle' align='center'><b><font color='blue' face='VERDANA' size='2'>"+imageLink+"</font></b></td>");
    }
    masterPrintHtml.println("<td width='10%' bgcolor='' valign='middle' align='center' ><font color='black' face='VERDANA' size='2'>"+totalExecutionTime+"</font></td>");
//    System.out.println(this.totalPassTestCase);
  }


  /**
   * Create a header functionality for master file html
   *
   * @author Ankit
   */
  public String createAPIFeatureFileHeader( String name) {
    try {
      totalFailTestCase = 0;
      totalPassTestCase = 0;
      totalSkipTestCase = 0;
      featureName = name;
      JavaWrappers.createDir(featurePath, featureName)
          .renameTo(new File(featurePath, featureName));
      featureReportFolder = featurePath + File.separator + featureName;
      JavaWrappers.createDir(featureReportFolder, "Feature")
          .renameTo(new File(featureReportFolder, "Feature"));
      featureReportFilePath = "Feature" + File.separator + featureName + File.separator + featureName + ".html";
      openSuiteFile();
      createAPIFeatureHtml();
    } catch (Exception ex) {
      ex.printStackTrace();
    }

    return featureReportFolder;
  }


  public void createAPIFeatureHtml() {
    // creating html table
    masterPrintHtml.println("<html>");
    masterPrintHtml.println("<title>API Test Script-Report</title>");
    masterPrintHtml.println("<head></head>");
    masterPrintHtml.println("<body>");
    masterPrintHtml.println("<script type=\"text/javascript\">" +

        "function toggle_visibility(id,id1) {" +
        "var e = document.getElementById(id);" +

        "if(e.style.display == 'block') {" +
        "document.getElementById(id1).innerHTML = \"+\";" +
        "e.style.display = 'none';" +

        "}" +
        "else {" +
        "document.getElementById(id1).innerHTML = \"-\";" +
        "e.style.display = 'block';" +

        "}" +
        "}" +
        "</script>");

    // mentioning Suite name and Application on top of the table
    masterPrintHtml.println("<h2 align='center'>");
    masterPrintHtml.println("<span style='color:grey'>" + featureName + "</span>");
    masterPrintHtml.println("<BR>");
    masterPrintHtml.println("<BR>");
    //creating table for TestCases and their results
//		masterPrintHtml.println("<table border='0' width='100%' height='45'>");
    masterPrintHtml
        .println("<table class='tg' border='2' style='border-collapse:collapse;width:100%;'>");
    masterPrintHtml.println("<tbody>");

    //Setting the header for the table
//		masterPrintHtml.println("<tr bgcolor='#b3b3ff'>");
    masterPrintHtml.println("<tr bgcolor='#92DAEA'>");
    masterPrintHtml
        .println("<b><font color='Black' face='Tahoma' size='4'>TestCaseName</font></b>");
    masterPrintHtml.println("</td>");
    masterPrintHtml.println("<td width='auto' valign='middle' align='center'>");
    masterPrintHtml.println("<b><font color='Black' face='Tahoma' size='4'>Status</font></b>");
    masterPrintHtml.println("</td>");
    masterPrintHtml.println("</td>");
//		masterPrintHtml.println("<td width='auto' valign='middle' align='center'>");
//		masterPrintHtml.println("<b><font color='Black' face='Tahoma' size='4'>Blocked percentage</font></b>");
//		masterPrintHtml.println("</td>");
    masterPrintHtml.println("<td width='auto' valign='middle' align='center'>");
    masterPrintHtml.println("<b><font color='Black' face='Tahoma' size='4'>TimeTaken</font></b>");
    masterPrintHtml.println("</td>");
    masterPrintHtml.println("</tr>");
  }


  private void createSuiteHtml() {
    // creating html table
    masterPrintHtml.println("<html>");
    masterPrintHtml.println("<title>Automation Test Script-Report</title>");
    masterPrintHtml.println("<head></head>");
    masterPrintHtml.println("<body>");
    String driverType = DriverFactory.environment.get("driverType");
    // mentioning Suite name and Application on top of the table
    masterPrintHtml.println("<h2 align='center'>");
    masterPrintHtml.println("<span style='color:grey'>" + featureName + "</span>");
    if(driverType.equalsIgnoreCase("android") || driverType.equalsIgnoreCase("ios")) {
    masterPrintHtml.println(" Executed on " + driverType + " ");
    masterPrintHtml.println("<BR>");
    masterPrintHtml.println("<span style='color:grey'> AppVersion </span>");
      masterPrintHtml.println("<span style='color:black'>: </span>");
      masterPrintHtml.println("<span style='color:grey'>" + DriverFactory.environment.get("appVersion") + "</span>");
    }
//    else if (driverType.equalsIgnoreCase("chrome")){
//      masterPrintHtml.println("<span style='color:grey'> Version </span>");
//      masterPrintHtml.println("<span style='color:black'>: </span>");
//      masterPrintHtml.println("<span style='color:grey'>" + appVersion + "</span>");
//    }
//    masterPrintHtml.println("<span style='color:black'>, </span>");
//    masterPrintHtml.println("<span style='color:grey'>" + deviceID + "</span>");
    masterPrintHtml.println("<BR>");
    masterPrintHtml.println("<BR>");
    //creating table for TestCases and their results
//		masterPrintHtml.println("<table border='0' width='100%' height='45'>");
    masterPrintHtml
        .println("<table class='tg' border='2' style='border-collapse:collapse;width:100%;'>");
    masterPrintHtml.println("<tbody>");

    //Setting the header for the table
//		masterPrintHtml.println("<tr bgcolor='#b3b3ff'>");
    masterPrintHtml.println("<tr bgcolor='#92DAEA'>");
    masterPrintHtml.println("<td width='auto' valign='middle' align='center'>");
    masterPrintHtml.println("<td width='auto' valign='middle' align='center'>");
    masterPrintHtml
        .println("<b><font color='Black' face='Tahoma' size='4'>Device/Version</font></b>");
    masterPrintHtml
        .println("<b><font color='Black' face='VERDANA' size='4'>Scenario</font></b>");
    masterPrintHtml.println("</td>");
    masterPrintHtml.println("<td width='auto' valign='middle' align='center'>");
    masterPrintHtml.println("<b><font color='Black' face='VERDANA' size='4'>Status</font></b>");
    masterPrintHtml.println("</td>");
    masterPrintHtml.println("<td width='auto' valign='middle' align='center'>");
    masterPrintHtml.println("<b><font color='Black' face='VERDANA' size='4'>TimeTaken</font></b>");
    masterPrintHtml.println("</td>");
    masterPrintHtml.println("</tr>");
    mSuiteStartTime = System.currentTimeMillis();
  }

  /**
   * create footer part of the master file html
   *
   * @author Ankit
   */
  private void completeSuiteFileFooter(FinalReport finalReport) {
    openSuiteFile();
    long mEndTime = System.currentTimeMillis();
    int testCaseTotal = this.totalPassTestCase + this.totalFailTestCase;
    System.out.println(testCaseTotal);
    masterPrintHtml.println("</tbody>");
    masterPrintHtml.println("</table>");
    masterPrintHtml.println("<br>");

    masterPrintHtml
        .println("<table class='tg' border='2' style='border-collapse:collapse;width:100%;'>");
    masterPrintHtml.println("<tbody>");
    masterPrintHtml.println("<tr style='background-color:#92DAEA;text-align:center'>");
    masterPrintHtml.println(
        "<th class='tg-031e' colspan='6'><b><font color='Black' face='VERDANA' size='3'>TestScenario Execution Details</b><br></th>");
    masterPrintHtml.println("</tr>");
    masterPrintHtml.println("<tr style='background-color:#D7E0E1;'>");
    masterPrintHtml.println(
        "<td class='tg-9hbo' style='text-align:center'><b><font color='Black' face='VERDANA' size='2'>Total Test Cases</b></td>");
    masterPrintHtml.println(
        "<td class='tg-9hbo' style='text-align:center'><b><font color='Black' face='VERDANA' size='2'>Passed</b></td>");
    masterPrintHtml.println(
        "<td class='tg-9hbo' style='text-align:center'><b><font color='Black' face='VERDANA' size='2'>Failed</b></td>");
    masterPrintHtml.println(
        "<td class='tg-9hbo' style='text-align:center'><b><font color='Black' face='VERDANA' size='2'>Skipped</b></td>");
    masterPrintHtml.println(
        "<td class='tg-9hbo' style='text-align:center'><b><font color='Black' face='VERDANA' size='2'>Start Time</b></td>");
    masterPrintHtml.println(
        "<td class='tg-9hbo' style='text-align:center'><b><font color='Black' face='VERDANA' size='2'>End Time</b></td>");
    masterPrintHtml.println(
        "<td class='tg-9hbo' style='text-align:center'><b><font color='Black' face='VERDANA' size='2'>Total Execution Time</b></td>");
    masterPrintHtml.println("</td>");
    masterPrintHtml.println("<tr>");
    masterPrintHtml.println(
        "<td class='tg-yw4l' style='text-align:center'><font color='Black' face='VERDANA' size='2'>"
            + testCaseTotal + "</td>");
    masterPrintHtml.println(
        "<td class='tg-yw4l' style='text-align:center'><font color='Black' face='VERDANA' size='2'>"
            + this.totalPassTestCase + "</td>");
    masterPrintHtml.println(
        "<td class='tg-baqh' style='text-align:center'><font color='Black' face='VERDANA' size='2'>"
            + this.totalFailTestCase + "</td>");
    masterPrintHtml.println(
        "<td class='tg-baqh' style='text-align:center'><font color='Black' face='VERDANA' size='2'>"
            + this.totalSkipTestCase + "</td>");
    masterPrintHtml.println(
        "<td class='tg-yw4l' style='text-align:center'><font color='Black' face='VERDANA' size='2'>"
            + mSuiteStartTime + "</td>");
    masterPrintHtml.println(
        "<td class='tg-yw4l' style='text-align:center'><font color='Black' face='VERDANA' size='2'>"
            + JavaWrappers.getCurrentTime("HH:m:ss") + "</td>");
    String totalTimeTaken = JavaWrappers.getTime(mSuiteStartTime, mEndTime);
    masterPrintHtml.println(
        "<td class='tg-yw4l' style='text-align:center'><font color='Black' face='VERDANA' size='2'>"
            + totalTimeTaken + "</td>");
    masterPrintHtml.println("</tr>");

    masterPrintHtml.println("</tbody>");
    masterPrintHtml.println("</table>");
    masterPrintHtml.println("</body>");
    masterPrintHtml.println("</html>");
    masterPrintHtml.close();
    finalReport
        .addSuiteFileInto_FinalReport(this.totalPassTestCase, this.totalFailTestCase, this.totalSkipTestCase,featureName,
            featureReportFilePath, totalTimeTaken);

  }


}
