package com.microsoft.projectoxford.face.samples.db;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*exif정보가 있는 사진만 분석을 할 수 있는데.. 우리 앱 카메라는 사진 찍어도 사진 안에 exif정보가 없어 ㅠ
* exif정보 넣는거 찾아봐야 할 것 같아 ! 그리고 snow같은 카메라들은 높이,너비 값만 exif정보가 있고 gps정보는 없어. */

public class ExifActivity extends AppCompatActivity {

    TextView textView;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exif);
        textView = (TextView)findViewById(R.id.textView);
        imageView = (ImageView)findViewById(R.id.imageView);


    }

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
        }
    } //onActivityResult() end.

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
            Float Latitude = convertToDegree(exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE)); // 위도
            Float Longitude = convertToDegree(exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE)); //경도
            stringBuffer.append("위도 : "+Latitude+", 경도 : "+Longitude);
            stringBuffer.append("\n[지역명] \n"+location(Latitude,Longitude));
            stringBuffer.append("\n[End information]");

            textView.setText(stringBuffer.toString());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }// photoExifInfo() end.

    private Float convertToDegree(String stringDMS){
        Float result = null;
        String[] DMS = stringDMS.split(",", 3);

        String[] stringD = DMS[0].split("/", 2);
        Double D0 = new Double(stringD[0]);
        Double D1 = new Double(stringD[1]);
        Double FloatD = D0/D1;

        String[] stringM = DMS[1].split("/", 2);
        Double M0 = new Double(stringM[0]);
        Double M1 = new Double(stringM[1]);
        Double FloatM = M0/M1;

        String[] stringS = DMS[2].split("/", 2);
        Double S0 = new Double(stringS[0]);
        Double S1 = new Double(stringS[1]);
        Double FloatS = S0/S1;

        result = new Float(FloatD + (FloatM/60) + (FloatS/3600));

        return result;


    };


    public String location(Float Latitude,Float Longitude ){
        final Geocoder geocoder = new Geocoder(this);
        List<Address> list = null;
        try {
            double d1 = Double.parseDouble(String.valueOf(Latitude));
            double d2 = Double.parseDouble(String.valueOf(Longitude));

            list = geocoder.getFromLocation(
                    d1, // 위도
                    d2, // 경도
                    10); // 얻어올 값의 개수
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("test", "입출력 오류 - 서버에서 주소변환시 에러발생");
        }
        if (list != null) {
            if (list.size()==0) {
                return "해당되는 주소 정보는 없습니다";
            } else {
                // 구는 null값이 나오는 곳이 있고 안나오는 곳이있다.
                // 시까지는 정확한 결과인것 같다.
                // 핸드폰 자체에서도 매번 정확한 gps를 정보를 가져올 수는 없는 것같다. 핸드폰 기본 갤러리에서도 내가 실제 찍은 장소랑 살짝 오차가 있음.
                // 번지까지 정보를 가져오기에는 너무 오차가능성이 높아질 듯.

                StringBuffer stringBuffer= new StringBuffer();
                stringBuffer.append(list.get(0).getCountryName()+" ");//국가명
                stringBuffer.append(list.get(0).getLocality()+" ");//구 메인(시)
                stringBuffer.append(list.get(0).getSubLocality()+" ");//구 서브데이터
                stringBuffer.append(list.get(0).getThoroughfare()+" ");//동
                //stringBuffer.append(list.get(0).getSubThoroughfare());//번지


                return stringBuffer.toString();
                        // list.get(0).getAddressLine(0).toString(); // 전체주소(국가, 시, 구, 동, 번지)
            }
        }
        return "";
    }
}
