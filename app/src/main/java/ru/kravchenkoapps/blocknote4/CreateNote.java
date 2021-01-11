package ru.kravchenkoapps.blocknote4;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.LoadAdError;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CreateNote extends AppCompatActivity {
    private TextView Name_Note, User_Text;
    private ImageView imageFolder;
    String Text_Name_Note, Text_User_Text;
    private ActionBar actionBar;
    String search_dir = "dir";
    private String selected_dir;
    String [] dirs;
    private SharedPreferences def_pref;
    private Window window; // переменная для изменения цвета статус-бара (там шже часы, связь и т.д.)
    ColorDrawable colorDrawable1;
    private Menu menu;
    private int favourites = 0;
    String user_folder;
    int dark_time = 0;//маркер темного времени суток, 0 - нет, 1 - да
    Boolean dark_switch;//переменная включения темной темы ночью
    String c_blue, c_purple, c_red, c_orange, c_green, c_grey, c_dark;
    private InterstitialAd mInterstitialAd;//для рекламы
    //AddsShow addsShow;
    String app_ad_test_block_id, app_ad_PROM_block_id;
    View createConst;
    String theme_color;
    int Edit_tag = 0;//признак того, что текст заметки редактировался
    int Ad_load_tag = 0;
    String size;//переменная, в которую будет помещаться выбор пользователя по размеру такста
    int Status_purchase = 0;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //подключаем настройки к переменной
        def_pref = PreferenceManager.getDefaultSharedPreferences(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_activity);

        //устанавливаем кнопку Home на actionbar
        if (getSupportActionBar() != null)
        {
            actionBar = getSupportActionBar(); //находим actionBar, если он есть
            actionBar.setDisplayHomeAsUpEnabled(true);//показываем в акшинбаре стрелочку выхода из него,то есть кнопка возврата домой
            actionBar.setTitle(getString(R.string.new_note));//устанавливаем в акшинбаре надпись
        }

        //код для подготовки к изменению цвета статус-бара
        window = this.getWindow();// без этого флага работать не будет
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);//без этого флага работать не будет
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);//без этого флага работать не будет
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorAccent));// finally change the color

        Name_Note = findViewById(R.id.name_note);
        User_Text = findViewById(R.id.user_text);
        imageFolder = findViewById(R.id.image_f_create);
        Intent fold = getIntent();//получаем входящий интент из MainAvtivity
        Status_purchase = fold.getIntExtra("show_add", 0);
        // Create the InterstitialAd
        app_ad_test_block_id = getString(R.string.app_ad_test_block_id);
        app_ad_PROM_block_id = getString(R.string.app_ad_prom_block_id);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                //Toast.makeText(CreateNote.this, "onAdLoaded()", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                @SuppressLint("DefaultLocale") String error =
                        String.format(
                                "domain: %s, code: %d, message: %s",
                                loadAdError.getDomain(), loadAdError.getCode(), loadAdError.getMessage());
                Log.w("MY", "onAdFailedToLoad() with error: " + error);

            }
            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
                IntentCreateMain();
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the interstitial ad is closed.
                IntentCreateMain();
                Log.w("MY", "onAdClosed()");
            }
        });

    //слушатели вводимого текста
        Name_Note.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                return false;
            }
        });
        User_Text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                return false;
            }
        });
        //отслеживаем изменение текста
        //необходимо для вывода openQuitDialog();
                Name_Note.addTextChangedListener(new TextWatcher() {
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
        User_Text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
        //при наборе текста в USER_TEXT - производим загрузку рекламы и устанавливаем тег в 1
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (Ad_load_tag == 0 && Status_purchase == 0) {//если реклама не загружена и не куплена полная версия, тогда загружаем рекламу
                mInterstitialAd.setAdUnitId(app_ad_PROM_block_id);//до выгрузки на Маркет необходимо использовать тестовый ID рекламы app_ad_test_block_id
                mInterstitialAd.loadAd(new AdRequest.Builder().build());

                Ad_load_tag = 1;
                Log.w("MY", "onTextChanged(CharSequence charSequence, int i, int i1, int i2)");
            }

            }

            @Override
            public void afterTextChanged(Editable editable) {
                Edit_tag = 1;//ставим тег изменения текста
            }
        });

        //НАСТРОЙКИ (текст, включение ссылок)
        init_text_size();

         //выпадающий список папок
        //берем из базы наименования папок
        AppDatabase database = App.getInstance().getDatabase();
        NoteDao noteDao = database.noteDao();
        List<String> dirs_list = noteDao.getDirs(search_dir);
        //добавляем общую папку на первое место в мапссиве
        dirs_list.add(0, getString(R.string.main_folder));
        //получаем интент с папкой, в которой находимся, далее устанавливаем эту папку в спиннер
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
        }
        dirs = dirs_list.toArray(new String[dirs_list.size()]);
        final Spinner spinner = findViewById(R.id.directory_1);
        // Создаем адаптер ArrayAdapter с помощью массива строк и стандартной разметки элемета spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.test_list_item, dirs);
        // Определяем разметку для использования при выборе элемента
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Применяем адаптер к элементу spinner
        spinner.setAdapter(adapter);
        //берем выбранное значение для записи в базу данных в поле directory
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (spinner.getSelectedItem() != null) {
                    selected_dir = spinner.getSelectedItem().toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ////ВХОДЯЩИЙ INTENT (TEXT) ИЗ ДРУГИХ ПРИЛОЖЕНИЙ
        Intent intent = getIntent();
        String input_action = intent.getAction();
        String input_type = intent.getType();

        if (Intent.ACTION_SEND.equals(input_action) && input_type != null) {
            if ("text/plain".equals(input_type)) {
                handleSendText(intent);
            }
        }
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
        //получаем цвета (необходимо для корректной работы переключения языка приложения)
        c_blue = getString(R.string.c2_blue);
        c_purple = getString(R.string.c2_purple);
        c_red = getString(R.string.c2_red);
        c_orange = getString(R.string.c2_orange);
        c_green = getString(R.string.c2_green);
        c_grey = getString(R.string.c2_grey);
        c_dark = getString(R.string.c2_dark);

        //addsShow = new AddsShow();
        //addsShow.AdCreate(this);
        createConst = (View) findViewById(R.id.create_const_layout);

    }//конец onCreate

    void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            User_Text.setText(sharedText);
        }
    }

    //добавляем кнопки справа сохранить,удалить и т.д. предварительно кодим menu/create_menu.xml
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create_menu, menu);
        this.menu = menu;//нужно для звездочки
        return true; //super.onCreateOptionsMenu(menu)
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
    //КНОПКА СОХРАНИТЬ
        if(item.getItemId() == R.id.save_click){
            AppDatabase database = App.getInstance().getDatabase();
            Text_User_Text = User_Text.getText().toString();
            Text_Name_Note = Name_Note.getText().toString();

            if (!Text_User_Text.equals("")||!Text_Name_Note.equals("")) {
            if (Name_Note.getText().toString().equals(""))
            {
                //Text_Name_Note = getResources().getString(R.string.new_note);
            int limit = 20;
            if (!Text_User_Text.equals("")) {//если текст самой заметки заполнен, то берем первые 30 символов для названия заметки
            String subStr = Text_User_Text.length() > limit ? Text_User_Text.substring(0, limit) : Text_User_Text;
            Text_Name_Note = subStr + getString(R.string.points);
                }
            }
            //else {
            //    Text_Name_Note = Name_Note.getText().toString();
            //}

            NoteDao noteDao = database.noteDao();
            Note note = new Note();
            note.namenote = Text_Name_Note.trim();//удаление пробелов в строке перед и после текста
            note.usertext = Text_User_Text;
            note.on_delete = 0;
            note.directory = selected_dir;
            note.favourites = favourites;//устанавливаем признак Избранное
            Log.w("MY", String.valueOf(favourites));

            long time_long = Calendar.getInstance().getTimeInMillis();//берем текущее время в миллисекундах
            Log.w("MY", String.valueOf(time_long));

            note.time = time_long;
            noteDao.insert(note);

                //////////////////////////////////////
                ////////////РЕКЛАМА//////////////////
                ////////////////////////////////////

                AddShow();//показ рекламы из этого класса - ПОКА ЗАКОМЕНТИЛ, ЧТОБЫ НЕ МЕШАЛА
                //далее листенером читаем действия пользователя и переходим на MainAvtivity
                Toast.makeText(this, getString(R.string.t_save_note_4), Toast.LENGTH_SHORT).show();
                //IntentCreateMain();
            }
            else {//иначе заметка абсолютно пуста
            Toast.makeText(this, getString(R.string.t_save_note_1), Toast.LENGTH_LONG).show();
            }//конец Иначе заметка абсолютно пуста
        }//конец кнопки сохранить
        if(item.getItemId() == R.id.favourites_click) {
            if (favourites == 0) {
                menu.getItem(0).setIcon(getDrawable(R.drawable.baseline_star_white_48));
                favourites++ ;
            }
        else  {
            menu.getItem(0).setIcon(getDrawable(R.drawable.baseline_star_border_white_48));
            favourites--;
            }
        }
        return true;
    }
    ////ИЗМЕНЕНИЕ ЦВЕТОВОЙ СХЕМЫ
    private void settings_themes() {
        theme_color = def_pref.getString("key_theme_color", getString(R.string.c2_blue));//подвязываем ключ из preference_screen.xml к def_pref и size
        dark_switch = def_pref.getBoolean("switch_preference_dark", false);
        if (theme_color != null & !dark_switch ||theme_color != null & dark_switch & dark_time == 0) {
            Log.w("MY", "Установка светлой темы");
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
                setTheme(R.style.AppThemeDark);//устанавливаем стиль
                //устанавливаем фон, т.к. стиль не меняет фон на View, а тут constr...layout почему то View
                createConst.setBackgroundColor(ContextCompat.getColor(this, R.color.ThemeDark_fon));
                //устанавливаем цвета шрифта, т.к. стиль их тоже не меняет
                Name_Note.setTextColor(ContextCompat.getColor(this, R.color.ThemeDark_text_color));
                Name_Note.setHintTextColor(ContextCompat.getColor(this, R.color.ThemeDark_hint_color));
                User_Text.setTextColor(ContextCompat.getColor(this, R.color.ThemeDark_text_color));
                User_Text.setHintTextColor(ContextCompat.getColor(this, R.color.ThemeDark_hint_color));
                imageFolder.setImageResource(R.drawable.baseline_folder_white_48);
                //устанавливаем цвет фона спиннера, т.к. стиль их тоже не меняет
                Spinner spinner_dark = findViewById(R.id.directory_1);
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
            setTheme(R.style.AppThemeDark);//устанавливаем стиль
            //устанавливаем фон, т.к. стиль не меняет фон на View, а тут constr...layout почему то View
            createConst.setBackgroundColor(ContextCompat.getColor(this, R.color.ThemeDark_fon));
            //устанавливаем цвета шрифта, т.к. стиль их тоже не меняет
            Name_Note.setTextColor(ContextCompat.getColor(this, R.color.ThemeDark_text_color));
            Name_Note.setHintTextColor(ContextCompat.getColor(this, R.color.ThemeDark_hint_color));
            User_Text.setTextColor(ContextCompat.getColor(this, R.color.ThemeDark_text_color));
            User_Text.setHintTextColor(ContextCompat.getColor(this, R.color.ThemeDark_hint_color));
            imageFolder.setImageResource(R.drawable.baseline_folder_white_48);
            //устанавливаем цвет фона спиннера, т.к. стиль их тоже не меняет
            Spinner spinner_dark = findViewById(R.id.directory_1);
            ColorDrawable dark_color = new ColorDrawable();
            dark_color.setColor(Color.parseColor("#303030"));
            spinner_dark.setPopupBackgroundDrawable(dark_color);
        }
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
                    Name_Note.setTextSize(24);//устанавливаем размер текста заголовка
                    User_Text.setTextSize(24);//устанавливаем размер текста статьи
                    break;//при совпадении первого кейса прерываем switch и т.д.
                case "Middle":
                    Name_Note.setTextSize(18);//устанавливаем размер текста заголовка
                    User_Text.setTextSize(18);//устанавливаем размер текста статьи
                    break;
                case "Small":
                    Name_Note.setTextSize(14);//устанавливаем размер текста заголовка
                    User_Text.setTextSize(14);//устанавливаем размер текста статьи
                    break;

            }
        }

    }//конец изменения размера текста

    @Override
    protected void onResume() {
        settings_themes();
        super.onResume();

    }

    public void AddShow() {
        if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            Log.w("MY", "AddShow () The interstitial wasn't loaded yet.");
            IntentCreateMain();
        }
    }

    public void IntentCreateMain() {
        Intent i = new Intent(CreateNote.this, MainActivity.class);
        //i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // если это вернуть, то переход по рекламе не работает
        startActivity(i);
    }
    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        if (Edit_tag ==1) {
            openQuitDialog();
        }
        else {
            IntentCreateMain();
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
                IntentCreateMain();

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


}
