package ui.app.android;

import java.text.MessageFormat;
import test.Constants;

public abstract class AndroidCommands {

  String connected_android_devices1 = "(lsof -i tcp:5037 | grep LISTEN | awk \''{print $2}\'' | xargs kill -9) && (adb devices)";
  String connected_android_devices = "adb devices";
  String android_OS_version = "adb -s {0} shell getprop ro.build.version.release";
  String android_SDK_version = "adb -s {0} shell getprop ro.build.version.sdk";
  String android_device_brandName = "adb -s {0} shell getprop ro.vendor.product.brand";
  String android_device_manufacture = "adb -s {0} shell getprop ro.product.manufacturer";
  String android_device_model_number = "adb -s {0} shell getprop ro.product.model";
  String sim_operator = "adb -s {0} shell getprop gsm.sim.operator.alpha";
  String sim_country_code = "adb -s {0} shell getprop gsm.sim.operator.iso-country";

  String installApk = "adb -s {0}  install -r {1}";   // {1}= app path
  String uninstallApk = "adb -s {0}  uninstall {1}";  //
  String launchApp = "adb -s {0}  shell am start -S -n {1}/{2}";  // {0}= udid, {1}=package,{2}=activity



  String check_installedPackage = "adb -s {0} shell pm list packages {1}";
  String installed_apps = "adb -s {0} shell pm list packages -3'|cut -f 2 -d ':";
  String installed_appVersion = "adb -s {0} shell dumpsys package {1} | grep versionName";
  String installed_buildNumber = "adb -s {0} shell dumpsys package {1} | grep versionCode | cut -d '=' -f2";
  String current_focusApp_package = "adb -s {0} shell dumpsys window windows | grep -E \"mCurrentFocus\" | cut -d '/' -f1 | sed \"s/.* //g\"";
  String current_focusApp_activity = "adb -s {0} shell dumpsys window windows | grep -E \"mCurrentFocus\" | cut -d '/' -f2 | cut -d '}' -f1";

  String android_Device_TimeZone = "shell getprop gsm.nitz.timezone_id";

  String deepLinks = "adb -s {0} shell am start -a {1} -d {2} ";

  String clearLogcat = "adb -s {0} shell logcat -c";
  String crash_logs = "adb -s {0} shell logcat -b crash";
  String clean_cache = "adb -s {0} shell pm clear {1}";
  String capture_httpLogs = "adb -s {0} logcat | grep \"D OkHttp  \"";
  String force_stopApp = "adb -s {0} shell am force-stop {1}";


  String battery_Level = "adb -s {0}  shell dumpsys battery | grep level";
  String reset_battery_stat = "adb -s {0} shell dumpsys battery reset";
  String get_battery_stat = "adb -s {0} shell dumpsys battery";
  String generate_bug_report ="adb -s {0} bugreport";
  String disable_usb_charging ="adb -s {0} shell dumpsys battery set usb 0";
  String enable_usb_charging ="adb -s {0} shell dumpsys battery set usb 1";
  String disable_ac_charging = "adb -s {0} shell dumpsys battery set ac 0";
  String enable_ac_charging = "adb -s {0} shell dumpsys battery set ac 1";

  String connected_wifi_Status = "adb -s {0} shell dumpsys wifi | grep \"Wi-Fi is\"";

  String enable_wifi = "adb -s {0} shell am broadcast -a io.appium.settings.wifi --es setstatus enable";
  String disable_wifi = "adb -s {0} shell am broadcast -a io.appium.settings.wifi --es setstatus disable";
  String enable_wifi1 = "adb -s {0} shell svc wifi enable";
  String disable_wifi1 = "adb -s {0} shell svc wifi disable";
  String getSim1Operator = "gsm.sim.operator.alpha";
  String getSimNetworkType = "gsm.network.type";
  String checkIfSimIsInRoaming = "gsm.operator.isroaming";
  String mcc = "gsm.sim.operator.numeric";
  String is_wifi_present = "adb -s {0} shell dumpsys netstats | grep -E \"iface=wlan.*networkId\"";
  String wifi_ip = "adb -s {0} shell ip route | grep \"{1} .*link.*src\" | awk \''{print $9}\''";
  String airplane_mode_off = "adb -s {0} shell settings put global airplane_mode_on 0";
  String airplane_mode_on = "adb -s {0} shell settings put global airplane_mode_on 1";
  String airplane_mode1_off = "adb -s {0} shell am broadcast -a android.intent.action.AIRPLANE_MODE --ez state false";
  String airplane_mode1_on = "adb -s {0} shell am broadcast -a android.intent.action.AIRPLANE_MODE --ez state true";

  String press_homeKey = "adb -s {0} shell input keyevent KEYCODE_HOME";

  String event_Logs = "adb -s {0} logcat | grep FILE_EVENT_QUEUE";

  String uninstall_settings1 = "adb -s {0} uninstall io.appium.uiautomator2.server";
  String uninstall_settings2 = "adb -s {0} uninstall io.appium.uiautomator2.server.test";
  String uninstall_settings3 = "adb -s {0} uninstall io.appium.unlock";
  String uninstall_settings4 = "adb -s {0} uninstall io.appium.settings";

  String appPID = "adb -s {0} shell pidof -s {1}";
  String getAudioState = "adb -s {0} shell dumpsys audio | grep -H \"{1} -- state\"";
  String captureScreenshots = "adb -s {0} exec-out screencap -p >{1}";  // {0}- deviceID, {1}- path of the screenshot

  String killAll_node = "killall -9 node";

  String audio_focus = "adb -s {0} shell dumpsys audio | grep -A1 \"Audio Focus stack entries (last is top of stack):\"";

  String getCommand(String key, String... params) {
    String command = MessageFormat.format(key, (Object[]) params);
    return command;
  }


  public static String getAudioStateCommand(String pid){
    return "adb -s {0} shell dumpsys audio | grep -H \""+pid+" -- state\"";
  }


}
