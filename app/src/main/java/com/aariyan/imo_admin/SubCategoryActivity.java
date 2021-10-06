package com.aariyan.imo_admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.aariyan.imo_admin.Adapter.SubCategoryAdapter;
import com.aariyan.imo_admin.Constant.Constant;
import com.aariyan.imo_admin.Model.CategoryModel;
import com.aariyan.imo_admin.Model.SubCategoryModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SubCategoryActivity extends AppCompatActivity {

    private String id = "";

    private ImageView backBtn,addCategory;

    private RecyclerView recyclerView;
    private static ProgressBar progressBar;

    private List<SubCategoryModel> list = new ArrayList<>();

    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_category);

        manageIntent();

        context = SubCategoryActivity.this;

        initUI();
    }

    private void manageIntent() {
        if (getIntent() != null) {
            id = getIntent().getStringExtra("id");
        }
    }

    private void initUI() {
        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(view -> onBackPressed());

        addCategory = findViewById(R.id.addCategoryBtn);
        addCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
        });

        recyclerView = findViewById(R.id.categoryRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(context,2));

        progressBar = findViewById(R.id.progressbar);

        //Load Category
        loadSubCategory();
    }

    public void loadSubCategory() {
        Constant.subCategoryRef.orderByChild("parentId").equalTo(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    list.clear();
                    for (DataSnapshot dataSnapshot:snapshot.getChildren()) {
                        SubCategoryModel model = dataSnapshot.getValue(SubCategoryModel.class);
                        list.add(model);
                    }
                    SubCategoryAdapter adapter = new SubCategoryAdapter(SubCategoryActivity.this,list,SubCategoryActivity.this);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(SubCategoryActivity.this, "No data found!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void openDialog() {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.category_layout);

        EditText category = dialog.findViewById(R.id.categoryEditText);
        Button create = dialog.findViewById(R.id.createCategory);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(category.getText().toString().trim())) {
                    category.setError("Can't be empty");
                    category.requestFocus();
                    return;
                }
                createCategoryOnDatabase(category.getText().toString(),dialog);
            }
        });

        dialog.show();
    }

    private void createCategoryOnDatabase(String category, Dialog dialog) {
        String ids = UUID.randomUUID().toString()+System.currentTimeMillis();
        SubCategoryModel model = new SubCategoryModel(ids,category,id);
        Constant.subCategoryRef.child(ids).setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(context, "Added", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                loadSubCategory();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Try again later!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }
}