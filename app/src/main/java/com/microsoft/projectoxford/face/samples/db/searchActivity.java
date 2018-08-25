package com.microsoft.projectoxford.face.samples.db;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.microsoft.projectoxford.face.samples.R;

import java.util.Locale;

import static android.speech.tts.TextToSpeech.ERROR;

public class searchActivity extends AppCompatActivity {

    private TextToSpeech tts;
    private static final int REQUEST_SEARCH_RESULT = 55;
    Boolean show = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saerch);
        setTitle("검색필터 지정화면");
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
    public void searchResult(View view) {

        Intent intent = new Intent(this,SearchResultctivity.class);
        startActivityForResult(intent,REQUEST_SEARCH_RESULT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        show = false;
        Toast.makeText(getApplicationContext(),"검색을 종료하시려면 뒤로가기 버튼을 눌러주세요.",Toast.LENGTH_LONG).show();
    }

    public void butDateSearch(View view) {
        Intent intent = new Intent (this,DateSearch.class);
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
    protected void onRestart() {
        super.onRestart();
        String searchString="";

        SharedPreferences search = getSharedPreferences("searchSource", MODE_PRIVATE);
        String date = search.getString("date","");
        String location = search.getString("location","");
        String person = search.getString("person","");

        if(show == true){
            if(!date.equals("")){
                String strYear = date.substring(0,4);
                String strMonth = date.substring(4,6);
                String strday = date.substring(4,6);
                searchString += "날짜   "+strYear+"년 "+strMonth+"월 "+strday+"일 ";
            }
            if(!location.equals("")){
                searchString += "\n위치   "+location;
            }
            if(!person.equals("")){
                searchString += "\n사람   "+person;
            }
            if((!date.equals(""))||(!location.equals(""))||(!person.equals(""))){
                searchString += " 을 검색하시려는 것이 맞으신가요? \n맞다면 가장 아래에 위치한 검색하기 버튼을 눌러주세요." +
                        "\n 검색조건을 더 지정하시려면 버튼을 눌러 지정해주세요.";
                Toast.makeText(getApplicationContext(),searchString+"",Toast.LENGTH_LONG).show();
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
