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

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.contract.CreatePersonResult;
import com.microsoft.projectoxford.face.samples.R;
import com.microsoft.projectoxford.face.samples.helper.LogHelper;
import com.microsoft.projectoxford.face.samples.helper.SampleApp;
import com.microsoft.projectoxford.face.samples.ui.LearningActivity;
import com.microsoft.projectoxford.face.samples.ui.PersonSelectImage;
import com.microsoft.projectoxford.face.samples.helper.StorageHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;


public class PersonActivity extends AppCompatActivity {
    //등록된 인물 이름 - 220번째줄에서 처리
    // 유아이 소희 (코밋 확인 용)
    String personName;
    // Background task of adding a person to person group.
    class AddPersonTask extends AsyncTask<String, String, String> {
        // Indicate the next step is to add face in this person, or finish editing this person.
        boolean mAddFace;

        AddPersonTask (boolean addFace) {
            mAddFace = addFace;
        }

        @Override
        protected String doInBackground(String... params) {
            // Get an instance of face service client.
            FaceServiceClient faceServiceClient = SampleApp.getFaceServiceClient();
            try{
                publishProgress("동기화 하는 중입니다..");
                addLog("Request: Creating Person in person group" + params[0]);

                // Start the request to creating person.
                CreatePersonResult createPersonResult = faceServiceClient.createPersonInLargePersonGroup(
                        params[0],
                        getString(R.string.user_provided_person_name),
                        getString(R.string.user_provided_description_data));

                return createPersonResult.personId.toString();
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
                addLog("Response: Success. Person " + result + " created.");
                personId = result;
                //setInfo("Successfully Synchronized!");
                setInfo("성공적으로 동기화되었습니다!");

                if (mAddFace) {
                    addFace();
                } else {
                    doneAndSave();
                }
            }
        }
    }

    class DeleteFaceTask extends AsyncTask<String, String, String> {
        String mPersonGroupId;
        UUID mPersonId;

        DeleteFaceTask(String personGroupId, String personId) {
            mPersonGroupId = personGroupId;
            mPersonId = UUID.fromString(personId);
        }

        @Override
        protected String doInBackground(String... params) {
            // Get an instance of face service client.
            FaceServiceClient faceServiceClient = SampleApp.getFaceServiceClient();
            try{
                publishProgress("Deleting selected faces...");
                addLog("Request: Deleting face " + params[0]);

                UUID faceId = UUID.fromString(params[0]);
                faceServiceClient.deletePersonFaceInLargePersonGroup(personGroupId, mPersonId, faceId);
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
                setInfo("Face " + result + " successfully deleted");
                addLog("Response: Success. Deleting face " + result + " succeed");
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

    boolean addNewPerson;
    String personId;
    String personGroupId;
    String oldPersonName;

    private static final int REQUEST_SELECT_IMAGE = 0;

    FaceGridViewAdapter faceGridViewAdapter;

    // Progress dialog popped up when communicating with server.
    ProgressDialog progressDialog;

    EditText editTextPersonName;
    TextView speech;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);
        super.setTitle("인물 화면");

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            addNewPerson = bundle.getBoolean("AddNewPerson");
            personGroupId = bundle.getString("PersonGroupId");
            oldPersonName = bundle.getString("PersonName");
            personName = bundle.getString("PersonName");//등록된 인물 이름 받아오기
            if (!addNewPerson) {
                personId = bundle.getString("PersonId");
            }
        }

        initializeGridView();
        speech = (TextView)findViewById(R.id.Speech1);
        editTextPersonName = (EditText)findViewById(R.id.edit_person_name);
        editTextPersonName.setText(oldPersonName);

