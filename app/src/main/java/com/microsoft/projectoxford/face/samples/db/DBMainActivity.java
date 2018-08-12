package com.microsoft.projectoxford.face.samples.db;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.microsoft.projectoxford.face.samples.R;

public class DBMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dbmain);
    }

    public void onClickInsertDB(View view) {
        Intent intent = new Intent(this,DBphpActivity.class);
        startActivity(intent);
    }

    public void onClickExifInfo(View view) {
        Intent intent = new Intent(this,ExifActivity.class);
        startActivity(intent);
    }

    public void onClickRecord(View view) {
        Intent intent = new Intent(this,RecordActivity.class);
        startActivity(intent);
    }
}
