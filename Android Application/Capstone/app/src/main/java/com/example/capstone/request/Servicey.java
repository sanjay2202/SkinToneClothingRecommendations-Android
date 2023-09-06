package com.example.capstone.request;

import com.example.capstone.Utils.Credentials;
import com.example.capstone.Utils.OutfitApi;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Servicey {

    private static Retrofit.Builder retrofitBuilder =
            new Retrofit.Builder()
                    .baseUrl(Credentials.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create());

    private static Retrofit retrofit = retrofitBuilder.build();

    private static OutfitApi outfitApi = retrofit.create(OutfitApi.class);

    public static OutfitApi getOutfitApi(){
        return outfitApi;
    }
}
