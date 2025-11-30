package com.umbra;

import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {
        Scanner input = new Scanner(System.in);
        UmbraProject.addModule(input.nextLine()+".um");
        input.close();
    }
}