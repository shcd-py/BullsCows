package com.example.bullscows.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

/**
 * Skor veritabanı tanımı
 * Room ile SQLite veritabanı oluşturur
 */
@Database(entities = {Score.class}, version = 1, exportSchema = false)
public abstract class ScoreDatabase extends RoomDatabase {

    // Singleton instance
    private static ScoreDatabase instance;

    /**
     * DAO (Data Access Object) erişimi için abstract method
     * @return ScoreDao nesnesini döndürür
     */
    public abstract ScoreDao scoreDao();

    /**
     * Singleton pattern ile veritabanı instance'ı oluşturur veya mevcut olanı döndürür
     * @param context Uygulama context'i
     * @return ScoreDatabase instance'ı
     */
    public static synchronized ScoreDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            ScoreDatabase.class,
                            "bulls_cows_database")
                    .fallbackToDestructiveMigration() // Versiyon değiştiğinde veritabanını sıfırlar
                    .build();
        }
        return instance;
    }

    /**
     * Veritabanı instance'ını temizler
     * Test veya yeniden yapılandırma için kullanılabilir
     */
    public static void destroyInstance() {
        instance = null;
    }
}