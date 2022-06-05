package com.example.nmm.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nmm.R;
import com.example.nmm.ScoresClickListener;
import com.example.nmm.models.Scores;

import java.util.List;

public class ScoresListAdapter extends RecyclerView.Adapter<ScoresViewHolder>{

    Context context;
    List<Scores> list;
    ScoresClickListener listener;

    public ScoresListAdapter(Context context, List<Scores> list, ScoresClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ScoresViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ScoresViewHolder(LayoutInflater.from(context).inflate(R.layout.scores_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ScoresViewHolder holder, int position) {
        holder.title.setText(list.get(position).getTitle());
        holder.title.setSelected(true);

        holder.scores.setText(list.get(position).getScore());
        holder.winner.setText(list.get(position).getWinner());
        holder.date.setText(list.get(position).getDate());

        holder.scores_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClick(list.get(holder.getAdapterPosition()));
            }
        });
        holder.scores_container.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                listener.onLongClick(list.get(holder.getAdapterPosition()), holder.scores_container);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}

class ScoresViewHolder extends RecyclerView.ViewHolder {

    CardView scores_container;
    TextView title, scores, winner, date;

    public ScoresViewHolder(@NonNull View itemView) {
        super(itemView);
        scores_container = itemView.findViewById(R.id.scores_container);
        title = itemView.findViewById(R.id.title);
        scores = itemView.findViewById(R.id.scores);
        date = itemView.findViewById(R.id.date);
        winner = itemView.findViewById(R.id.winner);
    }
}
