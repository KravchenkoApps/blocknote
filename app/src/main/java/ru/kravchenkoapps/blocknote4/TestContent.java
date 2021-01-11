package ru.kravchenkoapps.blocknote4;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.util.Linkify;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static ru.kravchenkoapps.blocknote4.R.color.ThemeDark_hint_color;
import static ru.kravchenkoapps.blocknote4.R.color.ThemeDark_text_color;

public class TestContent extends AppCompatActivity {
    private long time;
    TextView Text_id_2;
    private TextView Name_note_2, User_text_2;
    private ImageView imageFolder;
    private String Text_Name_Note_2, Text_User_Text_2;
    private ActionBar actionBar;
    String textToSend;
    private static final int WRITE_REQUEST_CODE = 1;
    String dir_note;
    private String search_dir = "dir";
    String[] dirs;
    private String selected_dir;
    private int save_index = 0;//индекс нажатия для fab
    private SharedPreferences def_pref;//Для того, чтобы обратиться к памяти и вытащить из памяти то,
    //что сохранилось мы используем класс SharedPreferences
    String size;//переменная, в которую будет помещаться выбор пользователя по размеру такста
    Boolean links_switch;//переменная включения активности ссылок в заметке
    Boolean dark_switch;//переменная включения темной темы ночью
    private Window window; // переменная для изменения цвета статус-бара (там шже часы, связь и т.д.)
    ColorDrawable colorDrawable1;
    private String TimeStr;
    private int favourites;
    private Menu menu;
    public String TimeStr_alert , MessageForAlert;
    public Note note_alert;
    public AppDatabase database_alert;
    public NoteDao noteDao_alert;
    int dark_time = 0;//маркер темного времени суток, 0 - нет, 1 - да
    public FloatingActionButton EditFab;
    public boolean Keyboard_visible;
    String c_blue, c_purple, c_red, c_orange, c_green, c_grey, c_dark;
    View listConst;
    int Edit_tag = 0;//признак того, что текст заметки редактировался

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //подключаем настройки к переменной
        def_pref = PreferenceManager.getDefaultSharedPreferences(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_room);

        //устанавливаем кнопку Home на actionbar
        if (getSupportActionBar() != null)//если actionBar существует, то следующ.код исполнится
        {
            actionBar = getSupportActionBar(); //находим actionBar, если он есть
            actionBar.setDisplayHomeAsUpEnabled(true);//показываем в акшинбаре стрелочку выхода из него,то есть кнопка возврата домой
        }
        //привязываем в коде наши элементы экрана
        Text_id_2 = findViewById(R.id.text_id_2);
        Name_note_2 = findViewById(R.id.name_note_2);
        User_text_2 = findViewById(R.id.user_text_2);
        imageFolder = findViewById(R.id.image_f_edit);

