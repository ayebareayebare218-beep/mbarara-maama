package com.example.mbararamama_checkai;

import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class LearnActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn);

        // Find the button and set the click listener to close this screen
        Button btnCloseLearn = findViewById(R.id.btnCloseLearn);
        btnCloseLearn.setOnClickListener(v -> finish());
    }
}