package ru.kravchenkoapps.blocknote4;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class CustomViewModel extends AndroidViewModel {

    private static AppDatabase database;

    public CustomViewModel(@NonNull Application application) {
        super(application);
       database = App.getInstance().getDatabase();
        Log.w("MY", "database = App.getInstance().getDatabase();");
        NoteDao noteDao = database.noteDao();
    }

    //используется при поиске внутри пользовательской папки, либо общей папки
    public  static LiveData<List<Name>> searchQuery(String queryText, String folder_selected) {
        return database.noteDao().searchFor(queryText,folder_selected);
        }
    //используется при поиске в разделе "Мои заметки"
    public  static LiveData<List<Name>> searchQueryAll(String queryText, String folder_selected) {
        return database.noteDao().searchForAll(queryText,folder_selected);
    }
    //используется при поиске в "Корзине"
    public  static LiveData<List<Name>> searchQueryBasket(String queryText, String folder_selected) {
        return database.noteDao().searchForBasket(queryText,folder_selected);
    }

}
