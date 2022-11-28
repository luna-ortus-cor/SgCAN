package unused;

import java.util.HashMap;

public class Guardian {
    private String ID;
    private String username;
    private String fn;
    private String ln;
    private String mobile;
    private String address;
    private String pw;
    private String users;

    public Guardian(String ID, String username, String fn, String ln, String mobile, String address, String pw, String users){
        this.ID = ID;
        this.username = username;
        this.fn = fn;
        this.ln = ln;
        this.mobile = mobile;
        this.address = address;
        this.pw = pw;
        this.users = users;
    }

    public HashMap<String, String> createGObject(){
        HashMap<String, String> hm = new HashMap<>();
        hm.put("ID", this.ID);
        hm.put("username", this.username);
        hm.put("fn", this.fn);
        hm.put("ln", this.ln);
        hm.put("mobile", this.mobile);
        hm.put("address", this.address);
        hm.put("pw", this.pw);
        hm.put("users", this.users);
        return hm;
    }
}
