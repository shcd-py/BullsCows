package com.example.bullscows.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bullscows.R;
import com.example.bullscows.adapter.GuessAdapter;
import com.example.bullscows.engine.GameEngine;
import com.example.bullscows.engine.GuessChecker;
import com.example.bullscows.engine.NumberGenerator;
import com.example.bullscows.model.GuessResult;
import com.example.bullscows.util.Constants;
import com.example.bullscows.util.GamePreferences;

import java.util.ArrayList;
import java.util.List;

/**
 * Tek kişilik oyun modu aktivitesi
 * Kullanıcının bilgisayarın tuttuğu sayıyı tahmin etmesini sağlar
 */
public class SoloGameActivity extends AppCompatActivity implements View.OnClickListener {

    // UI bileşenleri
    private TextView attemptsText;
    private TextView recordText;
    private EditText guessInput;
    private ImageButton btnDelete;
    private ImageButton btnSubmit;
    private RecyclerView guessesList;
    private GuessAdapter guessAdapter;

    // Numpad butonları
    private Button[] numberButtons = new Button[10];

    // Üst menü butonları
    private ImageButton btnMenu;
    private ImageButton btnNewGame;
    private ImageButton btnInfo;

    // Oyun mantığı
    private GameEngine gameEngine;
    private String secretNumber;
    private List<GuessItem> guesses;
    private int attempts;
    private int recordAttempts;
    private GamePreferences gamePreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solo_game);

        // Game engine başlat
        gameEngine = new GameEngine(new NumberGenerator(), new GuessChecker());
        gamePreferences = new GamePreferences(this);

        // UI bileşenlerini başlat
        initializeViews();
        setupClickListeners();

        // RecyclerView ayarla
        setupRecyclerView();

        // Oyunu başlat
        startNewGame();
    }

    /**
     * UI bileşenlerini tanımlar
     */
    private void initializeViews() {
        attemptsText = findViewById(R.id.text_attempts);
        recordText = findViewById(R.id.text_record);
        guessInput = findViewById(R.id.guess_input);
        guessInput.setFocusable(false); // Manuel giriş yerine numpad kullanılacak
        guessesList = findViewById(R.id.guesses_list);
        btnDelete = findViewById(R.id.btn_delete);
        btnSubmit = findViewById(R.id.btn_submit);

        // Üst menü butonları
        btnMenu = findViewById(R.id.btn_menu);
        btnNewGame = findViewById(R.id.btn_new_game);
        btnInfo = findViewById(R.id.btn_info);

        // Numpad butonlarını ayarla
        for (int i = 0; i < 10; i++) {
            int id = getResources().getIdentifier("btn_" + i, "id", getPackageName());
            numberButtons[i] = findViewById(id);
        }
    }

    /**
     * Tıklama olaylarını ayarlar
     */
    private void setupClickListeners() {
        // Numpad butonları
        for (Button button : numberButtons) {
            button.setOnClickListener(this);
        }

        // Diğer butonlar
        btnDelete.setOnClickListener(v -> deleteLastDigit());
        btnSubmit.setOnClickListener(v -> submitGuess());

        // Üst menü butonları
        btnMenu.setOnClickListener(v -> finish());
        btnNewGame.setOnClickListener(v -> showNewGameConfirmDialog());
        btnInfo.setOnClickListener(v -> showGameInfo());
    }

    /**
     * RecyclerView ve adaptörü ayarlar
     */
    private void setupRecyclerView() {
        guesses = new ArrayList<>();
        guessAdapter = new GuessAdapter(guesses);
        guessesList.setLayoutManager(new LinearLayoutManager(this));
        guessesList.setAdapter(guessAdapter);
    }

    /**
     * Yeni oyun başlatır
     */
    private void startNewGame() {
        // Yeni gizli sayı oluştur
        secretNumber = gameEngine.generateSecretNumber();

        // Eski tahminleri temizle
        guesses.clear();
        guessAdapter.notifyDataSetChanged();

        // Değişkenleri sıfırla
        attempts = 0;
        updateAttemptsText();

        // Kaydedilen rekoru al
        recordAttempts = gamePreferences.getHighScore(Constants.MODE_SOLO);
        updateRecordText();

        // Giriş alanını temizle
        guessInput.setText("");
    }

    /**
     * Yeni oyun başlatma onay diyaloğunu gösterir
     */
    private void showNewGameConfirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.new_game)
                .setMessage(R.string.new_game_confirm)
                .setPositiveButton(R.string.yes, (dialog, which) -> startNewGame())
                .setNegativeButton(R.string.no, null)
                .show();
    }

    /**
     * Oyun hakkında bilgi diyaloğunu gösterir
     */
    private void showGameInfo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.game_info_title)
                .setMessage(R.string.solo_game_info)
                .setPositiveButton(R.string.ok, null)
                .show();
    }

    /**
     * Deneme sayısı metnini günceller
     */
    private void updateAttemptsText() {
        attemptsText.setText(getString(R.string.attempts, attempts));
    }

    /**
     * Rekor metnini günceller
     */
    private void updateRecordText() {
        if (recordAttempts > 0) {
            recordText.setText(getString(R.string.record, recordAttempts));
        } else {
            recordText.setText(R.string.no_record);
        }
    }

    /**
     * Numpad butonlarına tıklama olayı
     */
    @Override
    public void onClick(View v) {
        // Mevcut giriş metnini al
        String currentInput = guessInput.getText().toString();

        // 4 basamaktan fazla olmamalı
        if (currentInput.length() >= 4) {
            return;
        }

        // Hangi rakam butonu tıklandı?
        for (int i = 0; i < 10; i++) {
            if (v.getId() == numberButtons[i].getId()) {
                String digit = String.valueOf(i);

                // Her rakam sadece bir kez kullanılabilir
                if (currentInput.contains(digit)) {
                    Toast.makeText(this, R.string.digit_already_used, Toast.LENGTH_SHORT).show();
                    return;
                }

                // Rakamı ekle
                guessInput.setText(currentInput + digit);
                break;
            }
        }
    }

    /**
     * Son rakamı siler
     */
    private void deleteLastDigit() {
        String currentInput = guessInput.getText().toString();
        if (!TextUtils.isEmpty(currentInput)) {
            guessInput.setText(currentInput.substring(0, currentInput.length() - 1));
        }
    }

    /**
     * Tahmin gönderir
     */
    private void submitGuess() {
        String guess = guessInput.getText().toString();

        // Geçerli bir tahmin mi?
        if (!gameEngine.isValidGuess(guess)) {
            Toast.makeText(this, R.string.invalid_guess, Toast.LENGTH_SHORT).show();
            return;
        }

        // Tahmini kontrol et
        GuessResult result = gameEngine.checkGuess(secretNumber, guess);
        attempts++;

        // Tahminler listesine ekle
        GuessItem guessItem = new GuessItem(attempts, guess, result);
        guesses.add(0, guessItem); // En üste ekle
        guessAdapter.notifyItemInserted(0);
        guessesList.scrollToPosition(0);

        // UI güncelle
        updateAttemptsText();
        guessInput.setText("");

        // Doğru tahmin mi?
        if (result.getBulls() == 4) {
            handleCorrectGuess();
        }
    }

    /**
     * Doğru tahmin sonrası işlemler
     */
    private void handleCorrectGuess() {
        // Rekoru güncelle
        if (recordAttempts == 0 || attempts < recordAttempts) {
            recordAttempts = attempts;
            gamePreferences.saveHighScore(Constants.MODE_SOLO, attempts);
            updateRecordText();
        }

        // Tebrik diyaloğu göster
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.congratulations)
                .setMessage(getString(R.string.correct_guess_message, attempts, secretNumber))
                .setPositiveButton(R.string.new_game, (dialog, which) -> startNewGame())
                .setNegativeButton(R.string.back_to_menu, (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }

    /**
     * Tahmin veri sınıfı
     */
    public static class GuessItem {
        private int attemptNumber;
        private String guess;
        private GuessResult result;

        public GuessItem(int attemptNumber, String guess, GuessResult result) {
            this.attemptNumber = attemptNumber;
            this.guess = guess;
            this.result = result;
        }

        public int getAttemptNumber() {
            return attemptNumber;
        }

        public String getGuess() {
            return guess;
        }

        public GuessResult getResult() {
            return result;
        }

        public String getFormattedGuess() {
            return "Tahmin #" + attemptNumber + ": " + guess;
        }
    }
}