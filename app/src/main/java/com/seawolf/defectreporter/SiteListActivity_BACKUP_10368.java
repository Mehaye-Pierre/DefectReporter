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
        // load the activity_site_list xml
        setContentView(R.layout.activity_site_list);
        // call the loadList method when the app starts
        loadList();
        // call the displayList method when the app starts
        displayList();
        // gets the buttonAddNewSite element from the xml
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
        //GitHub account
        SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        //GitHub repository
        SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
        Gson gson = new Gson();
        // turns the siteList into a Json?
        String json = gson.toJson(siteList);
        //string is added, like on git(add)
        prefsEditor.putString("JsonList", json);
        // commit
        prefsEditor.commit();
    }

    private void loadList(){
        SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        Gson gson = new Gson();
        String json = appSharedPrefs.getString("JsonList", "");
        if (json.isEmpty()) {
            siteList = new ArrayList<Site>();
        } else {
            // since List<Site>> is not native, a TypeToken is used
            Type type = new TypeToken<List<Site>>() {
            }.getType();
            siteList = gson.fromJson(json, type);
        }
    }

    private void displayList(){
        LinearLayout listLayout = findViewById(R.id.layoutListDisplay);
        listLayout.removeAllViews();
        for (Site site : siteList){
            LinearLayout layout = new LinearLayout(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            layout.setOrientation(LinearLayout.HORIZONTAL);
            layout.setLayoutParams(params);
            final Site tmpSite = site;
            Button buttonNavigate = new Button(this);
            buttonNavigate.setText(site.getName());
            buttonNavigate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToSiteActivity(tmpSite);
                }
            });
            Button buttonDelete = new Button(this);
            buttonDelete.setText("X");
            buttonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogDeleteConfirm(tmpSite);
                }
            });
            layout.addView(buttonNavigate);
            layout.addView(buttonDelete);
            listLayout.addView(layout);
        }

    }

    private void deleteSite(Site site){
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this.getApplicationContext());
        SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
        Gson gson = new Gson();
        String json = appSharedPrefs.getString(site.getName(), "");
        List<Defect> defectList;
        if (json.isEmpty()) {
            defectList = new ArrayList<Defect>();
        } else {
            Type type = new TypeToken<List<Defect>>() {
            }.getType();
            defectList = gson.fromJson(json, type);
        }
        for(Defect defect:defectList){
            defect.deletePhoto();
            prefsEditor.remove(defect.getName());
        }
        prefsEditor.commit();
        siteList.remove(site);
        saveList();
        displayList();
    }



    private void dialogDeleteConfirm(final Site site) {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Attention")
                .setMessage("Supprimer le défaut "+site.getName()+" ?")
                .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteSite(site);
                    }

                })
                .setNegativeButton("Non", null)
                .show();
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
        // Text = add a new site
        tv.setText("Ajouter un nouveau chantier");
        tv.setPadding(40, 40, 40, 40);
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(20);

        final EditText et = new EditText(this);
        TextView tv1 = new TextView(this);
<<<<<<< HEAD
        //TODO translate to french!
        tv1.setText("Input Site Name");
=======
        tv1.setText("Nom du chantier");
>>>>>>> 3468a3fa46322bb33b25dda5515894791ee06d52

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
