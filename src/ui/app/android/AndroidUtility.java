package ui.app.android;

import java.util.ArrayList;
import java.util.List;
import test.DriverFactory;
import ui.driverUtils.Drivers;
import utils.ExecuteCommand;
import utils.JavaWrappers;

public class AndroidUtility extends AndroidCommands {

  public String getInstalledAppVersion(String deviceID, String packageName) {
    String appVersion = "0";
    String cmdQuery = getCommand(installed_appVersion, deviceID, packageName);
    Object[] result = new ExecuteCommand().executeCommand(cmdQuery, 2, true, true, 2);
    if (!((String) result[0]).isEmpty()) {
      appVersion = ((String) result[0]).split("=")[1].trim();
    }
    return appVersion.replaceAll("\n","").trim();
  }

  public String getInstalledAppBuildVersion(String deviceID, String packageName) {
    String appBuildVersion = "0";
    String cmdQuery = getCommand(installed_buildNumber, deviceID, packageName);
    Object[] result = new ExecuteCommand().executeCommand(cmdQuery, 2, true, true, 2);
    if (!((String) result[0]).isEmpty()) {
      appBuildVersion = JavaWrappers.getNumericValue((String) result[0]);
    }
    return appBuildVersion.replaceAll("\n","").trim();
  }

  public List<String> getConnectedDevices() {
    List<String> devices = new ArrayList<String>();
    try {
      Object[] result = new ExecuteCommand()
          .executeCommand(connected_android_devices, 2, true, true, 2);
      String[] resultSet = ((String) result[0]).split("List of devices attached")[1].split("\n");
      for (String device : resultSet) {
        device = device.replaceAll("\\n", "");
        if (!device.contains("List of devices attached") && !device.isEmpty()) {
          device = device.replace("device", "");
          device = device.trim();
          devices.add(device);
        }
      }
      if (devices.size() == 0) {
        System.err.print("No Device connected, Please connect Android devices");
        System.exit(0);
      }
    } catch (Exception e) {
      e.printStackTrace();
      System.err.print("No Device connected, Please connect Android devices");
      System.exit(0);
    }
    return devices;
  }


  /**
   * Check if the app package is already installed
   *
   * @param deviceID
   * @param packageName
   * @return
   */
  public boolean isAppInstalled(String deviceID, String packageName) {
    boolean installed = false;
    String cmdQuery = getCommand(check_installedPackage, deviceID, packageName);
    Object[] result = new ExecuteCommand().executeCommand(cmdQuery, 1, true, true, 2);
    try {
      String output = ((String) result[0]).split(":")[1];
      if (output.equalsIgnoreCase(packageName)) {
        installed = true;
        System.out.println("App is already installed: " + packageName);
      }
    } catch (Exception e) {

    }
    return installed;
  }


  public boolean removeAppIfInstall(String deviceID, String packageName) {
    boolean removed = false;
    String cmdQuery = getCommand(check_installedPackage, deviceID, packageName);
    Object[] packagesDetails = new ExecuteCommand().executeCommand(cmdQuery, 1, true, true, 2);
    String[] output = ((String) packagesDetails[0]).split("\n");
    for (String packages : output) {
      packages = packages.split(":")[1];
      String uninstallApk_command = getCommand(uninstallApk, deviceID, packages);
      Object[] result = new ExecuteCommand().executeCommand(uninstallApk_command, 1, true, true, 2);
      if (((String) result[0]).equalsIgnoreCase("Success")) {
        removed = true;
      }
    }
    return removed;
  }

  /**
   * Get the connected device Android OS version
   *
   * @param deviceID
   * @return
   */
  public String getDeviceAndroidVersion(String deviceID) {
    String cmdQuery = getCommand(android_OS_version, deviceID);
    Object[] result = new ExecuteCommand().executeCommand(cmdQuery, 1, true, true, 2);
    return ((String) result[0]).replaceAll("\n","").trim();
  }


  public String getDeviceAndroidSDKVersion(String deviceID) {
    String cmdQuery = getCommand(android_SDK_version, deviceID);
    Object[] result = new ExecuteCommand().executeCommand(cmdQuery, 1, true, true, 2);
    return ((String) result[0]).replaceAll("\n","").trim();
  }


