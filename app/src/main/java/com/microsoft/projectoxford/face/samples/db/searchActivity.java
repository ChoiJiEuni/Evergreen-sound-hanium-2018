package com.microsoft.projectoxford.face.samples.db;

import android.content.Intent;
import android.content.SharedPreferences;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.microsoft.projectoxford.face.samples.R;

public class searchActivity extends AppCompatActivity {


    private TextToSpeech tts;
    private static final int REQUEST_SEARCH_RESULT = 55;
    Boolean show = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setTitle("검색하고 싶은 조건을 선택해주세요.");


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        Toast.makeText(getApplicationContext(),"검색버튼을 눌러 조건을 지정한 후 \n가장 아래에 있는 검색 필터 적용 버튼을 눌러주세요.",Toast.LENGTH_LONG).show();
        /*// TTS를 생성하고 OnInitListener로 초기화 한다.
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

    public void butDateSearch(View view) {
        Intent intent = new Intent (this,DateSearchActivity.class);
        show = true;
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }

    public void butLocationSearch(View view) {
        Intent intent = new Intent (this, LocationSearchActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        show = true;
        startActivity(intent);
    }

    public void butPersonSearch(View view) {
        Intent intent = new Intent (this, PersonSearchActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        show = true;
        startActivity(intent);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        show = false;
        Toast.makeText(getApplicationContext(),"검색을 종료하시려면 뒤로가기 버튼을 눌러주세요.",Toast.LENGTH_LONG).show();
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
