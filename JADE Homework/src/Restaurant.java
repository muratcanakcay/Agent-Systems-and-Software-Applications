import java.util.HashMap;

public class Restaurant {
    // ------------- MENUS

    private static String[] turkishMenu0 = {"kebap", "fasulye"};
    private static String[] turkishMenu1 = {"kebap", "bezelye"};
    private static String[] turkishMenu2 = {"fasulye", "bezelye"};

    private static String[][] turkishMenus = {turkishMenu0, turkishMenu1, turkishMenu2};

    private static String[] japaneseMenu0 = {"czang chi", "long wu"};
    private static String[] japaneseMenu1 = {"long wu", "myrtsa"};
    private static String[] japaneseMenu2 = {"proyt la", "munst wi"};

    private static String[][] japaneseMenus = {japaneseMenu0, japaneseMenu1, japaneseMenu2};

    public static HashMap<String, String[][]> Menus = new HashMap<String, String[][]>() {
        {
            put("turkish", turkishMenus);
            put("japanese", japaneseMenus);
        }
    };


    // -------------- PRICES

    public static HashMap<String, Double> Prices = new HashMap<String, Double>() {
        {
            put("kebap", 10.0);
            put("bezelye", 6.0);
            put("fasulye", 6.0);
            put("czang chi", 16.0);
            put("long wu", 20.0);
            put("proyt la", 13.0);
            put("munst wi", 6.0);
        }
    };

    // ------------- WORKING HOURS

    private static Integer[] workingHours0 = {1000, 1715};
    private static Integer[] workingHours1 = {1800, 2000};
    private static Integer[] workingHours2 = {1300, 2300};

    public static Integer[][] WorkingHours =  {
            workingHours0,
            workingHours1,
            workingHours2,
    };
}


