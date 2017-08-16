package none.cameracrypt;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.File;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // Test
        File sdCard = Environment.getExternalStorageDirectory();
        final File directory = new File(
                sdCard.getAbsolutePath() + "/data/MyCameraApp");
        if (!directory.exists()) {
            if (!directory.mkdirs()) {

                Log.d("MyCameraApp", "failed to find directory");
            }
        }
        Log.d("WelcomeActivity", "Path: "+directory.getParent());

        // Add a listener to the Capture button
        Button captureButton = (Button) findViewById(R.id.takePhoto);
        captureButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // Segue to next activity
                        Intent myIntent = new Intent(v.getContext(), CameraActivity.class);
                        startActivity(myIntent);
                    }
                }
        );

        Button storedImagesButton = (Button) findViewById(R.id.storedPhotos);
        storedImagesButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // Segue to next activity
                        Intent myIntent = new Intent(v.getContext(), ImageTableActivity.class);
                        startActivity(myIntent);
                    }
                }
        );
    }

    // Disable phone back button
    @Override
    public void onBackPressed() {
    }
}
