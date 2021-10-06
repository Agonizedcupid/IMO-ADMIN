package com.aariyan.imo_admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ContentInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.aariyan.imo_admin.Adapter.CategoryAdapter;
import com.aariyan.imo_admin.Constant.Constant;
import com.aariyan.imo_admin.Model.CategoryModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CategoryActivity extends AppCompatActivity {

    private ImageView backBtn,addCategory;

    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    private List<CategoryModel> list = new ArrayList<>();

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        context = CategoryActivity.this;

        initUI();
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
        recyclerView.setLayoutManager(new GridLayoutManager(CategoryActivity.this,2));

        progressBar = findViewById(R.id.progressbar);

        //Load Category
        loadCategory();
    }

    private void loadCategory() {
        Constant.categoryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    list.clear();
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                        CategoryModel model = dataSnapshot.getValue(CategoryModel.class);
                        list.add(model);
                    }
                    CategoryAdapter adapter = new CategoryAdapter(context,list);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                } else {
                    Toast.makeText(context, "No Category Found!", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void openDialog() {
        Dialog dialog = new Dialog(CategoryActivity.this);
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
        String id = UUID.randomUUID().toString()+System.currentTimeMillis();
        CategoryModel model = new CategoryModel(id,category);
        Constant.categoryRef.child(id).setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(CategoryActivity.this, "Added", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CategoryActivity.this, "Try again later!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }
}