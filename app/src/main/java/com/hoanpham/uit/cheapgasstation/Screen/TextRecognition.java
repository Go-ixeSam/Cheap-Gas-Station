package com.hoanpham.uit.cheapgasstation.Screen;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.hoanpham.uit.cheapgasstation.R;

import java.util.List;

public class TextRecognition extends AppCompatActivity {
    private Button snapBtn;
    private Button detectBtn;
    private ImageView imageView;
    private TextView txtView;
    private Bitmap imageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_1);
        snapBtn = findViewById(R.id.snapBtn);
        detectBtn = findViewById(R.id.detectBtn);
        imageView = findViewById(R.id.imageView);
        txtView = findViewById(R.id.txtView);
        snapBtn.setOnClickListener(view -> dispatchTakePictureIntent());
        detectBtn.setOnClickListener(view -> {
            if(imageBitmap == null){
                Toast.makeText(this, "Bạn chưa chụp ảnh", Toast.LENGTH_SHORT).show();
                return;
            }
            detectTxt();
        });
    }

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void dispatchTakePictureIntent() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(TextRecognition.this, new String[] {Manifest.permission.CAMERA}, 1221);
        } else {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1221) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
        }
    }

    private void detectTxt() {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(imageBitmap);
        FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
        detector.processImage(image).addOnSuccessListener(this::processTxt).addOnFailureListener(e -> {
            Log.d("TAGGG", "detectTxt: " + e.getMessage());
        });
    }

    private void processTxt(FirebaseVisionText text) {
        List<FirebaseVisionText.TextBlock> blocks = text.getTextBlocks();
        if (blocks.size() == 0) {
            Toast.makeText(TextRecognition.this, "No Text :(", Toast.LENGTH_LONG).show();
            return;
        }

                    txtView.setTextSize(24);
        SpannableStringBuilder lineText = new SpannableStringBuilder();

        for (FirebaseVisionText.TextBlock block : text.getTextBlocks()) {
            for (FirebaseVisionText.Line line: block.getLines()) {
                lineText.append(line.getText());
            }
        }
        removerCharacterNotNumber(lineText.toString());
    }

    private void removerCharacterNotNumber(String line){
        String numberString = line.replaceAll("[^0-9]", "");
        txtView.setTextSize(24);
        txtView.setText(formatString(numberString));
    }

    private String formatString(String text){
        int length = text.length();
        SpannableStringBuilder builder = new SpannableStringBuilder();
        for(int i = 0; i < length; i++){
            builder.append(text.charAt(i)).append(i == length - 1 ? "" : ",");
        }
        return builder.toString();
    }
}
