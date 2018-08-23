package com.microsoft.projectoxford.face.samples.db;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.microsoft.projectoxford.face.samples.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class SearchResultctivity extends AppCompatActivity {
    String myJSON;
    public Bitmap b;
    private static final String TAG_RESULTS="evergreen";
    private static final String TAG_PATH = "path";
    JSONArray peoples = null;
    ArrayList<HashMap<String, String>> personList;
    ArrayList<HashMap<String, Bitmap>> personListBit;
    ListView list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_resultctivity);

        list = (ListView) findViewById(R.id.listView);
        personList = new ArrayList<HashMap<String,String>>();
        personListBit = new ArrayList<HashMap<String,Bitmap>>();
        Intent intent = getIntent();
        String img_date= intent.getStringExtra("img_date");
        String img_location= intent.getStringExtra("img_location");
        String img_person= intent.getStringExtra("img_person");

        GetDataJSON task = new GetDataJSON();
        task.execute("http://14.63.195.105/showTest.php",img_date,img_location,img_person);
    } // onCreate() END.

    protected void showList(){
        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            peoples = jsonObj.getJSONArray(TAG_RESULTS);

            for(int i=0;i<peoples.length();i++){
                JSONObject c = peoples.getJSONObject(i);
                String path = c.getString(TAG_PATH);

                HashMap<String,String> persons = new HashMap<String,String>();
                HashMap<String,Bitmap> personBitmaps = new HashMap<String, Bitmap>();
                b = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(path));

                personBitmaps.put(TAG_PATH, b);
                persons.put(TAG_PATH,path);

                //personList.add(persons);
                personListBit.add(personBitmaps);
            }

            ListAdapter adapter = new SimpleAdapter(
                    SearchResultctivity.this, personList, R.layout.search_item,
                    new String[]{TAG_PATH},
                    new int[]{R.id.path}
            );

            list.setAdapter(adapter);

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    } // showList() END.


    class GetDataJSON extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            String uri = params[0];
            String img_date = params[1];
            String img_location = params[2];
            String img_person = params[3];
            String postParameters = "&img_date=" + img_date
                    +"&img_location=" + img_location
                    +"&img_person=" + img_person; // php에 보낼값.
            BufferedReader bufferedReader = null;
            try {
                URL url = new URL(uri);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setReadTimeout(5000);
                con.setConnectTimeout(5000);
                con.setRequestMethod("POST");
                con.connect();

                OutputStream outputStream = con.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = con.getResponseCode();

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = con.getInputStream();
                }
                else{
                    inputStream = con.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");

                StringBuilder sb = new StringBuilder();

                bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                String json;
                while((json = bufferedReader.readLine())!= null){
                    sb.append(json+"\n");
                }

                return sb.toString().trim();

            }catch(Exception e){
                return null;
            }



        }

        @Override
        protected void onPostExecute(String result){
            myJSON=result;
            showList();
        }
    } // GetDataJSON() END.

}
