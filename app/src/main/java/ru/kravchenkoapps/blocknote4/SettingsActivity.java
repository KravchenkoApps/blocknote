package ru.kravchenkoapps.blocknote4;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SettingsActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private ActionBar actionBar;//переменная для акшинбара
    private SharedPreferences def_pref;
    private ColorDrawable colorDrawable1;
    private Window window; // переменная для изменения цвета статус-бара (там шже часы, связь и т.д.)
    int dark_time = 0;//маркер темного времени суток, 0 - нет, 1 - да
    Boolean dark_switch;//переменная включения темной темы ночью
    String c_blue, c_purple, c_red, c_orange, c_green, c_grey, c_dark;
    //изменение языка

    public int dark_set;
    int tag_policy_view = 0;


    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        def_pref = PreferenceManager.getDefaultSharedPreferences(this);
        def_pref.registerOnSharedPreferenceChangeListener(this);
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null)//если actionBar существует, то следующ.код исполнится
        {
            actionBar = getSupportActionBar(); //находим actionBar, если он есть
            actionBar.setDisplayHomeAsUpEnabled(true);//показываем в акшинбаре стрелочку выхода из него,то есть кнопка возврата домой
            actionBar.setTitle(getString(R.string.settings));//устанавливаем на экране настроек надпись
        }

        //заменяем основной контент на наш фрагмент через команду getFragmentManager
        //replace- переместиться из android.R.id.content в новый SettingsFragment
        //commit() - запустить
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();


        //код для подготовки к изменению цвета статус-бара
        window = this.getWindow();
        // без этого флага работать не будет
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // без этого флага работать не будет
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorAccent));

        //получаем цвета (необходимо для корректной работы переключения языка приложения)
        c_blue = getString(R.string.c2_blue);
        c_purple = getString(R.string.c2_purple);
        c_red = getString(R.string.c2_red);
        c_orange = getString(R.string.c2_orange);
        c_green = getString(R.string.c2_green);
        c_grey = getString(R.string.c2_grey);
        c_dark = getString(R.string.c2_dark);

        //КОД, необходимый для работы темной темы ночью
        Date currentDate = new Date();//берем текущее время
        DateFormat timeFormat = new SimpleDateFormat("HH", Locale.getDefault());
        int time_now = Integer.parseInt(timeFormat.format(currentDate));//получаем текущий час в формате "HH" в числовую переменную
        Log.w("MY", String.valueOf(time_now));
        if (time_now >= 20 || time_now <= 6) {
            dark_time = 1;
            Log.w("MY", "dark_time = 1");
        }
        else {
            dark_time = 0;
            Log.w("MY", "dark_time = 0");
        }

        settings_themes();

    }

    @Override
    //пишем обработчик кнопки "Назад" (стрелочки) в actionBar
    //так как кнопка эта это по сути item, то используем onOptionsItemSelected
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) //если кнопка назад = кнопке home
        //а эта кнопка в логику андроид именно так и вшита, то
        {
            if (tag_policy_view == 0) {//если мы не на экране политики конфиденциальности, тогда выходим
                FinishAndMainActivity();
            }
            else {//если в политике, то запускаем заново экран настроек
                getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
                tag_policy_view = 0;
            }
        }
//иначе вернуть истину, то есть все будет работать, как и работает
        return true;
    }
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
                setDarkTheme();//VOID

                Log.w("MY", "dark_set = " + String.valueOf(dark_set));
            }
         }

//иначе включаем темную тему
        else {
            setDarkTheme();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void recreate() {
        super.recreate();
    }

        @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("key_theme_color") || key.equals("switch_preference_dark") ) {
            recreate();
        }
        if (key.equals("lang")) {
            //recreate();
            restartApp2();
        }
    }

    private void restartApp() {
        Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
        int mPendingIntentId = 1;
        PendingIntent mPendingIntent = PendingIntent.getActivity(getApplicationContext(), mPendingIntentId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager) getApplicationContext().getSystemService(SettingsActivity.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
        System.exit(0);
    }

    private void restartApp2() {
        //this.finishAffinity();
        SettingsActivity.this.finish();
        Intent intent = new Intent(this, SettingsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(intent);

    }
//включение темной темы
public void setDarkTheme() {
    colorDrawable1 = new ColorDrawable ();
    colorDrawable1.setColor(Color.parseColor("#282828"));
    actionBar.setBackgroundDrawable(colorDrawable1);
    window.setStatusBarColor(ContextCompat.getColor(this,R.color.StatusBar_Dark));
    //включаем темный стиль
    setTheme(R.style.AppThemeDark_Pref);
}
    //при нажатии кнопки назад этот код позволяет корректно отображать переключение темы
    //за счет удаления задачи и создания новой задачи вместе с запуском main activity
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if (tag_policy_view == 0) {//если мы не на экране политики конфиденциальности, тогда выходим
            FinishAndMainActivity();
        }
        else {//если в политике, то запускаем заново экран настроек
            getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
            tag_policy_view = 0;
        }
    }

    public void FinishAndMainActivity () {
        finish();
        Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

      public void onClickTest (View view) {
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragmentPolicy()).commit();
        tag_policy_view = 1;

    }

   }
