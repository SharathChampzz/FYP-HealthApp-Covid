package com.example.geofencing_1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class VoteMedicine extends AppCompatActivity  {


    private static final String TAG = "VoteMedcine";
    TextView disease, medicine;
    Button helpfull, nothelpfull;
    ImageView image;
    String key, dis, med, url;
    int helped, nothelped;
    int count = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote_medicine);

        image = findViewById(R.id.imageView2);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            key = bundle.getString("key");
            dis = bundle.getString("disease");
            med = bundle.getString("medication");
            helped = bundle.getInt("helpfull");
            nothelped = bundle.getInt("nothelpfull");
            url = bundle.getString("url");
//            Toast.makeText(getApplicationContext(),url, Toast.LENGTH_SHORT).show();


            Picasso.get()
                    .load(url)
                    .placeholder(R.drawable.icon)
                    .error(R.drawable.icon)
                    .fit()
                    .into(image);


        }

        Log.d(TAG, "Key : " + key);
        DatabaseReference medref = FirebaseDatabase.getInstance().getReference("medicines/" + key);
        disease = findViewById(R.id.disease);
        medicine = findViewById(R.id.medication);
        helpfull = findViewById(R.id.helpfull);
        nothelpfull = findViewById(R.id.nothelpfull);

        String helps = "Helpfull 👍   " + String.valueOf(helped);
        helpfull.setText(helps);

        String helpless = "Not Helpfull 👎   " + String.valueOf(nothelped);
        nothelpfull.setText(helpless);

        disease.setText(dis);
        medicine.setText(med);
        Helper obj = new Helper(dis, med, url);

        helpfull.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (count == 0) {
                    medref.child("helpfull").setValue(helped + 1);
                    String x = "Helpfull 👍   " + String.valueOf(helped + 1);
                    helpfull.setText(x);
                    Toast.makeText(getApplicationContext(), "We would like to add more such medications Thank You!🤗", Toast.LENGTH_SHORT).show();
                    count += 1;
                }
            }
        });

        nothelpfull.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(count == 0) {
                    medref.child("nothelpfull").setValue(nothelped + 1);
                    String x = "Not Helpfull 👎   " + String.valueOf(nothelped + 1);
                    nothelpfull.setText(x);
                    Toast.makeText(getApplicationContext(), "Sorry for this one 😣", Toast.LENGTH_SHORT).show();
                    count += 1;
                }
            }
        });
    }
}