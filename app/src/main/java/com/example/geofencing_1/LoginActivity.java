package com.example.geofencing_1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String TEXT = "text";

    EditText phone;
    Button enter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if(!loadData().equals("")){
            startActivity(new Intent(getApplicationContext(), MainActivity.class ));
            finish();
        }
        phone = findViewById(R.id.phone);
        enter = findViewById(R.id.button);

        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                save();
                startActivity(new Intent(getApplicationContext(), MainActivity.class ));
                finish();
            }
        });
    }

    private void save() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TEXT, phone.getText().toString());
        editor.apply();
        StorageClass.phno = phone.getText().toString();
        Toast.makeText(this, "Data saved!", Toast.LENGTH_SHORT).show();
    }
    public String loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        StorageClass.phno = sharedPreferences.getString(TEXT, "");
        return StorageClass.phno;
    }
}