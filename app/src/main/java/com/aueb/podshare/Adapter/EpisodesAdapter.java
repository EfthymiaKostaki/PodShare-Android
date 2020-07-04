package com.aueb.podshare.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.aueb.podshare.R;
import com.aueb.podshare.GetEpisodes;
import com.aueb.podshare.Utility;

import java.util.List;

public class EpisodesAdapter extends RecyclerView.Adapter<EpisodesAdapter.EpisodesAdapterViewHolder> {
    private int selectedPosition;
    Context context;
    List<GetEpisodes> episodes;
    private RecyclerItemClickListener listener;

    public EpisodesAdapter(Context context, List<GetEpisodes> episodes, RecyclerItemClickListener listener) {
        this.context = context;
        this.episodes = episodes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EpisodesAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.episodes, parent, false);

        return new EpisodesAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EpisodesAdapterViewHolder holder, int position) {
        GetEpisodes getEpisodes = episodes.get(position);
        if (getEpisodes != null) {
            if (selectedPosition == position) {
                holder.itemView.setBackground(ContextCompat.getColor(context, R.color.primaryColor));
                holder.iv_play_active.setVisibility(View.VISIBLE);
            } else {
                holder.itemView.setBackground(ContextCompat.getColor(context, R.color.otherColor));
                holder.iv_play_active.setVisibility(View.INVISIBLE);
            }
        }
        holder.tv_name.setText(getEpisodes.getName());
        String duration = Utility.convertDuration(Long.parseLong(getEpisodes.getEpisodeDuration()));

        holder.bind(getEpisodes, listener);

    }

    @Override
    public int getItemCount() {
        return episodes.size();
    }

    public class EpisodesAdapterViewHolder extends RecyclerView.ViewHolder{
        private TextView tv_name;
        ImageView iv_play_active;

        public EpisodesAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.name);
        }

        public void bind(final GetEpisodes getEpisodes, final RecyclerItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    listener.onClickListener(getEpisodes, getAdapterPosition());
                }
            });
        }
    }

    public interface RecyclerItemClickListener {
        void onClickListener(GetEpisodes episodes, int position);

    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
    }
}
