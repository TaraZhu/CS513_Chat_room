package util;



import server.serverControl.ServerMessageThread;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by tonggezhu on 6/18/17.
 */
public class CharacterUtil {

    public static final String ERROR = "ERROR";
    public static final String SUCCESS = "SUCCESS";

    public static boolean isEmpty (String str) {
        if (str.isEmpty()) {
            return true;
        }
        return false;
    }

    public static boolean isNumber (String str) {
        Pattern pattern = Pattern.compile("^[0-9]*$");
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    public static boolean isPortCorrect (String str) {

        int aux = Integer.valueOf(str);
        if(aux <= 1024 || aux >= 65535 ) {
            return false;
        }
        return true;
    }

    public static boolean isUsernameLegal (String str) {
        if("".equals(str) || str.length() < 3) return false;
        Pattern pattern = Pattern.compile("^[A-Za-z0-9]*$");
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    public static boolean isIPLegal (String str) {
        if("".equals(str) || str.length() < 7 || str.length() > 15) {
            return false;
        }
        if(str.equals("localhost")) {return true;} else {

        String rexp = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";
        Pattern pattern = Pattern.compile(rexp);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();}
    }

    public static boolean isDuplicate (Map<String, ServerMessageThread> map, String username) {
        Set<String> set = map.keySet();
        Iterator iterator = set.iterator();
        while(iterator.hasNext()){
            if(username.equals(iterator.next())) return true;
        }
        return false;
    }

//    public static Integer randomPort() {
//        Random random = new Random();
//        return 1025 + random.nextInt(64511);
//
//    }

}