        //код для подготовки к изменению цвета статус-бара
        window = this.getWindow();// без этого флага работать не будет
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);//без этого флага работать не будет
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);//без этого флага работать не будет
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorAccent));// finally change the color

        //получаем данные базы
        AppDatabase database = App.getInstance().getDatabase();
        NoteDao noteDao = database.noteDao();

        //получаем сообщение со значением времени заметки из MainActivity
        Intent i = getIntent();
        if (i != null) {
            time = i.getLongExtra("time",0);
        }
        //устанавливаем время в поле экрана
        @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
        Text_id_2.setText(df.format(time * 1000));

        //по времени заметки получаем все данные объекта Note и заполняем поля на экране
        TimeStr = String.valueOf(time) + "%";
        final Note note = noteDao.getTime1(TimeStr);
        Name_note_2.setText(note.namenote);
        User_text_2.setText(note.usertext);
        dir_note = note.directory;//сохраняем в переменную текущее название папки
        //устанавливаем звездочку
        favourites = note.favourites;//получаем значение звездочки из базы
        actionBar.setTitle(note.namenote);

        //отключаем режим редактирования текста при первом открытии заметки

        Name_note_2.setCursorVisible(false);
        Name_note_2.setShowSoftInputOnFocus(false);
        User_text_2.setCursorVisible(false);
        User_text_2.setShowSoftInputOnFocus(false);

        //НАСТРОЙКИ (текст, включение ссылок)
        init_text_size();
        text_active_links ();
        //добавляем слушатели (нужны для кнопки поделиться, чтобы заполнить текстовые переменные)
        //из которых текст берется для отправки
        Name_note_2.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                return false;
            }
        });
        User_text_2.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                return false;
            }
        });
        //отслеживаем изменение текста
        //необходимо для вывода openQuitDialog();
        Name_note_2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
            Edit_tag = 1;//ставим тег изменения текста
            }
        });
        User_text_2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                Edit_tag = 1;//ставим тег изменения текста
            }
        });

        //ВЫПАДАЮЩИЙ СПИСОК ПАПОК

        //получаем все имена папок из базы, за исключением той,
        //которая была установлена ранее на этой заметке (текущую папку)
        List<String> dirs_list = noteDao.getDirs2(search_dir, dir_note);
        dirs_list.add(0, dir_note);//добавляем на первое место в List текущую папку
        //если заметка в пользовательской папке, то добавляем Общую папку
        if (!dir_note.equals(getString(R.string.main_folder))) {
            dirs_list.add(getString(R.string.main_folder));
        }
        if (note.on_delete == 1) {
            dirs_list.clear();
            dirs_list.add(note.directory);
        }
        //преобразуем List в обычный массив String [] т.к. из List
        //адаптер некорректно выводит значения в отображаемый список на экране
        dirs = dirs_list.toArray(new String[dirs_list.size()]);
        //спиннер
        final Spinner spinner = findViewById(R.id.directory_2);

        // Создаем адаптер ArrayAdapter с помощью массива строк и стандартной разметки элемента spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.test_list_item, dirs);
        // Определяем разметку для использования при выборе элемента
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Применяем адаптер к элементу spinner
        spinner.setAdapter(adapter);

        //берем выбранное значение для записи в базу данных в поле directory, используя setOnItemSelectedListener
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (spinner.getSelectedItem() != null) {//если мы что-то выбрали
                    selected_dir = spinner.getSelectedItem().toString();//добавляем это в переменную и преобразуем в строку
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //КНОПКА РЕДАКТИРОВАНИЯ
        EditFab = findViewById(R.id.edit_fab);
        EditFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //включаем режим редактирования заметки
                if (note.on_delete != 1) {
                EditFab.hide();//скрываем FAB
                Name_note_2.setCursorVisible(true);
                Name_note_2.setShowSoftInputOnFocus(true);
                User_text_2.setShowSoftInputOnFocus(true);
                User_text_2.setCursorVisible(true);

                EditFab.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#669900")));
                EditFab.setImageResource(R.drawable.baseline_save_black_36);
                EditFab.setImageTintList(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));

                //открываем клавиатуру
                    openCloseKeyboard();


                Log.w("MY", String.valueOf(save_index));
                //если на FAB отображается дискета, то сохраняем и перемещаемся на главный экран
                    if (save_index > 0) {
                    Name_note_2.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                            return false;
                        }
                    });
                    User_text_2.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                            return false;
                        }
                    });

                    Text_Name_Note_2 = Name_note_2.getText().toString().trim();
                    Text_User_Text_2 = User_text_2.getText().toString();

                    //получаем данные базы
                    AppDatabase database = App.getInstance().getDatabase();
                    NoteDao noteDao = database.noteDao();
                    //по времени заметки получаем все данные объекта Note и заполняем поля объекта Note новыми данными
                    Note note = noteDao.getTime1(TimeStr);
                    note.namenote = Text_Name_Note_2.trim();//trim - удаление пробелов перед и после строки
                    note.usertext = Text_User_Text_2;
                    note.directory = selected_dir;
                    //обновляем время - устанавливаем текущее
                    note.time = Calendar.getInstance().getTimeInMillis();
                    noteDao.update(note);
                    Toast.makeText(TestContent.this, getString(R.string.t_save_note_4), Toast.LENGTH_LONG).show();
                    //перемещаемся на главный экран
                    Intent i = new Intent(TestContent.this, MainActivity.class);
                    startActivity(i);

                    InputMethodManager imm2 = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    if (imm2 != null) {
                        imm2.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                    }
                    TestContent.this.finish();
                } else {//иначе (при первом нажатии save_index = 1

                    save_index++;
                    Log.w("MY", String.valueOf(save_index));
                }
                     }
                else {
                    Toast.makeText(TestContent.this, getString(R.string.t_basket6), Toast.LENGTH_LONG).show();
                }

            }
        });
        //КОД, необходимый для работы темной темы ночью
        Date currentDate = new Date();//берем текущее время
        DateFormat timeFormat = new SimpleDateFormat("HH", Locale.getDefault());
        int time_now = Integer.parseInt(timeFormat.format(currentDate));//получаем текущий час в формате "HH" в числовую переменную
        Log.w("MY", String.valueOf(time_now));
        if (time_now >= 20 || time_now <= 6) {
            dark_time = 1;
        }
        else {
            dark_time = 0;
        }
        watchOpenKeyboard();
