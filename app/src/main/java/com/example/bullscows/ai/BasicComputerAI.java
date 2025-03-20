package com.example.bullscows.ai;

import com.example.bullscows.engine.GuessChecker;
import com.example.bullscows.model.GuessResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Basit bilgisayar AI
 * Olası sayıları filtreleyerek tahmin yapan basit bir algoritma kullanır
 */
public class BasicComputerAI implements ComputerAI {

    private List<String> possibleNumbers;
    private Random random;
    private String lastGuess;
    private GuessChecker guessChecker;

    /**
     * Constructor
     */
    public BasicComputerAI() {
        random = new Random();
        guessChecker = new GuessChecker();
        reset();
    }

    @Override
    public String makeGuess(List<GuessResult> previousResults) {
        // İlk tahmin
        if (previousResults == null || previousResults.isEmpty()) {
            lastGuess = "1234"; // Sabit başlangıç tahmini
            return lastGuess;
        }

        // Son tahmin sonucunu al
        GuessResult lastResult = previousResults.get(previousResults.size() - 1);

        // Sonuca göre olası sayıları filtrele
        filterPossibleNumbers(lastResult);

        // Eğer olası sayı kalmadıysa (muhtemelen tutarsız feedback verilmiş)
        if (possibleNumbers.isEmpty()) {
            reset(); // Tüm olasılıkları sıfırla
            lastGuess = generateRandomGuess(); // Rastgele bir tahmin yap
            return lastGuess;
        }

        // Olası sayılardan birini rastgele seç
        lastGuess = possibleNumbers.get(random.nextInt(possibleNumbers.size()));
        return lastGuess;
    }

    @Override
    public void reset() {
        // Olası tüm 4 basamaklı, her basamağı farklı sayıları oluştur
        possibleNumbers = generateAllPossibleNumbers();
        lastGuess = null;
    }

    /**
     * Son tahmin ve sonucuna göre olası sayıları filtreler
     * @param result Son tahmin sonucu
     */
    private void filterPossibleNumbers(GuessResult result) {
        List<String> filteredNumbers = new ArrayList<>();

        for (String candidate : possibleNumbers) {
            // Bu aday sayı, son tahminle aynı bulls ve cows sonucunu veriyor mu?
            GuessResult candidateResult = guessChecker.check(candidate, lastGuess);

            if (candidateResult.getBulls() == result.getBulls() &&
                    candidateResult.getCows() == result.getCows()) {
                filteredNumbers.add(candidate);
            }
        }

        possibleNumbers = filteredNumbers;
    }

    /**
     * Tüm olası 4 basamaklı, her basamağı farklı sayıları üretir
     * @return Olası tüm sayıların listesi
     */
    private List<String> generateAllPossibleNumbers() {
        List<String> numbers = new ArrayList<>();

        // Tüm 4 basamaklı, her basamağı farklı sayıları üret
        for (int d1 = 0; d1 <= 9; d1++) {
            for (int d2 = 0; d2 <= 9; d2++) {
                if (d2 == d1) continue; // Rakamlar farklı olmalı

                for (int d3 = 0; d3 <= 9; d3++) {
                    if (d3 == d1 || d3 == d2) continue;

                    for (int d4 = 0; d4 <= 9; d4++) {
                        if (d4 == d1 || d4 == d2 || d4 == d3) continue;

                        String number = "" + d1 + d2 + d3 + d4;
                        numbers.add(number);
                    }
                }
            }
        }

        return numbers;
    }

    /**
     * Rastgele geçerli bir tahmin üretir
     * @return 4 basamaklı rastgele bir sayı
     */
    private String generateRandomGuess() {
        List<Integer> digits = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            digits.add(i);
        }

        // Rastgele karıştır
        for (int i = 0; i < digits.size(); i++) {
            int j = random.nextInt(digits.size());
            int temp = digits.get(i);
            digits.set(i, digits.get(j));
            digits.set(j, temp);
        }

        // İlk 4 rakamı al
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            sb.append(digits.get(i));
        }

        return sb.toString();
    }
}