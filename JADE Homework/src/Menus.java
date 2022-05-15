import java.util.HashMap;

public class Menus {
    public static String[][] Turkish = {{"kebap", "fasulye"}};

    public static String[] turkishMenu1 = {"kebab", "fasulye"};
    public static String[] turkishMenu2 = {"kebab", "bezelye"};
    public static String[] turkishMenu3 = {"fasulye", "bezelye"};

    public static String[][] turkishMenus = {turkishMenu1, turkishMenu2, turkishMenu3};

    public static HashMap<String, String[][]> Cuisines = new HashMap<String, String[][]>() {
        { put("turkish", turkishMenus); }
    };
}