  /**
   * Get the connected device name which is including: BrandName-Model Number
   *
   * @param deviceID
   * @return
   */
  public String getDeviceName(String deviceID) {
    String cmdQuery = getCommand(android_device_brandName, deviceID);
    Object[] result = new ExecuteCommand().executeCommand(cmdQuery, 1, true, true, 1);
    String brand = ((String) result[0]).replaceAll("\n","").trim();

    if(brand.isEmpty()) {
      cmdQuery = getCommand(android_device_manufacture, deviceID);
      result = new ExecuteCommand().executeCommand(cmdQuery, 1, true, true, 1);
      brand = ((String) result[0]).replaceAll("\n", "").trim();
    }
    cmdQuery = getCommand(android_device_model_number, deviceID);
    result = new ExecuteCommand().executeCommand(cmdQuery, 1, true, true, 1);
    String modelNumber = ((String) result[0]).replaceAll("\n","").trim();
    if (brand.isEmpty()) {
      return modelNumber;
    } else {
      return brand + ":" + modelNumber;
    }
  }

  /**
   * This method is installing the apk on the device 1. first it will check the same package is
   * installed or not a. if installed A. checked the installed version
   *
   * @param deviceID
   * @throws Exception
   */
  public synchronized void installApk(String deviceID, String path, String packageName)
      throws Exception {
    String installApk_command = getCommand(installApk, deviceID, path);
    String uninstallApk_command = getCommand(uninstallApk, deviceID, packageName);
//		String needToRunOnAppVersion = DataMaps.SuiteConfigValues.get("App_Version");

    /**  remove all app of either debug or prod then install based on evn */
    removeAppIfInstall(deviceID, packageName);
    installAPK(installApk_command);
	
		/*
		
//		 this logic is based on version comparision then
		
		// check the app is already install on the device
		if(isAppInstalled(deviceID, packageName)) {
			// check the installed version 
			if(getInstalledAppVersion(deviceID, packageName).equalsIgnoreCase(needToRunOnAppVersion)) {
				System.out.println("Same version is already installed");
			}
			else {
				// if the installed version is not same then uninstall and install the required version
				String result = new ExecuteCommand().executeCommand(uninstallApk_command);
				if(result.equalsIgnoreCase("Success")) {
					installAPK(installApk_command);
				}
				else {
					System.err.println("Unable to uninstall :"+packageName);
					throw new Exception("Unable to uninstall :"+packageName);
				}
			}
		}
		else {   // if app is not install on the device
			installAPK(installApk_command);
		}
		*/

    clearCache(deviceID, packageName);
  }

  public void clearCache(String deviceID, String packageName) {
    String cmdQuery = getCommand(clean_cache, deviceID, packageName);
		Object[] result = new ExecuteCommand().executeCommand(cmdQuery, 1, true, true, 1);
  }

  public void stopApp(String deviceID, String packageName) {
    String cmdQuery = getCommand(force_stopApp, deviceID, packageName);
		Object[] result = new ExecuteCommand().executeCommand(cmdQuery, 1, true, true, 1);
  }

  public String getCrashLog(String deviceID) {
    String cmdQuery = getCommand(crash_logs, deviceID);
		Object[] result = new ExecuteCommand().executeCommand(cmdQuery, 1, true, true, 2);
    return (String)result[0];
  }

  private void installAPK(String cmdQuery) throws Exception {
		Object[] result = new ExecuteCommand().executeCommand(cmdQuery, 1, true, true, 3);
    if (((String)result[0]).equalsIgnoreCase("Success")) {
      System.out.println("APK has been installed");
    } else {
      System.err.println("Unable to install :" + cmdQuery);
      throw new Exception("Unable to install :" + cmdQuery);
    }
  }


//  public void enableWiFi(String deviceID) throws Exception {
//    String cmdQuery = getCommand(enable_wifi, deviceID);
//		Object[] result = new ExecuteCommand().executeCommand(cmdQuery, 1, true, true, 3);
//    new ExecuteCommand().validateExecutedCommnd(cmdQuery, "Broadcast completed: result=-1");
//  }
//
//  public void disableWiFi(String deviceID) throws Exception {
//    String cmdQuery = getCommand(disable_wifi, deviceID);
//    new ExecuteCommand().validateExecutedCommnd(cmdQuery, "Broadcast completed: result=-1");
//  }

  public void makeAppInGround(String deviceID) {
    String cmdQuery = getCommand(press_homeKey, deviceID);
		Object[] result = new ExecuteCommand().executeCommand(cmdQuery, 1, true, false, 1);
  }