        speech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                promptSpeechInput();
            }
        });

        progressDialog = new ProgressDialog(this);
        //progressDialog.setTitle(getString(R.string.progress_dialog_title));
        progressDialog.setTitle("기다려 주세요.");
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

    private void initializeGridView() {
        GridView gridView = (GridView) findViewById(R.id.gridView_faces);

        gridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);
        gridView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(
                    ActionMode mode, int position, long id, boolean checked) {
                faceGridViewAdapter.faceChecked.set(position, checked);

                GridView gridView = (GridView) findViewById(R.id.gridView_faces);
                gridView.setAdapter(faceGridViewAdapter);
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.menu_delete_items, menu);

                faceGridViewAdapter.longPressed = true;

                GridView gridView = (GridView) findViewById(R.id.gridView_faces);
                gridView.setAdapter(faceGridViewAdapter);

                TextView addNewItem = (TextView) findViewById(R.id.add_face);
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
                faceGridViewAdapter.longPressed = false;

                for (int i = 0; i < faceGridViewAdapter.faceChecked.size(); ++i) {
                    faceGridViewAdapter.faceChecked.set(i, false);
                }

                GridView gridView = (GridView) findViewById(R.id.gridView_faces);
                gridView.setAdapter(faceGridViewAdapter);

                TextView addNewItem = (TextView) findViewById(R.id.add_face);
                addNewItem.setEnabled(true);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        faceGridViewAdapter = new FaceGridViewAdapter();
        GridView gridView = (GridView) findViewById(R.id.gridView_faces);
        gridView.setAdapter(faceGridViewAdapter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean("AddNewPerson", addNewPerson);
        outState.putString("PersonId", personId);
        outState.putString("PersonGroupId", personGroupId);
        outState.putString("OldPersonName", oldPersonName);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        addNewPerson = savedInstanceState.getBoolean("AddNewPerson");
        personId = savedInstanceState.getString("PersonId");
        personGroupId = savedInstanceState.getString("PersonGroupId");
        oldPersonName = savedInstanceState.getString("OldPersonName");
    }

    /*public void doneAndSave(View view) {
        if (personId == null) {
            new AddPersonTask(false).execute(personGroupId);
        } else {
            doneAndSave();
        }
    }*/

    public void addFace(View view) {
        if (personId == null) {
            new AddPersonTask(true).execute(personGroupId);
        } else {
            addFace();
        }
    }

    private void doneAndSave() {
        TextView textWarning = (TextView)findViewById(R.id.info);
        EditText editTextPersonName = (EditText)findViewById(R.id.edit_person_name);
        String newPersonName = editTextPersonName.getText().toString();
        if (newPersonName.equals("")) {
            //textWarning.setText(R.string.person_name_empty_warning_message);
            textWarning.setText("이름을 지정하여 주세요.");
            return;
        }

        StorageHelper.setPersonName(personId, newPersonName, personGroupId, PersonActivity.this);

        finish();
    }

    private void addFace() {
        setInfo("");

        // DB
        EditText editTextPersonName = (EditText)findViewById(R.id.edit_person_name);
        SharedPreferences insert = getSharedPreferences("RegisteredTB_Pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = insert.edit();
        editor.putString("name", editTextPersonName.getText().toString()); //First라는 key값으로 infoFirst 데이터를 저장한다.
        editor.commit(); //완료한다.

        // 소히
        //Intent intent = new Intent(this, SelectImageActivity.class);
        Intent intent = new Intent(this, PersonSelectImage.class);
        startActivityForResult(intent, REQUEST_SELECT_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode)
        {
            case REQUEST_SELECT_IMAGE:
                if (resultCode == RESULT_OK) {
                    Uri uriImagePicked = data.getData();
                    Intent intent = new Intent(this, AddFaceToPersonActivity.class);
                    intent.putExtra("PersonId", personId);
                    intent.putExtra("PersonGroupId", personGroupId);
                    intent.putExtra("ImageUriStr", uriImagePicked.toString());
                    startActivity(intent);
                }
                break;
            case REQ_CODE_SPEECH_INPUT:
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    editTextPersonName.setText(result.get(0));
                }
                break;

            default:
                break;
        }
    }

    private void deleteSelectedItems() {
        List<String> newFaceIdList = new ArrayList<>();
        List<Boolean> newFaceChecked = new ArrayList<>();
        List<String> faceIdsToDelete = new ArrayList<>();
        for (int i = 0; i < faceGridViewAdapter.faceChecked.size(); ++i) {
            boolean checked = faceGridViewAdapter.faceChecked.get(i);
            if (checked) {
                String faceId = faceGridViewAdapter.faceIdList.get(i);
                faceIdsToDelete.add(faceId);
                new DeleteFaceTask(personGroupId, personId).execute(faceId);
            } else {
                newFaceIdList.add(faceGridViewAdapter.faceIdList.get(i));
                newFaceChecked.add(false);
            }
        }

        StorageHelper.deleteFaces(faceIdsToDelete, personId, this);

        faceGridViewAdapter.faceIdList = newFaceIdList;
        faceGridViewAdapter.faceChecked = newFaceChecked;
        faceGridViewAdapter.notifyDataSetChanged();
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

    private class FaceGridViewAdapter extends BaseAdapter {
        List<String> faceIdList;
        List<Boolean> faceChecked;
        boolean longPressed;

        FaceGridViewAdapter() {
            longPressed = false;
            faceIdList = new ArrayList<>();
            faceChecked = new ArrayList<>();

            Set<String> faceIdSet = StorageHelper.getAllFaceIds(personId, PersonActivity.this);
            for (String faceId: faceIdSet) {
                faceIdList.add(faceId);
                faceChecked.add(false);
            }
        }

        @Override
        public int getCount() {
            return faceIdList.size();
        }

        @Override
        public Object getItem(int position) {
            return faceIdList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // set the item view
            if (convertView == null) {
                LayoutInflater layoutInflater
                        = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(
                        R.layout.item_face_with_checkbox, parent, false);
            }
            convertView.setId(position);

            Uri uri = Uri.parse(StorageHelper.getFaceUri(
                    faceIdList.get(position), PersonActivity.this));
            ((ImageView)convertView.findViewById(R.id.image_face)).setImageURI(uri);

            // set the checked status of the item
            CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.checkbox_face);
            if (longPressed) {
                checkBox.setVisibility(View.VISIBLE);

                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        faceChecked.set(position, isChecked);
                    }
                });
                checkBox.setChecked(faceChecked.get(position));
            } else {
                checkBox.setVisibility(View.INVISIBLE);
            }

            //*/0828
          /*  SharedPreferences insert = getSharedPreferences("test", MODE_PRIVATE);
            Boolean input = insert.getBoolean("input",false);
            if(input == true){
                test();
            }
            //

            Boolean end = insert.getBoolean("end",false);
            Boolean group = insert.getBoolean("group",false);
            if(group != true){
                if(end == true){

                    if (personId == null) {
                        new AddPersonTask(false).execute(personGroupId);
                    } else {
                        doneAndSave();
                    }
                }}*/


            return convertView;
        }
    }
    //지은: 전 Activity가 finish() 되었을때
    @Override
    protected void onRestart() {
        super.onRestart();
       /* SharedPreferences insert = getSharedPreferences("test", MODE_PRIVATE);
        Boolean end = insert.getBoolean("end",false);
        Boolean group = insert.getBoolean("group",false);
        if(group != true){
            if(end == true){

                Intent intent = new Intent(PersonActivity.this,LearningActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }
        }else{*/
            if (personId == null) {
                new AddPersonTask(false).execute(personGroupId);
            } else {
                doneAndSave();
            }
       // }

    }
    //0825
    public void test(){

        //*/
        SharedPreferences insert = getSharedPreferences("test", MODE_PRIVATE);
        SharedPreferences.Editor editor = insert.edit();
        editor.putBoolean("input", false); //First라는 key값으로 infoFirst 데이터를 저장한다.
        editor.commit(); //완료한다.
        //*/
        //0823 조건걸어야함.
        Intent learningIntent = getIntent();
        String bmUri = learningIntent.getStringExtra("bitmap");
        String name = learningIntent.getStringExtra("name");

        Intent intent = new Intent(this, AddFaceToPersonActivity.class);
        intent.putExtra("PersonId", personId);
        intent.putExtra("PersonGroupId", personGroupId);
        intent.putExtra("ImageUriStr", bmUri);
        intent.putExtra("bitmap",bmUri);
        intent.putExtra("name",name);
        startActivity(intent);




    }
}
