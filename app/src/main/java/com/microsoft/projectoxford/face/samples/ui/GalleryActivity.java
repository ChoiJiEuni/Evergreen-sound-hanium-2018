package com.microsoft.projectoxford.face.samples.ui;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;

import com.microsoft.projectoxford.face.samples.R;

public class GalleryActivity extends Activity {
    ImageAdapter ia;
  //  private Context mContext;
  Cursor imageCursor;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        imageCursor.close();
    }

    @Override
    protected void onRestart() {
        this.onCreate(null);
        super.onRestart();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("chae","oncreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery3);
        setTitle("갤러리 펼쳐 보기");
       // mContext = this;

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
            int exifOrientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            int exifDegree = exifOrientationToDegrees(exifOrientation);
            bmp = rotate(bmp, exifDegree);

            if (bmp != null) {
                Bitmap resized = Bitmap.createScaledBitmap(bmp, 500, 500, true);
                imageView.setImageBitmap(resized); //갤러리에 보임
                imageView.setContentDescription(imgPath);
            }}catch(Exception e){

            }
            return imageView;

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