  public String getCurrentOpenAppPackage(String deviceID) {
    String cmdQuery = getCommand(current_focusApp_package, deviceID);
		Object[] result = new ExecuteCommand().executeCommand(cmdQuery, 1, true, true, 1);
    return (String) result[0];
  }


  public String getCurrentOpenAppActivity(String deviceID) {
    String cmdQuery = getCommand(current_focusApp_activity, deviceID);
		Object[] result = new ExecuteCommand().executeCommand(cmdQuery, 1, true, true, 1);
		return (String) result[0];
  }


  public void clearDeviceAppiumSettings(String deviceID) {
    String cmdQuery = getCommand(uninstall_settings1, deviceID);
		Object[] result = new ExecuteCommand().executeCommand(cmdQuery, 1, true, true, 1);
    cmdQuery = getCommand(uninstall_settings2, deviceID);
		result = new ExecuteCommand().executeCommand(cmdQuery, 1, true, true, 1);
    cmdQuery = getCommand(uninstall_settings3, deviceID);
		result = new ExecuteCommand().executeCommand(cmdQuery, 1, true, true, 1);
    cmdQuery = getCommand(uninstall_settings4, deviceID);
		result = new ExecuteCommand().executeCommand(cmdQuery, 1, true, true, 1);
  }


  public boolean isWifiPresent(String browser, String wifiName, String deviceID) throws Exception {
    String network = DriverFactory.environment.get("networkType").trim();
    if (network.trim().equalsIgnoreCase("")) {
      network = "wifi";
    }
    if (network.trim().equalsIgnoreCase("data")) {
      return true;
    }
    if (browser.trim().equalsIgnoreCase("IOS")) {
      return true;
    }
    if (getNetworkIPAddress(true, deviceID).trim().equalsIgnoreCase("")) {
      networkSetUp("wifi", deviceID);

    }
    String cmdQuery = getCommand(is_wifi_present, deviceID);
		Object[] result = new ExecuteCommand().executeCommand(cmdQuery, 1, true, true, 1);
		String output = (String)result[0];
//    String output = runCommandUsingTerminal(devicefarm,
//        "adb -s " + udid + " shell dumpsys netstats | grep -E 'iface=wlan.*networkId'", false, "1");
    if (wifiName.trim().toLowerCase().contains("any") && !output.trim().equalsIgnoreCase("")) {
      return true;
    }
    if (!wifiName.trim().contains(",") && output.trim().toLowerCase()
        .contains(wifiName.trim().toLowerCase())) {
      return true;
    } else if (wifiName.trim().contains(",")) {
      String[] wifiNames = wifiName.trim().split(",");
      for (String name : wifiNames) {
        if (output.trim().toLowerCase().contains(name.trim().toLowerCase())) {
          return true;
        }
      }
    }
    return false;
  }

  public String getNetworkIPAddress(boolean wifi, String udid){
    String port = "wlan";
    if (!wifi) {
      port = "(rmnet|ccmni)";
    }

//    String cmdQuery = "adb -s "+udid+" shell ip route | grep \""+port+".*link.*src\" | awk \'{print $9}\'";
    String cmdQuery = "adb -s "+udid+" shell ip route | grep -E \""+port+".*link.*src\" | awk \'{print $9}\'";
		Object[] output = new ExecuteCommand().executeCommand(cmdQuery, 1, true, true, 1);
    return ((String) output[0]).trim().split("\n")[0];
  }

