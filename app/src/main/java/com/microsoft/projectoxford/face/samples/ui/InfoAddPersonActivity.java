package com.microsoft.projectoxford.face.samples.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.microsoft.projectoxford.face.samples.R;
import com.microsoft.projectoxford.face.samples.persongroupmanagement.PersonGroupListActivity;

public class InfoAddPersonActivity extends AppCompatActivity {
    @Override
    protected void onRestart() {
        super.onRestart();
        Intent intent = new Intent(this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_add_person);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("");
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

    public void onButtonToAddPerson(View view) {
        SharedPreferences insert = getSharedPreferences("machine", MODE_PRIVATE);
        SharedPreferences.Editor editor = insert.edit();
        editor.putBoolean("input", false);
        editor.putBoolean("end", false);
        editor.putBoolean("group", true);
        editor.commit(); //완료한다.

        Intent intent = new Intent(this, PersonGroupListActivity.class);
        startActivity(intent);
    }
}
