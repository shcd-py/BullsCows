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
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bullscows.R;
import com.example.bullscows.adapter.GuessAdapter;
import com.example.bullscows.ai.AdvancedComputerAI;
import com.example.bullscows.ai.BasicComputerAI;
import com.example.bullscows.ai.ComputerAI;
import com.example.bullscows.engine.GameEngine;
import com.example.bullscows.engine.GuessChecker;
import com.example.bullscows.engine.NumberGenerator;
import com.example.bullscows.model.GuessResult;
import com.example.bullscows.util.Constants;
import com.example.bullscows.util.GamePreferences;

import java.util.ArrayList;
import java.util.List;

/**
 * Bilgisayara karşı oyun modu aktivitesi
 * Kullanıcı ve bilgisayar karşılıklı olarak tahmin yapar
 */
public class ComputerGameActivity extends AppCompatActivity implements View.OnClickListener {

    // Oyun durumları
    private static final int STATE_PLAYER_GUESS = 1;
    private static final int STATE_COMPUTER_GUESS = 2;

    // UI bileşenleri - Ortak
    private TextView titleText;
    private TextView turnInfoText;
    private ImageButton btnMenu;
    private ImageButton btnNewGame;
    private ImageButton btnInfo;

    // UI bileşenleri - Kullanıcı Tahmin
    private CardView playerGuessCard;
    private RecyclerView guessHistoryList;
    private GuessAdapter guessAdapter;
    private EditText playerGuessInput;
    private ImageButton playerDeleteButton;
    private ImageButton playerSubmitButton;
    private Button[] playerNumberButtons = new Button[10];

    // UI bileşenleri - Bilgisayar Tahmin
    private CardView computerGuessCard;
    private TextView computerGuessText;
    private EditText feedbackInput;
    private ImageButton feedbackDeleteButton;
    private ImageButton feedbackSubmitButton;
    private Button[] digitsButtons = new Button[5]; // 1, 2, 3, 4 ve boşluk butonu
    private Button plusButton;
    private Button minusButton;

