package com.example.bullscows.ai;

import com.example.bullscows.model.GuessResult;

import java.util.List;

/**
 * Bilgisayar AI arayüzü
 * Farklı zorluk seviyelerindeki bilgisayar AI'ları bu arayüzü uygular
 */
public interface ComputerAI {

    /**
     * Bilgisayarın bir sonraki tahminini üretir
     * @param previousResults Önceki tahminlerin sonuçları
     * @return Bilgisayarın tahmini (4 basamaklı string)
     */
    String makeGuess(List<GuessResult> previousResults);

    /**
     * Bilgisayar AI'ını sıfırlar
     * Yeni oyun başlatıldığında çağrılır
     */
    void reset();
}