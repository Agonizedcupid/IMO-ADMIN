package com.aariyan.imo_admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.aariyan.imo_admin.Constant.Constant;
import com.aariyan.imo_admin.Model.PointModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class PointActivity extends AppCompatActivity {

    private EditText questionPoint,wrongAnswerPoint,rightAnswerPoint;
    private Button questionBtn,wrongBtn,rightBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point);

        initUI();

        loadPoint();
    }

    private void loadPoint() {
        Constant.pointRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot:snapshot.getChildren()) {
                        PointModel model = dataSnapshot.getValue(PointModel.class);
                        questionPoint.setText(model.getQuestionPoint(), TextView.BufferType.EDITABLE);
                        wrongAnswerPoint.setText(model.getWrongAnswerPoint(), TextView.BufferType.EDITABLE);
                        rightAnswerPoint.setText(model.getRightAnswerPoint(), TextView.BufferType.EDITABLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initUI() {
        questionPoint = findViewById(R.id.questionPointEditText);
        wrongAnswerPoint = findViewById(R.id.wrongAnswerEditText);
        rightAnswerPoint = findViewById(R.id.rightAnswerEditText);
        questionBtn = findViewById(R.id.questionPointBtn);
        wrongBtn = findViewById(R.id.wrongAnswerBtn);
        rightBtn = findViewById(R.id.rightAnswerBtn);

        questionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constant.pointRef.child("01").child("questionPoint").setValue(questionPoint.getText().toString());
                finish();
            }
        });

        wrongBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constant.pointRef.child("01").child("wrongAnswerPoint").setValue(wrongAnswerPoint.getText().toString());
                finish();
            }
        });

        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constant.pointRef.child("01").child("rightAnswerPoint").setValue(rightAnswerPoint.getText().toString());
                finish();
            }
        });
    }
}