package org.example;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Character.isDigit;

public class Day3 {

    public static void main(String[] args) throws URISyntaxException, IOException {
        String fileName = "day-3-input.txt";

        ClassLoader classLoader = Day1.class.getClassLoader();
        URL resource = classLoader.getResource(fileName);
        var file = new File(resource.toURI());

        part1(file);
        part2(file);

    }

    private static void part2(File file) throws IOException {
        var lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);

        var asterisks  = new ArrayList<AsteriskDto>();

        for (int line = 0; line < lines.size(); line ++) {
            for(int column = 0; column < lines.get(line).length(); column++) {
                String currentLine = lines.get(line);
                if (currentLine.charAt(column) == '*') {
                    List<Integer> numbers = new ArrayList<>();

                    calculateNumbersInSameLine(column, currentLine, numbers);

                    if(line != 0) {
                        String previousLine = lines.get(line - 1);
                        calculateNumbersInLine(previousLine, column, numbers);
                    }

                    if(line < lines.size()) {
                        String nextLine = lines.get(line + 1);
                        calculateNumbersInLine(nextLine, column, numbers);
                    }

                    if(numbers.size() == 2) {
                        asterisks.add(AsteriskDto.builder()
                                .line(line)
                                .column(column)
                                .numbers(numbers)
                                .build());
                    }
                }
            }
        }

        var result = asterisks.stream()
                .map(AsteriskDto::getGearScore)
                .reduce(0, Integer::sum);
        System.out.println("Part2 " + result);
    }

    private static void calculateNumbersInLine(String nextLine, int column, List<Integer> numbers) {
        if(isDigit(nextLine.charAt(column))) {
            numbers.add(getFullNumber(column, nextLine));
        } else {
            if(column > 0 && isDigit(nextLine.charAt(column - 1))) {
                numbers.add(getFullNumber(column - 1, nextLine));
            }
            if(column + 1 < nextLine.length() && isDigit(nextLine.charAt(column + 1))) {
                numbers.add(getFullNumber(column + 1, nextLine));
            }
        }
    }

    private static Integer getFullNumber(int column, String previousLine) {
        var left = extractLeftNumber(column, previousLine);
        var right = extractRightNumber(column, previousLine);
        return Integer.valueOf(left.append(previousLine.charAt(column)).append(right).toString());
    }

    private static void calculateNumbersInSameLine(int column, String currentLine, List<Integer> numbers) {
        if (column != 0 && isDigit(currentLine.charAt(column - 1 ))) {
            var number = extractLeftNumber(column, currentLine);
            numbers.add(Integer.valueOf(number.toString()));
        }

        if (column < currentLine.length() - 1 && isDigit(currentLine.charAt(column + 1 ))) {
            var number = extractRightNumber(column, currentLine);
            numbers.add(Integer.valueOf(number.toString()));
        }
    }

    private static StringBuilder extractLeftNumber(int column, String currentLine) {
        var number = new StringBuilder();
        var finish = column - 1;
        while (finish >= 0 && isDigit(currentLine.charAt(finish))) {
            number.append(currentLine.charAt(finish));
            finish--;
        }
        return number.reverse();
    }

    private static StringBuilder extractRightNumber(int column, String currentLine) {
        var number = new StringBuilder();
        var finish = column + 1;
        while (finish < currentLine.length() && isDigit(currentLine.charAt(finish))) {
            number.append(currentLine.charAt(finish));
            finish++;
        }
        return number;
    }

    private static void part1(File file) throws IOException {
        var lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);

        var numbers  = new ArrayList<NumberDto>();

        for (int line = 0; line < lines.size(); line ++) {
            for(int column = 0; column < lines.get(line).length(); column++) {
                StringBuilder number = new StringBuilder();
                int lastDigitPlace = column;
                while (lastDigitPlace < lines.get(line).length()
                        && isDigit(lines.get(line).charAt(lastDigitPlace))) {
                    number.append(lines.get(line).charAt(lastDigitPlace));
                    lastDigitPlace++;
                }
                if (number.length() > 0) {
                    numbers.add(NumberDto.builder()
                           .line(line)
                           .firstColumn(column)
                           .lastColumn(lastDigitPlace - 1)
                           .number(Integer.valueOf(number.toString()))
                           .build());
                    column = lastDigitPlace;
                }
            }
        }

        var result = numbers.stream().filter(n -> isValid(n, lines))
                .map(NumberDto::getNumber)
                .reduce(0, Integer::sum);
        System.out.println("Part1 " + result);
    }

    private static boolean isValid(NumberDto numberDto, List<String> data) {
        List<Character> surroundingChars = new ArrayList<>();

        int lineOfNumberInQuestion = numberDto.getLine();

        if(lineOfNumberInQuestion != 0){
            var prevLine = data.get(lineOfNumberInQuestion - 1);
            addSurroundingChars(numberDto, surroundingChars, prevLine);
        }

        if(lineOfNumberInQuestion < data.size() - 1){
            var nextLine = data.get(lineOfNumberInQuestion + 1);
            addSurroundingChars(numberDto, surroundingChars, nextLine);
        }

        var currentLine = data.get(lineOfNumberInQuestion);
        if(numberDto.getFirstColumn() != 0) {
            surroundingChars.add(currentLine.charAt(numberDto.getFirstColumn() - 1));
        }
        if(numberDto.getLastColumn() < currentLine.length() - 1) {
            surroundingChars.add(currentLine.charAt(numberDto.getLastColumn() + 1));
        }

        return surroundingChars.stream().anyMatch(Day3::isSymbol);
    }

    private static void addSurroundingChars(NumberDto numberDto, List<Character> surroundingChars, String line) {
        var firstColumn = numberDto.getFirstColumn() > 0 ? numberDto.getFirstColumn() - 1 : numberDto.getFirstColumn();
        var lastColumn = numberDto.getLastColumn() < line.length() - 1 ? numberDto.getLastColumn() + 1 : numberDto.getLastColumn();
        while(firstColumn <= lastColumn){
            surroundingChars.add(line.charAt(firstColumn));
            firstColumn ++;
        }
    }

    private static boolean isSymbol(char testChar) {
        return !isDigit(testChar) && '.' != testChar;
    }


    @Getter
    @Builder
    private static class AsteriskDto {
        private int line;
        private int column;
        @Singular
        private List<Integer> numbers;

        public Integer getGearScore(){
            return numbers.stream().reduce(1, (a, b) -> a * b);
        }
    }

    @Getter
    @Builder
    private static class NumberDto {
        private int line;
        private int firstColumn;
        private int lastColumn;
        private Integer number;
    }
}