//получаем цвета (необходимо для корректной работы переключения языка приложения)
        c_blue = getString(R.string.c2_blue);
        c_purple = getString(R.string.c2_purple);
        c_red = getString(R.string.c2_red);
        c_orange = getString(R.string.c2_orange);
        c_green = getString(R.string.c2_green);
        c_grey = getString(R.string.c2_grey);
        c_dark = getString(R.string.c2_dark);
        //нужно для работы темной темы
        listConst = (View) findViewById(R.id.const_list_room);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    //обработчик кнопок в toolbar
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //пишем обработчик кнопки "Назад" (стрелочки) в actionBar
        if (item.getItemId() == android.R.id.home) //если кнопка назад = кнопке home
        //а эта кнопка в логику андроид именно так и вшита, то
        {
            finish();
        }
        //КНОПКА "СОХРАНИТЬ"
        if (item.getItemId() == R.id.save_edit2) {
            NameNoteListen(); //получаем введенный заголовок на текущий момент

            User_text_2.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    return false;
                }
            });

            Text_Name_Note_2 = Name_note_2.getText().toString();
            Text_User_Text_2 = User_text_2.getText().toString();

            //получаем данные базы
            AppDatabase database = App.getInstance().getDatabase();
            NoteDao noteDao = database.noteDao();
            //по времени заметки получаем все данные объекта Note и заполняем поля объекта Note новыми данными
            Note note = noteDao.getTime1(TimeStr);
            note.namenote = Text_Name_Note_2.trim();
            note.usertext = Text_User_Text_2;
            note.directory = selected_dir;
            note.favourites = favourites;
            note.time = Calendar.getInstance().getTimeInMillis();
            noteDao.update(note);
            Toast.makeText(this, getString(R.string.t_save_note_4), Toast.LENGTH_SHORT).show();
            //определяем, если клавиатура показана, то прячем ее
            final View activityRootView = getWindow().getDecorView().getRootView();
            activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    Rect r = new Rect();
                    //r will be populated with the coordinates of your view that area still visible.
                    activityRootView.getWindowVisibleDisplayFrame(r);
                    int heightDiff = activityRootView.getRootView().getHeight() - (r.bottom - r.top);
                    if (heightDiff < activityRootView.getRootView().getHeight() / 4 ) { // if more than 100 pixels, its probably a keyboard...
                        Keyboard_visible = false;// клавиатура hide
                        Log.w("MY", "Keyboard_visible = false;");
                    }
                    else{
                        Keyboard_visible = true; //клавиатура show
                        openCloseKeyboard();//значит закрываем ее
                        Log.w("MY", "Выполнено");

                    }
                }
            });

            //перемещаемся на главный экран
            Intent i = new Intent(TestContent.this, MainActivity.class);
            startActivity(i);
            TestContent.this.finish();

        }
        //КНОПКА "ВОССТАНОВИТЬ"
        if (item.getItemId() == R.id.recover_basket) {
            //получаем данные базы
            AppDatabase database = App.getInstance().getDatabase();
            NoteDao noteDao = database.noteDao();
            NoteDao noteDao2 = database.noteDao();
            String main_folder = getString(R.string.main_folder);
            //по времени заметки получаем все данные объекта Note
            Note note = noteDao.getTime1(TimeStr);
            //note.on_delete = 0;
            List<String> folders_list = noteDao.getDirs3(search_dir);
            folders_list.add(main_folder);
            final String[] folders_list_array = folders_list.toArray(new String[folders_list.size()]);
            int sizeArray = folders_list.size();//размер ArrayList
            Log.w("MY", String.valueOf(sizeArray));
            //int startIndex - стартовый индекс единицы массива
            //ищем, есть ли пользовательская папка, в которой ранее была эта заметка
            for (int startIndex = 0; startIndex < sizeArray; startIndex++) {
                if ((folders_list_array[startIndex]).equals(note.directory)) {//и если она была
                    note.on_delete = 0;//устанавливаем признак, что она уже не удалена
                    noteDao.update(note);//и обновляем заметку
                    Log.w("MY", String.valueOf(note.directory));
                    break;//и прерываем цикл
                }
                //но если пользовательская папка, в которой находилась заметка уже удалена
                note.on_delete = 0;
                note.directory = main_folder;//помещаем ее в общую папку при восстановлении
                noteDao.update(note);
                Log.w("MY", String.valueOf(note.directory));
            }

            Toast.makeText(this, getString(R.string.t_basket7), Toast.LENGTH_LONG).show();
            //перемещаемся на главный экран
            onMainActivity();
        }
        //КНОПКА "УДАЛИТЬ"
        if (item.getItemId() == R.id.delete_edit2 || item.getItemId() == R.id.delet_basket) {
            Log.w("MY", "Нажали на Удалить");

            //получаем в переменную время, на выходе имеем GetLongTime
            String GetTime = (String) Text_id_2.getText();
            Log.w("MY", GetTime);
            @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
            Date date = new Date();
            try {
                date = df.parse(GetTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            assert date != null;
            final long GetLongTime = (date.getTime()) / 1000;
            //получаем Заголовок заметки
            NameNoteListen();
            Text_Name_Note_2 = Name_note_2.getText().toString();
            //переводим время в понятную величину для отправки запроса в базу данных
            TimeStr_alert = String.valueOf(GetLongTime) + "%";
            //получаем базу
            database_alert = App.getInstance().getDatabase();
            noteDao_alert = database_alert.noteDao();
            //по времени заметки получаем все данные объекта Note
            note_alert = noteDao_alert.getTime1(TimeStr_alert);
            if (note_alert.on_delete == 0) {
                MessageForAlert = getString(R.string.alert_delete_1) + " " + getString(R.string.alert_delete_2);
            }
            if (note_alert.on_delete == 1) {
                MessageForAlert = getString(R.string.alert_delete_1) + " " + getString(R.string.alert_delete_3);
            }

            final AlertDialog.Builder dialog_del_card_view = new AlertDialog.Builder(TestContent.this);

            dialog_del_card_view.setTitle(getString(R.string.alert_del_name) + '"' + Text_Name_Note_2 + '"')
                    .setMessage(MessageForAlert)
                    .setIcon(R.drawable.baseline_delete_black_24)
                    .setPositiveButton(getString(R.string.button_positive), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //если запись не в корзине
                            if (note_alert.on_delete == 0) {
                                note_alert.on_delete = 1; // помещаем ее в корзину
                                note_alert.namenote = Text_Name_Note_2;
                                noteDao_alert.update(note_alert); //обновляем запись
                                //перемещаемся на главный экран
                                Toast.makeText(TestContent.this, getString(R.string.t_basket3), Toast.LENGTH_SHORT).show();
                                onMainActivity();
                            }
                            //если запись в корзине
                            else if (note_alert.on_delete == 1) {
                                //удаляем заметку из базы Room
                                noteDao_alert.delete(note_alert);
                                Toast.makeText(TestContent.this, getString(R.string.t_basket4), Toast.LENGTH_SHORT).show();
                                //перемещаемся на главный экран
                                onMainActivity();
                            }

                        }
                    })
                    .setNegativeButton(getString(R.string.button_negative), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            dialog_del_card_view.show();

        }//конец кнопки УДАЛИТЬ

        //пишем обработку кнопки "Отправить/Поделиться"
        if (item.getItemId() == R.id.send_edit2) {
            //заполняем переменные из слушателя вводимого текста
            //т.е. что отображается в текст вью с учетом редактирование, то и вставляется в переменные
            Text_Name_Note_2 = Name_note_2.getText().toString();
            Text_User_Text_2 = User_text_2.getText().toString();
            //формируем текстовую переменную, которая служит для отправки текста в функции "Поделиться"
            textToSend = "*" + Text_Name_Note_2.trim() + "*" + "\n\n" + Text_User_Text_2;
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, textToSend);

            sendIntent.setType("text/plain");

            Intent shareIntent = Intent.createChooser(sendIntent, null);
            startActivity(shareIntent);
            textToSend = null;
        }
        //пишем обработку кнопки "Сохранить в файл"
        if (item.getItemId() == R.id.save_file_edit2) {
            createFile();
        }
        //пишем обработку кнопки "Избранное" (звездочка)
        if (item.getItemId() == R.id.favourites_click2) {
            if (favourites == 0) {
                menu.getItem(0).setIcon(getDrawable(R.drawable.baseline_star_white_48));
                favourites++ ;
                Log.w("MY", "Сработка - if (favourites == 0)");
            }
            else  {
                menu.getItem(0).setIcon(getDrawable(R.drawable.baseline_star_border_white_48));
                favourites--;
                Log.w("MY", "Сработка - if (favourites == 1)");
            }
        }