    // Oyun değişkenleri
    private String gameMode;
    private GameEngine gameEngine;
    private ComputerAI computerAI;
    private String playerSecret; // Kullanıcının tuttuğu sayı
    private String computerSecret; // Bilgisayarın tuttuğu sayı
    private List<GuessItem> playerGuesses; // Kullanıcının tahminleri
    private List<GuessResult> computerGuessResults; // Bilgisayarın tahmin sonuçları
    private String currentComputerGuess; // Bilgisayarın mevcut tahmini
    private int currentGameState; // Mevcut oyun durumu
    private int turn; // Tur sayısı
    private GamePreferences gamePreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_computer_game);

        // Oyun modunu al
        gameMode = getIntent().getStringExtra(Constants.EXTRA_GAME_MODE);
        if (gameMode == null) {
            gameMode = Constants.MODE_BASIC; // Varsayılan mod
        }

        // Oyun motorunu başlat
        gameEngine = new GameEngine(new NumberGenerator(), new GuessChecker());
        gamePreferences = new GamePreferences(this);

        // Bilgisayar AI'ını ayarla
        setupComputerAI();

        // UI bileşenlerini başlat
        initializeViews();
        setupClickListeners();

        // Oyun verilerini başlat
        initializeGameData();

        // Yeni oyunu başlat
        startNewGame();
    }

    /**
     * Oyun moduna göre bilgisayar AI'ını ayarlar
     */
    private void setupComputerAI() {
        if (Constants.MODE_HARD.equals(gameMode)) {
            computerAI = new AdvancedComputerAI();
        } else {
            computerAI = new BasicComputerAI();
        }

        // Oyun motoruna AI'ı ayarla
        gameEngine.setComputerAI(computerAI);
    }

    /**
     * UI bileşenlerini tanımlar
     */
    private void initializeViews() {
        // Ortak bileşenler
        titleText = findViewById(R.id.text_title);
        turnInfoText = findViewById(R.id.text_turn_info);
        btnMenu = findViewById(R.id.btn_menu);
        btnNewGame = findViewById(R.id.btn_new_game);
        btnInfo = findViewById(R.id.btn_info);

        // Başlığı ayarla
        titleText.setText(Constants.MODE_HARD.equals(gameMode) ? R.string.hard_mode : R.string.basic_mode);

        // Kullanıcı tahmin bileşenleri
        playerGuessCard = findViewById(R.id.card_player_guess);
        guessHistoryList = findViewById(R.id.guesses_list);
        playerGuessInput = findViewById(R.id.player_guess_input);
        playerGuessInput.setFocusable(false); // Manuel giriş yerine numpad kullanılacak
        playerDeleteButton = findViewById(R.id.player_btn_delete);
        playerSubmitButton = findViewById(R.id.player_btn_submit);

        // Kullanıcı numpad butonları
        for (int i = 0; i < 10; i++) {
            int id = getResources().getIdentifier("player_btn_" + i, "id", getPackageName());
            playerNumberButtons[i] = findViewById(id);
        }

        // Bilgisayar tahmin bileşenleri
        computerGuessCard = findViewById(R.id.card_computer_guess);
        computerGuessText = findViewById(R.id.text_computer_guess);
        feedbackInput = findViewById(R.id.feedback_input);
        feedbackInput.setFocusable(false); // Manuel giriş yerine butonlar kullanılacak
        feedbackDeleteButton = findViewById(R.id.feedback_btn_delete);
        feedbackSubmitButton = findViewById(R.id.feedback_btn_submit);

        // Feedback butonları
        for (int i = 1; i <= 4; i++) {
            int id = getResources().getIdentifier("btn_digit_" + i, "id", getPackageName());
            digitsButtons[i-1] = findViewById(id);
        }
        digitsButtons[4] = findViewById(R.id.btn_space);
        plusButton = findViewById(R.id.btn_plus);
        minusButton = findViewById(R.id.btn_minus);

        // RecyclerView ayarla
        setupRecyclerView();
    }

    /**
     * RecyclerView ve adaptörü ayarlar
     */
    private void setupRecyclerView() {
        playerGuesses = new ArrayList<>();
        guessAdapter = new GuessAdapter(playerGuesses);
        guessHistoryList.setLayoutManager(new LinearLayoutManager(this));
        guessHistoryList.setAdapter(guessAdapter);
    }

    /**
     * Tıklama olaylarını ayarlar
     */
    private void setupClickListeners() {
        // Üst menü butonları
        btnMenu.setOnClickListener(v -> finish());
        btnNewGame.setOnClickListener(v -> showNewGameConfirmDialog());
        btnInfo.setOnClickListener(v -> showGameInfo());

        // Kullanıcı tahmin bileşenleri
        playerDeleteButton.setOnClickListener(v -> deleteLastDigitFromPlayerGuess());
        playerSubmitButton.setOnClickListener(v -> submitPlayerGuess());

        // Kullanıcı numpad butonları
        for (Button button : playerNumberButtons) {
            button.setOnClickListener(this);
        }

        // Bilgisayar tahmin bileşenleri
        feedbackDeleteButton.setOnClickListener(v -> deleteLastCharFromFeedback());
        feedbackSubmitButton.setOnClickListener(v -> submitFeedback());

        // Feedback butonları
        for (Button button : digitsButtons) {
            button.setOnClickListener(this);
        }
        plusButton.setOnClickListener(this);
        minusButton.setOnClickListener(this);

        findViewById(R.id.feedback_controls).setVisibility(View.VISIBLE);
    }

    /**
     * Oyun verilerini başlatır
     */
    private void initializeGameData() {
        playerGuesses = new ArrayList<>();
        computerGuessResults = new ArrayList<>();
        guessAdapter = new GuessAdapter(playerGuesses);
        guessHistoryList.setLayoutManager(new LinearLayoutManager(this));
        guessHistoryList.setAdapter(guessAdapter);
    }

    /**
     * Yeni oyun başlatır
     */
    private void startNewGame() {
        // Bilgisayarın ve kullanıcının tuttuğu sayıları oluştur
        computerSecret = gameEngine.generateSecretNumber();
        playerSecret = gameEngine.generateSecretNumber(); // Otomatik olarak kullanıcı için de bir sayı tut

        // Verileri temizle
        playerGuesses.clear();
        computerGuessResults.clear();
        guessAdapter.notifyDataSetChanged();

        // İlk durumu ayarla - Kullanıcı tahmin eder
        turn = 1;
        setGameState(STATE_PLAYER_GUESS);

        // Giriş alanlarını temizle
        playerGuessInput.setText("");
        feedbackInput.setText("");
    }

    /**
     * Oyun durumunu değiştirir (Kullanıcı tahmin / Bilgisayar tahmin)
     */
    private void setGameState(int state) {
        currentGameState = state;

        if (state == STATE_PLAYER_GUESS) {
            // Kullanıcı tahmin aşaması
            turnInfoText.setText(getString(R.string.turn_player, turn));
            playerGuessCard.setVisibility(View.VISIBLE);
            computerGuessCard.setVisibility(View.GONE);
        } else {
            // Bilgisayar tahmin aşaması
            turnInfoText.setText(R.string.turn_computer);
            playerGuessCard.setVisibility(View.GONE);
            computerGuessCard.setVisibility(View.VISIBLE);
            findViewById(R.id.feedback_controls).setVisibility(View.VISIBLE);

            // Bilgisayarın tahminini al
            currentComputerGuess = gameEngine.getComputerGuess(computerGuessResults);
            computerGuessText.setText(currentComputerGuess);
        }
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
                .setMessage(Constants.MODE_HARD.equals(gameMode) ?
                        R.string.hard_game_info : R.string.basic_game_info)
                .setPositiveButton(R.string.ok, null)
                .show();
    }

    /**
     * Buton tıklama olayları
     */
    @Override
    public void onClick(View v) {
        // Kullanıcı numpad butonları
        if (currentGameState == STATE_PLAYER_GUESS) {
            handlePlayerNumberButtonClick(v);
        }
        // Feedback butonları
        else if (currentGameState == STATE_COMPUTER_GUESS) {
            handleFeedbackButtonClick(v);
        }
    }

    /**
     * Kullanıcı numpad butonlarına tıklama
     */
    private void handlePlayerNumberButtonClick(View v) {
        // Mevcut giriş metnini al
        String currentInput = playerGuessInput.getText().toString();

        // 4 basamaktan fazla olmamalı
        if (currentInput.length() >= 4) {
            return;
        }

        // Hangi rakam butonu tıklandı?
        for (int i = 0; i < 10; i++) {
            if (v.getId() == playerNumberButtons[i].getId()) {
                String digit = String.valueOf(i);

                // Her rakam sadece bir kez kullanılabilir
                if (currentInput.contains(digit)) {
                    Toast.makeText(this, R.string.digit_already_used, Toast.LENGTH_SHORT).show();
                    return;
                }

                // Rakamı ekle
                playerGuessInput.setText(currentInput + digit);
                break;
            }
        }
    }

    /**
     * Feedback butonlarına tıklama
     */
    private void handleFeedbackButtonClick(View v) {
        String currentFeedback = feedbackInput.getText().toString();

        // Maksimum uzunluk kontrolü
        if (currentFeedback.length() >= 5 && v.getId() != feedbackDeleteButton.getId()) {
            return;
        }

        // + butonu
        if (v.getId() == plusButton.getId()) {
            feedbackInput.setText(currentFeedback + "+");
        }
        // - butonu
        else if (v.getId() == minusButton.getId()) {
            feedbackInput.setText(currentFeedback + "-");
        }
        // Boşluk butonu
        else if (v.getId() == digitsButtons[4].getId()) {
            feedbackInput.setText(currentFeedback + " ");
        }
        // Rakam butonları (1-4)
        else {
            for (int i = 0; i < 4; i++) {
                if (v.getId() == digitsButtons[i].getId()) {
                    feedbackInput.setText(currentFeedback + (i + 1));
                    break;
                }
            }
        }
    }

    /**
     * Kullanıcı tahmininden son rakamı siler
     */
    private void deleteLastDigitFromPlayerGuess() {
        String currentInput = playerGuessInput.getText().toString();
        if (!TextUtils.isEmpty(currentInput)) {
            playerGuessInput.setText(currentInput.substring(0, currentInput.length() - 1));
        }
    }

    /**
     * Kullanıcı tahminini gönderir
     */
    private void submitPlayerGuess() {
        String guess = playerGuessInput.getText().toString();

        // Geçerli bir tahmin mi?
        if (!gameEngine.isValidGuess(guess)) {
            Toast.makeText(this, R.string.invalid_guess, Toast.LENGTH_SHORT).show();
            return;
        }

        // Tahmini kontrol et
        GuessResult result = gameEngine.checkGuess(computerSecret, guess);

        // Tahminler listesine ekle
        GuessItem guessItem = new GuessItem(turn, guess, result);
        playerGuesses.add(0, guessItem); // En üste ekle
        guessAdapter.notifyItemInserted(0);
        guessHistoryList.scrollToPosition(0);

        // Giriş alanını temizle
        playerGuessInput.setText("");

        // Doğru tahmin mi?
        if (result.getBulls() == 4) {
            handlePlayerWin();
            return;
        }

        // Bilgisayarın tahmin etme sırasına geç
        setGameState(STATE_COMPUTER_GUESS);
    }

    /**
     * Feedback metninden son karakteri siler
     */
    private void deleteLastCharFromFeedback() {
        String currentInput = feedbackInput.getText().toString();
        if (!TextUtils.isEmpty(currentInput)) {
            feedbackInput.setText(currentInput.substring(0, currentInput.length() - 1));
        }
    }

    /**
     * Bilgisayar tahminine feedback gönderir
     */
    private void submitFeedback() {
        String feedback = feedbackInput.getText().toString();

        // Feedback formatını kontrol et
        if (!isValidFeedback(feedback)) {
            Toast.makeText(this, R.string.invalid_feedback, Toast.LENGTH_SHORT).show();
            return;
        }

        // Feedback'i GuessResult'a dönüştür
        GuessResult result = parseFeedback(feedback);

        // Tutarsız feedback kontrolü
        if (isInconsistentFeedback(result)) {
            showInconsistentFeedbackDialog();
            return;
        }

        // Sonucu bilgisayarın tahmin geçmişine ekle
        computerGuessResults.add(result);

        // Bilgisayar kazandı mı?
        if (result.getBulls() == 4) {
            handleComputerWin();
            return;
        }

        // Sonraki tura geç
        turn++;
        setGameState(STATE_PLAYER_GUESS);

        // Feedback giriş alanını temizle
        feedbackInput.setText("");
    }

    /**
     * Feedback metni geçerli mi kontrol eder
     */
    private boolean isValidFeedback(String feedback) {
        // +X -Y formatını kontrol et (örn: +1 -2)
        if (feedback.matches("\\+[0-4] -[0-4]")) {
            int bulls = Integer.parseInt(feedback.substring(1, 2));
            int cows = Integer.parseInt(feedback.substring(4, 5));
            return bulls + cows <= 4; // Bulls ve cows toplamı 4'ten fazla olamaz
        }
        return false;
    }

    /**
     * Feedback metnini GuessResult nesnesine dönüştürür
     */
    private GuessResult parseFeedback(String feedback) {
        int bulls = Integer.parseInt(feedback.substring(1, 2));
        int cows = Integer.parseInt(feedback.substring(4, 5));
        return new GuessResult(bulls, cows);
    }

    /**
     * Verilen feedback önceki feedbacklerle tutarlı mı kontrol eder
     */
    private boolean isInconsistentFeedback(GuessResult feedback) {
        // İlk feedback için tutarsızlık kontrolü yapılmaz
        if (computerGuessResults.isEmpty()) {
            return false;
        }

        // Basit tutarsızlık kontrolü: Tüm rakamlar doğru denmişse (4 bulls+cows)
        // Sonraki tahminlerde de 4 olmalı
        int totalBullsCows = feedback.getBulls() + feedback.getCows();
        for (GuessResult prevResult : computerGuessResults) {
            int prevTotal = prevResult.getBulls() + prevResult.getCows();

            // Önceden 4 denmişse ve şimdi daha az deniyor veya tam tersi
            if ((prevTotal == 4 && totalBullsCows < 4) ||
                    (prevTotal < 4 && totalBullsCows == 4)) {
                return true;
            }
        }

        // Gelişmiş tutarsızlık kontrolü yapılabilir, ama bu basit örnek için yeterli
        return false;
    }

    /**
     * Tutarsız feedback hata diyaloğunu gösterir
     */
    private void showInconsistentFeedbackDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.inconsistent_feedback_title)
                .setMessage(R.string.inconsistent_feedback_message)
                .setPositiveButton(R.string.ok, null)
                .show();
    }

    /**
     * Kullanıcı kazandığında çağrılır
     */
    private void handlePlayerWin() {
        // Rekoru güncelle
        int recordTurns = gamePreferences.getHighScore(gameMode);
        if (recordTurns == 0 || turn < recordTurns) {
            gamePreferences.saveHighScore(gameMode, turn);
        }

        // Tebrik diyaloğu göster
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.you_win)
                .setMessage(getString(R.string.player_win_message, turn, computerSecret))
                .setPositiveButton(R.string.new_game, (dialog, which) -> startNewGame())
                .setNegativeButton(R.string.back_to_menu, (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }

    /**
     * Bilgisayar kazandığında çağrılır
     */
    private void handleComputerWin() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.computer_wins)
                .setMessage(getString(R.string.computer_win_message, turn, playerSecret))
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