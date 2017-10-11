package com.finflux.common.util;

import java.io.File;

import org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil;

public class FinfluxFileUtils {

    public static String getFinfluxBaseDirectory() {
        return System.getProperty("user.home") + File.separator + ".finflux";
    }

    public static String generateFileLocation(final String parentDirectoryPath, final String... childDirectoriesPaths) {
        String fileLocation = FinfluxFileUtils.getFinfluxBaseDirectory() + File.separator
                + ThreadLocalContextUtil.getTenant().getName().replaceAll(" ", "").trim() + File.separator + parentDirectoryPath;
        for (final Object childDirectory : childDirectoriesPaths) {
            fileLocation += File.separator + childDirectory;
        }
        return fileLocation;
    }
}
