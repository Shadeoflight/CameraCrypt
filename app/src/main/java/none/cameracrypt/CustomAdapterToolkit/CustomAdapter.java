package none.cameracrypt.CustomAdapterToolkit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import none.cameracrypt.CameraActivity;
import none.cameracrypt.EncryptionToolkit.ImageCrypt;
import none.cameracrypt.EncryptionToolkit.ImageCryptTools;
import none.cameracrypt.ImageViewActivity;
import none.cameracrypt.R;
import none.cameracrypt.ResultActivity;
import none.cameracrypt.WelcomeActivity;

/**
 * Created by joshua.wu on 7/28/17.
 */

public class CustomAdapter extends BaseAdapter implements ListAdapter {

    private File sdCard = Environment.getExternalStorageDirectory();
    private File[] listFile;
    private ArrayList<String> mFileStrings = new ArrayList<String>();

    static class ViewHolder{
        public TextView name;
        public Button cryptBtnStatus;
        public Button viewBtnStatus;
    }

    // Declare password variable
    private String passwordString = "";

    //private Button viewBtn;

    private CustomAdapter adapter;

    private ArrayList<String> list = new ArrayList<String>();
    private Context context;

    private ViewHolder holder;
    private View view;

    //
    //  Initialization method
    //

    public CustomAdapter(ArrayList<String> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int pos) {
        return list.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return 0;
        //just return 0 if your list items do not have an Id variable.
    }


    // Called after each list redraw
    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {

        // Initialize view holder
        holder = new ViewHolder();

        view = convertView;

        //
        //
        //

//        if(view == null)
//        {
//            File file = new File(
//                    sdCard.getAbsolutePath() + "/data/MyCameraApp/");
//            if (!file.exists()) {
//                if (!file.mkdirs()) {
//
//                    Log.d("ImageTableActivity", "Failed to load directory");
//                }
//            }
//
//            if(file.isDirectory())
//            {
//                // Generate file array
//                listFile = file.listFiles();
//
//                String fileNameString = "";
//
//                for(int i = 0; i < listFile.length; i++)
//                {
//
//                    // Retrieve file name data to make array
//                    fileNameString = listFile[i].getAbsolutePath()
//                            .substring(listFile[i].getAbsolutePath().lastIndexOf("/")+1);
//                    Log.d("ImageTableActivity", "File found: "+fileNameString);
//                    // Remove file extension
//                    mFileStrings.add(fileNameString.substring(0, fileNameString.lastIndexOf('.')));
//
//                }
//
//                // Print file names
//                for(String name : mFileStrings)
//                {
//                    Log.d("ImageTableActivity", "File name: "+name);
//                }
//
//                list = mFileStrings;
//
//            }
//        }


        //
        //
        //

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.image_table_item, parent, false);
        }

        //Handle TextView and display string from your list
        //TextView listItemText = (TextView)view.findViewById(R.id.text);
        holder.name = (TextView)view.findViewById(R.id.text);

        // TODO: TEST
        Log.d("CustomAdapter","NAME: "+list.get(position));
        File file = new File(sdCard.getAbsolutePath() + "/data/MyCameraApp/" + "encrypted_" + list.get(position) + ".enc");
        if(file.exists())
        {
            list.set(position, "encrypted_"+list.get(position));
            Log.d("CustomAdapter","NAME MODIFIED: "+list.get(position));
        }
        else{
            if(list.get(position).contains("encrypted_"))
            {
                list.set(position, list.get(position).replace("encrypted_",""));
            }
        }

        // Check image name for encryption tag
        if(list.get(position).contains("encrypted_"))
        {
            // Remove encryption tag
            String imageFileName = list.get(position).replace("encrypted_","");
            //listItemText.setText(imageFileName);
            holder.name.setText(list.get(position).replace("encrypted_",""));
        }
        else
        {
            //listItemText.setText(list.get(position));
            holder.name.setText(list.get(position));
        }

        //Handle buttons and add onClickListeners
        //viewBtn = (Button)view.findViewById(R.id.table_view_button);
        holder.viewBtnStatus = (Button)view.findViewById(R.id.table_view_button);
//        holder.viewBtnStatus.setTag(position);

        //final Button cryptBtn = (Button)view.findViewById(R.id.table_crypt_button);
        holder.cryptBtnStatus = (Button)view.findViewById(R.id.table_crypt_button);
//        holder.cryptBtnStatus.setTag(position);

        //
        //  Detect encrypted files
        //
        final String fileName = list.get(position);

        Log.d("CustomAdapter", "File found: "+fileName);

        if(fileName.contains("encrypted_"))
        {
            //holder.viewBtnStatus.setEnabled(false);
            holder.viewBtnStatus.setText("Delete");
            holder.cryptBtnStatus.setText("Unlock");
        }
        else{
            holder.viewBtnStatus.setEnabled(true);
            holder.cryptBtnStatus.setText("Lock");
        }

        //viewBtn.setOnClickListener(new View.OnClickListener(){
        holder.viewBtnStatus.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                // Detect encrypted files
                if(!fileName.contains("encrypted_"))
                {
                    // Pop intent
                    Intent imageViewIntent = new Intent(v.getContext(), ImageViewActivity.class);
                    imageViewIntent.putExtra("file_name", fileName+".jpg");

                    v.getContext().startActivity(imageViewIntent);
                }
                else{
                    // Enable deletion of table row

                    //

                }
            }
        });

        // Modify image
        //cryptBtn.setOnClickListener(new View.OnClickListener(){
        holder.cryptBtnStatus.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(final View v) {

                //String cryptButtonText = cryptBtn.getText().toString();
                String cryptButtonText = holder.cryptBtnStatus.getText().toString();

                // Initialize file name
                final String fileName = list.get(position);

                // Detect encrypted files
                if(!fileName.contains("encrypted_"))
                {

                    // Get prompts.xml view
                    LayoutInflater li = LayoutInflater.from(context);
                    View promptsView = li.inflate(R.layout.password_prompt, parent, false);

                    final EditText userInput = (EditText) promptsView
                            .findViewById(R.id.editTextDialogUserPasswordInput);

                    AlertDialogTools alertDialogObj = new AlertDialogTools();
                    alertDialogObj.alertDialog("encrypt", context, promptsView, userInput, fileName, v, holder);

                    //notifyDataSetChanged();
                }
                else{

                    // Get prompts.xml view
                    LayoutInflater li = LayoutInflater.from(context);
                    View promptsView = li.inflate(R.layout.password_verify_prompt, parent, false);

                    final EditText userInput = (EditText) promptsView
                            .findViewById(R.id.editTextDialogUserPasswordVerifyInput);

                    AlertDialogTools alertDialogObj = new AlertDialogTools();
                    alertDialogObj.alertDialog("decrypt", context, promptsView, userInput, fileName, v, holder);

                    //notifyDataSetChanged();
                }
            }
        });

        return view;
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
     *  Alert dialog toast - Modified
     */

    public void showToast(View v, String message){
        Toast.makeText(v.getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
