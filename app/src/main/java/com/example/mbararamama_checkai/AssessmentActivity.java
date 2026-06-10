package com.example.mbararamama_checkai;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.tensorflow.lite.Interpreter;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AssessmentActivity extends AppCompatActivity {

    private EditText editAge, editSystolicBP, editDiastolicBP, editHemoglobin, editVisits, editComplications;
    private Interpreter tflite;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment);

        // Initialize Database
        dbHelper = new DatabaseHelper(this);

        editAge = findViewById(R.id.editAge);
        editSystolicBP = findViewById(R.id.editSystolicBP);
        editDiastolicBP = findViewById(R.id.editDiastolicBP);
        editHemoglobin = findViewById(R.id.editHemoglobin);
        editVisits = findViewById(R.id.editVisits);
        editComplications = findViewById(R.id.editComplications);

        try {
            tflite = new Interpreter(loadModelFile());
        } catch (Exception e) {
            Toast.makeText(this, "Model loading failed!", Toast.LENGTH_LONG).show();
        }

        findViewById(R.id.btnAssess).setOnClickListener(v -> runInference());
    }

    private MappedByteBuffer loadModelFile() throws IOException {
        AssetFileDescriptor fileDescriptor = this.getAssets().openFd("maternal_health_model.tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, fileDescriptor.getStartOffset(), fileDescriptor.getDeclaredLength());
    }

    private void runInference() {
        if (tflite == null) return;

        try {
            String ageStr = editAge.getText().toString();
            String sysStr = editSystolicBP.getText().toString();
            String diaStr = editDiastolicBP.getText().toString();
            String hemoStr = editHemoglobin.getText().toString();
            String visitsStr = editVisits.getText().toString();
            String compStr = editComplications.getText().toString();

            if (ageStr.isEmpty() || sysStr.isEmpty() || diaStr.isEmpty() ||
                    hemoStr.isEmpty() || visitsStr.isEmpty() || compStr.isEmpty()) {
                Toast.makeText(this, "Please fill all boxes", Toast.LENGTH_SHORT).show();
                return;
            }

            float age = Float.parseFloat(ageStr);
            float sys = Float.parseFloat(sysStr);
            float dia = Float.parseFloat(diaStr);
            float hemo = Float.parseFloat(hemoStr);
            float visits = Float.parseFloat(visitsStr);
            float comp = Float.parseFloat(compStr);

            // 🌟 ENTERPRISE UPGRADE: Medical Data Validation
            if (age < 10 || age > 65) { showToast("Age must be between 10 and 65"); return; }
            if (sys < 70 || sys > 250) { showToast("Invalid Systolic BP (70-250)"); return; }
            if (dia < 40 || dia > 150) { showToast("Invalid Diastolic BP (40-150)"); return; }
            if (hemo < 3 || hemo > 20) { showToast("Invalid Hemoglobin (3-20)"); return; }
            if (comp < 0 || comp > 1) { showToast("Complications must be 0 or 1"); return; }

            float[][] input = new float[1][6];
            input[0][0] = age; input[0][1] = sys; input[0][2] = dia;
            input[0][3] = hemo; input[0][4] = visits; input[0][5] = comp;

            float[][] output = new float[1][3];
            tflite.run(input, output);

            int maxIdx = 0;
            float maxVal = output[0][0];
            for (int i = 1; i < 3; i++) {
                if (output[0][i] > maxVal) { maxVal = output[0][i]; maxIdx = i; }
            }

            saveResultToDatabase(maxIdx, sysStr, diaStr, hemoStr);
            openReportScreen(maxIdx, sysStr, diaStr, hemoStr, ageStr);

        } catch (NumberFormatException e) {
            showToast("Please enter numbers only");
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void openReportScreen(int riskIndex, String sys, String dia, String hemo, String age) {
        Intent intent = new Intent(AssessmentActivity.this, ReportActivity.class);
        intent.putExtra("bp", sys + "/" + dia);
        intent.putExtra("hemo", hemo);
        intent.putExtra("age", age);
        intent.putExtra("riskIndex", riskIndex);
        startActivity(intent);
        finish(); // Closes assessment so back button doesn't bring user back to form
    }

    private void saveResultToDatabase(int index, String sys, String dia, String hemo) {
        String riskLabel = (index == 0) ? "HIGH RISK" : (index == 1) ? "LOW RISK" : "MID RISK";
        String timestamp = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(new Date());
        String vitals = "BP: " + sys + "/" + dia + " | Hb: " + hemo;

        dbHelper.addRecord(timestamp, riskLabel, vitals);
    }

    @Override
    protected void onDestroy() {
        if (tflite != null) tflite.close();
        super.onDestroy();
    }
}