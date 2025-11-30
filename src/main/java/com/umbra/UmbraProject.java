package com.umbra;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

import com.umbra.UmbraFile.Value;

public class UmbraProject {
    static HashMap<String, UmbraFile> modules = new HashMap<>();
    static Scanner input = new Scanner(System.in);

    public static HashMap<String, Value> projectVar = new HashMap<>();

    public static void addModule(String modulePath) {
        try {
            String name = modulePath;
            if (modulePath.contains("/")) {
                name = modulePath.substring(modulePath.indexOf('/'));
            }
            name = name.substring(0, name.indexOf('.'));

            modules.put(name, new UmbraFile(name, Path.of(modulePath)));
        } catch (IOException e) {
            System.err.println("error: module \""+modulePath+"\" not found");
        }
    }
    
}
