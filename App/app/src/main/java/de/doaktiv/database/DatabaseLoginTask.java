package de.doaktiv.database;

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

import de.doaktiv.android.DatabaseService;

/**
 * logs in with the saved user profile or tries to create a new one
 */
public class DatabaseLoginTask extends DatabaseTask<Void, User> {

    @Override
    public User execute(DatabaseService service) {
        int attempt = 0;

        // 2 attempts then cancel
        while (attempt < 2) {
            try {
                File userFile = new File(service.getFilesDirectory(), "user.dat");
                String userJson = null;
                if (userFile.exists()) {
                    // login with saved user
                    BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(userFile)));

                    StringBuilder jsonBuilder = new StringBuilder();
                    String inputLine;
                    while ((inputLine = in.readLine()) != null)
                        jsonBuilder.append(inputLine);
                    in.close();
                    userJson = jsonBuilder.toString();
                } else {
                    // create new user
                    URL url = new URL("http://fluegelrad.ddns.net/scripts/createUser.php");
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

                // error check
                if (userJson.startsWith("Error:"))
                    throw new DatabaseException(userJson);

                JSONArray array = new JSONArray(new JSONTokener(userJson));
                int id = array.getInt(0);
                String token = array.getString(1);

                User user = new User(id, token);

                return user;
            } catch (Exception e) {
                e.printStackTrace();

                attempt++;
            }
        }

        return null;
    }
}
