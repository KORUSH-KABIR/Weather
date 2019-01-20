package ir.iran.weather.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import ir.iran.weather.R;
import ir.iran.weather.weatherAPI.WeatherPhoto;
import ir.iran.weather.weatherAPI.YahooWeatherAPI;
import ir.iran.weather.weatherAPI.models.Forecasts;

public class ForecastsAdapter extends RecyclerView.Adapter<ForecastsAdapter.ViewHolder> {

    private Context context;
    private List<Forecasts> models; // ساخت یک لیست از مقادیر آب و هوای آینده
    private LayoutInflater layoutInflater;

    public ForecastsAdapter(Context context , YahooWeatherAPI api) {
        this.context = context;
        this.models = api.getResult().getForecasts(); // گرفتن لیست از api
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // تنظیم لایه ای که باید برای هر سطر نمایش دهد
        View view = layoutInflater.inflate(R.layout.layout_rec_forecast , parent , false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        // // گرفتن اطلاعات از لیست و نمایش آن روی ویو ها

        String tempHighValue = String.valueOf(models.get(position).getHigh());
        String tempLowValue  = String.valueOf(models.get(position).getLow());
        String tempDateValue = getDate(models.get(position).getDate());
        String tempTextValue = models.get(position).getText();
        String tempDayValue  = models.get(position).getDay();

        holder.tempHigh.setText(tempHighValue);
        holder.tempLow.setText(tempLowValue);
        holder.date.setText(tempDateValue);
        holder.day.setText(tempDayValue);
        holder.imgWeather.setImageDrawable(context.getResources()
                .getDrawable(WeatherPhoto.getPhotoWeather(
                        tempTextValue
                )));
    }

    /**
     * وظیفه تبدیل تاریخ از unixTime به قابل نمایش برای کاربر
     */
    @SuppressLint("SimpleDateFormat")
    private String getDate(long time){

        Date date = new Date(time * 1000L);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+03:30"));

        return simpleDateFormat.format(date);
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tempLow , tempHigh , date , day;
        private ImageView imgWeather;

        ViewHolder(View itemView) {
            super(itemView);
            imgWeather = itemView.findViewById(R.id.imgWeather);
            tempLow = itemView.findViewById(R.id.tempLow);
            tempHigh = itemView.findViewById(R.id.tempHigh);
            date = itemView.findViewById(R.id.date);
            day = itemView.findViewById(R.id.day);
        }
    }

}
