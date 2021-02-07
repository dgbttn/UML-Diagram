package parser;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipExtracter {
    public static String extract(String path, String output){

        File file = new File(output);
        if (!file.exists()) file.mkdirs();

        byte[] buffer = new byte[1024];

        try {
            ZipInputStream zipIS = new ZipInputStream(new FileInputStream(path));
            ZipEntry entry=null;
            while ((entry = zipIS.getNextEntry()) != null) {
                String entryName = entry.getName();
                String outputFile = output + File.separator + entryName;

                if (entry.isDirectory()) new File(outputFile).mkdirs();
                else {
                    FileOutputStream fos = new FileOutputStream(outputFile);
                    int len;
                    while ((len = zipIS.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                    fos.close();
                }
            }
            zipIS.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return output;
    }
}