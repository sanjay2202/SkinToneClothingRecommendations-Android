package com.example.capstone.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import com.bumptech.glide.Glide;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.capstone.Model.OutfitModel;
import com.example.capstone.R;
import com.example.capstone.Utils.Credentials;

import java.util.List;

public class OutfitRecyclerView extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<OutfitModel> mOutfits;
    private OnOutfitListener onOutfitListener;

    private static final int DISPLAY_RANDOM = 1;
    private static final int DISPLAY_SEARCH = 2;
    private static final int DISPLAY_RECOMMENDED = 3;

    public OutfitRecyclerView(OnOutfitListener onOutfitListener) {
        this.onOutfitListener = onOutfitListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = null;
        if (viewType == DISPLAY_SEARCH){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.outfit_list_item,
                    parent,false);
            return new OutfitViewHolder(view, onOutfitListener);
        }else if (viewType == DISPLAY_RANDOM) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.outfit_list_item,
                    parent,false);
            return new OutfitViewHolder(view, onOutfitListener);
        }else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.outfit_list_item,
                    parent,false);
            return new OutfitViewHolder(view, onOutfitListener);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        int itemViewType = getItemViewType(position);
        if (itemViewType == DISPLAY_SEARCH){
            ((OutfitViewHolder)holder).title.setText(mOutfits.get(position).getProductDisplayName());
            Log.v("Test","Title"+mOutfits.get(position).getProductDisplayName());
            ((OutfitViewHolder)holder).articleType.setText("ArticleType: "+mOutfits.get(position).getArticleType());
            ((OutfitViewHolder)holder).usage.setText("Usage: "+mOutfits.get(position).getUsage());


            // Set the Bitmap to the ImageView using Glide
            Glide.with(holder.itemView.getContext())
                    .load(mOutfits.get(position).getImage_url())
                    .into(((OutfitViewHolder)holder).imageView);
        } else if (itemViewType == DISPLAY_RANDOM) {
            ((OutfitViewHolder)holder).title.setText(mOutfits.get(position).getProductDisplayName());
            Log.v("Test","Title"+mOutfits.get(position).getProductDisplayName());
            ((OutfitViewHolder)holder).articleType.setText("ArticleType: "+mOutfits.get(position).getArticleType());
            ((OutfitViewHolder)holder).usage.setText("Usage: "+mOutfits.get(position).getUsage());

            // Set the Bitmap to the ImageView using Glide
            Glide.with(holder.itemView.getContext())
                    .load(mOutfits.get(position).getImage_url())
                    .into(((OutfitViewHolder)holder).imageView);
        }else {
            ((OutfitViewHolder)holder).title.setText(mOutfits.get(position).getProductDisplayName());
            Log.v("Test","Title"+mOutfits.get(position).getProductDisplayName());
            ((OutfitViewHolder)holder).articleType.setText("ArticleType: "+mOutfits.get(position).getArticleType());
            ((OutfitViewHolder)holder).usage.setText("Usage: "+mOutfits.get(position).getUsage());

            // Set the Bitmap to the ImageView using Glide
            Glide.with(holder.itemView.getContext())
                    .load(mOutfits.get(position).getImage_url())
                    .into(((OutfitViewHolder)holder).imageView);
        }


    }

    @Override
    public int getItemCount() {
        if (mOutfits != null){
            return mOutfits.size();
        }
        return 0;
    }


    public void setmOutfits(List<OutfitModel> mOutfits) {
        this.mOutfits = mOutfits;
        notifyDataSetChanged();
    }

    // Getting the name of the outfit clicked
    public OutfitModel getSelectedOutfit(int position){
        if (mOutfits != null){
            if (mOutfits.size() > 0){
                return mOutfits.get(position);
            }
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        if (Credentials.RANDOM) {
            return DISPLAY_RANDOM;
        } else if (Credentials.RECOMMENDED) {
            return DISPLAY_RECOMMENDED;
        } else {
            return DISPLAY_SEARCH;
        }
    }

}
