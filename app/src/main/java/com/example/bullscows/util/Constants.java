package com.example.bullscows.util;

/**
 * Uygulama genelinde kullanılan sabit değerler
 */
public class Constants {

    // Oyun modları
    public static final String MODE_SOLO = "solo";
    public static final String MODE_BASIC = "basic";
    public static final String MODE_HARD = "hard";

    // Intent extra anahtarları
    public static final String EXTRA_GAME_MODE = "game_mode";

    // SharedPreferences anahtarları
    public static final String PREFS_NAME = "BullsCowsPrefs";
    public static final String PREF_HIGH_SCORE_SOLO = "high_score_solo";
    public static final String PREF_HIGH_SCORE_BASIC = "high_score_basic";
    public static final String PREF_HIGH_SCORE_HARD = "high_score_hard";

    // Animasyon süreleri (ms)
    public static final int ANIMATION_SHORT = 300;
    public static final int ANIMATION_MEDIUM = 500;
    public static final int ANIMATION_LONG = 800;

    // Varsayılan değerler
    public static final int DEFAULT_SCORE = 0;

    // UI sabitleri
    public static final int MAX_GUESS_HISTORY = 10; // Görüntülenecek maksimum tahmin sayısı

    // Bilgisayar AI sabitleri
    public static final int COMPUTER_GUESS_DELAY = 1000; // Bilgisayar tahmini için bekleme süresi (ms)

    // Özel karakterler
    public static final String BULL_SYMBOL = "🎯"; // Bulls sembolü
    public static final String COW_SYMBOL = "🐄"; // Cows sembolü

    // Hata mesajları
    public static final String ERROR_INVALID_GUESS = "Geçersiz tahmin! 4 farklı rakam içermelidir.";
    public static final String ERROR_INVALID_FEEDBACK = "Geçersiz geri bildirim! +X -Y formatında olmalıdır.";
    public static final String ERROR_INCONSISTENT_FEEDBACK = "Tutarsız geri bildirim! Önceki geribildirimlerle uyuşmuyor.";

    // Diğer mesajlar
    public static final String MESSAGE_PLAYER_WIN = "Tebrikler! %d denemede kazandınız.";
    public static final String MESSAGE_COMPUTER_WIN = "Bilgisayar %d denemede kazandı.";

    // Gizli ayarlar (debug mode)
    public static final boolean DEBUG_MODE = false; // Debug modu açık/kapalı
    public static final boolean SHOW_SECRET_NUMBER = false; // Gizli sayıyı göster (sadece debug modunda)

    private Constants() {
        // Private constructor to prevent instantiation
    }
}