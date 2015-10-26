package com.example.shristi.myapplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.files.BackendlessFile;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

public class MainActivity extends Activity {

    private ProgressDialog progressDialog;
   // ImageView mImage = (ImageView) findViewById(R.id.camera_image);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String appVersion = "v1";
        Backendless.initApp(this, "4F353D35-3748-11E4-FF69-1C34254BAC00", "067042D3-996E-3921-FFFF-46F091C4F100", appVersion);

        //TextView textMake = (TextView) findViewById(R.id.textMake);
       // Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/verdana.ttf");
        TextView textOr = (TextView) findViewById(R.id.textOr);

       // textOr.setTypeface(typeface);
        //textMake.setTypeface(typeface);

        Button photoBtn = (Button) findViewById(R.id.photoBtn);
         //photoBtn.setTypeface( typeface );
        photoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MakePhotoActivity.class);
                startActivityForResult(intent, Default.MAKE_NEW_PHOTO);
            }
        });
        Button chooseBtn = (Button) findViewById(R.id.chooseBtn);
        //chooseBtn.setTypeface( typeface );
        chooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                //MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                //Intent photoPickerIntent = new Intent( MainActivity.this, MakeChoiceActivity.class);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, Default.SELECT_PHOTO);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch (requestCode) {
            case Default.SELECT_PHOTO:
                if (resultCode == RESULT_OK) {
                    final Uri selectedImage = imageReturnedIntent.getData();

                    final File imageFile = new File(getRealPathFromURI(selectedImage));
                    final String filePath = imageFile.getPath();
                    progressDialog = ProgressDialog.show(MainActivity.this, "", "Loading", true);
                    Backendless.Files.upload(imageFile, Default.DEFAULT_PATH_ROOT, new AsyncCallback<BackendlessFile>() {
                        @Override
                        public void handleResponse(BackendlessFile response) {
                            String photoBrowseUrl = response.getFileURL();
                            //Toast.makeText(MainActivity.this, photoBrowseUrl, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent();
                         //   intent.putExtra(Default.PHOTO_BROWSE_URL, photoBrowseUrl);
                            TextView t = (TextView) findViewById(R.id.disp);
                            t.setText(getRealPathFromURI(selectedImage));

                          //  intent.putExtra(Default.FILE_PATH, filePath);
                            setResult(Default.ADD_NEW_PHOTO_RESULT, intent);
                            progressDialog.cancel();
                           // finish();
                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {
                            progressDialog.cancel();
                            Toast.makeText(MainActivity.this, fault.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                break;
            case Default.MAKE_NEW_PHOTO:
                String photoCameraUrl = imageReturnedIntent.getStringExtra(Default.PHOTO_CAMERA_URL);
                String filePath = imageReturnedIntent.getStringExtra(Default.FILE_PATH);
                Intent intent = new Intent();
                intent.putExtra(Default.PHOTO_CAMERA_URL, photoCameraUrl);
                intent.putExtra(Default.FILE_PATH, filePath);
                setResult(Default.ADD_NEW_PHOTO_RESULT, intent);
                finish();
                break;
        }
    }

    private String getRealPathFromURI(Uri contentURI) {
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
           // t.setText(idx);
            return cursor.getString(idx);
        }
    }
}

  /*  @Override
    protected void onActivityResult( int requestCode, int resultCode, Intent imageReturnedIntent )
    {
        super.onActivityResult( requestCode, resultCode, imageReturnedIntent );

        switch( requestCode )
        {
            case Default.SELECT_PHOTO:
                if( resultCode == RESULT_OK && null!=imageReturnedIntent )
                {
                    Uri selectedImage = imageReturnedIntent.getData();
                 File imageFile;
                    //= new File( getRealPathFromURI( selectedImage ) );
                    final String[] filePath = {MediaStore.Images.Media.DATA};
                    // imageFile.getPath();
                    Cursor cursor = getContentResolver().query( selectedImage, filePath, null, null, null );
                    if( cursor == null )
                    {
                        imageFile= new File(selectedImage.getPath());
                    }
                    else
                    {
                        cursor.moveToFirst();
                        int idx = cursor.getColumnIndex( filePath[0]);
                       String imgDecodableString = cursor.getString(idx);
                        cursor.close();
                        imageFile= new File(cursor.getString( idx ));

                        mImage.setImageBitmap(BitmapFactory.decodeFile(imgDecodableString));
                    }

                    progressDialog = ProgressDialog.show( MainActivity.this, "", "Loading", true );
                    Backendless.Files.upload( imageFile, Default.DEFAULT_PATH_ROOT, new AsyncCallback<BackendlessFile>()
                    {
                        @Override
                        public void handleResponse( BackendlessFile response )
                        {
                            String photoBrowseUrl = response.getFileURL();
                            Intent intent = new Intent();
                            intent.putExtra( Default.PHOTO_BROWSE_URL, photoBrowseUrl );
                            intent.putExtra( Default.FILE_PATH, filePath );
                            setResult( Default.ADD_NEW_PHOTO_RESULT, intent );
                            progressDialog.cancel();
                            finish();
                        }

                        @Override
                        public void handleFault( BackendlessFault fault )
                        {
                            progressDialog.cancel();
                            Toast.makeText( MainActivity.this, fault.toString(), Toast.LENGTH_SHORT ).show();
                        }
                    } );
                }
                break;

            case Default.MAKE_NEW_PHOTO:
                String photoCameraUrl = imageReturnedIntent.getStringExtra( Default.PHOTO_CAMERA_URL );
                String filePath = imageReturnedIntent.getStringExtra( Default.FILE_PATH );
                Intent intent = new Intent();
                intent.putExtra( Default.PHOTO_CAMERA_URL, photoCameraUrl );
                intent.putExtra( Default.FILE_PATH, filePath );
                setResult( Default.ADD_NEW_PHOTO_RESULT, intent );
                finish();
                break;
        }
    }

    private String getRealPathFromURI( Uri contentURI )
    {
        Cursor cursor = getContentResolver().query( contentURI, filep, null, null, null );
        if( cursor == null )
        {
            return contentURI.getPath();
        }
        else
        {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex( MediaStore.Images.ImageColumns.DATA );
            return cursor.getString( idx );
        }
    }*/





