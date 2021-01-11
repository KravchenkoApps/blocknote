package ru.kravchenkoapps.blocknote4;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputFilter;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

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

import static ru.kravchenkoapps.blocknote4.R.color.ThemeDark_fon;
import static ru.kravchenkoapps.blocknote4.R.color.ThemeDark_hint_color;
import static ru.kravchenkoapps.blocknote4.R.color.ThemeDark_text_color;

public class CheckboxActivity extends AppCompatActivity {
    TableLayout tableLayout;
    TableRow RowNameNote;
    TableRow RowDate;
    public int i = 0;//id текстового поля в строке
    int a;//id строки при добавлении в цикле при открытии списка из MainActivity
    public int c = 200;//id чекбокса
    int id_name_note = 100;
    int id_add_button = 800;
    TextView Name_Note;//переменная для поля в xml
    TextView New_Item;
    String Text_Item = "";
    AppDatabase database;
    NoteDao noteDao;
    long time;//время, полученное через intent с главного экрана
    Button AddButton;
    CheckBox checkBox;//1
    EditText editText;//2
    ImageButton Delete_item;//3
    TableRow tr;
    int OpenMode = 0;
    String TimeStr;
    Note note2;
    String search_dir = "dir";
    String dir_note;
    private String[] dirs;
    private String selected_dir;
    String main_folder;
    private String Text_Name_Note;
    String textToSend;
    private static final int WRITE_REQUEST_CODE = 1;
    int favourites;
    Menu menu;//для установки favourites
    private SharedPreferences def_pref;//Для того, чтобы обратиться к памяти и вытащить из памяти то,
    //что сохранилось мы используем класс SharedPreferences
    private Window window; // переменная для изменения цвета статус-бара (там шже часы, связь и т.д.)
    ColorDrawable colorDrawable1;
    private ActionBar actionBar;
    String check_in_text;
    public String TimeStr_alert;
    public Note note_alert;
    public String MessageForAlert;
    public AppDatabase database_alert;
    public NoteDao noteDao_alert;
    public TextView newDate;
    String user_folder;
    public List<String> dirs_list;
    int dark_time = 0;//маркер темного времени суток, 0 - нет, 1 - да
    Boolean dark_switch;//переменная включения темной темы ночью
    int ForSplitEnd = 0;//признак завершения цикла дбавления строк (при открытии существующего списка)
    int firstTimeNewLine = 0;//признак добавления строки первый раз
    int SoftKeyOpen = 0;//признак открытой/закрытой клавиатуры
    String c_blue, c_purple, c_red, c_orange, c_green, c_grey, c_dark;
    public Spinner spinner;
    String theme_color;

    //float dX, dY;

    //@SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_checkbox);

        //устанавливаем кнопку Home на actionbar
        if (getSupportActionBar() != null)//если actionBar существует, то следующ.код исполнится
        {
            actionBar = getSupportActionBar(); //находим actionBar, если он есть
            actionBar.setDisplayHomeAsUpEnabled(true);//показываем в акшинбаре стрелочку выхода из него,то есть кнопка возврата домой
        }

        //получаем цвета (необходимо для корректной работы переключения языка приложения)
        c_blue = getString(R.string.c2_blue);
        c_purple = getString(R.string.c2_purple);
        c_red = getString(R.string.c2_red);
        c_orange = getString(R.string.c2_orange);
        c_green = getString(R.string.c2_green);
        c_grey = getString(R.string.c2_grey);
        c_dark = getString(R.string.c2_dark);

        tableLayout = findViewById(R.id.TableLayout);
        tableLayout.setStretchAllColumns(true);//растягиваем ячейки на всю ширину
        RowDate = findViewById(R.id.row_date);//находим строку даты
        RowNameNote = findViewById(R.id.row_name_note);//находим строку для заголовка в макете

        main_folder = getResources().getString(R.string.main_folder);

        //подключаем НАСТРОЙКИ к переменной
        def_pref = PreferenceManager.getDefaultSharedPreferences(this);
        theme_color = def_pref.getString("key_theme_color", getString(R.string.c2_blue));
        dark_switch = def_pref.getBoolean("switch_preference_dark", false);

