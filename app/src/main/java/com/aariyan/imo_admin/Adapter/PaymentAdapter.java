package com.aariyan.imo_admin.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator;
import androidx.recyclerview.widget.RecyclerView;

import com.aariyan.imo_admin.Constant.Constant;
import com.aariyan.imo_admin.Interface.APISerrvice;
import com.aariyan.imo_admin.Model.NotificationModel;
import com.aariyan.imo_admin.Model.PaymentRequestModel;
import com.aariyan.imo_admin.Model.QuestionModel;
import com.aariyan.imo_admin.Notification.Client;
import com.aariyan.imo_admin.Notification.Data;
import com.aariyan.imo_admin.Notification.MyResponse;
import com.aariyan.imo_admin.Notification.Sender;
import com.aariyan.imo_admin.Notification.Token;
import com.aariyan.imo_admin.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.ViewHolder> {

    private Context context;
    private List<PaymentRequestModel> list;

    //Notification Part:
    APISerrvice apiSerrvice;
    private static String whatTypes = "";

    private String invitationMessage = "Your Question";
    private String receiverId;
    public static boolean notify = false;

    private String notificationStatus = "Your payment request updated!";
    private FirebaseAuth userAuth;

    public PaymentAdapter(Context context, List<PaymentRequestModel> list) {
        this.context = context;
        this.list = list;
        apiSerrvice = Client.getClient("https://fcm.googleapis.com/").create(APISerrvice.class);
        userAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.single_payment_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        PaymentRequestModel model = list.get(position);
        holder.userPhone.setText("Phone: "+model.getPhoneNumber());
        holder.statusBox.setText(model.getStatus(), TextView.BufferType.EDITABLE);
        holder.requestedPoint.setText("Requested: " + model.getPoint() + " Points.");
        holder.dateTime.setText(model.getDate() + "  " + model.getTime());
        holder.messageBox.setText(model.getMessage(), TextView.BufferType.EDITABLE);
        holder.paymentId.setText("Pay To: " + model.getPaymentId());
        holder.paymentMethod.setText("Method: " + model.getPaymentMethod());
        holder.updateUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constant.paymentRef.child(model.getId()).child("message").setValue(holder.messageBox.getText().toString());
                Constant.paymentRef.child(model.getId()).child("status").setValue(holder.statusBox.getText().toString());
                notifyItemChanged(position);
                sendNotification(model);
            }
        });
    }

    private void sendNotification(PaymentRequestModel model) {
        receiverId = model.getUserId();
        invitationMessage = notificationStatus;

        notify = true;
        sendMessage("Admin", receiverId, notificationStatus, invitationMessage, model.getId());
    }

    private void sendMessage(String s, String receiverId, String notificationStatus, String invitationMessage, String id) {
        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("sender", s);
        hashMap.put("receiver", receiverId);
        hashMap.put("message", invitationMessage);

        final String msg = invitationMessage;

        if (notify) {
            //assert users != null;
            sendNotification(receiverId, notificationStatus, msg, "");
        }

        notify = false;
    }

    private void sendNotification(final String receiver, final String notificationStatus, final String msg, String idForNotification) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Token");

        Query query = tokens.orderByKey().equalTo(receiver);
        String title = notificationStatus;

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Token token = snapshot.getValue(Token.class);

                    //Here data will be change

                    Data data = new Data(
                            userAuth.getCurrentUser().getUid(),
                            R.mipmap.ic_launcher,
                            "Please check your notification tab for more details!",
                            title,
                            receiverId,
                            whatTypes,
                            ""
                    );

                    Sender sender = new Sender(data, token.getToken());

                    Log.d("TOKEN_RESULT", token.getToken());

                    apiSerrvice.sendNotification(sender).enqueue(new Callback<MyResponse>() {
                        @Override
                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {

                            //Toast.makeText(context, "" + response.code(), Toast.LENGTH_SHORT).show();

                            if (response.isSuccessful()) {
                                //Toast.makeText(LoanEnquiryDetails.this, "Notification Sent", Toast.LENGTH_SHORT).show();
                                saveNotification(receiverId, notificationStatus, title, idForNotification);
                                //Toast.makeText(context, "Invitation sent.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "" + response.message(), Toast.LENGTH_SHORT).show();
                            }


                        }

                        @Override
                        public void onFailure(Call<MyResponse> call, Throwable t) {

                            Toast.makeText(context, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.d("TEST_RESULT", t.getMessage());
                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void saveNotification(String receiverId, String notificationStatus, String title, String idForNotification) {

        String id = UUID.randomUUID().toString();
        NotificationModel model = new NotificationModel(
                id,
                idForNotification,
                Constant.getCurrentDate(),
                Constant.getCurrentTime(),
                receiverId,
                "",
                title,
                notificationStatus
        );

        Constant.notificationRef.child(id).setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(context, "Notification Sent", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(context, "Unable to send notification", Toast.LENGTH_SHORT).show();
            }
        });

    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView userPhone, requestedPoint, dateTime, paymentMethod, paymentId;
        private EditText messageBox, statusBox;
        private Button updateUser;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userPhone = itemView.findViewById(R.id.phoneNumber);
            requestedPoint = itemView.findViewById(R.id.points);
            dateTime = itemView.findViewById(R.id.dateTime);
            messageBox = itemView.findViewById(R.id.messageBox);
            statusBox = itemView.findViewById(R.id.statusBox);
            updateUser = itemView.findViewById(R.id.updateUserBtn);

            paymentMethod = itemView.findViewById(R.id.paymentMethod);
            paymentId = itemView.findViewById(R.id.paymentId);
        }
    }
}
