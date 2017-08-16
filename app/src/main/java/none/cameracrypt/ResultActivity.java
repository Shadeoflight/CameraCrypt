package none.cameracrypt;

import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ResultActivity extends AppCompatActivity {

    File sdCard = Environment.getExternalStorageDirectory();

    private ImageView imageOrientation;

    // Prompt variables
    final Context context = this;
    private EditText result;

    // Image bitmap variable
    private Bitmap bmp = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // Get intent contents
        byte[] byteArray = getIntent().getByteArrayExtra("image");

        if(byteArray == null){
            Log.d("ResultActivity","Byte array is empty");
        }

        if (byteArray != null) {
            bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        }

        // Display image view
        ImageView imageViewer = (ImageView) findViewById(R.id.captured_image);
        imageViewer.setImageBitmap(bmp);

        // Add a listener to the "Re-try" button
        Button retryButton = (Button) findViewById(R.id.result_back_button);
        retryButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Segue to next activity
                        Intent result_myIntent = new Intent(v.getContext(), CameraActivity.class);
                        startActivity(result_myIntent);
                    }
                }
        );

        // Add a listener to the "Done" button
        Button doneButton = (Button) findViewById(R.id.result_done_button);
        doneButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {

                        // Get prompts.xml view
                        LayoutInflater li = LayoutInflater.from(context);
                        View promptsView = li.inflate(R.layout.name_image_prompt, null);

                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                context);

                        // Set prompts.xml to alertdialog builder
                        alertDialogBuilder.setView(promptsView);

                        final EditText userInput = (EditText) promptsView
                                .findViewById(R.id.editTextDialogUserInput);

                        // Set dialog message
                        alertDialogBuilder
                                .setCancelable(false)
                                .setPositiveButton("Save",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,int id) {
                                                // Get user input and set it to result
                                                // Edit text
                                                Log.d("ResultActivity", "onClick: " + userInput.getText());

                                                final String appPath = sdCard.getAbsolutePath() + "/data/MyCameraApp/";
                                                File file = new File(appPath + userInput.getText() + ".jpg");
                                                File encryptedFile = new File(appPath + "encrypted_" + userInput.getText() + ".enc");

                                                if(file.exists() || encryptedFile.exists())
                                                {
                                                    // Toast
                                                    showBurnedToast();
                                                }
                                                else
                                                {
                                                    // Save the image
                                                    String imageName = "";
                                                    imageName = userInput.getText() + "";
                                                    saveToExternalStorage(bmp, imageName);

                                                    // Segue to next activity
                                                    Intent result_myIntent = new Intent(v.getContext(), WelcomeActivity.class);
                                                    startActivity(result_myIntent);

                                                    // Toast
                                                    showToast();
                                                }

                                            }
                                        })
                                .setNeutralButton("Exit",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();

                                                // Segue to next activity
                                                Intent result_myIntent = new Intent(v.getContext(), WelcomeActivity.class);
                                                startActivity(result_myIntent);
                                            }
                                        })
                                .setNegativeButton("Cancel",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,int id) {
                                                dialog.cancel();
                                            }
                                        });

                        // Generate alert dialog
                        AlertDialog alertDialog = alertDialogBuilder.create();

                        // Show it
                        alertDialog.show();
                    }
                }
        );
    }

    // Disable phone back button
    @Override
    public void onBackPressed() {
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){

        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            resultChangeToLandscape();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            resultChangeToPortrait();
        }
    }

    public void resultChangeToLandscape() {
        imageOrientation = (ImageView) findViewById(R.id.captured_image);

        imageOrientation.setRotation(0);
    }
    public void resultChangeToPortrait() {

        imageOrientation = (ImageView) findViewById(R.id.captured_image);

        imageOrientation.setRotation(90);
    }

    /**
     *  Alert dialog toast
     */

    public void showBurnedToast(){
        Toast.makeText(this, R.string.result_alert_burned_toast, Toast.LENGTH_SHORT).show();
    }

    public void showToast(){
        Toast.makeText(this, R.string.result_alert_toast, Toast.LENGTH_SHORT).show();
    }

    /**
     *  Image storage methods
     */

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    private String saveToExternalStorage(Bitmap bitmapImage, String imageName)
    {
        //ContextWrapper cw = new ContextWrapper(getApplicationContext());

        // Requires "rooted" device to view contents in adb
        // Path to image storage
        File directory = new File(
                sdCard.getAbsolutePath() + "/data/MyCameraApp");
        if (!directory.exists()) {
                if (!directory.mkdirs()) {

                Log.d("ResultActivity", "Failed to create storage directory");
                return null;
            }
        }

        // Create imageDir file in path
        File mypath=new File(directory,imageName + ".jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Log.d("ResultActivity", "Path: "+directory.getAbsolutePath());

        return directory.getAbsolutePath();
    }

}
