package report.custom;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.sql.Driver;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.DriverFactory;
import test.TestNgMethods;
import ui.driverUtils.Drivers;
import utils.ExcelUtils;
import utils.JavaWrappers;


public class MailReport extends ReportsSession{

	private static Logger log = LoggerFactory.getLogger(MailReport.class);

    public static PrintStream printStream;
    public static long mreportStartTime = 0;
    private String reportStartTime = "";
    /**
     * Initializing the UI automation final report file and create the folder "Reports" directory of Project
     * @param folderName
     * @return
     * @throws IOException
     */
	public void initializeMailReport(String folderName) throws IOException{
		reportFolder = JavaWrappers.createDir(reportDir, folderName).getAbsolutePath();
    log.info(reportFolder);
		System.out.println("report path: "+reportFolder);
		createdFolderName = reportFolder.substring(reportFolder.indexOf(folderName), reportFolder.length());
		JavaWrappers.createDir(reportFolder, "Feature").renameTo(new File(reportFolder,"Feature"));
		featurePath = reportFolder +File.separator+"Feature";
		mreportStartTime =  System.currentTimeMillis();
		reportStartTime = JavaWrappers.getCurrentTime("HH:mm:ss");
		new SummaryReport().initializeSummaryReport();
	}

	public void createConsolidateReport() {
		try {
			openFinalReportFile();
			OutputStream outputStream = new FileOutputStream(
					new File(reportFolder + "/" + mailReportName), true);
			int totalPassPercentage = ((totalPassTestCases*100) / totalTestCases);
			String totalTimeTaken = JavaWrappers.getTime(mreportStartTime, System.currentTimeMillis());
			printStream = new PrintStream(outputStream);
			printStream.println("<html>");
			printStream.println("<title>Automation Report</title>");
			printStream.println("<head></head>");
			printStream.println("<body>");
			printStream.println("<TABLE BORDER=0 CELLPADDING=3 CELLSPACING=1 WIDTH=100% BGCOLOR=BLACK>");
			String user = System.getProperty("BUILD_USER_ID") != null && !System.getProperty("BUILD_USER_ID").trim().equalsIgnoreCase("") ? System.getProperty("BUILD_USER_ID").trim() : System.getProperty("user.name");
			String env = System.getProperty("env") != null && !System.getProperty("env").trim().equalsIgnoreCase("") ? System.getProperty("env").trim() : DriverFactory.getEnv().trim();
			String machineName = env.toUpperCase();
			try {
				machineName = InetAddress.getLocalHost().getHostName();
			} catch (Exception ex) {
				//Do nothing;
			}
			printStream.println("<TR><TD WIDTH=90% ALIGN=CENTER BGCOLOR=BLACK><FONT FACE=VERDANA COLOR=White SIZE=3><B>"+DriverFactory.environment.get("orgName")+"</B></FONT></TD></TR>");
			printStream.println("<TR><TD ALIGN=CENTER BGCOLOR=White><FONT FACE=VERDANA COLOR=Black SIZE=3><B>Automation Reporting</B></FONT></TD></TR></TABLE>");
			printStream.println("<TABLE CELLPADDING=3 WIDTH=100%><TR height=30><TD WIDTH=100% ALIGN=CENTER BGCOLOR=WHITE><FONT FACE=VERDANA COLOR=//0073C5 SIZE=2><B>&nbsp; Automation Result : "+new Date()+" on Machine/Env "+machineName+" by user "+user+"</B></FONT></TD></TR><TR HEIGHT=5></TR></TABLE>");
			printStream.println("<br/>");
			printStream.println("<table border=0 cellpadding=3 cellspacing=1 width=100% style=border-collapse:collapse;><tbody>");
			String projectType = DriverFactory.environment.get("projectType");
			if (projectType.equalsIgnoreCase("android") || projectType.equalsIgnoreCase("ios")) {
				printStream.println("<tr><td align=center bgcolor=black ><FONT FACE=VERDANA COLOR=white SIZE=2><B>"+env+"-"+DriverFactory.environment.get("appVersion")+"</B></FONT></td></tr>");
			}
			printStream.println("<tr><td align=center bgcolor=black ><FONT FACE=VERDANA COLOR=white SIZE=2> Total Test cases : "+totalTestCases+"</FONT></td></tr>");
			printStream.println("<tr><td align=center bgcolor=black ><FONT FACE=VERDANA COLOR=white SIZE=2> Pass Percentage : "+totalPassPercentage+"%</FONT></td></tr>");
			printStream.println("<tr><td align=center bgcolor=black ><FONT FACE=VERDANA COLOR=white SIZE=2> Execution time : "+totalTimeTaken+"</FONT></td></tr>");
			printStream.println("</table>");
			printStream.println("<table class='tg' border=2 cellpadding=3 cellspacing=1 width=100% style=border-collapse:collapse;><tbody>");
			//First line of table
			printStream.println("<tr height=20 align=center style='height:15.75pt;font-family:Verdana'>");
			printStream.println("<td class=xl76 rowspan=2 height=40 ><b>Module</b></td>");
			printStream.println("<td rowspan=2 ><b>PASS %</b></td>");
			for (String tag : DriverFactory.tags) {
				printStream.println("<td colspan=3 class=xl80 >" + tag + "</td> ");
			}
			printStream.println("<td colspan=3 class=xl79>TOTAL</td>");
			printStream.println("</tr>");

			// second line of table
			for (String tag : DriverFactory.tags) {
				printStream.println("<td align=center class=xl80 >Pass</td>");
				printStream.println("<td align=center class=xl80 >Fail</td>");
				printStream.println("<td align=center class=xl80 >Skip</td>");
			}
			printStream.println("<td class=xl79 align=center>Pass</td>");
			printStream.println("<td class=xl79 align=center>Fail</td>");
			printStream.println("<td class=xl79 align=center>Skip</td>");
			printStream.println("</tr>");

			// below code for the use for data entry
			String columnStyle ="class=xl66";
			int totalPass = 0;
			int totalFail = 0;
			int totalSkip = 0;
			for (String module : DriverFactory.modules) {
				printStream.println("<tr align=center style='mso-height-source:userset;height:15.75pt;font-family:Arial'>");
				printStream.println("<td " + columnStyle + ">"+module+"</td>");
				long totalModuleTestCases= DriverFactory.results.stream().filter(m->m.get("FeatureName").equals(module)).count();
				String link = DriverFactory.results.stream().filter(m -> m.get("FeatureName").equals(module)).findFirst().get().get("ModuleLink");
				String moduleLink = totalModuleTestCases == 0 ? "#" : link;
				long totalModulePassTestCase = DriverFactory.results.stream().filter(m->m.get("FeatureName").equals(module) && m.get("Status").equals("PASS")).count();
				long percentage = (totalModulePassTestCase * 100) / totalModuleTestCases;
				printStream.println("<td " + columnStyle + "><a style='text-decoration:none;' href=\"" + moduleLink + "\">" + percentage + "%</a></td>");
				for (String tag : DriverFactory.tags) {
					long pass  = DriverFactory.results.stream().filter(m->m.get("FeatureName").equals(module) && m.get("Status").equals("PASS") && m.get("Priority").equals(tag)).count();
					printStream.println("<td " + columnStyle + ">"+pass+"</td>");
					long fail = DriverFactory.results.stream().filter(m->m.get("FeatureName").equals(module) && m.get("Status").equals("FAIL") && m.get("Priority").equals(tag)).count();
					printStream.println("<td " + columnStyle + ">"+fail+"</td>");
					long skip = DriverFactory.results.stream().filter(m->m.get("FeatureName").equals(module) && m.get("Status").equals("SKIP") && m.get("Priority").equals(tag)).count();
					printStream.println("<td " + columnStyle + ">" + skip + "</td>");
				}
				long pass = DriverFactory.results.stream().filter(m->m.get("FeatureName").equals(module) && m.get("Status").equals("PASS")).count();
				totalPass += pass;
				printStream.println("<td " + columnStyle + ">" + pass + "</td>");
				long fail = DriverFactory.results.stream().filter(m->m.get("FeatureName").equals(module) && m.get("Status").equals("FAIL")).count();
				totalFail += fail;
				printStream.println("<td " + columnStyle + ">" + fail + "</td>");
				long skip = DriverFactory.results.stream().filter(m->m.get("FeatureName").equals(module) && m.get("Status").equals("SKIP")).count();
				totalSkip += skip;
				printStream.println("<td " + columnStyle + ">" + skip + "</td>");
				printStream.println("</tr>");
			}
			// last line
			printStream.println("<tr height=20 align=center style='mso-height-source:userset;height:15.75pt;font-family:Arial;background-color:black;color:white'>");
			printStream.println("<td class=xl68 >Total</td>");
			printStream.println("<td class=xl68 >"+totalPassPercentage+"%</td>");
			columnStyle = "class=xl65";
			for (String tag : DriverFactory.tags) {
				long pass = DriverFactory.results.stream().filter(m->m.get("Priority").equals(tag) && m.get("Status").equals("PASS")).count();
				printStream.println("<td "+columnStyle+">"+pass+"</td>");
				long fail = DriverFactory.results.stream().filter(m->m.get("Priority").equals(tag) && m.get("Status").equals("FAIL")).count();
				printStream.println("<td "+columnStyle+">"+fail+"</td>");
				long skip = DriverFactory.results.stream().filter(m->m.get("Priority").equals(tag) && m.get("Status").equals("SKIP")).count();
				printStream.println("<td "+columnStyle+">"+skip+"</td>");
			}
			printStream.println("<td "+columnStyle+">"+totalPass+"</td>");
			printStream.println("<td "+columnStyle+">"+totalFail+"</td>");
			printStream.println("<td "+columnStyle+">"+totalSkip+"</td>");
			printStream.println("</tr></table>");

			printStream.println("</body></html>");
		}catch(Exception e){
			e.printStackTrace();
		}

	}





	


