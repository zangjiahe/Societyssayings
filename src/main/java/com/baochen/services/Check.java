package com.baochen.services;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.baochen.services.DBCon.MyClose;

public class Check  {

    CloseableHttpClient httpClient = HttpClients.createDefault();
    private DBCon db;
    private Connection con;
    private PreparedStatement ps;
    private ResultSet rs;
    public Check() {
        super();
        db = new DBCon();
        con = db.getConnection();
    }

    public void getSay(){
        //创建httpGet
        HttpGet httpGet = new HttpGet("https://cdn.ipayy.net/says/api.php");
        //http响应
        httpGet.setHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
//        httpGet.setHeader("","");
        httpGet.setHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36");
        CloseableHttpResponse response = null;
        try {
            //执行请求
            response = httpClient.execute(httpGet);
            //判断返回状态是否ok
            if (response.getStatusLine().getStatusCode() == 200) {
                //获取实体
                HttpEntity entity = response.getEntity();
                //获取内容
                String content = EntityUtils.toString(entity,"utf-8");
                System.out.println(content);
                if (content!=null&&content!=""){
                    System.out.println(cheackSay(content));
                    if (!cheackSay(content)){
                        saveSay(content);
                    }
                }
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
//            try {
////                httpClient.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }
    }


public boolean cheackSay(String say){
        boolean flag=false;
        try {
            ps = con.prepareStatement("select say from say where say like '%"+say+"%'");
            rs = ps.executeQuery();

            if (rs.next()){
                flag=true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
//            MyClose(rs, ps, con);
        }
    return flag;
}

    public boolean saveSay(String say){
        boolean flag=true;
        try {
            ps = con.prepareStatement("insert into say (id,say) values (null,?)");

            ps.setString(1, say);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
//            MyClose(rs, ps, con);
        }
        return flag;
    }



}
