package com.seawolf.defectreporter;

/**
 * Created by Seawolf on 28/05/2018.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * TODO : Add/edit the name
 * TODO : Take a (new) picture
 * TODO : Add/edit a comment
 */

public class DefectActivity extends Activity {

    Defect defect;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_defect);

        Intent intent = getIntent();
        if (intent != null) {
            defect = (Defect)intent.getSerializableExtra("DEFECT");
            TextView tv = findViewById(R.id.textViewDefectName);
            tv.setText(defect.getName());
        }

        Button addImgButton = findViewById(R.id.buttonAddImage);
        addImgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeAndSavePicture();
            }
        });

        Button modifyCommentButton = findViewById(R.id.buttonDefectComment);
        modifyCommentButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                popCommentDialog();
            }
        });
        //setImage();

    }


    private void setImage(){
        if(defect.getPhotoPath() != null){
            Bitmap myBitmap = BitmapFactory.decodeFile(defect.getPhotoPath());
            ImageView myImage = (ImageView) findViewById(R.id.imageDefect);
            myImage.setImageBitmap(myBitmap);
        }

    }

    private void takeAndSavePicture(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProviderCustom.getUriForFile(this,
                        this.getApplicationContext().getPackageName() + ".my.package.name.provider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, 1);
            }
        }
        setImage();
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        defect.setPhotoPath(image.getAbsolutePath());
        return image;
    }

    private void popCommentDialog(){
        //called by our button
        //this will be a "popup" showing when the user presses the button, where the user will write the data
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        LinearLayout layout = new LinearLayout(this);
        LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(parms);

        layout.setGravity(Gravity.CLIP_VERTICAL);
        layout.setPadding(2, 2, 2, 2);

        TextView tv = new TextView(this);
        tv.setText("Ajouter un nouveau défaut");
        tv.setPadding(40, 40, 40, 40);
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(20);

        final TextView originalComment = findViewById(R.id.textViewComment);

        final EditText et = new EditText(this);
        et.setText(originalComment.getText().toString());
        String etStr = et.getText().toString();
        TextView tv1 = new TextView(this);
        tv1.setText("Nom du défaut");

        LinearLayout.LayoutParams tv1Params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tv1Params.bottomMargin = 5;
        layout.addView(tv1,tv1Params);
        layout.addView(et, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        alertDialogBuilder.setView(layout);
        alertDialogBuilder.setTitle("");
        alertDialogBuilder.setCustomTitle(tv);


        // Setting Negative "Cancel" Button
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });

        // Setting Positive "OK" Button
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                originalComment.setText(et.getText().toString());
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();

        try {
            alertDialog.show();
        } catch (Exception e) {
            // WindowManager$BadTokenException will be caught and the app would
            // not display the 'Force Close' message
            e.printStackTrace();
        }
    }
}
