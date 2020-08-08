package report.custom;

import java.io.File;
import java.io.FileOutputStream;
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
import ui.driverUtils.Drivers;
import utils.JavaWrappers;


@Data
public class TestReporting extends FeatureReporting {
	
	private OutputStream htmlFile, logfile,proxyFile;
	private PrintStream printHtml, printLog,printProxyLog;
	private String testCaseName = "";
	private String logFileName ="";
//	private String debugLogFileName = "";
	private String proxyFileName="";
	private String testReportFolder ="";
	private String testCaseReportPath ="";
	private int apiRow = 0;
	
	public void initializeTestReport(String testName){
//		this.testCasePath = JavaWrappers.createDir(testSuitePath, testName).getAbsolutePath();
//		this.testReport = ts.getIndividulaSuiteReportFolder()+File.separator+"TestCases";
		this.testReportFolder = DriverFactory.getFeatureReporting().getFeatureReportFolder()+File.separator+"TestCases";
		this.testCaseName = testName;
		String name = openFile();
		header();
	}
	
	public String initializeApiTestReport(String testName){
//		this.testCasePath = JavaWrappers.createDir(testSuitePath, testName).getAbsolutePath();
		this.testReportFolder = DriverFactory.getFeatureReporting().getFeatureReportFolder()+File.separator+"TestCases";
		this.testCaseName = testName;
		testCaseReportPath = openFile();
		header();
		return testCaseReportPath;
	}
	
	
	private String openFile() {
		String fileName = "";
		try {
			fileName = this.testCaseName+"_"+ JavaWrappers.getCurrentTime("HH_mm_ss")+".html";
			logFileName = this.testCaseName+"_"+JavaWrappers.getCurrentTime("HH_mm_ss")+".txt";
//			proxyFileName = this.testCaseName+"_"+JavaWrappers.getCurrentTime("HH_mm_ss")+".har";
			String logFile = this.testReportFolder+File.separator+"Logs"+File.separator+ logFileName;
			proxyFileName = this.testCaseName+"_log_"+JavaWrappers.getCurrentTime("HH_mm_ss")+".txt";
			String proxyLogFile = this.testReportFolder+File.separator+"Logs"+File.separator+ proxyFileName;
			String testCaseFile = this.testReportFolder+File.separator+fileName;
			htmlFile = new FileOutputStream(new File(testCaseFile), true);
			printHtml = new PrintStream(htmlFile);
//			if (CommonDataMaps.masterConfigValues.get("consoleLogs").equalsIgnoreCase("true")){
//				String logPath = this.testReport+File.separator+"Logs";
				logfile = new FileOutputStream(new File(logFile),true);
				proxyFile = new FileOutputStream(new File(proxyLogFile),true);
				printLog = new PrintStream(logfile);
				printProxyLog = new PrintStream(proxyFile);
//				System.setOut(printlog);
//				System.setErr(printlog);
//			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return fileName;
	}
	
	
	public void addAppCrashLog() {
		getHttpLogs();
		String fileLink = "";
		boolean crashFound = false;
		String crashLog = new AndroidUtility().getCrashLog(Drivers.getUdid());
		if(!crashLog.isEmpty()) {
			crashFound = true;
		}
		String[] logs = crashLog.split("\n");
		for(String s : logs) {
			printLog.append(s);
			printLog.append(System.getProperty("line.separator"));
		}
		printLog.close();
		if(crashFound) {
			fileLink = "<a href=\"" + "../TestCases/Logs/"+ logFileName+"\"><b>CrashLog</b></a>";
			printHtml.println("<br>");
			printHtml.println("<h3 align='center'> <span style=\"color:blue\">"+fileLink+"</span> </h3>");
			printHtml.println("<br>");
		}
		completeReport();
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
	
	
	private void getHttpLogs() {
		printHtml.println("</tbody>");
		printHtml.println("</table>");
		/*
		
		List<String> logs = ts.getAdbHttpLogs().getAdbLogs();
		//		List<LogEntry> logEntries = ts.getDriver().manage().logs().get("logcat").filter(Level.ALL);
		//		List<LogEntry> logEntries = ts.getDriver().manage().logs().get("logcat").getAll();
		//		logEntries = orderLogEntries(logEntries);
		//		if(!logEntries.isEmpty()) {
		//			for (LogEntry entry : logEntries) {
		//				printProxyLog.append(entry.getMessage());
		//			}
		//			printProxyLog.close();
		//			String fileLink = "<a href=\"" + "../TestCases/Logs/"+ proxyFileName+"\"><b>ProxyLogs</b></a>";
		//			printhtml.println("<br>");
		//			printhtml.println("<h3 align='center'> <span style=\"color:blue\">"+fileLink+"</span> </h3>");
		//			//		printhtml.println("<br>");
		//			//		printhtml.println("<br>");
		//		}
		if(!logs.isEmpty()) {
			for(String text : logs) {
				printProxyLog.append(text);
				printProxyLog.append(System.getProperty("line.separator"));
			}
			printProxyLog.close();
			String fileLink = "<a href=\"" + "../TestCases/Logs/"+ proxyFileName+"\"><b>ProxyLogs</b></a>";
			printhtml.println("<br>");
			printhtml.println("<h3 align='center'> <span style=\"color:blue\">"+fileLink+"</span> </h3>");
			//		printhtml.println("<br>");
			//		printhtml.println("<br>");
		}
		*/
	}
	
	
	
	
	/*
	
	public String addConsoleLogsToFile(TestSuites ts){
//		Capabilities cap = ((RemoteWebDriver) ts.getDriver()).getCapabilities();
				String fileLink = "";
					try {
		String browserName = getCurentBrowserName(ts);
		if (browserName.equalsIgnoreCase("chrome") || browserName.equalsIgnoreCase("ch")){
			LogEntries logs = ts.getDriver().manage().logs().get("browser");
			for (LogEntry entry : logs) {
				//	            System.out.println(new Date(entry.getTimestamp()) + " " + entry.getLevel() + " " + entry.getMessage());
				if (entry.getLevel() == entry.getLevel().SEVERE)
					printlog.append(new Date(entry.getTimestamp()) + " " + entry.getLevel() + " " + entry.getMessage()+" "+ System.getProperty("line.separator"));
			}
			printlog.append(System.getProperty("line.separator"));
			printlog.append(System.getProperty("line.separator"));
			printlog.append("Performance Logs: "+ System.getProperty("line.separator"));
			logs = ts.getDriver().manage().logs().get(LogType.PERFORMANCE);
//			logs = ts.getDriver().manage().logs().get(LogType.
			printlog.append(System.getProperty("line.separator"));
			for (LogEntry entry : logs) {
                 if(entry.toString().contains("\"type\":\"XHR\"") & entry.toString().contains("\"url\":\"https://domain.com/")) {
//				if (entry.getLevel() == entry.getLevel().SEVERE) {
					printlog.append(new Date(entry.getTimestamp()) + " " + entry.toString() +" "+ System.getProperty("line.separator"));
					printlog.append(System.getProperty("line.separator"));
					printlog.append(System.getProperty("line.separator"));
				}
			}
			printlog.close();
		
			//add proxy log:
//			BrowserMobProxy proxy = ts.getSeleniumDriver().getProxyObject(ts.getSeleniumDriver());
//			Har harObject = proxy.getHar();
//			// Write HAR Data in a File
////			File harFile = new File(pro);
//			try {
//				harObject.writeTo(proxyFile);
//			} catch (IOException ex) {
//				 System.out.println (ex.toString());
//			     System.out.println("Could not find file " + proxyFileName);
//			}
			
			fileLink = "<a href=\"" + "../TestCases/Logs/"+ logFileName+"\"><b>Browser_ConsoleLogs</b></a>";
			printhtml.println("</tbody>");
			printhtml.println("</table>");
			printhtml.println("<br>");
			printhtml.println("<h3 align='center'> <span style=\"color:blue\">"+fileLink+"</span> </h3>");
			printhtml.println("<br>");
			completeReport();
		}
		}catch(Exception e) {
			e.printStackTrace();
		} 
		return fileLink;
	}
	
	*/
	
	
	
	public void log(String taskPerformed, String info, String strPassFail) throws Exception {
		boolean snapshotPermitted = DriverFactory.environment.get("ScreenshotOnFailure").equalsIgnoreCase("true");
//		try {
			String value =  strPassFail.toUpperCase();
			String nameOfScreenShot ="";
			String imgLink = "";
			if(snapshotPermitted || strPassFail.equalsIgnoreCase("FAIL") || strPassFail.equalsIgnoreCase("WARNING") || strPassFail.startsWith("WAR")) {
				nameOfScreenShot = ""; //captureScreenShot(ts);
				if (value.equals("PASS") && (nameOfScreenShot.contains("Image_"))) {
				imgLink = "<a href=\"" + "../TestCases/ScreenShot/"+ nameOfScreenShot + "\">"+value+"</a>";
				}
				else if(nameOfScreenShot.equalsIgnoreCase("UnableToCapture")) {
					System.out.println("Unable to capture screenshot");
					value = "UnableToCapture";
					    imgLink = "UnableToCapture";
				}
				else if(nameOfScreenShot.equalsIgnoreCase("secureFlag")) {
					value = "secureFlag";
					    imgLink = "secureFlag";
				}
				else{
					imgLink = "<a href=\"" + "../TestCases/ScreenShot/"+ nameOfScreenShot + "\"><font color='#FF7373'>"+value+"</a>";
				}
			}
			else {
				imgLink = value;
			}
			printHtml.append("<tr>");

			printHtml
			.append("<td width='35%' bgcolor='' valign='top' align='justify' ><font color='#000000' face='Tahoma' size='2'>"
					+ taskPerformed + "</font></td>");
			printHtml
			.append("<td width='42%' bgcolor='' valign='top' align='justify' ><font color='#000000' face='Tahoma' size='2'>"
					+ info + "</font></td>");
			if (strPassFail.equalsIgnoreCase("PASS")) {
				printHtml
						.append(
								"<td width='10%' bgcolor='' valign='middle' align='center'><b><font color='#000000' face='Tahoma' size='2'>"
										+ imgLink + "</font></b></td>");
			}
			else if(strPassFail.equalsIgnoreCase("done")){
				printHtml
						.append(
								"<td width='10%' bgcolor='' valign='middle' align='center'><b><font color='#000000' face='Tahoma' size='2'>Done</font></b></td>");
			}
			else if (strPassFail.equalsIgnoreCase("FAIL")) {
				printHtml
				.append("<td width='10%' bgcolor='' valign='middle' align='center'><b><font color='#FF7373' face='Tahoma' size='2'>"
						+imgLink+ "</font></b></td>");
//				throw new Exception("failures came");
			} else if (strPassFail.equals("")){
				printHtml
				.append("<td width='10%' bgcolor='' valign='middle' align='center'><b><font color='#FF7373' face='Tahoma' size='2'></font></td>");	
			}
			else {
				printHtml
				.append("<td width='10%' bgcolor='' valign='middle' align='center'><b><font color='#FF7373' face='Tahoma' size='2'>"
						+imgLink+"</font></b></td>");
			}
			printHtml
			.append("<td width='13%' bgcolor='' valign='middle' align='center' ><font color='#000000' face='Tahoma' size='2'>"
					+ JavaWrappers.getCurrentTime("HH:mm:ss") + "</font></td>");	
			printHtml.append("</tr>");
			
			if (strPassFail.toUpperCase().equals("FAIL")){
				throw new Exception("failures came");
			}
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
			.append("<td width='35%' bgcolor='' valign='top' align='' ><font color='#000000' face='Tahoma' size='2'>"
					+ requestDetails + "</font></td>");
			if (rs == null){
				printHtml
				.append("<td width='42%' bgcolor='' valign='top' align='justify' ><font color='#000000' face='Tahoma' size='2'>"
						+ "<b>Api Response is NULL</b>");
				printHtml.append("</font></td>");
			}
			else{
			printHtml
			.append("<td width='42%' bgcolor='#D3D3D3' valign='top' align='justify' ><font color='#000000' face='Tahoma' size='2'>"
					+ "<b>Api Response_"+rs.getStatusCode()+", Time taken: "+rs.getTime()+" millisec</b>");
			formatResponse(rs);
			printHtml.append("</font></td>");
			}
			if (strPassFail.toUpperCase().equals("PASS")) {
				printHtml
				.append("<td width='10%' bgcolor='' valign='middle' align='center'><b><font color='#000000' face='Tahoma' size='2'>"
						+imgLink+ "</font></b></td>");
			} else if (strPassFail.toUpperCase().equals("FAIL")) {
				printHtml
				.append("<td width='10%' bgcolor='' valign='middle' align='center'><b><font color='#FF7373' face='Tahoma' size='2'>"
						+imgLink+ "</font></b></td>");
			} else {
				printHtml
				.append("<td width='10%' bgcolor='' valign='middle' align='center'><b><font color='#FF7373' face='Tahoma' size='2'>"
						+imgLink+"</font></b></td>");
			}
			printHtml
			.append("<td width='13%' bgcolor='' valign='middle' align='center' ><font color='#000000' face='Tahoma' size='2'>"
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
			.append("<td width='35%' bgcolor='' valign='top' align='justify' ><font color='#000000' face='Tahoma' size='2'>"
					+ taskPerformed + "</font></td>");
			printHtml
			.append("<td width='42%' bgcolor='' valign='top' align='justify' ><font color='#000000' face='Tahoma' size='2'>"
					+ info + "</font></td>");
			if (strPassFail.toUpperCase().equals("PASS")) {
				printHtml
				.append("<td width='10%' bgcolor='' valign='middle' align='center'><b><font color='#000000' face='Tahoma' size='2'>"
						+imgLink+ "</font></b></td>");
			} else if (strPassFail.toUpperCase().equals("FAIL")) {
				printHtml
				.append("<td width='10%' bgcolor='' valign='middle' align='center'><b><font color='#FF7373' face='Tahoma' size='2'>"
						+imgLink+ "</font></b></td>");
			} else {
				printHtml
				.append("<td width='10%' bgcolor='' valign='middle' align='center'><b><font color='#FF7373' face='Tahoma' size='2'>"
						+imgLink+"</font></b></td>");
			}
			printHtml
			.append("<td width='13%' bgcolor='' valign='middle' align='center' ><font color='#000000' face='Tahoma' size='2'>"
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
			.append("<td width='35%' bgcolor='' valign='top' align='justify' ><font color='#000000' face='Tahoma' size='2'>"
					+ taskPerformed + "</font></td>");
			printHtml
			.append("<td width='42%' bgcolor='' valign='top' align='justify' ><font color='#000000' face='Tahoma' size='2'>"
					+ info + "</font></td>");
			if (strPassFail.toUpperCase().equals("PASS")) {
				printHtml
				.append("<td width='18%' bgcolor='' valign='middle' align='center'><b><font color='#000000' face='Tahoma' size='2'>"
						+imgLink+ "</font></b></td>");
			} else if (strPassFail.toUpperCase().equals("FAIL")) {
				printHtml
				.append("<td width='10%' bgcolor='' valign='middle' align='center'><b><font color='#FF7373' face='Tahoma' size='2'>"
						+imgLink+ "</font></b></td>");
			} else {
				printHtml
				.append("<td width='10%' bgcolor='' valign='middle' align='center'><b><font color='#FF7373' face='Tahoma' size='2'>"
						+imgLink+"</font></b></td>");
			}
			printHtml
			.append("<td width='13%' bgcolor='' valign='middle' align='center' ><font color='#000000' face='Tahoma' size='2'>"
					+ JavaWrappers.getCurrentTime("HH:mm:ss") + "</font></td>");	
			printHtml.append("</tr>");

		} catch (Exception ex) {
			System.out.println("Error while reporting: "+ex.getMessage());
			ex.printStackTrace();
		}
	}
	
	
	

	public void header() {
		try {
//			String complementryInfo = " for <span style=\"color:red\">"+suiteReporting.getFlavor()+"</span>";
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
//			printhtml.println("<font face='Tahoma'size='2'>");
//			printhtml.println("<h3 align='right' ><font color='#000000' face='Tahoma' size='3'></font></h3>");
//			printhtml.println("<h3 align='center'>Environment : <span style=\"color:grey\">"+env+"</span> </h3>");
			printHtml.println("<h3 align='center'>TestCase : <span style=\"color:grey\">"+testCaseName+"</span> </h3>");
			printHtml.println("<br>");
//			printhtml.println("<table border='0' width='100%' height='47'>");
			printHtml.println("<table class='tg' border='2' style='border-collapse:collapse;width:100%;word-break: break-all'>");
			printHtml.println("<tr bgcolor='#92DAEA'>");
			printHtml
			.println("<td width='35%' bgcolor='' align='center'><b><font color='#000000' face='Tahoma' size='2'>TaskPerformed</font></b></td>");
			printHtml
			.println("<td width='42%' bgcolor=''align='center'><b><font color='#000000' face='Tahoma' size='2'>Info</font></b></td>");
			printHtml
			.println("<td width='10%' bgcolor='' align='center'><b><font color='#000000' face='Tahoma' size='2'>Status</font></b></td>");
			printHtml
			.println("<td width='13%' bgcolor='' align='center'><b><font color='#000000' face='Tahoma' size='2'>CurrentTime</font></b></td>");
			printHtml.println("</tr>");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void completeReport(){
       printHtml.close();
	}

}
