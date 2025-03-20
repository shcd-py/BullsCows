package com.example.bullscows.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Skorları temsil eden entity sınıfı
 * Room veritabanı için tablo tanımı
 */
@Entity(tableName = "scores")
public class Score {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String mode; // Oyun modu (solo, basic, hard)
    private int attempts; // Tahmin sayısı
    private long date; // Tarih (unix timestamp)

    /**
     * Constructor
     * @param mode Oyun modu
     * @param attempts Tahmin sayısı
     * @param date Tarih
     */
    public Score(String mode, int attempts, long date) {
        this.mode = mode;
        this.attempts = attempts;
        this.date = date;
    }

    // Getter ve Setter metodları

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public int getAttempts() {
        return attempts;
    }

    public void setAttempts(int attempts) {
        this.attempts = attempts;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    /**
     * Oyun modunu daha okunabilir bir formatta döndürür
     * @return Formatlanmış mod adı
     */
    public String getFormattedMode() {
        switch (mode) {
            case "solo":
                return "Tek Kişilik";
            case "basic":
                return "Bilgisayara Karşı (Basic)";
            case "hard":
                return "Bilgisayara Karşı (Hard)";
            default:
                return mode;
        }
    }
}