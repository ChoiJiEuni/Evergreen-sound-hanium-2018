package com.microsoft.projectoxford.face.samples.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.microsoft.projectoxford.face.samples.R;
import com.microsoft.projectoxford.face.samples.db.searchActivity;

public class ViewPhotoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_photo);
        setTitle("갤러리 보기 방식 선택 화면");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);




    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
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
