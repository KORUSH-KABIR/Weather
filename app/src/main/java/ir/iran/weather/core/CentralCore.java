package ir.iran.weather.core;

import android.app.Application;

import ir.iran.weather.R;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class CentralCore extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Font(); // کار تغییر فونت برنامه را برعهده دارد
    }

    private void Font(){
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("exo2_bold.otf") // آدرس فونت
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }

}
