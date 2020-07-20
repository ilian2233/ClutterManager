package com.company;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    private static void sortFiles(Map<String, String> map) {
        if (map == null) {
            return;
        }
        map.forEach(Main::moveAllFilesWithSameExtension);
    }

    private static void moveAllFilesWithSameExtension(String i, String j) {

        File[] files = new File("").listFiles((dir, name) -> name.endsWith("." + i));
        if (files == null) {
            return;
        }
        List<File> fileList = Arrays.asList(files);

        if (fileList.isEmpty()) {
            return;
        }

        File newDir = new File(j);
        int k = 1;
        while (newDir.exists() || newDir.isDirectory()) {
            newDir = new File(j + k);
            k++;
        }

        try {
            if (!newDir.createNewFile() && !newDir.mkdir()) {
                System.out.println("Directory not created!");
                return;
            }
        } catch (IOException e){
            System.out.println("Directory not created!");
            return;
        }

        final File finalNewDir = newDir;

        fileList.forEach(file -> {
            try {
                Files.move(file.toPath(), finalNewDir.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }

    private static void help() {

        String help;

        try {
            FileInputStream readMe = new FileInputStream("ReadMe");
            help = new String(readMe.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            help = "Error occurred!\nPlease reinstall the program.";
        }

        System.out.println(help);
    }

    private static void saveMap(Map<String, String> map) throws IOException {
        File mapSaveFile = new File("map");
        mapSaveFile.createNewFile();
        FileOutputStream fileOutputStream = new FileOutputStream(mapSaveFile);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(map);
        objectOutputStream.close();
        fileOutputStream.close();
    }

    private static Map<String, String> retrieveMap() throws ClassNotFoundException, IOException {
        FileInputStream fileInputStream;
        try {
            fileInputStream = new FileInputStream("map");
        } catch (FileNotFoundException e) {
            return new HashMap<>();
        }
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
        return (Map<String, String>) objectInputStream.readObject();
    }

    public static void main(String[] args) {

        Map<String, String> extensionToStringMap;
        try {
            extensionToStringMap = retrieveMap();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        if (args.length < 1) {
            help();
            return;
        }

        switch (args[0]) {
            case "-start":
                sortFiles(extensionToStringMap);
                System.out.println("Files sorted");
                break;
            case "-add":
                if ((!args[1].isBlank()) && (!args[2].isBlank())) {
                    extensionToStringMap.put(args[1], args[2]);
                    System.out.println("Extension added.");
                } else {
                    help();
                }
                break;
            case "-remove":
                if (extensionToStringMap == null) {
                    break;
                }
                if (args[1].isBlank()) {
                    help();
                }
                extensionToStringMap.remove(args[1]);
                //TODO: add removing directory
                break;
            default:
                help();
                break;
        }

        //TODO: add logger

        try {
            saveMap(extensionToStringMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
