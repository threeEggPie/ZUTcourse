package xyz.kingsword.course.util;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtil {
    public static void zip(File srcFile, File targetFile) {
        try {
            OutputStream targetOutPutStream = new FileOutputStream(targetFile);
            ZipOutputStream zos = new ZipOutputStream(targetOutPutStream);
            if (srcFile.isDirectory()) {
                File[] files = srcFile.listFiles();
                if (files != null) {
                    for (File file : files) {
                        InputStream inputStream = new FileInputStream(file);
                        ZipEntry zipEntry = new ZipEntry(srcFile.getName() + File.separator + file.getName());
                        zos.putNextEntry(zipEntry);
                        byte[] bytes = new byte[100];
                        int len;
                        while ((len = inputStream.read(bytes)) > -1) {
                            zos.write(bytes, 0, len);
                        }
                        zos.closeEntry();
                        inputStream.close();
                        file.delete();
                    }
                }
            }
            zos.close();
            targetOutPutStream.close();
            srcFile.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
