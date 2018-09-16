//
// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license.
//
// Microsoft Cognitive Services (formerly Project Oxford): https://www.microsoft.com/cognitive-services
//
// Microsoft Cognitive Services (formerly Project Oxford) GitHub:
// https://github.com/Microsoft/Cognitive-Face-Android
//
// Copyright (c) Microsoft Corporation
// All rights reserved.
//
// MIT License:
// Permission is hereby granted, free of charge, to any person obtaining
// a copy of this software and associated documentation files (the
// "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to
// permit persons to whom the Software is furnished to do so, subject to
// the following conditions:
//
// The above copyright notice and this permission notice shall be
// included in all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED ""AS IS"", WITHOUT WARRANTY OF ANY KIND,
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
// NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
// LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
// OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
// WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
//
package com.microsoft.projectoxford.face.samples.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.contract.Emotion;
import com.microsoft.projectoxford.face.contract.Face;
import com.microsoft.projectoxford.face.contract.IdentifyResult;
import com.microsoft.projectoxford.face.contract.TrainingStatus;
import com.microsoft.projectoxford.face.samples.R;
import com.microsoft.projectoxford.face.samples.db.RecordActivity;
import com.microsoft.projectoxford.face.samples.db.renameLoc_Activity;
import com.microsoft.projectoxford.face.samples.helper.ImageHelper;
import com.microsoft.projectoxford.face.samples.helper.LogHelper;
import com.microsoft.projectoxford.face.samples.helper.SampleApp;
import com.microsoft.projectoxford.face.samples.helper.StorageHelper;

import com.microsoft.projectoxford.face.samples.persongroupmanagement.AddFaceToPersonActivity;
import com.microsoft.projectoxford.face.samples.persongroupmanagement.PersonGroupListActivity;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Member;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

// 유아이 : 소희
public class IdentificationActivity extends AppCompatActivity {

    private static final int RENAME_LOC_INFO= 1689;
    private static int PersonCount=0; // 전체 인원 수
    // HashMap map=new HashMap();////// 희:사람 이름 넣을 해시 맵
    private String PersonName=null; // 인식 된 사람 이름, 해쉬맵에 저장한 걸 여기다가 넣었음

    Float Longitude =  Float.valueOf(0); //경도
    String strLocation = "";
    int inedx=0;

    //*/0825
    private View convertView;
    private float average=0; // 행복도 평균값 => 383번째줄 identify(분석)버튼 누르면 행복도 평균값 계산
    float sum = 0;

    //*/ DB
    Uri imageUri = null;
    private static String IP_ADDRESS = "14.63.195.105"; // 한이음 서버 IP
    private static String TAG = "php";
    String userName="B_tester";
    String userPass="1111";
    String DatabaseName ="B_db";
    String EmotionValue = "";
    Float Latitude = Float.valueOf(0); // 위도
    static  HashMap map=new HashMap();
    static HashMap bitmaps=new HashMap();
    int i =0;
    int count =0;

