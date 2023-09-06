package com.example.capstone.Adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.capstone.R;

public class OutfitViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    TextView title, articleType, usage;
    ImageView imageView;


    OnOutfitListener onOutfitListener;

    public OutfitViewHolder(@NonNull View itemView, OnOutfitListener onOutfitListener) {
        super(itemView);

        this.onOutfitListener = onOutfitListener;

        imageView = itemView.findViewById(R.id.outfit_img);
        title = itemView.findViewById(R.id.Outfit_title);
        articleType = itemView.findViewById(R.id.articleType);
        usage = itemView.findViewById(R.id.usage);

        itemView.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        onOutfitListener.onOutfitClick(getAdapterPosition());
    }
}
