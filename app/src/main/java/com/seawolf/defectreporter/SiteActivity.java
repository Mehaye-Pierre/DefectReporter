package com.seawolf.defectreporter;

/**
 * Created by Seawolf on 28/05/2018.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.crlandroidpdfwriter.PDFWriter;
import com.example.crlandroidpdfwriter.PaperSize;
import com.example.crlandroidpdfwriter.StandardFonts;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * TODO : Edit a defect
 * TODO : Remove a defect
 */

public class SiteActivity extends Activity {

    List<Defect> defectList;
    Site site;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site);

        Intent intent = getIntent();
        if (intent != null) {
            site = (Site)intent.getSerializableExtra("SITE");
            TextView tv = findViewById(R.id.textSiteName);
            tv.setText(site.getName());
        }

        loadList();
        displayList();
        Button writeExcelButton = findViewById(R.id.buttonAddNewDefect);
        writeExcelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popDialogDefect();
            }
        });



       Button createPdfButton = findViewById(R.id.createPdfBtn);
        createPdfButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generatePDF();
                sendMail();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadList();
        displayList();
    }

    private void generatePDF(){
        PDFWriter pdfWriter = new PDFWriter(PaperSize.FOLIO_WIDTH, PaperSize.FOLIO_HEIGHT);
        pdfWriter.setFont(StandardFonts.SUBTYPE, StandardFonts.TIMES_BOLD);
        for(Defect defect: defectList){
            pdfWriter.addText(500, 450,15, defect.getDescription());
            if(defect.getPhotoPath() != null){
                Bitmap defectError =  getResizedBitmap(BitmapFactory.decodeFile(defect.getPhotoPath()),400);
                pdfWriter.addImage(10, PaperSize.FOLIO_HEIGHT - 720, defectError);
                }
            //TODO only create a new Page if there are more entries to go
            pdfWriter.newPage();
        }
        outputToFile("TestPDFReport", pdfWriter.asString(), "ISO-8859-1");
    }
    public void outputToFile(String fileName, String pdfContent, String encoding) {
        File newFile = new File(getExternalFilesDir(null), fileName +".pdf");

        try {
            newFile.createNewFile();
            try {
                FileOutputStream pdfFile = new FileOutputStream(newFile);
                pdfFile.write(pdfContent.getBytes(encoding));
                pdfFile.close();
                System.out.println("PDF JEAH!");
            } catch(FileNotFoundException e) {
               e.printStackTrace();
            }

        } catch(IOException e) {
            e.printStackTrace();
        }
    }



    private void sendMail() {
        File filelocation = new File(getExternalFilesDir(null), "TestPDFReport.pdf");
        Uri path = FileProviderCustom.getUriForFile(this,this.getApplicationContext().getPackageName() + ".my.package.name.provider",filelocation);
        Intent i = new Intent(Intent.ACTION_SEND);
        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        i.setType("vnd.android.cursor.dir/email");
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{});
        i.putExtra(Intent.EXTRA_SUBJECT, "Empty");
        i.putExtra(Intent.EXTRA_STREAM, path);
        Log.v(getClass().getSimpleName(),path.getPath());
        try {
            startActivity(Intent.createChooser(i, "Envoi du mail ..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Pas de client mail installé.", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveList(){
        //and since no one can remember android synthax
        SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(defectList);
        prefsEditor.putString(site.getName(), json);
        prefsEditor.commit();
    }

    private void loadList(){
        SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        Gson gson = new Gson();
        String json = appSharedPrefs.getString(site.getName(), "");
        if (json.isEmpty()) {
            defectList = new ArrayList<Defect>();
        } else {
            Type type = new TypeToken<List<Defect>>() {
            }.getType();
            defectList = gson.fromJson(json, type);
        }
    }

    private void displayList(){
        LinearLayout listLayout = findViewById(R.id.layoutListDisplay);
        listLayout.removeAllViews();
        for (Defect defect : defectList){
            LinearLayout layout = new LinearLayout(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            layout.setOrientation(LinearLayout.HORIZONTAL);
            layout.setLayoutParams(params);
            final Defect tmpDefect = defect;
            Button buttonNavigate = new Button(this);
            buttonNavigate.setText(defect.getName());
            buttonNavigate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToDefectActivity(tmpDefect);
                }
            });
            Button buttonDelete = new Button(this);
            buttonDelete.setText("X");
            buttonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogDeleteConfirm(tmpDefect);
                }
            });
            layout.addView(buttonNavigate);
            layout.addView(buttonDelete);
            listLayout.addView(layout);
        }


    }

    private void deleteDefect(Defect defect){
        defect.deletePhoto();
        defectList.remove(defect);
        saveList();
        displayList();
    }







    private void dialogDeleteConfirm(final Defect defect) {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Attention")
                .setMessage("Supprimer le défaut "+defect.getName()+" ?")
                .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteDefect(defect);
                    }

                })
                .setNegativeButton("Non", null)
                .show();
    }


    private void popDialogDefect(){
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

        final EditText et = new EditText(this);
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
                Defect defect = new Defect(0,et.getText().toString(), Calendar.getInstance().getTime());
                defectList.add(defect);
                saveList();
                displayList();
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

    private void goToDefectActivity(Defect defect){
        Intent intent = new Intent(this,DefectActivity.class);
        intent.putExtra("DEFECT", defect);
        intent.putExtra("SITENAME",site.getName());
        startActivity(intent);
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }

        return Bitmap.createScaledBitmap(image, width, height, true);
    }
}
