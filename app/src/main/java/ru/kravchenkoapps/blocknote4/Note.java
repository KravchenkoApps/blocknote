package ru.kravchenkoapps.blocknote4;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Note {


    @PrimaryKey(autoGenerate = true)
    public int id;
    @ColumnInfo (name = "time")
    public long time;
    @ColumnInfo (name = "name_note")
    public String namenote;// заголовок
    @ColumnInfo (name = "usertext")
    public String usertext;// текст пользователя
    @ColumnInfo (name = "directory")
    public String directory;// папка, в которой находится заметка
    @ColumnInfo (name = "on_delete")
    public int on_delete;// признак нахождения в корзине
    @ColumnInfo (name = "list_tag")
    public String list_tag;// признак того, что заметка является списком
    @ColumnInfo (name = "favourites")
    public int favourites;

    //дополнительные поля

    @ColumnInfo (name = "item_1")
    public int item_1;
    @ColumnInfo (name = "item_2")
    public int item_2;
    @ColumnInfo (name = "item_3")
    public long item_3;
    @ColumnInfo (name = "item_4")
    public String item_4;
    @ColumnInfo (name = "item_5")
    public String item_5;
}
