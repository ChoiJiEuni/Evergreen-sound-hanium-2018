package com.microsoft.projectoxford.face.samples.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.microsoft.projectoxford.face.samples.R;

public class InfoShowActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_show);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("");

    }
    @Override
    protected void onRestart() {
        super.onRestart();
        Intent intent = new Intent(this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
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

    public void OnButtonClickedToShow(View view) {
        Intent intent = new Intent(this,ViewPhotoActivity.class);
        startActivity(intent);

    }
}
