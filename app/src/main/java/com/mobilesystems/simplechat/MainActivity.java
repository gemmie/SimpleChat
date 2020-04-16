package com.mobilesystems.simplechat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import static android.view.View.*;

public class MainActivity extends AppCompatActivity {

    public static String IP = "ip";
    public static String NICK = "nick";

    Button startButton;
    EditText ipEditText;
    EditText nickEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        setContentView(R.layout.activity_main);

        startButton = findViewById(R.id.startButton);
        ipEditText = findViewById(R.id.ipEditText);
        nickEditText = findViewById(R.id.nickEditText);

        startButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SimpleChatActivity.class);
                intent.putExtra(IP, ipEditText.getText().toString());
                intent.putExtra(NICK, nickEditText.getText().toString());
                startActivity(intent);
            }
        });
    }
}
