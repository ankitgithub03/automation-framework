package report.custom;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import test.DriverFactory;
import utils.JavaWrappers;


public class FinalReport extends ReportsSession{
	
	  private String reportFolder = "Automation";
    public static OutputStream finalHtmlFile;
    public static PrintStream finalPrintHtml;
    public static long mreportStartTime = 0;
    public static int totalPassedTestCase =0;
    public static int totalFailedTestCase =0;
	  public static int totalSkipTestCase =0;
    private String reportStartTime = "";
    
    
    
    /**
     * Initializing the UI automation final report file and create the folder in "user" directory
     * @param reportName
     * @param reportFolderName
     * @return
     * @throws IOException
     */
	public String initializeFinalUIReport(String reportName,String reportFolderName) throws IOException{
		String systemUserName = System.getProperty("user.home");
		reportFolder = reportFolderName;
		finalReportPath = JavaWrappers.createDir(systemUserName, reportFolder).getAbsolutePath();
		createdFolderName = finalReportPath.substring(finalReportPath.indexOf(reportFolder), finalReportPath.length());
		JavaWrappers.createDir(finalReportPath, "Feature").renameTo(new File(finalReportPath,"Feature"));
		featurePath = finalReportPath+File.separator+"Feature";
		System.out.println("report path; "+featurePath);
		mreportStartTime =  System.currentTimeMillis();
		reportStartTime = JavaWrappers.getCurrentTime("HH:mm:ss");
		createUIFinalReportHeader(reportName);
		return finalReportFile;
	}
	
	
    /**
     * Initializing API automation the final report file and create the folder in "user" directory
     * @return
     * @throws IOException
     */
	public String initializeFinalAPIReport(String reportType) throws IOException{
		String systemUserName = System.getProperty("user.home");
		reportFolder = reportType+"Report";
		finalReportPath = JavaWrappers.createDir(systemUserName, reportFolder).getAbsolutePath();
		createdFolderName = finalReportPath.substring(finalReportPath.indexOf(reportFolder), finalReportPath.length());
		JavaWrappers.createDir(finalReportPath, "Feature").renameTo(new File(finalReportPath,"Feature"));
		featurePath = finalReportPath+File.separator+"Feature";
		System.out.println("report path; "+featurePath);
		mreportStartTime =  System.currentTimeMillis();
		reportStartTime = JavaWrappers.getCurrentTime("HH:mm:ss");
		create_API_FinalReprtHeader(reportType);
		return finalReportFile;
	}
	
	
	private void create_API_FinalReprtHeader(String reportType){
		try {
			openFinalReportFile();
			// creating html table
			String hStyle = "style='background-color:#768f90; padding:15px 0; border:1px solid'";
			finalPrintHtml.println("<html>");
			finalPrintHtml.println("<title>"+reportType+" Report</title>");
			finalPrintHtml.println("<head></head>");
			finalPrintHtml.println("<body>");
			
			// mentioning Suite name and Application on top of the table
			finalPrintHtml.println("<h2 align='center'>");
			finalPrintHtml.println("<span style='color:black'><u>"+reportType+" Report</u></span>");
			finalPrintHtml.println("<BR>");
			finalPrintHtml.println("<h3 align='center'>");
			finalPrintHtml.println("<BR>");
			finalPrintHtml.println("<BR>");
			//creating table for TestCases and their results
//			finalPrintHtml.println("<table border='0' width='100%' height='45'>");
			finalPrintHtml.println("<table class='tg' border='2' style='border-collapse:collapse;width:100%;'>");
			finalPrintHtml.println("<tbody>");
			
			//Setting the header for the table
			finalPrintHtml.println("<tr bgcolor='#92DAEA'>");
			finalPrintHtml.println("<td width='auto' valign='middle' align='center'>");
			finalPrintHtml.println("<b><font color='Black' face='Tahoma' size='4'>TestSuiteName</font></b>");
			finalPrintHtml.println("</td>");
			finalPrintHtml.println("<td width='auto' valign='middle' align='center'>");
			finalPrintHtml.println("<b><font color='Black' face='Tahoma' size='4'>PASS</font></b>");
			finalPrintHtml.println("</td>");
			finalPrintHtml.println("</td>");
			finalPrintHtml.println("<td width='auto' valign='middle' align='center'>");
			finalPrintHtml.println("<b><font color='Black' face='Tahoma' size='4'>FAIL</font></b>");
			finalPrintHtml.println("</td>");
			finalPrintHtml.println("<td width='auto' valign='middle' align='center'>");
			finalPrintHtml.println("<b><font color='Black' face='Tahoma' size='4'>TimeTaken</font></b>");
			finalPrintHtml.println("</td>");
			finalPrintHtml.println("</tr>");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private void createUIFinalReportHeader(String reportName){
		try {
			openFinalReportFile();
			// creating html table
			String hStyle = "style='background-color:#768f90; padding:15px 0; border:1px solid'";
			finalPrintHtml.println("<html>");
			finalPrintHtml.println("<title>Automation Report</title>");
			finalPrintHtml.println("<head></head>");
			finalPrintHtml.println("<body>");
			
			// mentioning Suite name and Application on top of the table
			finalPrintHtml.println("<h2 align='center'>");
			finalPrintHtml.println("<span style='color:black'><u>"+reportName+ " Report</u></span>");
//			System.out.println("In final report "+DataMaps.SuiteConfigValues.get("url"));
			String platform = DriverFactory.environment.get("OSType");
			
//			if(platform.equalsIgnoreCase("Android")) {
				
//				System.out.println("In final report "+DataMaps.SuiteConfigValues.get("BROWSER_NAME_Android"));
				finalPrintHtml.println("<BR>");
				finalPrintHtml.println("<h3 align='center'>");
				finalPrintHtml.println("<span style='color:black'><u>Environment</u></span>");
				finalPrintHtml.println("<span style='color:black'>"+"-"+ DriverFactory.environment.get("Environment")+"</span>");
				finalPrintHtml.println("<span style='color:black'>"+" : "+ DriverFactory.environment.get("App_Version")+"</span>");
//				finalPrintHtml.println("<span style='color:black'>"+"- "+DataMaps.SuiteConfigValues.get("Android_VERSION")+"</span>");
//				}
//				else if(platform.equalsIgnoreCase("iOS")) {
////					System.out.println("In final report "+DataMaps.SuiteConfigValues.get("BROWSER_NAME_Iphone"));
//					finalPrintHtml.println("<BR>");
//					finalPrintHtml.println("<h3 align='center'>");
//					finalPrintHtml.println("<span style='color:black'><u>Environment</u></span>");
//					finalPrintHtml.println("<span style='color:black'>"+"-"+DataMaps.SuiteConfigValues.get("AppEnvironment")+"</span>");
////					finalPrintHtml.println("<span style='color:black'>"+": "+DataMaps.SuiteConfigValues.get("BROWSER_NAME_Iphone")+"</span>");
//					finalPrintHtml.println("<span style='color:black'>"+"- "+DataMaps.SuiteConfigValues.get("IOS_VERSION")+"</span>");
//				}
//				else {
//					finalPrintHtml.println("<BR>");
//					finalPrintHtml.println("<h3 align='center'>");
//					finalPrintHtml.println("<span style='color:black'><u>Environment</u></span>");
//					System.out.println("In final report "+DataMaps.SuiteConfigValues.get("url"));
//				}
			
			finalPrintHtml.println("<BR>");
			finalPrintHtml.println("<BR>");
			//creating table for TestCases and their results
//			finalPrintHtml.println("<table border='0' width='100%' height='45'>");
			finalPrintHtml.println("<table class='tg' border='2' style='font-weight:200;border-collapse:collapse;width:100%;'>");
			finalPrintHtml.println("<tbody>");
			
			//Setting the header for the table
			finalPrintHtml.println("<tr bgcolor='#92DAEA'>");
			finalPrintHtml.println("<td width='25%' valign='middle' align='center'>");
			finalPrintHtml.println("<b><font color='Black' face='Tahoma' size='4'>TestSuiteName</font></b>");
			finalPrintHtml.println("</td>");
			finalPrintHtml.println("<td width='17%' valign='middle' align='center'>");
			finalPrintHtml.println("<b><font color='Black' face='Tahoma' size='4'>Pass</font></b>");
			finalPrintHtml.println("</td>");
//			finalPrintHtml.println("</td>");
			finalPrintHtml.println("<td width='17%' valign='middle' align='center'>");
			finalPrintHtml.println("<b><font color='Black' face='Tahoma' size='4'>Fail</font></b>");
			finalPrintHtml.println("</td>");
			finalPrintHtml.println("<td width='17%' valign='middle' align='center'>");
			finalPrintHtml.println("<b><font color='Black' face='Tahoma' size='4'>Skip</font></b>");
			finalPrintHtml.println("</td>");
			finalPrintHtml.println("<td width='24%' valign='middle' align='center'>");
			finalPrintHtml.println("<b><font color='Black' face='Tahoma' size='4'>TimeTaken</font></b>");
			finalPrintHtml.println("</td>");
			finalPrintHtml.println("</tr>");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
    
	
	public void addSuiteFileInto_FinalReport(int totalPass, int totalFail, int totalSkip,String testSuiteName,String partialResultFileLink, String totalExecutionTime) {
		System.out.println("Adding Completed suite  "+testSuiteName);
		String dStyle = "style='background-color:#f8f8e9; padding:10px 5px; border:1px solid'";
		totalPassedTestCase = totalPassedTestCase +totalPass;
		totalFailedTestCase = totalFailedTestCase+totalFail;
		totalSkipTestCase = totalSkipTestCase+totalSkip;

		String imageLink = "<a style='text-decoration:none;' href='" +partialResultFileLink +"'>"+testSuiteName+" </a>";

		finalPrintHtml.println("<tr>");
		finalPrintHtml
		.println("<td width='25%' bgcolor='' valign='top' align='justify' ><b><font color='#FF7373' face='Tahoma' size='2'>"
				+imageLink + "</font></b></td>");
		finalPrintHtml
		.println("<td width='17%' bgcolor='' valign='middle' align='center'><b><font color='#000000' face='Tahoma' size='2'>"
				+totalPass+ "</font></b></td>");
		finalPrintHtml
		.println("<td width='17%' bgcolor='' valign='middle' align='center'><b><font color='#FF7373' face='Tahoma' size='2'>"
				+totalFail+ "</font></b></td>");
		finalPrintHtml
				.println("<td width='17%' bgcolor='' valign='middle' align='center'><b><font color='#FF7373' face='Tahoma' size='2'>"
						+totalSkip+ "</font></b></td>");
		finalPrintHtml
		.println("<td width='24%' bgcolor='' valign='middle' align='center' ><font color='#000000' face='Tahoma' size='2'>"
				+ totalExecutionTime + "</font></td>");	
	}
    
	public void openFinalReportFile() {
		try {
			finalHtmlFile = new FileOutputStream(new File(finalReportPath + "/"+"FinalReport.html"), true);
			finalReportFile = finalReportPath + "/"+"FinalReport.html";
			finalPrintHtml = new PrintStream(finalHtmlFile);
		}catch (Exception ex){
		 ex.printStackTrace();
		}
	}
	
	public void completeFinalReportFileFooter(){
		openFinalReportFile();
		long mEndTime = System.currentTimeMillis();
		finalPrintHtml.println("</tbody>");
		finalPrintHtml.println("</table>");
		finalPrintHtml.println("<br>");
//		finalPrintHtml.println("<table border='0' width='50%' height='30'>");
		finalPrintHtml.println("<table class='tg' border='2' style='border-collapse:collapse;width:100%;'>");
		finalPrintHtml.println("<tbody>");
		finalPrintHtml.println("<tr style='background-color:#92DAEA;text-align:center'>");
		finalPrintHtml.println("<th class='tg-031e' colspan='6'><b><font color='Black' face='Tahoma' size='3'>TestSuites Execution Details</b><br></th>");
		finalPrintHtml.println("</tr>");
		finalPrintHtml.println("<tr style='background-color:#D7E0E1;'>");
		finalPrintHtml.println("<td class='tg-9hbo' style='text-align:center'><b><font color='Black' face='Tahoma' size='2'>Total Test Cases</b></td>");
		finalPrintHtml.println("<td class='tg-9hbo' style='text-align:center'><b><font color='Black' face='Tahoma' size='2'>Passed</b></td>");
		finalPrintHtml.println("<td class='tg-9hbo' style='text-align:center'><b><font color='Black' face='Tahoma' size='2'>Failed</b></td>");
		finalPrintHtml.println("<td class='tg-9hbo' style='text-align:center'><b><font color='Black' face='Tahoma' size='2'>Skipped</b></td>");
		finalPrintHtml.println("<td class='tg-9hbo' style='text-align:center'><b><font color='Black' face='Tahoma' size='2'>Start Time</b></td>");
		finalPrintHtml.println("<td class='tg-9hbo' style='text-align:center'><b><font color='Black' face='Tahoma' size='2'>End Time</b></td>");
		finalPrintHtml.println("<td class='tg-9hbo' style='text-align:center'><b><font color='Black' face='Tahoma' size='2'>Total Execution Time</b></td>");
		finalPrintHtml.println("</td>");
		finalPrintHtml.println("<tr>");
		int total = totalPassedTestCase+totalFailedTestCase;
		finalPrintHtml.println("<td class='tg-yw4l' style='text-align:center'><font color='Black' face='Tahoma' size='2'>"+total+"</td>");
		finalPrintHtml.println("<td class='tg-yw4l' style='text-align:center'><font color='Black' face='Tahoma' size='2'>"+totalPassedTestCase+"</td>");
		finalPrintHtml.println("<td class='tg-baqh' style='text-align:center'><font color='Black' face='Tahoma' size='2'>"+totalFailedTestCase+"</td>");
		finalPrintHtml.println("<td class='tg-baqh' style='text-align:center'><font color='Black' face='Tahoma' size='2'>"+totalSkipTestCase+"</td>");
		finalPrintHtml.println("<td class='tg-yw4l' style='text-align:center'><font color='Black' face='Tahoma' size='2'>"+reportStartTime+"</td>");
		finalPrintHtml.println("<td class='tg-yw4l' style='text-align:center'><font color='Black' face='Tahoma' size='2'>"+JavaWrappers.getCurrentTime("HH:m:ss")+"</td>");
		String totalTimeTaken  = JavaWrappers.getTime(mreportStartTime,mEndTime);
		finalPrintHtml.println("<td class='tg-yw4l' style='text-align:center'><font color='Black' face='Tahoma' size='2'>"+totalTimeTaken+"</td>");
		finalPrintHtml.println("</tr>");
		finalPrintHtml.println("</tbody>");
		finalPrintHtml.println("</table>");
		finalPrintHtml.println("</body>");
		finalPrintHtml.println("</html>");
        finalPrintHtml.close();
	}
}
