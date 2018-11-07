package com.microsoft.projectoxford.face.samples.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.microsoft.projectoxford.face.samples.R;
import com.microsoft.projectoxford.face.samples.db.DBMainActivity;
import com.microsoft.projectoxford.face.samples.helper.StorageHelper;
import com.microsoft.projectoxford.face.samples.persongroupmanagement.PersonGroupActivity;
import com.microsoft.projectoxford.face.samples.persongroupmanagement.PersonGroupListActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

import static android.speech.tts.TextToSpeech.ERROR;

public class MainActivity extends AppCompatActivity {

    String mPersonGroupId;
    boolean detected;

    PersonGroupListAdapter mPersonGroupListAdapter;

    /////////////추가용
    private static final int REQUEST_TAKE_PHOTO = 0;
    private TextToSpeech tts;

    //*/ DB
    private static String IP_ADDRESS = "14.63.195.105"; // 한이음 서버 IP
    private static String TAG = "php";
    String userName="";
    String userPass="1111";
    String DatabaseName ="";

    // The URI of photo taken with camera
    private Uri mUriPhotoTaken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        TextView InfoChange = (TextView)findViewById(R.id.mainInfoChange);
        setTitle("너들나들");

        getHashKey();
        // DB, Table 생성
        initDB();

        detected = false;

