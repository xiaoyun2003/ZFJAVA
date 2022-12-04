

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HTTP{
    public URL u;
    public HttpURLConnection h;
    public Exception e;
    public HashMap<String,String> header;
    HTTP (String url) {
        try {
            this.u=new URL(url);
        } catch (MalformedURLException ex) {
            this.e=e;
        }

    }
    public String get() {
        String res="";
        try {
            this.h= (HttpURLConnection) this.u.openConnection();
            this.h.setInstanceFollowRedirects(false);
            if (this.header!=null ){
                for (String k : this.header.keySet()) {
                    this.h.setRequestProperty(k, this.header.get(k));
                }
            }
            this.h.setRequestMethod("GET");
            BufferedReader in=new BufferedReader(new InputStreamReader(this.h.getInputStream(),"UTF-8"));
            String line;
            while((line=in.readLine())!=null){
                res+=line;
            }
            in.close();
            return res;
        } catch (Exception e) {
            this.e=e;
            return "";
        }
    }
    public boolean setHeader(HashMap<String,String> header){
        if (header!=null ){
            this.header=header;
            return true;
        }
        return false;
    }

    public String post(String data){
        String res="";
        try {
            this.h= (HttpURLConnection) this.u.openConnection();
            this.h.setInstanceFollowRedirects(false);
            if (this.header!=null ){
                for (String k : this.header.keySet()) {
                    this.h.setRequestProperty(k, this.header.get(k));
                }
            }
            this.h.setRequestMethod("POST");
            this.h.setDoInput(true);
            this.h.setDoOutput(true);
            OutputStream ou= this.h.getOutputStream();
            ou.write(data.getBytes());
            BufferedReader in=new BufferedReader(new InputStreamReader(this.h.getInputStream(),"UTF-8"));
            String line;
            while((line=in.readLine())!=null){
                res+=line;
            }
            in.close();
            return res;
        } catch (Exception e) {
            this.e=e;
            return "";
        }

    }
    public Map<String, List<String>> getResponseHeader(){
        return this.h.getHeaderFields();
    }

    public String getCookie(){
        return this.h.getHeaderField("Set-Cookie");
    }
    public int getCode(){
        try {
            return this.h.getResponseCode();
        } catch (IOException ex) {
            this.e=ex;
            return -1;
        }
    }

}