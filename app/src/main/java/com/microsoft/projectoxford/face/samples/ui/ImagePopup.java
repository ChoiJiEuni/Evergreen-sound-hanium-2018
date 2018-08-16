package com.microsoft.projectoxford.face.samples.ui;


import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.microsoft.projectoxford.face.samples.R;

import java.io.File;
import java.io.OutputStream;

public class
ImagePopup extends Activity implements OnClickListener{
    private Context mContext = null;
    private final int imgWidth = 320;
    private final int imgHeight = 372;
    private String imgPath;
    private Bitmap bm;
    MediaPlayer player;
    private static String RECORDED_FILE;//재생될 녹음 파일명

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_popup);
        mContext = this;

        /** 전송메시지 */
        Intent i = getIntent();
        Bundle extras = i.getExtras();
        imgPath = extras.getString("filename");

        /** 완성된 이미지 보여주기  */
        BitmapFactory.Options bfo = new BitmapFactory.Options();
        bfo.inSampleSize = 2;
        ImageView iv = (ImageView)findViewById(R.id.imageView);
        bm = BitmapFactory.decodeFile(imgPath, bfo);
        //Bitmap resized = Bitmap.createScaledBitmap(bm, imgWidth, imgHeight, true);
        iv.setImageBitmap(bm);

        /** 리스트로 가기 버튼 */
        Button btn1 = (Button)findViewById(R.id.btn_back);
        btn1.setOnClickListener(this);
        Button btn2 = (Button)findViewById(R.id.btn_startPlay);
        btn2.setOnClickListener(this);
        Button btn3 = (Button)findViewById(R.id.btn_stopPlay);
        btn3.setOnClickListener(this);
        Button btn4 = (Button)findViewById(R.id.btn_share);
        btn4.setOnClickListener(this);

    }

    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_back:
                Intent intent = new Intent(mContext, Gallery.class);
                startActivity(intent);
                break;
            case R.id.btn_startPlay: //녹음재생
                if (player != null) {
                    player.stop();
                    player.release();
                    player = null;
                }

                Toast.makeText(getApplicationContext(), "녹음된 파일을 재생합니다.", Toast.LENGTH_LONG).show();
                try {
                    player = new MediaPlayer();

                    player.setDataSource(RECORDED_FILE);
                    player.prepare();
                    player.start();
                } catch (Exception e) {
                    Log.e("SampleAudioRecorder", "Audio play failed.", e);
                }
                break;
            case R.id.btn_stopPlay:
                if (player == null)
                    return;

                Toast.makeText(getApplicationContext(), "재생이 중지되었습니다.", Toast.LENGTH_LONG).show();

                player.stop();
                player.release();
                player = null;
                break;
            case R.id.btn_share:
                sendMMS();
                break;
        }
    }
    private void sendMMS(){
        try{
            ContentResolver contentR = this.getContentResolver();

            Uri uri = Uri.parse(imgPath);
            OutputStream outstream;
            try{
                outstream = contentR.openOutputStream(uri);
                bm.compress(Bitmap.CompressFormat.JPEG, 100, outstream);
                outstream.close();
            }catch (Exception e){
                System.err.println(e.toString());
            }
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_STREAM,uri);
            intent.setType("image/jpg");
            this.startActivity(intent);
        }catch(Exception e){
            Toast.makeText(this,"failed",Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}