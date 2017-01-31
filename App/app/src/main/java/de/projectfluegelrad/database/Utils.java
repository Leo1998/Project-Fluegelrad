package de.projectfluegelrad.database;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class Utils {

    private Utils() {}

    /**
     * writes all data from in to out
     *
     * @param in
     * @param out
     * @throws IOException
     */
    public static void copyStream(InputStream in, OutputStream out) throws IOException {
        byte data[] = new byte[4096];
        int count;
        while ((count = in.read(data)) != -1) {
            out.write(data, 0, count);
        }
    }

    /**
     * hashes a string
     *
     * @param s
     * @return
     */
    public static String hashString(String s) {
        try {
            byte[] bytesOfMessage = s.getBytes("UTF-8");

            return UUID.nameUUIDFromBytes(bytesOfMessage).toString();
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
