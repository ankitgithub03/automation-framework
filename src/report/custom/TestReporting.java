package report.custom;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import org.openqa.selenium.logging.LogEntry;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

import io.restassured.response.Response;
import test.DriverFactory;
import ui.app.android.AndroidUtility;
import ui.driverUtils.DriverActionsUtils;
import ui.driverUtils.Drivers;
import utils.HashMapNew;
import utils.JavaWrappers;


@Data
public class TestReporting extends FeatureReporting {
	
	private PrintStream printHtml, printStackTraceLog,printCrashLog,printProxyLog;
	private String testCaseName = "";
	private String stackTraceLogFileName ="";
	private String crashLogFileName ="";
	private String proxyFileName="";
	private String testReportFolder ="";
	private String testCaseReportPath ="";
	private int apiRow = 0;
	private String featureName ="";
	private String gifFolder ="";
	private String browserNetworkLogFile ="";
	private String browserNetworkLogName ="";
	private String browserNetworkLogJsonFile ="";
	private String browserNetworkLogJsonName ="";
	protected HashMapNew sTest = null;

	public TestReporting(HashMapNew sTestDetails) throws IOException {
		this.sTest = DriverFactory.getWholeTestDetails();
	}
	
	public void initializeTestReport(String featureName, String testName) throws IOException {
		this.featureName = featureName;
		this.testReportFolder = DriverFactory.getFeatureReport(featureName).getFeatureReportFolder()+File.separator+"TestCases";
    JavaWrappers.createDir(testReportFolder, testName+"_gif").renameTo(new File(testReportFolder, testName+"_gif"));
    this.gifFolder = testReportFolder+File.separator+testName+"_gif";
		this.testCaseName = testName;
		testCaseReportPath =openFile();
		header();
	}
	
