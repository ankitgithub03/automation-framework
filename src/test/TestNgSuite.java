package test;

import java.io.File;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import report.custom.MailReport;
import ui.app.testng.AppTestNgSuite;
import ui.driverUtils.FindLocators;
import utils.JavaWrappers;
import utils.MachineSearch;
import utils.ReadLocatorsXmlFile;

public abstract class TestNgSuite {

  public MailReport mailReport = new MailReport();
  public String projectSourceDirectory = System.getProperty("user.dir") + File.separator + "src";
  public String projectDataDirectory = System.getProperty("user.dir") + File.separator + "user-files";
  public String projectDirectory = System.getProperty("user.dir");
  public String mailReportFile ="";

  private static Logger log = LoggerFactory.getLogger(TestNgSuite.class);

  public abstract void initializeSuite(ITestContext itx) throws IOException;

  public abstract void tearDownSuite();

  public void initializeLocatorsFile() {
    String locatorsFileName = DriverFactory.environment.get("projectType").toLowerCase().trim() + "-locators.xml";
    DriverFactory.locatorsMapValues = new ReadLocatorsXmlFile().getLocators(projectDataDirectory, locatorsFileName);
    new FindLocators();
  }

  private void initializeConfigFile(){
    String dataFile = new MachineSearch().searchMachineForFile(projectDataDirectory, "Config_" + DriverFactory.getEnv().toLowerCase() + ".xml");
    DriverFactory.environment.putAll(new ReadLocatorsXmlFile().getXMLNodeValue(dataFile, "//"+DriverFactory.getEnv().toUpperCase()));
    String envFile = new MachineSearch().searchMachineForFile(projectSourceDirectory, "Environments.xml");
    DriverFactory.environment.putAll(new ReadLocatorsXmlFile().getXMLNodeValue(envFile, "//"+DriverFactory.getEnv().toUpperCase()));
  }

  public void setupEnvironmentAndConfig(){
    String configureFile = new MachineSearch().searchMachineForFile(projectSourceDirectory, "Configuration.xml");
    DriverFactory.environment = new ReadLocatorsXmlFile().getXMLNodeValue(configureFile, "configuration");
    String env = System.getProperty("env") != null && !System.getProperty("env").trim().equalsIgnoreCase("") ? System.getProperty("env").trim() : DriverFactory.environment.get("environment").trim();
    DriverFactory.environment.put("environment",env);
    DriverFactory.setEnv(env);
    initializeConfigFile();
  }

  public void setupReport() throws IOException {
    mailReport.initializeMailReport(JavaWrappers.toCamelCase(DriverFactory.environment.get("orgName")));
  }



}
