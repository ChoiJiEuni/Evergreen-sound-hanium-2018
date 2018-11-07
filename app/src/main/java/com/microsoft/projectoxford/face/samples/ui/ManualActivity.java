package com.microsoft.projectoxford.face.samples.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.microsoft.projectoxford.face.samples.R;

public class ManualActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual);
        setTitle("앱 설명서");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    // 뒤로가기 버튼 행위
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onButtonInfoAddPerson(View view) {
        Intent intent = new Intent(this,InfoAddPersonActivity.class);
        startActivity(intent);
    }

    public void OnButtonClickedImage(View view) {
        Intent intent = new Intent(this,InfoImageActivity.class);
        startActivity(intent);
    }

    public void OnButtonClickedInfoShow(View view) {
        Intent intent = new Intent(this,InfoShowActivity.class);
        startActivity(intent);
    }
}
