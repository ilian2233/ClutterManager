package com.company;

import lombok.extern.log4j.Log4j2;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
public class Main {

    private static void sortFiles(Map<String, String> map) {
        log.info("Start sorting files.");
        if (map == null) {
            return;
        }
        map.forEach(Main::moveAllFilesWithSameExtension);
    }

    private static void moveAllFilesWithSameExtension(String extension, String directory) {

        log.info("Moving all '" + extension + "' files to the folder '" + directory + "'");
        File[] files = new File(".").listFiles((dir, name) -> name.endsWith("." + extension));
        if (files == null) {
            return;
        }
        List<File> fileList = Arrays.asList(files);

        if (fileList.isEmpty()) {
            return;
        }

        File newDir = new File(directory);
        if (!newDir.exists()) {
            try {
                if (!newDir.mkdir()) {
                    System.out.println("Directory not created!");
                    return;
                }
            } catch (SecurityException e) {
                System.out.println("Directory not created!");
                return;
            }
        }

        final File finalNewDir = newDir;
        fileList.forEach(file -> {
            try {
                Files.move(file.toPath(), Paths.get(finalNewDir.toPath() + "/" + file.getName()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }

    private static void help() {
        log.info("Displaying help.");
        String help;

        try {
            InputStream readMe = Main.class.getClassLoader().getResourceAsStream("ReadMe");
            help = new String(readMe.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            help = "Error occurred!\nPlease reinstall the program.";
        }

        System.out.println(help);
    }

    private static void saveMap(Map<String, String> map) throws IOException {
        log.info("Saving map.");
        File mapSaveFile = new File(".map");
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
            log.info("Trying to find existing map.");
            fileInputStream = new FileInputStream(".map");
        } catch (FileNotFoundException e) {
            log.info("Map not found; Creating new Map.");
            return new HashMap<>();
        }
        log.info("Map retrieved.");
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
        return (Map<String, String>) objectInputStream.readObject();
    }

    public static void main(String[] args) {

        if (args.length < 1) {
            log.info("No arguments found displaying help.");
            help();
            return;
        }

        Map<String, String> extensionToStringMap;
        try {
            log.info("Trying to retrieve map.");
            extensionToStringMap = retrieveMap();
        } catch (IOException | ClassNotFoundException e) {
            log.info("Critical fail!");
            log.error(e.getStackTrace());
            System.out.println("Error creating map!");
            return;
        }

        log.info("First parameter: " + args[0]);
        switch (args[0]) {
            case "-start":
                sortFiles(extensionToStringMap);
                System.out.println("Files sorted");
                break;
            case "-add":
                if ((!args[1].isBlank()) && (!args[2].isBlank())) {
                    log.info("Adding instructions for '." + args[1] + "' to " + args[2] + ".");
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
                log.info("Removing instructions for '." + args[1] + "'.");
                extensionToStringMap.remove(args[1]);
                break;
            default:
                help();
                break;
        }

        try {
            saveMap(extensionToStringMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
