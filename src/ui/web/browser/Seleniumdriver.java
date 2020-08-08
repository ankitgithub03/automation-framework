package ui.web.browser;

import io.github.bonigarcia.wdm.WebDriverManager;
import io.github.bonigarcia.wdm.config.DriverManagerType;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.DriverFactory;
import ui.driverUtils.Drivers;
import utils.OSValidator;

public class Seleniumdriver extends Drivers {

  private static Logger log = LoggerFactory.getLogger(Seleniumdriver.class);

  public WebDriver createChromeDriver() {
    WebDriver driver = null;
    ChromeOptions option = new ChromeOptions();
    option.addArguments("--start-maximized");
    option.addArguments("disable-infobars");
    option.setPageLoadStrategy(PageLoadStrategy.NONE);
    option.addArguments("--enable-automation");
    option.addArguments("--always-authorize-plugins");
    option.addArguments("--window-size=1500,1000");
    option.setCapability(ChromeOptions.CAPABILITY, option);

    Map<String, Object> prefs = new HashMap<String, Object>();
    prefs.put("credentials_enable_service", false);
    prefs.put("profile.password_manager_enabled", false);
    prefs.put("profile.default_content_settings.popups", 0);
    option.setExperimentalOption("prefs", prefs);
    if (OSValidator.isUnix(os)) {
      log.info("Linux Found");
      option.addArguments("--headless");
      option.addArguments("--no-sandbox");
      option.addArguments("--disable-dev-shm-usage");
    }

    // In case of extension is required in chrome
    if (!DriverFactory.environment.get("chrome_extension_file_path").trim().equalsIgnoreCase("")) {
      File addonpath = new File(
          System.getProperty("user.dir") + DriverFactory.environment.get("chrome_extension_file_path"));
      option.addExtensions(addonpath);
    }

    desiredCapabilities = DesiredCapabilities.chrome();
    // below code is to capture the logs
    LoggingPreferences loggingPreferences = new LoggingPreferences();
    loggingPreferences.enable(LogType.PERFORMANCE, Level.ALL);
    loggingPreferences.enable(LogType.BROWSER, Level.ALL);
    option.setCapability(CapabilityType.LOGGING_PREFS, loggingPreferences);
    desiredCapabilities.setCapability(ChromeOptions.CAPABILITY, option);
    desiredCapabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
    desiredCapabilities.setCapability(CapabilityType.LOGGING_PREFS, loggingPreferences);
    try {
      if (DriverFactory.environment.get("grid").equalsIgnoreCase("true")) {
        desiredCapabilities
            .setCapability("version", DriverFactory.environment.get("browserVersion").trim());
        driver = new RemoteWebDriver(new URL(DriverFactory.environment.get("ip")), desiredCapabilities);
      } else {
        DriverManagerType chrome = DriverManagerType.CHROME;
        WebDriverManager.getInstance(chrome).setup();
        driver = new ChromeDriver(desiredCapabilities);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return driver;
  }


}
