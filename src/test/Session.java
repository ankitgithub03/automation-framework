package test;

import org.openqa.selenium.WebDriver;
import utils.HashMapNew;

public class Session implements Runnable {
  
  private volatile boolean exit = false;
  private String driverType;
  private HashMapNew Environment;
  private WebDriver driver;

  public Session(HashMapNew Environment, String driverType, WebDriver driver) {
    this.Environment = Environment;
    this.driverType = driverType;
    this.driver = driver;
  }

  public void run() {
    String maxDuration = Environment.get("maxDuration").trim();
      if(maxDuration != null && !maxDuration.trim().equalsIgnoreCase("")) {
        int duration = Integer.parseInt(maxDuration);
        try {
           do {
            Thread.sleep(1000L);
            duration--;
           } while(!exit && duration > 0);
           if(duration == 0) {
               if(driverType.trim().toUpperCase().contains("ANDROID") || driverType.trim().toUpperCase().contains("IOS") || driverType.trim().toUpperCase().contains("CHROME")) {
               } else {
                 if(driver != null) {
                   try {
                     driver.quit();
                   } catch (Exception e) {
                     //Do Nothing
                   }
                 }
               }
               System.out.println("Session terminated because maxDuration - " + maxDuration + " reached");
             }
        } catch (Exception e) {
          //Do Nothing
        }
    }
  }

  public void stop() {
    exit = true;
  }
}