  public boolean networkSetUp(String network, String udid){
    String output = "";
    boolean success;
    String operatorName;
    if (!network.trim().equalsIgnoreCase("wifi") && !network.trim().equalsIgnoreCase("wi-fi")) {
      if (!network.trim().equalsIgnoreCase("") && !network.trim().equalsIgnoreCase("airplane")) {
        output = this.getNetworkIPAddress(false, udid);
        if (output.trim().equalsIgnoreCase("")) {
          new ExecuteCommand()
              .executeCommand(getCommand(airplane_mode_off, udid), 1, true, true, 1);
//          new ExecuteCommand()
//              .executeCommand(getCommand(airplane_mode1_off, udid), 1, true, true, 1);
//          this.networkToggle(udid, Arrays.asList("wifi", "data", "airplane"), Arrays.asList(false, true, false));
          success = this.waitForWifi(false, udid);
          if (!success) {
            return false;
          }

          operatorName = "";

          try {
            operatorName = this.getOperatorName(udid);
          } catch (Exception var7) {
          }

          if (operatorName.trim().equalsIgnoreCase("")) {
            return false;
          }

          success = this.waitForData(true, udid);
          if (!success) {
            return false;
          }
        }
      } else if (network.trim().equalsIgnoreCase("airplane")) {
        new ExecuteCommand().executeCommand(getCommand(airplane_mode_on, udid), 1, true, true, 1);
//        new ExecuteCommand().executeCommand(getCommand(airplane_mode1_on, udid), 1, true, true, 1);
//        this.networkToggle(udid, Arrays.asList("wifi", "data", "airplane"), Arrays.asList(false, false, true));
        success = this.waitForWifi(false, udid);
        if (!success) {
          return false;
        }
      }
    } else {
      output = this.getNetworkIPAddress(true, udid);
      if (output.trim().equalsIgnoreCase("")) {
        new ExecuteCommand().executeCommand(getCommand(airplane_mode_off, udid), 1, true, true, 1);
//        new ExecuteCommand().executeCommand(getCommand(airplane_mode1_off, udid), 1, true, true, 1);
        new ExecuteCommand().executeCommand(getCommand(enable_wifi1, udid), 1, true, true, 1);
//        this.networkToggle(udid, Arrays.asList("wifi", "data", "airplane"), Arrays.asList(true, false, false));
        success = this.waitForWifi(true, udid);
        if (!success) {
//          this.networkToggle(udid, Arrays.asList("wifi", "data", "airplane"), Arrays.asList(false, true, false));
//          this.networkToggle(udid, Arrays.asList("wifi", "data", "airplane"), Arrays.asList(true, false, false));
          success = this.waitForWifi(true, udid);
        }

        if (!success) {
          return false;
        }
        operatorName = "";
        try {
          operatorName = this.getOperatorName(udid);
        } catch (Exception var8) {
        }

        if (!operatorName.trim().equalsIgnoreCase("")) {
          success = this.waitForData(false, udid);
          if (!success) {
            return false;
          }
        }
      }
    }
    return true;
  }

  public boolean waitForWifi(boolean up, String udid){
    String wifiIp = "";
    int counter = 10;
    if (up) {
      do {
        wifiIp = getNetworkIPAddress(true, udid);
        counter--;
      } while (wifiIp.trim().equalsIgnoreCase("") && counter > 0);
      if (wifiIp.trim().equalsIgnoreCase("")) {
        return false;
      }
    } else {
      do {
        wifiIp = getNetworkIPAddress(true, udid);
        counter--;
      } while (!wifiIp.trim().equalsIgnoreCase("") && counter > 0);
      if (!wifiIp.trim().equalsIgnoreCase("")) {
        return false;
      }
    }
    return true;
  }

//  public void networkToggle(String udid, List<String> networkType, List<Boolean> mode) throws Exception {
//    if (!this.isAppInstalled(udid, "io.appium.networktoggle")) {
//      this.installAppFromResources(udid, "network_toggle.apk");
//    }
//
//    String arguments = "";
//
//    for(int i = 0; i < networkType.size(); ++i) {
//      String network = ((String)networkType.get(i)).trim();
//      String m = (Boolean)mode.get(i) ? "on" : "off";
//      if (!network.trim().equalsIgnoreCase("airplane") && !network.trim().equalsIgnoreCase("data") && !network.trim().equalsIgnoreCase("3g") && !network.trim().equalsIgnoreCase("2g")) {
//        arguments = arguments + "-e " + network + " " + m + " ";
//      }
//    }
//
//    this.launchApp(udid, "io.appium.networktoggle", "io.appium.networktoggle.MainActivity " + arguments);
//  }

  public boolean waitForData(boolean up, String udid){
    String dataIp = "";
    int counter = 10;
    if (up) {
      do {
        dataIp = getNetworkIPAddress(false, udid);
        counter--;
      } while (dataIp.trim().equalsIgnoreCase("") && counter > 0);
      if (dataIp.trim().equalsIgnoreCase("")) {
        return false;
      }
    } else {
      do {
        dataIp = getNetworkIPAddress(false, udid);
        counter--;
      } while (!dataIp.trim().equalsIgnoreCase("") && counter > 0);
      if (!dataIp.trim().equalsIgnoreCase("")) {
        return false;
      }
    }
    return true;
  }


