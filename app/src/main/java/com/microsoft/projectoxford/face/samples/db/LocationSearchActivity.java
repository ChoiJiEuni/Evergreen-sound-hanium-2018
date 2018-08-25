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
import android.widget.Toast;

import static android.speech.tts.TextToSpeech.ERROR;

import com.microsoft.projectoxford.face.samples.R;

import java.util.ArrayList;
import java.util.Locale;

public class LocationSearchActivity extends AppCompatActivity {


    private EditText locationInput;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    //private TextToSpeech tts;
    String locationResult="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_search);
        setTitle("위치 검색 화면");
        locationInput = (EditText)findViewById(R.id.locationInput);
        Toast.makeText(getApplicationContext(),getString(R.string.search_info),Toast.LENGTH_LONG).show();
      /*  // TTS를 생성하고 OnInitListener로 초기화 한다.
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != ERROR) {
                    // 언어를 선택한다.
                    tts.setLanguage(Locale.KOREAN);
                }
            }
        });*/

    }

    public void btnSpeak(View view) {
        promptSpeechInput();
    }

    public void OnClickedfinsh(View view) {
        SharedPreferences search = getSharedPreferences("searchSource", MODE_PRIVATE);
        SharedPreferences.Editor editor = search.edit();
        editor.putString("location",locationResult);
        editor.commit();
        finish();
    }
    // Showing google speech input dialog.
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_LONG).show();
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
                    locationInput.setText(locationResult);

                    Toast.makeText(getApplicationContext(),locationResult+" "+getString(R.string.search_info2),Toast.LENGTH_LONG).show();
                    //tts.setSpeechRate(0.9f);
                   // tts.speak(result.get(0)+"을 검색하실건가요? 아니면 버튼을 누르고 다시 말해주세요.",TextToSpeech.QUEUE_FLUSH, null);

                }
                break;
            }

        }
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



