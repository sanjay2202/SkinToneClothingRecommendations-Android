package com.example.capstone.response;

import com.example.capstone.Model.OutfitModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

// This class is for getting multiple Outfits
public class OutfitSearchResponse {

    @SerializedName("total_results")
    @Expose()
    private int total_count;

    @SerializedName("results")
    @Expose()
    private List<OutfitModel> outfits;

    public int getTotal_count() {
        return total_count;
    }

    public List<OutfitModel> getOutfits(){
        return outfits;
    }

    @Override
    public String toString() {
        return "OutfitSearchResponse{" +
                "total_count=" + total_count +
                ", outfits=" + outfits +
                '}';
    }
}
