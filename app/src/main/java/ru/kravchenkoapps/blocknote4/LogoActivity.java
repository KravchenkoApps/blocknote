package ru.kravchenkoapps.blocknote4;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LogoActivity extends Activity {
    Animation AnimLogo, AnimCompany ;//переменные самой анимации
    ImageView logoImage; // переменная картинки, которую будем анимировать
    ImageView companyImage;
    private int startPoint = 0;
    private SharedPreferences def_pref;
    Boolean dark_switch;//переменная включения темной темы ночью
    ColorDrawable colorDrawable1;
    String c_dark;
    View logoConst;
    int dark_time = 0;//маркер темного времени суток, 0 - нет, 1 - да


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        def_pref = PreferenceManager.getDefaultSharedPreferences(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logo_activity);
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
        c_dark = getString(R.string.c_dark);
        logoConst = (View) findViewById(R.id.logo_const_layout);
        init();
        startMainActivity();

    }

    private void init() {
// Загружаем анимации в переменные
        AnimLogo = AnimationUtils.loadAnimation(LogoActivity.this,R.anim.logo_anim);//логотип
        AnimCompany = AnimationUtils.loadAnimation(LogoActivity.this,R.anim.company_anim);//картинка
//привязываем к нашим переменным для кнопки и картинки реальные кнопку и картинку с нашего logo_activity.xml
        logoImage = findViewById(R.id.imageLogo);
        companyImage = findViewById(R.id.imageCompany);
//Запускаем анимацию
        logoImage.startAnimation(AnimLogo);
        companyImage.startAnimation(AnimCompany);
    }

    private void startMainActivity () //функция для автоматического запуска MainActivity
    /***В основном потоке нам нельзя делать Thread.sleep(2000),
     * но мы можем создать параллельный поток и "усыпить" его на какое-то время,
     * а потом запустить код на переход в MainActivity***/

    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1500);//время задержки
                    if (startPoint == 0) {
                        startActivity();
                    }
                } catch (InterruptedException e) { //ловим ошибки, обязательно, можно создать код этот через красную лампочку при нажатии на sleep
                    e.printStackTrace();
                }

            }
        }).start();
    }
    private void startActivity() {
        Intent i = new Intent(LogoActivity.this, MainActivity.class); //указываем откуда куда перемещаемся
        startActivity(i); //запускаем Intent
        LogoActivity.this.finish();//после запуска Intent закрываем Logo_Activity
    }

    public void onLogoClick (View view) {
        startActivity();
        startPoint = 1;
    }
    public void onCompanyClick (View view) {
        startActivity();
        startPoint = 1;
    }
    public void onEndImageClick (View view) {
        startActivity();
        startPoint = 1;
    }

    private void settings_themes() {
        String theme_color = def_pref.getString("key_theme_color", getString(R.string.c_blue));//подвязываем ключ из preference_screen.xml к def_pref и size
        dark_switch = def_pref.getBoolean("switch_preference_dark", false);
        if (theme_color != null & !dark_switch ||theme_color != null & dark_switch & dark_time == 0) {
            Log.w("MY", "Код не исполнился");
            colorDrawable1 = new ColorDrawable();


            if (theme_color.equals(c_dark)) {
                colorDrawable1.setColor(Color.parseColor("#282828"));

                setTheme(R.style.AppThemeDark);//устанавливаем стиль
                //устанавливаем фон, т.к. стиль не меняет фон на View, а тут constr...layout почему то View
                logoConst.setBackgroundColor(ContextCompat.getColor(this, R.color.ThemeDark_fon));
                logoImage.setImageResource(R.drawable.block_note_logo2_dark);

            }
        }
//иначе включаем темную тему
        else {
            colorDrawable1 = new ColorDrawable();
            colorDrawable1.setColor(Color.parseColor("#282828"));

            setTheme(R.style.AppThemeDark);//устанавливаем стиль
            //устанавливаем фон, т.к. стиль не меняет фон на View, а тут constr...layout почему то View
            logoConst.setBackgroundColor(ContextCompat.getColor(this, R.color.ThemeDark_fon));
            logoImage.setImageResource(R.drawable.block_note_logo2_dark);
        }
    }

    @Override
    protected void onResume() {
        settings_themes();
        super.onResume();

    }

}
