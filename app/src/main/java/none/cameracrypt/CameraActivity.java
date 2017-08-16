package none.cameracrypt;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.media.Image;
import android.os.Environment;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;
import android.content.pm.Signature;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import java.lang.Object;

import none.cameracrypt.CameraToolkit.CameraPreviewLegacy;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;


public class CameraActivity extends AppCompatActivity {

    // Declare camera variables
    private Camera mCamera;
    private CameraPreviewLegacy mPreview;

    // Declare image variable
    private ImageView capturedImage;

    private static final String TAG = "CameraActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        // Check if camera is enabled on phone
        Context contextActivity = this;
        if(checkCameraHardware(contextActivity))
        {
            Log.d("CameraActivity","Phone has camera!");
        }
        else
        {
            Log.d("CameraActivity","Phone does not have camera!");
        }

        // Create an instance of Camera
        mCamera = getCameraInstance();

        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreviewLegacy(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);

        preview.addView(mPreview);

        // Add a listener to the Capture button
        Button captureButton = (Button) findViewById(R.id.button_capture);
        captureButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // get an image from the camera
                        mCamera.takePicture(null, null, mPicture);
                    }
                }
        );
    }

    /**
     *  Camera initialization methods
     */

    /** A safe way to get an instance of the Camera object. */
    @Deprecated
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    /**
     *  Camera output methods
     */

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

            if(bitmap == null)
            {
                Log.d("CameraActivity", "Bitmap is empty");
            }

            Log.d("test","\n\nTEST\n\n");
            Log.d("test","\n\nTEST\n\n");
            Log.d("test","\n\nTEST\n\n");
            Log.d("test","\n\nTEST\n\n");
            Log.d("test","\n\nTEST\n\n");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

            if(baos == null)
            {
                Log.d("CameraActivity", "Byte array output stream null!");
            }

            byte[] imageInByte = baos.toByteArray();

            Log.d("CameraActivity", "Byte Array generated");

            // Pop intent
            Intent resultIntent = new Intent(CameraActivity.this, ResultActivity.class);
            resultIntent.putExtra("image", imageInByte);
            startActivity(resultIntent);
        }
    };

    /**
     *  Camera checking methods
     */

    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();              // release the camera immediately on pause event
    }

    private void releaseCamera(){
        if (mCamera != null){
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }

}
