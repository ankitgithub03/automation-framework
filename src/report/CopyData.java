package report;

import java.io.File;
import java.io.IOException;

public class CopyData {

	public static void copyDirectory(File sourceLocation , File targetLocation)throws IOException {
        if (sourceLocation.isDirectory()) {
            if (!targetLocation.exists()) {
                targetLocation.mkdir();
            }
            String[] children = sourceLocation.list();
            for (int i=0; i<children.length; i++) {
                copyDirectory(new File(sourceLocation, children[i]),
                        new File(targetLocation, children[i]));
            }
        }
    }

}
