package com.microsoft.projectoxford.face.samples.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.microsoft.projectoxford.face.samples.R;
import com.microsoft.projectoxford.face.samples.db.DBMainActivity;
import com.microsoft.projectoxford.face.samples.db.DBphpActivity;
import com.microsoft.projectoxford.face.samples.db.ExifActivity;
import com.microsoft.projectoxford.face.samples.helper.StorageHelper;
import com.microsoft.projectoxford.face.samples.persongroupmanagement.PersonGroupListActivity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static android.speech.tts.TextToSpeech.ERROR;

public class MainActivity extends AppCompatActivity {

    String mPersonGroupId;
    boolean detected;

    PersonGroupListAdapter mPersonGroupListAdapter;

    /////////////추가용
    private static final int REQUEST_TAKE_PHOTO = 0;
    private TextToSpeech tts;


    // The URI of photo taken with camera
    private Uri mUriPhotoTaken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        detected = false;

        /////추가용
        // TTS를 생성하고 OnInitListener로 초기화 한다.
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != ERROR) {
                    // 언어를 선택한다.
                    tts.setLanguage(Locale.KOREAN);
                }
            }
        });
    }

    private void setIdentifyButtonEnabledStatus(boolean isEnabled) {
        Button button = (Button) findViewById(R.id.identify);
//  button.setEnabled(isEnabled); 필요 없는 거라서 지워도 되는데 혹시 몰라서 주석 처리함
    }
    private void refreshIdentifyButtonEnabledStatus() {
        if (detected && mPersonGroupId != null) {
            //   setIdentifyButtonEnabledStatus(true);
        } else {
            //   setIdentifyButtonEnabledStatus(false);
        }
    }
    void setPersonGroupSelected(int position) {
        TextView textView = (TextView) findViewById(R.id.text_person_group_selected);
        if (position > 0) {
            String personGroupIdSelected = mPersonGroupListAdapter.personGroupIdList.get(position);
            mPersonGroupListAdapter.personGroupIdList.set(
                    position, mPersonGroupListAdapter.personGroupIdList.get(0));
            mPersonGroupListAdapter.personGroupIdList.set(0, personGroupIdSelected);
            ListView listView = (ListView) findViewById(R.id.list_person_groups_identify);
            listView.setAdapter(mPersonGroupListAdapter);
            setPersonGroupSelected(0);
        } else if (position < 0) {
            setIdentifyButtonEnabledStatus(false);
            textView.setTextColor(Color.RED);
            textView.setText(R.string.no_person_group_selected_for_identification_warning);
        } else {
            mPersonGroupId = mPersonGroupListAdapter.personGroupIdList.get(0);
            String personGroupName = StorageHelper.getPersonGroupName(
                    mPersonGroupId, MainActivity.this);
            //  refreshIdentifyButtonEnabledStatus();
            textView.setTextColor(Color.BLACK);
            textView.setText(String.format("Person group to use: %s", personGroupName));
        }
    }
    /////추가 메소드
    // Save the activity state when it's going to stop.
    //작업이 중지될 때 작업 상태를 저장합니다.
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("ImageUri", mUriPhotoTaken);
    }

    // Recover the saved state when the activity is recreated.
    // 작업을 재생성할 때 저장된 상태를 복구합니다.
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mUriPhotoTaken = savedInstanceState.getParcelable("ImageUri");
    }

    //분석할 사진 촬영 또는 갤러리에서 선택
    public void OnButtonClickedImage(View view) {
       //// Intent intent = new Intent(this, SelectImageActivity.class);
        // 추강

       // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
       // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      /////  startActivity(intent);
        tts.speak("촬영이 시작됩니다. 정면을 응시하여 주세요.",TextToSpeech.QUEUE_FLUSH, null);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(getPackageManager()) != null) {
            // Save the photo taken to a temporary file.
            // File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            try {
                /////////////////////새 저장 폴더 만들기//////////////////////
                //문제있음: 여기서는 폴더가 생기는데 나중에 저장된 경로를 보면 사라져있음
                // File dir = new File(storageDir.getPath(), "evergreen");
                File dir =new File( Environment.getExternalStorageDirectory().getAbsolutePath()+"/evergreen/");
                Log.d("chae",dir+"");

                if(!dir.exists())

                    dir.mkdirs();
                ///////////////////////////////////////////////////////////////
                File file = File.createTempFile("evergreen_", ".jpg", dir);
                mUriPhotoTaken = Uri.fromFile(file);
                Log.d("chae",mUriPhotoTaken+"넘긴거");
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,mUriPhotoTaken));

                intent.putExtra(MediaStore.EXTRA_OUTPUT, mUriPhotoTaken);
                startActivityForResult(intent, REQUEST_TAKE_PHOTO);
            } catch (IOException e) {
                setInfo(e.getMessage());
            }
        }

    }
    private void setInfo(String info) {
        TextView textView = (TextView) findViewById(R.id.info);
        textView.setText(info);
    }
    // 사용자 그룹을 포함하는 ListView의 어댑터입니다.
    private class PersonGroupListAdapter extends BaseAdapter {
        List<String> personGroupIdList;

        // Initialize with detection result.
        PersonGroupListAdapter() {
            personGroupIdList = new ArrayList<>();

            Set<String> personGroupIds
                    = StorageHelper.getAllPersonGroupIds(MainActivity.this);

            for (String personGroupId : personGroupIds) {
                personGroupIdList.add(personGroupId);
                if (mPersonGroupId != null && personGroupId.equals(mPersonGroupId)) {
                    personGroupIdList.set(
                            personGroupIdList.size() - 1,
                            mPersonGroupListAdapter.personGroupIdList.get(0));
                    mPersonGroupListAdapter.personGroupIdList.set(0, personGroupId);
                }
            }
        }

        @Override
        public int getCount() {
            return personGroupIdList.size();
        }

        @Override
        public Object getItem(int position) {
            return personGroupIdList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater layoutInflater =
                        (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.item_person_group, parent, false);
            }
            convertView.setId(position);

            // set the text of the item
            //항목의 텍스트 설정
            String personGroupName = StorageHelper.getPersonGroupName(
                    personGroupIdList.get(position), MainActivity.this);
            int personNumberInGroup = StorageHelper.getAllPersonIds(
                    personGroupIdList.get(position), MainActivity.this).size();
            ((TextView) convertView.findViewById(R.id.text_person_group)).setText(
                    String.format(
                            "%s (Person count: %d)",
                            personGroupName,
                            personNumberInGroup));

            if (position == 0) {
                ((TextView) convertView.findViewById(R.id.text_person_group)).setTextColor(
                        Color.parseColor("#3399FF"));
            }

            return convertView;
        }
    }

    public void OnButtonClickedGallery(View view){
        Intent intent = new Intent(this,Gallery.class);
        startActivity(intent);
    }
    /// 인물 등록하는 화면으로 넘어감!! 기존의 manage person groups 역할!
    public void onButtonAddPerson(View view) {
        Intent intent = new Intent(this, PersonGroupListActivity.class);
        startActivity(intent);

    }
    /// 한이음 서버 데베에는 접근이 안되서 일단은 로컬서버 데베에 저장하는 거로 함.
    public void onButtonAddPHP(View view) {
        Intent intent = new Intent(this,DBMainActivity.class);
        startActivity(intent);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode)
        {
            case REQUEST_TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    Uri imageUri;
                    if (data == null || data.getData() == null) {
                        imageUri = mUriPhotoTaken;
                    } else {
                        imageUri = data.getData();
                    }
                    Intent intent = new Intent(this, IdentificationActivity.class);
                    intent.setData(imageUri);


                    // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    // intent.addFlags(intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivityForResult(intent, RESULT_OK);
                    // finish();
                }
                break;
            default:
                break;
        }
    }
}
