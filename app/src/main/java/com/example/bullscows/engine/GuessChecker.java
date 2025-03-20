package com.example.bullscows.engine;

import com.example.bullscows.model.GuessResult;

/**
 * Tahmin sonuçlarını hesaplayan sınıf
 * Gizli sayı ve tahmine göre Bulls ve Cows sayılarını belirler
 */
public class GuessChecker {
    /**
     * Verilen gizli sayı ve tahmine göre bulls ve cows sayılarını hesaplar
     *
     * Bulls: Doğru rakam ve doğru pozisyon
     * Cows: Doğru rakam ama yanlış pozisyon
     *
     * @param secret Gizli sayı
     * @param guess Tahmin
     * @return Bulls ve cows sayılarını içeren GuessResult nesnesi
     */
    public GuessResult check(String secret, String guess) {
        // Geçerlilik kontrolü
        if (secret == null || guess == null || secret.length() != 4 || guess.length() != 4) {
            return new GuessResult(0, 0);
        }

        int bulls = 0;
        int cows = 0;

        // Kullanılan rakamları işaretlemek için boolean dizileri
        boolean[] secretUsed = new boolean[4];
        boolean[] guessUsed = new boolean[4];

        // Önce bulls'ları bul
        for (int i = 0; i < 4; i++) {
            if (secret.charAt(i) == guess.charAt(i)) {
                bulls++;
                secretUsed[i] = true;
                guessUsed[i] = true;
            }
        }

        // Sonra cows'ları bul
        for (int i = 0; i < 4; i++) {
            if (!guessUsed[i]) {  // Bu pozisyon bull değilse
                for (int j = 0; j < 4; j++) {
                    // Rakam secret'ta var ve bu pozisyon kullanılmamış ve bull değilse
                    if (!secretUsed[j] && guess.charAt(i) == secret.charAt(j)) {
                        cows++;
                        secretUsed[j] = true;
                        break;  // Bu tahmin için bull veya cow bulundu, sonraki tahmine geç
                    }
                }
            }
        }

        return new GuessResult(bulls, cows);
    }

    /**
     * Alternatif, daha basit hesaplama yöntemi
     * (Özel durumlarda hatalı sonuç verebilir, eğitim amaçlıdır)
     */
    public GuessResult checkSimple(String secret, String guess) {
        int bulls = 0;
        int cows = 0;

        // Bulls hesapla (aynı pozisyonda aynı rakamlar)
        for (int i = 0; i < 4; i++) {
            if (secret.charAt(i) == guess.charAt(i)) {
                bulls++;
            }
        }

        // Her rakam için eşleşme sayısı
        for (char digit : guess.toCharArray()) {
            if (secret.indexOf(digit) >= 0) {
                cows++; // Rakam secret'ta varsa cows'u arttır
            }
        }

        // Bulls'ları cows'lardan çıkar (bulls zaten cows'a dahil edilmiş)
        cows -= bulls;

        return new GuessResult(bulls, cows);
    }
}