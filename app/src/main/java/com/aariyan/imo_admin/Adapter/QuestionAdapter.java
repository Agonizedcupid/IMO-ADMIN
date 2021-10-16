package com.aariyan.imo_admin.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aariyan.imo_admin.Constant.Constant;
import com.aariyan.imo_admin.Interface.APISerrvice;
import com.aariyan.imo_admin.Model.NotificationModel;
import com.aariyan.imo_admin.Model.QuestionModel;
import com.aariyan.imo_admin.Notification.Client;
import com.aariyan.imo_admin.Notification.Data;
import com.aariyan.imo_admin.Notification.MyResponse;
import com.aariyan.imo_admin.Notification.Sender;
import com.aariyan.imo_admin.Notification.Token;
import com.aariyan.imo_admin.R;
import com.google.android.gms.common.internal.service.Common;
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

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.ViewHolder> {

    private Context context;
    private List<QuestionModel> list;

    //Notification Part:
    APISerrvice apiSerrvice;
    private static String whatTypes = "";

    private String invitationMessage = "Your Question";
    private String receiverId;
    public static boolean notify = false;

    private String notificationStatus = "";
    private FirebaseAuth userAuth;

    public QuestionAdapter(Context context,List<QuestionModel> list) {
        this.context = context;
        this.list = list;
        apiSerrvice = Client.getClient("https://fcm.googleapis.com/").create(APISerrvice.class);
        userAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.single_question_request,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        QuestionModel model = list.get(position);
        holder.questionName.setText("Q: "+model.getQuestion());
        holder.oOne.setText(model.getOptionOne());
        holder.oTwo.setText(model.getOptionTwo());
        holder.oThree.setText(model.getOptionThree());
        holder.oFour.setText(model.getOptionFour());
        holder.correctAnswer.setText(model.getAnswer());
        holder.acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constant.questionRef.child(model.getId()).child("status").setValue("accepted").addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        notificationStatus = "accepted";
                        acceptQuestion(model);
                        Toast.makeText(context, "Changed!", Toast.LENGTH_SHORT).show();
                    }
                });
                notifyItemChanged(position);
            }
        });

        holder.rejectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constant.questionRef.child(model.getId()).child("status").setValue("rejected").addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        notificationStatus = "rejected";
                        acceptQuestion(model);
                        Toast.makeText(context, "Changed!", Toast.LENGTH_SHORT).show();
                    }
                });
                notifyItemChanged(position);
            }
        });
    }

    private void acceptQuestion(QuestionModel model) {
        receiverId = model.getUploaderId();
        invitationMessage = "Your question "+notificationStatus;

        notify = true;
        sendMessage("Admin", receiverId, notificationStatus, invitationMessage,model.getId());
    }

    private void sendMessage(String s, String receiverId, String notificationStatus, String invitationMessage, String id) {
        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("sender", s);
        hashMap.put("receiver", receiverId);
        hashMap.put("message", invitationMessage);

        final String msg = invitationMessage;

        if (notify) {
            //assert users != null;
            sendNotification(receiverId, notificationStatus, msg,"");
        }

        notify = false;
    }

    private void sendNotification(final String receiver, final String notificationStatus, final String msg,String idForNotification) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Token");

        Query query = tokens.orderByKey().equalTo(receiver);
        String title = "Your question has been "+notificationStatus;

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

                    Log.d("TOKEN_RESULT",token.getToken());

                    apiSerrvice.sendNotification(sender).enqueue(new Callback<MyResponse>() {
                        @Override
                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {

                            //Toast.makeText(context, "" + response.code(), Toast.LENGTH_SHORT).show();

                            if (response.isSuccessful()) {
                                //Toast.makeText(LoanEnquiryDetails.this, "Notification Sent", Toast.LENGTH_SHORT).show();
                                saveNotification(receiverId, notificationStatus, title,idForNotification);
                                //Toast.makeText(context, "Invitation sent.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, ""+response.message(), Toast.LENGTH_SHORT).show();
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

        private TextView questionName,oOne,oTwo,oThree,oFour;
        private TextView correctAnswer;
        private RadioButton acceptBtn,rejectBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            questionName = itemView.findViewById(R.id.questionName);
            oOne = itemView.findViewById(R.id.oOne);
            oTwo = itemView.findViewById(R.id.oTwo);
            oThree = itemView.findViewById(R.id.oThree);
            oFour = itemView.findViewById(R.id.oFour);
            correctAnswer = itemView.findViewById(R.id.correctAnswer);
            acceptBtn = itemView.findViewById(R.id.acceptBtn);
            rejectBtn = itemView.findViewById(R.id.rejectBtn);
        }
    }
}