        addNameNote();//добавляем в эту строку заголовок
        Name_Note = findViewById(id_name_note);
        NameNoteListen();
        //код для подготовки к изменению цвета статус-бара
        window = this.getWindow();// без этого флага работать не будет
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);//без этого флага работать не будет
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);//без этого флага работать не будет
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorAccent));// finally change the color

        //получаем сообщение со значением времени заметки из MainActivity
        Intent list = getIntent();
        time = list.getLongExtra("time",0);
        /////////////
        //если время не нулевое, то мы ОТКРЫЛИ заметку-список из главного экрана
        ////////////
        if (time != 0) {
        BaseGet();
        OpenMode = 1;//устанавливаем режим открытия
        TimeStr = time + "%";
        note2 = noteDao.getTime1(TimeStr);
        dir_note = note2.directory;
        favourites = note2.favourites;
        Name_Note.setText(note2.namenote);
        actionBar.setTitle(getString(R.string.list1));
        String ForSplit = note2.usertext;
        if (!ForSplit.equals("")) {//если список внутри не пустой, тогда открываем его
            Log.w("MY", "String ForSplit = " + ForSplit);
            String[] subStr;
            String delimeter_1 = "\n"; // Разделитель 1
            String delimeter_2 = "]";
            subStr = ForSplit.split(delimeter_1); // Разделение строки str с помощью метода split()
            for(a = 0; a < subStr.length; a++) {
                String [] subChek;
                subChek = subStr[a].split(delimeter_2);
                Log.w("MY", "subChek[0] = " + subChek[0]);
                Log.w("MY", "subChek[1] = " + subChek[1]);
                ForSplitEnd = 1;
                addNewEditText(subChek[1], a, subChek[0]);

            }
            i = subStr.length - 1;
            Log.w("MY", "i = subStr.length - 1 = " + i);
            //editText.setFocusable(true);
            firstTimeNewLine = 0;

            }// конец, если список не пустой
            else {
            addNewEditText("", i,"");
        }
            //после исполнения кода на загрузку строк, если список не пустой
            //либо на открытие пустой строки, если список был совсем пустой
            //исполняется код ниже
            //тут на этой строке изначально был OpenMode = 1;
            //ПОЛУЧЕМ ДАННЫЕ ДЛЯ SPINNER ОТКРЫТОГО СПИСКА
            dirs_list = noteDao.getDirs2(search_dir, dir_note);
            dirs_list.add(0, dir_note);//добавляем на первое место в List текущую папку
            //если заметка в пользовательской папке, то добавляем Общую папку
            if (!dir_note.equals(main_folder)) {
                dirs_list.add(main_folder);
            }
            if (note2.on_delete == 1) {
                dirs_list.clear();
                dirs_list.add(note2.directory);
            }
            //преобразуем List в обычный массив String [] т.к. из List
            //адаптер некорректно выводит значения в отображаемый список на экране
            dirs = dirs_list.toArray(new String[dirs_list.size()]);
        }
        //////
        //В противном случае это НОВЫЙ СПИСОК
        /////
        else  {
            addNewEditText("", i,"");
            //ПОЛУЧЕМ ДАННЫЕ ДЛЯ SPINNER НОВОГО СПИСКА

            actionBar.setTitle(getString(R.string.new_list1));
           }
        //добавляем кнопку ДОБАВИТЬ СТРОКУ
        addButtonNewLine();
        //добавляем дату и папку
        addDateAndFolder();
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
//определяем, показана клавиатура или нет
        final View activityRootView = getWindow().getDecorView().getRootView();
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                //r will be populated with the coordinates of your view that area still visible.
                activityRootView.getWindowVisibleDisplayFrame(r);
                int heightDiff = activityRootView.getRootView().getHeight() - (r.bottom - r.top);
                if (heightDiff < activityRootView.getRootView().getHeight() / 4 ) { // if more than 100 pixels, its probably a keyboard...
                    // клавиатура hide
                    SoftKeyOpen = 0;
                }
                else{
                    //клавиатура show
                    SoftKeyOpen = 1;
                }
            }
        });

    }//конец onCreate
    //НОВАЯ СТРОКА
    //@SuppressLint("ClickableViewAccessibility")
    public void addNewEditText(String text, int p, @NonNull String strFlag) {
        //firstTimeNewLine = 1;
        LayoutInflater inflater = getLayoutInflater();
        //если установлена темная тема
        if (theme_color.equals(c_dark)||dark_switch) {
            tr = (TableRow)inflater.inflate(R.layout.table_row_dark, tableLayout, false);
            //находим также элементы в макете, добавляем их в код
            checkBox = tr.findViewById(R.id.checkBox);
            editText = tr.findViewById(R.id.tvText);
            Delete_item = tr.findViewById(R.id.button_del_row);

            editText.setTextColor(ContextCompat.getColor(this, ThemeDark_text_color));
            Log.w("MY", "Установлена темная тема");
        }
        else {//все другие цветовые темы
        tr = (TableRow)inflater.inflate(R.layout.table_row, tableLayout, false);
        //находим также элементы в макете, добавляем их в код
        checkBox = tr.findViewById(R.id.checkBox);
        editText = tr.findViewById(R.id.tvText);
        Delete_item = tr.findViewById(R.id.button_del_row);
        }

        editText.setText(text);
        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(80);
        editText.setFilters(filterArray);
        editText.setId(p);
        if (OpenMode == 0) {//если это новый список
            editText.requestFocus();//ставим фокус на новую строку
            if (SoftKeyOpen == 0) {
                //открываем клавиатуру
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                }
                SoftKeyOpen = 1;
            }
        }
        if (OpenMode == 1 && ForSplitEnd == 1 && firstTimeNewLine == 1)//если это существющий список
            //и он уже открылся (отработал цикл добавления строк при открытии
            //и первый раз добавляем новую строку
        {
            editText.requestFocus();
            editText.setFocusable(true);
            if (SoftKeyOpen == 0) {
            //открываем клавиатуру
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            }
                SoftKeyOpen = 1;
            }
            Log.w("MY", "OpenMode == 1 && ForSplitEnd == 1 && firstTimeNewLine == 1");
        }
        //editText.requestFocus();
        Log.w("MY", "ID новой строки = " + p);

        GradientDrawable gd_et = new GradientDrawable();
        //gd.setColor(Color.parseColor("#00ffffff"));
        gd_et.setStroke(3,Color.parseColor("#F38E8E8E"));
        gd_et.setCornerRadius(9);

        editText.setBackground(gd_et);
        editText.setPadding(15,23,15,23);

        //запрет на ввод новой строки в ячейке (кнопка Enter на клавиатуре не сработает)
        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {

                if (keyCode==KeyEvent.KEYCODE_ENTER)
                {
                    // Just ignore the [Enter] key
                    return true;
                }
                // Handle all other keys in the default way
                return (keyCode == KeyEvent.KEYCODE_ENTER);
            }
        });
        checkBox.setId(c);
        if (strFlag.equals("[V")) {
            checkBox.setChecked(true);
        }
        Log.w("MY", "ID чекбокса = " + c);
        c++;
        //добавляем строку в table
        tableLayout.addView(tr);

 //////////////////////////////////////////////
