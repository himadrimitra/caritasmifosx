package com.finflux.common.util;

import java.io.File;

public class FinfluxFileUtils {

    public static File createDirectories(final boolean isHierarchy, final String... finalDirectories) {
        File file = null;
        for (String finalDirectory : finalDirectories) {
            if (isHierarchy && file != null) {
                finalDirectory = file.getPath() + File.separator + finalDirectory;
            }
            file = new File(finalDirectory);
            if (!file.exists()) {
                file.mkdir();
            }
        }
        return file;
    }
}
