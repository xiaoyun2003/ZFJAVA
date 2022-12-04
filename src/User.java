
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;

public class User {
    //学号
    public String user;
    //密码
    public String pwd;
    //姓名
    public String name;
    //登录凭证
    public String cookie;
    public String URL;
    //登录码 成功200 失败 201
    public int LoginCode;

    public String LoginMsg;
    //设置url
    public boolean setURL(String url){
        if(url==null || url.isEmpty()){
            return false;
        }
        this.URL=url;
        return true;
    }




}
