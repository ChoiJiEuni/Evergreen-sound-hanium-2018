package com.microsoft.projectoxford.face.samples.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.microsoft.projectoxford.face.samples.R;
import com.microsoft.projectoxford.face.samples.helper.ImageHelper;
import com.microsoft.projectoxford.face.samples.persongroupmanagement.PersonActivity;
import com.microsoft.projectoxford.face.samples.persongroupmanagement.PersonGroupListActivity;

import java.util.HashMap;

public class LearningActivity extends AppCompatActivity {
    HashMap map,bitmaps=null;
    int count,i=0;
    SharedPreferences insert;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learning);
        insert = getSharedPreferences("test", MODE_PRIVATE);
        editor = insert.edit();
        map =IdentificationActivity.getter1();
        bitmaps =IdentificationActivity.getter2();

        count = map.size();
        i = insert.getInt("index",0);
        /*Bitmap mBitmap = ImageHelper.loadSizeLimitedBitmapFromUri(
                (Uri) bitmaps.get(0), getContentResolver());
        ImageView iv = (ImageView)findViewById(R.id.testImageVie22w);
        iv.setImageBitmap(mBitmap);*/
        if(i<count){
            test();
        }
        else{
            Intent intent = new Intent(this,IdentificationActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        }


    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Boolean end = insert.getBoolean("end",false);
        Boolean group = insert.getBoolean("group",false);
        if(group != true){
            if(end == true){
               finish();
            }}
    }

    public void test(){
        editor.putBoolean("input", true);
        editor.putBoolean("end", false);
        editor.putBoolean("repeat", true);
        int index = insert.getInt("index",0)+1;
        editor.putInt("index", index);
        editor.commit(); //완료한다.


        Intent intent = new Intent(this,PersonGroupListActivity.class);
        intent.putExtra("bitmap",bitmaps.get(0).toString());
        intent.putExtra("name",(String)map.get(0));
        intent.putExtra("input",true);

        startActivity(intent);
    }
}
