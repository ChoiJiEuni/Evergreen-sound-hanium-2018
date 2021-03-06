//
// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license.
//
// Microsoft Cognitive Services (formerly Project Oxford): https://www.microsoft.com/cognitive-services
//
// Microsoft Cognitive Services (formerly Project Oxford) GitHub:
// https://github.com/Microsoft/Cognitive-Face-Android
//
// Copyright (c) Microsoft Corporation
// All rights reserved.
//
// MIT License:
// Permission is hereby granted, free of charge, to any person obtaining
// a copy of this software and associated documentation files (the
// "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to
// permit persons to whom the Software is furnished to do so, subject to
// the following conditions:
//
// The above copyright notice and this permission notice shall be
// included in all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED ""AS IS"", WITHOUT WARRANTY OF ANY KIND,
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
// NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
// LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
// OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
// WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
//
package com.microsoft.projectoxford.face.samples.persongroupmanagement;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.samples.R;
import com.microsoft.projectoxford.face.samples.helper.LogHelper;
import com.microsoft.projectoxford.face.samples.helper.SampleApp;
import com.microsoft.projectoxford.face.samples.helper.StorageHelper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;


public class PersonGroupActivity extends AppCompatActivity {
    //ImageView coverImg;
    TextView coverImg;
    LinearLayout relative1,relative2;
    int imageIndex=0;

    public void addPerson2(View view) {
        if (!personGroupExists) {
            new AddPersonGroupTask(true).execute(personGroupId);
        } else {
            addPerson();
        }
    }

    // Background task of adding a person group.
    class AddPersonGroupTask extends AsyncTask<String, String, String> {
        // Indicate the next step is to add person in this group, or finish editing this group.
        boolean mAddPerson;

        AddPersonGroupTask(boolean addPerson) {
            mAddPerson = addPerson;
        }