//иначе вернуть истину, то есть все будет работать, как и работает
        return true;
    }


    //добавляем меню с кнопками: сохранить, отправить, удалить, избранное
    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //получаем данные базы
        AppDatabase database = App.getInstance().getDatabase();
        NoteDao noteDao = database.noteDao();
        //по времени заметки получаем все данные объекта Note
        Note note = noteDao.getTime1(TimeStr);
        if (note.on_delete == 1) { //если заметка в Корзине, то
            getMenuInflater().inflate(R.menu.basket_menu, menu);
        } else if (note.on_delete == 0) { //если заметка НЕ в Корзине, то другое меню
            getMenuInflater().inflate(R.menu.edit2_menu, menu);
        }
        this.menu = menu;//нужно для звездочки
        if (favourites == 1) {//если она установлена
            menu.getItem(0).setIcon(getDrawable(R.drawable.baseline_star_white_48));
        }//если не установлена, то картинка загружается та, которая стоит в меню (пустая звездочка)
        return true; //super.onCreateOptionsMenu(menu)
    }

    //кнопка сохранения в файл во внешнем накопителе (SD-карте)
    // createFile()
    // onActivityResult
    // writeInFile

    private void createFile() {
        String textName = Name_note_2.getText().toString() + ".txt";
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TITLE, textName);

