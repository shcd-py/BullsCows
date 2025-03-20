package com.example.bullscows.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bullscows.R;
import com.example.bullscows.database.Score;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Yüksek skorları RecyclerView'da göstermek için adapter
 */
public class ScoreAdapter extends RecyclerView.Adapter<ScoreAdapter.ScoreViewHolder> {

    private List<Score> scores;
    private SimpleDateFormat dateFormat;

    /**
     * Constructor
     * @param scores Skor listesi
     */
    public ScoreAdapter(List<Score> scores) {
        this.scores = scores;
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
    }

    @NonNull
    @Override
    public ScoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_score, parent, false);
        return new ScoreViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ScoreViewHolder holder, int position) {
        Score score = scores.get(position);

        // Pozisyon (1. 2. vs)
        holder.rankText.setText(String.format(Locale.getDefault(), "%d.", position + 1));

        // Atama bilgisi (x tahmin)
        holder.scoreText.setText(String.format(Locale.getDefault(), "%d tahmin", score.getAttempts()));

        // Tarih
        Date date = new Date(score.getDate());
        holder.dateText.setText(dateFormat.format(date));
    }

    @Override
    public int getItemCount() {
        return scores.size();
    }

    /**
     * Skor listesini günceller
     * @param newScores Yeni skor listesi
     */
    public void updateScores(List<Score> newScores) {
        this.scores = newScores;
        notifyDataSetChanged();
    }

    /**
     * Skor ViewHolder sınıfı
     */
    static class ScoreViewHolder extends RecyclerView.ViewHolder {
        TextView rankText;
        TextView scoreText;
        TextView dateText;

        ScoreViewHolder(@NonNull View itemView) {
            super(itemView);
            rankText = itemView.findViewById(R.id.text_rank);
            scoreText = itemView.findViewById(R.id.text_score);
            dateText = itemView.findViewById(R.id.text_date);
        }
    }
}