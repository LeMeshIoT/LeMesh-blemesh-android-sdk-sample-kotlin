package cn.lelight.iot.blemesh.demo.utils;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtils {
    public static boolean copyAssetsFileToCacheDir(Context context, String fileName) {
        try {
            File cacheDir = context.getCacheDir();
            if (!cacheDir.exists()) {
                cacheDir.mkdirs();
            }
            //
            File outFile = new File(cacheDir, fileName);
            if (!outFile.exists()) {
                // 文件不在
                boolean res = false;

                res = outFile.createNewFile();

                if (!res) {
                    // false
                    return false;
                }
            } else {
                // true
                return true;
            }
            InputStream open = context.getAssets().open(fileName);
            FileOutputStream fos = new FileOutputStream(outFile);
            byte[] buffer = new byte[1024];
            int byteCount;
            while ((byteCount = open.read(buffer)) != -1) {
                fos.write(buffer, 0, byteCount);
            }
            fos.flush();
            open.close();
            fos.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
