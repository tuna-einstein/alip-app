package analogclock.widget.usp.com.analogclockwidget;

import android.content.Context;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by umasankar on 11/24/14.
 */
public class MailCard {

    public static String EMAIL_BODY =
            "http://maps.google.com/?q=%f,%f \n"
                    + "%s , %s, %s"
                    + "\n \n"
                    + "Weather Report: \n"
                    + "Temperature : %s \n"
                    + "Humidity    : %.2f %% \n"
                    + "Pressure    : %.2f hPa \n"
                    + "Wind speed  : %s \n \n"
                    + "%s \n\n"
                    + "https://play.google.com/store/apps/details?id=%s";
    private static final char DEGREE = '\u00B0';

    public static String getEmailBody(Context context, MyLocation location) {
        double temperature = location.weatherInfo.temperature;
        String temp = String.format("%.2f" + DEGREE + "F (%.2f" + DEGREE + " C)", temperature * 9 / 5 + 32, temperature);

        double windSpeed = location.weatherInfo.windSpeed;
        String ws = String.format("%.2f miles/sec, %s \n(%.2f km/s)", windSpeed/1.6,
                location.weatherInfo.windDirection + DEGREE, windSpeed);


        return String.format(EMAIL_BODY, location.latitude, location.longitude,
                location.streetAddress, location.city, location.country,
                temp,
                location.weatherInfo.humidity,
                location.weatherInfo.pressure,
                ws,
                DateFormat.getDateTimeInstance().format(new Date()),
                context.getPackageName());

    }
}
