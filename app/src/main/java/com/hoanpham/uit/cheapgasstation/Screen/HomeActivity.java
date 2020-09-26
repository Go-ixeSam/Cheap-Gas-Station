package com.hoanpham.uit.cheapgasstation.Screen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.hoanpham.uit.cheapgasstation.R;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView mText = findViewById(R.id.ddd);
        mText.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == findViewById(R.id.ddd)) {
            startActivity(new Intent(HomeActivity.this, GoogleMapActivity.class));
        }
    }
}
