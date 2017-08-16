package none.cameracrypt.EncryptionToolkit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.crypto.CipherOutputStream;

/**
 * Created by joshua.wu on 8/2/17.
 */

public class ImageCryptTools {

    File sdCard = Environment.getExternalStorageDirectory();

    //
    //  Encrypt file method
    //

    public boolean encryptImageFile(String fileName, String passwordString , Context context){

        //
        //  Initialize variables
        //

        // Declare image bitmap
        Bitmap bitmap = null;

        // Retrieve directory
        final File directory = new File(
                sdCard.getAbsolutePath() + "/data/MyCameraApp");
        if (!directory.exists()) {
            if (!directory.mkdirs()) {

                Log.d("ImageCryptTools", "Failed to find directory");
            }
        }

        // Retrieve imageDir file in path
        final File myPath = new File(directory,fileName+".jpg");

        // Temporarily store image bitmap in variable
        try {
            // Initialize file input stream
            FileInputStream inputStream = new FileInputStream(myPath);

            // Retrieve image bitmap
            bitmap = BitmapFactory.decodeStream(inputStream);

            inputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        //
        // Delete file
        //

        fileDelete(myPath);

        //
        // Encrypt image bitmap
        //

        byte[] encryptedBytes = null;

        // Initialize ByteArrayOutputStream
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        if (bitmap != null) {

            // Compress bitmap and store in baos
            // JPEG and JPG similar
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            // Convert baos to byte array
            byte[] imageByteArray = baos.toByteArray();

            // Test
            Log.d("ImageCryptTools", "NULL bitmap not null");

            // 99 PROBLEMS, but a seg fault isn't one.
            try {
                // Initialize password key
                ImageCrypt cryptItem = new ImageCrypt(passwordString);

                // Encrypt image with password key
                encryptedBytes = cryptItem.encryptImage(imageByteArray);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //
        //  Store encrypted image file
        //

        if (!directory.exists()) {
            if (!directory.mkdirs()) {

                Log.d("ImageCryptTools", "Failed to create directory for file storage");
            }
        }

        // Create imageDir file in path
        File myStoragePath = new File(directory,"encrypted_"+fileName+".enc");

        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(myStoragePath);

            if(encryptedBytes == null){
                Log.d("ImageCryptTools", "Encrypted file null");

                return false;
            }
            else{
                // Write encrypted file to phone storage
                writeFile(encryptedBytes, "encrypted_"+fileName+".enc");

                // Test
                Log.d("ImageCryptTools", "Encrypted image file saved!");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return true;
    }

    //
    //  Decrypt file method
    //

    public boolean decryptImageFile(String fileName, String passwordString , Context context){

        Bitmap decryptedBitmap = null;

        // Retrieve directory
        final File directory = new File(
                sdCard.getAbsolutePath() + "/data/MyCameraApp");
        if (!directory.exists()) {
            if (!directory.mkdirs()) {

                Log.d("MyCameraApp", "failed to find directory");
            }
        }

        // Retrieve encrypted image file path
        final String encryptedFileName = fileName+".enc";
        final File myPath = new File(directory,encryptedFileName);

        try{

            // Read encrypted file bytes
            byte encryptedBytes[] = new byte[(int) myPath.length()];
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(myPath));
            DataInputStream dis = new DataInputStream(bis);
            dis.readFully(encryptedBytes);

            // Declare crypt item
            ImageCrypt cryptItemB = new ImageCrypt(passwordString);

            // Decrypt image with password key
            byte[] decryptedBytes = cryptItemB.decryptImage(encryptedBytes);

            decryptedBitmap = BitmapFactory.decodeByteArray(decryptedBytes, 0, decryptedBytes.length);

            dis.close();
            bis.close();

        } catch (Exception e) {

            e.printStackTrace();

            return false;
        }

        // Delete encrypted file
        fileDelete(myPath);

        //
        //  Store decrypted image
        //

        if (!directory.exists()) {
            if (!directory.mkdirs()) {

                Log.d("MyCameraAppDecryption", "failed to create directory");
            }
        }

        // Remove encryption tag
        String imageFileName = fileName.replace("encrypted_","");

        // Create imageDir file in path
        File myStoragePath=new File(directory,imageFileName+".jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(myStoragePath);

            // Use the compress method on the BitMap object to write image to the OutputStream
            if (decryptedBitmap != null) {
                decryptedBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);

                // Test
                Log.d("DecryptedImage", "Decrypted image saved!");
            }
            else{
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return true;
    }


    //
    //  Helper functions
    //

    private void writeFile(byte[] data, String fileName) throws IOException{
        FileOutputStream out = new FileOutputStream(new File(sdCard.getAbsolutePath() + "/data/MyCameraApp/" +fileName));
        out.write(data);
        out.close();
    }

    private void fileDelete(File myPath) {

        // Delete image bitmap from phone storage
        try {

            File file = new File(String.valueOf(myPath));
            boolean deleted = file.delete();

            if(deleted == false){
                throw new IOException("Image deletion unsuccessful!");
            }

        }catch (IOException e){
            Log.d("delete","Deletion unsuccessful!");
        }

    }
}
