package com.example.mbararamama_checkai;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // This button is on your Dashboard "Hello Mama" card
        Button btnStartNew = findViewById(R.id.btnGoToAssess);

        // These buttons are in your Quick Access section
        Button btnHistory = findViewById(R.id.btnViewHistory);
        Button btnLearn = findViewById(R.id.btnLearn); // Added Learn Button link

        // Navigate to Assessment
        btnStartNew.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AssessmentActivity.class);
            startActivity(intent);
        });

        // Navigate to History
        btnHistory.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
            startActivity(intent);
        });

        // Navigate to Learn Screen
        btnLearn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LearnActivity.class);
            startActivity(intent);
        });
    }
}