package com.example.mbararamama_checkai;

import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class HistoryActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        dbHelper = new DatabaseHelper(this);
        LinearLayout historyContainer = findViewById(R.id.historyContainer);
        Button btnCloseHistory = findViewById(R.id.btnCloseHistory);

        loadHistoryFromDatabase(historyContainer);

        btnCloseHistory.setOnClickListener(v -> finish());
    }

    private void loadHistoryFromDatabase(LinearLayout container) {
        Cursor cursor = dbHelper.getAllRecords();

        if (cursor.getCount() == 0) {
            TextView emptyText = new TextView(this);
            emptyText.setText("No history found. Take an assessment first!");
            emptyText.setTextSize(16);
            emptyText.setPadding(20, 20, 20, 20);
            container.addView(emptyText);
            return;
        }

        while (cursor.moveToNext()) {
            String date = cursor.getString(1);
            String risk = cursor.getString(2);
            String vitals = cursor.getString(3);

            // 1. Create Card dynamically
            CardView card = new CardView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 0, 0, 24);
            card.setLayoutParams(params);
            card.setRadius(16);
            card.setCardElevation(6);

            // Set Card Color based on Risk
            if (risk.contains("HIGH")) card.setCardBackgroundColor(Color.parseColor("#FFEBEE")); // Light Red
            else if (risk.contains("LOW")) card.setCardBackgroundColor(Color.parseColor("#E8F5E9")); // Light Green
            else card.setCardBackgroundColor(Color.parseColor("#FFF9C4")); // Light Yellow

            // 2. Create Layout inside Card
            LinearLayout innerLayout = new LinearLayout(this);
            innerLayout.setOrientation(LinearLayout.VERTICAL);
            innerLayout.setPadding(30, 30, 30, 30);

            // 3. Add Text Views (Date, Risk, Vitals)
            TextView tvDate = new TextView(this);
            tvDate.setText(date);
            tvDate.setTextSize(14);
            tvDate.setTextColor(Color.parseColor("#757575"));

            TextView tvRisk = new TextView(this);
            tvRisk.setText(risk);
            tvRisk.setTextSize(18);

            // THE FIX IS HERE:
            tvRisk.setTypeface(null, Typeface.BOLD);

            if (risk.contains("HIGH")) tvRisk.setTextColor(Color.parseColor("#D32F2F"));
            else if (risk.contains("LOW")) tvRisk.setTextColor(Color.parseColor("#2E7D32"));
            else tvRisk.setTextColor(Color.parseColor("#F57F17"));

            TextView tvVitals = new TextView(this);
            tvVitals.setText(vitals);
            tvVitals.setTextSize(15);
            tvVitals.setTextColor(Color.parseColor("#333333"));
            tvVitals.setPadding(0, 10, 0, 0);

            // 4. Assemble
            innerLayout.addView(tvDate);
            innerLayout.addView(tvRisk);
            innerLayout.addView(tvVitals);
            card.addView(innerLayout);
            container.addView(card);
        }
        cursor.close();
    }
}
