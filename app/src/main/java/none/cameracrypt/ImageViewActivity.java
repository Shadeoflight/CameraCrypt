package none.cameracrypt;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageViewActivity extends AppCompatActivity {

    File sdCard = Environment.getExternalStorageDirectory();

    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        // Get intent contents
        final String fileName = getIntent().getStringExtra("file_name");

        final File directory = new File(
                sdCard.getAbsolutePath() + "/data/MyCameraApp");
        if (!directory.exists()) {
            if (!directory.mkdirs()) {

                Log.d("ImageViewActivity", "Failed to create directory");
            }
        }

        // Create imageDir file in path
        final File myPath = new File(directory,fileName);

        try {

            // Initialize file input stream
            FileInputStream inputStream = new FileInputStream(myPath);

            // Retrieve image bitmap
            bitmap = BitmapFactory.decodeStream(inputStream);

            inputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        // Display image view
        ImageView imageViewer = (ImageView) findViewById(R.id.image_view);
        imageViewer.setImageBitmap(bitmap);

        // Add a listener to button
        Button backButton = (Button) findViewById(R.id.image_view_back_button);
        backButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Segue to next activity
                        Intent result_myIntent = new Intent(v.getContext(), ImageTableActivity.class);
                        startActivity(result_myIntent);
                    }
                }
        );

        // Add a listener to button
        Button deleteButton = (Button) findViewById(R.id.image_view_delete_button);
        deleteButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        try {

                            File file = new File(String.valueOf(myPath));
                            boolean deleted = file.delete();

                            if(deleted == false){
                                throw new IOException("Image deletion unsuccessful!");
                            }

                        }catch (IOException e){
                            Log.d("ImageViewActivity","File deletion unsuccessful!");
                        }

                        // Segue to next activity
                        Intent result_myIntent = new Intent(v.getContext(), ImageTableActivity.class);
                        startActivity(result_myIntent);
                    }
                }
        );

        // Add a listener to button
        Button sendButton = (Button) findViewById(R.id.image_view_send_button);
        sendButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // Add image to gallery
                        MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, fileName, "Test");

                        // Toast
                        showToast();
                    }
                }
        );

    }

    /**
     *  Toast method
     */
    public void showToast(){
        Toast.makeText(this, "Image sent to gallery!", Toast.LENGTH_SHORT).show();
    }
}