	public void initializeApiTestReport(String featureName,String testName) throws IOException {
		this.testReportFolder = DriverFactory.getFeatureReport(featureName).getFeatureReportFolder()+File.separator+"TestCases";
		this.testCaseName = testName;
		testCaseReportPath = openFile();
		header();
	}
	
	
	private String openFile() {
		String fileName = "";
		try {
			fileName = this.testCaseName+"_"+ JavaWrappers.getCurrentTime("HH_mm_ss")+".html";
			stackTraceLogFileName = this.testCaseName+"_stackTraces_"+JavaWrappers.getCurrentTime("HH_mm_ss")+".txt";
			crashLogFileName = this.testCaseName+"_crashLog_"+JavaWrappers.getCurrentTime("HH_mm_ss")+".txt";
			String stacktraceLogFile = this.testReportFolder+File.separator+"Logs"+File.separator+ stackTraceLogFileName;
			String crashLogFile = this.testReportFolder+File.separator+"Logs"+File.separator+ crashLogFileName;
			proxyFileName = this.testCaseName+"_log_"+JavaWrappers.getCurrentTime("HH_mm_ss")+".txt";
			String proxyLogFile = this.testReportFolder+File.separator+"Logs"+File.separator+ proxyFileName;
			String testCaseFile = this.testReportFolder+File.separator+fileName;
			OutputStream htmlFile = new FileOutputStream(new File(testCaseFile), true);
			printHtml = new PrintStream(htmlFile);
			OutputStream stackTraceLogFileObj = new FileOutputStream(new File(stacktraceLogFile),true);
			OutputStream crashLogFileObj = new FileOutputStream(new File(crashLogFile),true);
			OutputStream proxyFile = new FileOutputStream(new File(proxyLogFile),true);
			  printStackTraceLog = new PrintStream(stackTraceLogFileObj);
			  printCrashLog = new PrintStream(crashLogFileObj);
				printProxyLog = new PrintStream(proxyFile);
				if(DriverFactory.environment.get("projectType").equalsIgnoreCase("web")){
					browserNetworkLogName =testCaseName+JavaWrappers.getCurrentTime("HH_mm_ss")+".har";
					browserNetworkLogFile =this.testReportFolder+File.separator+"Logs"+File.separator+ browserNetworkLogName;
					browserNetworkLogJsonName =testCaseName+JavaWrappers.getCurrentTime("HH_mm_ss")+"1.json";
					browserNetworkLogJsonFile =this.testReportFolder+File.separator+"Logs"+File.separator+ browserNetworkLogName;
				}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return fileName;
	}
	
	
	public void addAppCrashLog() {
		String crashLog = new AndroidUtility().getCrashLog(DriverFactory.getTestDetails("udid"));
		if(!crashLog.isEmpty()) {
		String[] logs = crashLog.split("\n");
		for(String s : logs) {
			printCrashLog.append(s);
			printCrashLog.append(System.getProperty("line.separator"));
		}
		printCrashLog.close();
			String filePath = "../TestCases/Logs/"+crashLogFileName;
			DriverFactory.setTestDetails("crashLogsPath",filePath);
		}
	}

	public void writeStackTrace(StackTraceElement[] trace){
		for(int i = 0 ; i < trace.length; i++){
			printStackTraceLog.append(trace[i].toString());
			printStackTraceLog.append(System.getProperty("line.separator"));
		}
		printStackTraceLog.close();
		String filePath = "../TestCases/Logs/"+ stackTraceLogFileName;
		DriverFactory.setTestDetails("stackTracePath",filePath);
	}

	public void addLogFileInReport(){
		completeTestReportTable();
		printHtml.println("<br>");
		printHtml.println("<table width=100%>");
		printHtml.println("<tr>");
		if(!DriverFactory.getTestDetails("videoGifPath").isEmpty()){
			printHtml.println("<td align=center><a HREF='" + DriverFactory.getTestDetails("videoGifPath") + "'><FONT FACE=VERDANA COLOR=BLACK SIZE=2>VIDEO GIF</FONT></A></TD>");
		}
		if(!DriverFactory.getTestDetails("stackTracePath").isEmpty()){
			printHtml.println("<td align=center><a HREF='" + DriverFactory.getTestDetails("stackTracePath") + "'><FONT FACE=VERDANA COLOR=BLACK SIZE=2>SYS TRACE</FONT></A></TD>");
		}
		if(!DriverFactory.getTestDetails("crashLogsPath").isEmpty()){
			printHtml.println("<td align=center><a HREF='" + DriverFactory.getTestDetails("crashLogsPath") + "'><FONT FACE=VERDANA COLOR=BLACK SIZE=2>CRASH LOGS</FONT></A></TD>");
		}
		if(!DriverFactory.getTestDetails("browserNetworkLogs").isEmpty()){
			printHtml.println("<td align=center><a HREF='" + DriverFactory.getTestDetails("browserNetworkLogs") + "'><FONT FACE=VERDANA COLOR=BLACK SIZE=2>NETWORK LOGS</FONT></A></TD>");
		}
    printHtml.println("</tr></table>");
	}

	private void completeTestReportTable(){
		printHtml.println("</tbody>");
		printHtml.println("</table>");
	}



	private List<LogEntry> orderLogEntries(List<LogEntry> logEntries) {
		List<LogEntry> newEntries = new ArrayList<>();
		LogEntry tempEntry;
		for (LogEntry entry : logEntries) {
			tempEntry = new LogEntry(entry.getLevel(), entry.getTimestamp(), entry.getMessage() + "\n");
			String level = tempEntry.getLevel().getName();
//			if(level.equalsIgnoreCase("SEVERE") || level.equalsIgnoreCase("WARNING") | level.equalsIgnoreCase("DEBUG")) {
//				if(entry.getMessage().contains("OkHttp  :")) {
					newEntries.add(tempEntry);
					System.out.println(tempEntry.getLevel().intValue()+" "+tempEntry.getLevel().getName()+" : "+tempEntry.getMessage());
//				}
//			}
		}
		return newEntries;
	}

	private String  createBackendLogFile(){
		String name = "";
    try {
			if (sTest.containsKey("RAW_RESPONSE") && !sTest.get("RAW_RESPONSE").trim().equalsIgnoreCase("")) {
				name = sTest.get("API_NAME") + "_requestResponse" + System.currentTimeMillis() + ".html";
				String path = getTestReportFolder()+File.separator+"ScreenShot"+ File.separator + name;
				FileOutputStream fileOutputStream = new FileOutputStream(path, true);
				new PrintStream(fileOutputStream).println(
						"<HTML><BODY><TABLE ALIGN=CENTER style=\"table-layout: fixed; width: 100%\" BORDER=1><THEAD><TR><TH WIDTH=50% ALIGN=LEFT>REQUEST</TH><TH WIDTH=50% ALIGN=LEFT>RESPONSE</TH></TR></THEAD><TR VALIGN=TOP><TD WIDTH=50% style=\"word-wrap: break-word\" ALIGN=LEFT>");
				new PrintStream(fileOutputStream).println(sTest.get("RAW_REQUEST")
						+ "</TD><TD WIDTH=50% style=\"word-wrap: break-word\" ALIGN=LEFT>");
				new PrintStream(fileOutputStream)
						.println(sTest.get("RAW_RESPONSE") + "</TD></TR></TABLE>");
				new PrintStream(fileOutputStream).println("</BODY></HTML>");
				fileOutputStream.close();
				sTest.remove("API_NAME");
				sTest.remove("RAW_RESPONSE");
				sTest.remove("RAW_REQUEST");
			}
		}catch (Exception e){
    	e.printStackTrace();
		}
    return name;
	}

	private String createLink(String status , String nameOfScreenShot){
		String link = "";
		if(!nameOfScreenShot.isEmpty()){
			if(status.equalsIgnoreCase("pass")){
				link = "<a href=\"" + "../TestCases/ScreenShot/" + nameOfScreenShot + "\">" + status + "</a>";
			}
			else if(status.equalsIgnoreCase("Fail")){
				link = "<a href=\"" + "../TestCases/ScreenShot/" + nameOfScreenShot + "\"><font color='#FF7373'>" + status + "</a>";
			}
		}
		else{
			link = status;
		}
		return link;
	}

	public void log(String taskPerformed, String info, String strPassFail){
			String value =  JavaWrappers.toCamelCase(strPassFail);
			String nameOfScreenShot ="";
			String imgLink = "";
			if(!DriverFactory.environment.get("projectType").equalsIgnoreCase("backend") && !sTest.containsKey("RAW_RESPONSE") && sTest.get("RAW_RESPONSE").trim().equalsIgnoreCase("")){
				if (strPassFail.equalsIgnoreCase("FAIL") || strPassFail.equalsIgnoreCase("WARNING")
						|| strPassFail.toLowerCase().startsWith("warn") || strPassFail
						.equalsIgnoreCase("PASS")) {
					nameOfScreenShot = DriverActionsUtils.getScreenShot().getName();
				}
			}
			else{
				nameOfScreenShot = createBackendLogFile();
			}
					if (value.equalsIgnoreCase("PASS") && ((nameOfScreenShot.contains("Image_")) || (nameOfScreenShot.contains("_requestResponse")))) {
						imgLink =createLink(value,nameOfScreenShot);
					} else if (nameOfScreenShot.equalsIgnoreCase("UnableToCapture")) {
						System.out.println("Unable to capture screenshot");
						value = "UnableToCapture";
						imgLink = "UnableToCapture";
					} else if (nameOfScreenShot.equalsIgnoreCase("secureFlag")) {
						value = "secureFlag";
						imgLink = "secureFlag";
					} else if (value.equalsIgnoreCase("FAIL")){
						imgLink =createLink(value,nameOfScreenShot);
					}
					else
						imgLink = value;
			printHtml.append("<tr>");
			printHtml
			.append("<td width='35%' bgcolor='' valign='top' align='justify' ><font color='#000000' face='VERDANA' size='2'>"
					+ taskPerformed + "</font></td>");
			printHtml
			.append("<td width='42%' bgcolor='' valign='top' align='justify' ><font color='#000000' face='VERDANA' size='2'>"
					+ info + "</font></td>");
			if (strPassFail.equalsIgnoreCase("Pass")) {
				printHtml
						.append(
								"<td width='10%' bgcolor='' valign='middle' align='center'><b><font color='#000000' face='VERDANA' size='2'>"
										+ imgLink + "</font></b></td>");
			}
			else if(strPassFail.equalsIgnoreCase("done")){
				printHtml
						.append(
								"<td width='10%' bgcolor='' valign='middle' align='center'><font color='#000000' face='VERDANA' size='2'>Done</font></td>");
			}
			else if (strPassFail.equalsIgnoreCase("FAIL")) {
				printHtml
				.append("<td width='10%' bgcolor='' valign='middle' align='center'><b><font color='#FF7373' face='VERDANA' size='2'>"
						+imgLink+ "</font></b></td>");
//				throw new Exception("failures came");
			} else if (strPassFail.equals("")){
				printHtml
				.append("<td width='10%' bgcolor='' valign='middle' align='center'><font color='#FF7373' face='VERDANA' size='2'></font></td>");
			}
			else {
				printHtml
				.append("<td width='10%' bgcolor='' valign='middle' align='center'><b><font color='#FF7373' face='VERDANA' size='2'>"
						+imgLink+"</font></b></td>");
			}
			printHtml
			.append("<td width='13%' bgcolor='' valign='middle' align='center' ><font color='#000000' face='VERDANA' size='2'>"
					+ JavaWrappers.getCurrentTime("HH:mm:ss") + "</font></td>");	
			printHtml.append("</tr>");
//			if (strPassFail.toUpperCase().equals("FAIL")){
//				throw new Exception("failures came");
//			}
	}
	
	
	
	/*
	 * API test steps reporting file
	 * 
	 */
	public synchronized void log(String requestDetails, Response rs, String strPassFail) throws Exception {
//		try {
			String value =  strPassFail.toUpperCase();
			String imgLink = value;
			printHtml.append("<tr>");
			requestDetails = requestDetails.replaceAll("\n", "<br/>");
			printHtml
			.append("<td width='35%' bgcolor='' valign='top' align='' ><font color='#000000' face='VERDANA' size='2'>"
					+ requestDetails + "</font></td>");
			if (rs == null){
				printHtml
				.append("<td width='42%' bgcolor='' valign='top' align='justify' ><font color='#000000' face='VERDANA' size='2'>"
						+ "<b>Api Response is NULL</b>");
				printHtml.append("</font></td>");
			}
			else{
			printHtml
			.append("<td width='42%' bgcolor='#D3D3D3' valign='top' align='justify' ><font color='#000000' face='VERDANA' size='2'>"
					+ "<b>Api Response_"+rs.getStatusCode()+", Time taken: "+rs.getTime()+" millisec</b>");
			formatResponse(rs);
			printHtml.append("</font></td>");
			}
			if (strPassFail.toUpperCase().equals("PASS")) {
				printHtml
				.append("<td width='10%' bgcolor='' valign='middle' align='center'><b><font color='#000000' face='VERDANA' size='2'>"
						+imgLink+ "</font></b></td>");
			} else if (strPassFail.toUpperCase().equals("FAIL")) {
				printHtml
				.append("<td width='10%' bgcolor='' valign='middle' align='center'><b><font color='#FF7373' face='VERDANA' size='2'>"
						+imgLink+ "</font></b></td>");
			} else {
				printHtml
				.append("<td width='10%' bgcolor='' valign='middle' align='center'><b><font color='#FF7373' face='VERDANA' size='2'>"
						+imgLink+"</font></b></td>");
			}
			printHtml
			.append("<td width='13%' bgcolor='' valign='middle' align='center' ><font color='#000000' face='VERDANA' size='2'>"
					+ JavaWrappers.getCurrentTime("HH:mm:ss") + "</font></td>");	
			printHtml.append("</tr>");
//			if (strPassFail.toUpperCase().equals("FAIL")){
//				throw new Exception("failures came");
//			}
	}
	
	
	
	
	/*
	 * API test steps reporting file
	 * 
	 */
	public synchronized void addApiTestSteps(String taskPerformed, String info, String strPassFail) throws Exception {
		try {
			String value =  strPassFail.toUpperCase();
			String imgLink = "";
			imgLink = value;
			printHtml.append("<tr>");
			printHtml
			.append("<td width='35%' bgcolor='' valign='top' align='justify' ><font color='#000000' face='VERDANA' size='2'>"
					+ taskPerformed + "</font></td>");
			printHtml
			.append("<td width='42%' bgcolor='' valign='top' align='justify' ><font color='#000000' face='VERDANA' size='2'>"
					+ info + "</font></td>");
			if (strPassFail.toUpperCase().equals("PASS")) {
				printHtml
				.append("<td width='10%' bgcolor='' valign='middle' align='center'><b><font color='#000000' face='VERDANA' size='2'>"
						+imgLink+ "</font></b></td>");
			} else if (strPassFail.toUpperCase().equals("FAIL")) {
				printHtml
				.append("<td width='10%' bgcolor='' valign='middle' align='center'><b><font color='#FF7373' face='VERDANA' size='2'>"
						+imgLink+ "</font></b></td>");
			} else {
				printHtml
				.append("<td width='10%' bgcolor='' valign='middle' align='center'><b><font color='#FF7373' face='VERDANA' size='2'>"
						+imgLink+"</font></b></td>");
			}
			printHtml
			.append("<td width='13%' bgcolor='' valign='middle' align='center' ><font color='#000000' face='VERDANA' size='2'>"
					+ JavaWrappers.getCurrentTime("HH:mm:ss") + "</font></td>");	
			printHtml.append("</tr>");

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (strPassFail.toUpperCase().equals("FAIL")){
			throw new Exception("failures came");
		}
	}
	
	public void formatResponse(Response rs){
		try {
			String json_String_to_print = rs.getBody().asString();
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			JsonParser jp = new JsonParser();
			try{
				json_String_to_print = gson.toJson(jp.parse(json_String_to_print));
			}catch (Exception e) {
				json_String_to_print = rs.getStatusLine();
			}
			if (json_String_to_print == null || json_String_to_print.isEmpty()){
				json_String_to_print =" ";
			}
//			if (json_String_to_print.length() > 10){
			printHtml.append("<br/><button id=\"expandCollapse"+apiRow+"\" onclick=\"toggle_visibility('response"+apiRow+"','expandCollapse"+apiRow+"');\">+</button>"+
			"<div id=\"response"+apiRow+"\" style=\"display:none;\">"+
			json_String_to_print+
			"</div>");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		apiRow++;
	}

	
	
	
	
	public void addTestSteps(String taskPerformed, String info, String strPassFail, boolean screenshot) {
		boolean snapshotPermitted = DriverFactory.environment.get("ScreenshotOnFailure").equalsIgnoreCase("true");
		try {
			String value =  strPassFail.toUpperCase();
			String nameOfScreenShot ="";
			String imgLink = "";
//			System.out.println(ts.getClass().getName());
//			if (taskPerformed.equalsIgnoreCase("Completed Test") && strPassFail.equalsIgnoreCase("FAIL") && ts.getBrowserConsoleLogs()){
//			}
			if((snapshotPermitted || strPassFail.equalsIgnoreCase("FAIL")) && screenshot) {
				nameOfScreenShot = ""; //captureScreenShot(ts);
				if(nameOfScreenShot.equalsIgnoreCase("UnableToCapture")) {
					System.out.println("Unable to capture screenshot");
					value = "UnableToCapture";
	                  // adding this scenario when driver is in TIME OUT state
                    strPassFail = "UnableToCapture";
				}
				else {
//				imgLink = "<a href=\"" + "../TestCase/ScreenShot/"+ nameOfScreenShot + "\">"+value+"</a>";
				imgLink = "<a href=\"" + "../TestCases/ScreenShot/"+ nameOfScreenShot + "\">"+value+"</a>";
				}
			}
			else {
				imgLink = value;
			}
//			System.out.println("Name of screenshot: "+nameOfScreenShot);
			printHtml.append("<tr>");

			printHtml
			.append("<td width='35%' bgcolor='' valign='top' align='justify' ><font color='#000000' face='VERDANA' size='2'>"
					+ taskPerformed + "</font></td>");
			printHtml
			.append("<td width='42%' bgcolor='' valign='top' align='justify' ><font color='#000000' face='VERDANA' size='2'>"
					+ info + "</font></td>");
			if (strPassFail.toUpperCase().equals("PASS")) {
				printHtml
				.append("<td width='18%' bgcolor='' valign='middle' align='center'><b><font color='#000000' face='VERDANA' size='2'>"
						+imgLink+ "</font></b></td>");
			} else if (strPassFail.toUpperCase().equals("FAIL")) {
				printHtml
				.append("<td width='10%' bgcolor='' valign='middle' align='center'><b><font color='#FF7373' face='VERDANA' size='2'>"
						+imgLink+ "</font></b></td>");
			} else {
				printHtml
				.append("<td width='10%' bgcolor='' valign='middle' align='center'><b><font color='#FF7373' face='VERDANA' size='2'>"
						+imgLink+"</font></b></td>");
			}
			printHtml
			.append("<td width='13%' bgcolor='' valign='middle' align='center' ><font color='#000000' face='VERDANA' size='2'>"
					+ JavaWrappers.getCurrentTime("HH:mm:ss") + "</font></td>");	
			printHtml.append("</tr>");

		} catch (Exception ex) {
			System.out.println("Error while reporting: "+ex.getMessage());
			ex.printStackTrace();
		}
	}
	
	
	

	public void header() {
		try {
			printHtml.println("</table>");
			printHtml.println("<html>");
			printHtml.println("<title> Test Case Report </title>");
			printHtml.println("<head></head>");
			printHtml.println("<body>");
			printHtml.println("<script type=\"text/javascript\">"+

    "function toggle_visibility(id,id1) {"+
    "var e = document.getElementById(id);"+


       "if(e.style.display == 'block') {"+
       "document.getElementById(id1).innerHTML = \"+\";"+
       "e.style.display = 'none';"+

       "}"+
       "else {"+
       "document.getElementById(id1).innerHTML = \"-\";"+
       "e.style.display = 'block';"+

       "}"+
       "}"+
					"</script>");
			String driverType = DriverFactory.environment.get("driverType");
			printHtml.println("<br>");
			printHtml.println("<TABLE BORDER=2 CELLPADDING=2 CELLSPACING=1 style='border-collapse:collapse;width:100%;word-break: break-all'>");
			printHtml.println("<tr><td BGCOLOR='White'=20%><FONT FACE=VERDANA COLOR=BLACK SIZE=2><B>Module </B></FONT></td>");
			printHtml.println("<td COLSPAN=8 BGCOLOR='White'><FONT FACE=VERDANA COLOR=BLACK SIZE=2>" + featureName + "</FONT></td></tr>");
			printHtml.println("<tr><td BGCOLOR='White'=20%><FONT FACE=VERDANA COLOR=BLACK SIZE=2><B>Execution on </B></FONT></td>");
			if(driverType.equalsIgnoreCase("android") || driverType.equalsIgnoreCase("ios")) {
				printHtml.println("<td COLSPAN=8 BGCOLOR='White'><FONT FACE=VERDANA COLOR=BLACK SIZE=2>" + DriverFactory.getTestDetails("deviceName")+"/"+DriverFactory.getTestDetails("deviceOsVersion")  + "</FONT></td></tr>");
			}
			else{
				printHtml.println("<td COLSPAN=8 BGCOLOR='White'><FONT FACE=VERDANA COLOR=BLACK SIZE=2><B>" + JavaWrappers.toCamelCase(driverType)+ "</B></FONT></td></tr>");
			}
			printHtml.println("<tr><td BGCOLOR='White'=20%><FONT FACE=VERDANA COLOR=BLACK SIZE=2><B>Test Case </B></FONT></td>");
			printHtml.println("<td COLSPAN=8 BGCOLOR='White'><FONT FACE=VERDANA COLOR=BLACK SIZE=2>" + DriverFactory.getTestDetails("testCaseName") + "</FONT></td></tr>");
			printHtml.println("</TABLE><BR/>");
//			printHtml.println("<br>");
//			printhtml.println("<table border='0' width='100%' height='47'>");
			printHtml.println("<table class='tg' border='2' style='border-collapse:collapse;width:100%;word-break: break-all'>");
			printHtml.println("<tr bgcolor='#92DAEA'>");
			printHtml
			.println("<td width='35%' bgcolor='' align='center'><b><font color='#000000' face='VERDANA' size='2'>Steps</font></b></td>");
			printHtml
			.println("<td width='42%' bgcolor=''align='center'><b><font color='#000000' face='VERDANA' size='2'>Info</font></b></td>");
			printHtml
			.println("<td width='10%' bgcolor='' align='center'><b><font color='#000000' face='VERDANA' size='2'>Result</font></b></td>");
			printHtml
			.println("<td width='13%' bgcolor='' align='center'><b><font color='#000000' face='VERDANA' size='2'>Time</font></b></td>");
			printHtml.println("</tr>");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void completeReport(){
       printHtml.close();
	}

}
