package ru.kravchenkoapps.blocknote4;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface NoteDao {
    @Query("SELECT * FROM note")
    Note [] getAll();

    //@Query("SELECT * FROM note WHERE id = :id")
    //Note getById(int id);

    //основной запрос для открытия заметки и работой с заметкой
    @Query("SELECT * FROM note WHERE time LIKE :TimeStr ")
    Note getTime1(String TimeStr);

    //дополнительный запрос на выборку заметок, находящихся в польз.папке, которая редактируется или удаляется
    @Query("SELECT * FROM note WHERE time LIKE :time ")
    Note getTime2(Long time);

    //основной запрос, показывающий все заметки
    @Query("SELECT name_note, time, directory, list_tag, favourites FROM Note WHERE name_note NOT LIKE :search_dir AND on_delete <> :on_del")
    List<Name> getNameNote(String search_dir, int on_del);

    //запрос, показывающий Избранные заметки
    @Query("SELECT name_note, time, directory, list_tag, favourites FROM Note WHERE name_note NOT LIKE :search_dir AND on_delete <> :on_del AND favourites = 1")
    List<Name> getFavourites(String search_dir, int on_del);

    //выборка заметок из пользовательской папки, либо из общей папки ДЛЯ ОТОБРАЖЕНИЯ В ПАПКЕ
    @Query("SELECT name_note, time, directory, list_tag, favourites FROM Note WHERE directory LIKE :select_dir AND name_note NOT like :search_dir AND on_delete <> :on_del")
    List<Name> openDir(String select_dir, String search_dir, int on_del);

    //выборка заметок из пользовательской папки, либо из общей папки ДЛЯ УДАЛЕНИЯ
    @Query("SELECT time FROM Note WHERE directory LIKE :select_dir AND name_note NOT like :search_dir AND on_delete <> :on_del")
    List<Long> noteForDelete(String select_dir, String search_dir, int on_del);

    //выборка всех заметок в Корзине для ее полного очищения
    @Query("SELECT time FROM Note WHERE on_delete = :on_del AND name_note NOT LIKE 'dir'")
    List<Long> basketForDelete(int on_del);

    //выборка всех пользовательских папок (для отображения в DrawerLayout и для восстановления заметки)
    @Query("SELECT directory FROM Note WHERE name_note LIKE :search_dir")
    List<String> getDirs(String search_dir);

    @Query("SELECT directory FROM Note WHERE name_note LIKE :search_dir AND NOT directory = :dir_note ")
    List<String> getDirs2(String search_dir, String dir_note);

    @Query("SELECT directory FROM Note WHERE name_note LIKE :search_dir")
    List<String> getDirs3(String search_dir);

    //для отработки удаления папки (только папки)
    @Query("SELECT * FROM Note WHERE directory LIKE :this_dir")
    Note getThisDir(String this_dir);

    @Query("SELECT COUNT (directory) FROM Note WHERE name_note LIKE :search_dir")
    int getDirs4(String search_dir);

    //используется для ПОИСКА по Общей папке и пользовательским папкам
    @Query("SELECT name_note, time, directory, list_tag, favourites FROM Note WHERE name_note LIKE :searchquery " +
            "AND directory LIKE :folder_selected AND on_delete <> 1")
    LiveData<List<Name>> searchFor(String searchquery, String folder_selected);

    //используется для ПОИСКА по "Моим заметкам"
    @Query("SELECT name_note, time, directory, list_tag, favourites FROM Note WHERE name_note LIKE :searchquery " +
            "AND directory <> :folder_selected AND on_delete <> 1")
    LiveData<List<Name>> searchForAll(String searchquery, String folder_selected);

    //используется для ПОИСКА по "Корзине"
    @Query("SELECT name_note, time, directory, list_tag, favourites FROM Note WHERE name_note LIKE :searchquery " +
            "AND directory <> :folder_selected AND on_delete = 1")
    LiveData<List<Name>> searchForBasket(String searchquery, String folder_selected);

    @Insert
    void insert (Note note);

    @Update
    void update (Note note);

    @Delete
    void delete (Note note);

    @Delete
    void deleteDir (Note note);

    }
