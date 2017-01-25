package de.projectfluegelrad.database;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

public class DatabaseLoginTask implements DatabaseTask<Void, User> {

    @Override
    public User execute(DatabaseManager databaseManager, Void... params) {
        int attempt = 0;

        while(attempt < 2) {
            try {
                File userFile = new File(databaseManager.getFilesDirectory(), "user.dat");
                String userJson = null;
                if (userFile.exists()) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(userFile)));

                    StringBuilder jsonBuilder = new StringBuilder();
                    String inputLine;
                    while ((inputLine = in.readLine()) != null)
                        jsonBuilder.append(inputLine);
                    in.close();
                    userJson = jsonBuilder.toString();
                } else {
                    URL url = new URL("http://fluegelrad.ddns.net/createUser.php");
                    URLConnection c = url.openConnection();
                    BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream()));

                    StringBuilder jsonBuilder = new StringBuilder();
                    String inputLine;
                    while ((inputLine = in.readLine()) != null)
                        jsonBuilder.append(inputLine);
                    in.close();

                    userJson = jsonBuilder.toString();

                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(userFile)));
                    writer.write(userJson);
                    writer.close();
                }

                if (userJson.startsWith("Error:"))
                    throw new DatabaseException(userJson);

                JSONArray array = new JSONArray(new JSONTokener(userJson));
                int id = array.getInt(0);
                String token = array.getString(1);

                User user = new User(id, token);

                return user;
            } catch(Exception e) {
                e.printStackTrace();

                attempt++;
            }
        }

        return null;
    }
}
