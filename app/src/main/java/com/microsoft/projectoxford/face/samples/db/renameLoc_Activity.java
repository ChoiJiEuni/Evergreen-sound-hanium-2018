package com.microsoft.projectoxford.face.samples.db;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.speech.RecognizerIntent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.microsoft.projectoxford.face.samples.R;
import com.microsoft.projectoxford.face.samples.ui.IdentificationActivity;
import com.microsoft.projectoxford.face.samples.ui.MainActivity;

import java.util.ArrayList;
import java.util.Locale;

public class renameLoc_Activity extends AppCompatActivity {
    private final int REQ_CODE_SPEECH_INPUT = 100;
    String locationResult="";
    private EditText locationRenameInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rename_loc_);
        locationRenameInput = (EditText)findViewById(R.id.locationRenameInput);
        setTitle("위치정보 변경 화면");
        Toast.makeText(getApplicationContext(),"등록하실 위치를 음성 또는 텍스트로 입력해주세요.",Toast.LENGTH_SHORT).show();
    }
    public void btnSpeak(View view) {
        promptSpeechInput();
    }

    public void OnClickedRename(View view) {
        if(locationRenameInput.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(),"등록하실 위치를 음성 또는 텍스트로 입력해주세요.",Toast.LENGTH_SHORT).show();
        }
        else{
            SharedPreferences insert = getSharedPreferences("Picture_info_Pref", MODE_PRIVATE);
            SharedPreferences.Editor editor = insert.edit();
            editor.putString("location",locationRenameInput.getText().toString());
            editor.commit();
            renameLoc(insert);
        }
    }

    public void OnClickedfinsh(View view) {
          factCheck();
    }
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    locationResult = result.get(0);
                    locationRenameInput.setText(locationResult);
                    //tts.setSpeechRate(0.9f);
                    // tts.speak(result.get(0)+"을 검색하실건가요? 아니면 버튼을 누르고 다시 말해주세요.",TextToSpeech.QUEUE_FLUSH, null);

                }
                break;
            }

        }
    }
    // 위치 변경 취소 다이얼로그
    public void factCheck (){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("위치 정보 등록을 취소하시겠습니까? \n'네'를 누르면 다음으로 진행됩니다.")
                .setPositiveButton("네", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        test();
                        setResult(RESULT_OK);
                        finish();
                        //긍정 버튼을 클릭했을 때, 실행할 동작
                    }
                })
                .setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        //부정 버튼을 클릭했을 때, 실행할 동작
                    }
                });
        builder.show();
    } //renameLoc () end.

    //위치 변경 다이얼로그
    public void renameLoc(SharedPreferences in){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String insertLocation="";
        String message="";
        if(!(in.getString("location","").equals(""))){
            insertLocation = in.getString("location","");
            message = "촬영 장소가 " +  insertLocation + " 맞나요? \n'네'를 누르면 다음으로 진행됩니다.";
        }

        builder.setMessage(message)
                .setPositiveButton("네", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        test();
                        setResult(RESULT_OK);
                        finish();
                        //긍정 버튼을 클릭했을 때, 실행할 동작
                    }
                })
                .setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        //부정 버튼을 클릭했을 때, 실행할 동작
                    }
                });
        builder.show();
    }
    public void test(){
        Intent intent2 = new Intent(renameLoc_Activity.this,RecordActivity.class);
        startActivity(intent2);
    }
}
