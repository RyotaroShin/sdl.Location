package jp.ac.titech.itpro.sdl.location;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Xml;
import android.view.View;

import com.google.android.gms.common.api.ResultTransform;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;

public class Async extends AsyncTask<Double,Void,Void>{

    private MainActivity activity;
    private Bitmap photo;

    public Async(MainActivity activity){
        this.activity = activity;
    }

    private RestaurantInfo info;


    public RestaurantInfo getJson(double lat, double lng){
        StringBuilder urlStrBuilder = new StringBuilder("https://maps.googleapis.com/maps/api/place/search/json");
        urlStrBuilder.append("?location=" + lat + "," + lng);
        urlStrBuilder.append("&sensor=true&rankby=distance&types=restaurant&key=AIzaSyB7Pp7DhronhUe_aWSPw6ePzcR6AE3Dphg");

        StringBuilder response = new StringBuilder();

        try {

            URL u = new URL(urlStrBuilder.toString());
            HttpsURLConnection con = (HttpsURLConnection) u.openConnection();
            con.setReadTimeout(10000);
            con.setConnectTimeout(15000);
            con.setDoInput(true);
            con.connect();
            InputStream is = con.getInputStream();

            String encoding = con.getContentEncoding();
            if (null == encoding) {
                encoding = "UTF-8";
            }
            final InputStreamReader inReader = new InputStreamReader(is, encoding);
            final BufferedReader bufReader = new BufferedReader(inReader);
            response = new StringBuilder();
            String line = null;
            // 1行ずつ読み込む
            while ((line = bufReader.readLine()) != null) {
                response.append(line);
            }
            bufReader.close();
            inReader.close();
            is.close();
        }catch(IOException e){
            e.printStackTrace();
            System.out.println("aaaaaaa");
        }

            Gson gson = new Gson();
            JsonObject jsonObj = (JsonObject) new Gson().fromJson(response.toString(), JsonObject.class);
            JsonArray jsonArray = jsonObj.get("results").getAsJsonArray();


            List<RestaurantInfo>list = new ArrayList<>();

            for(int i=0;i<jsonArray.size();i++){
                RestaurantInfo tmp = new RestaurantInfo();

                    JsonObject jsn = jsonArray.get(i).getAsJsonObject();
                    JsonObject location = jsn.get("geometry").getAsJsonObject().get("location").getAsJsonObject();
                    tmp.name = jsn.get("name").getAsString();
                    tmp.lat = location.get("lat").getAsDouble();
                    tmp.lng = location.get("lng").getAsDouble();
                try{
                    JsonArray photos = jsn.get("photos").getAsJsonArray();
                    int num = photos.size();
                    Random rnd = new Random();
                    tmp.photo = photos.get(rnd.nextInt(num)).getAsJsonObject().get("photo_reference").getAsString();
                }catch(NullPointerException e){
                    e.printStackTrace();
                }

                if(tmp.name != null){
                    list.add(tmp);
                }
            }
            for(int i=0;i<list.size();i++){
                System.out.println(list.get(i));
            }
            Random rnd = new Random();
            int num = rnd.nextInt(list.size());
            info = list.get(num);
            Bitmap photo = getPhoto(info.photo);

            return info;




    }

    private Bitmap getPhoto(String ref){
        if(ref == null)return null;
        if(ref == "")return null;
        StringBuilder urlStrBuilder = new StringBuilder("https://maps.googleapis.com/maps/api/place/photo");
        urlStrBuilder.append("?photoreference="+ref);
        urlStrBuilder.append("&maxwidth=400&key=AIzaSyB7Pp7DhronhUe_aWSPw6ePzcR6AE3Dphg");

        try {
            URL u = new URL(urlStrBuilder.toString());
            HttpsURLConnection con = (HttpsURLConnection) u.openConnection();
            con.setReadTimeout(10000);
            con.setConnectTimeout(15000);
            con.setDoInput(true);
            con.connect();
            InputStream is = con.getInputStream();
            Bitmap bit = BitmapFactory.decodeStream(is);

            return bit;

        }catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }


    @Override
    protected Void doInBackground(Double... args) {
        double lat = args[0];
        double lng = args[1];

        RestaurantInfo info = getJson(lat,lng);
        System.out.println("nu");
//        System.out.println(info);
//        System.out.println(info.name);

        this.activity.restaurantInfo = info;

        photo = getPhoto(info.photo);
        this.activity.photo = photo;


        return null;
    }

    @Override
    protected void onPostExecute(Void v){
        this.activity.textView.setText(info.name);
        this.activity.image.setImageBitmap(photo);
        this.activity.goButton.setVisibility(View.VISIBLE);
    }

}

