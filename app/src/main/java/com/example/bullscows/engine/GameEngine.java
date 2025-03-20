package com.example.bullscows.engine;

import com.example.bullscows.ai.ComputerAI;
import com.example.bullscows.model.GuessResult;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Oyun mantığını yöneten ana sınıf
 * Tahminleri kontrol etme, sayı üretme gibi temel işlemleri barındırır
 */
public class GameEngine {
    private NumberGenerator numberGenerator;
    private GuessChecker guessChecker;
    private ComputerAI computerAI;

    /**
     * Varsayılan constructor
     */
    public GameEngine() {
        this.numberGenerator = new NumberGenerator();
        this.guessChecker = new GuessChecker();
    }

    /**
     * Özel bileşenlerle constructor
     * @param numberGenerator Sayı üretici
     * @param guessChecker Tahmin kontrolcüsü
     */
    public GameEngine(NumberGenerator numberGenerator, GuessChecker guessChecker) {
        this.numberGenerator = numberGenerator;
        this.guessChecker = guessChecker;
    }

    /**
     * 4 basamaklı rastgele sayı oluşturur
     * @return 4 basamaklı rastgele sayı (String olarak)
     */
    public String generateSecretNumber() {
        return numberGenerator.generate();
    }

    /**
     * Tahmin sonucunu kontrol eder
     * @param secret Gizli sayı
     * @param guess Kullanıcı tahmini
     * @return Bulls ve Cows sayılarını içeren sonuç
     */
    public GuessResult checkGuess(String secret, String guess) {
        return guessChecker.check(secret, guess);
    }

    /**
     * Bilgisayar AI'ını ayarlar
     * @param computerAI Bilgisayar AI'ı
     */
    public void setComputerAI(ComputerAI computerAI) {
        this.computerAI = computerAI;
    }

    /**
     * Bilgisayarın tahminini alır
     * @param previousResults Önceki tahmin sonuçları
     * @return Bilgisayarın tahmini
     */
    public String getComputerGuess(List<GuessResult> previousResults) {
        if (computerAI == null) {
            throw new IllegalStateException("Computer AI not set");
        }
        return computerAI.makeGuess(previousResults);
    }

    /**
     * Tahminin geçerli olup olmadığını kontrol eder
     * (4 basamaklı, sadece rakam ve tüm rakamlar farklı)
     * @param guess Kontrol edilecek tahmin
     * @return Tahmin geçerliyse true, değilse false
     */
    public boolean isValidGuess(String guess) {
        if (guess == null || guess.length() != 4) {
            return false;
        }

        for (int i = 0; i < guess.length(); i++) {
            if (!Character.isDigit(guess.charAt(i))) {
                return false;
            }
        }

        Set<Character> digits = new HashSet<>();
        for (char c : guess.toCharArray()) {
            digits.add(c);
        }

        return digits.size() == 4;
    }

    /**
     * Tahmin sonucunu +X -Y formatına dönüştürür
     * @param result Tahmin sonucu
     * @return +X -Y formatında string
     */
    public String formatResult(GuessResult result) {
        return "+" + result.getBulls() + " -" + result.getCows();
    }

    /**
     * +X -Y formatındaki sonucu GuessResult nesnesine dönüştürür
     * @param formattedResult Formatlanmış tahmin sonucu
     * @return GuessResult nesnesi
     */
    public GuessResult parseResult(String formattedResult) {
        try {
            formattedResult = formattedResult.trim();
            int bulls = Integer.parseInt(formattedResult.substring(1, formattedResult.indexOf(" ")));
            int cows = Integer.parseInt(formattedResult.substring(formattedResult.lastIndexOf("-") + 1));
            return new GuessResult(bulls, cows);
        } catch (Exception e) {
            // Geçersiz format
            return new GuessResult(0, 0);
        }
    }
}