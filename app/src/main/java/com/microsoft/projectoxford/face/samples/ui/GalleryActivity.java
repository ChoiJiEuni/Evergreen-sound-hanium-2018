package com.microsoft.projectoxford.face.samples.ui;


import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.app.Activity;
import java.io.InputStream;
import android.content.Context;
import java.util.List;
import java.io.IOException;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.microsoft.projectoxford.face.samples.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GalleryActivity extends AppCompatActivity {
    ImageAdapter ia;

    //String result;

    public String timee;
    public Float Latitude = Float.valueOf(0); // 위도
    public Float Longitude =  Float.valueOf(0); //경도
    public String strLocation = "";
  //  private Context mContext;
    Cursor imageCursor;
    Uri imageUri = null;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        imageCursor.close();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("chae","oncreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery3);
        setTitle("갤러리 펼쳐 보기");
       // mContext = this;\
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent_test = getIntent();
        imageUri = intent_test.getData(); // 찍은 사진 사진 uri

        GridView gv = (GridView) findViewById(R.id.ImgGridView);
        ia = new ImageAdapter(this);
        gv.setAdapter(ia);
        ia.notifyDataSetChanged();
        gv.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                ia.callImageViewer(position);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    /**
     * ==========================================
     * Adapter class
     * ==========================================
     */
    public class ImageAdapter extends BaseAdapter {
        private String imgData;
        private String geoData;
        private ArrayList<String> thumbsDataList;
        private ArrayList<String> thumbsIDList;




        ImageAdapter(Context c) {
            // mContext = c;
            thumbsDataList = new ArrayList<String>();
            thumbsIDList = new ArrayList<String>();
            getThumbInfo(thumbsIDList, thumbsDataList);

        }
        //클릭한 사진 불러오는 함수
        public final void callImageViewer(int selectedIndex) {
            Intent i = new Intent(getApplicationContext(), ImagePopup.class);
            String imgPath = getImageInfo(imgData, geoData, thumbsIDList.get(selectedIndex));
            i.putExtra("filename", imgPath);
            startActivityForResult(i, RESULT_OK);
        }

        public boolean deleteSelected(int sIndex) {
            return true;
        }

        public int getCount() {
            return thumbsIDList.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            // 이미지 띄울 때 출력이니까 여기에 떠야하는 거 맞음
            ImageView imageView;
            if (convertView == null) {
                imageView = new ImageView(getApplicationContext());
                // imageView.setLayoutParams(new GridView.LayoutParams(95, 95));
                // imageView.setAdjustViewBounds(false);
                // imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                // imageView.setPadding(2, 2, 2, 2);
            } else {
                imageView = (ImageView) convertView;
            }
            BitmapFactory.Options bo = new BitmapFactory.Options();
            bo.inSampleSize = 8;
            /**이미지 상황에 맞게 회전 및 불러오기*/
            try{
            String imgPath = getImageInfo(imgData, geoData, thumbsIDList.get(position));
            Bitmap bmp = BitmapFactory.decodeFile(thumbsDataList.get(position), bo);
            ExifInterface exif = new ExifInterface(imgPath);
            int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            int exifDegree = exifOrientationToDegrees(exifOrientation);
            bmp = rotate(bmp, exifDegree);

            if (bmp != null) {
                Bitmap resized = Bitmap.createScaledBitmap(bmp, 500, 500, true);
                imageView.setImageBitmap(resized); //갤러리에 보임
                StringBuffer time = new StringBuffer();

                try {

                    timee=exif.getAttribute(ExifInterface.TAG_DATETIME).toString();
                    time.append(timee.substring(0,4));
                    time.append("년  ");
                    time.append(timee.substring(4,7));
                    time.append("월  ");
                    time.append(timee.substring(7,10));
                    time.append("일  ");
                    time.append(timee.substring(10,13));
                    time.append("시");


                } catch (Exception e) {

                }
                imageView.setContentDescription(strLocation+" 시간 "+time.toString());

            }}catch(Exception e){

            }
            return imageView;
        }


        private Uri getImageUri(Context context, Bitmap inImage) {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
            return Uri.parse(path);
        }

        private Float convertToDegree(String stringDMS){ // 메소드니까 위치 상관 없을 듯
            Float result = null;
            try{
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
            }catch (Exception e){
                Toast.makeText(getApplicationContext(),"위치변환오류",Toast.LENGTH_LONG).show();
            }
            return result;
        }

        public String location(Float Latitude,Float Longitude ){ // 얘도 메소드니까 위치 상관 x
                    final Geocoder geocoder = new Geocoder(GalleryActivity.this);
                    List<Address> list = null;
                    try {
                        double d1 = Double.parseDouble(String.valueOf(Latitude));
                        double d2 = Double.parseDouble(String.valueOf(Longitude));

                        list = geocoder.getFromLocation(
                        d1, // 위도
                        d2, // 경도
                        10); // 얻어올 값의 개수
                        //개수는 필요없음

            } catch (IOException e) {
                e.printStackTrace();
                Log.e("log", "입출력 오류 - 서버에서 주소변환시 에러발생");
            }
            if (list != null) {
                if (list.size()==0) {
                    return "해당되는 주소 정보는 없습니다";
                } else {
                    StringBuffer stringBuffer= new StringBuffer();
                    stringBuffer.append(list.get(0).getCountryName()+" ");//국가명
                    stringBuffer.append(list.get(0).getLocality()+" ");//구 메인(시)
                    if(!(list.get(0).getSubLocality().equals(null))){
                        stringBuffer.append(list.get(0).getSubLocality()+" ");//구 서브데이터
                    }
                    if(!(list.get(0).getThoroughfare().equals(null))){
                        stringBuffer.append(list.get(0).getThoroughfare()+" ");//동
                    }
                    return stringBuffer.toString();
                }
            }
            return "";
        }


        public int exifOrientationToDegrees(int exifOrientation)
        {
            if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_90)
            {
                return 90;
            }
            else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_180)
            {
                return 180;
            }
            else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_270)
            {
                return 270;
            }
            return 0;
        }
        public Bitmap rotate(Bitmap bitmap, int degrees)
        {
            if(degrees != 0 && bitmap != null)
            {
                Matrix m = new Matrix();
                m.setRotate(degrees, (float) bitmap.getWidth() / 2,
                        (float) bitmap.getHeight() / 2);

                try
                {
                    Bitmap converted = Bitmap.createBitmap(bitmap, 0, 0,
                            bitmap.getWidth(), bitmap.getHeight(), m, true);
                    if(bitmap != converted)
                    {
                        bitmap.recycle();
                        bitmap = converted;
                    }
                }
                catch(OutOfMemoryError ex)
                {
                    // 메모리가 부족하여 회전을 시키지 못할 경우 그냥 원본을 반환합니다.
                }
            }
            return bitmap;
        }

        private void getThumbInfo(ArrayList<String> thumbsIDs, ArrayList<String> thumbsDatas) {
            String sortOrderDESC = MediaStore.Images.Media._ID + " COLLATE LOCALIZED DESC";//최신순으로 정렬

            String[] proj = {MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media.DISPLAY_NAME,
                    MediaStore.Images.Media.SIZE};

            imageCursor = managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    proj, null, null, sortOrderDESC);

            if (imageCursor != null && imageCursor.moveToFirst()) {
                String title;
                String thumbsID;
                String thumbsImageID;
                String thumbsData;
                String data;
                String imgSize;

                int thumbsIDCol = imageCursor.getColumnIndex(MediaStore.Images.Media._ID);
                int thumbsDataCol = imageCursor.getColumnIndex(MediaStore.Images.Media.DATA);
                int thumbsImageIDCol = imageCursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME);
                int thumbsSizeCol = imageCursor.getColumnIndex(MediaStore.Images.Media.SIZE);
                int num = 0;
                do {
                    thumbsID = imageCursor.getString(thumbsIDCol);
                    thumbsData = imageCursor.getString(thumbsDataCol);
                    thumbsImageID = imageCursor.getString(thumbsImageIDCol);
                    imgSize = imageCursor.getString(thumbsSizeCol);
                    num++;
                    if (thumbsImageID != null&&thumbsData.contains("evergreen")) {
                        thumbsIDs.add(thumbsID);
                        thumbsDatas.add(thumbsData);
                    }
                } while (imageCursor.moveToNext());
            }
            //imageCursor.close();
            return;
        }

        private String getImageInfo(String ImageData, String Location, String thumbID) {
            String imageDataPath = null;
            String sortOrderDESC = MediaStore.Images.Media._ID + " COLLATE LOCALIZED DESC";//최신순으로 정렬
            String[] proj = {MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media.DISPLAY_NAME,
                    MediaStore.Images.Media.SIZE};
            Cursor imageCursor = managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    proj, "_ID='" + thumbID + "'", null, sortOrderDESC);

            if (imageCursor != null && imageCursor.moveToFirst()) {
                if (imageCursor.getCount() > 0) {
                    int imgData = imageCursor.getColumnIndex(MediaStore.Images.Media.DATA);
                    imageDataPath = imageCursor.getString(imgData);
                    Log.d("chae", imageDataPath);
                }
            }


           // imageCursor.close();
            return imageDataPath;
        }
    }
}
