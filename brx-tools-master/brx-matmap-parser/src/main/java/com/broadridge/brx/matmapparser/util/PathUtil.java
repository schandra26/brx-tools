package com.broadridge.brx.matmapparser.util;

import lombok.experimental.UtilityClass;

import java.net.URL;

@UtilityClass
public class PathUtil {

    public static String getProjectPathFromRepositoryURL(final URL repositoryUrl) {
        return repositoryUrl.getPath().substring(1);
    }

    public static String getFilenameFromFilePath(final String filePath) {
        return filePath.substring(filePath.lastIndexOf('/') + 1);
    }
}
