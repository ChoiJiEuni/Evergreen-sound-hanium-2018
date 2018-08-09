package com.microsoft.projectoxford.face.samples.db;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.microsoft.projectoxford.face.samples.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExifActivity extends AppCompatActivity {

    TextView textView;
    ImageView imageView;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Uri selectUri  = data.getData();   //사진 uri저장

        try {

            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectUri);
            imageView.setImageBitmap(bitmap);
            InputStream in; //Uri를 Exif객체 인자로 넣을 수 있게 변환.
            ExifInterface exifInterface = null;
            in = getContentResolver().openInputStream(selectUri);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                exifInterface = new ExifInterface(in); // 사진 상세정보 객체
                in.close();

                photoExifInfo(selectUri);
        } }catch (Exception e){
                Toast.makeText(getApplicationContext(),"오류",Toast.LENGTH_LONG).show();
        }}




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exif);
        textView = (TextView)findViewById(R.id.textView);
        imageView = (ImageView)findViewById(R.id.imageView);


    }



    public void onClickgallery(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent,1);
    }

    public void photoExifInfo(Uri selectUri){

        InputStream in; //Uri를 Exif객체 인자로 넣을 수 있게 변환.
        ExifInterface exifInterface = null;
        try {
            in = getContentResolver().openInputStream(selectUri);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                exifInterface = new ExifInterface(in); // 사진 상세정보 객체
            }
            in.close();
            String[] attributes = new String[]{ExifInterface.TAG_APERTURE, //0
                    ExifInterface.TAG_DATETIME, //1 찍은 날짜, 시간
                    ExifInterface.TAG_EXPOSURE_TIME,//2 노광시간
                    ExifInterface.TAG_FLASH, //3 이미지 촬영 시 플래시 상태
                    ExifInterface.TAG_FOCAL_LENGTH, //4 렌즈의 실제 초점거리
                    ExifInterface.TAG_GPS_ALTITUDE, //5 고도
                    ExifInterface.TAG_GPS_ALTITUDE_REF,//6 고도 레퍼런스?
                    ExifInterface.TAG_GPS_DATESTAMP,//7
                    ExifInterface.TAG_GPS_LATITUDE,//8 위도
                    ExifInterface.TAG_GPS_LATITUDE_REF,//9 위도 레퍼런스?
                    ExifInterface.TAG_GPS_LONGITUDE,//10 경도
                    ExifInterface.TAG_GPS_LONGITUDE_REF,//11 경도 레퍼런스?
                    ExifInterface.TAG_GPS_PROCESSING_METHOD,//12 진행중
                    ExifInterface.TAG_GPS_TIMESTAMP,//13
                    ExifInterface.TAG_IMAGE_LENGTH,//14 높이
                    ExifInterface.TAG_IMAGE_WIDTH,//15 너비
                    ExifInterface.TAG_ISO,//16 IOS 감도.
                    ExifInterface.TAG_MAKE, //17 카메라 제조업체
                    ExifInterface.TAG_MODEL,//18 카메라 모델
                    ExifInterface.TAG_ORIENTATION,//19
                    ExifInterface.TAG_WHITE_BALANCE}; //20 화이트 밸런스 모드

            String[] info = new String[] {"TAG_APERTURE ",
                    "찍은 날짜, 시간 ",
                    "노광시간 ",
                    "이미지 촬영 시 플래시 상태 ",
                    "렌즈의 실제 초점거리 ",
                    "고도 ",
                    "고도 레퍼런스 ",
                    "TAG_GPS_DATESTAMP ",
                    "위도 ",
                    "위도 레퍼런스 ",
                    "경도 ",
                    "경도 레퍼런스 ",
                    "진행중 ",
                    "TAG_GPS_TIMESTAMP ",
                    "높이 ",
                    "너비 ",
                    "IOS 감도 ",
                    "카메라 제조업체 ",
                    "카메라 모델 ",
                    "TAG_ORIENTATION ",
                    "화이트 밸런스 모드 "
            };

            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("[Exif information] \n");

            for (int i = 0; i < attributes.length; i++) {
                String value = exifInterface.getAttribute(attributes[i]);
                stringBuffer.append("["+i + "] ");
                stringBuffer.append(info[i]+": ");
                if (value != null) {
                    stringBuffer.append(value);
                }
                stringBuffer.append("\n");
            }
            stringBuffer.append("\n[End information] \n" + attributes.length + "");
            textView.setText(stringBuffer.toString());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
