package org.example;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


public class Day4 {

    public static void main(String[] args) throws URISyntaxException, IOException {
        String fileName = "day-4-input.txt";

        ClassLoader classLoader = Day4.class.getClassLoader();
        URL resource = classLoader.getResource(fileName);
        var file = new File(resource.toURI());

        part1(file);
        part2(file);

    }

    private static void part2(File file) throws IOException {
        var lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
        var cardDTOs = lines.stream().map(Day4::mapToCardDTO).collect(Collectors.toList());
        cardDTOs.forEach(dto -> addCopies(dto, cardDTOs));

        var result = cardDTOs.stream().map(CardDTO::getCopies).reduce(0L, Long::sum);

        System.out.println("Part2 " + result);
    }

    private static void part1(File file) throws IOException {
        var lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
        var result = lines.stream().map(Day4::mapToCardDTO).map(Day4::getCardScore).reduce(0.0, Double::sum);

        System.out.println("Part1 " + result);
    }

    private static CardDTO mapToCardDTO(String line) {
        String[] card = line.split(":");
        int cardNumber = Integer.parseInt(card[0].trim().split("\\s+")[1]);
        String[] numbers = card[1].split("\\|");
        var winningNumbers = Arrays.stream(numbers[0].trim().split("\\s+")).map(Integer::valueOf).collect(Collectors.toSet());
        var numbersYouHave = Arrays.stream(numbers[1].trim().split("\\s+")).map(Integer::valueOf).collect(Collectors.toList());

        return  CardDTO.builder().cardNumber(cardNumber).winningNumbers(winningNumbers).numbersYouHave(numbersYouHave).copies(1L).build();
    }

    private static double getCardScore(CardDTO cardDTO) {
        var winningNumbers = calculateCopiesScore(cardDTO);

        return winningNumbers > 0 ? Math.pow(2, winningNumbers - 1) : 0;
    }

    private static long calculateCopiesScore(CardDTO cardDTO) {
        return cardDTO.getNumbersYouHave().stream().filter(n -> cardDTO.getWinningNumbers().contains(n)).count();
    }

    private static void addCopies(CardDTO cardDTO, List<CardDTO> cardDTOS) {
        long copiesScore = calculateCopiesScore(cardDTO);
        if(copiesScore > 0) {
            for (int i = cardDTO.getCardNumber(); i <= cardDTO.getCardNumber() + copiesScore - 1 && i < cardDTOS.size(); i++) {
                cardDTOS.get(i).setCopies(cardDTOS.get(i).getCopies() + cardDTO.getCopies());
            }
        }
    }

    @Builder
    @Getter
    private static class CardDTO{
        private int cardNumber;
        private Set<Integer> winningNumbers;
        private List<Integer> numbersYouHave;
        @Setter
        private long copies = 1;
    }
}
