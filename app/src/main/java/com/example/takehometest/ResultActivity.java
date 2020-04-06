package com.example.takehometest;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity {

    private String name;
    private String resultstring;
    private int result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView tv = (TextView)findViewById(R.id.results);

        Bundle mb = getIntent().getExtras();
        name = mb.getString("name","name");
        resultstring = mb.getString("resultstring","resstring");
        result = mb.getInt("result", -1);

        tv.setText("Hi "+ name + " the calculation returned " + resultstring + " and the result was " + Integer.toString(result));
    }
}
