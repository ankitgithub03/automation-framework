package driverUtils;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import lombok.Data;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;


public interface Driver {

    AndroidDriver androidDriver = null;
    IOSDriver iosDriver = null;
    AppiumDriver appiumDriver = null;
    WebDriver webDriver = null;
    ChromeDriver chromeDriver = null;
    FirefoxDriver firefoxDriver = null;
    InternetExplorerDriver internetExplorerDriver = null;

}
