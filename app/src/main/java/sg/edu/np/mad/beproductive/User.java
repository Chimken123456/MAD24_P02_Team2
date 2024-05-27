package sg.edu.np.mad.beproductive;

import java.util.jar.Attributes;

public class User {
    private int id;
    private String name;
    private String password;
    private String email;

    public int getId()
    {
        return id;
    }
    public void setId(int i)
    {
        id = i;
    }

    public void setName(String n)
    {
        name = n;
    }

    public String getName()
    {
        return name;
    }

    public void setPassword(String p)
    {
        password= p;
    }
    public String getPassword()
    {
        return password;
    }

    public void setEmail(String e)
    {
        email = e;
    }
    public String getEmail()
    {
        return email;
    }

    public User(String n, String p, String e)
    {
        id = 0;
        name = n;
        password = p;
        email = e;

    }
}
