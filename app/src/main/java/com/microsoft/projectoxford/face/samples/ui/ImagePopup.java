package com.microsoft.projectoxford.face.samples.ui;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.microsoft.projectoxford.face.samples.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;


public class
ImagePopup extends AppCompatActivity implements OnClickListener{
    private Context mContext = null;
    private final int imgWidth = 320;
    private final int imgHeight = 372;
    private final String IP = "14.63.195.105"; // 서버 접근 IP

    private String userName;
    private String userPass;
    private String databaseName;
    private String imgPath;
    private Bitmap bm;
    MediaPlayer player;

    private String infoMessage;
    private static String TAG = "kwon";
    private String mJsonString;

    private String RECORDED_FILE;//재생될 녹음 파일명
    // private URI mImageCaptureUri;
    ImageView iv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_popup);
        setTitle("선택한 사진 자세히 보기");
        mContext = this;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        /** 전송메시지 */
        Intent i = getIntent();
        Bundle extras = i.getExtras();
        imgPath = extras.getString("filename");

        /** 사용자 정보 및 이미지 정보 가져오기*/
        infoMessage = "";
        SharedPreferences sharedPreferences = getSharedPreferences("USER",MODE_PRIVATE);
        if(!(sharedPreferences.getString("ID","").equals(""))){
            // 사용자 정보 멤버변수에 저장
            userName = sharedPreferences.getString("ID","");
            databaseName = userName+"_db";
            userPass = "1111";
            GetData task = new GetData();
            task.execute("http://"+IP+"/showImageInfo.php", imgPath, userName, userPass, databaseName);
}

    /** 완성된 이미지 보여주기  */
    BitmapFactory.Options bfo = new BitmapFactory.Options();
        bfo.inSampleSize = 2;
                iv = (ImageView)findViewById(R.id.imageView);
                bm = BitmapFactory.decodeFile(imgPath, bfo);
                try{
                ExifInterface exif = new ExifInterface(imgPath);
                int exifOrientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                int exifDegree = exifOrientationToDegrees(exifOrientation);
                bm = rotate(bm, exifDegree);
                }catch(Exception e){

        }
        //Bitmap resized = Bitmap.createScaledBitmap(bm, imgWidth, imgHeight, true);
        iv.setImageBitmap(bm);

        /** 리스트로 가기 버튼 */
        TextView btn1 = (TextView) findViewById(R.id.btn_delete);
        btn1.setOnClickListener(this);
        TextView btn2 = (TextView) findViewById(R.id.btn_startPlay);
        btn2.setOnClickListener(this);
        TextView btn3 = (TextView) findViewById(R.id.btn_stopPlay);
        btn3.setOnClickListener(this);
        TextView btn4 = (TextView) findViewById(R.id.btn_share);
        btn4.setOnClickListener(this);

        /*정보 읽어주기*/

                }
@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public int exifOrientationToDegrees(int exifOrientation)
    {
        if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_90)
        {
            return 90;
        }
        else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_180)
        {
            return 180;
        }
        else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_270)
        {
            return 270;
        }
        return 0;
    }
    public Bitmap rotate(Bitmap bitmap, int degrees)
    {
        if(degrees != 0 && bitmap != null)
        {
            Matrix m = new Matrix();
            m.setRotate(degrees, (float) bitmap.getWidth() / 2,
                    (float) bitmap.getHeight() / 2);

            try
            {
                Bitmap converted = Bitmap.createBitmap(bitmap, 0, 0,
                        bitmap.getWidth(), bitmap.getHeight(), m, true);
                if(bitmap != converted)
                {
                    bitmap.recycle();
                    bitmap = converted;
                }
            }
            catch(OutOfMemoryError ex)
            {
                // 메모리가 부족하여 회전을 시키지 못할 경우 그냥 원본을 반환합니다.
            }
        }
        return bitmap;
    }
