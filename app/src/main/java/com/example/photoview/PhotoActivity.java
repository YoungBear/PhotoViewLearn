package com.example.photoview;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PhotoActivity extends AppCompatActivity {

    private static final String TAG = "PhotoActivity";

    private static final String IMAGE_PATH = "image_path";
    private static final String IMAGE_URI = "image_uri";

    @BindView(R.id.photo_view)
    PhotoView mPhotoView;

    public static void startPhotoActivity(Context context, String path) {
        Intent intent = new Intent(context, PhotoActivity.class);
        intent.putExtra(IMAGE_PATH, path);
        context.startActivity(intent);
    }

    public static void startPhotoActivity(Context context, Uri imageUri) {
        Intent intent = new Intent(context, PhotoActivity.class);
        intent.putExtra(IMAGE_URI, imageUri);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        ButterKnife.bind(this);
        initPhotoView();
    }

    private void initPhotoView() {

        Intent intent = getIntent();
        if (null != intent) {
            if (intent.hasExtra(IMAGE_PATH)) {
                String path = intent.getStringExtra(IMAGE_PATH);
                Uri imageUri = Uri.fromFile(new File(path));
                if (null != imageUri) {
                    mPhotoView.setImageURI(imageUri);
                }
            } else if (intent.hasExtra(IMAGE_URI)) {
                Uri imageUri = intent.getParcelableExtra(IMAGE_URI);
                if (null != imageUri) {
                    mPhotoView.setImageURI(imageUri);
                }
            }

        }
    }

    @Override
    protected void onDestroy() {
        mPhotoView.destroyDrawingCache();
        super.onDestroy();
    }

    @OnClick(R.id.photo_view)
    public void onViewClicked() {
        finish();
    }
}
