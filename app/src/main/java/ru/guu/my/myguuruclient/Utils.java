package ru.guu.my.myguuruclient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Инал on 15.03.2015.
 */
public class Utils {


    public static String getTimeRange(String startTime, String finishTime){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        try{
            Date startDate = sdf.parse(startTime);
            Date finishDate = sdf.parse(finishTime);
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
            return formatter.format(startDate) + " - " + formatter.format(finishDate);
        }catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static int getTodayWeekDayNumber(){
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        return day - 1; //Sunday == 0, Saturday == 6
    }
}
