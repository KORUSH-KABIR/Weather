package ir.iran.weather.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import ir.aid.library.Frameworks.setup.SetupActivity;
import ir.aid.library.Frameworks.utils.SharedPreferenceUtils;
import ir.iran.weather.R;

/*
 برای این که صفحه به پشت آیکون های نوار ابزار هم برود *
 و تمام صفحه نیز باشد از کلاس SetupActivity که از *
 کتابخانه korush kabir گرفته شده است استفاده میکنیم *
 */
public class EnterCityActivity extends SetupActivity {

    private static final String CITY_KEY = "City Location";
    private SharedPreferenceUtils preference;
    private EditText edtCity;
    private Button btnSaveCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_city);
        preference = new SharedPreferenceUtils(this);
        setNotificationBar();
        init();
    }

    @Override
    public void setNotificationBar() {
        super.setNotificationBar();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
    }

    private void init() {

        edtCity = findViewById(R.id.edtCity);
        btnSaveCity = findViewById(R.id.btnSaveCity);

        initButton();
    }

    private void initButton(){

        btnSaveCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edtCity.getText().toString().equals("")){
                    edtCity.setError("City is Empty");
                }
                else {

                    preference.writeString(
                            CITY_KEY,
                            edtCity.getText().toString()
                    );

                    Intent intent = new Intent();
                    setResult(Activity.RESULT_OK , intent);
                    finish();
                    overridePendingTransition(R.anim.animate_enter_to_activity, R.anim.animate_exit_of_activity);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        // تا زمانی که شهر را وارد نکنید اجازه ورود به برنامه را ندارد
        Toast.makeText(this, "Please Enter Your City", Toast.LENGTH_SHORT).show();
    }
}
