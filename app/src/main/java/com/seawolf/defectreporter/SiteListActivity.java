package com.seawolf.defectreporter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import java.util.List;

/**
 * TODO : Edit a site
 * TODO : Remove a site
 */
public class SiteListActivity extends AppCompatActivity{

    private List<Site> siteList;

    //Ok so first things first, we have this lovely list of site
    //This will be our first screen, where our list of site is displayed
    //So, when we start an app, we need to be able to retrieve the list from our previous use of the app (a save mecanism)
    //To save things "efficiently" with Android, we use Json

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_list);
        loadList();
        displayList();
        Button writeExcelButton = findViewById(R.id.buttonAddNewSite);
        writeExcelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popDialogSite();
            }
        });
    }

    private void saveList(){
        //and since no one can remember android synthax
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this.getApplicationContext());
        SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(siteList);
        prefsEditor.putString("JsonList", json);
        prefsEditor.commit();
    }

    private void loadList(){
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this.getApplicationContext());
        Gson gson = new Gson();
        String json = appSharedPrefs.getString("JsonList", "");
        if (json.isEmpty()) {
            siteList = new ArrayList<Site>();
        } else {
            Type type = new TypeToken<List<Site>>() {
            }.getType();
            siteList = gson.fromJson(json, type);
        }
    }

    private void displayList(){
        LinearLayout listLayout = findViewById(R.id.layoutListDisplay);
        listLayout.removeAllViews();
        for (Site site : siteList){
            final Site tmpSite = site;
            Button tmpButton = new Button(this);
            tmpButton.setText(site.getName());
            tmpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToSiteActivity(tmpSite);
                }
            });
            listLayout.addView(tmpButton);
        }


    }

    private void popDialogSite(){
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
        tv.setText("Ajouter un nouveau chantier");
        tv.setPadding(40, 40, 40, 40);
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(20);

        final EditText et = new EditText(this);
        String etStr = et.getText().toString();
        TextView tv1 = new TextView(this);
        tv1.setText("Input Student ID");

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
                Site site = new Site(0,et.getText().toString());
                siteList.add(site);
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

    private void goToSiteActivity(Site site){
        Intent intent = new Intent(this,SiteActivity.class);
        intent.putExtra("SITE", site);
        startActivity(intent);
    }
}
