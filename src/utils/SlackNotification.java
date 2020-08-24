package utils;

import net.gpedro.integrations.slack.SlackApi;
import net.gpedro.integrations.slack.SlackAttachment;
import net.gpedro.integrations.slack.SlackField;
import net.gpedro.integrations.slack.SlackMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import report.custom.ReportsSession;
import test.DriverFactory;

public class SlackNotification {

  static Logger log = LoggerFactory.getLogger(SlackNotification.class);
  String OS = System.getProperty("os.name").toLowerCase();


  public void sendSlackMessage(String kibanaUrl, int passPercentage, String commitId, String developerName,
      String sourceBranch, String targetBranch, String prNumber) {
    String driverType = DriverFactory.environment.get("projectType");
    String slackTitle = DriverFactory.environment.get("orgName").trim()+" "+JavaWrappers.toCamelCase(driverType.trim())+" Test Report";
    if (DriverFactory.environment.get("slackNotification").trim().equalsIgnoreCase("true")) {
      String msg = "Env: " + DriverFactory.getEnv() + "\n";
			if (driverType.equalsIgnoreCase("ANDROID") || driverType.trim().equalsIgnoreCase("IOS")) {
				msg += "App Version: " + DriverFactory.environment.get("appVersion") + "\n";
			}
			String tags = System.getProperty("tags");
			if (tags != null && !tags.trim().equalsIgnoreCase("")) {
				msg += "Tags: " + tags + "\n";
			}
      msg += "Build ";
      String color = "#D00000"; //Red
      String customMessage = "your TEST failed";
      if (ReportsSession.totalPassPercentage < Integer.parseInt(DriverFactory.environment.get("expectedPassPercentage").trim())) {
        msg += "Failure ";
        color = "#D00000"; //Red
        customMessage = "your TEST failed";
      } else if (ReportsSession.totalFailTestCases > 0) {
        msg += "Unstable ";
        color = "#FFFF00"; //Yellow
        customMessage = "your BUILD/TEST found to be unstable, might want to check why";
      } else {
        msg += "Stable ";
        color = "#008000"; //Green
        customMessage = "your TEST rocks";
      }
      msg += "after "+DriverFactory.environment.get("totalTimeTaken")+"\n";
      msg += "Test Status: "+ReportsSession.totalPassPercentage+"%\n";
      msg += "\t Passed: "+ ReportsSession.totalPassTestCases +", Failed: "+ReportsSession.totalFailTestCases+", Skipped: "+ReportsSession.totalSkipTestCases;

      if (!ReportsSession.reportUrl.trim().equalsIgnoreCase("")) {
        String jenkinsIp = DriverFactory.environment.get("jenkinsIp").trim();
        if (!jenkinsIp.trim().equalsIgnoreCase("") && System.getProperty("JOB_NAME") != null && !System.getProperty("JOB_NAME").trim().equalsIgnoreCase("")) {
          String completeUrl = jenkinsIp + "/job/" + System.getProperty("JOB_NAME").trim() + "/ws/" + ReportsSession.reportUrl;
          String failSafeReport = jenkinsIp+"/job/"+System.getProperty("JOB_NAME").trim() + "/ws/"+"target/failsafe-reports/emailable-report.html";
          msg += "\nConsolidated Report:  <" +completeUrl+"|Click here>";
          msg += "\nFailSafe Report: "+"<"+failSafeReport+"|Click here>";
          if (!DriverFactory.environment.get("testCaseSheetUrl").trim().equalsIgnoreCase("")) {
            msg += "\nTest case sheet: <"+DriverFactory.environment.get("testCaseSheetUrl")+"|Click here>";
          }
          if (!kibanaUrl.trim().equalsIgnoreCase("")) {
            msg += "\n Kibana url: <"+kibanaUrl+"|Click here>";
          }
					if (System.getProperty("gitBranch") != null && !System.getProperty("gitBranch").trim().equalsIgnoreCase("")) {
						msg += "\nTest Git Branch: "+System.getProperty("gitBranch").trim();
					}
          slackTitle = System.getProperty("JOB_NAME").trim()+" Job Report";
          if (System.getProperty("JOB_NAME") != null && !System.getProperty("JOB_NAME").trim().equalsIgnoreCase("") && System.getProperty("BUILD_NUMBER") != null && !System.getProperty("BUILD_NUMBER").trim().equalsIgnoreCase("") && System.getProperty("FEEDBACK_ID") != null && !System.getProperty("FEEDBACK_ID").trim().equalsIgnoreCase("")) {
            slackTitle = System.getProperty("FEEDBACK_ID").trim()+" Job Report";
						if (!prNumber.trim().equalsIgnoreCase("")) {
							msg += "\nPR#: " + prNumber;
						}
						if (!sourceBranch.trim().equalsIgnoreCase("")) {
							msg += "\nPR Source Branch: "+sourceBranch;
						}
						if (!targetBranch.trim().equalsIgnoreCase("")) {
							msg += "\nPR Target Branch: "+targetBranch;
						}
						if (!commitId.trim().equalsIgnoreCase("")) {
							msg += "\nCommit Id: "+commitId;
						}
						if (!developerName.trim().equalsIgnoreCase("")) {
							msg += "\nDeveloper: "+developerName;
						}
          }
        } else {
          msg += "\nDebugging Report: "+ReportsSession.reportUrl;
        }
      }

      SlackApi api = new SlackApi(DriverFactory.environment.get("slackWebHook").trim());
      SlackAttachment attachment = new SlackAttachment();
      attachment.setFallback(slackTitle);
      attachment.setColor(color);
      SlackField fields = new SlackField();
      fields.setTitle(slackTitle);
      fields.setValue(msg);
      fields.setShorten(false);
      attachment.addFields(fields);
      String slackHandle = DriverFactory.environment.get("slackHandle");
			if (slackHandle.trim().contains(",")) {
        slackHandle = slackHandle.replaceAll(",", " ");
      }
      api.call(new SlackMessage("Hi "+slackHandle+" : "+customMessage+" - "+passPercentage+"% pass rate").setLinkNames(true).addAttachments(attachment));
    }
  }
}
