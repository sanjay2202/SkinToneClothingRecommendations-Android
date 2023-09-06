package com.example.capstone.response;

import com.example.capstone.Model.OutfitModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OutfitResponse {

    @SerializedName("results")
    @Expose
    private OutfitModel outfit;

    public OutfitModel getOutfit(){
        return outfit;
    }

    @Override
    public String toString() {
        return "OutfitResponse{" +
                "outfit=" + outfit +
                '}';
    }

    private String image;

    public String getImage() {
        return image;
    }

}