//삭제
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_delete:

                // 권: DB에서 삭제
                SharedPreferences sharedPreferences = getSharedPreferences("USER",MODE_PRIVATE);
                if(!(sharedPreferences.getString("ID","").equals(""))){
                    userName = sharedPreferences.getString("ID","");
                    databaseName = userName+"_db";
                    DeletePicture task = new DeletePicture();
                    task.execute("http://" + IP + "/deletePicture.php",
                            userName, userPass, databaseName, //userInfo
                            imgPath);
                }

                //삭제
                File file = new File(imgPath);
                file.delete();
                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);

                intent.setData(Uri.fromFile(file));

                sendBroadcast(intent);

                finish();
                break;
            case R.id.btn_startPlay: //녹음재생
                if (player != null) {
                    player.stop();
                    player.release();
                    player = null;
                }

                //if (RECORDED_FILE != null && !RECORDED_FILE.equals("")) {
                    Toast.makeText(getApplicationContext(), "녹음된 파일을 재생합니다.", Toast.LENGTH_LONG).show();
                    try {
                        player = new MediaPlayer();
                        player.setDataSource(RECORDED_FILE);
                        player.prepare();
                        player.start();
                    } catch (Exception e) {
                        Log.e("SampleAudioRecorder", "Audio play failed.", e);
                    }
                //} else {
                //    Toast.makeText(getApplicationContext(), "녹음된 파일이 없습니다.", Toast.LENGTH_LONG).show();
                //}
                break;
            case R.id.btn_stopPlay:
                if (player == null)
                    return;

                Toast.makeText(getApplicationContext(), "재생이 중지되었습니다.", Toast.LENGTH_LONG).show();

                player.stop();
                player.release();
                player = null;
                break;
            case R.id.btn_share:
                PopupMenu p = new PopupMenu(
                        getApplicationContext(), // 현재 화면의 제어권자
                        v); // anchor : 팝업을 띄울 기준될 위젯
                getMenuInflater().inflate(R.menu.menu, p.getMenu());
                // 이벤트 처리
                p.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getTitle().equals("페이스북")){
                            shareImageFacebook();
                        }
                        else if(item.getTitle().equals("인스타그램")){
                            shareImageInstagram();
                        }
                        return false;
                    }
                });
                p.show(); // 메뉴를 띄우기


                break;
        }
    }

    private class GetData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(ImagePopup.this,
                    "데이터를 가져오는 중입니다", null, true, true);
        }


        @Override
        protected void onPostExecute(String imgInfo) {
            super.onPostExecute(imgInfo);

            progressDialog.dismiss();
            Log.d(TAG, "response - " + imgInfo);

            /*정상적으로 정보를 가져왔을 경우 결과값을 삽입*/
            if (imgInfo == null){
                infoMessage = errorString;
            }
            else {
                mJsonString = imgInfo;
                showResult();
            }
        }


        @Override
        protected String doInBackground(String... params) {

            String serverURL = params[0];
            String userName = (String)params[2];
            String userPass = (String)params[3];
            String databaseName = (String)params[4];

            String postParameters = "img_path=" + params[1]
                    +"&userName=" + userName
                    +"&userPass=" + userPass
                    +"&databaseName=" + databaseName;


            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "response code - " + responseStatusCode);

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
                String line;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }

                bufferedReader.close();

                return sb.toString().trim();

            } catch (Exception e) {

                Log.d(TAG, "GetData : Error ", e);
                errorString = e.toString();

                return null;
            }
        }
    }


    private void showResult(){

        String TAG_JSON="evergreen";
        String TAG_LOC = "location";
        String TAG_DATE = "date";
        //String TAG_HAP ="happiness";
        String TAG_CNT ="personCount";
        String TAG_KNOW ="personKnowCount";
        String TAG_NAME = "personName";
        String TAG_REC = "record";

       // String TAG_PROPORTION = "ficial_proportion";
        //String TAG_FACE = "face";


        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for(int i=0;i<jsonArray.length();i++){

                JSONObject item = jsonArray.getJSONObject(i);

                String location = item.getString(TAG_LOC);
                String date = item.getString(TAG_DATE);
                //String happiness = item.getString(TAG_HAP);
                String personCount = item.getString(TAG_CNT);
                String personKnowCount = item.getString(TAG_KNOW);
                String name = item.getString(TAG_NAME);
                RECORDED_FILE = item.getString(TAG_REC);

                // 얼굴비율 값 가져오기

                // 얼굴 화면 넘어갔는지 값 가져오기

                if (!location.equals("N")) {
                    if (!location.equals(""))
                        infoMessage += location + "에서 ";
                    infoMessage += date.substring(0, 4) + "년 " + date.substring(4, 6) + "월 " + date.substring(6,8) + "일에 ";
                    int unknownCount = Integer.parseInt(personCount) - Integer.parseInt(personKnowCount);
                    if (unknownCount != Integer.parseInt(personCount)) {
                        infoMessage += name + "외 ";
                    }
                    infoMessage += unknownCount + "명과 찍은 사진입니다.";
                } else {
                    infoMessage = "저장되지 않은 사진입니다.";
                }

                Log.d(TAG, infoMessage);
                //채윤 이미지에 설명추가
                iv.setContentDescription(infoMessage);
                Toast.makeText(getApplicationContext(), infoMessage, Toast.LENGTH_LONG).show();
            }

        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }

    }


    public void shareImageFacebook(){
        Bitmap image = bm;
        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(image)
                .build();
        SharePhotoContent content = new SharePhotoContent.Builder()
                .addPhoto(photo)
                .build();
        ShareDialog shareDialog = new ShareDialog(this);
        shareDialog.show(content, ShareDialog.Mode.AUTOMATIC);
    }
    public void shareImageInstagram(){
        PackageManager manager = getBaseContext().getPackageManager();
        Intent i = manager.getLaunchIntentForPackage("com.instagram.android");
        if(i==null){
            i=new Intent(Intent.ACTION_VIEW,Uri.parse("marker://details?id=com.instagram.android"));
            startActivity(i); return;
        }
        String type="image/*";

       Intent shareIntent = new Intent(Intent.ACTION_SEND);
       shareIntent.setType(type);
       shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
       File media = new File(imgPath);
       Uri uri = Uri.fromFile(media);
       shareIntent.putExtra(Intent.EXTRA_STREAM,uri);
       shareIntent.setPackage("com.instagram.android");
       startActivity(shareIntent);
    }

    //*/ 권: DB에서 이미지 삭제
    class DeletePicture extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(ImagePopup.this,
                    "사진을 삭제하는 중입니다.", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
        }


        @Override
        protected String doInBackground(String... params) {

            String serverURL = (String)params[0];
            String userName = (String)params[1];
            String userPass = (String)params[2];
            String databaseName = (String)params[3];
            String img_path=(String)params[4];

            String postParameters = "&userName=" + userName
                    +"&userPass=" + userPass
                    +"&databaseName=" + databaseName
                    +"&img_path=" + img_path;

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

                Log.d(TAG, "deleteFromDB: Error ", e);
                return new String("Error: " + e.getMessage());
            }

        }
    } // deletePicture() end.
/*
    private void sendMMS() {
        try {
            ContentResolver contentR = this.getContentResolver();

            Uri uri = Uri.parse("" + imgPath);
            OutputStream outstream;
            try {
                outstream = contentR.openOutputStream(uri);
                bm.compress(Bitmap.CompressFormat.JPEG, 1000, outstream);
                outstream.close();
            } catch (Exception e) {
                System.err.println(e.toString());
            }
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "Image Choose"), 1);
           // this.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "failed", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
*/

/*
    private void getImage(){
        Uri u= Uri.parse("mmsto: 01012345678"); //sms 문자와 관련된 Data는 'smsto:'로 시작. 이후는 문자를 받는 사람의 전화번호
        Intent i= new Intent(Intent.ACTION_SENDTO,u); //시스템 액티비티인 SMS문자보내기 Activity의 action값
        i.putExtra("mms_body", );  //보낼 문자내용을 추가로 전송, key값은 반드시 'sms_body'
        startActivity(i);//액티비티 실행
        } */
/*
    // 소희 : 문자로 공유하기
    public void onButtonMsg() {
       // getImage();
    }
*/
}