package smart2g.dyx.com.a2gplug;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Constants {

    public static final String SWITCH_ACTION = "SWITCH_ACTION";

    public static final String TIMER_ACTION = "TIMER_ACTION";

    public static final String RESPONSE_SYSTEM = "RESPONSE_SYSTEM";

    public static final String RESPONSE_TIMER = "RESPONSE_TIMER";

    public static final String RESPONSE_SWITCH = "RESPONSE_SWITCH";

    public static final String REPORT_SWITCH = "REPORT_SWITCH";

    public static final String REPORT_TIMER = "REPORT_TIMER";

    public static final String REPORT_SYSTEM = "REPORT_SYSTEM";

    public static final String RESPONSE_TIME = "RESPONSE_TIME";

    public static final String SWITCH = "SWITCH";

    public static final String TIMER = "TIMER";

    public static String mac = "865533039132174";

    //865533039132174  865533030627446

    public static final String MAIN_LINK = "http://182.92.110.42:8091/";

    public static boolean isNumber(String cardNum) {
        Pattern pattern = Pattern.compile("[0-9]{1,}");
        Matcher matcher = pattern.matcher((CharSequence) cardNum);
        boolean result=matcher.matches();
        return result;
    }

}
