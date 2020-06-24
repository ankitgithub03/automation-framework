package ui.app.iOS;

public interface iOSCommands {

  String physicallyConnected_iOS_Devices = "idevice_id -l";

  String iphone_info = "ideviceinfo -u {0} -s";

  // to check that specific bundleID is present in phone or not
  String isAppInstalled = "ios-deploy -u {0} --exists --bundle_id {0}";

  String getAllInstalledAppPackage = "ideviceinstaller -u {0} -l";

  // {1} is ipa file path
  String install_IPAFile = "ideviceinstaller -u {0} -i {1}";

  // {1} is a package name
  String uninstall_IPAFile = "ideviceinstaller -u {0} -U {1}";

}
