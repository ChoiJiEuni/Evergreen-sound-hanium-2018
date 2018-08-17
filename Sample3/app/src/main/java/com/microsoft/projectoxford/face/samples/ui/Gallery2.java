package com.microsoft.projectoxford.face.samples.ui;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.microsoft.projectoxford.face.samples.R;

import java.io.File;
import java.util.ArrayList;

public class Gallery2 extends AppCompatActivity {
    public String basePath = null;
    public GridView mGridView;
    public CustomImageAdapter mCustomImageAdapter;
    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery2);

        mContext=this;

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");

        if (! mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
            }
        }
        basePath = mediaStorageDir.getPath();
        Log.d("chae",basePath);
        mGridView = (GridView)findViewById(R.id.gridview); // .xml의 GridView와 연결
        mCustomImageAdapter = new CustomImageAdapter(this, basePath); // 앞에서 정의한 Custom Image Adapter와 연결
        mGridView.setAdapter(mCustomImageAdapter); // GridView가 Custom Image Adapter에서 받은 값을 뿌릴 수 있도록 연결
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mCustomImageAdapter.callImageViewer(position);
                Toast.makeText(getApplicationContext(), mCustomImageAdapter.getItemPath(position), Toast.LENGTH_LONG).show();
            }
        });
    }
    public class CustomImageAdapter extends BaseAdapter {
        int CustomGalleryItemBg; // 앞서 정의해 둔 attrs.xml의 resource를 background로 받아올 변수 선언
        String mBasePath; // CustomGalleryAdapter를 선언할 때 지정 경로를 받아오기 위한 변수
        Context mContext; // CustomGalleryAdapter를 선언할 때 해당 activity의 context를 받아오기 위한 context 변수
        String[] mImgs; // 위 mBasePath내의 file list를 String 배열로 저장받을 변수
        Bitmap bm; // 지정 경로의 사진을 Bitmap으로 받아오기 위한 변수

        public String TAG = "Gallery Adapter Example :: ";
        //클릭한 사진 불러오는 함수
        public final void callImageViewer(int selectedIndex){
            Intent i = new Intent(mContext, ImagePopup.class);
            String imgPath = getItemPath(selectedIndex);
                    //mBasePath+ File.separator +mImgs[selectedIndex];
                    //getImageInfo(imgData, geoData, thumbsIDList.get(selectedIndex));
            i.putExtra("filename", imgPath);
            startActivityForResult(i, 1);
        }
        public String getItemPath(int p){
            String imgPath = mBasePath+ File.separator +mImgs[p];
            return imgPath;
        }
        public CustomImageAdapter(Context context, String basepath){ // CustomGalleryAdapter의 생성자
            this.mContext = context;
            this.mBasePath = basepath;

            File file = new File(mBasePath); // 지정 경로의 directory를 File 변수로 받아
            if(!file.exists()){
                if(!file.mkdirs()){
                    Log.d(TAG, "failed to create directory");
                }
            }
            mImgs = file.list(); // file.list() method를 통해 directory 내 file 명들을 String[] 에 저장

            /* 앞서 정의한 attrs.xml에서 gallery array의 배경 style attribute를 받아옴 */
            //TypedArray array = mContext.obtainStyledAttributes(R.styleable.GalleryTheme);
            //CustomGalleryItemBg = array.getResourceId(R.styleable.GalleryTheme_android_galleryItemBackground, 0);
            //array.recycle();
        }

        @Override
        public int getCount() { // Gallery array의 객체 갯수를 앞서 세어 둔 file.list()를 받은 String[]의 원소 갯수와 동일하다는 가정 하에 반환
            return mImgs.length;
        }

        @Override
        public Object getItem(int position) { // Gallery array의 해당 position을 반환
            return position;
        }

        @Override
        public long getItemId(int position) { // Gallery array의 해당 position을 long 값으로 반환
            return position;
        }


        // Override this method according to your need
        // 지정 경로 내 사진들을 보여주는 method.
        // Bitmap을 사용할 경우, memory 사용량이 커서 Thumbnail을 사용하거나 크기를 줄일 필요가 있음
        // setImageDrawable()이나 setImageURI() 등의 method로 대체 가능
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                // if it's not recycled, initialize some attributes
                imageView = new ImageView(mContext);
            } else {
                imageView = (ImageView) convertView;
            }
            //bm = BitmapFactory.decodeFile(mBasePath + File.separator + mImgList[position]);
            bm = BitmapFactory.decodeFile(mBasePath+ File.separator +mImgs[position]);
            Bitmap mThumbnail = ThumbnailUtils.extractThumbnail(bm, 300, 300);
            imageView.setPadding(8, 8, 8, 8);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setLayoutParams(new GridView.LayoutParams(GridView.LayoutParams.MATCH_PARENT, GridView.LayoutParams.MATCH_PARENT));
            imageView.setImageBitmap(mThumbnail);
            return imageView;
        }

    }
}
