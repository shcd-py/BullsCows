package com.example.bullscows.ai;

import com.example.bullscows.engine.GuessChecker;
import com.example.bullscows.model.GuessResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Gelişmiş bilgisayar AI
 * Knuth algoritmasına dayalı daha akıllı bir strateji kullanır
 * (Minimax prensibini uygular)
 */
public class AdvancedComputerAI implements ComputerAI {

    private List<String> possibleNumbers;
    private List<String> allNumbers;
    private String lastGuess;
    private GuessChecker guessChecker;

    /**
     * Constructor
     */
    public AdvancedComputerAI() {
        guessChecker = new GuessChecker();
        reset();
    }

    @Override
    public String makeGuess(List<GuessResult> previousResults) {
        // İlk tahmin - Knuth algoritmasına göre en iyi ilk tahmin 1234'tür
        if (previousResults == null || previousResults.isEmpty()) {
            lastGuess = "1234";
            return lastGuess;
        }

        // Son tahmin sonucunu al
        GuessResult lastResult = previousResults.get(previousResults.size() - 1);

        // Sonuca göre olası sayıları filtrele
        filterPossibleNumbers(lastResult);

        // Eğer olası sayı kalmadıysa (muhtemelen tutarsız feedback verilmiş)
        if (possibleNumbers.isEmpty()) {
            reset(); // Tüm olasılıkları sıfırla
            lastGuess = "1234"; // Yeniden başla
            return lastGuess;
        }

        // Sadece bir olasılık kaldıysa, o doğru cevaptır
        if (possibleNumbers.size() == 1) {
            lastGuess = possibleNumbers.get(0);
            return lastGuess;
        }

        // Minimax stratejisi: Her tahminin en kötü durumda eleyeceği minimum sayıyı maksimize et
        lastGuess = findOptimalGuess();
        return lastGuess;
    }

    @Override
    public void reset() {
        // Olası tüm 4 basamaklı, her basamağı farklı sayıları oluştur
        allNumbers = generateAllPossibleNumbers();
        possibleNumbers = new ArrayList<>(allNumbers);
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
     * Minimax stratejisine göre en iyi tahmini bulur
     * @return En iyi tahmin
     */
    private String findOptimalGuess() {
        String bestGuess = null;
        int minMaxEliminated = 0;

        // Tüm olası tahminleri değerlendir
        for (String guess : allNumbers) {
            int maxEliminated = computeWorstCaseElimination(guess);

            // Bu tahmin daha iyi mi?
            if (bestGuess == null || maxEliminated > minMaxEliminated) {
                bestGuess = guess;
                minMaxEliminated = maxEliminated;
            }
        }

        // En iyi tahmini bulamazsak (aşırı büyük veri seti nedeniyle), olası sayılardan birini seç
        if (bestGuess == null || !possibleNumbers.contains(bestGuess)) {
            return possibleNumbers.get(0);
        }

        return bestGuess;
    }

    /**
     * Verilen tahmin için en kötü durum senaryosunda elenecek sayı adedini hesaplar
     * (Minimax stratejisi)
     * @param guess Değerlendirilecek tahmin
     * @return En kötü durumda elenecek minimum sayı
     */
    private int computeWorstCaseElimination(String guess) {
        // Her olası bulls/cows sonucu için adet tut
        Map<String, Integer> resultCounts = new HashMap<>();

        // Tüm olası sayılar için bu tahminin sonucunu hesapla
        for (String number : possibleNumbers) {
            GuessResult result = guessChecker.check(number, guess);
            String key = result.getBulls() + "B" + result.getCows() + "C";

            // Bu sonuca sahip sayı adedini güncelle
            resultCounts.put(key, resultCounts.getOrDefault(key, 0) + 1);
        }

        // En kötü durum: En çok sayıda olasılık kalması (en az eleme)
        int worstCase = 0;
        for (int count : resultCounts.values()) {
            if (count > worstCase) {
                worstCase = count;
            }
        }

        // Worst case'de elenecek sayı
        return possibleNumbers.size() - worstCase;
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
}