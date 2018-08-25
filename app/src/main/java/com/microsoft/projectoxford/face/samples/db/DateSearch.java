package com.microsoft.projectoxford.face.samples.db;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.microsoft.projectoxford.face.samples.R;

import java.util.ArrayList;
import java.util.Locale;

import static android.speech.tts.TextToSpeech.ERROR;

public class DateSearch extends AppCompatActivity {

    private EditText dateInput;

    private final int REQ_CODE_SPEECH_INPUT = 100;
  //  private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_search);
        setTitle("날짜 검색 화면");
        dateInput = (EditText)findViewById(R.id.dateInput);
        Toast.makeText(getApplicationContext(),"입력상자에 2018 0306 \n형식으로 날짜를 입력해주세요." +
                "\n 키보드는 숫자만 있는 키보드 입니다.",Toast.LENGTH_LONG).show();
        // TTS를 생성하고 OnInitListener로 초기화 한다.
    /*    tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != ERROR) {
                    // 언어를 선택한다.
                    tts.setLanguage(Locale.KOREAN);
                }
            }
        });*/


    }

    // 검색버튼
    public void OnClickedfinsh(View view) {
        SharedPreferences search = getSharedPreferences("searchSource", MODE_PRIVATE);
        SharedPreferences.Editor editor = search.edit();
        editor.putString("date",dateInput.getText().toString());
        editor.commit();
        finish();
    }




   /* @Override
    protected void onDestroy() {
        super.onDestroy();
        // TTS 객체가 남아있다면 실행을 중지하고 메모리에서 제거한다.
        if(tts != null){
            tts.stop();
            tts.shutdown();
            tts = null;
        }
    }*/

}
