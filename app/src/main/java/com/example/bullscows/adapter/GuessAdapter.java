package com.example.bullscows.adapter;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bullscows.R;
import com.example.bullscows.activity.ComputerGameActivity;
import com.example.bullscows.activity.SoloGameActivity;
import com.example.bullscows.model.GuessResult;

import java.util.List;

/**
 * Tahmin geçmişini RecyclerView'da göstermek için adapter
 */
public class GuessAdapter extends RecyclerView.Adapter<GuessAdapter.GuessViewHolder> {

    private List<?> guesses; // SoloGameActivity.GuessItem veya ComputerGameActivity.GuessItem

    /**
     * Constructor
     * @param guesses Tahmin listesi
     */
    public GuessAdapter(List<?> guesses) {
        this.guesses = guesses;
    }

    @NonNull
    @Override
    public GuessViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_guess, parent, false);
        return new GuessViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull GuessViewHolder holder, int position) {
        Object guessItem = guesses.get(position);

        // SoloGameActivity.GuessItem tipinden mi?
        if (guessItem instanceof SoloGameActivity.GuessItem) {
            SoloGameActivity.GuessItem item = (SoloGameActivity.GuessItem) guessItem;
            holder.bind(item.getFormattedGuess(), item.getResult());
        }
        // ComputerGameActivity.GuessItem tipinden mi?
        else if (guessItem instanceof ComputerGameActivity.GuessItem) {
            ComputerGameActivity.GuessItem item = (ComputerGameActivity.GuessItem) guessItem;
            holder.bind(item.getFormattedGuess(), item.getResult());
        }
    }

    @Override
    public int getItemCount() {
        return guesses.size();
    }

    /**
     * Tahmin ViewHolder sınıfı
     */
    static class GuessViewHolder extends RecyclerView.ViewHolder {
        private TextView guessText;
        private TextView resultText;
        private CardView cardView;

        GuessViewHolder(@NonNull View itemView) {
            super(itemView);
            guessText = itemView.findViewById(R.id.text_guess);
            resultText = itemView.findViewById(R.id.text_result);
            cardView = itemView.findViewById(R.id.card_guess);
        }

        /**
         * ViewHolder'ı verilerle doldurur
         * @param guessString Tahmin metni
         * @param result Tahmin sonucu
         */
        void bind(String guessString, GuessResult result) {
            guessText.setText(guessString);
            resultText.setText(result.toBullsCowsString());

            // Bulls sayısına göre kart rengi değiştir
            int colorResId;
            switch (result.getBulls()) {
                case 4:
                    colorResId = R.color.correct_guess;
                    break;
                case 3:
                    colorResId = R.color.good_guess;
                    break;
                case 2:
                    colorResId = R.color.medium_guess;
                    break;
                case 1:
                    colorResId = R.color.poor_guess;
                    break;
                default:
                    colorResId = R.color.wrong_guess;
                    break;
            }

            int color = ContextCompat.getColor(itemView.getContext(), colorResId);
            cardView.setCardBackgroundColor(ColorStateList.valueOf(color));
        }
    }
}