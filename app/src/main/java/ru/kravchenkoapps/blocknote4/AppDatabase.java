package ru.kravchenkoapps.blocknote4;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database (entities = {Note.class},exportSchema = false, version = 2)
public abstract class AppDatabase extends RoomDatabase {
    public abstract NoteDao noteDao ();
}
