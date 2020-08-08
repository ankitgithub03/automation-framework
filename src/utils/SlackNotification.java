package utils;

import net.gpedro.integrations.slack.SlackApi;
import net.gpedro.integrations.slack.SlackAttachment;
import net.gpedro.integrations.slack.SlackField;
import net.gpedro.integrations.slack.SlackMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.DriverFactory;

public class SlackNotification {

  static Logger log = LoggerFactory.getLogger(SlackNotification.class);
  String OS = System.getProperty("os.name").toLowerCase();


  public void sendMessage(String env, String appVersion, String reportUrl, int totalPassedTCs,
      int totalFailedTCs, int totalSkippedMtds, String strTimeDifference, String browser,
      String suiteFolderName, String slackHandle, String tags, String testCaseSheet,
      String kibanaUrl, int passPercentage, String commitId, String developerName,
      String sourceBranch, String targetBranch, String prNumber) {
    if (DriverFactory.environment.get("slackNotification").trim().equalsIgnoreCase("true")) {
      String msg = "Env: " + env + "\n";
			if (browser.trim().equalsIgnoreCase("ANDROID") || browser.trim().equalsIgnoreCase("IOS")) {
				msg += "App Version: " + appVersion + "\n";
			}
			if (tags != null && !tags.trim().equalsIgnoreCase("")) {
				msg += "Tags: " + tags + "\n";
			}
      msg += "Build ";
      String color = "#D00000"; //Red
      String customMessage = "your TEST failed";
      if (passPercentage < Integer
          .parseInt(DriverFactory.environment.get("passPercentage").trim())) {
        msg += "Failure ";
        color = "#D00000"; //Red
        customMessage = "your TEST failed";
      } else if (totalFailedTCs > 0) {
        msg += "Unstable ";
        color = "#FFFF00"; //Yellow
        customMessage = "your BUILD/TEST found to be unstable, might want to check why";
      } else {
        msg += "Stable ";
        color = "#008000"; //Green
        customMessage = "your TEST rocks";
      }
      msg += "after " + strTimeDifference + "\n";
      msg += "Test Status: " + passPercentage + "%\n";
      msg += "\t Passed: " + totalPassedTCs + ", Failed: " + totalFailedTCs + ", Skipped: "
          + totalSkippedMtds;

      String title =
          DriverFactory.environment.get("orgName").trim() + " " + toCamelCase(browser.trim())
              + " App Test Report";
      if (!reportUrl.trim().equalsIgnoreCase("")) {
        String jenkinsIp = DriverFactory.environment.get("jenkinsIp").trim();
        if (!jenkinsIp.trim().equalsIgnoreCase("") && System.getProperty("JOB_NAME") != null
            && !System.getProperty("JOB_NAME").trim().equalsIgnoreCase("")) {
          String completeUrl =
              jenkinsIp + "/job/" + System.getProperty("JOB_NAME").trim() + "/ws/" + reportUrl;
          String failSafeReport =
              jenkinsIp + "/job/" + System.getProperty("JOB_NAME").trim() + "/ws/"
                  + "target/failsafe-reports/emailable-report.html";
          msg += "\nConsolidated Report: " + "<" + completeUrl + "|Click here>";
          msg += "\nFailSafe Report: " + "<" + failSafeReport + "|Click here>";
          if (!testCaseSheet.trim().equalsIgnoreCase("")) {
            msg += "\nTest case sheet: " + "<" + testCaseSheet + "|Click here>";
          }
          if (!kibanaUrl.trim().equalsIgnoreCase("")) {
            msg += "\nKibana url: " + "<" + kibanaUrl + "|Click here>";
          }
					if (System.getProperty("gitBranch") != null && !System.getProperty("gitBranch").trim()
							.equalsIgnoreCase("")) {
						msg += "\nTest Git Branch: " + System.getProperty("gitBranch").trim();
					}
          title = System.getProperty("JOB_NAME").trim() + " Job Report";
          if (System.getProperty("JOB_NAME") != null && !System.getProperty("JOB_NAME").trim()
              .equalsIgnoreCase("") && System.getProperty("BUILD_NUMBER") != null && !System
              .getProperty("BUILD_NUMBER").trim().equalsIgnoreCase("")
              && System.getProperty("FEEDBACK_ID") != null && !System.getProperty("FEEDBACK_ID")
              .trim().equalsIgnoreCase("")) {
            title = System.getProperty("FEEDBACK_ID").trim() + " Job Report";
						if (!prNumber.trim().equalsIgnoreCase("")) {
							msg += "\nPR#: " + prNumber;
						}
						if (!sourceBranch.trim().equalsIgnoreCase("")) {
							msg += "\nPR Source Branch: " + sourceBranch;
						}
						if (!targetBranch.trim().equalsIgnoreCase("")) {
							msg += "\nPR Target Branch: " + targetBranch;
						}
						if (!commitId.trim().equalsIgnoreCase("")) {
							msg += "\nCommit Id: " + commitId;
						}
						if (!developerName.trim().equalsIgnoreCase("")) {
							msg += "\nDeveloper: " + developerName;
						}
          }
        } else {
          msg += "\nDebugging Report: " + reportUrl;
        }
      }

      SlackApi api = new SlackApi(DriverFactory.environment.get("slackWebHook").trim());
      SlackAttachment attachment = new SlackAttachment();
      attachment.setFallback(title);
      attachment.setColor(color);
      SlackField fields = new SlackField();
      fields.setTitle(title);
      fields.setValue(msg);
      fields.setShorten(false);
      attachment.addFields(fields);
			if (slackHandle.trim().contains(",")) {
				slackHandle = slackHandle.replaceAll(",", " ");
			}
      api.call(new SlackMessage(
          "Hi " + slackHandle + " : " + customMessage + " - " + passPercentage + "% pass rate")
          .setLinkNames(true).addAttachments(attachment));
    }
  }

  private String toCamelCase(String s) {
    String[] parts = s.split(" ");
    String camelCaseString = "";
    for (String part : parts) {
      camelCaseString = camelCaseString + toProperCase(part) + " ";
    }
    return camelCaseString.trim();
  }

  private String toProperCase(String s) {
    return s.substring(0, 1).toUpperCase() +
        s.substring(1).toLowerCase();
  }
}
