import java.util.HashMap;

public class Restaurant {
    // ------------- MENUS

    private static String[] turkishMenu0 = {"kebap", "fasulye"};
    private static String[] turkishMenu1 = {"kebap", "bezelye"};
    private static String[] turkishMenu2 = {"fasulye", "bezelye"};

    private static String[][] turkishMenus = {turkishMenu0, turkishMenu1, turkishMenu2};

    public static HashMap<String, String[][]> Menus = new HashMap<String, String[][]>() {
        { put("turkish", turkishMenus); }
    };


    // -------------- PRICES

    public static HashMap<String, Double> Prices = new HashMap<String, Double>() {
        {
            put("kebap", 10.0);
            put("bezelye", 6.0);
            put("fasulye", 6.0);



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


