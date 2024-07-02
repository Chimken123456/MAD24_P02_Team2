package sg.edu.np.mad.beproductive;

public class Global {
    public static String user_Id;
    private static String usernum;

    public static String getUser_Id() {
        return user_Id;
    }

    public static void setUser_Id(String user_Id) {
        Global.user_Id = user_Id;
    }

    public static String getUsernum() {
        return usernum;
    }

    public static void setUsernum(String usernum) {
        Global.usernum = usernum;
    }

}
