package com.microsoft.projectoxford.face.samples.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.microsoft.projectoxford.face.samples.R;
import com.microsoft.projectoxford.face.samples.helper.ImageHelper;
import com.microsoft.projectoxford.face.samples.persongroupmanagement.PersonGroupListActivity;

import java.util.HashMap;

public class LearningMainActivity extends AppCompatActivity {
    HashMap map,bitmaps=null;
    int count,i=0;
    SharedPreferences insert;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learning_main);

        insert = getSharedPreferences("test", MODE_PRIVATE);
        editor = insert.edit();
        map =IdentificationActivity.getter1();  // 사람이름 들어있는
        bitmaps =IdentificationActivity.getter2(); // 크롭된 이미지 map

        count = map.size();
        i = insert.getInt("index",0);
        if(count>0){
            // 머신러닝할 사람이 0명이상인지.
            if(i<count){
                register_step1();
            }
            else{
                Intent intent = new Intent(this,MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        }
        else{
            Toast.makeText(getApplicationContext(),"저장 되었습니다.",Toast.LENGTH_LONG).show();
            finish();
        }


    }
    public void register_step1(){
        editor.putBoolean("input", true);
        editor.putBoolean("end", false);
        editor.putBoolean("repeat", true);
        editor.putBoolean("end", true);
        editor.putBoolean("group", false);
        int index = insert.getInt("index",0)+1;
        editor.putInt("index", index);
        editor.commit(); //완료한다.



        Intent intent = new Intent(this,PersonGroupListActivity.class);
        intent.putExtra("bitmap",bitmaps.get(index-1).toString());
        intent.putExtra("name",(String)map.get(index-1));
        intent.putExtra("input",true);

        startActivity(intent);
    }
}
