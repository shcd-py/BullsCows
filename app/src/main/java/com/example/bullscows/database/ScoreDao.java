package com.example.bullscows.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/**
 * Skor veritabanı erişim arayüzü
 * Room veritabanı için DAO (Data Access Object)
 */
@Dao
public interface ScoreDao {

    /**
     * Yeni bir skor ekler
     * @param score Eklenecek skor
     * @return Eklenen skorun ID'si
     */
    @Insert
    long insert(Score score);

    /**
     * Bir skoru günceller
     * @param score Güncellenecek skor
     */
    @Update
    void update(Score score);

    /**
     * Bir skoru siler
     * @param score Silinecek skor
     */
    @Delete
    void delete(Score score);

    /**
     * Belirli bir moda ait en iyi 10 skoru getirir
     * @param mode Oyun modu
     * @return En iyi 10 skor listesi (LiveData olarak)
     */
    @Query("SELECT * FROM scores WHERE mode = :mode ORDER BY attempts ASC LIMIT 10")
    LiveData<List<Score>> getTopScoresByMode(String mode);

    /**
     * Tüm skorları listeler
     * @return Tüm skorların listesi
     */
    @Query("SELECT * FROM scores ORDER BY date DESC")
    LiveData<List<Score>> getAllScores();

    /**
     * Belirli bir modun en iyi skorunu getirir
     * @param mode Oyun modu
     * @return En iyi skor (en az tahmin sayısı)
     */
    @Query("SELECT * FROM scores WHERE mode = :mode ORDER BY attempts ASC LIMIT 1")
    Score getBestScoreByMode(String mode);

    /**
     * Belirli bir moda ait tüm skorları temizler
     * @param mode Oyun modu
     */
    @Query("DELETE FROM scores WHERE mode = :mode")
    void deleteAllScoresByMode(String mode);

    /**
     * Tüm skorları temizler
     */
    @Query("DELETE FROM scores")
    void deleteAllScores();

    /**
     * Belirli bir moda ait son skoru getirir
     * @param mode Oyun modu
     * @return Son skor (tarih olarak)
     */
    @Query("SELECT * FROM scores WHERE mode = :mode ORDER BY date DESC LIMIT 1")
    Score getLastScoreByMode(String mode);
}