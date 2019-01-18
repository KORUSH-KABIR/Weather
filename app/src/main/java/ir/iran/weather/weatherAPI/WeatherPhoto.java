package ir.iran.weather.weatherAPI;

import ir.iran.weather.R;

public class WeatherPhoto {

    private static String[] cloudy = {
            "Cloudy" ,
            "tornado" ,
            "tropical storm" ,
            "hurricane" ,
            "foggy" ,
            "cloudy" ,
            "mostly cloudy (night)" ,
            "mostly cloudy (day)" ,
            "partly cloudy (night)" ,
            "partly cloudy (day)" ,
            "partly cloudy"
    };

    private static String[] rainy = {
            "severe thunderstorms" ,
            "thunderstorms" ,
            "mixed rain and hail" ,
            "isolated thunderstorms" ,
            "scattered thunderstorms" ,
            "scattered showers" ,
            "thundershowers" ,
            "isolated thundershowers"
    };

    private static String[] sunny = {
            "cold" ,
            "sunny" ,
            "fair (night)" ,
            "clear (night)" ,
            "clear" ,
            "fair (day)" ,
            "hot"
    };

    private static String[] wind = {
            "dust" ,
            "haze" ,
            "smoky" ,
            "windy" ,
            "blustery"
    };

    private static String[] snowy = {
            "mixed rain and snow" ,
            "mixed rain and sleet" ,
            "mixed snow and sleet" ,
            "freezing drizzle" ,
            "freezing rain" ,
            "showers" ,
            "snow flurries" ,
            "light snow showers" ,
            "blowing snow" ,
            "snow" ,
            "hail" ,
            "drizzle" ,
            "sleet" ,
            "heavy snow" ,
            "snow showers"
    };

    /**
     * آیکون متن مورد نظر را برمیگرداند
     */
    public static int getPhotoWeather(String text){

        if (isTrue(cloudy , text)){
            return R.drawable.icon_cloudy;
        }
        else if (isTrue(rainy , text)){
            return R.drawable.icon_rainy;
        }
        else if (isTrue(sunny , text)){
            return R.drawable.icon_sunny;
        }
        else if (isTrue(wind , text)){
            return R.drawable.icon_windy;
        }
        else if (isTrue(snowy , text)){
            return R.drawable.icon_snowy;
        }
        else {
            return R.drawable.icon_snowy;
        }
    }

    /**
     * در این متد برسی میشود که آیا این متن ورودی در لیست های نوشته شده وجود دارد یا خیر
     * و در صورت وجود درجا مقدار true و در غیر این صورت مقدار false را برمیگرداند
     */
    private static boolean isTrue(String[] list , String text){

        for(String aList : list){
            boolean isTrue = aList.equalsIgnoreCase(text);
            if(isTrue){
                return true;
            }
        }

        return false;
    }
}
