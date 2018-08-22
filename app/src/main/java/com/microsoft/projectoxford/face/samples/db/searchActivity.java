package com.microsoft.projectoxford.face.samples.db;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.microsoft.projectoxford.face.samples.R;

public class searchActivity extends AppCompatActivity {

    EditText editDate,editLocation,editPerson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saerch);
        editDate = (EditText)findViewById(R.id.editDate);
        editLocation = (EditText)findViewById(R.id.editLocation);
        editPerson = (EditText)findViewById(R.id.editPerson);
    }
    public void searchResult(View view) {
        String img_date = editDate.getText().toString();
        String img_location = editLocation.getText().toString();
        String img_person = editPerson.getText().toString();

        Intent intent = new Intent(this,SearchResultctivity.class);
        intent.putExtra("img_date",img_date);
        intent.putExtra("img_location",img_location);
        intent.putExtra("img_person",img_person);
        startActivity(intent);
    }
}
