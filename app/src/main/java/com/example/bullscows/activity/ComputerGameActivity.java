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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private ImageButton btnSurrender; // Teslim olma butonu

    // UI bileşenleri - Kullanıcı Tahmin
    private CardView playerGuessCard;
    private RecyclerView guessHistoryList;
    private GuessAdapter guessAdapter;
    private EditText playerGuessInput;
    private ImageButton playerDeleteButton;
    private ImageButton playerSubmitButton;
    private Button[] playerNumberButtons = new Button[10];
    private View inputCard; // Tahmin giriş kartı
    private View playerNumpad; // Numpad konteyner

    // UI bileşenleri - Bilgisayar Tahmin
    private CardView computerGuessCard;
    private TextView computerGuessText;
    private EditText feedbackInput;
    private ImageButton feedbackDeleteButton;
    private ImageButton feedbackSubmitButton;
    private Button[] digitsButtons = new Button[5]; // 0, 1, 2, 3, 4 butonları
    private Button plusButton;
    private Button minusButton;
    private View feedbackControls; // Feedback kontrolleri konteyner

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

    private boolean gameFinished;
    private boolean surrendered;

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

        // Oyun durumu varsa yükle, yoksa yeni oyun başlat
        gameFinished = false;
        surrendered = false;
        if (!restoreGame()) {
            startNewGame();
        }

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
        btnSurrender = findViewById(R.id.btn_surrender);
        btnSurrender.setOnClickListener(v -> showSurrenderDialog());
        // Başlığı ayarla
        titleText.setText(Constants.MODE_HARD.equals(gameMode) ? R.string.hard_mode : R.string.basic_mode);

        // Kullanıcı tahmin bileşenleri
        playerGuessCard = findViewById(R.id.card_player_guess);
        guessHistoryList = findViewById(R.id.guesses_list);
        playerGuessInput = findViewById(R.id.player_guess_input);
        playerGuessInput.setFocusable(false); // Manuel giriş yerine numpad kullanılacak
        playerDeleteButton = findViewById(R.id.player_btn_delete);
        playerSubmitButton = findViewById(R.id.player_btn_submit);
        inputCard = findViewById(R.id.input_card);
        playerNumpad = findViewById(R.id.player_numpad);

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

        // Feedback kontrolleri
        feedbackControls = findViewById(R.id.feedback_controls);
        feedbackDeleteButton = findViewById(R.id.feedback_btn_delete);
        feedbackSubmitButton = findViewById(R.id.feedback_btn_submit);

        // Feedback butonları - 0, 1, 2, 3, 4 butonları
        for (int i = 0; i <= 4; i++) {
            int id = getResources().getIdentifier("btn_digit_" + i, "id", getPackageName());
            digitsButtons[i] = findViewById(id);
        }
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
        clearSavedGame();
        gameFinished = false;
        surrendered = false;
        // Bilgisayarın ve kullanıcının tuttuğu sayıları oluştur
        computerSecret = gameEngine.generateSecretNumber();
        playerSecret = gameEngine.generateSecretNumber(); // Otomatik olarak kullanıcı için de bir sayı tut

        // Bilgisayar AI'ını sıfırla
        if (computerAI != null) {
            computerAI.reset();
        }

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

            // Kullanıcı arayüzü göster
            playerGuessCard.setVisibility(View.VISIBLE);
            inputCard.setVisibility(View.VISIBLE);
            playerNumpad.setVisibility(View.VISIBLE);

            // Bilgisayar arayüzünü gizle
            computerGuessCard.setVisibility(View.GONE);
            feedbackControls.setVisibility(View.GONE);
        } else {
            // Bilgisayar tahmin aşaması
            turnInfoText.setText(R.string.turn_computer);

            // Kullanıcı arayüzünü gizle
            playerGuessCard.setVisibility(View.GONE);
            inputCard.setVisibility(View.GONE);
            playerNumpad.setVisibility(View.GONE);

            // Bilgisayar arayüzünü göster
            computerGuessCard.setVisibility(View.VISIBLE);
            feedbackControls.setVisibility(View.VISIBLE);

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
     * Pes etme işlemi için onay diyaloğunu gösterir
     */
    private void showSurrenderConfirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.surrender_title)
                .setMessage(R.string.surrender_confirm)
                .setPositiveButton(R.string.yes, (dialog, which) -> showSurrenderDialog())
                .setNegativeButton(R.string.no, null)
                .show();
    }

    /**
     * Teslim olma diyaloğunu gösterir
     */
    private void showSurrenderDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.surrender_title)
                .setMessage(getString(R.string.surrender_message, computerSecret))
                .setPositiveButton(R.string.new_game, (dialog, which) -> {
                    surrendered = true;
                    clearSavedGame();
                    startNewGame();
                })
                .setNegativeButton(R.string.back_to_menu, (dialog, which) -> {
                    surrendered = true;
                    clearSavedGame();
                    finish();
                })
                .setCancelable(false)
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
        // Rakam butonları (0-4)
        else {
            for (int i = 0; i <= 4; i++) {
                if (v.getId() == digitsButtons[i].getId()) {
                    feedbackInput.setText(currentFeedback + (i));
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

        // Feedback formatını kontrol et (Farklı formatları kabul ediyoruz)
        GuessResult result = parseFeedbackFormat(feedback);
        if (result == null) {
            Toast.makeText(this, R.string.invalid_feedback, Toast.LENGTH_SHORT).show();
            return;
        }

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
     * Farklı formatlardaki feedback metinlerini GuessResult nesnesine dönüştürür
     * Kabul edilen formatlar:
     * - "+X -Y" (örn: "+1 -2")
     * - "-X +Y" (örn: "-2 +1")
     * - "+X" (sadece bulls, örn: "+3")
     * - "-Y" (sadece cows, örn: "-2")
     * - "X,Y" (örn: "1,2" - 1 bull, 2 cow)
     * - "X" (sadece bulls, örn: "2" - 2 bull)
     */
    private GuessResult parseFeedbackFormat(String feedback) {
        if (feedback == null || feedback.trim().isEmpty()) {
            return null;
        }

        feedback = feedback.trim();

        // Format: "+X -Y" veya "-X +Y"
        Pattern pattern1 = Pattern.compile("(\\+|-)([0-4])\\s*(\\+|-)([0-4])");
        Matcher matcher1 = pattern1.matcher(feedback);

        if (matcher1.matches()) {
            int bulls = 0;
            int cows = 0;

            // İlk işaret + ise bulls, - ise cows
            if (matcher1.group(1).equals("+")) {
                bulls = Integer.parseInt(matcher1.group(2));
            } else {
                cows = Integer.parseInt(matcher1.group(2));
            }

            // İkinci işaret + ise bulls, - ise cows
            if (matcher1.group(3).equals("+")) {
                bulls = Integer.parseInt(matcher1.group(4));
            } else {
                cows = Integer.parseInt(matcher1.group(4));
            }

            // Toplam değer 4'ten büyük olamaz
            if (bulls + cows <= 4) {
                return new GuessResult(bulls, cows);
            }

            return null;
        }

        // Format: Sadece "+X" (bulls) veya "-X" (cows)
        Pattern pattern2 = Pattern.compile("(\\+|-)([0-4])");
        Matcher matcher2 = pattern2.matcher(feedback);

        if (matcher2.matches()) {
            int value = Integer.parseInt(matcher2.group(2));
            if (matcher2.group(1).equals("+")) {
                return new GuessResult(value, 0);
            } else {
                return new GuessResult(0, value);
            }
        }

        // Format: "X,Y" (X bulls, Y cows)
        Pattern pattern3 = Pattern.compile("([0-4]),([0-4])");
        Matcher matcher3 = pattern3.matcher(feedback);

        if (matcher3.matches()) {
            int bulls = Integer.parseInt(matcher3.group(1));
            int cows = Integer.parseInt(matcher3.group(2));

            if (bulls + cows <= 4) {
                return new GuessResult(bulls, cows);
            }

            return null;
        }

        // Format: Sadece "X" (X bulls)
        if (feedback.matches("[0-4]")) {
            return new GuessResult(Integer.parseInt(feedback), 0);
        }

        return null;
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
        gameFinished = true;
        clearSavedGame();
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
        gameFinished = true;
        clearSavedGame();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.computer_wins)
                .setMessage(getString(R.string.computer_win_message, turn, playerSecret))
                .setPositiveButton(R.string.new_game, (dialog, which) -> startNewGame())
                .setNegativeButton(R.string.back_to_menu, (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }

    /**
     * Kaydedilmiş oyunu yükler
     *
     * @return yüklendiyse true
     */
    private boolean restoreGame() {
        playerSecret = gamePreferences.getString(Constants.PREF_COMP_PLAYER_SECRET, null);
        computerSecret = gamePreferences.getString(Constants.PREF_COMP_COMPUTER_SECRET, null);
        if (playerSecret == null || computerSecret == null) {
            return false;
        }

        turn = gamePreferences.getInt(Constants.PREF_COMP_TURN, 1);
        currentGameState = gamePreferences.getInt(Constants.PREF_COMP_STATE, STATE_PLAYER_GUESS);
        currentComputerGuess = gamePreferences.getString(Constants.PREF_COMP_CURRENT_GUESS, null);

        playerGuesses.clear();
        computerGuessResults.clear();

        String playerJson = gamePreferences.getString(Constants.PREF_COMP_PLAYER_GUESSES, null);
        if (playerJson != null) {
            try {
                JSONArray arr = new JSONArray(playerJson);
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    int attempt = obj.getInt("attempt");
                    String guess = obj.getString("guess");
                    int bulls = obj.getInt("bulls");
                    int cows = obj.getInt("cows");
                    playerGuesses.add(new GuessItem(attempt, guess, new GuessResult(bulls, cows)));
                }
            } catch (JSONException ignored) {
            }
        }

        String compJson = gamePreferences.getString(Constants.PREF_COMP_RESULTS, null);
        if (compJson != null) {
            try {
                JSONArray arr = new JSONArray(compJson);
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    int bulls = obj.getInt("bulls");
                    int cows = obj.getInt("cows");
                    computerGuessResults.add(new GuessResult(bulls, cows));
                }
            } catch (JSONException ignored) {
            }
        }

        guessAdapter.notifyDataSetChanged();
        setGameState(currentGameState);
        if (currentGameState == STATE_COMPUTER_GUESS && currentComputerGuess != null) {
            computerGuessText.setText(currentComputerGuess);
        }
        return true;
    }

    /**
     * Oyun durumunu kaydeder
     */
    private void saveGame() {
        JSONArray playerArr = new JSONArray();
        JSONArray compArr = new JSONArray();
        try {
            for (GuessItem item : playerGuesses) {
                JSONObject obj = new JSONObject();
                obj.put("attempt", item.getAttemptNumber());
                obj.put("guess", item.getGuess());
                obj.put("bulls", item.getResult().getBulls());
                obj.put("cows", item.getResult().getCows());
                playerArr.put(obj);
            }
            for (GuessResult res : computerGuessResults) {
                JSONObject obj = new JSONObject();
                obj.put("bulls", res.getBulls());
                obj.put("cows", res.getCows());
                compArr.put(obj);
            }
        } catch (JSONException ignored) {
        }

        gamePreferences.saveString(Constants.PREF_COMP_PLAYER_SECRET, playerSecret);
        gamePreferences.saveString(Constants.PREF_COMP_COMPUTER_SECRET, computerSecret);
        gamePreferences.saveInt(Constants.PREF_COMP_TURN, turn);
        gamePreferences.saveInt(Constants.PREF_COMP_STATE, currentGameState);
        if (currentComputerGuess != null) {
            gamePreferences.saveString(Constants.PREF_COMP_CURRENT_GUESS, currentComputerGuess);
        }
        gamePreferences.saveString(Constants.PREF_COMP_PLAYER_GUESSES, playerArr.toString());
        gamePreferences.saveString(Constants.PREF_COMP_RESULTS, compArr.toString());
    }

    /**
     * Kaydedilmiş oyunu temizler
     */
    private void clearSavedGame() {
        gamePreferences.remove(Constants.PREF_COMP_PLAYER_SECRET);
        gamePreferences.remove(Constants.PREF_COMP_COMPUTER_SECRET);
        gamePreferences.remove(Constants.PREF_COMP_TURN);
        gamePreferences.remove(Constants.PREF_COMP_STATE);
        gamePreferences.remove(Constants.PREF_COMP_CURRENT_GUESS);
        gamePreferences.remove(Constants.PREF_COMP_PLAYER_GUESSES);
        gamePreferences.remove(Constants.PREF_COMP_RESULTS);
        gamePreferences.remove(Constants.PREF_COMP_SURRENDERED);
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

    @Override
    protected void onPause() {
        super.onPause();
        if (!gameFinished && !surrendered) {
            saveGame();
        }
    }
}