package ru.kravchenkoapps.blocknote4;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

public class DrawerAdapterSecond extends ArrayAdapter<String> {

    private final Context context;
    private final String[] values;

    public DrawerAdapterSecond(@NonNull Context context, String[] values) {
        super(context, R.layout.card_drawer_2, values);
        this.context = context;
        this.values = values;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row2View = inflater.inflate(R.layout.card_drawer_2, parent, false);
        TextView textView = row2View.findViewById(R.id.item_folder2);
        textView.setText(values[position]);
        return row2View;
    }
}