// Optionally, specify a URI for the directory that should be opened in
// the system file picker when your app creates the document.
        //intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, link);

        startActivityForResult(intent, WRITE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String textBody = User_text_2.getText().toString();
        if (requestCode == WRITE_REQUEST_CODE) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    if (data != null
                            && data.getData() != null) {
                        writeInFile(data.getData(), textBody);//используем OutputStream и BufferedWriter для записи
                    }
                    break;
                case Activity.RESULT_CANCELED:
                    break;
            }
        }
    }

    private void writeInFile(@NonNull Uri uri, @NonNull String text) {
        OutputStream outputStream;
        try {
            outputStream = getContentResolver().openOutputStream(uri);
            assert outputStream != null;//!!!!!!!!!!!!!!!!!
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outputStream));
            bw.write(text);
            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Toast.makeText(this, getString(R.string.t_save_note_5), Toast.LENGTH_LONG).show();

    }

    ///ФУНКЦИЯ ИЗМЕНЕНИЯ РАЗМЕРА ТЕКСТА
    private void init_text_size() //инициализация текста
    {

        size = def_pref.getString("text_size_note", "Middle");//подвязываем ключ из preference_screen.xml к def_pref и size
        //когда функция getString найдет значение,которое выбрал пользователь на экране preference_screen.xml, она присвоит его переменной size
        if (size != null) {//условие ЕСЛИ нужно для того, чтобы в случае, если size будет пустым (пользователь ничего не выбрал)
            //то приложение может выдать ошибку и закрыться. Чтобы этого не произошло, проверяем
            //то,что в переменной size есть значениеяё
            switch (size) { //переключаем размер текста в зависимости от значения в переменной size
                case "Big":
                    Name_note_2.setTextSize(24);//устанавливаем размер текста заголовка
                    User_text_2.setTextSize(24);//устанавливаем размер текста статьи
                    break;//при совпадении первого кейса прерываем switch и т.д.
                case "Middle":
                    Name_note_2.setTextSize(18);//устанавливаем размер текста заголовка
                    User_text_2.setTextSize(18);//устанавливаем размер текста статьи
                    break;
                case "Small":
                    Name_note_2.setTextSize(14);//устанавливаем размер текста заголовка
                    User_text_2.setTextSize(14);//устанавливаем размер текста статьи
                    break;

            }
        }

    }//конец изменения размера текста

    ////ФУНКЦИЯ ВКЛЮЧЕНИЯ АКТИВНЫХ ССЫЛОК В ТЕКСТЕ ЗАМЕТКИ
    private void text_active_links() {
        links_switch = def_pref.getBoolean("switch_preference_links", false);
        int links_switch_int;
        if (!links_switch) {
            links_switch_int = 0;
        }
        else links_switch_int = 1;
        if (links_switch != null) {
            switch (links_switch_int) {
                case 1:
                    Linkify.addLinks(User_text_2, Linkify.WEB_URLS);
                    User_text_2.setLinksClickable(true);
                    break;
            }
        }
    }
    ////ИЗМЕНЕНИЕ ЦВЕТОВОЙ СХЕМЫ
    private void settings_themes() {
        String theme_color = def_pref.getString("key_theme_color", getString(R.string.c2_blue));//подвязываем ключ из preference_screen.xml к def_pref и size
        dark_switch = def_pref.getBoolean("switch_preference_dark", false);
        if (theme_color != null & !dark_switch ||theme_color != null & dark_switch & dark_time == 0) {
            Log.w("MY", "Код не исполнился");
            colorDrawable1 = new ColorDrawable ();
            if (theme_color.equals(c_blue)) {
                colorDrawable1.setColor(Color.parseColor("#3C4CB3"));
                actionBar.setBackgroundDrawable(colorDrawable1);
                window.setStatusBarColor(ContextCompat.getColor(this,R.color.StatusBar_Blue2));
            }
            if (theme_color.equals(c_purple)) {
                colorDrawable1.setColor(Color.parseColor("#6A5ACD"));
                actionBar.setBackgroundDrawable(colorDrawable1);
                window.setStatusBarColor(ContextCompat.getColor(this,R.color.StatusBar_Purple));
            }
            if (theme_color.equals(c_red)) {
                colorDrawable1.setColor(Color.parseColor("#B22222"));
                actionBar.setBackgroundDrawable(colorDrawable1);
                window.setStatusBarColor(ContextCompat.getColor(this,R.color.StatusBar_Red));
            }
            if (theme_color.equals(c_orange)) {
                colorDrawable1.setColor(Color.parseColor("#FF8C00"));
                actionBar.setBackgroundDrawable(colorDrawable1);
                window.setStatusBarColor(ContextCompat.getColor(this,R.color.StatusBar_Orange));
            }
            if (theme_color.equals(c_green)) {
                colorDrawable1.setColor(Color.parseColor("#659A3F"));
                actionBar.setBackgroundDrawable(colorDrawable1);
                window.setStatusBarColor(ContextCompat.getColor(this,R.color.StatusBar_Green2));
            }
            if (theme_color.equals(c_grey)) {
                colorDrawable1.setColor(Color.parseColor("#808080"));
                actionBar.setBackgroundDrawable(colorDrawable1);
                window.setStatusBarColor(ContextCompat.getColor(this,R.color.StatusBar_Grey));
            }
            if (theme_color.equals(c_dark)) {
                colorDrawable1.setColor(Color.parseColor("#282828"));
                actionBar.setBackgroundDrawable(colorDrawable1);
                window.setStatusBarColor(ContextCompat.getColor(this,R.color.StatusBar_Dark));
                setTheme(R.style.AppThemeDark);
                //устанавливаем фон, т.к. стиль не меняет фон на View, а тут constr...layout почему то View
                listConst.setBackgroundColor(ContextCompat.getColor(this, R.color.ThemeDark_fon));
                //устанавливаем цвета шрифта во View, т.к. стиль их тоже не меняет
                Name_note_2.setTextColor(ContextCompat.getColor(this, ThemeDark_text_color));
                Name_note_2.setHintTextColor(ContextCompat.getColor(this, ThemeDark_hint_color));
                User_text_2.setTextColor(ContextCompat.getColor(this, ThemeDark_text_color));
                User_text_2.setHintTextColor(ContextCompat.getColor(this, ThemeDark_hint_color));
                Text_id_2.setTextColor(ContextCompat.getColor(this, ThemeDark_text_color));
                imageFolder.setImageResource(R.drawable.baseline_folder_white_48);
                //устанавливаем цвет фона спиннера, т.к. стиль их тоже не меняет
                Spinner spinner_dark = findViewById(R.id.directory_2);
                ColorDrawable dark_color = new ColorDrawable();
                dark_color.setColor(Color.parseColor("#303030"));
                spinner_dark.setPopupBackgroundDrawable(dark_color);
            }
        }
//иначе включаем темную тему
        else {
            colorDrawable1 = new ColorDrawable();
            colorDrawable1.setColor(Color.parseColor("#282828"));
            actionBar.setBackgroundDrawable(colorDrawable1);
            window.setStatusBarColor(ContextCompat.getColor(this,R.color.StatusBar_Dark));
            setTheme(R.style.AppThemeDark);
            //устанавливаем фон, т.к. стиль не меняет фон на View, а тут constr...layout почему то View
            listConst.setBackgroundColor(ContextCompat.getColor(this, R.color.ThemeDark_fon));
            //устанавливаем цвета шрифта во View, т.к. стиль их тоже не меняет
            Name_note_2.setTextColor(ContextCompat.getColor(this, ThemeDark_text_color));
            Name_note_2.setHintTextColor(ContextCompat.getColor(this, ThemeDark_hint_color));
            User_text_2.setTextColor(ContextCompat.getColor(this, ThemeDark_text_color));
            User_text_2.setHintTextColor(ContextCompat.getColor(this, ThemeDark_hint_color));
            Text_id_2.setTextColor(ContextCompat.getColor(this, ThemeDark_text_color));
            imageFolder.setImageResource(R.drawable.baseline_folder_white_48);
            //устанавливаем цвет фона спиннера, т.к. стиль их тоже не меняет
            Spinner spinner_dark = findViewById(R.id.directory_2);
            ColorDrawable dark_color = new ColorDrawable();
            dark_color.setColor(Color.parseColor("#303030"));
            spinner_dark.setPopupBackgroundDrawable(dark_color);
        }
    }

    @Override
    protected void onResume() {
        settings_themes();
        super.onResume();

    }

    public void NameNoteListen () {
        Name_note_2.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                return false;
            }
        });

    }
    //перемещение на главный экран (после сохранения, удаления и т.д.)
    public void onMainActivity() {
        Intent i = new Intent(TestContent.this, MainActivity.class);
        startActivity(i);

    }
    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        if (Edit_tag ==1) {
            openQuitDialog();
        }
        else {
            onMainActivity();
        }
    }
    //ВЫ ТОЧНО ХОТИТЕ ВЫЙТИ БЕЗ СОХРАНЕНИЯ
    private void openQuitDialog() {
        AlertDialog.Builder quitDialog = new AlertDialog.Builder(
                this);
        quitDialog.setTitle(getString(R.string.pop_exit_note));

        quitDialog.setPositiveButton(getString(R.string.button_positive), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onMainActivity();

            }
        });

        quitDialog.setNegativeButton(getString(R.string.button_negative), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        quitDialog.show();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        finish();
    }

    public void openCloseKeyboard() {
        //открываем/закрываем клавиатуру
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
    }
    //определяем, показана клавиатура или нет, если не показана, то выводим FAB
    public void watchOpenKeyboard() {
        final View activityRootView = getWindow().getDecorView().getRootView();
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                //r will be populated with the coordinates of your view that area still visible.
                activityRootView.getWindowVisibleDisplayFrame(r);
                int heightDiff = activityRootView.getRootView().getHeight() - (r.bottom - r.top);
                if (heightDiff < activityRootView.getRootView().getHeight() / 4 ) { // if more than 100 pixels, its probably a keyboard...
                    Keyboard_visible = false;// клавиатура hide
                    EditFab.show();
                }
                else{
                    Keyboard_visible = true; //клавиатура show
                    EditFab.hide();
                }
            }
        });
    }

}

