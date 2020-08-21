package test;

import java.io.File;

public interface Constants {

  String HOME_DIRECTORY = System.getProperty("user.home");

  String ADB_PATH =
      HOME_DIRECTORY + File.separator + "Library" + File.separator + "Android"
          + File.separator + "sdk" + File.separator + "platform-tools" + File.separator;

}
