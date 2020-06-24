package ui.app.android;

public interface AndroidCommands {

  String connected_Android_Devices = "adb devices";
  String android_OS_Version = "adb -s {0} shell getprop ro.build.version.release";
  String android_SDK_Version = "adb -s {0} shell getprop ro.build.version.sdk";

  String installApk = "adb -s {0}  install {1}";
  String uninstallApk = "adb -s {0}  uninstall {1}";

  String android_Device_BrandName = "adb -s {0} shell getprop ro.vendor.product.brand";
  String android_Device_Model_Number = "adb -s {0} shell getprop ro.product.model";

  String aheck_InstalledPackage = "adb -s {0} shell pm list packages {1}";
  String installed_AppVersion = "adb -s {0} shell dumpsys package {1} | grep versionName";
  String installed_BuildNumber = "adb -s {0} shell dumpsys package {1} | grep versionCode | cut -d '=' -f2";
  String current_FocusApp_Package = "adb -s {0} shell dumpsys window windows | grep -E 'mCurrentFocus' | cut -d '/' -f1 | sed \"s/.* //g\"";
  String current_FocusApp_Activity = "adb -s {0} shell dumpsys window windows | grep -E 'mCurrentFocus' | cut -d '/' -f2 | cut -d '}' -f1";

  String android_Device_TimeZone = "shell getprop gsm.nitz.timezone_id";

  String deepLinks = "adb -s {0} shell am start -a {1} -d {2} ";

  String clearLogCat_Logs = "adb -s {0} shell logcat -c";
  String crash_Logs = "adb -s {0} shell logcat -b crash";
  String clean_cache = "adb -s {0} shell pm clear {1}";
  String capture_httpLogs = "adb -s {0} logcat | grep \"D OkHttp  \"";
  String force_stopApp = "adb -s {0} shell am force-stop {1}";


  String battery_Level = "adb -s {0}  shell dumpsys battery | grep level";

  String connected_WIFI_Status = "adb -s {0} shell dumpsys wifi | grep \"Wi-Fi is\"";

  String enable_wifi = "adb -s {0} shell am broadcast -a io.appium.settings.wifi --es setstatus enable";
  String disable_wifi = "adb -s {0} shell am broadcast -a io.appium.settings.wifi --es setstatus disable";
  String press_homeKey = "adb -s {0} shell input keyevent KEYCODE_HOME";


  String event_Logs = "adb -s {0} logcat | grep FILE_EVENT_QUEUE";

  String uninstall_settings1 = "adb -s {0} uninstall io.appium.uiautomator2.server";
  String uninstall_settings2 = "adb -s {0} uninstall io.appium.uiautomator2.server.test";
  String uninstall_settings3 = "adb -s {0} uninstall io.appium.unlock";
  String uninstall_settings4 = "adb -s {0} uninstall io.appium.settings";

  String killAll_node = "killall -9 node";


}