////////////КНОПКА УДАЛЕНИЯ СТРОКИ/////////////
//////////////////////////////////////////////
        Delete_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View row = (View) v.getParent();//получаем родитель - всю строку, для удаления
                ViewGroup vg = (ViewGroup) v.getParent();//получаем родитель для получения child
                View v_ed = vg.getChildAt(1);//получаем объект child по индексу, в данном случае EditText с экрана, у него в строке индекс 1
                int e = v_ed.getId();//получаем ID этого EditText
                //int new_id_et = e;//новый ID для присваивания EditText
                int new_id_cb = e+200;//новый ID для присваивания checkBox

                for (int y = e+1; y <= i; y++) {
                    EditText ThisEditText = tableLayout.findViewById(y);//находим следующий по списку editText
                    Log.w("MY", "находим следующий по списку editText = " + y);
                    //if (editText != null) {//если он есть
                    ThisEditText.setId(y-1);//присваиваем ему ID текущего элемента
                    Log.w("MY", "присваиваем ему ID текущего элемента ThisEditText.setId = " + (y-1));
                        checkBox = tableLayout.findViewById(y+200);//находим следующий по списку checkBox
                        checkBox.setGravity(Gravity.CENTER);
                        Log.w("MY", "находим следующий по списку checkBox = " + (y+200));
                        checkBox.setId(new_id_cb);//присваиваем ему новый ID, равный ID текущего элемента
                        Log.w("MY", "присваиваем ему новый ID, равный ID текущего элемента = " + new_id_cb);
                        c = new_id_cb + 1;
                        Log.w("MY", "c = " + c);
                        new_id_cb++;
                        e = y;
                }//конец for

                i--;
                Log.w("MY", "i-- = " + i);
                Log.w("MY", "e = " + e);
                if(e > 1) {
                c = e+200;
                    Log.w("MY", "c = " + c);
                }
                else {
                    c = 200;
                    Log.w("MY", " else c = " + c);
                }

                tableLayout.removeView(row);
            }//конец onClick(View v)
        });//конец setOnClickListener
    } //конец addNewEditText

    //добавляем заголовок
    public void addNameNote () {
        TableRow.LayoutParams params_name = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
        params_name.span = 3;

        EditText NameNote = new EditText(this);
        params_name.setMargins(20,0,20,10);
        if (theme_color.equals(c_dark) || dark_switch) {
            NameNote.setTextColor(ContextCompat.getColor(this, ThemeDark_text_color));
            NameNote.setHintTextColor(ContextCompat.getColor(this, ThemeDark_hint_color));
        }
        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(25);
        NameNote.setFilters(filterArray);
        NameNote.setId(id_name_note);
        NameNote.setHint(getResources().getString(R.string.hint_name_list));
        NameNote.setPadding(15,25,15,25);//внутренние отступы
        GradientDrawable gd_nn = new GradientDrawable();
        gd_nn.setStroke(3,Color.parseColor("#F38E8E8E"));
        gd_nn.setCornerRadius(9);

        NameNote.setBackground(gd_nn);
        RowNameNote.addView(NameNote, params_name);
    }
    //добавляем кнопку добавления новой строки
    public void addButtonNewLine () {
        TableRow.LayoutParams params_button = new TableRow.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1f);

        final TableRow tableRowButton = new TableRow(this);
        tableRowButton.setLayoutParams(params_button);

        AddButton = new Button(this);
        AddButton.setId(id_add_button);
        AddButton.setText(getString(R.string.add_line));

        tableRowButton.addView(AddButton);
        tableLayout.setStretchAllColumns(true);
        final LinearLayout lb = new LinearLayout(this);
        LinearLayout.LayoutParams params_lb = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        lb.setLayoutParams(params_lb);
        lb.addView(tableRowButton);
        tableLayout.addView(lb);

        AddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstTimeNewLine = 1;
                tableLayout.removeView(lb);
                i++;
                addNewEditText("", i, "" );
                addButtonNewLine();
            }
        });
    }
    //добавляем дату и выбор папки
    public void addDateAndFolder () {
        newDate = new TextView(this);
        ImageView folderView = new ImageView(this);
        folderView.setImageResource(R.drawable.baseline_folder_black_48);

        if (theme_color.equals(c_dark)||dark_switch) {
            newDate.setTextColor(ContextCompat.getColor(this, ThemeDark_text_color));
            folderView.setImageResource(R.drawable.baseline_folder_white_24);//белая папка
        }
        spinner = new Spinner(this);
        newDate.setPadding(0,20,0,20);//внутренний отступ

        newDate.setLines(2);

        //общие параметры
        TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,TableRow.LayoutParams.MATCH_PARENT);

        //параметры даты
        TableRow.LayoutParams newDateParams = new TableRow.LayoutParams(0,0,0.1f);
        newDateParams.height = TableRow.LayoutParams.WRAP_CONTENT;
        int MyHeight1 = 110; // желаемая ширина
        float density = getApplicationContext().getResources().getDisplayMetrics().density; // плотность пикселей экрана
        newDateParams.width = (int) (density * MyHeight1);

        newDateParams.setMargins(20,0,10,0);
        newDateParams.gravity = Gravity.CENTER_HORIZONTAL;
        newDateParams.gravity = Gravity.START;

        //параметры изображения папки
        TableRow.LayoutParams folderViewParams = new TableRow.LayoutParams(0,0,0.1f);
        int MyHeight2 = 34;
        folderViewParams.width = (int) (density * MyHeight2);
        folderViewParams.height = (int) (density * MyHeight2);
        folderViewParams.gravity = Gravity.END;
        folderViewParams.gravity = Gravity.CENTER_VERTICAL;

        //параметры спиннера
        TableRow.LayoutParams spinnerParams = new TableRow.LayoutParams(0,0,0.8f);
        spinnerParams.setMargins(0,0,15,0);
        int MyWidth1 = 190;
        spinnerParams.width = (int) (density * MyWidth1);
        spinnerParams.height = TableRow.LayoutParams.MATCH_PARENT;
        spinner.setPadding(0,0,5,0);
        @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
        if (OpenMode == 0) {//если это новый список
        //получаем текущее время и устанавливаем его
        Date calendar = Calendar.getInstance().getTime();
        newDate.setText(df.format(calendar));
        //устанавливаем спиннер с папками
            BaseGet();
            dirs_list = noteDao.getDirs(search_dir);
            dirs_list.add(0, main_folder);
            //получаем интент с папкой, в которой находимся, далее устанавливаем эту папку в спиннер
            Intent fold = getIntent();
            user_folder = fold.getStringExtra("user_folder");
            Log.w("MY", "user_folder = " + user_folder);
            //нужно, чтобы работал интент из других приложений,
            //потому что user_folder в этом случае со значением null будет приходить
            if (user_folder != null) {
                int index_selected_folder = dirs_list.indexOf(user_folder);//по названию папки получаем номер индекса в массиве
                if (index_selected_folder != 0) {//если находим такую папку
                    dirs_list.remove(user_folder); //то удаляем ее из списка
                    dirs_list.add(0, user_folder); // и на первое место в список добавляем ее же
                }
                dirs = dirs_list.toArray(new String[dirs_list.size()]);
            }

        //spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.test_list_item, dirs);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }
    if (OpenMode == 1) {//если мы открыли список из Main Activity
        newDate.setText(df.format(time * 1000));
        // Создаем адаптер ArrayAdapter с помощью массива строк и стандартной разметки элемета spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.test_list_item, dirs);
        // Определяем разметку для использования при выборе элемента
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Применяем адаптер к элементу spinner
        spinner.setAdapter(adapter);
    }
    RowDate.addView(newDate, newDateParams);
    RowDate.addView(folderView, folderViewParams);
    RowDate.addView(spinner, spinnerParams);

        //берем выбранное значение ПАПКИ для записи в базу данных в поле directory, используя setOnItemSelectedListener
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
    }
    //получение базы
    public void BaseGet () {
        database = App.getInstance().getDatabase();
        noteDao = database.noteDao();
    }

    ///////УСТАНАВЛИВАЕМ МЕНЮ//////////////
    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //в зависмости от того, новый это список или нет, из корзины или нет
        //загружаем соответствующее меню
        if (OpenMode == 0) {
            getMenuInflater().inflate(R.menu.list_menu_new, menu);
        }
        if (OpenMode == 1 && note2.on_delete == 0) {
            getMenuInflater().inflate(R.menu.edit2_menu, menu);
        }
        if ((OpenMode == 1 && note2.on_delete == 1)){
            getMenuInflater().inflate(R.menu.basket_menu, menu);
        }
        this.menu = menu;//нужно для звездочки
        if (favourites == 1) {//если она установлена
            menu.getItem(0).setIcon(getDrawable(R.drawable.baseline_star_white_48));
        }//если не установлена, то картинка загружается та, которая стоит в меню (пустая звездочка)

        return super.onCreateOptionsMenu(menu);
    }
    ///////ОБРАБОТКА КНОПОК В МЕНЮ//////////////
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//пишем обработчик кнопки "Назад" (стрелочки) в actionBar
        if (item.getItemId() == android.R.id.home){ //если кнопка назад = кнопке home
            if (SoftKeyOpen == 1) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                }
            }
            finish();
            Toast.makeText(this, getString(R.string.t_save_note_2), Toast.LENGTH_LONG).show();
        }
