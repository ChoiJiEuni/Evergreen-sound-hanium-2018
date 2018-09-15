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
        Toast.makeText(getApplicationContext(),"변경하실 위치를 음성 또는 텍스트로 입력해주세요.",Toast.LENGTH_SHORT).show();
    }
    public void btnSpeak(View view) {
        promptSpeechInput();
    }

    public void OnClickedRename(View view) {
        if(locationRenameInput.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(),"변경하실 위치를 음성 또는 텍스트로 입력해주세요.",Toast.LENGTH_SHORT).show();
        }
        else{
            SharedPreferences insert = getSharedPreferences("Picture_info_Pref", MODE_PRIVATE);
            SharedPreferences.Editor editor = insert.edit();
            editor.putString("location",locationRenameInput.getText().toString());
            editor.commit();
            setResult(RESULT_OK);
            finish();
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
    // 위치 변경 다이얼로그
    public void factCheck (){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("이 화면을 나가시면 위치 정보를 이용하여 검색하실때 불이익이 있습니다.")
                .setPositiveButton("네", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
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
}
