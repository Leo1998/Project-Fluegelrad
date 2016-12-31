package de.projectfluegelrad.database;

import android.util.Base64;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.util.UUID;

public class Utils {

    private Utils() {}

    public static void copyStream(InputStream in, OutputStream out) throws IOException {
        byte data[] = new byte[4096];
        int count;
        while ((count = in.read(data)) != -1) {
            out.write(data, 0, count);
        }
    }

    public static String hashPath(String s) {
        try {
            byte[] bytesOfMessage = s.getBytes("UTF-8");

            return UUID.nameUUIDFromBytes(bytesOfMessage).toString();
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}