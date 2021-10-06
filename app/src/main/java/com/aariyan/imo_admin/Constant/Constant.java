package com.aariyan.imo_admin.Constant;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Constant {

    public static DatabaseReference categoryRef = FirebaseDatabase.getInstance().getReference().child("QUIZ").child("Category");
    public static DatabaseReference subCategoryRef = FirebaseDatabase.getInstance().getReference().child("QUIZ").child("SubCategory");
}