//пишем обработчик кнопки "СОХРАНИТЬ"
        if (item.getItemId() == R.id.save_edit2 || item.getItemId() == R.id.save_list) {
            saveButton();
        }
//пишем обработчик кнопки "УДАЛИТЬ"
        if (item.getItemId() == R.id.delete_edit2 || item.getItemId() == R.id.delet_basket) {
            deleteButton();
        }
//пишем обработчик кнопки "ОТПРАВИТЬ"
        if (item.getItemId() == R.id.send_list || item.getItemId() == R.id.send_edit2) {
            sendButton();
        }
//пишем обработчик кнопки "СОХРАНИТЬ В ФАЙЛ"
        if (item.getItemId() == R.id.save_file_edit2) {
            createFile();
        }
//пишем обработчик кнопки "ИЗБРАННОЕ"
        if (item.getItemId() == R.id.favourites_list || item.getItemId() == R.id.favourites_click2) {
            favButton();
        }
        return super.onOptionsItemSelected(item);
    }
    ////КНОПКА СОХРАНИТЬ////
    public void saveButton() {
        Note note = new Note();
        note.list_tag = getResources().getString(R.string.list1);
        //создаем массив и циклом, затем в массив складываем ID вьюшек, в которых содержится текст
        int [] mas = new int[i+1];
        for (int n = 0; n <= i; n++) {
            mas[n] = n;
            Log.w("MY", "mas[n] = n РАВНО " + n);
            //далее в цикле находим саму вьюшку (текстовую строку)
            New_Item = findViewById(mas[n]);
            CheckBox Chek = findViewById(n+200);//и чекбокс
            if (Chek.isChecked()) {
                check_in_text = "[V]";
            }
            else {
                check_in_text = "[ ]";
            }
            //получаем из нее текст и через разделитель складываем в текстовую переменную
            if (New_Item != null) {
                if (!New_Item.getText().toString().equals("")) {//если текст в строке есть
                    Text_Item = Text_Item + check_in_text + New_Item.getText().toString() + "\n";
                }
                //если тест в строку не введен, то и сохраняться такая строка не будет;
                        Log.w("MY", Text_Item);
            }
        }
        //устанавливаем название списка, если название пустое, то вставляем R.string.new_list
        if (Name_Note.getText().toString().equals("")) {
            @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            Date date_moment = Calendar.getInstance().getTime();//берем именно время момента сохранения
            Text_Name_Note = getResources().getString(R.string.new_list) + " (" + df.format(date_moment) + ")";
        }
        else {
            Text_Name_Note = Name_Note.getText().toString();
        }

        //добавляем данные в поля Заметки и сохраняем
        note.namenote = Text_Name_Note;
        note.usertext = Text_Item;
        note.on_delete = 0;
        note.directory = selected_dir;
        note.favourites = favourites;
        note.time = Calendar.getInstance().getTimeInMillis();//берем текущее время в миллисекундах
        if (OpenMode == 0) {//если это новый список, то вставляем новый объект note в базу
            noteDao.insert(note);
        }
        if (OpenMode == 1) {//если это открытая заметка, то переназначаем поля в уже открытом объекте note2
            note2.namenote = note.namenote;
            note2.usertext = note.usertext;
            note2.directory = note.directory;
            note2.favourites = favourites;
            note2.time = note.time;
            noteDao.update(note2);//обновляем открытый объект
            Log.w("MY", "noteDao.update(note);");
        }
        if (New_Item == null) {
            Toast.makeText(this, getString(R.string.t_save_note_3), Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(this, getString(R.string.t_save_note_4), Toast.LENGTH_LONG).show();
            //возвращаемся на главный экран
            Intent i = new Intent(CheckboxActivity.this, MainActivity.class);
            startActivity(i);
            CheckboxActivity.this.finish();
        }
        if (SoftKeyOpen == 1) {
            //закрываем клавиатуру
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            }
            SoftKeyOpen = 0;
        }
    }
    ////код для сохранения в файл////
    public void codeFileTextBody() {
        Note note3 = new Note();
        note3.list_tag = getResources().getString(R.string.list1);
        //создаем массив и циклом затем в массив складываем ID вьюшек, в которых содержится текст
        int [] mas = new int[i+1];
        for (int n = 0; n <= i; n++) {
            mas[n] = n;
            Log.w("MY", "mas[n] = n РАВНО " + n);
            //далее в цикле находим саму вьюшку
            New_Item = findViewById(mas[n]);
            //int r = n + 200;
            CheckBox Chek = findViewById(n+200);//и чекбокс
            if (Chek.isChecked()) {
                Text_Item = Text_Item + "[ v ] ";
            }
            else {
                Text_Item = Text_Item + "[    ] ";
            }
            //получаем из нее текст и через разделитель складываем в текстовую переменную
            if (New_Item != null) {
                Text_Item = Text_Item + New_Item.getText().toString() + "\n";
                Log.w("MY", Text_Item);
            }
        }
    }
    ////КНОПКА УДАЛИТЬ////
    //получаем значение времени для последующего запроса в базу
    public void deleteButton() {
        String GetTime = (String) newDate.getText();
        Log.w("MY", "String GetTime = " + GetTime);
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
        Text_Name_Note = Name_Note.getText().toString();
        //переводим время в понятную величину для отправки запроса в базу данных
        TimeStr_alert = String.valueOf(GetLongTime) + "%";
        //получаем базу
        database_alert = App.getInstance().getDatabase();
        noteDao_alert = database_alert.noteDao();
        //database_alert = App.getInstance().getDatabase();
        //noteDao_alert = database_alert.noteDao();
        //по времени заметки получаем все данные объекта Note
        note_alert = noteDao_alert.getTime1(TimeStr_alert);
        if (note_alert.on_delete == 0) {
            MessageForAlert = getString(R.string.alert_delete_1) + " " + getString(R.string.alert_delete_2);
        }
        if (note_alert.on_delete == 1) {
            MessageForAlert = getString(R.string.alert_delete_1) + " " + getString(R.string.alert_delete_3);
        }

        final AlertDialog.Builder dialog_del_card_view = new AlertDialog.Builder(CheckboxActivity.this);
        dialog_del_card_view.setTitle(getString(R.string.alert_del_name) + '"' + Text_Name_Note + '"')
                .setMessage(MessageForAlert)
                .setIcon(R.drawable.baseline_delete_black_24)
                .setPositiveButton(getString(R.string.button_positive), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //если запись не в корзине
                        if (note_alert.on_delete == 0) {
                            note_alert.on_delete = 1; // помещаем ее в корзину
                            note_alert.namenote = Text_Name_Note;
                            noteDao_alert.update(note_alert); //обновляем запись
                            //перемещаемся на главный экран
                            Toast.makeText(CheckboxActivity.this, getString(R.string.t_basket5), Toast.LENGTH_SHORT).show();
                            onMainActivity();
                        }
                        //если запись в корзине
                        else if (note_alert.on_delete == 1) {
                            //удаляем заметку из базы Room
                            noteDao_alert.delete(note_alert);
                            Toast.makeText(CheckboxActivity.this, getString(R.string.t_basket4), Toast.LENGTH_SHORT).show();
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
    }//конкц кнопки Удалить
    ////КНОПКА ОТПРАВИТЬ////
    public void sendButton() {
        //заполняем переменные из слушателя вводимого текста
        //т.е. что отображается в текст вью с учетом редактирование, то и вставляется в переменные

        //создаем массив и циклом затем в массив складываем ID вьюшек, в которых содержится текст
        int [] mas = new int[i+1];
        for (int n = 0; n <= i; n++) {
            mas[n] = n;
            Log.w("MY", "mas[n] = n РАВНО " + n);
            //далее в цикле находим саму вьюшку
            New_Item = findViewById(mas[n]);
            CheckBox Chek = findViewById(n+200);//и чекбокс
            if (Chek.isChecked()) {
                Text_Item = Text_Item + "[ v ] ";
            }
            else {
                Text_Item = Text_Item + "[    ] ";
            }
            //получаем из нее текст и через разделитель складываем в текстовую переменную
            if (New_Item != null) {
                Text_Item = Text_Item + New_Item.getText().toString() + "\n";
                Log.w("MY", Text_Item);
            }
        }

        //формируем текстовую переменную, которая служит для отправки текста в функции "Поделиться"
        textToSend = getResources().getString(R.string.list1) + ": " + Name_Note.getText().toString() + "\n\n" + Text_Item;
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, textToSend);

        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
        textToSend = null;
    }
    ////КНОПКА ИЗБРАННОЕ////
    @SuppressLint("UseCompatLoadingForDrawables")
    public void favButton() {
        if (favourites == 0) {
            menu.getItem(0).setIcon(getDrawable(R.drawable.baseline_star_white_48));
            favourites++ ;
        }
        else  {
            menu.getItem(0).setIcon(getDrawable(R.drawable.baseline_star_border_white_48));
            favourites--;
        }
    }

    //КНОПКА СОХРАНИТЬ В ФАЙЛ во внешнем накопителе (SD-карте)
    // createFile()
    // onActivityResult
    // writeInFile

    private void createFile() {
        String textName = Name_Note.getText().toString() + ".txt";
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TITLE, textName);

        startActivityForResult(intent, WRITE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        codeFileTextBody();
        String textBody = Text_Item;
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
            assert outputStream != null;
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outputStream));
            bw.write(text);
            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Toast.makeText(this, getString(R.string.t_save_note_5), Toast.LENGTH_LONG).show();
    }

    ////ИЗМЕНЕНИЕ ЦВЕТОВОЙ СХЕМЫ
    private void settings_themes() {

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

                setDarkTheme();//VOID
                colorDrawable1.setColor(Color.parseColor("#282828"));
                actionBar.setBackgroundDrawable(colorDrawable1);
                window.setStatusBarColor(ContextCompat.getColor(this,R.color.StatusBar_Dark));
                setTheme(R.style.AppThemeDark);
            }
        }
