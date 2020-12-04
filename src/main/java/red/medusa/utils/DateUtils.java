package red.medusa.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author huguanghui
 * @since 2020/11/27 周五
 */
public class DateUtils {

    public static String dateTime(Date date) {
        if (date == null)
            return "N/A";
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return f.format(date);
    }
}



















