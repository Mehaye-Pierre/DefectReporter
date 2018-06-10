package com.seawolf.defectreporter;

/**
 * Created by Seawolf on 28/05/2018.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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

    }

    private void saveList(){
        //and since no one can remember android synthax
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this.getApplicationContext());
        SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(defectList);
        prefsEditor.putString(site.getName(), json);
        prefsEditor.commit();
    }

    private void loadList(){
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this.getApplicationContext());
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
}
