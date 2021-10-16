package com.aariyan.imo_admin.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aariyan.imo_admin.Constant.Constant;
import com.aariyan.imo_admin.Model.SubCategoryModel;
import com.aariyan.imo_admin.PaymentRequest;
import com.aariyan.imo_admin.R;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

public class SubCategoryAdapter extends RecyclerView.Adapter<SubCategoryAdapter.ViewHolder> {

    private Context context;
    private List<SubCategoryModel> list;
    private Activity activity;
    public SubCategoryAdapter(Context context,List<SubCategoryModel>list, Activity activity) {
        this.context = context;
        this.list = list;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.single_category_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SubCategoryModel model = list.get(position);
        holder.name.setText(model.getSubCategoryName());

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constant.subCategoryRef.child(model.getId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context, "Deleted!", Toast.LENGTH_SHORT).show();
                        notifyDataSetChanged();
                        activity.finish();
                        activity.onBackPressed();
                    }
                });
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startQuestionActivity(model);
            }
        });
    }

    private void startQuestionActivity(SubCategoryModel model) {
        Intent intent = new Intent(context, PaymentRequest.class);
        intent.putExtra("categoryId", model.getParentId());
        intent.putExtra("subCategoryId", model.getSubCategoryName());
        intent.putExtra("name", model.getSubCategoryName());
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView name;
        private ImageView delete;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.categoryName);
            delete = itemView.findViewById(R.id.deleteCategory);
        }
    }
}
