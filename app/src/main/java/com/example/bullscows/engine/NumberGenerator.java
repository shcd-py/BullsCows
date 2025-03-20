package com.example.bullscows.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Rastgele sayı üreteci
 * Oyun için gerekli 4 basamaklı (her basamağı farklı) sayıları üretir
 */
public class NumberGenerator {
    private Random random;

    /**
     * Varsayılan constructor
     */
    public NumberGenerator() {
        this.random = new Random();
    }

    /**
     * Seed kullanarak constructor (test için)
     * @param seed Random üreteci için seed
     */
    public NumberGenerator(long seed) {
        this.random = new Random(seed);
    }

    /**
     * Her basamağı farklı, 4 basamaklı rastgele bir sayı üretir
     * @return 4 basamaklı sayı (String olarak)
     */
    public String generate() {
        List<Integer> digits = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            digits.add(i);
        }
        Collections.shuffle(digits, random);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            sb.append(digits.get(i));
        }

        return sb.toString();
    }

    /**
     * İlk basamağı sıfır olmayan, 4 basamaklı rastgele bir sayı üretir
     * @return 4 basamaklı sayı (String olarak)
     */
    public String generateNonZeroStart() {
        List<Integer> firstDigit = new ArrayList<>();
        for (int i = 1; i < 10; i++) {  // 1'den 9'a
            firstDigit.add(i);
        }
        Collections.shuffle(firstDigit, random);

        List<Integer> remainingDigits = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            if (i != firstDigit.get(0)) {  // İlk basamakla aynı olmamalı
                remainingDigits.add(i);
            }
        }
        Collections.shuffle(remainingDigits, random);

        StringBuilder sb = new StringBuilder();
        sb.append(firstDigit.get(0));  // İlk basamak

        // Diğer 3 basamak
        for (int i = 0; i < 3; i++) {
            sb.append(remainingDigits.get(i));
        }

        return sb.toString();
    }

    /**
     * Random üretecini döndürür (test için)
     * @return Random nesnesi
     */
    public Random getRandom() {
        return random;
    }

    /**
     * Random üretecini ayarlar (test için)
     * @param random Yeni random nesnesi
     */
    public void setRandom(Random random) {
        this.random = random;
    }
}