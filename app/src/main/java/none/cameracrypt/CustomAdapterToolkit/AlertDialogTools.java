package none.cameracrypt.CustomAdapterToolkit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import none.cameracrypt.EncryptionToolkit.ImageCryptTools;

/**
 * Created by joshua.wu on 8/4/17.
 */

public class AlertDialogTools {

    //
    //  Declare dialog variables
    //
    private String lockState = "";
    private String cryptButtonText = "";
    private String cryptSuccessToast = "";
    private String cryptErrorToast = "";
    private boolean viewButtonState = false;

    private boolean cryptSuccess = false;

    //
    //  Custom alert dialog method
    //
    // cryptType - "encrypt"/"decrypt"
    public void alertDialog(final String cryptType,
                            final Context context,
                            View promptsView,
                            final EditText userInput,
                            final String fileName,
                            final View v,
                            final CustomAdapter.ViewHolder holder){
        //
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // Set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        if(cryptType.equals("encrypt")){
            lockState = "Lock";
            cryptButtonText = "Unlock";
            viewButtonState = false;
            cryptSuccessToast = "Encryption successful";
            cryptErrorToast = "Encryption not successful";
        }
        else{
            lockState = "Unlock";
            cryptButtonText = "Lock";
            viewButtonState = true;
            cryptSuccessToast = "Decryption successful";
            cryptErrorToast = "Decryption not successful";
        }

        // Set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton(lockState,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // Get user input and set it to result
                                // Edit text
                                Log.d("AlertDialogTools", "Positive button onClick: " + userInput.getText());

                                String passwordString = userInput.getText().toString();

                                //
                                //  Encryption/Decryption
                                //

                                ImageCryptTools cryptToolsClass = new ImageCryptTools();

                                if(cryptType.equals("encrypt"))
                                {
                                    cryptSuccess = cryptToolsClass.encryptImageFile(fileName, passwordString, context);
                                }
                                else
                                {
                                    cryptSuccess = cryptToolsClass.decryptImageFile(fileName, passwordString, context);
                                }

                                if(cryptSuccess)
                                {
                                    // Display success message
                                    showToast(v, cryptSuccessToast);

                                    // Method 1
                                    //holder.viewBtnStatus.setEnabled(viewButtonState);
                                    //holder.cryptBtnStatus.setText(cryptButtonText);
                                    // Method 2
                                    //Button button = (Button)v;
                                    //button.setText(cryptButtonText);

                                    // TODO: Remove this crap
//                                    ((Activity)context).finish();
//                                    ((Activity)context).overridePendingTransition(0, 0);
//                                    ((Activity)context).startActivity(((Activity)context).getIntent());
//                                    ((Activity)context).overridePendingTransition(0, 0);
                                }
                                else
                                {
                                    showToast(v, cryptErrorToast);
                                }
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

    //
    //  Toast method
    //
    private void showToast(View v, String message){
        Toast.makeText(v.getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
