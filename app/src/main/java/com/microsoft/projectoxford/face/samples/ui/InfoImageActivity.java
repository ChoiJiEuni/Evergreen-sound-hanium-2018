package com.microsoft.projectoxford.face.samples.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.microsoft.projectoxford.face.samples.R;
import com.microsoft.projectoxford.face.samples.helper.StorageHelper;
import com.microsoft.projectoxford.face.samples.persongroupmanagement.PersonGroupListActivity;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

import static android.speech.tts.TextToSpeech.ERROR;

public class InfoImageActivity extends AppCompatActivity {
    private Uri mUriPhotoTaken;
    private static final int REQUEST_TAKE_PHOTO = 0;
    private static  final int REQUEST_TAKE_BACK=1;
    private static final int REQUEST_TAKE_AGAIN = 2;
    private TextToSpeech tts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_image);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("");
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
    }

    // 뒤로가기 버튼 행위
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void OnButtonClickedToImage(View view) {
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

                    tts.speak("촬영이 시작됩니다. 정면을 응시하여 주세요.", TextToSpeech.QUEUE_FLUSH, null);
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
                           // setInfo(e.getMessage());
                        }

                    }
                }
            }else{
                register();
            }


        }catch (Exception e){

        }
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

                        Intent intent = new Intent(InfoImageActivity.this, PersonGroupListActivity.class);
                        startActivityForResult(intent,REQUEST_TAKE_AGAIN);
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
                    startActivityForResult(intent, REQUEST_TAKE_BACK);
                    // finish();
                }
                break;
            case REQUEST_TAKE_BACK:
                Intent intent = new Intent(this,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            case REQUEST_TAKE_AGAIN:
                Intent intent2 = new Intent(this,MainActivity.class);
                intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent2);
            default:
                break;


        }
    }
}
