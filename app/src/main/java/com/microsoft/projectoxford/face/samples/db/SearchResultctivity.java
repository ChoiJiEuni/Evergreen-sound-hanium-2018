package com.microsoft.projectoxford.face.samples.db;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.microsoft.projectoxford.face.samples.R;
import com.microsoft.projectoxford.face.samples.ui.ImagePopup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class SearchResultctivity extends AppCompatActivity {

    String myJSON;
    public Bitmap b;
    private static final String TAG_RESULTS="evergreen";
    private static final String TAG_PATH = "path";
    JSONArray peoples = null;
    //ArrayList<HashMap<String, String>> personList;
    ArrayList<String> personList;
    ArrayList<HashMap<String, Bitmap>> personListBit;
    //ListView list;
    GridView gv;
    Cursor imageCursor = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_resultctivity);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("");
        Toast.makeText(getApplicationContext(),"검색 결과 입니다.\n종료하시려면 뒤로가기 버튼을 눌러주세요.",Toast.LENGTH_LONG).show();

        //list = (ListView) findViewById(R.id.listView);
        //personList = new ArrayList<HashMap<String,String>>();//ArrayList<String>
        personList = new ArrayList<String>();
        personListBit = new ArrayList<HashMap<String,Bitmap>>();
        Intent intent = getIntent();

        SharedPreferences search = getSharedPreferences("searchSource", MODE_PRIVATE);

        String img_date= search.getString("date","");
        String img_location= search.getString("location","");
        String img_person= search.getString("person","");

        SharedPreferences sharedPreferences = getSharedPreferences("USER",MODE_PRIVATE);
        if(!(sharedPreferences.getString("ID","").equals(""))){
            String userName = sharedPreferences.getString("ID","");
            String DatabaseName = userName+"_db";
            GetDataJSON task = new GetDataJSON();
            task.execute("http://14.63.195.105/showTest.php",img_date,img_location,img_person, userName,"1111",DatabaseName);
        }



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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try{
            imageCursor.close();
        }catch (Exception e){
            //Toast.makeText(getApplicationContext(),"cursor 오류",Toast.LENGTH_LONG).show();
        }
        SharedPreferences search = getSharedPreferences("searchSource", MODE_PRIVATE);
        SharedPreferences.Editor editor = search.edit();
        editor.clear();
        editor.putString("date","");
        editor.putString("location","");
        editor.putString("person","");
        editor.commit();
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
        private ArrayList<String>  personList;
        Context mContext = null;

        ImageAdapter(Context c,ArrayList<String> personList) {
            mContext = c;
            //thumbsDataList = new ArrayList<String>();
            //thumbsIDList = new ArrayList<String>();
            this.personList = personList;
            //getThumbInfo(thumbsIDList, thumbsDataList);
        }

        //클릭한 사진 불러오는 함수
        public final void callImageViewer(int selectedIndex) {
            Intent i = new Intent(getApplicationContext(), ImagePopup.class);
            String imgPath = personList.get(selectedIndex);
            i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            i.putExtra("filename", imgPath);
            startActivityForResult(i, RESULT_OK);
        }

        public boolean deleteSelected(int sIndex) {
            return true;
        }

        public int getCount() {
            return (null != personList) ? personList.size() : 0;
        }

        public Object getItem(int position) {
            return (null != personList) ? personList.get(position) : 0;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView =  null;
            if (convertView != null) {
                //imageView = new ImageView(getApplicationContext());

                imageView = (ImageView) convertView;


                // imageView.setLayoutParams(new GridView.LayoutParams(95, 95));
                // imageView.setAdjustViewBounds(false);
                // imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                // imageView.setPadding(2, 2, 2, 2);
            } else{
                imageView = new ImageView(getApplicationContext());
            }

            BitmapFactory.Options bo = new BitmapFactory.Options();
            bo.inSampleSize = 8;
            /**이미지 상황에 맞게 회전 및 불러오기*/
            try{
                String imgPath = personList.get(position);
                Bitmap bmp = BitmapFactory.decodeFile(personList.get(position), bo);
                ExifInterface exif = new ExifInterface(imgPath);
                int exifOrientation = exif.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                int exifDegree = exifOrientationToDegrees(exifOrientation);
                bmp = rotate(bmp, exifDegree);

                if (bmp != null) {
                    Bitmap resized = Bitmap.createScaledBitmap(bmp, 500, 500, true);
                    imageView.setImageBitmap(resized); //갤러리에 보임
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

    protected void showList(){
        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            peoples = jsonObj.getJSONArray(TAG_RESULTS);

            for(int i=0;i<peoples.length();i++){
                JSONObject c = peoples.getJSONObject(i);
                String path = c.getString(TAG_PATH);

                //HashMap<String,String> persons  = new HashMap<String,String>();
                //HashMap<String,Bitmap> personBitmaps = new HashMap<String, Bitmap>();
                //b = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(path));

                //personBitmaps.put("bitmap",MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(path)));
                // personBitmaps.put(TAG_PATH, b);
                //persons.put(TAG_PATH,path);

                // personList.add(persons);
                personList.add(path);
                //personListBit.add(personBitmaps);//
            }
            if(personList.size() == 0){
                Toast.makeText(getApplicationContext(),"검색된 사진이 존재하지 않습니다.",Toast.LENGTH_LONG).show();
            }
            gv = (GridView) findViewById(R.id.SearchImgGridView);
            final ImageAdapter ia = new ImageAdapter(this,personList);
            gv.setAdapter(ia);
            gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    ia.callImageViewer(position);
                }
            });
            /*ListAdapter adapter = new SimpleAdapter(
                    SearchResultctivity.this, personList, R.layout.search_item,
                    new String[]{TAG_PATH},
                    new int[]{R.id.path}
            );*/

            //ImageAdapter ia = new ImageAdapter(this);
            //gv.setAdapter(ImageAdapter);
            //list.setAdapter(adapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    } // showList() END.


    class GetDataJSON extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            String uri = params[0];
            String img_date = params[1];
            String img_location = params[2];
            String img_person = params[3];
            String username = params[4];
            String pass = params[5];
            String dbname = params[6];
            String postParameters = "&img_date=" + img_date
                    +"&img_location=" + img_location
                    +"&img_person=" + img_person
                    +"&user_name=" + username
                    +"&user_pass=" + pass
                    +"&db_name=" + dbname; // php에 보낼값.
            BufferedReader bufferedReader = null;
            try {
                URL url = new URL(uri);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setReadTimeout(5000);
                con.setConnectTimeout(5000);
                con.setRequestMethod("POST");
                con.connect();

                OutputStream outputStream = con.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = con.getResponseCode();

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = con.getInputStream();
                }
                else{
                    inputStream = con.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");

                StringBuilder sb = new StringBuilder();

                bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                String json;
                while((json = bufferedReader.readLine())!= null){
                    sb.append(json+"\n");
                }

                return sb.toString().trim();

            }catch(Exception e){
                return null;
            }



        }

        @Override
        protected void onPostExecute(String result){
            myJSON=result;
            showList();
        }
    } // GetDataJSON() END.
}
