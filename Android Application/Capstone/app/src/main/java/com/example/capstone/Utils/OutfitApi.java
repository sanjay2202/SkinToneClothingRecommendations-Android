package com.example.capstone.Utils;


import com.example.capstone.Model.OutfitModel;
import com.example.capstone.response.OutfitResponse;
import com.example.capstone.response.OutfitSearchResponse;

import java.util.HashMap;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface OutfitApi {

    //Search for outfits

//    http://192.168.160.209:5000/get_data
    // http://192.168.160.209:5000/search?query=Turtle+Check+Men+Navy+Blue+Shirt
    @GET("/search")
    Call<OutfitSearchResponse> searchOutfit(
            @Query("query") String query,
            @Query("page") int page,
            @Query("usage") String usage,
            @Query("season") String season,
            @Query("subCategory") String subCategory
    );


    @GET("/get_data")  // The route for getting the processed plot
    Call<OutfitSearchResponse> getOutfit(
            @Query("page") int page
    );

    @POST("/upload_profile_image")
    Call<Void> uploadProfileImage(@Body HashMap<String, String> imageURLMap);

    @POST("/send_try_on_image")
    Call<Void> uploadTryOnOutfit(@Body HashMap<String, String> imageURLMap);

    @GET("/get_Tried_Outfit")  // The route for getting the processed try-on image
    Call<OutfitResponse> getTriedOutfit();


    @GET("/get_recommended_outfits")
    Call<OutfitSearchResponse> getRecommendedOutfits(
            @Query("page") int page,
            @Query("usage") String usage,
            @Query("season") String season,
            @Query("subCategory") String subCategory
    );

    @POST("/upload")
    Call<Void> uploadProfileImageData(@Body HashMap<String, String> userData);


}
