package de.projectfluegelrad.database;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StreamUtils {

    public static void copyStream(InputStream in, OutputStream out) throws IOException {
        byte data[] = new byte[4096];
        int count;
        while ((count = in.read(data)) != -1) {
            out.write(data, 0, count);
        }
    }

}
