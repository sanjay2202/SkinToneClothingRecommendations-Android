package com.example.capstone.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.capstone.Model.OutfitModel;
import com.example.capstone.Utils.OutfitApi;
import com.example.capstone.request.OutfitApiClient;
import com.example.capstone.response.OutfitSearchResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OutfitRepository {
    // acts as repository
    private static OutfitRepository instance;

    private OutfitApiClient outfitApiClient;

    private String mQuery;
    private int mPageNumber;

    private String mUsage;

    private String mSeason;
    private String mSubCategory;

    public static OutfitRepository getInstance(){
        if (instance == null){
            instance = new OutfitRepository();
        }
        return instance;
    }

    private OutfitRepository(){
        outfitApiClient = OutfitApiClient.getInstance();
    }


    public LiveData<List<OutfitModel>> getOutfits(){
        return outfitApiClient.getOutfits();
    }

    public LiveData<List<OutfitModel>> getRandom(){
        return outfitApiClient.getOutfitsRandom();
    }

    public LiveData<List<OutfitModel>> getRecommended(){
        return outfitApiClient.getOutfitsRecommended();
    }

    public void searchOutfitApi(String query, int pageNumber, String usage, String season, String subCategory) {
        // Store the query and page number for future use
        mQuery = query;
        mPageNumber = pageNumber;

        outfitApiClient.searchOutfitsApi(query, pageNumber, usage, season, subCategory);
    }

    public void searchOutfitRandom( int pageNumber){
        mPageNumber = pageNumber;
        outfitApiClient.searchOutfitsRandom(pageNumber);
    }

    public void searchOutfitRecommended( int pageNumber, String usage, String season, String subCategory){
        mPageNumber = pageNumber;

        outfitApiClient.searchOutfitsRecommended(pageNumber, usage, season, subCategory);
    }

    public void searchNextPage(int pageNumber, String usage, String season, String subCategory) {
        outfitApiClient.searchOutfitsApi(mQuery, mPageNumber + 1, usage, season, subCategory);
    }



}
