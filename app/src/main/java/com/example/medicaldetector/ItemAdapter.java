package com.example.medicaldetector;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolderClass> {
    Context context;
    List<ItemDetails> itemDetails;

    public ItemAdapter(Context context, List<ItemDetails> itemDetails) {
        this.context = context;
        this.itemDetails = itemDetails;
    }


    @NonNull
    @Override
    public ItemAdapter.ViewHolderClass onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ui, parent, false);
        ViewHolderClass viewHolderClass = new ViewHolderClass(view);

        return viewHolderClass;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemAdapter.ViewHolderClass holder, int position) {
               holder.exprDate.setText("Expire date: "+itemDetails.get(position).geteDate());
               holder.name.setText("Name: "+itemDetails.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return itemDetails.size();
    }

    public class ViewHolderClass extends RecyclerView.ViewHolder {

        TextView name,exprDate;

        public ViewHolderClass(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            exprDate = itemView.findViewById(R.id.exprDate);
        }
    }
}
