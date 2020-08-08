package ui.app.ios;

public class IosUtility {

//
//
//  public String getIOSDeviceDetails(String command, String udid) throws Exception{
//    String output = "";
//    if(Environment.get("useIosEmulator").trim().equalsIgnoreCase("true")) {
//      //Do Nothing
//    } else {
//      if(!udid.trim().equalsIgnoreCase("")){
//        output = runCommandUsingTerminal(devicefarm, "ideviceinfo -u " + udid + " -k " + prop, false, "1");
//      } else{
//        output = runCommandUsingTerminal(devicefarm, "ideviceinfo -k " + prop, false, "1");
//      }
//    }
//    return output.trim();
//  }
//
//  public void uninstallIOSAppUsingCLI(String bundleId, String udid) throws Exception {
//    if(Environment.get("useIosEmulator").trim().equalsIgnoreCase("true")) {
//      if(!udid.trim().equalsIgnoreCase("")) {
//        runCommandUsingTerminal(devicefarm, "xcrun simctl uninstall " + udid + " " + bundleId, false, "1", "WAIT");
//      } else{
//        runCommandUsingTerminal(devicefarm, "xcrun simctl uninstall " + bundleId, false, "1", "WAIT");
//      }
//    } else {
//      if(!udid.trim().equalsIgnoreCase("")){
//        runCommandUsingTerminal(devicefarm, "ideviceinstaller -u " + udid + " -U " + bundleId, false, "1", "Complete");
//      } else{
//        runCommandUsingTerminal(devicefarm, "ideviceinstaller -U " + bundleId, false, "1", "Complete");
//      }
//    }
//  }
//
//  public void installIOSAppUsingCLI(String appPath, String udid) throws Exception {
//    if(Environment.get("useIosEmulator").trim().equalsIgnoreCase("true")) {
//      if(!udid.trim().equalsIgnoreCase("")){
//        runCommandUsingTerminal(devicefarm, "xcrun simctl install " + udid + " " + appPath, false, "1", "WAIT");
//      } else{
//        runCommandUsingTerminal(devicefarm, "xcrun simctl install " + appPath, false, "1", "WAIT");
//      }
//    } else {
//      if(!udid.trim().equalsIgnoreCase("")){
//        runCommandUsingTerminal(devicefarm, "ideviceinstaller -u " + udid + " -i " + appPath, false, "1", "Complete");
//      } else{
//        runCommandUsingTerminal(devicefarm, "ideviceinstaller -i " + appPath, false, "1", "Complete");
//      }
//    }
//  }

}
