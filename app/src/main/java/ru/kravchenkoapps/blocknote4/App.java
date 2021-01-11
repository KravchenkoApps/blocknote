package ru.kravchenkoapps.blocknote4;

import android.app.Application;
import android.util.Log;

import androidx.room.Room;

public class App extends Application {
    public static App instance;
    private AppDatabase database;

    @Override
    public void onCreate() {
        super.onCreate();
    instance = this;
    database = Room.databaseBuilder(this, AppDatabase.class, "my31database")
            .allowMainThreadQueries()
            .build();
        Log.w("MY", ".build database in App.class;");
    }

    public static App getInstance() {
        return instance;
    }

    public AppDatabase getDatabase () {
        return database;
    }
}