  public boolean disableWifi(String udid) throws Exception {
    String output = getNetworkIPAddress(true, udid);
    if (!output.trim().equalsIgnoreCase("")) {
      new ExecuteCommand().executeCommand(getCommand(airplane_mode_off,udid),1,true,true,1);
      new ExecuteCommand().executeCommand(getCommand(airplane_mode1_off,udid),1,true,true,1);
      new ExecuteCommand().executeCommand(getCommand(disable_wifi1,udid),1,true,true,1);
      boolean success = waitForWifi(false, udid);
      if (!success) {
//        networkToggle(udid, Arrays.asList("wifi", "data", "airplane"),
//            Arrays.asList(false, false, false));
        return waitForWifi(false, udid);
      }
    }
    return true;
  }


  public boolean enableWifi(String udid) throws Exception {
    String output = getNetworkIPAddress(true, udid);
    if (output.trim().equalsIgnoreCase("")) {
      new ExecuteCommand().executeCommand(getCommand(airplane_mode_off,udid),1,true,true,1);
//      new ExecuteCommand().executeCommand(getCommand(airplane_mode1_off,udid),1,true,true,1);
      new ExecuteCommand().executeCommand(getCommand(enable_wifi1,udid),1,true,true,1);
      boolean success = waitForWifi(true, udid);
      if (!success) {
//        networkToggle(udid, Arrays.asList("wifi", "data", "airplane"),
//            Arrays.asList(true, false, false));
//        return waitForWifi(true, udid);
      }
    }
    return true;
  }

  public String getOperatorName(String udid) {
    String operator = "";
    if (DriverFactory.environment.get("driverType").equalsIgnoreCase("ios")) {
//      operator = getIOSDeviceDetails(
//          "CarrierBundleInfoArray | grep CFBundleIdentifier | awk '{print $2}'", udid);
//      operator = operator.trim().split("\n")[0];
//    } else if (driverType.trim().toUpperCase().contains("IOS") && devicefarm) {
//      //Do Nothing
    } else {
      String operatorCommand = getCommand(sim_operator, udid);
      Object[] output = new ExecuteCommand().executeCommand(operatorCommand, 1, true, true,1);
      operator = ((String) output[0]).split("\n")[0];
    }
    if (operator.trim().length() < 2) {
      operator = "";
    }
    return operator;
  }

  public void enableCharging(String deviceId) {
    String enableBatteryChargingCommand = getCommand(enable_usb_charging, deviceId);
    new ExecuteCommand().executeCommand(enableBatteryChargingCommand, 1, true, false,1);
    String enableACBatteryChargingCommand = getCommand(enable_ac_charging, deviceId);
    new ExecuteCommand().executeCommand(enableACBatteryChargingCommand, 1, true, false,1);
  }

