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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Day2 {

    private static final Integer MAX_RED = 12;
    private static final Integer MAX_GREEN = 13;
    private static final Integer MAX_BLUE = 14;

    public static void main(String[] args) throws URISyntaxException, IOException {
        String fileName = "day-2-input.txt";

        ClassLoader classLoader = Day2.class.getClassLoader();
        URL resource = classLoader.getResource(fileName);
        var file = new File(resource.toURI());

        part1(file);
        part2(file);

    }

    private static void part1(File file) throws IOException {
        var lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
        var games = lines.stream().map(Day2::mapToGameDTO).filter(Day2::isRoundValid).collect(Collectors.toList());
        var sumOfValidGames = games.stream().map(GameDTO::getGame).reduce(0, Integer::sum);

        System.out.println("Part1 " + sumOfValidGames);
    }

    private static boolean isRoundValid(GameDTO gameDTO) {
        return gameDTO.getRounds().stream()
                .noneMatch(round -> round.getRed() > MAX_RED
                        || round.getGreen() > MAX_GREEN
                        || round.getBlue() > MAX_BLUE);
    }

    private static GameDTO mapToGameDTO(String line) {
        String[] game = line.split(":");

        int gameId = Integer.parseInt(game[0].trim().split("\\s+")[1]);

        String[] rounds = game[1].split(";");
        List<RoundsDTO> roundsDTOList = Arrays.stream(rounds).map(Day2::mapToRoundsDTO).collect(Collectors.toList());
        return  GameDTO.builder().game(gameId).rounds(roundsDTOList).build();
    }

    private static RoundsDTO mapToRoundsDTO(String r) {
        String[] coloursWithNumbers = r.split(",");

        var round = RoundsDTO.builder();
        Arrays.stream(coloursWithNumbers).forEach(
                coloursWithNumber -> {
                    String[] split = coloursWithNumber.trim().split("\\s+");
                    var number = Integer.parseInt(split[0]);
                    var color = split[1];
                    switch (color) {
                        case "red": round.setRed(number); break;
                        case "green": round.setGreen(number); break;
                        case "blue": round.setBlue(number); break;
                    }
                }
        );

        return round.build();
    }

    private static void part2(File file) throws IOException {
        var lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
        var gamesScore = lines.stream().map(Day2::mapToGameDTO).map(Day2::getGameScore).reduce(0, Integer::sum);

        System.out.println("Part2 " + gamesScore);
    }

    private static int getGameScore(GameDTO game) {
        int redCubes = game.getRounds().stream().map(RoundsDTO::getRed).max(Integer::compareTo).orElse(0);
        int greenCubes = game.getRounds().stream().map(RoundsDTO::getGreen).max(Integer::compareTo).orElse(0);
        int blueCubes = game.getRounds().stream().map(RoundsDTO::getBlue).max(Integer::compareTo).orElse(0);

        return redCubes*greenCubes*blueCubes;
    }

    @Builder(setterPrefix = "set")
    @Getter
    private static class RoundsDTO {
        private int red;
        private int green;
        private int blue;
    }

    @Builder
    @Getter
    private static class GameDTO {
        private int game;
        @Singular
        private List<RoundsDTO> rounds;
    }
}
