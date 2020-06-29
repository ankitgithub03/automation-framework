package utils;

import java.text.MessageFormat;

public class MobileCommands {
    public enum ADB{
        ENABLE_WIFI, DISABLE_WIFI, INSTALLED_APPS, AUDIO_FOCUS, GET_SIM_OPERATOR, GET_SIM_NETWORK_TYPE, IS_ROAMING, MCC
    }

    public static String getAdbCommand(ADB key, String... params) {
        String command = "";
        switch(key){
            case ENABLE_WIFI:
                command = "adb -s {0} shell svc wifi enable";
                break;

            case DISABLE_WIFI:
                command = "adb -s {0} shell svc wifi disable";
                break;

            case INSTALLED_APPS:
                command = "adb -s {0} shell pm list packages -3'|cut -f 2 -d ':";
                break;

            case AUDIO_FOCUS:
                command = "adb -s {0} shell dumpsys audio | grep -A1 \"Audio Focus stack entries (last is top of stack):\"";
                break;

            case GET_SIM_NETWORK_TYPE:
                command = "gsm.sim.operator.alpha";
                break;

            case GET_SIM_OPERATOR:
                command = "gsm.network.type";
                break;

            case IS_ROAMING:
                command = "gsm.operator.isroaming";
                break;

            case MCC:
                command = "gsm.sim.operator.numeric";
                break;

        }
        String value = MessageFormat.format(command, (Object[]) params);
        return value;
    }
}
