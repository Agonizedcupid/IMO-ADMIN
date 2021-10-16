package com.aariyan.imo_admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LogIn extends AppCompatActivity {

    private Button logInBtn;
    private EditText emailId, password;

    private FirebaseAuth userAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        userAuth = FirebaseAuth.getInstance();

        initUI();
    }

    @Override
    protected void onResume() {
        if (userAuth.getCurrentUser() != null) {
            startActivity(new Intent(LogIn.this, MainActivity.class));
            finish();
        }

        super.onResume();
    }

    private void initUI() {
        logInBtn = findViewById(R.id.logInBtn);
        emailId = findViewById(R.id.emailEdtText);
        password = findViewById(R.id.passwordEdtText);

        logInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userAuth.signInWithEmailAndPassword(emailId.getText().toString(), password.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        startActivity(new Intent(LogIn.this, MainActivity.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LogIn.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}