  public void disableCharging(String deviceId){
    String disableBatteryChargingCommand = getCommand(disable_usb_charging, deviceId);
    new ExecuteCommand().executeCommand(disableBatteryChargingCommand, 1, true, false,1);
    String disableACBatteryChargingCommand = getCommand(disable_ac_charging, deviceId);
    new ExecuteCommand().executeCommand(disableACBatteryChargingCommand, 1, true, false,1);
  }


//  public String[] getDeviceDetails(String udid, String driverType, boolean devicefarm)
//      throws Exception {
//    String model = "", manufacturer = "", operator = "", version = "";
//    if (driverType.trim().toUpperCase().contains("IOS") && !devicefarm) {
//      model = getIOSDeviceDetails("DeviceName", udid);
//      manufacturer = getIOSDeviceDetails("DeviceClass", udid);
//      operator = getIOSDeviceDetails(
//          "CarrierBundleInfoArray | grep CFBundleIdentifier | awk '{print $2}'", udid);
//      version = getIOSDeviceDetails("ProductVersion", udid);
//      model = model.trim().split("\n")[0];
//      manufacturer = manufacturer.trim().split("\n")[0];
//      operator = operator.trim().split("\n")[0];
//      version = version.trim().split("\n")[0];
//    } else if (driverType.trim().toUpperCase().contains("IOS") && devicefarm) {
//      //Do Nothing
//    } else {
//      model = runCommandUsingTerminal(devicefarm,
//          "adb -s " + udid + " shell getprop ro.product.model", false, "1");
//      manufacturer = runCommandUsingTerminal(devicefarm,
//          "adb -s " + udid + " shell getprop ro.product.manufacturer", false, "1");
//      operator = runCommandUsingTerminal(devicefarm,
//          "adb -s " + udid + " shell getprop gsm.sim.operator.alpha", false, "1");
//      version = runCommandUsingTerminal(devicefarm,
//          "adb -s " + udid + " shell getprop ro.build.version.release", false, "1");
//      model = model.trim().split("\n")[0];
//      manufacturer = manufacturer.trim().split("\n")[0];
//      operator = operator.trim().split("\n")[0];
//      version = version.trim().split("\n")[0];
//
//      if (model.trim().contains("error:")) {
//        System.out.println(
//            "SKIPEXCEPTION :: " + "Android device - " + driverType.trim().toUpperCase()
//                + " not found");
//        throw new SkipException(
//            "Android device - " + driverType.trim().toUpperCase() + " not found");
//      }
//    }
//    if (operator.trim().length() < 2) {
//      operator = "";
//    }
//    return new String[]{operator, version, manufacturer, model};
//  }
//
//  public String[] getAndroidDeviceDetails(String udid, boolean devicefarm) throws Exception {
//    String model = "", manufacturer = "", operator = "", version = "";
//    model = runCommandUsingTerminal(devicefarm,
//        "adb -s " + udid + " shell getprop ro.product.model", false, "1");
//    manufacturer = runCommandUsingTerminal(devicefarm,
//        "adb -s " + udid + " shell getprop ro.product.manufacturer", false, "1");
//    operator = runCommandUsingTerminal(devicefarm,
//        "adb -s " + udid + " shell getprop gsm.sim.operator.alpha", false, "1");
//    version = runCommandUsingTerminal(devicefarm,
//        "adb -s " + udid + " shell getprop ro.build.version.release", false, "1");
//    model = model.trim().split("\n")[0];
//    manufacturer = manufacturer.trim().split("\n")[0];
//    operator = operator.trim().split("\n")[0];
//    version = version.trim().split("\n")[0];
//    if (model.trim().contains("error:")) {
//      System.out.println("SKIPEXCEPTION :: " + "Android device - " + udid + " not found");
//      throw new SkipException("Android device - " + udid + " not found");
//    }
//    if (operator.trim().length() < 2) {
//      operator = "";
//    }
//    return new String[]{operator, version, manufacturer, model};
//  }

  public String getAudioFocusStack(String udid) throws Exception {
    String cmdQuery = getCommand(audio_focus, udid);
    Object[] output = new ExecuteCommand().executeCommand(cmdQuery, 1, false, false);
    return (String) output[0];
  }

  public String getAudioState(String udid, String packageId) throws Exception {
    String cmdQuery1 = getCommand(appPID, udid, packageId);
    Object[] output1 = new ExecuteCommand().executeCommand(cmdQuery1, 1, true, false);
    String cmdQuery2 = getCommand(getAudioState, udid, (String) output1[0]);
    Object[] output2 = new ExecuteCommand().executeCommand(cmdQuery2, 1, true, false);
    return (String) output2[0];
  }


  /**
   * @param url In case of TV: MWTV/livetv/MWTV_LIVETVCHANNEL_562 In case of Movies:
   *            EROSNOW/MOVIE/EROSNOW_MOVIE_6968305
   * @throws Exception
   */
  public void openDeepLinkUrl(String url) throws Exception {
    String packageName = DriverFactory.environment.get("appPackage");
    String activity = DriverFactory.environment.get("appActivity");
    String command =
        "adb -s " + Drivers.getUdid() + " shell am start -n "
            + packageName + "/"
            + activity
            + " -d 'wynkpremiere://" + url + "/q'";
//            .replace(":/", "") + "\"";
    System.out.println("Deeplink: " + command);
    new ExecuteCommand().executeCommand(command, 1, false, false);
  }

  public void pressAndroidHomeKey(String deviceID){
    String command = getCommand(press_homeKey,deviceID);
    new ExecuteCommand().executeCommand(command,1,false,true);
  }

  public void captureScreenshot(String deviceID,String path){
    String command = getCommand(captureScreenshots,deviceID,path);
    new ExecuteCommand().executeCommand(command,1,false,true,1);
  }

  public void clearLogcat(String deviceID) {
    String command = getCommand(clearLogcat, deviceID);
    Thread t = new Thread(() -> new ExecuteCommand().executeCommand(command, 1, false, false, 1));
    t.start();
  }



}
