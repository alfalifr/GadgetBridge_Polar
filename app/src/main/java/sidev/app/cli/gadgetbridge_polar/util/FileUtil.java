package sidev.app.cli.gadgetbridge_polar.util;

import java.io.File;
import java.io.IOException;

import nodomain.freeyourgadget.gadgetbridge.util.FileUtils;

public class FileUtil {
    private FileUtil(){}
    public static File getCsvFileDest(String fileName) throws IOException {
        File dir = FileUtils.getExternalFilesDir("csv");
        return new File(dir.getAbsolutePath(), fileName);
    }
/*
    public static File getFileDest(String fileName) throws IOException {
        File dir = FileUtils.getExternalFilesDir("csv");
        return new File(dir.getAbsolutePath(), fileName);
    }
 */
}
