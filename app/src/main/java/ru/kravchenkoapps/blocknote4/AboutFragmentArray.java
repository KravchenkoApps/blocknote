package ru.kravchenkoapps.blocknote4;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AboutFragmentArray extends Fragment {
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //инфлэйтим активити
        View v = inflater.inflate(R.layout.activity_about, null);
        //находит ListView, в который уже подтянут array через активити в поле entries
        ListView ListAbout = (ListView) v.findViewById(R.id.list_about);
       // обрабатываем нажатия на пункты
        ListAbout.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    getFragmentManager().beginTransaction().replace(android.R.id.content, new AboutFragmentPolicy()).commit();
                }
                if (i == 1) {
                    Log.w("MY", "Нажали кнопку купить");
                    MainActivity ma = (MainActivity) getActivity();
                    ma.launchBilling("notepad_full_version");

                }
            }
        });

        return v;
    }


}
