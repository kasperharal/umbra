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
            if (modulePath.startsWith("#")) {
                downloadFile("github uri", ""); // TODO
            }
            String name = modulePath;
            if (modulePath.contains("/")) {
                name = modulePath.substring(modulePath.lastIndexOf('/')+1);
            }
            name = name.substring(0, name.indexOf('.'));

            modules.put(name, new UmbraFile(name, Path.of(modulePath)));
        } catch (IOException e) {
            System.err.println("error: module \""+modulePath+"\" not found");
        }
    }

    private static void downloadFile(String fileUrl, String savePath) throws IOException {
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

        // Read file from input stream and save locally
        try (InputStream in = new BufferedInputStream(connection.getInputStream());
             FileOutputStream out = new FileOutputStream(savePath)) {

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        } finally {
            connection.disconnect();
        }
    }
    
}