	private void createUIFinalReportHeader(String reportHeaderName){
		try {
			openFinalReportFile();
			// creating html table
			String hStyle = "style='background-color:#768f90; padding:15px 0; border:1px solid'";
			printStream.println("<html>");
			printStream.println("<title>Automation Report</title>");
			printStream.println("<head></head>");
			printStream.println("<body>");
			
			// mentioning Suite name and Application on top of the table
			printStream.println("<h2 align='center'>");
			printStream.println("<span style='color:black'><u>"+reportHeaderName+ " Report</u></span>");
//			System.out.println("In final report "+DataMaps.SuiteConfigValues.get("url"));
			String platform = DriverFactory.environment.get("driverType");
			
//			if(platform.equalsIgnoreCase("Android")) {
				
//				System.out.println("In final report "+DataMaps.SuiteConfigValues.get("BROWSER_NAME_Android"));
				printStream.println("<BR>");
				printStream.println("<h3 align='center'>");
				printStream.println("<span style='color:black'><b>Env</b></span>");
				printStream.println("<span style='color:black'>"+" : "+ JavaWrappers.toCamelCase(DriverFactory.environment.get("environment"))+"</span>");
        printStream.println("<BR>");
			  printStream.println("<span style='color:black'><b>AppVersion</b></span>");
				printStream.println("<span style='color:black'>"+" : "+ DriverFactory.environment.get("appVersion")+"</span>");
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
			
			printStream.println("<BR>");
			printStream.println("<BR>");
			//creating table for TestCases and their results
//			finalPrintHtml.println("<table border='0' width='100%' height='45'>");
			printStream.println("<table class='tg' border='2' style='font-weight:200;border-collapse:collapse;width:100%;'>");
			printStream.println("<tbody>");
			
			//Setting the header for the table
			printStream.println("<tr bgcolor='#92DAEA'>");
			printStream.println("<td width='25%' valign='middle' align='center'>");
			printStream.println("<b><font color='Black' face='Tahoma' size='4'>Module</font></b>");
			printStream.println("</td>");
			printStream.println("<td width='17%' valign='middle' align='center'>");
			printStream.println("<b><font color='Black' face='Tahoma' size='4'>Pass</font></b>");
			printStream.println("</td>");
//			finalPrintHtml.println("</td>");
			printStream.println("<td width='17%' valign='middle' align='center'>");
			printStream.println("<b><font color='Black' face='Tahoma' size='4'>Fail</font></b>");
			printStream.println("</td>");
			printStream.println("<td width='17%' valign='middle' align='center'>");
			printStream.println("<b><font color='Black' face='Tahoma' size='4'>Skip</font></b>");
			printStream.println("</td>");
			printStream.println("<td width='24%' valign='middle' align='center'>");
			printStream.println("<b><font color='Black' face='Tahoma' size='4'>TimeTaken</font></b>");
			printStream.println("</td>");
			printStream.println("</tr>");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
    
	
	public void addSuiteFileInto_FinalReport(int totalPass, int totalFail, int totalSkip,String featureName,String partialResultFileLink, String totalExecutionTime) {
		System.out.println("Adding Completed suite  "+featureName);
		String dStyle = "style='background-color:#f8f8e9; padding:10px 5px; border:1px solid'";
		totalPassTestCases += totalPass;
		totalFailTestCases +=totalFail;
		totalSkipTestCases +=totalSkip;

		String imageLink = "<a style='text-decoration:none;' href='" +partialResultFileLink +"'>"+featureName+" </a>";

		printStream.println("<tr>");
		printStream
		.println("<td width='25%' bgcolor='' valign='top' align='justify' ><b><font color='#FF7373' face='Tahoma' size='2'>"
				+imageLink + "</font></b></td>");
		printStream
		.println("<td width='17%' bgcolor='' valign='middle' align='center'><b><font color='#000000' face='Tahoma' size='2'>"
				+totalPass+ "</font></b></td>");
		printStream
		.println("<td width='17%' bgcolor='' valign='middle' align='center'><b><font color='#FF7373' face='Tahoma' size='2'>"
				+totalFail+ "</font></b></td>");
		printStream
				.println("<td width='17%' bgcolor='' valign='middle' align='center'><b><font color='#FF7373' face='Tahoma' size='2'>"
						+totalSkip+ "</font></b></td>");
		printStream
		.println("<td width='24%' bgcolor='' valign='middle' align='center' ><font color='#000000' face='Tahoma' size='2'>"
				+ totalExecutionTime + "</font></td>");	
	}
    
	public void openFinalReportFile() {
		try {
			OutputStream finalHtmlFile = new FileOutputStream(new File(reportFolder + File.separator+mailReportName), true);
			mailReportFile = reportFolder + File.separator+mailReportName;
			printStream = new PrintStream(finalHtmlFile);
		}catch (Exception ex){
		 ex.printStackTrace();
		}
	}
	
	public void completeMailReportFileFooter(){
		long mEndTime = System.currentTimeMillis();
		printStream.println("</tbody>");
		printStream.println("</table>");
		printStream.println("<br>");
//		finalPrintHtml.println("<table border='0' width='50%' height='30'>");
		printStream.println("<table class='tg' border='2' style='border-collapse:collapse;width:100%;'>");
		printStream.println("<tbody>");
		printStream.println("<tr style='background-color:#92DAEA;text-align:center'>");
		printStream.println("<th class='tg-031e' colspan='7'><b><font color='Black' face='Tahoma' size='3'>Execution Details</b><br></th>");
		printStream.println("</tr>");
		printStream.println("<tr style='background-color:#D7E0E1;'>");
		printStream.println("<td class='tg-9hbo' style='text-align:center'><b><font color='Black' face='Tahoma' size='2'>Total Test Cases</b></td>");
		printStream.println("<td class='tg-9hbo' style='text-align:center'><b><font color='Black' face='Tahoma' size='2'>Passed</b></td>");
		printStream.println("<td class='tg-9hbo' style='text-align:center'><b><font color='Black' face='Tahoma' size='2'>Failed</b></td>");
		printStream.println("<td class='tg-9hbo' style='text-align:center'><b><font color='Black' face='Tahoma' size='2'>Skipped</b></td>");
		printStream.println("<td class='tg-9hbo' style='text-align:center'><b><font color='Black' face='Tahoma' size='2'>Start Time</b></td>");
		printStream.println("<td class='tg-9hbo' style='text-align:center'><b><font color='Black' face='Tahoma' size='2'>End Time</b></td>");
		printStream.println("<td class='tg-9hbo' style='text-align:center'><b><font color='Black' face='Tahoma' size='2'>Total Execution Time</b></td>");
		printStream.println("</td>");
		printStream.println("<tr>");
		int total = totalPassTestCases+totalFailTestCases;
		printStream.println("<td class='tg-yw4l' style='text-align:center'><font color='Black' face='Tahoma' size='2'>"+total+"</td>");
		printStream.println("<td class='tg-yw4l' style='text-align:center'><font color='Black' face='Tahoma' size='2'>"+totalPassTestCases+"</td>");
		printStream.println("<td class='tg-baqh' style='text-align:center'><font color='Black' face='Tahoma' size='2'>"+totalFailTestCases+"</td>");
		printStream.println("<td class='tg-baqh' style='text-align:center'><font color='Black' face='Tahoma' size='2'>"+totalSkipTestCases+"</td>");
		printStream.println("<td class='tg-yw4l' style='text-align:center'><font color='Black' face='Tahoma' size='2'>"+reportStartTime+"</td>");
		printStream.println("<td class='tg-yw4l' style='text-align:center'><font color='Black' face='Tahoma' size='2'>"+JavaWrappers.getCurrentTime("HH:m:ss")+"</td>");
		String totalTimeTaken  = JavaWrappers.getTime(mreportStartTime,mEndTime);
		printStream.println("<td class='tg-yw4l' style='text-align:center'><font color='Black' face='Tahoma' size='2'>"+totalTimeTaken+"</td>");
		printStream.println("</tr>");
		printStream.println("</tbody>");
		printStream.println("</table>");
		printStream.println("</body>");
		printStream.println("</html>");
        printStream.close();
	}
}