//иначе включаем темную тему
        else {
            setDarkTheme();//VOID
            colorDrawable1 = new ColorDrawable();
            colorDrawable1.setColor(Color.parseColor("#282828"));
            actionBar.setBackgroundDrawable(colorDrawable1);
            window.setStatusBarColor(ContextCompat.getColor(this,R.color.StatusBar_Dark));
        }
    }

    @Override
    protected void onResume() {
        settings_themes();
        super.onResume();

    }
    public void NameNoteListen () {
        Name_Note.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                return false;
            }
        });

    }
    //перемещение на главный экран (после сохранения, удаления и т.д.)
    public void onMainActivity() {
        Intent i = new Intent(CheckboxActivity.this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    //включение темной темы
    public void setDarkTheme() {
        colorDrawable1 = new ColorDrawable ();
        colorDrawable1.setColor(Color.parseColor("#282828"));
        actionBar.setBackgroundDrawable(colorDrawable1);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.StatusBar_Dark));
        //включаем темный стиль
        setTheme(R.style.AppThemeDark);
        //устанавливаем фон, т.к. стиль не меняет фон на View, а тут constr...layout почему то View
        ConstraintLayout listChekbox = findViewById(R.id.listCheckbox);
        listChekbox.setBackground(new ColorDrawable(ContextCompat.getColor(this, ThemeDark_fon)));
        //устанавливаем цвета шрифта во View, т.к. стиль их тоже не меняет
        editText.setTextColor(ContextCompat.getColor(this, ThemeDark_text_color));

        //устанавливаем цвет фона спиннера, т.к. стиль их тоже не меняет

        ColorDrawable dark_color = new ColorDrawable();
        dark_color.setColor(Color.parseColor("#303030"));
        spinner.setPopupBackgroundDrawable(dark_color);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER)
        {
            //Nothing
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
