package com.example.takehometest;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.preference.PreferenceManager;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final int MIN_FACT_VALUE = 2;
    private static final int UNINITIALIZED = -1;
    private static final String NONE = "Nothing";
    private static final int MINDELAY = 100;
    private static final String SMINDELAY = "10";
    //need these to track changes
    private SharedPreferences myPreference;

    private EditText fact_val;
    private Button bstart;
    private Button bstop;
    private String name = NONE;
    private int delay = MINDELAY;
    private String resultstring = NONE;
    private AddTask at;
    private ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //get the number
        fact_val = (EditText) findViewById(R.id.editText);
        bstart = findViewById(R.id.bstart);
        bstop = findViewById(R.id.bcancel);
        pb=findViewById(R.id.progressBar1);

        // lets get a handle to default shared prefs
        myPreference = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void doStart(View view) {
        //get the int in string form
        String s = fact_val.getText().toString();

        //TODO cast to int
        int value = Integer.parseInt(s);

        if (value < MIN_FACT_VALUE) {
            Toast.makeText(this, "Integer must be > 10", Toast.LENGTH_SHORT).show();
            return;
        }

        enableStartButton(false);

        //get delay
        String sdelay = myPreference.getString("delay", SMINDELAY);
        delay = Integer.parseInt(sdelay);

        //what is the max value
        pb.setMax(value);

        //TODO start thread
        at = new AddTask(this, delay);
        at.execute(value);
    }


    public void doCancel(View view) {
        enableStartButton(true);
        if (at != null)
            at.cancel(true);
    }

    private void enableStartButton(boolean bStartEnabled) {
        bstart.setEnabled(bStartEnabled);
        bstop.setEnabled(!bStartEnabled);
    }

    public void doAbout(MenuItem item) {
        // Create out AlterDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This app created by " + myPreference.getString("name", "unknown"));
        //create an anonymous class that is listening for button click
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            /**
             * This method will be invoked when a button in the dialog is clicked.
             * Note the @override
             * Note also that I have to scope the context in the toast below, thats because anonymous classes have a
             * reference to the class they were declared in accessed via Outerclassname.this
             *
             * @param dialog The dialog that received the click.
             * @param which  The button that was clicked (e.g.
             *               {@link DialogInterface#BUTTON1}) or the position
             */
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this, "clicked OK in Help", Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void doSettings(MenuItem item) {
        Intent myintent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(myintent);
    }

    void launchResultActivity(String resultstring, int result ) {
        enableStartButton(true);
        pb.setProgress(0);

        //create intent
        Intent mi = new Intent(this, ResultActivity.class);

        //create bundle, attach name resultstring, resultint
        Bundle bouts = new Bundle();
        name = myPreference.getString("name", NONE);
        bouts.putString("name", name);

        bouts.putString("resultstring", resultstring);
        bouts.putInt("result", result);

        mi.putExtras(bouts);
        startActivity(mi);
    }

    public static class AddTask extends AsyncTask<Integer, Integer, String> {

        private String SUCCESS= "Success";
        private String CANCELED = "User chose to cancel";

        private final int delay;
        private MainActivity ma;
        private int result = UNINITIALIZED;

        public AddTask(MainActivity ma, int delay) {
            this.ma = ma;
            this.delay = delay;
            this.result = ma.UNINITIALIZED;

        }

        @Override
        protected String doInBackground(Integer... integers) {
            //number to apply factorial operation to
            int fact_val = integers[0];

            //will hold the final calculated factorial value
            result=1;

            for (int i =1; i<=fact_val;i++) {
                //TODO handle cancel
                if (isCancelled()) {
                    result = UNINITIALIZED;
                    return CANCELED;
                }

                //sleep for delay milliseconds
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //1 round of the factorial
                result = result * i;

                //TODO update MainActivity ProgressBar
                publishProgress(i);
            }
            return SUCCESS;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            ma.launchResultActivity(s, result);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            ma.pb.setProgress(values[0]);
        }

        @Override
        protected void onCancelled(String s) {
            super.onCancelled(s);
            ma.launchResultActivity(s, result);
        }
    }
}
