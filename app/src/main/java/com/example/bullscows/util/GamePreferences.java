package com.example.bullscows.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Oyun ayarlarını ve rekorları SharedPreferences üzerinden yöneten sınıf
 */
public class GamePreferences {

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    /**
     * Constructor
     * @param context Uygulama context'i
     */
    public GamePreferences(Context context) {
        preferences = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    /**
     * Yüksek skoru kaydeder (daha düşükse)
     * @param gameMode Oyun modu (solo, basic, hard)
     * @param score Yeni skor
     * @return Skor kaydedildiyse true, edilmediyse false
     */
    public boolean saveHighScore(String gameMode, int score) {
        // Mevcut rekoru al
        int currentHighScore = getHighScore(gameMode);

        // Yeni skor daha iyiyse veya hiç rekor yoksa kaydet
        if (currentHighScore == 0 || score < currentHighScore) {
            String key = getScoreKey(gameMode);
            editor.putInt(key, score);
            editor.apply();
            return true;
        }

        return false;
    }

    /**
     * Yüksek skoru alır
     * @param gameMode Oyun modu (solo, basic, hard)
     * @return Yüksek skor (yoksa 0)
     */
    public int getHighScore(String gameMode) {
        String key = getScoreKey(gameMode);
        return preferences.getInt(key, Constants.DEFAULT_SCORE);
    }

    /**
     * Tüm rekorları temizler
     */
    public void clearAllHighScores() {
        editor.remove(Constants.PREF_HIGH_SCORE_SOLO);
        editor.remove(Constants.PREF_HIGH_SCORE_BASIC);
        editor.remove(Constants.PREF_HIGH_SCORE_HARD);
        editor.apply();
    }

    /**
     * Belirli bir modun rekorunu temizler
     * @param gameMode Temizlenecek oyun modu
     */
    public void clearHighScore(String gameMode) {
        String key = getScoreKey(gameMode);
        editor.remove(key);
        editor.apply();
    }

    /**
     * Oyun modu için SharedPreferences anahtarını döndürür
     * @param gameMode Oyun modu
     * @return SharedPreferences anahtarı
     */
    private String getScoreKey(String gameMode) {
        switch (gameMode) {
            case Constants.MODE_SOLO:
                return Constants.PREF_HIGH_SCORE_SOLO;
            case Constants.MODE_BASIC:
                return Constants.PREF_HIGH_SCORE_BASIC;
            case Constants.MODE_HARD:
                return Constants.PREF_HIGH_SCORE_HARD;
            default:
                return Constants.PREF_HIGH_SCORE_SOLO; // Varsayılan olarak solo mod
        }
    }

    /**
     * Herhangi bir değeri kaydeder
     * @param key Anahtar
     * @param value Değer
     */
    public void saveString(String key, String value) {
        editor.putString(key, value);
        editor.apply();
    }

    /**
     * Herhangi bir string değeri alır
     * @param key Anahtar
     * @param defaultValue Varsayılan değer
     * @return Değer
     */
    public String getString(String key, String defaultValue) {
        return preferences.getString(key, defaultValue);
    }

    /**
     * Herhangi bir integer değeri kaydeder
     * @param key Anahtar
     * @param value Değer
     */
    public void saveInt(String key, int value) {
        editor.putInt(key, value);
        editor.apply();
    }

    /**
     * Herhangi bir integer değeri alır
     * @param key Anahtar
     * @param defaultValue Varsayılan değer
     * @return Değer
     */
    public int getInt(String key, int defaultValue) {
        return preferences.getInt(key, defaultValue);
    }

    /**
     * Herhangi bir boolean değeri kaydeder
     * @param key Anahtar
     * @param value Değer
     */
    public void saveBoolean(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.apply();
    }

    /**
     * Herhangi bir boolean değeri alır
     * @param key Anahtar
     * @param defaultValue Varsayılan değer
     * @return Değer
     */
    public boolean getBoolean(String key, boolean defaultValue) {
        return preferences.getBoolean(key, defaultValue);
    }
}