package ru.kravchenkoapps.blocknote4;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements
        SearchView.OnQueryTextListener, View.OnLongClickListener, AdapterView.OnItemLongClickListener {

    TextView Time3;
    private DrawerLayout drawer;
    private Toolbar toolbar;
    SearchView SearchView;
    private String query;
    private RecyclerView recyclerView;
    private String search_dir = "dir";
    private int on_del;
    String NewDirectoryForSave;
    String TimeNewDirectory;
    ListView ListFirst;
    ListView ListSettings;
    private String [] dirs;
    private String select_dir;
    String [] FoldersArray;
    String [] FoldersArray3;
    String [] AllFoldersArray;
    TextView CardFolder1;
    private SomeDataRecyclerAdapter noteAdapter;
    private String TextDirName;
    private String DefaultUserText = "dir";
    private  int dirs_list_final;
    private int stop_tag=0;
    Menu menu;
    SharedPreferences def_pref;
    private ActionBar actionBar;
    LinearLayout main_linear_layout;
    ColorDrawable colorDrawable1;
    private String FolderSelected = "";
    private SharedPreferences sPref;
    private String SORT_NOTES;
    private List<Name> Selected_names;
    ConstraintLayout MainConst;
    private Window window; // переменная для изменения цвета статус-бара (там шже часы, связь и т.д.)
    public View CardView;
    public String MessageForAlert;
    public AppDatabase database_alert;
    public NoteDao noteDao_alert;
    public String TimeStr_alert;
    public Note note_alert;
    public String MessageDelBasket;
    int sizeArray_a, sizeArray_b;
    List<Long> time_list;
    List<Long> time_list2;
    public String fold_for_spinner;
    int dark_time = 0;//маркер темного времени суток, 0 - нет, 1 - да
    Boolean dark_switch;//переменная включения темной темы ночью
    FloatingActionButton AddMainFab;
    String sort_var = "";
    //нужно для установки значков в DrawerLayout
    static String all_notes_s, favourites_s, public_folder_s, basket_s, new_folder_s, settings_s, about_s;
    //цвета темы
    String c_blue, c_purple, c_red, c_orange, c_green, c_grey, c_dark;//
    EditText input;
    public int dark_set = 0;//признак факта установленной темной темы, нужен для белых значков
    public String theme_color, st_folder;
    TextView tv_folder;
    int TAG_IF_DEL_FOLDER = 0; //тег удаления папки
    int count;//счетчик для объединения массов в DrawerLayout

    private BillingClient mBillingClient;
    private Map<String, SkuDetails> mSkuDetailsMap = new HashMap<>();
    private String mSkuId = "notepad_full_version";
    SharedPreferences Purchase_Pref;
    final String TAG_PURCHASE = "TAG_PURCHASE";
    int Purchase_OK = 0;

    AppDatabase database_GB;
    NoteDao noteDao_GB;
    //переменные в AlertDialog создания ЗАметки/Списка
    View dialogView;
    TextView ItemZametka, ItemSpisok;
    ImageView viewZametka, viewSpisok;
    List<String> dirs_list_same;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        def_pref = PreferenceManager.getDefaultSharedPreferences(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        @SuppressLint("UseCompatLoadingForDrawables") Drawable d = getResources().getDrawable(R.drawable.baseline_sort_white_24);
        toolbar.setOverflowIcon(d);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        main_linear_layout = findViewById(R.id.LinearLayout);

        HowTimeNow();//проверяем, сейчас темное время суток или светлое

        //определяем, темная тема установлена или нет. Нужно для белых значков
        theme_color = def_pref.getString("key_theme_color", getString(R.string.c2_blue));//подвязываем ключ из preference_screen.xml к def_pref и size
        dark_switch = def_pref.getBoolean("switch_preference_dark", false);
        if (theme_color.equals(getString(R.string.c2_dark))) {
           dark_set = 1;
        }
        if (dark_switch & dark_time == 1) {
           dark_set = 1;
        }

        //БИЛЛИНГ

       mBillingClient = BillingClient.newBuilder(this).setListener(new PurchasesUpdatedListener() {
            @Override
            public void onPurchasesUpdated(int responseCode, @Nullable List<Purchase> purchases) {
                if (responseCode == BillingClient.BillingResponse.OK && purchases != null) {
                    //сюда мы попадем когда будет осуществлена покупка
                    payComplete();//предоставляем доступ к товару
                }
            }

        }).build();
        mBillingClient.startConnection(new BillingClientStateListener() {

            @Override
            public void onBillingServiceDisconnected() {
                //сюда мы попадем если что-то пойдет не так
            }
            @Override
            public void onBillingSetupFinished(@BillingClient.BillingResponse int billingResponseCode) {
                Log.w("MY", "onBillingSetupFinished");
                if (billingResponseCode == BillingClient.BillingResponse.OK) {
                    //здесь мы можем запросить информацию о товарах и покупках
                    Log.w("MY", "if (billingResponseCode == BillingClient.BillingResponse.OK)");
                    querySkuDetails(); //запрос о товарах

                    List<Purchase> purchasesList = queryPurchases(); //запрос о покупках
                    //если товар уже куплен, предоставить его пользователю
                    for (int i = 0; i < purchasesList.size(); i++) {
                        String purchaseId = purchasesList.get(i).getSku();
                        Log.w("MY", "Проверяем purchaseId = " + purchaseId);
                        if(TextUtils.equals(mSkuId, purchaseId)) {
                            payComplete();
                        }

                    }
                }
            }
        });

        //код для подготовки к изменению цвета статус-бара
        window = this.getWindow();// без этого флага работать не будет
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);//без этого флага работать не будет
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);//без этого флага работать не будет
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorAccent));// finally change the color

        fold_for_spinner = getString(R.string.main_folder);//для передачи в спиннер при создании заметки
        // подключаем поисковую строку
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
            doMySearch (query);
        }
        //находим DrawerLayout
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //наш recyclerView
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        AppDatabase database = App.getInstance().getDatabase();
        final NoteDao noteDao = database.noteDao();
        on_del = 1;

        /////////////СОРТИРОВКА /////////////////////
        final List<Name> names = noteDao.getNameNote(search_dir, on_del);
        //запускаем адаптер
        noteAdapter = new SomeDataRecyclerAdapter(names, dark_set);
        recyclerView.setAdapter(noteAdapter);
        CollectionSorted(names, noteAdapter);//прикручиваем сортировку

        //скрытие FAB при проктутке списка заметок
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 || dy < 0 && AddMainFab.isShown())
                    AddMainFab.hide();//скрываем, когда крутится
            }
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE)
                    AddMainFab.show();//показываем, когда остановился
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        //добавляем в draver layout папку, если она создана
        reciveIntent();

        ////////////////////////////////////
        ///////DRAWER LAYOUT////////////////
        ///////////////////////////////////
        all_notes_s = getString(R.string.all_notes);
        favourites_s = getString(R.string.favourites_folder);
        public_folder_s = getString(R.string.main_folder);
        basket_s = getString(R.string.basket);
        new_folder_s = getString(R.string.new_folder);
        settings_s = getString(R.string.settings);
        about_s = getString(R.string.about);

        ListFirst = findViewById(R.id.listFirst);
        //массив 1 части кнопок
        FoldersArray = getResources().getStringArray(R.array.folders);
        //создаем массив 2 части кнопок - пользовательских папок
        List<String> dirs_list = noteDao.getDirs3(search_dir);
        dirs = dirs_list.toArray(new String[dirs_list.size()]);
        //массив 3 части кнопок
        FoldersArray3 = getResources().getStringArray(R.array.folders3);

        //объединяем 3 массива
        ArrayForDrawerLayout(FoldersArray, dirs, FoldersArray3);

        //теперь создаем и инициализируем адаптер значениями объединенного массива
       DrawerListAdapter adapterAllList = new DrawerListAdapter(this, AllFoldersArray, dark_set);
       ListFirst.setAdapter(adapterAllList);

        //ДОЛГОЕ НАЖАТИЕ НА ЭЛЕМЕНТЕ ЛИСТА DRAWER LAYOUT (ОЧИЩЕНИЕ ЗАМЕТОК, ИЗМЕНЕНИЕ ПОЛЬЗОВАТЕЛЬСКИХ ПАПОК)
        ListFirst.setLongClickable(true);
        ListFirst.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, final View view, int i, long l) {
                tv_folder = view.findViewById(R.id.item_folder);
                st_folder = tv_folder.getText().toString();
                //ДОЛГОЕ НАЖАТИЕ НА ВСЕ ЗАМЕТКИ
                if (st_folder.equals(getString(R.string.all_notes))) {
                    int var_folder = 0;
                    DeleteAllMainDialog(tv_folder, var_folder, st_folder);
                }
                //ДОЛГОЕ НАЖАТИЕ НА ИЗБРАННОЕ
                else if (st_folder.equals(getString(R.string.favourites_folder))) {
                    return true;
                }
                //ДОЛГОЕ НАЖАТИЕ НА ОБЩАЯ ПАПКА
                else if (st_folder.equals(getString(R.string.main_folder))) {
                    int var_folder = 1;
                    DeleteAllMainDialog(tv_folder, var_folder, st_folder);
                }
                //ДОЛГОЕ НАЖАТИЕ НА КОРЗИНА
                else if (st_folder.equals(getString(R.string.basket))) {
                  on_del = 1;
                  BasketLongClick (tv_folder, on_del);
                 }
                //ДОЛГОЕ НАЖАТИЕ НА НОВАЯ ПАПКА
                else if (st_folder.equals(getString(R.string.new_folder))) {
                    return true;
                }
                //ДОЛГОЕ НАЖАТИЕ НА НАСТРОЙКИ
                else if (st_folder.equals(getString(R.string.settings))) {
                    return true;
                }
                //ДОЛГОЕ НАЖАТИЕ НА О ПРИЛОЖЕНИИ
                else if (st_folder.equals(about_s)) {
                    return true;
                }
                //ДОЛГОЕ НАЖАТИЕ НА ПОЛЬЗОВАТЕЛЬСКОЙ ПАПКЕ
                else {
                //получаем название папки, которую редактируем
                final String this_dir = st_folder;
                select_dir = st_folder;//помещаем его в переменную для запроса в базу данных
                on_del = 1;//здесь это нужно, чтобы далее выбрать не помещенные в Корзину заметки
                final String begin_title = getString(R.string.edit_folder);//получаем дефолтное начало шапки для AlertDialog.Builder
                final String[] actions = {getString(R.string.edit_user_folder1), getString(R.string.edit_user_folder2), getString(R.string.edit_user_folder3)};
                final AlertDialog.Builder dialog_list = new AlertDialog.Builder(MainActivity.this);
                dialog_list.setTitle(begin_title + " " + st_folder);//устанавливаем шапку
                dialog_list.setCancelable(true);//позволяет закрыть диалоговое окно при нажатии кнопки назад
                dialog_list.setItems(actions, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                //ПИШЕМ изменение наименования папки
                                AppDatabase database_0 = App.getInstance().getDatabase();
                                final NoteDao noteDao_0 = database_0.noteDao();
                                final Note note_0 = noteDao_0.getThisDir(this_dir);//получаем эту конкретную папку
                                input();
                                input.setText(note_0.directory);
                                input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                                    @Override
                                    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                                        return false;
                                    }
                                });
                                input.requestFocus();
                                input.setFocusable(true);
                                final AlertDialog.Builder new_folder = new AlertDialog.Builder(MainActivity.this);
                                new_folder.setTitle(getString(R.string.edit_user_folder6))
                                        .setView(input)
                                        .setCancelable(false);
                                if (dark_set == 0) {
                                    new_folder.setIcon(R.drawable.baseline_folder_black_48);
                                }
                                else {
                                    new_folder.setIcon(R.drawable.baseline_folder_white_48);
                                }
                                new_folder.setPositiveButton(getString(R.string.edit_user_folder7), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                TextDirName = input.getText().toString();

                                                AppDatabase database_3 = App.getInstance().getDatabase();//получаем базу
                                                NoteDao noteDao_3 = database_3.noteDao();//накатываем на базу интерфейс

                                                List<Long> list_note_for_rename = noteDao_0.noteForDelete(select_dir, search_dir, on_del);
                                                final Long[] list_note_for_rename_2 = list_note_for_rename.toArray(new Long[list_note_for_rename.size()]);
                                                int sizeArray = list_note_for_rename.size();//размер ArrayList
                                                //int startIndex - стартовый индекс единицы массива
                                                for (int startIndex = 0; startIndex < sizeArray; startIndex++) {
                                                    Note note3 = noteDao_3.getTime2(list_note_for_rename_2[startIndex]);
                                                    note3.directory = TextDirName;
                                                    noteDao_0.update(note3);
                                                }

                                                note_0.directory = TextDirName;
                                                noteDao.update(note_0);//записываем в базу
                                                dialog.dismiss();
                                                List<String> dirs_list = noteDao.getDirs3(search_dir);
                                                dirs = dirs_list.toArray(new String[dirs_list.size()]);

                                                final DrawerListAdapter adapterList2 = new DrawerListAdapter(MainActivity.this, dirs, dark_set);
                                                ListFirst.setAdapter(adapterList2);
                                                //после изменения наименования папки перезапускаем активити
                                                TAG_IF_DEL_FOLDER = 1;//чтобы в onDestroy не убивать процесс
                                                Intent intent = getIntent();
                                                finish();
                                                startActivity(intent);

                                                Toast.makeText(MainActivity.this, getString(R.string.t_save_folder), Toast.LENGTH_SHORT).show();

                                            }
                                        })
                                        .setNegativeButton(getString(R.string.button_negative), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                new_folder.show();

                                break;


                            case 1://////////ОЧИСТКА ПОЛЬЗОВАТЕЛЬСКОЙ ПАПКИ
                                MessageDelBasket = getString(R.string.alert_delete_5_1) + '"' + st_folder + '"' +
                                        getString(R.string.alert_delete_5_2) + getString(R.string.alert_delete_5_3);
                                final AlertDialog.Builder dialog_del_basket5 = new AlertDialog.Builder(MainActivity.this);
                                dialog_del_basket5.setTitle(getString(R.string.edit_user_folder4))
                                        .setMessage(MessageDelBasket);
                                if (dark_set == 0) {
                                    dialog_del_basket5.setIcon(R.drawable.baseline_delete_black_48);
                                }
                                else {
                                    dialog_del_basket5.setIcon(R.drawable.baseline_delete_white_48);
                                }
                                dialog_del_basket5.setPositiveButton(getString(R.string.button_positive), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                AppDatabase database_4 = App.getInstance().getDatabase();
                                                NoteDao noteDao_4 = database_4.noteDao();
                                                List<Long> list_note_for_del_3 = noteDao_4.noteForDelete(select_dir, search_dir, on_del);
                                                final Long[] list_note_for_del_4 = list_note_for_del_3.toArray(new Long[list_note_for_del_3.size()]);
                                                int sizeArray1 = list_note_for_del_3.size();//размер ArrayList
                                                //int startIndex - стартовый индекс единицы массива
                                                for (int startIndex = 0; startIndex < sizeArray1; startIndex++) {
                                                    Note note3 = noteDao_4.getTime2(list_note_for_del_4[startIndex]);
                                                    note3.on_delete = 1;//помещаем заметку в корзину
                                                    noteDao_4.update(note3);//обновляем заметку
                                                }
                                                OnCardDrawerClick(tv_folder);//заходим в ЭТУ же папку и видим, что она пустая
                                                Toast.makeText(MainActivity.this, getString(R.string.t_basket1), Toast.LENGTH_SHORT).show();
                                            }//конец onClick
                                        })
                                        .setNegativeButton(getString(R.string.button_negative), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.cancel();
                                            }
                                        });
                                dialog_del_basket5.show();
                                break;
                            case 2://////////УДАЛЕНИЕ ПАПКИ
                                MessageDelBasket = getString(R.string.alert_delete_6_1) + '"' + st_folder + '"' +
                                        getString(R.string.alert_delete_6_2);
                                final AlertDialog.Builder dialog_del_basket6 = new AlertDialog.Builder(MainActivity.this);
                                dialog_del_basket6.setTitle(getString(R.string.edit_user_folder5))
                                        .setMessage(MessageDelBasket);
                                if (dark_set == 0) {
                                    dialog_del_basket6.setIcon(R.drawable.baseline_delete_black_48);
                                }
                                else {
                                    dialog_del_basket6.setIcon(R.drawable.baseline_delete_white_48);
                                }
                                dialog_del_basket6.setPositiveButton(getString(R.string.button_positive), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                AppDatabase database_6 = App.getInstance().getDatabase();
                                                final NoteDao noteDao_6 = database_6.noteDao();
                                                //получаем список заметок в выбранной папке и удаляем их
                                                List<Long> list_note_for_del_1 = noteDao_6.noteForDelete(select_dir, search_dir, on_del);
                                                final Long[] list_note_for_del_2 = list_note_for_del_1.toArray(new Long[list_note_for_del_1.size()]);
                                                int sizeArray = list_note_for_del_1.size();//размер ArrayList
                                                //int startIndex - стартовый индекс единицы массива
                                                for (int startIndex = 0; startIndex < sizeArray; startIndex++) {
                                                    Note note2 = noteDao.getTime2(list_note_for_del_2[startIndex]);
                                                    noteDao.delete(note2);
                                                }
                                                //удаляем саму папку
                                                Note note = noteDao.getThisDir(this_dir);//получаем эту конкретную папку
                                                noteDao.deleteDir(note);//удаляем эту конкретную папку
                                                Log.w("MY", "noteDao.deleteDir(note)");
                                                //обновляем 2 лист
                                                List<String> dirs_list = noteDao.getDirs3(search_dir);
                                                dirs = dirs_list.toArray(new String[dirs_list.size()]);
                                                final DrawerListAdapter adapterList2 = new DrawerListAdapter(MainActivity.this, dirs, dark_set);
                                                ListFirst.setAdapter(adapterList2);
                                                //перезапускаем активити
                                                TAG_IF_DEL_FOLDER = 1;//чтобы в onDestroy не убивать процесс
                                                Intent intent = getIntent();
                                                finish();
                                                startActivity(intent);

                                            }//конец onClick
                                        })
                                        .setNegativeButton(getString(R.string.button_negative), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.cancel();
                                            }
                                        });
                                dialog_del_basket6.show();
                                break;
                        }
                    }
                });
                dialog_list.show();
                return true;
            }//конец долгое нажатие на пользовательскую папку

                return true;
            }
        });

    //FAB
        MainConst = findViewById(R.id.main_const);
        AddMainFab = findViewById(R.id.add_main_fab);

        AddMainFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String title_fab = getString(R.string.create);//устанавливаем шапку для AlertDialog.Builder
                final AlertDialog.Builder dialog_fab = new AlertDialog.Builder(MainActivity.this);
                LayoutInflater inflater_fab = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                assert inflater_fab != null;

                if (dark_set == 0) {
                    dialogView = inflater_fab.inflate(R.layout.dialog_fab, null);
                }
                else {
                    dialogView = inflater_fab.inflate(R.layout.dialog_fab_dark, null);
                }
                dialog_fab.setTitle(title_fab);//устанавливаем шапку

                dialog_fab.setView(dialogView);//R.layout.dialog_fab
                dialog_fab.setCancelable(true);//позволяет закрыть диалоговое окно при нажатии кнопки назад

                ///////////ОБРАБОТЧИКИ НАЖАТИЙ В ДИАЛОГОВОМ ОКНЕ СОЗДАНИЯ ЗАМЕТКИ/СПИСКА

                 //= dialogView.findViewById(R.id.zam);
                if (dark_set == 0) {
                    ItemZametka = dialogView.findViewById(R.id.zam);
                }
                else {
                    ItemZametka = dialogView.findViewById(R.id.zam_dark);
                }
                ItemZametka.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Purchase_Pref = getPreferences(MODE_PRIVATE);
                        int Status_Purchase = Purchase_Pref.getInt(TAG_PURCHASE, 0);
                        Intent i = new Intent(MainActivity.this, CreateNote.class);
                        i.putExtra("user_folder", fold_for_spinner);//передаем в интенте папку, в которой находимся
                        i.putExtra("show_add", Status_Purchase);
                        startActivity(i);
                        Log.w("MY", "fold_for_spinner = " + fold_for_spinner);
                    }
                });

                if (dark_set == 0) {
                    ItemSpisok = dialogView.findViewById(R.id.spis);
                }
                else {
                    ItemSpisok = dialogView.findViewById(R.id.spis_dark);
                }
                ItemSpisok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(MainActivity.this, CheckboxActivity.class);
                        i.putExtra("user_folder", fold_for_spinner);//передаем в интенте папку, в которой находимся

                        startActivity(i);
                        Log.w("MY", "fold_for_spinner = " + fold_for_spinner);
                    }
                });

                if (dark_set == 0) {
                    viewZametka = dialogView.findViewById(R.id.imageZam);
                }
                else {
                    viewZametka = dialogView.findViewById(R.id.imageZam_dark);
                }
                viewZametka.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Purchase_Pref = getPreferences(MODE_PRIVATE);
                        int Status_Purchase = Purchase_Pref.getInt(TAG_PURCHASE, 0);
                        Intent i = new Intent(MainActivity.this, CreateNote.class);
                        i.putExtra("user_folder", fold_for_spinner);//передаем в интенте папку, в которой находимся
                        i.putExtra("show_add", Status_Purchase);
                        startActivity(i);
                        Log.w("MY", "fold_for_spinner = " + fold_for_spinner);
                    }
                });

                 //= dialogView.findViewById(R.id.imageSpis);
                if (dark_set == 0) {
                    viewSpisok = dialogView.findViewById(R.id.imageSpis);
                }
                else {
                    viewSpisok = dialogView.findViewById(R.id.imageSpis_dark);
                }
                viewSpisok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(MainActivity.this, CheckboxActivity.class);
                        i.putExtra("user_folder", fold_for_spinner);//передаем в интенте папку, в которой находимся

                        startActivity(i);
                        Log.w("MY", "fold_for_spinner = " + fold_for_spinner);
                    }
                });
                dialog_fab.show();//показываем диалог
            }
        });

    //КЛИКИ ПО CARD VIEW - заметке на экране;
    recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
        @Override
        //ОДИНОЧНОЕ НАЖАТИЕ//ОТКРЫТИЕ ЗАМЕТКИ
        public void onItemClick(View view, int position) {
            Time3 = view.findViewById(R.id.time_3);//находим на экране переменную со временем
            String GetTime = (String) Time3.getText();//получаем в переменную время
            TextView ListTag = view.findViewById(R.id.textListTag);//находим на экране переменную с тэгом Список
            String TextListTag = (String) ListTag.getText();
            //получаем время и дату, переводим в миллисекунды и передаем в активити отображения заметки
            @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
            Date date = new Date();
            try {
                date = df.parse(GetTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            assert date != null;
            long GetLongTime = (date.getTime()) / 1000;
            //если был нажат список, то открываем активити со списком
            if (TextListTag.equals(getString(R.string.list1))) {
                Intent list = new Intent(MainActivity.this, CheckboxActivity.class);
                list.putExtra("time", GetLongTime);
                startActivity(list);
            }
            else {//в противном случае открываем обычную заметку
                Intent i = new Intent(MainActivity.this, TestContent.class);
                i.putExtra("time", GetLongTime);
                startActivity(i);
            }
        }
        //ДОЛГОЕ НАЖАТИЕ на CARD VIEW (на ЗАМЕТКУ) - УДАЛЕНИЕ
        @Override
        public void onItemLongClick(View view, int position) {

            Time3 = view.findViewById(R.id.time_3);//находим на экране переменную со временем
            String GetTime = (String) Time3.getText();//получаем в переменную время
            @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
            Date date = new Date();
            try {
                date = df.parse(GetTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            assert date != null;
            final long GetLongTime = (date.getTime()) / 1000;

            TextView NameNote3 = view.findViewById(R.id.name_note_3);
            String NameNoteAlert = (String) NameNote3.getText();
            //переводим время в понятную величину для отправки запроса в базу данных
             TimeStr_alert = String.valueOf(GetLongTime) + "%";
             //получаем базу
             database_alert = App.getInstance().getDatabase();
             noteDao_alert = database_alert.noteDao();
            //по времени заметки получаем все данные объекта Note
             note_alert = noteDao.getTime1(TimeStr_alert);
             if (note_alert.on_delete == 0) {
                 MessageForAlert = getString(R.string.alert_delete_1) + " " + getString(R.string.alert_delete_2);
             }
             if (note_alert.on_delete == 1) {
                 MessageForAlert = getString(R.string.alert_delete_1) + " " + getString(R.string.alert_delete_3);
             }

            final AlertDialog.Builder dialog_del_card_view = new AlertDialog.Builder(MainActivity.this);

            dialog_del_card_view.setTitle(getString(R.string.alert_del_name) + '"' + NameNoteAlert + '"')
                    .setMessage(MessageForAlert);
            if (dark_set == 0) {
                dialog_del_card_view.setIcon(R.drawable.baseline_delete_black_48);
            }
            else {
                dialog_del_card_view.setIcon(R.drawable.baseline_delete_white_48);
            }

            dialog_del_card_view.setPositiveButton(getString(R.string.button_positive), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //если запись не в корзине
                            if (note_alert.on_delete == 0) {
                                note_alert.on_delete = 1; // помещаем ее в корзину
                                noteDao_alert.update(note_alert); //обновляем запись
                                //перемещаемся на главный экран
                                Toast.makeText(MainActivity.this, getString(R.string.t_basket3), Toast.LENGTH_SHORT).show();
                                //перезапускаем адаптер
                                AppDatabase database = App.getInstance().getDatabase();
                                final NoteDao noteDao = database.noteDao();
                                on_del = 1;//здесь это нужно, чтобы далее выбрать не помещенные в Корзину заметки
                                //в зависимости от нахождения а папке обновляем адаптер
                                //если была открыта ВСЕ ЗАМЕТКИ
                                if (FolderSelected.equals(getString(R.string.all_notes))||FolderSelected.equals("")) {
                                    List<Name> names = noteDao.getNameNote(search_dir, on_del);
                                    Selected_names = names;
                                    noteAdapter = new SomeDataRecyclerAdapter(names, dark_set);
                                    recyclerView.setAdapter(noteAdapter);
                                    CollectionSorted(names, noteAdapter);//прикручиваем сортировку
                                    toolbar.setTitle(getString(R.string.all_notes));
                                }
                                //если была открыта ОБЩАЯ ПАПКА
                                if (FolderSelected.equals(getString(R.string.main_folder))) {
                                select_dir = FolderSelected;
                                List<Name> names_main_folder = noteDao.openDir(select_dir, search_dir, on_del);
                                SomeDataRecyclerAdapter noteAdapterMain = new SomeDataRecyclerAdapter(names_main_folder, dark_set);
                                recyclerView.setAdapter(noteAdapterMain);
                                CollectionSorted(names_main_folder, noteAdapterMain);//прикручиваем сортировку
                                toolbar.setTitle(getString(R.string.main_folder));
                                drawer.closeDrawer(GravityCompat.START);
                                select_dir = null;
                                }
                                //если была открыта ИЗБРАННОЕ
                                if (FolderSelected.equals(getString(R.string.favourites_folder))) {
                                on_del = 1;
                                List<Name> names = noteDao.getFavourites(search_dir, on_del);
                                Log.w("MY", String.valueOf(names));
                                Selected_names = names;
                                noteAdapter = new SomeDataRecyclerAdapter(names, dark_set);
                                recyclerView.setAdapter(noteAdapter);
                                //noteAdapter.updateData(names);
                                CollectionSorted(names, noteAdapter);//прикручиваем сортировку
                                toolbar.setTitle(getString(R.string.favourites_folder));
                                }
                                //если была открыта ПОЛЬЗОВАТЕЛЬСКАЯ ПАПКА
                                if (FolderSelected.equals(select_dir)) {
                                    List<Name> namesSelectDir = noteDao.openDir(select_dir, search_dir, on_del);
                                    SomeDataRecyclerAdapter noteAdapterSDir = new SomeDataRecyclerAdapter(namesSelectDir, dark_set);
                                    recyclerView.setAdapter(noteAdapterSDir);
                                    CollectionSorted(namesSelectDir, noteAdapterSDir);//прикручиваем сортировку
                                    toolbar.setTitle(select_dir);//отображаем название открытой папки
                                }
                            }
                            //если запись в корзине
                            else if (note_alert.on_delete == 1) {
                                //удаляем заметку из базы Room
                                noteDao_alert.delete(note_alert);
                                Toast.makeText(MainActivity.this, getString(R.string.t_basket4), Toast.LENGTH_SHORT).show();
                                //перезапускаем адаптер
                                FolderSelected = getString(R.string.basket);
                                AppDatabase database1 = App.getInstance().getDatabase();
                                final NoteDao noteDao1 = database1.noteDao();
                                on_del = 0;
                                List<Name> names = noteDao1.getNameNote(search_dir, on_del);
                                noteAdapter = new SomeDataRecyclerAdapter(names, dark_set);
                                recyclerView.setAdapter(noteAdapter);
                                CollectionSorted(names, noteAdapter);//прикручиваем сортировку
                                on_del = 1;
                                toolbar.setTitle(getString(R.string.basket));

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

        }//конец LongClick
    }));

        //получаем цвета (необходимо для корректной работы переключения языка приложения)
        c_blue = getString(R.string.c2_blue);
        c_purple = getString(R.string.c2_purple);
        c_red = getString(R.string.c2_red);
        c_orange = getString(R.string.c2_orange);
        c_green = getString(R.string.c2_green);
        c_grey = getString(R.string.c2_grey);
        c_dark = getString(R.string.c2_dark);
       //тестово
        //launchBilling("notepad_full_version");

        //Billing.getInstance().launchBilling(mSkuId, this);

    }//конец onCreate

    //ПОКУПКА
    public void launchBilling(String skuId) {
        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(mSkuDetailsMap.get(skuId))
                .build();
        mBillingClient.launchBillingFlow(this, billingFlowParams);
    }

    public void doMySearch (String query) {
        onQueryTextChange(query);
    }

    //НАЖАТИЕ НА СПИСОК в DRAWER LAYOUT
        public void OnCardDrawerClick (View view) {
            CardFolder1 = view.findViewById(R.id.item_folder);
            String GetFolder = CardFolder1.getText().toString();
            ///ВСЕ ЗАМЕТКИ
            if (GetFolder.matches(getString(R.string.all_notes))) {
                FolderSelected = getString(R.string.all_notes);
                fold_for_spinner = getString(R.string.main_folder);
                AppDatabase database = App.getInstance().getDatabase();
                final NoteDao noteDao = database.noteDao();
                on_del = 1;
                final List<Name> names = noteDao.getNameNote(search_dir, on_del);
                Log.w("MY", String.valueOf(names));
                Selected_names = names;
                SomeDataRecyclerAdapter noteAdapter_all_notes = new SomeDataRecyclerAdapter(names, dark_set);
                recyclerView.setAdapter(noteAdapter_all_notes);
                //noteAdapter.updateData(names);
                CollectionSorted(names, noteAdapter_all_notes);//прикручиваем сортировку
                toolbar.setTitle(getString(R.string.all_notes));
                drawer.closeDrawer(GravityCompat.START);
            }

            else if (GetFolder.equals(getString(R.string.new_folder))) {
                AppDatabase database = App.getInstance().getDatabase();//получаем базу
                final NoteDao noteDao = database.noteDao();//накатываем на базу интерфейс
                Purchase_Pref = getPreferences(MODE_PRIVATE);
                int Status_Purchase = Purchase_Pref.getInt(TAG_PURCHASE, 0);
                if (Status_Purchase == 1) {
                    dirs_list_final = 1;
                }
                else {
                    //считаем кол-во папок в базе
                    dirs_list_final = noteDao.getDirs4(search_dir);
                    Log.w("MY", String.valueOf(dirs_list_final));
                }

                if (dirs_list_final <= 3) {//если <= 3, то добавляем новую папку
                    input();
                    input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                            return false;
                        }
                    });
                    input.requestFocus();
                    input.setFocusable(true);
                    final AlertDialog.Builder new_folder = new AlertDialog.Builder(MainActivity.this);
                    new_folder.setTitle(getString(R.string.new_folder))
                            .setView(input)
                            .setCancelable(false);
                            if (dark_set == 0) {
                                new_folder.setIcon(R.drawable.baseline_folder_black_48);
                            }
                            else {
                                new_folder.setIcon(R.drawable.baseline_folder_white_48);
                            }
                    new_folder.setPositiveButton(getString(R.string.create_folder), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    TextDirName = input.getText().toString();
                                    stop_tag=0;
                                    AppDatabase database = App.getInstance().getDatabase();//получаем базу
                                    NoteDao noteDao = database.noteDao();//накатываем на базу интерфейс
                                    dirs_list_final = noteDao.getDirs4(search_dir);
                                    if (dirs_list_final != 0) {
                                        //помещаем в массив все пользовательские папки
                                        dirs_list_same = noteDao.getDirs3(search_dir);
                                        //берем массив варинатов ввода от пользователя
                                        String[] dirs_same_array = getResources().getStringArray(R.array.double_folders_protection);
                                        //и добавляем значения в наш массив, с которым потом будем сверять то, что ввел пользователь
                                        dirs_list_same.addAll(Arrays.asList(dirs_same_array));
                                        String[] dirs_same = dirs_list_same.toArray(new String[dirs_list_same.size()]);

                                        //String[] double_array;

                                        dirs_list_final = dirs_same.length;//теперь находим корректную длину массива

                                        while (dirs_list_final > 0) {
                                            if (dirs_same[dirs_list_final-1].equals(TextDirName)) {
                                                Toast.makeText(MainActivity.this, getString(R.string.t_save_folder2), Toast.LENGTH_LONG).show();
                                                stop_tag=1;
                                                break;
                                            }
                                            dirs_list_final--;
                                        }
                                    }
                                    if (stop_tag != 1) {

                                        Note note = new Note();
                                        note.directory = TextDirName;
                                        //заполняем поле дата в базе
                                        note.time = Calendar.getInstance().getTimeInMillis();
                                        //в поле usertext вставляем значение, указывающее на то, что это папка
                                        note.namenote = DefaultUserText;
                                        note.favourites = 0;
                                        if (TextDirName == null || TextDirName.equals("") || TextDirName.equals(" ")||TextDirName.equals("  ")) {
                                            Toast toast_empty_dir = Toast.makeText(MainActivity.this,getString(R.string.t_save_folder3),Toast.LENGTH_SHORT);
                                            toast_empty_dir.setGravity(Gravity.CENTER, 0, 0);
                                            toast_empty_dir.show();
                                        }
                                        else {
                                            noteDao.insert(note);//записываем в базу
                                            dialog.dismiss();
                                            List<String> dirs_list = noteDao.getDirs3(search_dir);
                                            dirs = dirs_list.toArray(new String[dirs_list.size()]);

                                            ArrayForDrawerLayout(FoldersArray, dirs, FoldersArray3);
                                            final DrawerListAdapter adapterAllList = new DrawerListAdapter(MainActivity.this, AllFoldersArray, dark_set);

                                            ListFirst.setAdapter(adapterAllList);
                                            Toast.makeText(MainActivity.this,getString(R.string.t_save_folder),Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            })
                            .setNegativeButton(getString(R.string.button_negative), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    new_folder.show();
                }//закрытие кода, если <= 4 папок
                else  {
                 Toast.makeText(this,getString(R.string.t_save_folder4),Toast.LENGTH_LONG).show();
                }
            }//конец Новая папка
            //открытие ОБЩАЯ ПАПКА
            else if (GetFolder.equals(getString(R.string.main_folder))) {
                FolderSelected = getString(R.string.main_folder);
                fold_for_spinner = FolderSelected;
                AppDatabase database = App.getInstance().getDatabase();
                final NoteDao noteDao = database.noteDao();
                select_dir = GetFolder;
                List<Name> names_main_folder = noteDao.openDir(select_dir, search_dir, on_del);
                SomeDataRecyclerAdapter noteAdapterMain = new SomeDataRecyclerAdapter(names_main_folder, dark_set);
                recyclerView.setAdapter(noteAdapterMain);
                CollectionSorted(names_main_folder, noteAdapterMain);//прикручиваем сортировку
                toolbar.setTitle(getString(R.string.main_folder));
                drawer.closeDrawer(GravityCompat.START);
                select_dir = null;
            }
            //открытие ИЗБРАННОЕ
            else if (GetFolder.equals(getString(R.string.favourites_folder))) {
                FolderSelected = getString(R.string.favourites_folder);
                AppDatabase database = App.getInstance().getDatabase();
                final NoteDao noteDao = database.noteDao();
                on_del = 1;
                List<Name> names = noteDao.getFavourites(search_dir, on_del);
                Log.w("MY", String.valueOf(names));
                Selected_names = names;
                noteAdapter = new SomeDataRecyclerAdapter(names, dark_set);
                recyclerView.setAdapter(noteAdapter);
                //noteAdapter.updateData(names);
                CollectionSorted(names, noteAdapter);//прикручиваем сортировку
                toolbar.setTitle(getString(R.string.favourites_folder));
                drawer.closeDrawer(GravityCompat.START);
            }
            //открытие КОРЗИНА
            else if (GetFolder.equals(getString(R.string.basket))) {
                FolderSelected = getString(R.string.basket);
                AppDatabase database = App.getInstance().getDatabase();
                final NoteDao noteDao = database.noteDao();
                on_del = 0;
                List<Name> names = noteDao.getNameNote(search_dir, on_del);
                Log.w("MY", String.valueOf(names));
                noteAdapter = new SomeDataRecyclerAdapter(names, dark_set);
                recyclerView.setAdapter(noteAdapter);
                CollectionSorted(names, noteAdapter);//прикручиваем сортировку
                on_del = 1;
                toolbar.setTitle(getString(R.string.basket));
                drawer.closeDrawer(GravityCompat.START);
            }
            //открытие НАСТРОЙКИ
            else if (GetFolder.equals(getString(R.string.settings))) {
                Intent s = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(s);
                //БИЛЛИНГ (ВРЕМЕННО)
                //launchBilling(mSkuId);
            }
            ///открытие О ПРИЛОЖЕНИИ
            else if (GetFolder.equals(getString(R.string.about))) {

                final String[] actions = {getString(R.string.privacy_policy), getString(R.string.buy_full_version)};
                final AlertDialog.Builder dialog_about_app = new AlertDialog.Builder(MainActivity.this);
                dialog_about_app.setTitle("О приложении");//устанавливаем шапку
                dialog_about_app.setCancelable(true);//позволяет закрыть диалоговое окно при нажатии кнопки назад
                dialog_about_app.setItems(actions, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    switch (i) {
                        case 0:
                            Intent s = new Intent(MainActivity.this, AboutActivity.class);
                            startActivity(s);
                            break;
                        case 1:
                            launchBilling("notepad_full_version");
                            break;
                    }
                    }
                });
                dialog_about_app.show();

            }

            else {
                    select_dir = GetFolder;
                    FolderSelected = GetFolder;
                    fold_for_spinner = GetFolder;
                    AppDatabase database = App.getInstance().getDatabase();
                    final NoteDao noteDao = database.noteDao();
                    on_del = 1;//здесь это нужно, чтобы далее выбрать не помещенные в Корзину заметки
                    List<Name> namesSelectDir = noteDao.openDir(select_dir, search_dir, on_del);
                    SomeDataRecyclerAdapter noteAdapterSDir = new SomeDataRecyclerAdapter(namesSelectDir, dark_set);
                    recyclerView.setAdapter(noteAdapterSDir);
                    CollectionSorted(namesSelectDir, noteAdapterSDir);//прикручиваем сортировку
                    toolbar.setTitle(GetFolder);//отображаем название открытой папки
                    drawer.closeDrawer(GravityCompat.START);
            }
        }

    ///////////////////////////////////////////////////////////
    /////////КНОПКИ ИЗМЕНЕНИЯ СОРТИРОВКИ ЗАМЕТОК//////////////
    /////////////////////////////////////////////////////////
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // А - Я

        if (item.getItemId() == R.id.sort_a_y) {
            sPref = getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor ed = sPref.edit();
            ed.putString(SORT_NOTES, "1");
            ed.apply();
            SomeDataRecyclerAdapter adapter_sort = new SomeDataRecyclerAdapter(Selected_names, dark_set);
            recyclerView.setAdapter(adapter_sort);
            CollectionSorted(Selected_names, adapter_sort);
            item.setChecked(true);
        }
        // Я - А
        if (item.getItemId() == R.id.sort_y_a) {
            sPref = getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor ed = sPref.edit();
            ed.putString(SORT_NOTES, "2");
            ed.apply();
            SomeDataRecyclerAdapter adapter_sort = new SomeDataRecyclerAdapter(Selected_names, dark_set);
            recyclerView.setAdapter(adapter_sort);
            CollectionSorted(Selected_names, adapter_sort);
            item.setChecked(true);
        }
        //СНАЧАЛА НОВЫЕ
        if (item.getItemId() == R.id.sort_new_o) {
            sPref = getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor ed = sPref.edit();
            ed.putString(SORT_NOTES, "3");
            ed.apply();
            SomeDataRecyclerAdapter adapter_sort = new SomeDataRecyclerAdapter(Selected_names, dark_set);
            recyclerView.setAdapter(adapter_sort);
            CollectionSorted(Selected_names, adapter_sort);
            item.setChecked(true);
        }
        //СНАЧАЛА СТАРЫЕ
        if (item.getItemId() == R.id.sort_old_n) {
            sPref = getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor ed = sPref.edit();
            ed.putString(SORT_NOTES, "4");
            ed.apply();
            SomeDataRecyclerAdapter adapter_sort = new SomeDataRecyclerAdapter(Selected_names, dark_set);
            recyclerView.setAdapter(adapter_sort);
            CollectionSorted(Selected_names, adapter_sort);
            item.setChecked(true);
        }

        if (item.getItemId() == android.R.id.home) //если кнопка назад = кнопке home
        //а эта кнопка в логику андроид именно так и вшита, то
        {
            SearchView.clearFocus();
            Log.w("MY", "SearchView.clearFocus();" );
        }
        return super.onOptionsItemSelected(item);
    }

    public void CollectionSorted (final List<Name> names, final SomeDataRecyclerAdapter noteAdapter) {
        Collections.sort(names, new Comparator<Name>() {
            @Override
            public int compare(Name o1, Name o2) {
                sPref = getPreferences(MODE_PRIVATE);
                sort_var = sPref.getString(SORT_NOTES, "");//помещаем номер выбранной сортировки в глобальную переменную (нужно для "точек")
                //Log.w("MY", "sort_var = " + sort_var);
                if (sort_var.equals("1")) {
                    return o1.namenote.compareTo(o2.namenote); // сортировка от А до Я
                }
                if (sort_var.equals("2")) {
                    return o2.namenote.compareTo(o1.namenote); // сортировка от Я до А
                }
                if (sort_var.equals("3")) {
                    return (int) (o2.time - o1.time); // так должна выглядеть сортировка по дате (Сначала новые)
                }
                if (sort_var.equals("4")) {
                    return (int) (o1.time - o2.time); // так должна выглядеть сортировка по дате (Сначала старые)
                }
                if (sort_var.equals("")) {
                    return (int) (o2.time - o1.time); // (Сначала новые)
                }
                menu.findItem(Integer.parseInt(sort_var)).setChecked(true);
                return 0;
            }
        });
        Selected_names = names;//глобальная переменная для моментальной смены сортировки
    }

    ////////////////////////
    ///ПИШЕМ МЕНЮ ПОИСКА///
    //////////////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {//при создании меню (первый запуск активити)
        MenuInflater inflater = getMenuInflater();
        MenuInflater inflater2 = getMenuInflater();
        inflater.inflate(R.menu.main, menu);//отображаем меню сортировки
        inflater2.inflate(R.menu.search_menu, menu);//отображаем поиск (лупа)

        // Associate searchable configuration with the SearchView
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView = (SearchView) searchItem.getActionView();//ранее было вот  так - (SearchView) MenuItemCompat.getActionView(searchItem)

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        assert searchManager != null;
        SearchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        SearchView.setIconifiedByDefault(false);
        SearchView.setFocusable(true);
        SearchView.setIconified(false);
        SearchView.requestFocusFromTouch();
        SearchView.setOnQueryTextListener(onQueryTextListener);
        SearchView.clearFocus();
        SearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                Log.w("MY", "getWindow().setSoftInputMode" );
                return true;
            }
        });

        this.menu = menu;//привязываем меню к глобальной переменной, уже не помню для чего это нужно
        //пишем созданение выбора сортировки
        MenuItem item_set1 = menu.findItem(R.id.sort_a_y);
        MenuItem item_set2 = menu.findItem(R.id.sort_y_a);
        MenuItem item_set3 = menu.findItem(R.id.sort_new_o);
        MenuItem item_set4 = menu.findItem(R.id.sort_old_n);
        if (sort_var.equals("1")) {//если в sort_var выбран первый вариант, то отображаем, что он выбран и т.д.
            item_set1.setChecked(true);
            Log.w("MY", "item_set2.setChecked(true)");
        }
        if (sort_var.equals("2")) {
            item_set2.setChecked(true);
        }
        if (sort_var.equals("3")) {
            item_set3.setChecked(true);
        }
        if (sort_var.equals("4")) {
            item_set4.setChecked(true);
        }
        //если в sort_var никакой вариант сортировки не выбран
        if (sort_var.equals("")) {//если в sort_var выбран первый вариант, то отображаем, что он выбран и т.д.
            item_set3.setChecked(true);
            Log.w("MY", "item_set2.setChecked(true)");
        }

        return true;
    }

    private android.widget.SearchView.OnQueryTextListener onQueryTextListener = new SearchView.OnQueryTextListener() {
       @Override
       public boolean onQueryTextSubmit(String query) {
           getResults(query, FolderSelected);
           return true;
       }

       @Override
       public boolean onQueryTextChange(String query) {
           if (!query.isEmpty()) { // если поисковая строка не пустая, то исполняется getResults
               getResults(query, FolderSelected);
           }
           if (FolderSelected.equals(getString(R.string.all_notes)) || FolderSelected.equals("")) {
               Log.w("MY", "FolderSelected = " + FolderSelected);
               AppDatabase database = App.getInstance().getDatabase();
               final NoteDao noteDao = database.noteDao();
               on_del = 1;
               List<Name> names = noteDao.getNameNote(search_dir, on_del);
               noteAdapter = new SomeDataRecyclerAdapter(names, dark_set);
               recyclerView.setAdapter(noteAdapter);
               CollectionSorted(names, noteAdapter);
           }
           else if (FolderSelected.equals(getString(R.string.basket))) {
               AppDatabase database = App.getInstance().getDatabase();
               final NoteDao noteDao = database.noteDao();
               on_del = 0;
               List<Name> names = noteDao.getNameNote(search_dir, on_del);
               Log.w("MY", String.valueOf(names));
               noteAdapter = new SomeDataRecyclerAdapter(names, dark_set);
               recyclerView.setAdapter(noteAdapter);
               CollectionSorted(names, noteAdapter);
           }
           else {
               AppDatabase database = App.getInstance().getDatabase();
               final NoteDao noteDao = database.noteDao();
               select_dir = FolderSelected;
               on_del = 1;
               List<Name> names_main_folder = noteDao.openDir(select_dir, search_dir, on_del);
               SomeDataRecyclerAdapter noteAdapterMain = new SomeDataRecyclerAdapter(names_main_folder, dark_set);
               recyclerView.setAdapter(noteAdapterMain);
               CollectionSorted(names_main_folder, noteAdapterMain);
               select_dir = null;
           }
           return true;
       }

       private void getResults(final String newText, String folder_selected) {
           String queryText = "%" + newText + "%";
           folder_selected = FolderSelected;
           CustomViewModel viewModel = ViewModelProviders.of(MainActivity.this).get(CustomViewModel.class);
           if (folder_selected.equals(getString(R.string.all_notes)) || folder_selected.equals("")) {
               CustomViewModel.searchQueryAll(queryText, folder_selected).observe(MainActivity.this, new Observer<List<Name>>() {
                   @Override
                   public void onChanged(List<Name> names) {
                       if (names == null) return;
                       SomeDataRecyclerAdapter adapterNew = new SomeDataRecyclerAdapter(names, dark_set);
                       recyclerView.setAdapter(adapterNew);
                   }
               });
           }
           else if (folder_selected.equals(getString(R.string.basket))) {
               CustomViewModel.searchQueryBasket(queryText, folder_selected).observe(MainActivity.this, new Observer<List<Name>>() {
                   @Override
                   public void onChanged(List<Name> names) {
                       if (names == null) return;
                       SomeDataRecyclerAdapter adapterNew = new SomeDataRecyclerAdapter(names, dark_set);
                       recyclerView.setAdapter(adapterNew);
                   }
               });
           }
           else {
               CustomViewModel.searchQuery(queryText, folder_selected).observe(MainActivity.this, new Observer<List<Name>>() {
                   @Override
                   public void onChanged(List<Name> names) {
                       if (names == null) return;
                       SomeDataRecyclerAdapter adapterNew = new SomeDataRecyclerAdapter(names, dark_set);
                       recyclerView.setAdapter(adapterNew);
                   }
               });
           }//конец, else
       }
   };

       //получаем наименование папки из класса CreateDir
    private void reciveIntent () {
        Intent i = getIntent();//получение сообщения от intent,
        if (i !=null) {
        NewDirectoryForSave = i.getStringExtra("directory");
        TimeNewDirectory = i.getStringExtra("dir_time");
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        getResultVoice(query, FolderSelected);
        return true;
    }
/////////ГОЛОСОВОЙ ПОИСК//////////
    @Override
    public boolean onQueryTextChange(String newText) {
        getResultVoice(query, FolderSelected);
        Log.w("MY", query);
        return true;
    }
    private void getResultVoice(final String newText, String folder_selected) {
        String queryText = "%" + newText + "%";
        folder_selected = FolderSelected;
        Log.w("MY", "folder_selected = " + folder_selected);
        Log.w("MY", "FolderSelected = " + FolderSelected);
        CustomViewModel viewModel = ViewModelProviders.of(MainActivity.this).get(CustomViewModel.class);
        if (folder_selected.equals(getString(R.string.all_notes)) || folder_selected.equals("")) {
            CustomViewModel.searchQueryAll(queryText, folder_selected).observe(MainActivity.this, new Observer<List<Name>>() {
                @Override
                public void onChanged(List<Name> names) {
                    if (names == null) return;
                    SomeDataRecyclerAdapter adapterNew = new SomeDataRecyclerAdapter(names, dark_set);
                    recyclerView.setAdapter(adapterNew);
                }
            });
        }
        else if (folder_selected.equals(getString(R.string.basket))) {
            CustomViewModel.searchQueryBasket(queryText, folder_selected).observe(MainActivity.this, new Observer<List<Name>>() {
                @Override
                public void onChanged(List<Name> names) {
                    if (names == null) return;
                    SomeDataRecyclerAdapter adapterNew = new SomeDataRecyclerAdapter(names, dark_set);
                    recyclerView.setAdapter(adapterNew);
                }
            });
        }
        else {
            CustomViewModel.searchQuery(queryText, folder_selected).observe(MainActivity.this, new Observer<List<Name>>() {
                @Override
                public void onChanged(List<Name> names) {
                    if (names == null) return;
                    SomeDataRecyclerAdapter adapterNew = new SomeDataRecyclerAdapter(names, dark_set);
                    recyclerView.setAdapter(adapterNew);
                }
            });
        }

    }
    @Override
    public boolean onLongClick(View v) {
        return true;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        return false;
    }
    public void GetBase () {
        database_GB = App.getInstance().getDatabase();//получаем базу
        noteDao_GB = database_GB.noteDao();//накатываем на базу интерфейс
    }

    ///////////////////////////////////////////////////
    /////////////МЕНЯЕМ ЦВЕТОВЫЕ СХЕМЫ////////////////
    /////////////////////////////////////////////////
    @SuppressLint("ObsoleteSdkInt")
    private void settings_themes() {
        //обязательно тут также оставляем theme_color и dark_switch, т.к. это тметод используется в onResume
        //оставляем их тут чтобы не поломались цвета и цвет значков
        theme_color = def_pref.getString("key_theme_color", getString(R.string.c2_blue));//подвязываем ключ из preference_screen.xml к def_pref и size
        dark_switch = def_pref.getBoolean("switch_preference_dark", false);
        if (theme_color != null & !dark_switch ||theme_color != null & dark_switch & dark_time == 0) {
            colorDrawable1 = new ColorDrawable ();
            if (theme_color.equals(c_blue)) {
                colorDrawable1.setColor(Color.parseColor("#3C4CB3"));
                actionBar.setBackgroundDrawable(colorDrawable1);
                window.setStatusBarColor(ContextCompat.getColor(this,R.color.StatusBar_Blue2));
                AddMainFab.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#3C4CB3")));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.StatusBar_Blue2));
                }
            }
            if (theme_color.equals(c_purple)) {
                colorDrawable1.setColor(Color.parseColor("#6A5ACD"));
                actionBar.setBackgroundDrawable(colorDrawable1);
                window.setStatusBarColor(ContextCompat.getColor(this,R.color.StatusBar_Purple));
                AddMainFab.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#6A5ACD")));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.StatusBar_Purple));
                }
            }
            if (theme_color.equals(c_red)) {
                colorDrawable1.setColor(Color.parseColor("#B22222"));
                actionBar.setBackgroundDrawable(colorDrawable1);
                window.setStatusBarColor(ContextCompat.getColor(this,R.color.StatusBar_Red));
                AddMainFab.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#B22222")));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.StatusBar_Red));
                }
            }
            if (theme_color.equals(c_orange)) {
                colorDrawable1.setColor(Color.parseColor("#FF8C00"));
                actionBar.setBackgroundDrawable(colorDrawable1);
                window.setStatusBarColor(ContextCompat.getColor(this,R.color.StatusBar_Orange));
                AddMainFab.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF8C00")));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.StatusBar_Orange));
                }
            }
            if (theme_color.equals(c_green)) {
                colorDrawable1.setColor(Color.parseColor("#659A3F"));
                actionBar.setBackgroundDrawable(colorDrawable1);
                window.setStatusBarColor(ContextCompat.getColor(this,R.color.StatusBar_Green2));
                AddMainFab.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#659A3F")));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.StatusBar_Green2));
                }
            }
            if (theme_color.equals(c_grey)) {
                colorDrawable1.setColor(Color.parseColor("#808080"));
                actionBar.setBackgroundDrawable(colorDrawable1);
                window.setStatusBarColor(ContextCompat.getColor(this,R.color.StatusBar_Grey));
                AddMainFab.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#808080")));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.StatusBar_Grey));
                }
            }
            if (theme_color.equals(c_dark)) {
                dark_set = 1;
                setDarkTheme();//VOID
            }
        }
