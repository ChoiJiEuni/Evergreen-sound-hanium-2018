package com.microsoft.projectoxford.face.samples.db;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.microsoft.projectoxford.face.samples.R;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DBMainActivity extends AppCompatActivity {
    private static String IP_ADDRESS = "14.63.195.105"; // 한이음 서버 IP
    private static String TAG = "php";
    String userName="B_tester";
    String userPass="1111";
    String DatabaseName ="B_db";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dbmain);
    }

    public void onClickInsertDB(View view) {
        Intent intent = new Intent(this,DBphpActivity.class);
        startActivity(intent);
    }

    public void onClickExifInfo(View view) {
        Intent intent = new Intent(this,ExifActivity.class);
        startActivity(intent);
    }

    public void onClickRecord(View view) {
        Intent intent = new Intent(this,RecordActivity.class);
        startActivity(intent);
    }

    public void onClickCreateDB(View view) {
        createDatabaseAndTable task = new createDatabaseAndTable();
        task.execute("http://" + IP_ADDRESS + "/DB.php",userName,userPass,DatabaseName);
    }

    public void onClickCreateTable(View view) {
        createDatabaseAndTable task = new createDatabaseAndTable();
        task.execute("http://" + IP_ADDRESS + "/tableCreate.php",userName,userPass,DatabaseName);
    }

    public void onClickInsertPicInfoValues(View view) {
        String img_path="C://android2/test1";
        String location="경기도 부천시";
        String create_date="20180302";
        String happiness = "44.23";
        String num_of_people = "5";

        insert_picture_info task = new insert_picture_info();
        task.execute("http://" + IP_ADDRESS + "/insert_picture_info.php",userName,userPass,DatabaseName,img_path,location,create_date,happiness,num_of_people);
    }

    public void onClickInsertRegisteredPersonValues(View view) {
        String name="홍길순";
        String person_img_path="C://android/test22";

        insert_registered_person_tb task = new insert_registered_person_tb();
        task.execute("http://" + IP_ADDRESS + "/insert_registered_person_tb.php",userName,userPass,DatabaseName,name,person_img_path);
    }

    public void onClickInsertRecognitionValues(View view) {
        String img_path="C://android2/test1";
        String name="홍길순";

        insert_recognition_tb task = new insert_recognition_tb();
        task.execute("http://" + IP_ADDRESS + "/insert_recognition_tb.php",userName,userPass,DatabaseName,img_path,name);

    }

    class createDatabaseAndTable extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(DBMainActivity.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(),"완료.",Toast.LENGTH_LONG).show();
        }


        @Override
        protected String doInBackground(String... params) {

            String serverURL = (String)params[0];
            String userName = (String)params[1];
            String userPass = (String)params[2];
            String databaseName = (String)params[3];
            String postParameters = "&userName=" + userName+"&userPass=" + userPass+"&databaseName=" + databaseName; // php에 보낼값.

            try {
                // php 가져오기.
                URL url = new URL(serverURL+"");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }


                bufferedReader.close();



                return sb.toString();


            } catch (Exception e) {

                Log.d(TAG, "createDB: Error ", e);
                return new String("Error: " + e.getMessage());
            }

        }
    } // createDatabaseAndTable() end.

    class insert_picture_info extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(DBMainActivity.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(),"완료.",Toast.LENGTH_LONG).show();
        }


        @Override
        protected String doInBackground(String... params) {

            String serverURL = (String)params[0];
            String userName = (String)params[1];
            String userPass = (String)params[2];
            String databaseName = (String)params[3];
            String img_path = (String)params[4];
            String location = (String)params[5];
            String create_date = (String)params[6];
            String happiness = (String)params[7];
            String num_of_people = (String)params[8];

            String postParameters = "&userName=" + userName
                    +"&userPass=" + userPass
                    +"&databaseName=" + databaseName
                    +"&img_path=" + img_path
                    +"&location=" + location
                    +"&create_date=" + create_date
                    +"&happiness=" + happiness
                    +"&num_of_people=" + num_of_people; // php에 보낼값.

            try {
                // php 가져오기.
                URL url = new URL(serverURL+"");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }


                bufferedReader.close();



                return sb.toString();


            } catch (Exception e) {

                Log.d(TAG, "createDB: Error ", e);
                return new String("Error: " + e.getMessage());
            }

        }
    } // insert_picture_info() end.
    class insert_registered_person_tb extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(DBMainActivity.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(),"완료.",Toast.LENGTH_LONG).show();
        }


        @Override
        protected String doInBackground(String... params) {

            String serverURL = (String)params[0];
            String userName = (String)params[1];
            String userPass = (String)params[2];
            String databaseName = (String)params[3];
            String name = (String)params[4];
            String person_img_path = (String)params[5];


            String postParameters = "&userName=" + userName
                    +"&userPass=" + userPass
                    +"&databaseName=" + databaseName
                    +"&name=" + name
                    +"&person_img_path=" + person_img_path; // php에 보낼값.

            try {
                // php 가져오기.
                URL url = new URL(serverURL+"");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }


                bufferedReader.close();



                return sb.toString();


            } catch (Exception e) {

                Log.d(TAG, "createDB: Error ", e);
                return new String("Error: " + e.getMessage());
            }

        }
    } //insert_registered_person_tb() end
    class insert_recognition_tb extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(DBMainActivity.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(),"완료.",Toast.LENGTH_LONG).show();
        }


        @Override
        protected String doInBackground(String... params) {

            String serverURL = (String)params[0];
            String userName = (String)params[1];
            String userPass = (String)params[2];
            String databaseName = (String)params[3];
            String img_path=(String)params[4];
            String name=(String)params[5];

            String postParameters = "&userName=" + userName
                    +"&userPass=" + userPass
                    +"&databaseName=" + databaseName
                    +"&img_path=" + img_path
                    +"&name=" + name; // php에 보낼값.

            try {
                // php 가져오기.
                URL url = new URL(serverURL+"");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }


                bufferedReader.close();



                return sb.toString();


            } catch (Exception e) {

                Log.d(TAG, "createDB: Error ", e);
                return new String("Error: " + e.getMessage());
            }

        }
    } // insert_recognition_tb() end.

}
