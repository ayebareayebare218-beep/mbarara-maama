package com.example.mbararamama_checkai;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ReportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        // Receive data from Assessment
        String bp = getIntent().getStringExtra("bp");
        String hemo = getIntent().getStringExtra("hemo");
        String age = getIntent().getStringExtra("age");
        int riskIndex = getIntent().getIntExtra("riskIndex", 1);

        // Bind Views
        TextView tvBP = findViewById(R.id.reportBP);
        TextView tvHemo = findViewById(R.id.reportHemo);
        TextView tvAge = findViewById(R.id.reportAge);
        LinearLayout banner = findViewById(R.id.reportRiskBanner);
        TextView tvTitle = findViewById(R.id.reportRiskTitle);
        TextView tvInstructions = findViewById(R.id.reportInstructions);
        Button btnShare = findViewById(R.id.btnShareReport);
        Button btnDone = findViewById(R.id.btnDone);

        // Display User Data
        tvBP.setText("Blood Pressure: " + bp + " mmHg");
        tvHemo.setText("Hemoglobin: " + hemo + " g/dL");
        tvAge.setText("Age: " + age + " Years");

        // UI Logic based on Risk
        if (riskIndex == 0) { // HIGH RISK
            banner.setBackgroundColor(Color.parseColor("#D32F2F"));
            tvTitle.setText("🟥 HIGH RISK");
            tvInstructions.setText("• Go to a hospital IMMEDIATELY.\n• Possible signs of Pre-eclampsia detected.\n• Do not wait for labor pains.\n• Seek emergency obstetric care.");
        } else if (riskIndex == 1) { // LOW RISK
            banner.setBackgroundColor(Color.parseColor("#388E3C"));
            tvTitle.setText("🟩 LOW RISK");
            tvInstructions.setText("• All is well! Continue your regular ANC visits.\n• Maintain a healthy diet and rest.\n• Keep this report for your next checkup.");
        } else { // MEDIUM RISK
            banner.setBackgroundColor(Color.parseColor("#FBC02D"));
            tvTitle.setText("🟨 MEDIUM RISK");
            tvTitle.setTextColor(Color.BLACK);
            tvInstructions.setText("• Schedule a visit with your midwife within 24 hours.\n• Monitor for headaches or blurred vision.\n• Rest frequently and keep hydrated.");
        }

        // Close Button
        btnDone.setOnClickListener(v -> finish());

        // Share Feature (WhatsApp/SMS)
        btnShare.setOnClickListener(v -> {
            String reportBody = "Mama-Check AI Report\n" +
                    "--------------------\n" +
                    "Risk Level: " + tvTitle.getText() + "\n" +
                    "BP: " + bp + "\n" +
                    "Hemoglobin: " + hemo + "\n" +
                    "Age: " + age + "\n" +
                    "--------------------\n" +
                    "Stay safe, Mama!";

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, reportBody);
            startActivity(Intent.createChooser(intent, "Share via"));
        });
    }
}