    // DB picture_info_tb, recognition_tb 이렇게 2개의 테이블의 삽입 작업. >분석버튼 누르고 눌러야함.
    @SuppressLint("NewApi")
    public void DB(View view) {
        //*/ 지은: ExifInterface 생성

        try {
            if(strLocation.equals("")){
                InputStream in; //Uri를 Exif객체 인자로 넣을 수 있게 변환.
                ExifInterface exif = null;
                in = getContentResolver().openInputStream(imageUri);
                exif = new ExifInterface(in); // 사진 상세정보 객체
                in.close();

                Latitude = convertToDegree(exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE).toString()); // 위도
                Longitude = convertToDegree(exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE).toString()); //경도
                strLocation = location(Latitude, Longitude);//*/ 지은: location
            }
        }catch (Exception e){

        }
        // DB 삽입.
        SharedPreferences insert = getSharedPreferences("Picture_info_Pref", MODE_PRIVATE);
        String record_path = insert.getString("record_path","null");

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat mFormat = new SimpleDateFormat("yyyyMMdd");
        String getTime = mFormat.format(date);

        registered_count();

        //*/ 09
        SharedPreferences sharedPreferences = getSharedPreferences("USER",MODE_PRIVATE);
        if(!(sharedPreferences.getString("ID","").equals(""))){
            userName = sharedPreferences.getString("ID","");
            DatabaseName = userName+"_db";
            insert_picture_infoTask task = new insert_picture_infoTask();
            task.execute("http://" + IP_ADDRESS + "/insert_picture_info.php",
                    userName,userPass,DatabaseName,
                    imageUri.getPath(),
                    strLocation,
                    getTime,
                    String.valueOf(average),
                    String.valueOf(PersonCount),
                    record_path);
        }

        Toast.makeText(getApplicationContext(),"저장 되었습니다.",Toast.LENGTH_LONG).show();

        SharedPreferences.Editor editor = insert.edit();
        editor.clear();
        editor.commit();

        for(int i=0;i<inedx;i++){
            if(!(map.get(i).equals(""))){
                String img_path = imageUri.getPath();
                String name=map.get(i).toString();


                try {
                    Thread.sleep(300);
                    insert_recognition_tb task2 = new insert_recognition_tb();
                    task2.execute("http://" + IP_ADDRESS + "/insert_recognition_tb.php",userName,userPass,DatabaseName,img_path,name);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public static HashMap getter1(){ //getter1
        return map;
    }
    public static HashMap getter2(){ //getter2
        return bitmaps;
    }

    //*/ DB 리스트뷰에서 등록된 사람들 이름만 뽑고, 평균까지 구함.
    public void registered_count() {
        inedx=0;
        int count = mFaceListAdapter.getCount();
        List<IdentifyResult> mIdentifyResults = mFaceListAdapter.mIdentifyResults;
        List<Face> faces = mFaceListAdapter.faces;
        List<Bitmap> faceThumbnails = mFaceListAdapter.faceThumbnails; //crop된 이미지 가지고 있음.

        if (mIdentifyResults.size() == faces.size()) {
            DecimalFormat formatter = new DecimalFormat("#0.00");

            for(int position = 0 ; position<count;position++){
                if (mIdentifyResults.get(position).candidates.size() > 0) {
                    String personId =
                            mIdentifyResults.get(position).candidates.get(0).personId.toString();
                    String personName = StorageHelper.getPersonName(
                            personId, mPersonGroupId, IdentificationActivity.this);
                    String per = personName;
                    //PersonName= personName; // 여기서 받은 이름 위에 지정한 전역변수에다가 반환
                    map.put(inedx, per); // 전역변수 PersonName 해시맵에다가 넣기
                    bitmaps.put(inedx,getImageUri(getApplicationContext(), faceThumbnails.get(position)));
                    inedx++;
                    //  PersonName = String.valueOf(map);
                    EmotionValue  = getEmotion(faces.get(position).faceAttributes.emotion);
                    sum += Float.parseFloat(EmotionValue);

                }
            }
        }
        average = sum/map.size();
      /*  SharedPreferences insert = getSharedPreferences("test", MODE_PRIVATE);
        SharedPreferences.Editor editor = insert.edit();
        editor.putInt("index", 0);
        editor.commit(); //완료한다.
        Intent testIntent = new Intent(this,LearningActivity.class);
        startActivity(testIntent);*/
    }
    private Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }



    // Background task of face identification.
    // 얼굴 식별의 백그라운드 작업
    private class IdentificationTask extends AsyncTask<UUID, String, IdentifyResult[]> {
        private boolean mSucceed = true;
        String mPersonGroupId;
        IdentificationTask(String personGroupId) {
            this.mPersonGroupId = personGroupId;
        }

        @Override
        protected IdentifyResult[] doInBackground(UUID... params) {
            String logString = "Request: Identifying faces ";
            for (UUID faceId: params) {
                logString += faceId.toString() + ", ";
            }
            logString += " in group " + mPersonGroupId;
            addLog(logString);

            // Get an instance of face service client to detect faces in image.
            // 얼굴 서비스 클라이언트의 인스턴스를 가져와 이미지에서 얼굴을 감지합니다.
            FaceServiceClient faceServiceClient = SampleApp.getFaceServiceClient();
            try{
                publishProgress("Getting person group status...");

                TrainingStatus trainingStatus = faceServiceClient.getLargePersonGroupTrainingStatus(
                        this.mPersonGroupId);     /* personGroupId */
                if (trainingStatus.status != TrainingStatus.Status.Succeeded) {
                    publishProgress("Person group training status is " + trainingStatus.status);
                    mSucceed = false;
                    return null;
                }

                publishProgress("Identifying...");

                // Start identification.
                // 식별 시작
                return faceServiceClient.identityInLargePersonGroup(
                        this.mPersonGroupId,   /* personGroupId */
                        params,                  /* faceIds */
                        1);  /* maxNumOfCandidatesReturned */
            }  catch (Exception e) {
                mSucceed = false;
                publishProgress(e.getMessage());
                addLog(e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            setUiBeforeBackgroundTask();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            // Show the status of background detection task on screen.a
            setUiDuringBackgroundTask(values[0]);
        }

        @Override
        protected void onPostExecute(IdentifyResult[] result) {
            // Show the result on screen when detection is done.
            setUiAfterIdentification(result, mSucceed);
        }
    }

    // 소히
    // public static Activity AActivity;

    //SelectImageActivity aActivity = (SelectImageActivity)SelectImageActivity.AActivity;

    String mPersonGroupId;

    boolean detected;

    FaceListAdapter mFaceListAdapter;

    PersonGroupListAdapter mPersonGroupListAdapter;



    @Override
    protected void onRestart() {
        super.onRestart();
  /*      SharedPreferences insert = getSharedPreferences("test", MODE_PRIVATE);
        Boolean end = insert.getBoolean("end",false);
        Boolean group = insert.getBoolean("group",false);
        if(group != true){
            if(end == true){
                finish();
            }}*/
    }
    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identification);
        super.setTitle("사진 분석 화면");

        // 소히
        // AActivity = IdentificationActivity.this;
        detected = false;

        progressDialog = new ProgressDialog(this);
        //progressDialog.setTitle(getString(R.string.progress_dialog_title));
        progressDialog.setTitle("기다려 주세요.");

        LogHelper.clearIdentificationLog();

        Intent intent_test = getIntent();
        detected = false;

        // If image is selected successfully, set the image URI and bitmap.
        //Uri imageUri = intent_test.getData(); //*/지은: 찍은 사진 사진 uri
        imageUri = intent_test.getData(); //*/지은: 찍은 사진 사진 uri



        Log.d("chae",imageUri.getPath()+"받은거");
//////////////////////////////////////
       /* if (imageUri != null) {

            try {
                mBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                MediaStore.Images.Media.insertImage(getContentResolver(),mBitmap,"사진","저장");

            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
           // mImageView.setImageBitmap(bm);

        }*/
        ///////////////////////////////
        ///원래코드
        mBitmap = ImageHelper.loadSizeLimitedBitmapFromUri(
                imageUri, getContentResolver());

        //갤러리에 촬영 사진추가
        //MediaStore.Images.Media.insertImage(getContentResolver(),mBitmap,"사진","저장");
        File dir =new File( imageUri.getPath());
        Log.d("chae",dir+"");

        if(!dir.exists())

            dir.mkdirs();
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,imageUri));

        if (mBitmap != null) {
            // Show the image on screen.
            ImageView imageView = (ImageView) findViewById(R.id.image);
            imageView.setImageBitmap(mBitmap);
        }

        // Clear the identification result.
        FaceListAdapter faceListAdapter = new FaceListAdapter(null);
        ListView listView = (ListView) findViewById(R.id.list_identified_faces);
        listView.setAdapter(faceListAdapter);

        // Clear the information panel.
        setInfo("");

        // Start detecting in image.
        detect(mBitmap);


        SharedPreferences insert = getSharedPreferences("test", MODE_PRIVATE);
        SharedPreferences.Editor editor = insert.edit();
        editor.putBoolean("repeat", false);
        editor.commit(); //완료한다.
    }

    @Override
    protected void onResume() {
        super.onResume();

        ListView listView = (ListView) findViewById(R.id.list_person_groups_identify);
        mPersonGroupListAdapter = new PersonGroupListAdapter();
        listView.setAdapter(mPersonGroupListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setPersonGroupSelected(position);
            }
        });

        if (mPersonGroupListAdapter.personGroupIdList.size() != 0) {
            setPersonGroupSelected(0);
        } else {
            setPersonGroupSelected(-1);
        }
    }

    void setPersonGroupSelected(int position) {
        TextView textView = (TextView) findViewById(R.id.text_person_group_selected);
        if (position > 0) {
            String personGroupIdSelected = mPersonGroupListAdapter.personGroupIdList.get(position);
            mPersonGroupListAdapter.personGroupIdList.set(
                    position, mPersonGroupListAdapter.personGroupIdList.get(0));
            mPersonGroupListAdapter.personGroupIdList.set(0, personGroupIdSelected);
            ListView listView = (ListView) findViewById(R.id.list_person_groups_identify);
            listView.setAdapter(mPersonGroupListAdapter);
            setPersonGroupSelected(0);
        } else if (position < 0) {
            setIdentifyButtonEnabledStatus(false);
            textView.setTextColor(Color.RED);
            textView.setText(R.string.no_person_group_selected_for_identification_warning);
        } else {
            mPersonGroupId = mPersonGroupListAdapter.personGroupIdList.get(0);
            String personGroupName = StorageHelper.getPersonGroupName(
                    mPersonGroupId, IdentificationActivity.this);
            refreshIdentifyButtonEnabledStatus();
            textView.setTextColor(Color.BLACK);
            textView.setText(String.format("Person group to use: %s", personGroupName));
        }
    }

    private void setUiBeforeBackgroundTask() {
        progressDialog.show();
    }

    // Show the status of background detection task on screen.
    private void setUiDuringBackgroundTask(String progress) {
        progressDialog.setMessage(progress);

        setInfo(progress);
    }

    // Show the result on screen when detection is done.
    // 검출이 완료되면 화면에 결과를 표시합니다.
    private void setUiAfterIdentification(IdentifyResult[] result, boolean succeed) {
        progressDialog.dismiss();

        setAllButtonsEnabledStatus(true);
        setIdentifyButtonEnabledStatus(false);

        if (succeed) {
            // Set the information about the detection result.
            setInfo("분석이 완료되었습니다.");
            average = sum/map.size(); //분석버튼 누르면 행복도 평균값 계산

            if (result != null) {
                mFaceListAdapter.setIdentificationResult(result);

                String logString = "Response: Success. ";
                for (IdentifyResult identifyResult: result) {
                    logString += "Face " + identifyResult.faceId.toString() + " is identified as "
                            + (identifyResult.candidates.size() > 0
                            ? identifyResult.candidates.get(0).personId.toString()
                            : "Unknown person")
                            + ". ";
                }
                addLog(logString);

                // Show the detailed list of detected faces.
                ListView listView = (ListView) findViewById(R.id.list_identified_faces);
                listView.setAdapter(mFaceListAdapter);

                // 지은: ExifInterface 생성
                try {
                    if(strLocation.equals("")){
                        InputStream in; //Uri를 Exif객체 인자로 넣을 수 있게 변환.
                        ExifInterface exif = null;
                        in = getContentResolver().openInputStream(imageUri);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            exif = new ExifInterface(in); // 사진 상세정보 객체
                        }
                        in.close();

                        Latitude = convertToDegree(exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE).toString()); // 위도
                        Longitude = convertToDegree(exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE).toString()); //경도
                        strLocation = location(Latitude, Longitude);//*/ 지은: location

                        registered_count();
                        StringBuffer names=new StringBuffer();
                        for(int i=0;i<inedx;i++){
                            if(!(map.get(i).equals(""))){
                                String img_path = imageUri.getPath();
                                String name=map.get(i).toString(); // 이건 name에다가 Uri 집어넣는 거니까 필요없고

                                names.append(name+" ");
                            }
                        }

                        if(names.toString().equals("")){
                            Toast.makeText(getApplicationContext(), "등록된 인물이 없습니다. 인물 등록 후 사용해 주세요.",Toast.LENGTH_SHORT).show();
                        }
                        else{

                            SharedPreferences insert = getSharedPreferences("Picture_info_Pref", MODE_PRIVATE);
                            SharedPreferences.Editor editor = insert.edit();
                            editor.putString("location",strLocation);
                            editor.commit();
                            renameLoc();
                           /* int num = PersonCount - (inedx-1);

                            Toast.makeText(getApplicationContext(),"인물: "+names.toString()+"외 "+num+"명 "+"위치: "+strLocation+"촬영 날짜: "+getTime,Toast.LENGTH_LONG).show();*/


                        }
                    }


                }catch (Exception e){

                }
            }
        }
    }

    // Background task of face detection.
    // 얼굴 감지의 백그라운드 작업입니다.
    private class DetectionTask extends AsyncTask<InputStream, String, Face[]> {
        @Override
        protected Face[] doInBackground(InputStream... params) {
            // Get an instance of face service client to detect faces in image.
            FaceServiceClient faceServiceClient = SampleApp.getFaceServiceClient();
            try{
                publishProgress("분석 중...");

                // Start detection.
                return faceServiceClient.detect(
                        params[0],  /* Input stream of image to detect */
                        true,       /* Whether to return face ID */
                        false,       /* Whether to return face landmarks */
                        /* Which face attributes to analyze, currently we support:
                           age,gender,headPose,smile,facialHair */
                        new FaceServiceClient.FaceAttributeType[] {
                                FaceServiceClient.FaceAttributeType.Emotion
                        });
            }  catch (Exception e) {
                publishProgress(e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            setUiBeforeBackgroundTask();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            // Show the status of background detection task on screen.
            setUiDuringBackgroundTask(values[0]);
        }

        @Override
        protected void onPostExecute(Face[] result) {
            progressDialog.dismiss();

            setAllButtonsEnabledStatus(true);

            //PersonCount = result.length
            // Log.d("sohee",PersonCount);

            if (result != null) {

                /////////////희 //*/>지은 : 사진 촬영시 인물 없으면 오류 나길래 if문 바로 위에 있었는데 안으로 집어 넣음.
                PersonCount=result.length; // 인원 수 반환
                Log.d("sohee", String.valueOf(PersonCount));

                // Set the adapter of the ListView which contains the details of detected faces.
                mFaceListAdapter = new FaceListAdapter(result);
                ListView listView = (ListView) findViewById(R.id.list_identified_faces);
                listView.setAdapter(mFaceListAdapter);

                if (result.length == 0) {
                    // 지은 : 이때 재촬영하라고 토스트 띄워야하고
                    detected = false;
                    setInfo("감지된 얼굴이 없습니다! 다시 한 번 촬영해 주세요");
                    Toast.makeText(getApplicationContext(),"감지된 얼굴이 없습니다! 다시 한 번 촬영해 주세요",Toast.LENGTH_LONG).show();
                } else {
                    detected = true;
                    setInfo("\"Identify\" 버튼을 클릭하여 분석을 시작해 주세요.");
                    identify();
                }
            } else {
                detected = false;
            }

            refreshIdentifyButtonEnabledStatus();
        }
    }

    /*희 굳이 메소드까지는 필요없을지도..
    public void getPersonCount(Face[] result){ //인원수 세는 메소드
        PersonCount=result.length; // 이 변수 안에다가 인원 수 집어넣음
        Log.d("hee", String.valueOf(PersonCount));
    }
    */

    // Flag to indicate which task is to be performed.
    private static final int REQUEST_SELECT_IMAGE = 0;

    // The image selected to detect.
    private Bitmap mBitmap;

    // Progress dialog popped up when communicating with server.
    ProgressDialog progressDialog;
    /*
        // Called when image selection is done.
        // 이미지 선택이 완료되면 호출됩니다.
        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            switch (requestCode)
            {
                case REQUEST_SELECT_IMAGE:
                    if(resultCode == RESULT_OK) {
                        detected = false;

                        // If image is selected successfully, set the image URI and bitmap.
                        Uri imageUri = data.getData();


                        mBitmap = ImageHelper.loadSizeLimitedBitmapFromUri(
                                imageUri, getContentResolver());

                        //갤러리에 촬영 사진추가
                        MediaStore.Images.Media.insertImage(getContentResolver(),mBitmap,"사진","저장");



                        if (mBitmap != null) {
                            // Show the image on screen.
                            ImageView imageView = (ImageView) findViewById(R.id.image);
                            imageView.setImageBitmap(mBitmap);
                        }

                        // Clear the identification result.
                        FaceListAdapter faceListAdapter = new FaceListAdapter(null);
                        ListView listView = (ListView) findViewById(R.id.list_identified_faces);
                        listView.setAdapter(faceListAdapter);

                        // Clear the information panel.
                        setInfo("");

                        // Start detecting in image.
                        detect(mBitmap);
                    }
                    break;
                default:
                    break;
            }
        }
    */
    // Start detecting in image.
    // 이미지에서 감지를 시작합니다.
    private void detect(Bitmap bitmap) {
        // Put the image into an input stream for detection.
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());

        setAllButtonsEnabledStatus(false);

        // Start a background task to detect faces in the image.
        new DetectionTask().execute(inputStream);
    }

    // Called when the "Detect" button is clicked.
    // 분석버튼 행위
    public void identify(View view) {
        // Start detection task only if the image to detect is selected.
        // 감지할 이미지를 선택한 경우에만 감지 작업을 시작합니다.
        if (detected && mPersonGroupId != null) {
            // Start a background task to identify faces in the image.
            List<UUID> faceIds = new ArrayList<>();
            for (Face face:  mFaceListAdapter.faces) {
                faceIds.add(face.faceId);
            }

            setAllButtonsEnabledStatus(false);

            new IdentificationTask(mPersonGroupId).execute(
                    faceIds.toArray(new UUID[faceIds.size()]));
        } else {
            // Not detected or person group exists.
            //setInfo("Please select an image and create a person group first.");
            setInfo("이미지를 선택해 주세요. 그리고 인물 그룹을 먼저 생성해 주세요.");
        }
    }
    // 지은 분석버튼 행위 > 버튼 아님.
    public void identify() {
        // Start detection task only if the image to detect is selected.
        // 감지할 이미지를 선택한 경우에만 감지 작업을 시작합니다.
        if (detected && mPersonGroupId != null) {
            // Start a background task to identify faces in the image.
            List<UUID> faceIds = new ArrayList<>();
            for (Face face:  mFaceListAdapter.faces) {
                faceIds.add(face.faceId);
            }

            setAllButtonsEnabledStatus(false);

            new IdentificationTask(mPersonGroupId).execute(
                    faceIds.toArray(new UUID[faceIds.size()]));
        } else {
            // Not detected or person group exists.
            //setInfo("Please select an image and create a person group first.");
            setInfo("이미지를 선택해 주세요. 그리고 인물 그룹을 먼저 생성해 주세요.");
            //*/
            Toast.makeText(getApplicationContext(),"인물 그룹을 먼저 생성해 주세요.",Toast.LENGTH_LONG).show();
        }
    }
    //*/
    public void managePersonGroups(View view) {

        Intent intent = new Intent(this, PersonGroupListActivity.class);
        startActivity(intent);
        refreshIdentifyButtonEnabledStatus();
    }

    public void record(View view) {
        Intent intent = new Intent(this, RecordActivity.class);
        startActivity(intent);
    }

    // Add a log item.
    private void addLog(String log) {
        LogHelper.addIdentificationLog(log);
    }

    // Set whether the buttons are enabled.
    private void setAllButtonsEnabledStatus(boolean isEnabled) {
        Button selectImageButton = (Button) findViewById(R.id.manage_person_groups);
        selectImageButton.setEnabled(isEnabled);

        // Button groupButton = (Button) findViewById(R.id.select_image);
        //groupButton.setEnabled(isEnabled);

        TextView identifyButton = (TextView) findViewById(R.id.identify);
        identifyButton.setEnabled(isEnabled);

        TextView viewLogButton = (TextView) findViewById(R.id.view_log);
        viewLogButton.setEnabled(isEnabled);
    }

    // Set the group button is enabled or not.
    private void setIdentifyButtonEnabledStatus(boolean isEnabled) {
        TextView button = (TextView) findViewById(R.id.identify);
        button.setEnabled(isEnabled);
    }

    // Set the group button is enabled or not.
    private void refreshIdentifyButtonEnabledStatus() {
        if (detected && mPersonGroupId != null) {
            setIdentifyButtonEnabledStatus(true);
        } else {
            setIdentifyButtonEnabledStatus(false);
        }
    }

    // Set the information panel on screen.
    private void setInfo(String info) {
        TextView textView = (TextView) findViewById(R.id.info);
        textView.setText(info);
    }

    // The adapter of the GridView which contains the details of the detected faces.
// 탐지된 얼굴의 세부 정보를 포함하는 그리드 보기의 어댑터입니다.
    private class FaceListAdapter extends BaseAdapter {
        // The detected faces.
        List<Face> faces;

        List<IdentifyResult> mIdentifyResults;

        // The thumbnails of detected faces.
        List<Bitmap> faceThumbnails;

        // Initialize with detection result.
        //리스트에 연결되어있는 어댑터
        FaceListAdapter(Face[] detectionResult) {
            faces = new ArrayList<>();
            faceThumbnails = new ArrayList<>();
            mIdentifyResults = new ArrayList<>();

            if (detectionResult != null) {
                faces = Arrays.asList(detectionResult);
                for (Face face: faces) {
                    try {
                        // Crop face thumbnail with five main landmarks drawn from original image.
                        faceThumbnails.add(ImageHelper.generateFaceThumbnail(
                                mBitmap, face.faceRectangle));
                    } catch (IOException e) {
                        // Show the exception when generating face thumbnail fails.
                        setInfo(e.getMessage());
                    }
                }
            }
        }

        public void setIdentificationResult(IdentifyResult[] identifyResults) {
            mIdentifyResults = Arrays.asList(identifyResults);
        }

        @Override
        public boolean isEnabled(int position) {
            return false;
        }

        @Override
        public int getCount() {
            return faces.size();
        }

        @Override
        public Object getItem(int position) {
            return faces.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            //*/ 텍스트뷰 id 값 = text_detected_face
            //*/ 이미지뷰 id값 = face_thumbnail
            //*/ LayoutInflater한 레이아웃 item_face_with_description
            //*/ item_face_with_description에서 텍스트뷰 배경 색 주었는데 실행시키면 리스트 항목으로 안보임.
            if (convertView == null) {
                LayoutInflater layoutInflater =
                        (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(
                        R.layout.item_face_with_description, parent, false);
            }
            convertView.setId(position);

            // Show the face thumbnail.
            //얼굴 축소 이미지를 보여 줍니다.
            ((ImageView)convertView.findViewById(R.id.face_thumbnail)).setImageBitmap(
                    faceThumbnails.get(position));

            if (mIdentifyResults.size() == faces.size()) {
                // Show the face details.
                //얼굴 세부 정보 표시
                DecimalFormat formatter = new DecimalFormat("#0.00");
                if (mIdentifyResults.get(position).candidates.size() > 0) {
                    String personId =
                            mIdentifyResults.get(position).candidates.get(0).personId.toString();
                    String personName = StorageHelper.getPersonName(
                            personId, mPersonGroupId, IdentificationActivity.this);
                    String per=personName;
                    //PersonName= personName; // 여기서 받은 이름 위에 지정한 전역변수에다가 반환
                    map.put(position,per); // 전역변수 PersonName 해시맵에다가 넣기
                    PersonName=String.valueOf(map);
                    // Log.d("soheeeeeeeeeeee:", PersonName);
                    // Log.d("soheeeeeeeeeeee:", (String) map.get(""));
                    Log.d("Soheeeeee:",PersonName);

                    //*/ 지은: 데베에는 getEmotion(faces.get(position).faceAttributes.emotion)값만 들어가야하는데 2번호출하면 문제 생길까봐 분리시킴.
                    EmotionValue  = getEmotion(faces.get(position).faceAttributes.emotion);
                    //*/sum += Float.parseFloat(EmotionValue);
                    String Emotion = String.format("행복도: "+EmotionValue);
                    //String Emotion = String.format("Happiness: %s", getEmotion(faces.get(position).faceAttributes.emotion));
                    String identity = "사람: " + personName + "\n"
                            + "신뢰도: " + formatter.format(
                            mIdentifyResults.get(position).candidates.get(0).confidence) +"\n"+ Emotion;
                    ((TextView) convertView.findViewById(R.id.text_detected_face)).setText(
                            identity);

                } else {
                    ((TextView) convertView.findViewById(R.id.text_detected_face)).setText(
                            R.string.face_cannot_be_identified);
                }

            }
            //*/ 리스트 항목 선택/터치/클릭 했을때
            final View finalConvertView = convertView; //*/ 이렇게 안하면 이 값 못씀.
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    TextView textView = (TextView) finalConvertView.findViewById(R.id.text_detected_face);
                    //Toast.makeText(getApplicationContext(),textView.getText().toString()+"!",Toast.LENGTH_SHORT).show();
                    String name =  textView.getText().toString() ;


                    //  String personName = StorageHelper.getPersonName(
                    //   personId, mPersonGroupId, IdentificationActivity.this);
                    if(name.equals("Unknown person")){

                        Intent intent = new Intent(IdentificationActivity.this, PersonGroupListActivity.class);
                        startActivity(intent);
                    }
                }
            });
            return convertView;
        }

    }
    //감정정보 – 다양한 감정 중 제일 높은 것을 추출
    //*/ private > public수정 // adapter에서 뺌.:  이 블록 밖에서 계산해야해서 (registered_count)
    public String getEmotion(Emotion emotion)
    {
        String emotionType = "";
        double emotionValue = 0.0;
        emotionValue = emotion.happiness;
        emotionType = "Happiness";
        return String.format("%f", emotionValue);
    }

    // The adapter of the ListView which contains the person groups.
    // 사용자 그룹을 포함하는 ListView의 어댑터입니다.
    private class PersonGroupListAdapter extends BaseAdapter {
        List<String> personGroupIdList;

        // Initialize with detection result.
        PersonGroupListAdapter() {
            personGroupIdList = new ArrayList<>();

            Set<String> personGroupIds
                    = StorageHelper.getAllPersonGroupIds(IdentificationActivity.this);

            for (String personGroupId: personGroupIds) {
                personGroupIdList.add(personGroupId);
                if (mPersonGroupId != null && personGroupId.equals(mPersonGroupId)) {
                    personGroupIdList.set(
                            personGroupIdList.size() - 1,
                            mPersonGroupListAdapter.personGroupIdList.get(0));
                    mPersonGroupListAdapter.personGroupIdList.set(0, personGroupId);
                }
            }
        }

        @Override
        public int getCount() {
            return personGroupIdList.size();
        }

        @Override
        public Object getItem(int position) {
            return personGroupIdList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater layoutInflater =
                        (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.item_person_group, parent, false);
            }
            convertView.setId(position);

            // set the text of the item
            //항목의 텍스트 설정
            String personGroupName = StorageHelper.getPersonGroupName(
                    personGroupIdList.get(position), IdentificationActivity.this);
            int personNumberInGroup = StorageHelper.getAllPersonIds(
                    personGroupIdList.get(position), IdentificationActivity.this).size();
            ((TextView)convertView.findViewById(R.id.text_person_group)).setText(
                    String.format(
                            "%s (Person count: %d)",
                            personGroupName,
                            personNumberInGroup));

            if (position == 0) {
                ((TextView)convertView.findViewById(R.id.text_person_group)).setTextColor(
                        Color.parseColor("#3399FF"));
            }

            return convertView;
        }
    }

    //*/ 지은
    private Float convertToDegree(String stringDMS){
        Float result = null;
        try{
            String[] DMS = stringDMS.split(",", 3);

            String[] stringD = DMS[0].split("/", 2);
            Double D0 = new Double(stringD[0]);
            Double D1 = new Double(stringD[1]);
            Double FloatD = D0/D1;

            String[] stringM = DMS[1].split("/", 2);
            Double M0 = new Double(stringM[0]);
            Double M1 = new Double(stringM[1]);
            Double FloatM = M0/M1;

            String[] stringS = DMS[2].split("/", 2);
            Double S0 = new Double(stringS[0]);
            Double S1 = new Double(stringS[1]);
            Double FloatS = S0/S1;

            result = new Float(FloatD + (FloatM/60) + (FloatS/3600));
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),"위치변환오류",Toast.LENGTH_LONG).show();
        }

        return result;


    } // convertToDegree() end

    //*/ 지은
    public String location(Float Latitude,Float Longitude ){
        final Geocoder geocoder = new Geocoder(this);
        List<Address> list = null;
        try {
            double d1 = Double.parseDouble(String.valueOf(Latitude));
            double d2 = Double.parseDouble(String.valueOf(Longitude));

            list = geocoder.getFromLocation(
                    d1, // 위도
                    d2, // 경도
                    10); // 얻어올 값의 개수
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("log", "입출력 오류 - 서버에서 주소변환시 에러발생");
        }
        if (list != null) {
            if (list.size()==0) {
                return "해당되는 주소 정보는 없습니다";
            } else {
                StringBuffer stringBuffer= new StringBuffer();
                stringBuffer.append(list.get(0).getCountryName()+" ");//국가명
                stringBuffer.append(list.get(0).getLocality()+" ");//구 메인(시)
                if(!(list.get(0).getSubLocality().equals(null))){
                    stringBuffer.append(list.get(0).getSubLocality()+" ");//구 서브데이터
                }
                if(!(list.get(0).getThoroughfare().equals(null))){
                    stringBuffer.append(list.get(0).getThoroughfare()+" ");//동
                }
                return stringBuffer.toString();
            }
        }
        return "";
    } // location() end.

    class insert_picture_infoTask extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(IdentificationActivity.this,
                    "기다려 주세요.", null, true, true);
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
            String img_path = (String)params[4];
            String location = (String)params[5];
            String create_date = (String)params[6];
            String happiness = (String)params[7];
            String num_of_people = (String)params[8];
            String record_path = (String)params[9];

            String postParameters = "&userName=" + userName
                    +"&userPass=" + userPass
                    +"&databaseName=" + databaseName
                    +"&img_path=" + img_path
                    +"&location=" + location
                    +"&create_date=" + create_date
                    +"&happiness=" + happiness
                    +"&num_of_people=" + num_of_people
                    +"&record_path=" + record_path; // php에 보낼값.

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

    class insert_recognition_tb extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(IdentificationActivity.this,
                    "기다려 주세요.", null, true, true);
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

    // 위치 변경 다이얼로그
    public void renameLoc(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String insertLocation="";
        String message="";
        SharedPreferences insert = getSharedPreferences("Picture_info_Pref", MODE_PRIVATE);
        message = "장소 추출이 실패하였습니다. 위치정보 변경 화면으로 전환하여 장소를 등록해 주세요.";
        if(!(insert.getString("location","").equals(""))){
            insertLocation = insert.getString("location","");
            message = "촬영 장소가 " +  insertLocation + " 아닌가요?\n변경하시겠습니까?";
        }

        builder.setMessage(message)
                .setPositiveButton("네", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(IdentificationActivity.this,renameLoc_Activity.class);
                        startActivityForResult(intent, RENAME_LOC_INFO);
                        //긍정 버튼을 클릭했을 때, 실행할 동작
                    }
                })
                .setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        showInfo();
                        //부정 버튼을 클릭했을 때, 실행할 동작
                    }
                });
        builder.show();
    } //renameLoc () end.

    // 사용자에게 사진 정보 알려주는 토스트
    public void showInfo(){
        StringBuffer names=new StringBuffer();
        for(int i=0;i<inedx;i++){
            if(!(map.get(i).equals(""))){
                String img_path = imageUri.getPath();
                String name=map.get(i).toString();

                names.append(name+" ");
            }
        }
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat mFormat = new SimpleDateFormat("yyyy년 MM월 dd일");
        String getTime = mFormat.format(date);
        int num = PersonCount - inedx;
        SharedPreferences insert = getSharedPreferences("Picture_info_Pref", MODE_PRIVATE);
        strLocation = insert.getString("location","");
        Toast.makeText(getApplicationContext(),"인물: "+names.toString()+"외 "+num+"명 "+"위치: "+strLocation+" 촬영날짜: "+getTime,Toast.LENGTH_LONG).show();
    } // showInfo() end.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode)
        {
            case RENAME_LOC_INFO:
                if (resultCode == RESULT_OK) {
                    showInfo();
                }
                break;
            default:
                break;
        }
    }
}