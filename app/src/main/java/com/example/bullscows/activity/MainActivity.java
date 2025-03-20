package com.example.bullscows.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bullscows.R;
import com.example.bullscows.util.Constants;
import com.example.bullscows.util.GamePreferences;

/**
 * Ana aktivite - Oyun menüsünü içerir
 * Oyun modları arasında seçim yapmayı sağlar
 */
public class MainActivity extends AppCompatActivity {

    private ImageButton btnInfo;
    private ImageButton btnScores;
    private Button btnSolo;
    private Button btnBasic;
    private Button btnHard;
    private GamePreferences gamePreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Tercihleri başlat
        gamePreferences = new GamePreferences(this);

        // UI bileşenlerini ayarla
        initializeViews();
        setupClickListeners();
    }

    /**
     * UI bileşenlerini tanımlar
     */
    private void initializeViews() {
        btnInfo = findViewById(R.id.btn_info);
        btnScores = findViewById(R.id.btn_scores);
        btnSolo = findViewById(R.id.btn_solo);
        btnBasic = findViewById(R.id.btn_basic);
        btnHard = findViewById(R.id.btn_hard);
    }

    /**
     * Butonlara tıklama olaylarını ayarlar
     */
    private void setupClickListeners() {
        // Bilgi butonu
        btnInfo.setOnClickListener(v -> showGameInfo());

        // Skorlar butonu
        btnScores.setOnClickListener(v -> showHighScores());

        // Oyun modu butonları
        btnSolo.setOnClickListener(v -> startSoloGame());
        btnBasic.setOnClickListener(v -> startComputerGame(Constants.MODE_BASIC));
        btnHard.setOnClickListener(v -> startComputerGame(Constants.MODE_HARD));
    }

    /**
     * Oyun hakkında bilgi diyaloğunu gösterir
     */
    private void showGameInfo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.game_info_title)
                .setMessage(R.string.game_info_message)
                .setPositiveButton(R.string.ok, null)
                .show();
    }

    /**
     * Yüksek skorları gösteren diyaloğu açar
     */
    private void showHighScores() {
        StringBuilder scoresText = new StringBuilder();

        // Solo mod rekoru
        int soloRecord = gamePreferences.getHighScore(Constants.MODE_SOLO);
        scoresText.append(getString(R.string.solo_mode)).append(": ");
        if (soloRecord > 0) {
            scoresText.append(soloRecord).append(" ").append(getString(R.string.guesses));
        } else {
            scoresText.append(getString(R.string.not_played_yet));
        }
        scoresText.append("\n\n");

        // Basic mod rekoru
        int basicRecord = gamePreferences.getHighScore(Constants.MODE_BASIC);
        scoresText.append(getString(R.string.basic_mode)).append(": ");
        if (basicRecord > 0) {
            scoresText.append(getString(R.string.win_in)).append(" ").append(basicRecord).append(" ").append(getString(R.string.guesses));
        } else {
            scoresText.append(getString(R.string.not_played_yet));
        }
        scoresText.append("\n\n");

        // Hard mod rekoru
        int hardRecord = gamePreferences.getHighScore(Constants.MODE_HARD);
        scoresText.append(getString(R.string.hard_mode)).append(": ");
        if (hardRecord > 0) {
            scoresText.append(getString(R.string.win_in)).append(" ").append(hardRecord).append(" ").append(getString(R.string.guesses));
        } else {
            scoresText.append(getString(R.string.not_played_yet));
        }

        // Skor diyaloğunu göster
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.high_scores)
                .setMessage(scoresText.toString())
                .setPositiveButton(R.string.ok, null)
                .show();
    }

    /**
     * Solo oyun modunu başlatır
     */
    private void startSoloGame() {
        Intent intent = new Intent(this, SoloGameActivity.class);
        startActivity(intent);
    }

    /**
     * Bilgisayara karşı oyun modunu başlatır
     * @param mode Oyun modu (Basic veya Hard)
     */
    private void startComputerGame(String mode) {
        Intent intent = new Intent(this, ComputerGameActivity.class);
        intent.putExtra(Constants.EXTRA_GAME_MODE, mode);
        startActivity(intent);
    }
}