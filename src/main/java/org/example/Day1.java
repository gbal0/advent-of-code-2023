package org.example;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Map;


public class Day1 {
    private final static Map<String, String> numbers = Map.of("one", "1",
            "two", "2",
            "three", "3",
            "four", "4",
            "five", "5",
            "six", "6",
            "seven", "7",
            "eight", "8",
            "nine", "9");

    public static void main( String[] args ) throws URISyntaxException {
        String fileName = "day-1-input.txt";

        ClassLoader classLoader = Day1.class.getClassLoader();
        URL resource = classLoader.getResource(fileName);
        var file = new File(resource.toURI());

        part1(file);
        part2(file);

    }

    private static void part1(File file) {
        try {
            var lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
            var sum = lines.stream().map(line -> {
                var onlyNumbers = line.replaceAll("[^\\d.]", "");
                return Integer.valueOf(String.valueOf(onlyNumbers.charAt(0)) + onlyNumbers.charAt(onlyNumbers.length() - 1));
            }).reduce(0, Integer::sum);

            System.out.println("Part1 " + sum);
        } catch (IOException e) {
            System.out.println("Error while reading file");
        }
    }

    private static void part2(File file) {
        try {
            var lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
            var sum = lines.stream().map(line -> {
                var listOfOccurrences = new ArrayList<String>();

                for(int i = 0; i < line.length(); i++){
                    String s = line.substring(i);
                    numbers.keySet().stream().filter(s::startsWith).findFirst().ifPresent(key -> listOfOccurrences.add(numbers.get(key)));
                    numbers.values().stream().filter(s::startsWith).findFirst().ifPresent(listOfOccurrences::add);
                }
                return Integer.valueOf(listOfOccurrences.get(0) + listOfOccurrences.get(listOfOccurrences.size() - 1));

            }).reduce(0, Integer::sum);

            System.out.println("Part2 " + sum);
        } catch (IOException e) {
            System.out.println("Error while reading file");
        }
    }

}
