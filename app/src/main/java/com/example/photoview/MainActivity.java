package com.example.photoview;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @BindView(R.id.btn_take_picture)
    Button mBtnTakePicture;
    @BindView(R.id.iv_take_picture)
    ImageView mIvTakePicture;
    @BindView(R.id.btn_pick_picture)
    Button mBtnPickPicture;
    @BindView(R.id.iv_pick_picture)
    ImageView mIvPickPicture;

    private static final int REQUEST_CODE_CHOOSE_PICTURE = 0;
    private static final int REQUEST_CODE_TAKE_PICTURE = 1;

    private static final int REQUEST_CODE_PERMISSION_CAMERA = 10;
    private static final int REQUEST_CODE_PERMISSION_STORAGE = 11;

    private static final String FILE_PROVIDER_NAME = "com.example.photoview.file_provider";

    private Uri mTakePictureImageUri;
    private Uri mPickPictureImageUri;

    private static final String TAKE_PHOTO_FILE_PATH =
            Environment.getExternalStorageDirectory().getAbsolutePath()
                    + "/" + "test_take_picture.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        checkStoragePermission();
    }

    @OnClick({R.id.btn_take_picture, R.id.iv_take_picture, R.id.btn_pick_picture, R.id.iv_pick_picture})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_take_picture:
                checkCameraPermission();
                break;
            case R.id.iv_take_picture:
                PhotoActivity.startPhotoActivity(this, TAKE_PHOTO_FILE_PATH);
                break;
            case R.id.btn_pick_picture:
                selectPicture();
                break;
            case R.id.iv_pick_picture:
                PhotoActivity.startPhotoActivity(this, mPickPictureImageUri);
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        Log.d(TAG, "onRequestPermissionsResult: requestCode: " + requestCode);
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION_CAMERA:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePicture();
                } else {
                    Toast.makeText(this, "need camera permission", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            case REQUEST_CODE_PERMISSION_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    finish();
                }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: requestCode: " + requestCode);

        switch (requestCode) {
            case REQUEST_CODE_CHOOSE_PICTURE:
                if (resultCode == Activity.RESULT_OK) {
                    mPickPictureImageUri = data.getData();
                    Log.d(TAG, "onActivityResult: mPickPictureImageUri: " + mPickPictureImageUri);
                    if (mPickPictureImageUri != null) {
                        mIvPickPicture.setImageURI(mPickPictureImageUri);
                        mIvPickPicture.setVisibility(View.VISIBLE);
                    }

                }
                break;
            case REQUEST_CODE_TAKE_PICTURE:
                if (resultCode == Activity.RESULT_OK) {
                    mTakePictureImageUri = Uri.fromFile(new File(TAKE_PHOTO_FILE_PATH));
                    if (null != mTakePictureImageUri) {
                        mIvTakePicture.setImageURI(mTakePictureImageUri);
                        mIvTakePicture.setVisibility(View.VISIBLE);
                    }
                }
                break;
            default:
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void checkStoragePermission() {
        if (PackageManager.PERMISSION_GRANTED !=
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE_PERMISSION_STORAGE);
        }
    }

    private void checkCameraPermission() {
        if (PackageManager.PERMISSION_GRANTED !=
                ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_CODE_PERMISSION_CAMERA);

        } else {
            takePicture();
        }
    }

    private void takePicture() {
        File file = new File(TAKE_PHOTO_FILE_PATH);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mTakePictureImageUri = FileProvider.getUriForFile(this,
                    FILE_PROVIDER_NAME,
                    file);//通过FileProvider创建一个content类型的Uri
        } else {
            mTakePictureImageUri = Uri.fromFile(file);
        }

        //调用系统相机
        Intent intentCamera = new Intent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intentCamera.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
        }
        intentCamera.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        //将拍照结果保存至photo_file的Uri中，不保留在相册中
        intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, mTakePictureImageUri);
        startActivityForResult(intentCamera, REQUEST_CODE_TAKE_PICTURE);
    }

    private void selectPicture() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE_CHOOSE_PICTURE);
    }
}
