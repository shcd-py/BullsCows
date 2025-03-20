package com.example.bullscows.model;

/**
 * Oyun durumunu temsil eden sınıf
 * Oyunun mevcut durumunu ve istatistiklerini tutar
 */
public class GameState {

    // Oyun modu sabitleri
    public static final int MODE_SOLO = 0;
    public static final int MODE_BASIC = 1;
    public static final int MODE_HARD = 2;

    // Oyun durumu sabitleri
    public static final int STATE_IN_PROGRESS = 0;
    public static final int STATE_PLAYER_WON = 1;
    public static final int STATE_COMPUTER_WON = 2;

    private int gameMode;
    private int gameState;
    private String playerSecret;
    private String computerSecret;
    private int attempts;
    private int turn;

    /**
     * Varsayılan constructor
     */
    public GameState() {
        gameMode = MODE_SOLO;
        gameState = STATE_IN_PROGRESS;
        attempts = 0;
        turn = 1;
    }

    /**
     * Parametreli constructor
     * @param gameMode Oyun modu
     */
    public GameState(int gameMode) {
        this.gameMode = gameMode;
        gameState = STATE_IN_PROGRESS;
        attempts = 0;
        turn = 1;
    }

    /**
     * Komple parametreli constructor
     * @param gameMode Oyun modu
     * @param gameState Oyun durumu
     * @param playerSecret Oyuncunun tuttuğu sayı
     * @param computerSecret Bilgisayarın tuttuğu sayı
     * @param attempts Tahmin sayısı
     * @param turn Tur sayısı
     */
    public GameState(int gameMode, int gameState, String playerSecret,
                     String computerSecret, int attempts, int turn) {
        this.gameMode = gameMode;
        this.gameState = gameState;
        this.playerSecret = playerSecret;
        this.computerSecret = computerSecret;
        this.attempts = attempts;
        this.turn = turn;
    }

    /**
     * Oyunu sıfırlar
     */
    public void reset() {
        gameState = STATE_IN_PROGRESS;
        attempts = 0;
        turn = 1;
    }

    /**
     * Tahmin sayısını bir artırır
     */
    public void incrementAttempts() {
        attempts++;
    }

    /**
     * Tur sayısını bir artırır
     */
    public void incrementTurn() {
        turn++;
    }

    // Getter ve Setter metodları

    public int getGameMode() {
        return gameMode;
    }

    public void setGameMode(int gameMode) {
        this.gameMode = gameMode;
    }

    public int getGameState() {
        return gameState;
    }

    public void setGameState(int gameState) {
        this.gameState = gameState;
    }

    public String getPlayerSecret() {
        return playerSecret;
    }

    public void setPlayerSecret(String playerSecret) {
        this.playerSecret = playerSecret;
    }

    public String getComputerSecret() {
        return computerSecret;
    }

    public void setComputerSecret(String computerSecret) {
        this.computerSecret = computerSecret;
    }

    public int getAttempts() {
        return attempts;
    }

    public void setAttempts(int attempts) {
        this.attempts = attempts;
    }

    public int getTurn() {
        return turn;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    /**
     * Oyun modunu string olarak döndürür
     */
    public String getGameModeAsString() {
        switch (gameMode) {
            case MODE_SOLO:
                return "Solo";
            case MODE_BASIC:
                return "Basic";
            case MODE_HARD:
                return "Hard";
            default:
                return "Unknown";
        }
    }

    /**
     * Oyun durumunu string olarak döndürür
     */
    public String getGameStateAsString() {
        switch (gameState) {
            case STATE_IN_PROGRESS:
                return "In Progress";
            case STATE_PLAYER_WON:
                return "Player Won";
            case STATE_COMPUTER_WON:
                return "Computer Won";
            default:
                return "Unknown";
        }
    }
}