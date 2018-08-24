package com.microsoft.projectoxford.face.samples.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.microsoft.projectoxford.face.samples.R;
import com.microsoft.projectoxford.face.samples.db.searchActivity;

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
        Intent intent=new Intent(this,searchActivity.class);
        startActivity(intent);
    }
}
