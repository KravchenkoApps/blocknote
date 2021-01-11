package ru.kravchenkoapps.blocknote4;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class SomeDataRecyclerAdapter extends RecyclerView.Adapter<SomeDataRecyclerAdapter.NoteViewHolder> {
    List<Name> notes;
    private int dark_tag;

    public SomeDataRecyclerAdapter (List<Name> notes, int dark_tag) {
        this.notes = notes;
        this.dark_tag = dark_tag;

    }

    public static class NoteViewHolder extends RecyclerView.ViewHolder {

        CardView cv;
        TextView nameNote3, time3, textViewFolder, textListTag;
        ImageView viewFavourites, viewFolder;

        public NoteViewHolder (View view) {
            super(view);

            cv = view.findViewById(R.id.card_view_drawer1);
            nameNote3 = view.findViewById(R.id.name_note_3);
            time3 = view.findViewById(R.id.time_3);
            textViewFolder = view.findViewById(R.id.textViewFolder);
            textListTag = view.findViewById(R.id.textListTag);
            viewFavourites = view.findViewById(R.id.imageFavourites);
            viewFolder = view.findViewById(R.id.image_f_main);
        }
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view,parent,false);

        return new NoteViewHolder(view);
    }

    public void updateData(List <Name> notes) {
        this.notes = notes;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int i)  {
    if (dark_tag == 1) {
            holder.viewFolder.setImageResource(R.drawable.baseline_folder_white_48);
        }
    holder.nameNote3.setText(notes.get(i).namenote);
    @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
    holder.time3.setText(df.format(notes.get(i).time));

        holder.textViewFolder.setText(notes.get(i).directory);
    holder.textListTag.setText(notes.get(i).list_tag);
    if (notes.get(i).favourites == 1 & dark_tag == 0) {
        holder.viewFavourites.setImageResource(R.drawable.baseline_star_black_48);
    }
        if (notes.get(i).favourites == 1 & dark_tag == 1) {
            holder.viewFavourites.setImageResource(R.drawable.baseline_star_white_48);
        }
    if (notes.get(i).favourites == 0) {
        return;
    }

    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

}
