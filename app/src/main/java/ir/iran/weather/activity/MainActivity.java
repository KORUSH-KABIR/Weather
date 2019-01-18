package ir.iran.weather.activity;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.AnimRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;

import ir.aid.library.Frameworks.helper.ConnectionHelper;
import ir.aid.library.Frameworks.setup.SetupActivity;
import ir.aid.library.Frameworks.utils.SharedPreferenceUtils;
import ir.aid.library.Interfaces.OnGetResponse;
import ir.iran.weather.R;
import ir.iran.weather.adapter.ForecastsAdapter;
import ir.iran.weather.weatherAPI.WeatherPhoto;
import ir.iran.weather.weatherAPI.YahooWeatherAPI;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.ScaleInTopAnimator;

public class MainActivity extends SetupActivity {

    private static final String CITY_KEY = "City Location";
    private static final int SETTINGS_REQUEST_CODE = 100;
    private boolean toolbarState = false;
    private int reconnectCount = 0;
    private SharedPreferenceUtils preference;
    private CardView toolbar;
    private RelativeLayout layoutToolbarBase , layoutToolbarSearch;
    private ImageView iconText , exit , search , closeToolbarCity , btnSearch;
    private TextView temp , city , textWeather , speed , humidity , sunrise , sunset;
    private EditText edtCity;
    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private MaterialDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preference = new SharedPreferenceUtils(this); // ساخت یک نمونه از این کلاس برای استفاده از پرفرنس
        init(); // تما ویو های داخل صفحه xml را find میکند
    }

    /**
     * مقدار قبلی پرفرنس را برسی میکند
     */
    private void initCity(){

        if(getCity().equals("")){ // برسی میکند که آیا شهری از قبل در پرفرنس ذخیره شده است یا خیر
            initIntent(); // در صورت عدم وجود شهر به اکتیویتی بعدی میرود
        }
        else {
            getResponseFromServer(getCity()); // اگر شهر وجود داشت پس اطلاعات آن را بارگیری میکند
        }
    }

    /**
     * اطلاعات داخل پرفرنس شهر را برمیگرداند
     */
    private String getCity(){
        return preference.readString(
                CITY_KEY,
                ""
        );
    }

    private void init(){

        layoutToolbarBase      = findViewById(R.id.layoutToolbarBase);
        layoutToolbarSearch    = findViewById(R.id.layoutToolbarSearch);
        toolbar                = findViewById(R.id.toolbar);
        search                 = findViewById(R.id.search);
        edtCity                = findViewById(R.id.edtCity);
        closeToolbarCity       = findViewById(R.id.closeToolbarCity);
        btnSearch              = findViewById(R.id.btnSearch);
        recyclerView           = findViewById(R.id.recyclerView);
        temp                   = findViewById(R.id.temp);
        city                   = findViewById(R.id.city);
        iconText               = findViewById(R.id.iconText);
        textWeather            = findViewById(R.id.textWeather);
        speed                  = findViewById(R.id.speed);
        humidity               = findViewById(R.id.humidity);
        sunrise                = findViewById(R.id.sunrise);
        sunset                 = findViewById(R.id.sunset);
        fab                    = findViewById(R.id.fab);
        exit                   = findViewById(R.id.exit);

        recyclerView.setLayoutManager(new LinearLayoutManager(
                this , LinearLayoutManager.VERTICAL , false));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new ScaleInTopAnimator());
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy > 0){
                    fab.hide();
                }
                else if(dy < 0){
                    fab.show();
                }
            }
        });

        initItems();
    }

    private void getResponseFromServer(String city){

        dialog.show();

        if(toolbarState){
            toolbarAnimationStart();
        }

        String url = "http://phoenix-iran.ir/Files_php_App/WeatherApi/newApiWeather.php";

        new ConnectionHelper(url , 5000)
                .addStringRequest("city" , city)
                .addStringRequest("unit" , "c")
                .getResponse(new OnGetResponse() {
                    @Override
                    public void notConnection(final String result) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(reconnectCount < 5){
                                    reconnectCount ++;
                                    initCity();
                                }
                                else {
                                    dialog.dismiss();
                                    Toast.makeText(MainActivity.this, result + " in 5 request. use of refresh button.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }

                    @Override
                    public void success(final String result) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(result.equalsIgnoreCase("null")){
                                    Toast.makeText(MainActivity.this, "the city in question was not found", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                    initIntent();
                                }
                                else {
                                    Gson gson = new Gson();
                                    YahooWeatherAPI api = gson.fromJson(result , YahooWeatherAPI.class);
                                    initItemsData(api);
                                }
                            }
                        });
                    }

                    @Override
                    public void nullable(String result) {

                    }
                });
    }

    private void initItemsData(YahooWeatherAPI api){

        ForecastsAdapter adapter = new ForecastsAdapter(this, api);

        String cityName = api.getResult().getLocation().getCity();
        String countryName = api.getResult().getLocation().getCountry();
        String cityText = cityName + " , " + countryName;

        String conditionTemp = String.valueOf((int) api.getResult().getCondition().getTemperature());

        String speedSky = String.valueOf(api.getResult().getWind().getSpeed()) + " km/h";
        String humiditySky = String.valueOf((int) api.getResult().getAtmosphere().getHumidity()) + " %";

        String sunriseTime = api.getResult().getAstronomy().getSunrise();
        String sunsetTime = api.getResult().getAstronomy().getSunset();

        String weatherText = api.getResult().getCondition().getText();

        ///////////////////////////////////

        dialog.dismiss(); // بعد از گرفتن تمام اطلاعات دیالوگ را میبندد

        ///////////////////////////////////

        city.setText(cityText);
        setAnimateForViews(city , R.anim.animate_city_title); // تنظیم انیمیشن برای این ویو

        iconText.setImageDrawable(getResources().getDrawable(WeatherPhoto.getPhotoWeather(weatherText)));
        setAnimateForViews(iconText , R.anim.animate_icon_sky); // تنظیم انیمیشن برای این ویو

        textWeather.setText(weatherText);
        setAnimateForViews(textWeather , R.anim.animate_text_sky); // تنظیم انیمیشن برای این ویو

        temp.setText(conditionTemp);
        setAnimateForViews(temp , R.anim.animate_temp); // تنظیم انیمیشن برای این ویو

        speed.setText(speedSky);
        humidity.setText(humiditySky);
        sunrise.setText(sunriseTime);
        sunset.setText(sunsetTime);

        //////// RecyclerView Animation ////////

        ScaleInAnimationAdapter animationAdapter = new ScaleInAnimationAdapter(adapter);
        animationAdapter.setDuration(200);
        animationAdapter.setHasStableIds(true);
        animationAdapter.setFirstOnly(false);
        animationAdapter.setStartPosition(500);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(new ScaleInAnimationAdapter(animationAdapter));
    }

    /**
     * برای ویو یک انیمیشن تنظیم میکند
     */
    private void setAnimateForViews(View view , @AnimRes int animate){
        view.startAnimation(AnimationUtils.loadAnimation(this , animate));
    }

    private void initItems(){

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getResponseFromServer(getCity());
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toolbarAnimationStart();
            }
        });

        closeToolbarCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toolbarAnimationStart();
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(edtCity.getText().toString().equals("")){
                    edtCity.setError("please first enter your city");
                }
                else {
                    preference.writeString(
                            CITY_KEY,
                            edtCity.getText().toString()
                    );

                    getResponseFromServer(getCity());
                }
            }
        });

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void toolbarAnimationStart(){

        toolbar.animate().setDuration(500)
                .translationY(20)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        toolbarAnimationRotateOne();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                })
                .start();
    }

    private void toolbarAnimationRotateOne(){

        toolbar.animate().setDuration(220)
                .rotationX(90)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {

                        if(toolbarState){
                            layoutToolbarSearch.setVisibility(View.GONE);
                            layoutToolbarBase.setVisibility(View.VISIBLE);
                            edtCity.setText("");
                            toolbarState = false;
                        }
                        else{
                            layoutToolbarSearch.setVisibility(View.VISIBLE);
                            layoutToolbarBase.setVisibility(View.GONE);
                            toolbarState = true;
                        }

                        toolbarAnimationRotateTwo();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                })
                .start();
    }

    private void toolbarAnimationRotateTwo(){

        toolbar.animate().setDuration(220)
                .rotationX(0)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        toolbarAnimationEnd();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                })
                .start();
    }

    private void toolbarAnimationEnd(){

        toolbar.animate().setDuration(200)
                .translationY(0)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {

                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                })
                .start();
    }

    private void initDialog(){

        MaterialDialog.Builder builder = new MaterialDialog.Builder(this)
                .customView(R.layout.dialog_wait, false)
                .cancelable(false)
                .autoDismiss(false);

        dialog = builder.build();
    }

    private void initIntent(){
        startActivityForResult(
                new Intent(MainActivity.this , EnterCityActivity.class),
                SETTINGS_REQUEST_CODE
        );
        overridePendingTransition(R.anim.animate_enter_to_activity, R.anim.animate_exit_of_activity);
    }

    @Override
    protected void onStart() {
        super.onStart();
        setNotificationBar();
        initDialog();
        initCity();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SETTINGS_REQUEST_CODE &&
                resultCode == Activity.RESULT_OK){

            getResponseFromServer(getCity());
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
    }

    @Override
    public void setNotificationBar() {
        super.setNotificationBar();
    }

    private boolean exitState = false;
    @Override
    public void onBackPressed() {

        if(!exitState){
            exitState = true;

            Toast.makeText(this, "please try again for exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exitState = false;
                }
            } , 2000);
        }
        else {
            super.onBackPressed();
        }
    }
}
