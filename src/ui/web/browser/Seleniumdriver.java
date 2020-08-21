package ui.web.browser;

import io.github.bonigarcia.wdm.WebDriverManager;
import io.github.bonigarcia.wdm.config.DriverManagerType;
import io.netty.handler.codec.http.HttpResponse;
import java.io.File;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.client.ClientUtil;
import net.lightbody.bmp.filters.ResponseFilter;
import net.lightbody.bmp.proxy.CaptureType;
import net.lightbody.bmp.util.HttpMessageContents;
import net.lightbody.bmp.util.HttpMessageInfo;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.DriverFactory;
import ui.driverUtils.Drivers;
import utils.JavaWrappers;
import utils.OSValidator;

public class Seleniumdriver extends Drivers {

  private static Logger log = LoggerFactory.getLogger(Seleniumdriver.class);

  public synchronized void createChromeDriver() {
    WebDriver driver = null;
    Proxy seleniumProxy = setupProxy();
    ChromeOptions option = new ChromeOptions();
    option.addArguments("--start-maximized");
    option.addArguments("disable-infobars");
    option.setPageLoadStrategy(PageLoadStrategy.NONE);
    option.addArguments("--enable-automation");
    option.addArguments("--always-authorize-plugins");
    option.addArguments("--window-size=1500,1000");
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

    // below code is to capture the logs
    LoggingPreferences loggingPreferences = new LoggingPreferences();
    loggingPreferences.enable(LogType.PERFORMANCE, Level.ALL);
    loggingPreferences.enable(LogType.BROWSER, Level.ALL);
    option.setCapability(CapabilityType.LOGGING_PREFS, loggingPreferences);
    option.setCapability(ChromeOptions.CAPABILITY, option);
    option.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
    option.setCapability(CapabilityType.PROXY, seleniumProxy);
    option.addArguments("--ignore-certificate-errors");
    try {
      if (DriverFactory.environment.get("grid").equalsIgnoreCase("true")) {
        driver = new RemoteWebDriver(new URL(DriverFactory.environment.get("ip")), option);
      } else {
        DriverManagerType chrome = DriverManagerType.CHROME;
        WebDriverManager.getInstance(chrome).setup();
        driver = new ChromeDriver(option);
      }
      Drivers.setWebDriver(driver);
      driver.get(DriverFactory.getTestDetails("webUrl"));
      System.out.println("open url--> "+DriverFactory.getTestDetails("webUrl"));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public Proxy setupProxy(){
    // start the proxy
    Proxy seleniumProxy = null;
    try {
      int port = new JavaWrappers().getAvailablePort(6999, 6000);
      BrowserMobProxy browserMobProxy = new BrowserMobProxyServer();
//      browserMobProxy.addResponseFilter(new ResponseFilter() {
//        @Override
//        public void filterResponse(HttpResponse response, HttpMessageContents contents, HttpMessageInfo messageInfo) {
//          if (response.headers().contains("x-requested-with") && response.headers().contains("application/json")) {
//            contents.setTextContents("This message body will appear in all responses!");
//          }
//        }
//      });
      seleniumProxy = ClientUtil.createSeleniumProxy(browserMobProxy);
      try {
        browserMobProxy.start(port, InetAddress.getLocalHost());
        String hostIp = Inet4Address.getLocalHost().getHostAddress();
        seleniumProxy.setHttpProxy(hostIp+":" + browserMobProxy.getPort());
        seleniumProxy.setSslProxy(hostIp+":" + browserMobProxy.getPort());
      } catch (Exception e) {
        e.printStackTrace();
      }
      DriverFactory.setBrowserMobProxy(browserMobProxy);
      browserMobProxy.enableHarCaptureTypes(CaptureType.REQUEST_CONTENT, CaptureType.RESPONSE_CONTENT);
      browserMobProxy.newHar(DriverFactory.getTestDetails("testCaseName").replaceAll(" ", "_").trim() + ".har");
      DriverFactory.setBrowserMobProxy(browserMobProxy);
    }catch(Exception e){
      e.printStackTrace();
    }
    return seleniumProxy;
  }


}
