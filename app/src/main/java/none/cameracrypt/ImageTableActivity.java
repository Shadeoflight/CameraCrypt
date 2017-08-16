package none.cameracrypt;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.PatternSyntaxException;

import none.cameracrypt.CustomAdapterToolkit.CustomAdapter;

public class ImageTableActivity extends AppCompatActivity {

    File sdCard = Environment.getExternalStorageDirectory();

    ArrayList<String> mFileStrings = new ArrayList<String>();
    private File[] listFile;

    ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_table);

        File file = new File(
                sdCard.getAbsolutePath() + "/data/MyCameraApp/");
        if (!file.exists()) {
            if (!file.mkdirs()) {

                Log.d("ImageTableActivity", "Failed to load directory");
            }
        }

        if(file.isDirectory())
        {
            // Generate file array
            listFile = file.listFiles();

            String fileNameString = "";

            for(int i = 0; i < listFile.length; i++)
            {

                // Retrieve file name data to make array
                fileNameString = listFile[i].getAbsolutePath()
                        .substring(listFile[i].getAbsolutePath().lastIndexOf("/")+1);
                Log.d("ImageTableActivity", "File found: "+fileNameString);
                // Remove file extension
                mFileStrings.add(fileNameString.substring(0, fileNameString.lastIndexOf('.')));

            }

            // Print file names
            for(String name : mFileStrings)
            {
                Log.d("ImageTableActivity", "File name: "+name);
            }

            // Set target id for list view
            list = (ListView) findViewById(R.id.list);

            // Generate adapter with mFileStrings array
            CustomAdapter adapter = new CustomAdapter(mFileStrings, this);

            // Set list view to custom adapter
            list.setAdapter( adapter );

        }

        // Add a listener to the Capture button
        Button tableReturnButton = (Button) findViewById(R.id.tableBackButton);
        tableReturnButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Segue to next activity
                        Intent myIntent = new Intent(v.getContext(), WelcomeActivity.class);
                        startActivity(myIntent);
                    }
                }
        );

        // Add a listener to the Delete All button
        Button tableDeleteButton = (Button) findViewById(R.id.deleteTableButton);
        tableDeleteButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        final File folderToDelete = new File(
                                sdCard.getAbsolutePath() + "/data/MyCameraApp/");

                        // Get prompts.xml view
                        LayoutInflater li = LayoutInflater.from(v.getContext());
                        View promptsView = li.inflate(R.layout.delete_table_prompt, null);

                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                v.getContext());

                        // Set prompts.xml to alertdialog builder
                        alertDialogBuilder.setView(promptsView);

                        // Set dialog message
                        alertDialogBuilder
                                .setCancelable(false)
                                .setPositiveButton("Delete All",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,int id) {
                                                // Delete all files
                                                deleteRecursive(folderToDelete);

                                                showToast("Deletion completed!");

                                                // Segue to next activity
                                                Intent myIntent = new Intent(v.getContext(), WelcomeActivity.class);
                                                startActivity(myIntent);
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

    // Recursively delete contents of app directory
    private void deleteRecursive(File fileOrDirectory) {

        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        boolean deleted = fileOrDirectory.delete();
        if(deleted == false){
            Log.d("ImageTableActivity","Delete all not successful");
        }

    }

    /**
     *  Alert dialog toast
     */

    public void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
