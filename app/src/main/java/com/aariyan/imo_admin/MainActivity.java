package com.aariyan.imo_admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.aariyan.imo_admin.Notification.Token;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    private CardView addQuestion;
    private CardView questionRequestCard,paymentRequestCard;

    public static String User_token = "";
    private FirebaseAuth userAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userAuth = FirebaseAuth.getInstance();

        initUI();

        retrieveToken();
    }

    private void retrieveToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {

                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();
                        updateToken(token);
                    }
                });
    }

    private void updateToken(String token) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Token_Admin");

        Token token1 = new Token(token);

        if (userAuth.getCurrentUser() != null) {
            databaseReference.child(userAuth.getCurrentUser().getUid()).setValue(token1);
        }

    }


    private void initUI() {

        questionRequestCard = findViewById(R.id.questionRequestCard);
        paymentRequestCard = findViewById(R.id.paymentRequestCard);
        paymentRequestCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, Payment.class));
            }
        });
        questionRequestCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, QuestionRequest.class));
            }
        });
        addQuestion = findViewById(R.id.addQuestionCard);
        addQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startCategoryActivity();
            }
        });
    }

    private void startCategoryActivity() {
        startActivity(new Intent(MainActivity.this, CategoryActivity.class));
    }
}