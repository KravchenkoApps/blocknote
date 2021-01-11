package ru.kravchenkoapps.blocknote4;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

public class DrawerListAdapter extends ArrayAdapter<String> {

    private final Context context;
    private final String[] values;
    private int dark_tag;
    //необходимо для установки иконок
    String all_notes_d;
    String favourites_d;
    String public_folder_d;
    String basket_d;
    String new_folder_d;
    String settings_d;
    String about_d;

    public DrawerListAdapter(@NonNull Context context, String[] values, int dark_tag) {
        super(context, R.layout.card_drawer_1, values);
        this.context = context;
        this.values = values;
        this.dark_tag = dark_tag;

        //необходимо для установки иконок
        all_notes_d = MainActivity.all_notes_s;
        favourites_d = MainActivity.favourites_s;
        public_folder_d = MainActivity.public_folder_s;
        basket_d = MainActivity.basket_s;
        new_folder_d = MainActivity.new_folder_s;
        settings_d = MainActivity.settings_s;
        about_d = MainActivity.about_s;
    }
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("ViewHolder") View rowView = inflater.inflate(R.layout.card_drawer_1, parent, false);
        TextView textView = rowView.findViewById(R.id.item_folder);
        ImageView imageView = rowView.findViewById(R.id.image_f_1);
        if (dark_tag == 1) {
            imageView.setImageResource(R.drawable.baseline_folder_white_48);
        }
        textView.setText(values[position]);
        // Изменение иконок в зависимости от позиции кнопки
        String s = values[position];

        if (s.equals(all_notes_d) && dark_tag == 0) {
            imageView.setImageResource(R.drawable.baseline_home_black_48);
            //Log.w("MY", "imageView.setImageResource(R.drawable.baseline_home_black_48)");
        }
        else if (s.equals(all_notes_d) && dark_tag == 1){
            imageView.setImageResource(R.drawable.baseline_home_white_48);
        }
        if (s.equals(new_folder_d) && dark_tag == 0) {
            imageView.setImageResource(R.drawable.baseline_create_new_folder_black_48);
        }
        else if (s.equals(new_folder_d) && dark_tag == 1){
            imageView.setImageResource(R.drawable.baseline_create_new_folder_white_48);
        }
        if (s.equals(public_folder_d) && dark_tag == 0) {
            imageView.setImageResource(R.drawable.baseline_folder_open_black_48);
        }
        else if (s.equals(public_folder_d) && dark_tag == 1){
            imageView.setImageResource(R.drawable.baseline_folder_open_white_48);
        }
        if (s.equals(favourites_d) && dark_tag == 0) {
            imageView.setImageResource(R.drawable.baseline_star_black_48);
        }
        else if (s.equals(favourites_d) && dark_tag == 1){
            imageView.setImageResource(R.drawable.baseline_grade_white_48);
        }

        if (s.equals(basket_d) && dark_tag == 0) {
            imageView.setImageResource(R.drawable.baseline_delete_black_48);
        }
        else if (s.equals(basket_d) && dark_tag == 1){
            imageView.setImageResource(R.drawable.baseline_delete_white_48);
        }
        if (s.equals(settings_d) && dark_tag == 0) {
            imageView.setImageResource(R.drawable.baseline_settings_black_48);
        }
        else if (s.equals(settings_d) && dark_tag == 1){
            imageView.setImageResource(R.drawable.baseline_settings_white_48);
        }
        if (s.equals(about_d) && dark_tag == 0) {
            imageView.setImageResource(R.drawable.baseline_android_black_48);
        }
        else if (s.equals(about_d) && dark_tag == 1){
            imageView.setImageResource(R.drawable.baseline_android_white_48);
        }

        return rowView;

    }


}
