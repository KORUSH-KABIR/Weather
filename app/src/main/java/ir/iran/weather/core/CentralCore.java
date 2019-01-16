package ir.iran.weather.core;

import android.app.Application;
import android.util.Log;

import ir.iran.weather.R;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class CentralCore extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Font();
    }

    private void Font(){
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("exo2_bold.otf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }

    public static void tag(String text){
        Log.d("CentralCore", text);
    }

}
