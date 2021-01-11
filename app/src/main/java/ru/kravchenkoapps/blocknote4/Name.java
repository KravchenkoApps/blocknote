package ru.kravchenkoapps.blocknote4;

import androidx.room.ColumnInfo;


public class Name implements Comparable<Name>{
    @ColumnInfo(name = "name_note")
    public String namenote;// заголовок
    @ColumnInfo (name = "time")
    public long time;
    @ColumnInfo (name = "directory")
    public String directory;
    @ColumnInfo (name = "list_tag")
    public String list_tag;
    @ColumnInfo (name = "favourites")
    public int favourites;

    //необходимо для сортировки
    @Override
    public int compareTo(Name o) {
        return 0;
    }
}
