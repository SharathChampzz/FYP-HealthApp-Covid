package com.example.geofencing_1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class QuizActivity extends AppCompatActivity {

    String[] qns = {"Do you have fever?",
            "Do you have dry cough?",
            "Do you have tiredness?",
            "Do you have aches and pains?",
            "Do you have sore throat?",
            "Do you have diarrhoea?",
            "Do you have conjunctivitis?",
            "Do you have headache?",
            "Do you have loss of taste or smell?",
            "Do you have a rash on skin, or discolouration of fingers or toes?",
            "Do you have difficulty breathing or shortness of breath?",
            "Do you have chest pain or pressure?",
            "Do you have loss of speech or movement?"
    };

    TextView question, result;
    Button yes, no, exit;
    ProgressBar progressBar;
    int qno = 0, score = 0, total = qns.length - 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        question = findViewById(R.id.question);
        yes = findViewById(R.id.yes);
        no = findViewById(R.id.no);
        exit =  findViewById(R.id.close);
        result = findViewById(R.id.result);
        progressBar = findViewById(R.id.progressBar);

        question.setText(qns[qno]);

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(qno >=0 && qno <=2){
                    score += 1;
                }
                else if(qno >=3 && qno <=9){
                    score += 3;
                }
                else{
                    score += 5;
                }
                update();
            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update();
            }
        });

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(QuizActivity.this, MainActivity.class));
                finish();
            }
        });
    }

    private void update() {
        if (qno == total){
            yes.setVisibility(View.GONE);
            no.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            String res = "Score : " + score;
            question.setText("");
            if(score < 20){
                question.setTextColor(getResources().getColor(R.color.green));
                question.setText(getString(R.string.safe));
//                result.setTextColor(getResources().getColor(R.color.green));  // did some changes like this
//                result.setText(getString(R.string.safe));
            }
            else if(score <= 28){
                question.setTextColor(getResources().getColor(R.color.teal_700));
                question.setText(getString(R.string.mild));
            }
            else{
                question.setTextColor(getResources().getColor(R.color.red));
                question.setText(getString(R.string.critical));
            }

            exit.setVisibility(View.VISIBLE);
        }
        else{
            qno += 1;
            question.setText(qns[qno]);
            double d = (double) qno / (total + 1);
            int prog = (int) Math.round(d*100);
            progressBar.setProgress(prog);
        }
    }
    }
