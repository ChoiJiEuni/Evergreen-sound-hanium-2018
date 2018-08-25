package com.microsoft.projectoxford.face.samples.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.microsoft.projectoxford.face.samples.R;
import com.microsoft.projectoxford.face.samples.db.searchActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ViewPhotoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_photo);
    }

    public void onClickgallery(View view) {
        Intent intent = new Intent(this,GalleryActivity.class);
        startActivity(intent);
    }

    public void onClickedSearch(View view) {
        SharedPreferences search = getSharedPreferences("searchSource", MODE_PRIVATE);
        SharedPreferences.Editor editor = search.edit();
        editor.clear();
        editor.putString("date","");
        editor.putString("location","");
        editor.putString("person","");
        editor.commit();

        Intent intent=new Intent(this,searchActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
