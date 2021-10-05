package vn.codefresher.musicplayer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MusicViewHolder>{

    private List<MusicModel> data = new ArrayList<>();
    private Callback callback;

    public MusicAdapter(Callback callback) {
        this.callback = callback;
    }

    @NonNull
    @Override
    public MusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_music, parent, false);
        return new MusicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicViewHolder holder, int position) {
        MusicModel musicModel = data.get(position);
        View view = holder.getView();
        TextView tvName = view.findViewById(R.id.tvName);
        tvName.setText(musicModel.getName());

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onClickItem(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setData(List<MusicModel> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    static class MusicViewHolder extends RecyclerView.ViewHolder {

        private View view;

        public MusicViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
        }

        View getView() {
            return view;
        }

    }

    interface Callback {
        void onClickItem(int position);
    }
}
