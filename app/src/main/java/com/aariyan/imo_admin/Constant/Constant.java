package com.aariyan.imo_admin.Constant;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Constant {

    public static DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("QUIZ").child("User");

    public static DatabaseReference categoryRef = FirebaseDatabase.getInstance().getReference().child("QUIZ").child("Category");
    public static DatabaseReference subCategoryRef = FirebaseDatabase.getInstance().getReference().child("QUIZ").child("SubCategory");
    public static DatabaseReference questionRef = FirebaseDatabase.getInstance().getReference().child("QUIZ").child("Questions");
    public static DatabaseReference notificationRef = FirebaseDatabase.getInstance().getReference().child("Notification");
    public static DatabaseReference paymentRef = FirebaseDatabase.getInstance().getReference().child("Payment");
    public static DatabaseReference pointRef = FirebaseDatabase.getInstance().getReference().child("Point");


    public static  String getCurrentDate() {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static  String getCurrentTime() {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }
}