        /////추가용
        // TTS를 생성하고 OnInitListener로 초기화 한다.
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != ERROR) {
                    // 언어를 선택한다.
                    tts.setLanguage(Locale.KOREAN);
                }
            }
        });

        Set<String> personGroupIds = StorageHelper.getAllPersonGroupIds(this);
        Iterator iterator = personGroupIds.iterator();
        if(iterator.hasNext()) {
            String personGroupId = personGroupIds.iterator().next();
            String groupName = StorageHelper.getPersonGroupName(personGroupId, this);
            if (groupName != null) {
                InfoChange.setText("");
                // Toast.makeText(this,"등록된 인물이 없습니다. 등록 후 사용해 주세요",Toast.LENGTH_LONG).show();

            }
        }
    }
    private void getHashKey(){
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageInfo == null)
            Log.e("KeyHash", "KeyHash:null");

        for (Signature signature : packageInfo.signatures) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            } catch (NoSuchAlgorithmException e) {
                Log.e("KeyHash", "Unable to get MessageDigest. signature=" + signature, e);
            }
        }
    }
    private void setIdentifyButtonEnabledStatus(boolean isEnabled) {
        //TextView button = (TextView) findViewById(R.id.identify);
//  button.setEnabled(isEnabled); 필요 없는 거라서 지워도 되는데 혹시 몰라서 주석 처리함
    }
    private void refreshIdentifyButtonEnabledStatus() {
        if (detected && mPersonGroupId != null) {
            //   setIdentifyButtonEnabledStatus(true);
        } else {
            //   setIdentifyButtonEnabledStatus(false);
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
                    mPersonGroupId, MainActivity.this);
            //  refreshIdentifyButtonEnabledStatus();
            textView.setTextColor(Color.BLACK);
            textView.setText(String.format("Person group to use: %s", personGroupName));
        }
    }
    /////추가 메소드
    // Save the activity state when it's going to stop.
    //작업이 중지될 때 작업 상태를 저장합니다.
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("ImageUri", mUriPhotoTaken);
    }

    // Recover the saved state when the activity is recreated.
    // 작업을 재생성할 때 저장된 상태를 복구합니다.
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mUriPhotoTaken = savedInstanceState.getParcelable("ImageUri");
    }

    //분석할 사진 촬영 또는 갤러리에서 선택
    public void OnButtonClickedImage(View view) {
       //// Intent intent = new Intent(this, SelectImageActivity.class);
        // 추강

        SharedPreferences insert = getSharedPreferences("machine", MODE_PRIVATE);
        SharedPreferences.Editor editor = insert.edit();
        editor.putBoolean("input", true);
     //   editor.putBoolean("end", false);
    //    editor.putBoolean("group", true);
        editor.commit(); //완료한다.

       // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
       // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      /////  startActivity(intent);
        //채윤: 그룹없으면 인물등록후 사용
        try{
            Set<String> personGroupIds = StorageHelper.getAllPersonGroupIds(this);
            Iterator iterator = personGroupIds.iterator();
            if(iterator.hasNext()){
                String personGroupId = personGroupIds.iterator().next();
                String groupName=StorageHelper.getPersonGroupName( personGroupId, this);
                Log.d("chae","goupName"+groupName);
               if(groupName==null){
                   register();

               }else{

                   tts.speak("촬영이 시작됩니다. 정면을 응시하여 주세요.",TextToSpeech.QUEUE_FLUSH, null);
                   Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                   if(intent.resolveActivity(getPackageManager()) != null) {
                       // Save the photo taken to a temporary file.
                       // File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                       try {
                           File dir =new File( Environment.getExternalStorageDirectory().getAbsolutePath()+"/evergreen/");
                           Log.d("chae",dir+"");

                           if(!dir.exists())
                               dir.mkdirs();
                           File file = File.createTempFile("evergreen_", ".jpg", dir);
                           mUriPhotoTaken = Uri.fromFile(file);
                           // Log.d("chae",mUriPhotoTaken+"넘긴거");
                           //sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,mUriPhotoTaken));

                           intent.putExtra(MediaStore.EXTRA_OUTPUT, mUriPhotoTaken);
                           startActivityForResult(intent, REQUEST_TAKE_PHOTO);
                       } catch (IOException e) {
                           setInfo(e.getMessage());
                       }

                   }
               }
            }else{
                register();
            }


        }catch (Exception e){

        }


    }
    private void setInfo(String info) {
        TextView textView = (TextView) findViewById(R.id.info);
        textView.setText(info);
    }

    public void OnButtonClickedShow(View view) {
        Intent intent = new Intent(this,ViewPhotoActivity.class);
        startActivity(intent);
    }

    public void OnButtonClickedManual(View view) {
            Intent intent = new Intent(this,ManualActivity.class);
            startActivity(intent);
    }

    // 사용자 그룹을 포함하는 ListView의 어댑터입니다.
    private class PersonGroupListAdapter extends BaseAdapter {
        List<String> personGroupIdList;

        // Initialize with detection result.
        PersonGroupListAdapter() {
            personGroupIdList = new ArrayList<>();

            Set<String> personGroupIds
                    = StorageHelper.getAllPersonGroupIds(MainActivity.this);

            for (String personGroupId : personGroupIds) {
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
                        (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.item_person_group, parent, false);
            }
            convertView.setId(position);

            // set the text of the item
            //항목의 텍스트 설정
            String personGroupName = StorageHelper.getPersonGroupName(
                    personGroupIdList.get(position), MainActivity.this);
            int personNumberInGroup = StorageHelper.getAllPersonIds(
                    personGroupIdList.get(position), MainActivity.this).size();
            ((TextView) convertView.findViewById(R.id.text_person_group)).setText(
                    String.format(
                            "%s (Person count: %d)",
                            personGroupName,
                            personNumberInGroup));

            if (position == 0) {
                ((TextView) convertView.findViewById(R.id.text_person_group)).setTextColor(
                        Color.parseColor("#3399FF"));
            }

            return convertView;
        }
    }

    public void OnButtonClickedGallery(View view){
        Intent intent = new Intent(this,GalleryActivity.class);
        startActivity(intent);
    }
    /// 인물 등록하는 화면으로 넘어감!! 기존의 manage person groups 역할!
    public void onButtonAddPerson(View view) {

        SharedPreferences insert = getSharedPreferences("machine", MODE_PRIVATE);
        SharedPreferences.Editor editor = insert.edit();
        editor.putBoolean("input", false);
        editor.putBoolean("end", false);
        editor.putBoolean("group", true);
        editor.commit(); //완료한다.

        Intent intent = new Intent(this, PersonGroupListActivity.class);
        startActivity(intent);

    }
    /// 한이음 서버 데베에는 접근이 안되서 일단은 로컬서버 데베에 저장하는 거로 함.
    public void onButtonAddPHP(View view) {
        Intent intent = new Intent(this,DBMainActivity.class);
        startActivity(intent);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode)
        {
            case REQUEST_TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    File dir =new File( Environment.getExternalStorageDirectory().getAbsolutePath()+"/evergreen/");
                    Log.d("chae",dir+"");

                    if(!dir.exists())
                        dir.mkdirs();
                    ///////////////////////////////////////////////////////////////

                    //*/ 갤러리에 사진 저장하는 부분
                    /*Intent intent  = new Intent (Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    intent.setData(mUriPhotoTaken);
                    sendBroadcast(intent);*/


                    Uri imageUri;
                    if (data == null || data.getData() == null) {
                        imageUri = mUriPhotoTaken;
                    } else {
                        imageUri = data.getData();
                    }
                    Intent intent = new Intent(this, IdentificationActivity.class);
                    intent.setData(imageUri);

                    // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    // intent.addFlags(intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivityForResult(intent, RESULT_OK);
                    // finish();
                }
                break;
            default:
                break;
        }
    }

    // DB, Table 생성.
    private void initDB(){
        SharedPreferences sharedPreferences = getSharedPreferences("USER",MODE_PRIVATE);
        SharedPreferences.Editor editor= sharedPreferences.edit();
        if(sharedPreferences.getString("ID","").equals("")){
            Random random = new Random();
            int randomFir = random.nextInt(999999999);
            String userID = String.valueOf(randomFir);
            long now = System.currentTimeMillis();
            Date date = new Date(now);
            SimpleDateFormat mFormat = new SimpleDateFormat("YYMMdd");
            String getTime = mFormat.format(date);
            userName = "a"+getTime+userID;
            DatabaseName = userName+"_db";
            editor.putString("ID",userName);
            editor.commit();
        }
        else{
            userName = sharedPreferences.getString("ID","");
            DatabaseName = userName+"_db";
        }
        createDatabaseAndTable DBTask = new createDatabaseAndTable();
        DBTask.execute("http://" + IP_ADDRESS + "/DB.php",userName,userPass,DatabaseName);
        createDatabaseAndTable TableTask = new createDatabaseAndTable();
        TableTask.execute("http://" + IP_ADDRESS + "/tableCreate.php",userName,userPass,DatabaseName);

    }

    // DB,Table 생성
    class createDatabaseAndTable extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(MainActivity.this,
                    "Please Wait", null, true, true);
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

    // 검색
    public void onClickedSearch(View view) {
     //   Intent intent=new Intent(this,searchActivity.class);
     //   startActivity(intent);
    }
    private void register(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        String message="";
        SharedPreferences insert = getSharedPreferences("Picture_info_Pref", MODE_PRIVATE);
        message = "생성된 앨범이 없습니다. 등록 후 사용해 주세요\n등록하시겠습니까?";

        builder.setMessage(message)
                .setPositiveButton("네", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SharedPreferences insert = getSharedPreferences("machine", MODE_PRIVATE);
                        SharedPreferences.Editor editor = insert.edit();
                        editor.putBoolean("input", false);
                        editor.putBoolean("end", false);
                        editor.putBoolean("group", true);
                        editor.commit(); //완료한다.

                        Intent intent = new Intent(MainActivity.this, PersonGroupListActivity.class);
                        startActivity(intent);
                        //긍정 버튼을 클릭했을 때, 실행할 동작
                    }
                })
                .setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                      /*  tts.speak("촬영이 시작됩니다. 정면을 응시하여 주세요.",TextToSpeech.QUEUE_FLUSH, null);
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if(intent.resolveActivity(getPackageManager()) != null) {
                            // Save the photo taken to a temporary file.
                            // File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                            try {
                                File dir =new File( Environment.getExternalStorageDirectory().getAbsolutePath()+"/evergreen/");
                                Log.d("chae",dir+"");

                                if(!dir.exists())
                                    dir.mkdirs();
                                File file = File.createTempFile("evergreen_", ".jpg", dir);
                                mUriPhotoTaken = Uri.fromFile(file);
                                // Log.d("chae",mUriPhotoTaken+"넘긴거");
                                //sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,mUriPhotoTaken));

                                intent.putExtra(MediaStore.EXTRA_OUTPUT, mUriPhotoTaken);
                                startActivityForResult(intent, REQUEST_TAKE_PHOTO);
                            } catch (IOException e) {
                                setInfo(e.getMessage());
                            }

                        }*/
                      Toast.makeText(getApplicationContext(),"기본 앨범이 존재하지 않으면 촬영을 진행할 수 없습니다.",Toast.LENGTH_SHORT);
                        //부정 버튼을 클릭했을 때, 실행할 동작
                    }
                });
        builder.setCancelable(false);
        builder.show();
    }

}
