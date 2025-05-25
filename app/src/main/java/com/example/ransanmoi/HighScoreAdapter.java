package com.example.ransanmoi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class HighScoreAdapter extends ArrayAdapter<HighScore> {
    private Context context;
    private List<HighScore> scores;

    public HighScoreAdapter(Context context, List<HighScore> scores) {
        super(context, R.layout.item_high_score, scores);
        this.context = context;
        this.scores = scores;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.item_high_score, parent, false);
            
            holder = new ViewHolder();
            holder.rankTextView = convertView.findViewById(R.id.textViewRank);
            holder.scoreTextView = convertView.findViewById(R.id.textViewScore);
            holder.dateTextView = convertView.findViewById(R.id.textViewDate);
            
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        HighScore score = scores.get(position);
        
        // Hiển thị thứ hạng (position + 1 vì position bắt đầu từ 0)
        holder.rankTextView.setText(String.format("#%d", position + 1));
        
        // Hiển thị điểm số
        holder.scoreTextView.setText(String.format("%d điểm", score.getScore()));
        
        // Hiển thị ngày giờ
        holder.dateTextView.setText(score.getFormattedDate());

        return convertView;
    }

    private static class ViewHolder {
        TextView rankTextView;
        TextView scoreTextView;
        TextView dateTextView;
    }
} 