//иначе включаем темную тему
        else {
            dark_set = 1;
            setDarkTheme();//VOID
        }
    }

    @Override
    protected void onResume() {
        settings_themes();
        super.onResume();
    }

    @Override
    public void recreate() {
        super.recreate();
    }

    public void input() {//метод создания текстового поля в Создании и Изменении пользовательской папки
        input = new EditText(MainActivity.this);
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(18);
        input.setFilters(FilterArray);
        input.setSingleLine(true);
    }

    //темная тема
    public void setDarkTheme() {
        colorDrawable1 = new ColorDrawable();
        colorDrawable1.setColor(Color.parseColor("#282828"));
        actionBar.setBackgroundDrawable(colorDrawable1);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.StatusBar_Dark));
        //включаем темный стиль
        setTheme(R.style.AppThemeDark);
        //темная заливка самого низа drawerLayot
        ListFirst.setBackgroundColor(ContextCompat.getColor(this, R.color.ThemeDark_fon));
        //темная заливка самого низа на главном экране (список заметок)
        MainConst.setBackgroundColor(ContextCompat.getColor(this, R.color.ThemeDark_fon));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.StatusBar_Dark));
        }
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        openQuitDialog();
    }
    //ВЫ ТОЧНО ХОТИТЕ ВЫЙТИ

    @Override
    protected void onDestroy()
    {
        /*учитываем значение TAG_IF_DEL_FOLDER, т.к. onDestroy работает после вызова finish()
        который используется после переименования/удаления пользовательской папки.
        Если это был finish() в коде переименования/удаления папки, тогда делаем просто super.onDestroy();

         */
        if (TAG_IF_DEL_FOLDER == 0) {
        finishAffinity();
        android.os.Process.killProcess(android.os.Process.myPid());//без этого не будет корректно закрываться приложение
        //а останется висеть в фоне, если сделать просто finish() на этом активити
        super.onDestroy();
        Log.w("MY", "super.onDestroy();");
        finish();
        Log.w("MY", "finish();");
        }
        else {
            super.onDestroy();
            TAG_IF_DEL_FOLDER = 0;
        }
    }

    private void openQuitDialog() {
        AlertDialog.Builder quitDialog = new AlertDialog.Builder(
                this);
        quitDialog.setTitle(getString(R.string.pop_exit_main));

        quitDialog.setPositiveButton(getString(R.string.button_positive), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                finish();
                Log.w("MY", "quitDialog.setPositiveButton");

            }
        });

        quitDialog.setNegativeButton(getString(R.string.button_negative), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        quitDialog.show();
    }

    //отображение диалога очистки ВСЕ ЗАМЕТКИ и ОБЩАЯ ПАПКА
    private void DeleteAllMainDialog (final View view, int folder, String folder_s) {
        GetBase();
        //database_a = App.getInstance().getDatabase();
        //noteDao_a = database_a.noteDao();
        //если выбрано ВСЕ ЗАМЕТКИ
        if (folder == 0) {
            on_del = 0;
            time_list2 = noteDao_GB.basketForDelete(on_del);//получаем список всех заметок не в Корзине (только время)
            sizeArray_a = time_list2.size();//размер ArrayList
        }
        //если выбрано ОБЩАЯ ПАПКА
        if (folder == 1) {
            on_del = 1;
            select_dir = folder_s;
            time_list2 = noteDao_GB.noteForDelete(select_dir, search_dir, on_del);
            sizeArray_a = time_list2.size();//размер ArrayList
        }

        Log.w("MY", String.valueOf(sizeArray_a));
        if (sizeArray_a > 0) {
            //создаем Alert Dialog - УДАЛЕНИЕ
            if (folder == 0) {//если ВСЕ ЗАМЕТКИ
                MessageDelBasket = getString(R.string.alert_del_all_n1) + sizeArray_a;
            }
            if (folder == 1) {//если ОБЩАЯ ПАПКА
                MessageDelBasket = getString(R.string.alert_del_all_n3) + sizeArray_a;
            }
            final AlertDialog.Builder dialog_del_all_n1 = new AlertDialog.Builder(MainActivity.this);
            dialog_del_all_n1.setTitle(getString(R.string.alert_del_name3))
                    .setMessage(MessageDelBasket);
            if (dark_set == 0) {
                dialog_del_all_n1.setIcon(R.drawable.baseline_delete_black_48);
            }
            else {
                dialog_del_all_n1.setIcon(R.drawable.baseline_delete_white_48);
            }
            dialog_del_all_n1.setPositiveButton(getString(R.string.button_positive), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            for (int startIndex = 0; startIndex < sizeArray_a; startIndex++) {
                                Note note = noteDao_GB.getTime2(time_list2.get(startIndex));
                                note.on_delete = 1;//помещаем заметку в корзину
                                noteDao_GB.update(note);
                            }
                            OnCardDrawerClick(view);
                            Toast.makeText(MainActivity.this, getString(R.string.t_basket1), Toast.LENGTH_LONG).show();
                        }
                    })
                    .setNegativeButton(getString(R.string.button_negative), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
            dialog_del_all_n1.show();
        } //конец if (sizeArray_b > 0)
        else {
            //создаем Alert Dialog - ИНФОРМИРОВАНИЕ
            MessageDelBasket = getString(R.string.alert_del_all_n2);
            final AlertDialog.Builder dialog_del_basket2 = new AlertDialog.Builder(MainActivity.this);
            dialog_del_basket2.setTitle(getString(R.string.alert_del_name3))
                    .setMessage(MessageDelBasket);
            if (dark_set == 0) {
                dialog_del_basket2.setIcon(R.drawable.baseline_delete_black_48);
            }
            else {
                dialog_del_basket2.setIcon(R.drawable.baseline_delete_white_48);
            }
            dialog_del_basket2.setPositiveButton(getString(R.string.button_ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
            dialog_del_basket2.show();
        } //конец else
    }
    //долгое нажатие на Корзину
    private void BasketLongClick (final View view, int folder) {
        GetBase();
        //database_b = App.getInstance().getDatabase();
        //noteDao_b = database_b.noteDao();
        time_list = noteDao_GB.basketForDelete(folder);//получаем список всех заметок в Корзине (только время)
        sizeArray_b = time_list.size();//размер ArrayList
        if (sizeArray_b > 0) {
            //создаем Alert Dialog - УДАЛЕНИЕ
            MessageDelBasket = getString(R.string.alert_del_basket1) + sizeArray_b + "\n\n" + getString(R.string.alert_delete_4);
            final AlertDialog.Builder dialog_del_basket1 = new AlertDialog.Builder(MainActivity.this);
            dialog_del_basket1.setTitle(getString(R.string.alert_del_name2))
                    .setMessage(MessageDelBasket);
            if (dark_set == 0) {
                dialog_del_basket1.setIcon(R.drawable.baseline_delete_black_48);
            }
            else {
                dialog_del_basket1.setIcon(R.drawable.baseline_delete_white_48);
            }
            dialog_del_basket1.setPositiveButton(getString(R.string.button_positive), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            for (int startIndex = 0; startIndex < sizeArray_b; startIndex++) {
                                Note note = noteDao_GB.getTime2(time_list.get(startIndex));
                                noteDao_GB.delete(note);
                            }
                            OnCardDrawerClick (view);
                            Toast.makeText(MainActivity.this, getString(R.string.t_basket2), Toast.LENGTH_LONG).show();
                        }
                    })
                    .setNegativeButton(getString(R.string.button_negative), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
            dialog_del_basket1.show();
        } //конец if (sizeArray_b > 0)
        else {
            //создаем Alert Dialog - ИНФОРМИРОВАНИЕ
            MessageDelBasket = getString(R.string.alert_del_basket2);
            final AlertDialog.Builder dialog_del_basket2 = new AlertDialog.Builder(MainActivity.this);
            dialog_del_basket2.setTitle(getString(R.string.alert_del_name2))
                    .setMessage(MessageDelBasket);
            if (dark_set == 0) {
                dialog_del_basket2.setIcon(R.drawable.baseline_delete_black_48);
            }
            else {
                dialog_del_basket2.setIcon(R.drawable.baseline_delete_white_48);
            }
            dialog_del_basket2.setPositiveButton(getString(R.string.button_ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });

            dialog_del_basket2.show();
        } //конец else
     }
    //объединение массисов для передачу в адаптер DrawerLayot (боковое меню)
    public void ArrayForDrawerLayout (String [] Array_1, String [] Array_2, String [] Array_3) {
        //объединяем 3 массива
        if (Array_2.length !=0) {//если пользовательские папки есть, тогда 3 массива объединяем
            Log.w("MY", "dirs.length !=0");
            AllFoldersArray = new String[Array_1.length+Array_2.length+Array_3.length];
            Log.w("MY", "длина массива = " + String.valueOf(Array_1.length+Array_2.length+Array_3.length));
            count = 0;//счетчик
            //складываем 1-й массив значений
            for(int i = 0; i<Array_1.length; i++) {
                AllFoldersArray[i] = Array_1[i];
                count++;
            }
            //складываем пользовательский массив значений
            for(int m = 0;m<Array_2.length;m++) {
                AllFoldersArray[count] = Array_2[m];
                count++;
            }
            //складываем 3-й массив значений
            for(int j = 0;j<Array_3.length;j++) {
                AllFoldersArray[count] = Array_3[j];
                count++;
            }
        }
        else {//иначе, значит нет пользовательских папок, поэтому складываем 2 массива

            AllFoldersArray = new String[Array_1.length+Array_3.length];
            int count = 0;
            for(int i = 0; i<Array_1.length; i++) {
                AllFoldersArray[i] = Array_1[i];
                count++;
            }
            for(int j = 0;j<Array_3.length;j++) {
                AllFoldersArray[count++] = Array_3[j];
            }
        }
    }

    //запрос о покупках
    private List<Purchase> queryPurchases() {
        Purchase.PurchasesResult purchasesResult = mBillingClient.queryPurchases(BillingClient.SkuType.INAPP);
        return purchasesResult.getPurchasesList(); //В purchasesList попадает список всех покупок, сделанных пользователем.
    }

    //ПРОВЕРКА ПОКУПКИ
    private void querySkuDetails() {
        SkuDetailsParams.Builder skuDetailsParamsBuilder = SkuDetailsParams.newBuilder();
        List<String> skuList = new ArrayList<>();
        skuList.add(mSkuId);
        skuDetailsParamsBuilder.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);
        mBillingClient.querySkuDetailsAsync(skuDetailsParamsBuilder.build(), new SkuDetailsResponseListener() {
            @Override
            public void onSkuDetailsResponse(int responseCode, List<SkuDetails> skuDetailsList) {
                if (responseCode == 0) {
                    Log.w("MY", "onSkuDetailsResponse if (responseCode == 0) ");
                    for (SkuDetails skuDetails : skuDetailsList) {
                        mSkuDetailsMap.put(skuDetails.getSku(), skuDetails);
                        Log.w("MY", "mSkuDetailsMap" + mSkuDetailsMap);
                    }
                }
            }
        });
    }

    //ПРЕДОСТАВИТЬ ДОСТУП К КУПЛЕННОМУ ТОВАРУ.
    private void payComplete() {
        //отправляем интент в main activity о возможности создания неограниченного кол-ва полз папок (тэг цифру)
        //и отправляем интент в CreateNote о б отключении рекламы (тэг цифру), вернее
        //если в мэйн активити мы уже знаем, что приложение куплено, то открываем CreateNote уже с соответствующим тэгом
        Log.w("MY", "payComplete() выполняем");
        Purchase_Pref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = Purchase_Pref.edit();
        Purchase_OK = 1;
        ed.putInt(TAG_PURCHASE, Purchase_OK);
        ed.apply();
    }
    public void HowTimeNow () {
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
     }
  }
