


import java.io.*;
import java.math.BigInteger;


import java.net.URLEncoder;
import java.security.KeyFactory;
import java.security.PublicKey;

import java.security.spec.RSAPublicKeySpec;


import java.util.Base64;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alibaba.fastjson.*;

import javax.crypto.Cipher;

//教务处系统接口
public class ZF {
    public String URL="http://218.197.80.10";
    public HashMap<String,String> dataxk=new HashMap<String,String>();
    public User Login(String user,String pwd){
        //构建用户个人信息类
        User us=new User();
        us.user=user;
        us.pwd=pwd;
        us.URL=this.URL;
        try{
        HTTP http=  new HTTP(this.URL+"/xtgl/login_getPublicKey.html?time=1662376193387&_=1662376193304");

        String RSA_STR=http.get();
            if(http.e!=null){
                us.LoginCode=201;
                us.LoginMsg="连接RSA服务端错误";
                return us;
            }

        if(RSA_STR.isEmpty()){
            us.LoginCode=201;
            us.LoginMsg="RSA密钥获取错误";
            return us;
        }
        JSONObject RSA_Pub=JSON.parseObject(RSA_STR);
        //加密
        String m=RSA_Pub.getString("modulus");
        String e= RSA_Pub.getString("exponent");
        BigInteger mb = new BigInteger(Base64.getDecoder().decode(m));
        BigInteger eb = new BigInteger(Base64.getDecoder().decode(e));
        String epwd="";
            //x509编码
            RSAPublicKeySpec spec = new RSAPublicKeySpec(mb,eb);
            KeyFactory KF = KeyFactory.getInstance("RSA");
            PublicKey pub = KF.generatePublic(spec);
            Cipher ci = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            ci.init(Cipher.ENCRYPT_MODE, pub);
            int inputLen = pwd.getBytes().length;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int offset = 0;
            byte[] cache;
            int i = 0;
            // 对数据分段加密
            while (inputLen - offset > 0) {
                if (inputLen - offset > 128) {
                    cache = ci.doFinal(pwd.getBytes(), offset, 128);
                } else {
                    cache = ci.doFinal(pwd.getBytes(), offset, inputLen - offset);
                }
                out.write(cache, 0, cache.length);
                i++;
                offset = i * 128;
            }
            byte[] encryptedData = out.toByteArray();
            out.close();
            epwd=new String(Base64.getEncoder().encode(encryptedData));

        String cookie=http.getCookie();
        HashMap<String,String> header=new HashMap<String,String>();
        header.put("Cookie",cookie);
        header.put("Content-Type","application/x-www-form-urlencoded");
        http=  new HTTP(this.URL+"/xtgl/login_slogin.html?time=1662386627257");

            http.setHeader(header);
            http.post("csrftoken=&yhm="+user+"&language=zh_CN&mm="+ URLEncoder.encode(epwd,"utf-8"));
            if(http.e!=null){
                us.LoginCode=201;
                us.LoginMsg="二次登录错误";
                return us;
            }

            //登录成功
            if(http.getCode()==302){
                us.LoginCode=200;
                us.LoginMsg="登陆成功";
                us.cookie=http.getCookie();
                return us;
            }else{
                us.LoginMsg="用户名不存在或密码错误";
                us.LoginCode=201;
                return us;
            }
        } catch (Exception e) {
            us.LoginMsg=e.getLocalizedMessage();
            us.LoginCode=203;
            return us;
        }
    }
    public void setUrl(String url){
        this.URL=url;
    }
    public MyClass[] getMyClasses(User u,String xnm,String xqm){
        try {
            HTTP http = new HTTP(u.URL + "/kbcx/xskbcx_cxXsgrkb.html?gnmkdm=N2151&su=" + u.user);
            HashMap<String, String> header = new HashMap<String, String>();
            header.put("Cookie", u.cookie);
            header.put("Content-Type", "application/x-www-form-urlencoded");
            http.setHeader(header);
            String res = http.post("xnm=" + xnm + "&xqm=" + xqm + "&kzlx=ck");
            JSONObject JS = JSON.parseObject(res);
            if (JS != null) {
                u.name = JS.getJSONObject("xsxx").getString("XM");
                JSONArray JSA = JS.getJSONArray("kbList");
                if (JSA != null) {
                    MyClass classes[] = new MyClass[JSA.size()];
                    for (int i = 0; i < JSA.size(); i++) {
                        MyClass c = new MyClass();
                        JSONObject j = JSA.getJSONObject(i);
                        c.cdmc = j.getString("cdmc");
                        c.jcs = j.getString("jcs");
                        c.xm = j.getString("xm");
                        c.jxbzc = j.getString("jxbzc");
                        c.kcmc = j.getString("kcmc");
                        c.kcxszc = j.getString("kcxszc");
                        c.xqj = j.getString("xqj");
                        c.zcd = j.getString("zcd");
                        classes[i] = c;
                    }
                    return classes;
                }
            }
        }catch(Exception e){
            System.out.println("[error]"+e.getLocalizedMessage());
            return null;
        }
        return null;
    }
    public HashMap<String,String> getHeadDataXK(User u,String xxkz_id){
        try {
            HTTP http = new HTTP(u.URL + "/xsxk/zzxkyzb_cxZzxkYzbDisplay.html?gnmkdm=N253512&su=" + u.user);
            HashMap<String, String> map = new HashMap<String, String>();
            HashMap<String, String> header = new HashMap<String, String>();
            header.put("Cookie", u.cookie);
            header.put("Content-Type", "application/x-www-form-urlencoded");
            http.setHeader(header);
            String res = http.post("xkkz_id=" + xxkz_id + "&xszxzt=" + this.dataxk.get("xszxzt")+ "&kspage=0&jspage=0");
            Pattern r = Pattern.compile("<input type=\"hidden\" name=\"(.*?)\" id=\"(.*?)\" value=\"(.*?)\"/>");
            Matcher m = r.matcher(res);
            while (m.find()) {
                if (m.group(2).indexOf("<") == -1) {
                    map.put(m.group(2), m.group(3));
                }
            }
            return map;
        }catch(Exception e){
            System.out.println(e.getLocalizedMessage());
            return null;
        }
    }
public CourseXK[] getCoursesXKByHomeData(HashMap<String,String> map){
        if (map==null){
            map=this.dataxk;
    }
        try{
            String c[]=map.get("COURSES").split(";");
            String d[]=map.get("COURSES_DM").split(";");
            String n[]=map.get("COURSES_NAME").split(";");
            CourseXK[] cs=new CourseXK[c.length];
            for(int i=0;i<c.length;i++){
                CourseXK c1=new CourseXK();
                c1.xxkz_id=c[i];
                c1.dm=d[i];
                c1.name=n[i];
                cs[i]=c1;
            }
            return cs;
        }catch(Exception e){
            return new CourseXK[0];
    }

}
    public boolean getHomeDataXK(User u){
        try {
            HashMap<String,String> map=new HashMap<String,String>();
            HTTP http = new HTTP(u.URL + "/xsxk/zzxkyzb_cxZzxkYzbIndex.html?gnmkdm=N253512&layout=default&su=" + u.user);
            HashMap<String, String> header = new HashMap<String, String>();
            header.put("Cookie", u.cookie);
            header.put("Content-Type", "application/x-www-form-urlencoded");
            http.setHeader(header);
            String res = http.get();
            //<li><a href="javascript:void(0)" onclick="queryCourse(this,'06','EE19B7BE0DD20A04E0530B50C5DAFCD4','2022','1506')" role="tab" data-toggle="tab">板块课(大学体育2)</a></li>

            Pattern r = Pattern.compile("<input type=\"hidden\" name=\"(.*?)\" id=\"(.*?)\" value=\"(.*?)\"/>");
            Pattern r1=Pattern.compile("queryCourse\\(this,'(.*?)','(.*?)','(.*?)','(.*?)'\\)\" role=\"tab\" data-toggle=\"tab\">(.*?)</a></li>");

            Matcher m = r.matcher(res);
            Matcher m1 = r1.matcher(res);
            while(m.find() ){
               if(m.group(2).indexOf("<")==-1) {
                   map.put(m.group(2), m.group(3));
               }
            }
            String COURSES="";
            String COURSES_DM="";
            String COURSES_NAME="";
            while(m1.find() ){
                if(m1.group(2).indexOf("<")==-1) {
                    COURSES+=";"+m1.group(2);
                    COURSES_DM+=";"+m1.group(1);
                    COURSES_NAME+=";"+m1.group(5);
                }
            }
            COURSES=COURSES.replaceFirst(";","");
            COURSES_DM=COURSES_DM.replaceFirst(";","");
            COURSES_NAME=COURSES_NAME.replaceFirst(";","");
            map.put("COURSES", COURSES);
            map.put("COURSES_DM", COURSES_DM);
            map.put("COURSES_NAME", COURSES_NAME);
            this.dataxk=map;
            return true;
        }catch(Exception e){
            System.out.println("[error]"+e.getLocalizedMessage());
            return false;
        }
    }
    //获取前置数据
    public HashMap<String,String>  getDataXK(){
      return this.dataxk;
    }
    public ClassXK[] getClassesXK(User u,CourseXK cxk){
        try {
            this.dataxk.putAll(getHeadDataXK(u,cxk.xxkz_id));
            this.dataxk.put("kklxdm",cxk.dm);
            HTTP http = new HTTP(u.URL + "/xsxk/zzxkyzb_cxZzxkYzbPartDisplay.html?gnmkdm=N253512&su=" + u.user);
            if(http.e!=null){
                System.out.println(http.e.getLocalizedMessage());
                return null;
            }
            HashMap<String, String> header = new HashMap<String, String>();
            header.put("Cookie", u.cookie);
            header.put("Content-Type", "application/x-www-form-urlencoded");
            http.setHeader(header);
            //一系列可怕的变量

            String jg_id = this.dataxk.get("jg_id_1");
            String njdm_id_1 =this.dataxk.get("njdm_id_1");
            String zyh_id_1 = this.dataxk.get("zyh_id_1");
            String zyh_id =this.dataxk.get("zyh_id");
            String bh_id =this.dataxk.get("bh_id");
            String xslbdm = this.dataxk.get("xslbdm");
            String ccdm =this.dataxk.get("ccdm");
            String xsbj =this.dataxk.get("xsbj");
            String xkxnm = this.dataxk.get("xkxnm");
            String xkxqm =this.dataxk.get("xkxqm");
            String kklxdm =this.dataxk.get("kklxdm");
            String njdm_id = this.dataxk.get("njdm_id");
            String bklx_id=this.dataxk.get("bklx_id");
            String rwlx=this.dataxk.get("rwlx");

            String data="rwlx=" + rwlx + "&xkly=0&bklx_id=" + bklx_id + "&sfkkjyxdxnxq=0&xqh_id=1&jg_id=" + jg_id + "&njdm_id_1=" + njdm_id_1 + "&zyh_id_1=" + zyh_id_1 + "&zyh_id=" + zyh_id + "&zyfx_id=wfx&njdm_id=" + njdm_id + "&bh_id=" + bh_id + "&xbm=1&xslbdm=" + xslbdm + "&ccdm=" + ccdm + "&xsbj=" + xsbj + "&sfkknj=0&sfkkzy=0&kzybkxy=0&sfznkx=0&zdkxms=0&sfkxq=0&sfkcfx=0&kkbk=0&kkbkdj=0&sfkgbcx=0&sfrxtgkcxd=0&tykczgxdcs=0&xkxnm=" + xkxnm + "&xkxqm=" + xkxqm + "&kklxdm=" + kklxdm + "&bbhzxjxb=0&rlkz=0&xkzgbj=0&kspage=1&jspage=10&jxbzb=";

            String res = http.post(data);
            if(res.isEmpty()){
                System.out.println("[error]:this res from http is empty");
                return null;
            }
            JSONObject JS = JSON.parseObject(res);
            JSONArray JSA=JS.getJSONArray("tmpList");
            if(JSA== null || JSA.size()==0 ){
                System.out.println("[error]JSA size is 0");
                System.out.println(res);
                return null;
            }
            ClassXK[] classesxk=new ClassXK[JSA.size()];
            for(int i=0;i<JSA.size();i++){
                ClassXK clxk=new ClassXK();
                JSONObject JS1=JSA.getJSONObject(i);
                clxk.jxb_id=JS1.getString("jxb_id");
                clxk.jxbmc=JS1.getString("jxbmc");
                clxk.jxbzls=JS1.getString("jxbzls");
                clxk.kch=JS1.getString("kch");
                clxk.kch_id=JS1.getString("kch_id");
                clxk.kcmc=JS1.getString("kcmc");
                clxk.kcrow=JS1.getString("kcrow");
                clxk.kklxdm=JS1.getString("kklxdm");
                clxk.kzmc=JS1.getString("kzmc");
                clxk.rwlx=this.dataxk.get("rwlx");
                clxk.xkkz_id=cxk.xxkz_id;


                classesxk[i]=clxk;
            }
            return classesxk;
        }catch(Exception e){
            System.out.println(e.getLocalizedMessage());
            return null;
        }

    }
    public JXBXK[] getJXBsXK(User u,ClassXK cxk){
        try{
            HTTP http = new HTTP(u.URL + "/xsxk/zzxkyzbjk_cxJxbWithKchZzxkYzb.html?gnmkdm=N253512&su=" + u.user);
            if(http.e!=null){
                System.out.println(http.e.getLocalizedMessage());
                return null;
            }
            HashMap<String, String> header = new HashMap<String, String>();
            header.put("Cookie", u.cookie);
            header.put("Content-Type", "application/x-www-form-urlencoded");
            http.setHeader(header);
            //一系列可怕的变量

            String jg_id = this.dataxk.get("jg_id_1");
            String zyh_id =this.dataxk.get("zyh_id");
            String bh_id =this.dataxk.get("bh_id");
            String xslbdm = this.dataxk.get("xslbdm");
            String ccdm =this.dataxk.get("ccdm");
            String xsbj =this.dataxk.get("xsbj");
            String xkxnm = this.dataxk.get("xkxnm");
            String xkxqm =this.dataxk.get("xkxqm");
            String kklxdm =cxk.kklxdm;
            String njdm_id = this.dataxk.get("njdm_id");
            String bklx_id=this.dataxk.get("bklx_id");

            String rwlx= cxk.rwlx;
            String kch_id=cxk.kch_id;
            String xkkz_id=cxk.xkkz_id;

            String data="rwlx=" + rwlx + "&xkly=0&bklx_id=" + bklx_id + "&sfkkjyxdxnxq=0&xqh_id=1&jg_id=" + jg_id +  "&zyh_id=" + zyh_id + "&zyfx_id=wfx&njdm_id=" + njdm_id + "&bh_id=" + bh_id + "&xbm=1&xslbdm=" + xslbdm + "&ccdm=" + ccdm + "&xsbj=" + xsbj + "&sfkknj=0&sfkkzy=0&kzybkxy=0&sfznkx=0&zdkxms=0&sfkxq=0&sfkcfx=0&kkbk=0&kkbkdj=0&xkxnm="+xkxnm+"&xkxqm="+xkxqm+"&xkxskcgskg=0&rlkz=0&kklxdm="+kklxdm+"&kch_id="+kch_id+"&jxbzcxskg=0&xkkz_id="+xkkz_id+"&cxbj=0&fxbj=0" ;


            String res = http.post(data);
            if(res.isEmpty()){
                System.out.println("[error]:this res from http is empty");
                return null;
            }


            JSONArray JSA = JSON.parseArray(res);

            if(JSA== null || JSA.size()==0 ){
                System.out.println("[error]JSA size is 0");
                System.out.println(res);
                return null;
            }
            JXBXK[] jxbsxk=new JXBXK[JSA.size()];
            for(int i=0;i<JSA.size();i++){
                JXBXK jxbxk=new JXBXK();
                JSONObject JS1=JSA.getJSONObject(i);
                jxbxk.do_jxb_id=JS1.getString("do_jxb_id");
                jxbxk.fxbj=JS1.getString("fxbj");
                jxbxk.jgpxzd=JS1.getString("jgpxzd");
                jxbxk.jsxx=JS1.getString("jsxx");
                jxbxk.jxdd=JS1.getString("jxdd");
                jxbxk.jxbrl=JS1.getString("jxbrl");
                jxbxk.jxms=JS1.getString("jxms");
                jxbxk.kclbmc=JS1.getString("kclbmc");
                jxbxk.kcxzmc=JS1.getString("kcxzmc");
                jxbxk.kkxymc=JS1.getString("kkxymc");
                jxbxk.sksj=JS1.getString("sksj");
                jxbxk.xqumc=JS1.getString("xqumc");
                jxbxk.year=JS1.getString("year");
                jxbxk.kch_id=cxk.kch_id;
                jxbxk.kklxdm=cxk.kklxdm;
                jxbsxk[i]=jxbxk;
            }
            return jxbsxk;
        }catch(Exception e){
            System.out.println(e.getLocalizedMessage());

            return null;
        }

    }
    public String selectClass(User u,JXBXK jxb){
        HTTP http = new HTTP(u.URL + "/xsxk/zzxkyzb_xkBcZyZzxkYzb.html?gnmkdm=N253512&su=" + u.user);
        HashMap<String, String> header = new HashMap<String, String>();
        header.put("Cookie", u.cookie);
        header.put("Content-Type", "application/x-www-form-urlencoded");
        http.setHeader(header);

        String jxb_ids=jxb.do_jxb_id;
        String kch_id=jxb.kch_id;
        String qz="0";
        String xkxnm=this.dataxk.get("xkxnm");
        String xkxqm=this.dataxk.get("xkxqm");
        String njdm_id=this.dataxk.get("njdm_id");
        String zyh_id=this.dataxk.get("zyh_id");
        String kklxdm=jxb.kklxdm;

        String res=http.post("jxb_ids="+jxb_ids+"&kch_id="+kch_id+"&qz="+qz+"&xkxnm="+xkxnm+"&xkxqm="+xkxqm+"&njdm_id="+njdm_id+"&zyh_id="+zyh_id+"&kklxdm="+kklxdm);
        if(http.e!=null || res.isEmpty()){
            System.out.println(http.e.getLocalizedMessage());
            return null;
        }
return res;
    }


}


