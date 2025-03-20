package com.example.bullscows.model;

/**
 * Tahmin sonuçlarını tutan sınıf
 * Bulls (doğru rakam, doğru yer) ve Cows (doğru rakam, yanlış yer) bilgisini tutar
 */
public class GuessResult {

    private int bulls; // Doğru rakam, doğru yer
    private int cows;  // Doğru rakam, yanlış yer

    /**
     * Constructor
     * @param bulls Doğru rakam, doğru yer sayısı
     * @param cows Doğru rakam, yanlış yer sayısı
     */
    public GuessResult(int bulls, int cows) {
        this.bulls = bulls;
        this.cows = cows;
    }

    /**
     * Bulls sayısını döndürür
     * @return Doğru rakam, doğru yer sayısı
     */
    public int getBulls() {
        return bulls;
    }

    /**
     * Bulls sayısını ayarlar
     * @param bulls Doğru rakam, doğru yer sayısı
     */
    public void setBulls(int bulls) {
        this.bulls = bulls;
    }

    /**
     * Cows sayısını döndürür
     * @return Doğru rakam, yanlış yer sayısı
     */
    public int getCows() {
        return cows;
    }

    /**
     * Cows sayısını ayarlar
     * @param cows Doğru rakam, yanlış yer sayısı
     */
    public void setCows(int cows) {
        this.cows = cows;
    }

    /**
     * Tahmin doğru mu (4 bulls) kontrolü
     * @return Tahmin doğruysa true, değilse false
     */
    public boolean isCorrect() {
        return bulls == 4;
    }

    /**
     * Sonucu "+X -Y" formatında döndürür
     * @return Bulls ve Cows için "+X -Y" formatında string
     */
    public String toFormattedString() {
        return "+" + bulls + " -" + cows;
    }

    /**
     * Sonucu "Bulls: X Cows: Y" formatında döndürür
     * @return Bulls ve Cows için açık string format
     */
    public String toBullsCowsString() {
        return "Bulls: " + bulls + " Cows: " + cows;
    }

    @Override
    public String toString() {
        return toBullsCowsString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        GuessResult that = (GuessResult) obj;
        return bulls == that.bulls && cows == that.cows;
    }

    @Override
    public int hashCode() {
        int result = bulls;
        result = 31 * result + cows;
        return result;
    }
}