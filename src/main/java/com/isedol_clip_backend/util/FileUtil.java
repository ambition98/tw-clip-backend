package com.isedol_clip_backend.util;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

@Slf4j
public class FileUtil {
    public static String getDataFromFilePath(String path) throws IOException {
        StringBuilder sb = new StringBuilder();
        File file = new File(path);
        FileInputStream input = new FileInputStream(file);
        int i;
        while((i = input.read()) != -1) {
            sb.append((char) i);
        }
        input.close();

        return sb.toString();
    }

    public static void putDataToFilePath(String path, byte[] data) throws IOException {
        File file = new File(path);
        FileOutputStream output = new FileOutputStream(file);
        output.write(data);
        output.flush();
        output.close();
    }
}
