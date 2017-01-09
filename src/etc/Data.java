package etc;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Dean on 2/18/2016.
 */
public class
Data {

    static PHPReader php;

    static public String getDate(String date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        date = sdf.format(cal.getTime());
        return date;
    }

    static public String getDateTime(String date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        date = sdf.format(cal.getTime());
        return date;
    }

    public String getTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        Calendar cal = Calendar.getInstance();

        return sdf.format(cal.getTime());
    }
    public static String getMonday(Calendar cal, SimpleDateFormat sdf, String startDate){
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        startDate = sdf.format(cal.getTime());
        return startDate;
    }

    public static String searchDate(String id, String table, String col, String dbName, String result){
        php = new PHPReader();
        php.addVariable("id", id);
        php.addVariable("table", table);
        php.addVariable("col", col);
        php.addVariable("dbName", dbName);
        php.execute("http://1.234.63.165/h2o/searchDate.php");

        try{
           result = php.get().trim();
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }
}
