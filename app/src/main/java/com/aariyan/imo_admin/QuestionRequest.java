package com.aariyan.imo_admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.aariyan.imo_admin.Adapter.QuestionAdapter;
import com.aariyan.imo_admin.Constant.Constant;
import com.aariyan.imo_admin.Model.QuestionModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class QuestionRequest extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private QuestionAdapter adapter;
    private List<QuestionModel> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_request);

        initUI();
    }

    private void initUI() {
        progressBar = findViewById(R.id.progressbar);
        recyclerView = findViewById(R.id.questionRequestRecyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadQuestion();
    }

    private void loadQuestion() {

        Constant.questionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    list.clear();
                    for (DataSnapshot dataSnapshot:snapshot.getChildren()) {
                        QuestionModel model = dataSnapshot.getValue(QuestionModel.class);
                        assert model != null;
                        if (model.getStatus().equals("pending")) {
                            list.add(model);
                        }
                    }
                    adapter = new QuestionAdapter(QuestionRequest.this,list);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                } else {
                    Toast.makeText(QuestionRequest.this, "No Pending Question!", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}