        @Override
        protected String doInBackground(String... params) {
            addLog("Request: Creating person group " + params[0]);

            // Get an instance of face service client.
            FaceServiceClient faceServiceClient = SampleApp.getFaceServiceClient();
            try{
                publishProgress("동기화하는 중입니다..");

                // Start creating person group in server.
                faceServiceClient.createLargePersonGroup(
                        params[0],
                        getString(R.string.user_provided_person_group_name),
                        getString(R.string.user_provided_person_group_description_data));

                return params[0];
            } catch (Exception e) {
                publishProgress(e.getMessage());
                addLog(e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            setUiBeforeBackgroundTask();
        }

        @Override
        protected void onProgressUpdate(String... progress) {
            setUiDuringBackgroundTask(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            progressDialog.dismiss();

            if (result != null) {
                addLog("Response: Success. Person group " + result + " created");

                personGroupExists = true;
                GridView gridView = (GridView) findViewById(R.id.gridView_persons);
                personGridViewAdapter = new PersonGridViewAdapter();
                gridView.setAdapter(personGridViewAdapter);

                setInfo("Success. Group " + result + " created");

                if (mAddPerson) {
                    addPerson();
                } else {
                    doneAndSave(false);
                }
            }
        }
    }

    class TrainPersonGroupTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            addLog("Request: Training group " + params[0]);

            // Get an instance of face service client.
            FaceServiceClient faceServiceClient = SampleApp.getFaceServiceClient();
            try{
                publishProgress("Training person group...");

                faceServiceClient.trainLargePersonGroup(params[0]);
                return params[0];
            } catch (Exception e) {
                publishProgress(e.getMessage());
                addLog(e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            setUiBeforeBackgroundTask();
        }

        @Override
        protected void onProgressUpdate(String... progress) {
            setUiDuringBackgroundTask(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            progressDialog.dismiss();

            if (result != null) {
                addLog("Response: Success. Group " + result + " training completed");

                finish();
            }
        }
    }

    class DeletePersonTask extends AsyncTask<String, String, String> {
        String mPersonGroupId;
        DeletePersonTask(String personGroupId) {
            mPersonGroupId = personGroupId;
        }
        @Override
        protected String doInBackground(String... params) {
            // Get an instance of face service client.
            FaceServiceClient faceServiceClient = SampleApp.getFaceServiceClient();
            try{
                publishProgress("Deleting selected persons...");
                addLog("Request: Deleting person " + params[0]);

                UUID personId = UUID.fromString(params[0]);
                faceServiceClient.deletePersonInLargePersonGroup(mPersonGroupId, personId);
                return params[0];
            } catch (Exception e) {
                publishProgress(e.getMessage());
                addLog(e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            setUiBeforeBackgroundTask();
        }

        @Override
        protected void onProgressUpdate(String... progress) {
            setUiDuringBackgroundTask(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            progressDialog.dismiss();

            if (result != null) {
                setInfo("Person " + result + " successfully deleted");
                addLog("Response: Success. Deleting person " + result + " succeed");
            }
        }
    }

    private void setUiBeforeBackgroundTask() {
        progressDialog.show();
    }

    // Show the status of background detection task on screen.
    private void setUiDuringBackgroundTask(String progress) {
        progressDialog.setMessage(progress);

        setInfo(progress);
    }

    public void addPerson(View view) {
        EditText  edit_person_group_name = (EditText)findViewById(R.id.edit_person_group_name);
        if((edit_person_group_name.getText().toString()).equals("")) { //그룹 이름을 입력하지 않았다면
            Toast.makeText(getApplicationContext(),"앨범 이름을 입력 후 버튼을 눌러주세요.",Toast.LENGTH_LONG).show();
        }else{
            if (!personGroupExists) {
                new AddPersonGroupTask(true).execute(personGroupId);
            } else {
                addPerson();
            }
        }
    }

    private void addPerson() {
        setInfo("");

        Intent intent = new Intent(this, PersonActivity.class);
        intent.putExtra("AddNewPerson", true);
        intent.putExtra("PersonName", "");
        intent.putExtra("PersonGroupId", personGroupId);
        startActivity(intent);
    }

    boolean addNewPersonGroup;
    boolean personGroupExists;
    String personGroupId;
    String oldPersonGroupName;

    PersonGridViewAdapter personGridViewAdapter;

    // Progress dialog popped up when communicating with server.
    ProgressDialog progressDialog;
    TextView btn;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    EditText editTextPersonGroupName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_group);
        setTitle("앨범 이름을 정해주세요.");
        coverImg = findViewById(R.id.coverImg2);
        relative1=findViewById(R.id.relative2);// 기본 그룹 생성되어있지 "않은"(x) 경우
        relative2=findViewById(R.id.createdGroup);// 기본 그룹 생성되어 "있는"(0) 경우

        SharedPreferences pref = getSharedPreferences("machine",MODE_PRIVATE);

        btn = (TextView)findViewById(R.id.Speech);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            addNewPersonGroup = bundle.getBoolean("AddNewPersonGroup");
            oldPersonGroupName = bundle.getString("PersonGroupName");
            personGroupId = bundle.getString("PersonGroupId");
            personGroupExists = !addNewPersonGroup;
        }

        initializeGridView();

        progressDialog = new ProgressDialog(this);
        //  progressDialog.setTitle(getString(R.string.progress_dialog_title));
        progressDialog.setTitle("기다려 주세요.");

        editTextPersonGroupName = (EditText)findViewById(R.id.edit_person_group_name);
        editTextPersonGroupName.setText(oldPersonGroupName); //*/ 0825주석풀음.

        if((editTextPersonGroupName.getText().toString()).equals("")){ //기본 그룹 생성 되어있지 않은 경우
            coverImg.setVisibility(View.GONE);
            relative1.setVisibility(View.VISIBLE);
            relative2.setVisibility(View.GONE);
        }else{ // 기본 그룹 생성되어있는 경우
            setTitle("");
            coverImg.setVisibility(View.GONE);
            relative1.setVisibility(View.GONE);
            relative2.setVisibility(View.VISIBLE);
        }

            if(pref.getBoolean("input",false) == true){
            imageIndex=1;
        } else{
            imageIndex=0;

        }
        changeImage();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                promptSpeechInput();
            }
        });
    }
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    editTextPersonGroupName.setText(result.get(0));
                    checkInput(result.get(0));
                }
                break;
            }

        }
    }
    private void initializeGridView() {
        GridView gridView = (GridView) findViewById(R.id.gridView_persons);

        gridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);
        gridView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode,
                                                  int position, long id, boolean checked) {
                personGridViewAdapter.personChecked.set(position, checked);

                GridView gridView = (GridView) findViewById(R.id.gridView_persons);
                gridView.setAdapter(personGridViewAdapter);
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.menu_delete_items, menu);

                personGridViewAdapter.longPressed = true;

                GridView gridView = (GridView) findViewById(R.id.gridView_persons);
                gridView.setAdapter(personGridViewAdapter);

                TextView addNewItem = (TextView) findViewById(R.id.add_person);
                addNewItem.setEnabled(false);

                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_delete_items:
                        deleteSelectedItems();
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                personGridViewAdapter.longPressed = false;

                for (int i = 0; i < personGridViewAdapter.personChecked.size(); ++i) {
                    personGridViewAdapter.personChecked.set(i, false);
                }

                GridView gridView = (GridView) findViewById(R.id.gridView_persons);
                gridView.setAdapter(personGridViewAdapter);

                TextView addNewItem = (TextView) findViewById(R.id.add_person);
                addNewItem.setEnabled(true);
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!personGridViewAdapter.longPressed) {
                    String personId = personGridViewAdapter.personIdList.get(position);
                    String personName = StorageHelper.getPersonName(
                            personId, personGroupId, PersonGroupActivity.this);

                    Intent intent = new Intent(PersonGroupActivity.this, PersonActivity.class);
                    intent.putExtra("AddNewPerson", false);
                    intent.putExtra("PersonName", personName);
                    intent.putExtra("PersonId", personId);
                    intent.putExtra("PersonGroupId", personGroupId);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (personGroupExists) {
            GridView gridView = (GridView) findViewById(R.id.gridView_persons);
            personGridViewAdapter = new PersonGridViewAdapter();
            gridView.setAdapter(personGridViewAdapter);
        }
    }

    //지은: 전 Activity가 finish() 되었을때
    @Override
    protected void onRestart() {
        super.onRestart();
        if (!personGroupExists) {
            imageIndex=0;
            changeImage();
            new AddPersonGroupTask(false).execute(personGroupId);
        } else {

            imageIndex=1;
            changeImage();
            doneAndSave(true);
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean("AddNewPersonGroup", addNewPersonGroup);
        outState.putString("OldPersonGroupName", oldPersonGroupName);
        outState.putString("PersonGroupId", personGroupId);
        outState.putBoolean("PersonGroupExists", personGroupExists);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        addNewPersonGroup = savedInstanceState.getBoolean("AddNewPersonGroup");
        personGroupId = savedInstanceState.getString("PersonGroupId");
        oldPersonGroupName = savedInstanceState.getString("OldPersonGroupName");
        personGroupExists = savedInstanceState.getBoolean("PersonGroupExists");
    }

    /*public void doneAndSave(View view) {
        if (!personGroupExists) {
            new AddPersonGroupTask(false).execute(personGroupId);
        } else {
            doneAndSave(true);
        }
    }*/

    private void doneAndSave(boolean trainPersonGroup) {
        EditText editTextPersonGroupName = (EditText)findViewById(R.id.edit_person_group_name);
        String newPersonGroupName = editTextPersonGroupName.getText().toString();
        if (newPersonGroupName.equals("")) {
            setInfo("그룹 이름을 지정하여 주세요.");
            return;
        }

        StorageHelper.setPersonGroupName(personGroupId, newPersonGroupName, PersonGroupActivity.this);

        if (trainPersonGroup) {
            new TrainPersonGroupTask().execute(personGroupId);
        } else {
            finish();
        }
    }

    private void deleteSelectedItems() {
        List<String> newPersonIdList = new ArrayList<>();
        List<Boolean> newPersonChecked = new ArrayList<>();
        List<String> personIdsToDelete = new ArrayList<>();
        for (int i = 0; i < personGridViewAdapter.personChecked.size(); ++i) {
            if (personGridViewAdapter.personChecked.get(i)) {
                String personId = personGridViewAdapter.personIdList.get(i);
                personIdsToDelete.add(personId);
                new DeletePersonTask(personGroupId).execute(personId);
            } else {
                newPersonIdList.add(personGridViewAdapter.personIdList.get(i));
                newPersonChecked.add(false);
            }
        }

        StorageHelper.deletePersons(personIdsToDelete, personGroupId, this);

        personGridViewAdapter.personIdList = newPersonIdList;
        personGridViewAdapter.personChecked = newPersonChecked;
        personGridViewAdapter.notifyDataSetChanged();
    }

    // Add a log item.
    private void addLog(String log) {
        LogHelper.addIdentificationLog(log);
    }

    // Set the information panel on screen.
    private void setInfo(String info) {
        TextView textView = (TextView) findViewById(R.id.info);
        textView.setText(info);
    }

    private class PersonGridViewAdapter extends BaseAdapter {

        List<String> personIdList;
        List<Boolean> personChecked;
        boolean longPressed;

        PersonGridViewAdapter() {
            longPressed = false;
            personIdList = new ArrayList<>();
            personChecked = new ArrayList<>();

            Set<String> personIdSet = StorageHelper.getAllPersonIds(personGroupId, PersonGroupActivity.this);
            for (String personId: personIdSet) {
                personIdList.add(personId);
                personChecked.add(false);
            }
        }

        @Override
        public int getCount() {
            return personIdList.size();
        }

        @Override
        public Object getItem(int position) {
            return personIdList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // set the item view
            if (convertView == null) {
                LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.item_person, parent, false);
            }
            convertView.setId(position);

            String personId = personIdList.get(position);

            // 머신러닝
            SharedPreferences insert = getSharedPreferences("machine", MODE_PRIVATE);
            Boolean input = insert.getBoolean("input",false);
            if(input == true){
                test();
            }
            //*/


            Set<String> faceIdSet = StorageHelper.getAllFaceIds(personId, PersonGroupActivity.this);
            if (!faceIdSet.isEmpty()) {
                Iterator<String> it = faceIdSet.iterator();
                Uri uri = Uri.parse(StorageHelper.getFaceUri(it.next(), PersonGroupActivity.this));
                ((ImageView)convertView.findViewById(R.id.image_person)).setImageURI(uri);
            } else {
                Drawable drawable = getResources().getDrawable(R.drawable.select_image);
                ((ImageView)convertView.findViewById(R.id.image_person)).setImageDrawable(drawable);
            }

            // set the text of the item
            String personName = StorageHelper.getPersonName(personId, personGroupId, PersonGroupActivity.this);
            ((TextView)convertView.findViewById(R.id.text_person)).setText(personName);

            // set the checked status of the item
            CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.checkbox_person);
            if (longPressed) {
                checkBox.setVisibility(View.VISIBLE);

                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        personChecked.set(position, isChecked);
                    }
                });
                checkBox.setChecked(personChecked.get(position));
            } else {
                checkBox.setVisibility(View.INVISIBLE);
            }

            return convertView;
        }
    }

    // 머신러닝
    public void test(){
        int size = personGridViewAdapter.personIdList.size();

        Intent learningIntent = getIntent();
        // String input = learningIntent.getStringExtra("input");
        String bmUri = learningIntent.getStringExtra("bitmap");
        String name = learningIntent.getStringExtra("name");
        // String input = learningIntent.getStringExtra("input");
        String personId = null;
        for(int position = 0 ; position < size ; position++){
            personId = personGridViewAdapter.personIdList.get(position);
            String personName = StorageHelper.getPersonName(
                    personId, personGroupId, PersonGroupActivity.this);
            if(personName.equals(name)){
                break;
            }
        }


        Intent intent = new Intent(PersonGroupActivity.this, PersonActivity.class);
        intent.putExtra("bitmap",bmUri+"");
        intent.putExtra("name",name+"");
        intent.putExtra("input",true);
        intent.putExtra("AddNewPerson", false);
        intent.putExtra("PersonName", name);
        intent.putExtra("PersonId", personId);
        intent.putExtra("PersonGroupId", personGroupId);

        startActivity(intent);
    }
    private void changeImage(){
        if(imageIndex==0){
            relative1.setVisibility(View.VISIBLE);
          //  relative2.setVisibility(View.INVISIBLE);
            coverImg.setVisibility(View.INVISIBLE);

        }else if(imageIndex==1){
            relative1.setVisibility(View.INVISIBLE);
            relative2.setVisibility(View.INVISIBLE);
            coverImg.setVisibility(View.VISIBLE);
        }
    }
    // 음성 입력 확인 다이얼로그
    public void checkInput(String result){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String message="";

        message = result+"을 입력하신게 맞습니까?";


        builder.setMessage(message)
                .setPositiveButton("네", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //긍정 버튼을 클릭했을 때, 실행할 동작
                        if (!personGroupExists) {
                            new AddPersonGroupTask(true).execute(personGroupId);
                        } else {
                            addPerson();
                        }
                    }
                })
                .setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //부정 버튼을 클릭했을 때, 실행할 동작
                        Toast.makeText(getApplicationContext(),"다시 입력해주세요.",Toast.LENGTH_LONG).show();
                    }
                });
        builder.show();
    } //checkInput () end.
}
