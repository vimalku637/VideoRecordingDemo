package com.indevconsultancy.recordvideodemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    ShapeableImageView imgVideo,imgPlay;
    MaterialButton btnRecordVideo;
    static final int REQUEST_VIDEO_CAPTURE = 1;
    String base64Video="";
    private static final int REQUEST = 112;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.record_video);

        appPermission();

        imgVideo=findViewById(R.id.imgVideo);
        imgPlay=findViewById(R.id.imgPlay);
        btnRecordVideo=findViewById(R.id.btnRecordVideo);
        btnRecordVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakeVideoIntent();
            }
        });
    }

    private void appPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            Log.d("TAG", "@@@ IN IF Build.VERSION.SDK_INT >= 23");
            String[] PERMISSIONS = {
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_FINE_LOCATION
            };
            if (!hasPermissions(this, PERMISSIONS)) {
                Log.d("TAG", "@@@ IN IF hasPermissions");
                ActivityCompat.requestPermissions((Activity) this, PERMISSIONS, REQUEST);
            } else {
                Log.d("TAG", "@@@ IN ELSE hasPermissions");
                //callNextActivity();
            }
        } else {
            Log.d("TAG", "@@@ IN ELSE  Build.VERSION.SDK_INT >= 23");
            //callNextActivity();
        }
    }

    private static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void dispatchTakeVideoIntent() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            Uri videoUri = data.getData();
            //videoView.setVideoURI(videoUri);
            Log.e("TAG>>>>", "onActivityResult:Video>> "+videoUri.toString());
            String[] projection = {MediaStore.Video.Media.DATA, MediaStore.Video.Media.SIZE, MediaStore.Video.Media.DURATION};
            Cursor cursor = managedQuery(videoUri, projection, null, null, null);

            cursor.moveToFirst();
            String filePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
            Log.d("File Name:",filePath);

            Bitmap thumb = ThumbnailUtils.createVideoThumbnail(filePath, MediaStore.Video.Thumbnails.MINI_KIND);
            // Setting the thumbnail of the video in to the image view
            imgVideo.setImageBitmap(thumb);
            imgPlay.setVisibility(View.VISIBLE);
            InputStream inputStream = null;
            // Converting the video in to the bytes
            try
            {
                inputStream = getContentResolver().openInputStream(videoUri);
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
            int len = 0;
            try
            {
                while ((len = inputStream.read(buffer)) != -1)
                {
                    byteBuffer.write(buffer, 0, len);
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            System.out.println("converted!");

            String videoData="";
            //Converting bytes into base64
            videoData = Base64.encodeToString(byteBuffer.toByteArray(), Base64.DEFAULT);
            Log.d("VideoData**>  " , videoData);

            String sinSaltoFinal2 = videoData.trim();
            String sinsinSalto2 = sinSaltoFinal2.replaceAll("\n", "");
            Log.d("VideoData**>  " , sinsinSalto2);

            base64Video = sinsinSalto2;
            Log.e("TAG>>", "base64Video:>> "+base64Video);
            Log.e("TAG>>", "thumnailImage:>> "+bitMapToString(thumb));
        }
    }

    public String bitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

}