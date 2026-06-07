package com.example.mbararamama_checkai;

import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class MainActivity extends AppCompatActivity {

    private EditText editAge, editSystolicBP, editDiastolicBP, editBloodSugar, editBodyTemp, editHeartRate;
    private Button btnAssess;
    private LinearLayout resultLayout;
    private TextView textResultTitle, textResultAction;

    private Interpreter tflite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UI
        editAge = findViewById(R.id.editAge);
        editSystolicBP = findViewById(R.id.editSystolicBP);
        editDiastolicBP = findViewById(R.id.editDiastolicBP);
        editBloodSugar = findViewById(R.id.editBloodSugar);
        editBodyTemp = findViewById(R.id.editBodyTemp);
        editHeartRate = findViewById(R.id.editHeartRate);
        btnAssess = findViewById(R.id.btnAssess);
        resultLayout = findViewById(R.id.resultLayout);
        textResultTitle = findViewById(R.id.textResultTitle);
        textResultAction = findViewById(R.id.textResultAction);

        // Load the model manually
        try {
            tflite = new Interpreter(loadModelFile());
        } catch (Exception e) {
            Toast.makeText(this, "Model loading failed!", Toast.LENGTH_LONG).show();
        }

        btnAssess.setOnClickListener(v -> runInference());
    }

    private MappedByteBuffer loadModelFile() throws IOException {
        // This opens the file directly from your project
        AssetFileDescriptor fileDescriptor = this.getAssets().openFd("maternal_health_model.tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    private void runInference() {
        try {
            String ageS = editAge.getText().toString();
            String sysS = editSystolicBP.getText().toString();
            String diaS = editDiastolicBP.getText().toString();
            String sugS = editBloodSugar.getText().toString();
            String temS = editBodyTemp.getText().toString();
            String heaS = editHeartRate.getText().toString();

            if (ageS.isEmpty() || sysS.isEmpty() || diaS.isEmpty() || sugS.isEmpty() || temS.isEmpty() || heaS.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Input: 1 row, 6 features
            float[][] input = new float[1][6];
            input[0][0] = Float.parseFloat(ageS);
            input[0][1] = Float.parseFloat(sysS);
            input[0][2] = Float.parseFloat(diaS);
            input[0][3] = Float.parseFloat(sugS);
            input[0][4] = Float.parseFloat(temS);
            input[0][5] = Float.parseFloat(heaS);

            // Output: 1 row, 3 classes (assuming Low, Mid, High)
            float[][] output = new float[1][3];

            // Run the model
            tflite.run(input, output);

            // Find best result
            int maxIdx = 0;
            if (output[0][1] > output[0][0]) maxIdx = 1;
            if (output[0][2] > output[0][maxIdx]) maxIdx = 2;

            showUI(maxIdx);

        } catch (Exception e) {
            Toast.makeText(this, "Error in assessment", Toast.LENGTH_SHORT).show();
        }
    }

    private void showUI(int index) {
        resultLayout.setVisibility(View.VISIBLE);
        if (index == 0) { // LOW
            resultLayout.setBackgroundColor(Color.parseColor("#4CAF50"));
            textResultTitle.setText("LOW RISK");
            textResultAction.setText("Continue normal checkups.");
        } else if (index == 1) { // MID
            resultLayout.setBackgroundColor(Color.parseColor("#FFEB3B"));
            textResultTitle.setText("MEDIUM RISK");
            textResultAction.setText("Consult midwife within 24h.");
        } else { // HIGH
            resultLayout.setBackgroundColor(Color.parseColor("#F44336"));
            textResultTitle.setText("HIGH RISK!");
            textResultAction.setText("GO TO EMERGENCY IMMEDIATELY!");
        }
    }

    @Override
    protected void onDestroy() {
        if (tflite != null) tflite.close();
        super.onDestroy();
    }
}