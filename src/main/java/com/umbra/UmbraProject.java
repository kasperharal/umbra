package com.umbra;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.*;

import com.umbra.UmbraFile.Value;

public class UmbraProject {
    public static String root = null;
    static HashMap<String, UmbraFile> modules = new HashMap<>();
    static Scanner input = new Scanner(System.in);

    public static HashMap<String, Value> projectVar = new HashMap<>();

    public static void addModule(String modulePath) {
        try {
            if (root == null) {
                root = modulePath.substring(0, modulePath.lastIndexOf('/'));
            }
            if (modulePath.startsWith("/")) {
                modulePath = root + modulePath;
            }

            String name = modulePath;
            if (modulePath.contains("/")) {
                name = modulePath.substring(modulePath.lastIndexOf('/')+1);
            }
            name = name.substring(0, name.indexOf('.'));

            if (modulePath.startsWith("#")) {
                if (name.startsWith("#")) name = name.substring(1);
                String gitCode = downloadFile("https://raw.githubusercontent.com/kasperharal/umbra/refs/heads/main/umbra/stdlib/"+modulePath.substring(1));
                modules.put(name, new UmbraFile(name, gitCode));
            }

            modules.put(name, new UmbraFile(name, Path.of(modulePath)));
        } catch (IOException e) {
            System.err.println("error: module \""+modulePath+"\" not found");
        }
    }

    private static String downloadFile(String fileUrl) throws IOException {
        URL url = null;
        try {
            url = new URI(fileUrl).toURL();
        } catch (MalformedURLException | URISyntaxException e) {
            e.printStackTrace();
        }
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // Optional: Set request method and headers
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(10000); // 10 seconds
        connection.setReadTimeout(10000);

        // Check HTTP response code
        int statusCode = connection.getResponseCode();
        if (statusCode != HttpURLConnection.HTTP_OK) {
            throw new IOException("Failed to download file. HTTP code: " + statusCode);
        }
        String output = "";
        // Read file from input stream and save locally
        try (InputStream in = new BufferedInputStream(connection.getInputStream())) {

            output = new String(in.readAllBytes());
        } finally {
            connection.disconnect();
        }

        return output;
    }
    
}
