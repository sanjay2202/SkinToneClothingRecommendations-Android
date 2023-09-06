package com.example.capstone.request;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.capstone.AppExecutors;
import com.example.capstone.Model.OutfitModel;
import com.example.capstone.Utils.Credentials;
import com.example.capstone.response.OutfitSearchResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Response;

public class OutfitApiClient {
    //Live data for search
    private MutableLiveData<List<OutfitModel>> mOutfits;
    private static OutfitApiClient instance;
    // making Global Runnable
    private RetrieveOutFitsRunnable retrieveOutFitsRunnable;

    // Live data for recommended outfits
    private MutableLiveData <List<OutfitModel>> mOutfitsRecommended;
    // making Global Runnable
    private RetrieveOutFitsRunnableRecommended retrieveOutFitsRunnableRecommended;


    // Live data for Default Random On Load
    private MutableLiveData<List<OutfitModel>> mOutfitsRandom;

    // making Random Runnable
    private RetrieveOutFitsRunnableRandom retrieveOutFitsRunnableRandom;

    public static OutfitApiClient getInstance(){
        if (instance == null){
            instance = new OutfitApiClient();
        }
        return instance;
    }

    private OutfitApiClient(){
        mOutfits = new MutableLiveData<>();
        mOutfitsRandom = new MutableLiveData<>();
        mOutfitsRecommended = new MutableLiveData<>();
    }

    public LiveData<List<OutfitModel>> getOutfits(){
        return mOutfits;
    }

    public LiveData<List<OutfitModel>> getOutfitsRandom(){
        return mOutfitsRandom;
    }

    public LiveData<List<OutfitModel>> getOutfitsRecommended(){
        return mOutfitsRecommended;
    }

    public void searchOutfitsApi(String query, int pageNumber, String usage, String season, String subCategory) {
        if (retrieveOutFitsRunnable != null) {
            retrieveOutFitsRunnable.cancelRequest();
            retrieveOutFitsRunnable = null;
        }

        retrieveOutFitsRunnable = new RetrieveOutFitsRunnable(query, pageNumber, usage, season, subCategory);

        final Future myHandler = AppExecutors.getInstance().networkIO().submit(retrieveOutFitsRunnable);

        AppExecutors.getInstance().networkIO().schedule(new Runnable() {
            @Override
            public void run() {
                // Cancelling the retrofit call
                myHandler.cancel(true);
            }
        }, 3000, TimeUnit.MILLISECONDS);
    }

    public void searchOutfitsRandom( int pageNumber){
        Credentials.RANDOM = true;
        Credentials.RECOMMENDED = false;
        if (retrieveOutFitsRunnableRandom != null){
            retrieveOutFitsRunnableRandom = null;
        }

        retrieveOutFitsRunnableRandom = new RetrieveOutFitsRunnableRandom(pageNumber);

        final Future myHandler2 = AppExecutors.getInstance().networkIO().submit(retrieveOutFitsRunnableRandom);

        AppExecutors.getInstance().networkIO().schedule(new Runnable() {
            @Override
            public void run() {
                // Cancelling the retrofit call
                myHandler2.cancel(true);
            }
        }, 1000, TimeUnit.MILLISECONDS);
    }

    public void searchOutfitsRecommended( int pageNumber, String usage, String season, String subCategory){
        Credentials.RANDOM = false;
        Credentials.RECOMMENDED = true;
        if (retrieveOutFitsRunnableRecommended != null){
            retrieveOutFitsRunnableRecommended = null;
        }

        retrieveOutFitsRunnableRecommended = new RetrieveOutFitsRunnableRecommended(pageNumber, usage, season, subCategory);

        final Future myHandler3 = AppExecutors.getInstance().networkIO().submit(retrieveOutFitsRunnableRecommended);

        AppExecutors.getInstance().networkIO().schedule(new Runnable() {
            @Override
            public void run() {
                // Cancelling the retrofit call
                myHandler3.cancel(true);
            }
        }, 1000, TimeUnit.MILLISECONDS);
    }


    // Retrieving data from RESTApi by runnable class

    private class RetrieveOutFitsRunnable implements Runnable{

        private String query;
        private int pageNumber;
        private String usage; // Add this line
        private String season; // Add this line
        private String subCategory; // Add this line
        boolean cancelRequest;

        public RetrieveOutFitsRunnable(String query, int pageNumber, String usage, String season, String subCategory) {
            this.query = query;
            this.pageNumber = pageNumber;
            this.usage = usage; // Initialize usage
            this.season = season; // Initialize season
            this.subCategory = subCategory; // Initialize subCategory

            cancelRequest = false;
        }

        @Override
        public void run() {
            // Getting the response objects

            try {
                Response response = getOutfits(query, pageNumber, usage, season, subCategory).execute();
                if (cancelRequest){
                    return;
                }
                if (response.code() == 200){
                    List<OutfitModel> list = new ArrayList<>(((OutfitSearchResponse)response.body()).getOutfits());
                    if (pageNumber == 1){
                        //sending data to live data
                        // PostValue: used for background thread
                        // setValue: not for background thread
                        mOutfits.postValue(list);
                    }else {
                        List<OutfitModel> currentOutfits = mOutfits.getValue();
                        currentOutfits.addAll(list);
                        mOutfits.postValue(currentOutfits);
                    }
                }else {
                    String error = response.errorBody().string();
                    Log.v("TAG", "ERROR"+error);
                    mOutfits.postValue(null);
                }
            } catch (IOException e) {
                e.printStackTrace();
                mOutfits.postValue(null);
            }

        }

        private Call<OutfitSearchResponse> getOutfits(String query, int pageNumber, String usage, String season, String subCategory) {
            return Servicey.getOutfitApi().searchOutfit(query, pageNumber, usage, season, subCategory);
        }

        private void cancelRequest(){
            Log.v("Tag","Cancelling search Request");
            cancelRequest = true;
        }
    }

    private class RetrieveOutFitsRunnableRandom implements Runnable{


        private int pageNumber;
        boolean cancelRequest;

        public RetrieveOutFitsRunnableRandom( int pageNumber) {
            this.pageNumber = pageNumber;
            cancelRequest = false;
        }

        @Override
        public void run() {
            // Getting the response objects

            try {
                Response response2 = getRandom(pageNumber).execute();
                if (cancelRequest){
                    return;
                }
                if (response2.code() == 200){
                    List<OutfitModel> list = new ArrayList<>(((OutfitSearchResponse)response2.body()).getOutfits());
                    if (pageNumber == 1){
                        //sending data to live data
                        // PostValue: used for background thread
                        // setValue: not for background thread
                        mOutfitsRandom.postValue(list);
                    }else {
                        List<OutfitModel> currentOutfits = mOutfitsRandom.getValue();
                        currentOutfits.addAll(list);
                        mOutfitsRandom.postValue(currentOutfits);
                    }
                }else {
                    String error = response2.errorBody().string();
                    Log.v("TAG", "ERROR"+error);
                    mOutfitsRandom.postValue(null);
                }
            } catch (IOException e) {
                e.printStackTrace();
                mOutfitsRandom.postValue(null);
            }

        }

        private Call<OutfitSearchResponse> getRandom( int pageNumber){
            return Servicey.getOutfitApi().getOutfit(
                    pageNumber
            );
        }

        private void cancelRequest(){
            Log.v("Tag","Cancelling search Request");
            cancelRequest = true;
        }
    }


    private class RetrieveOutFitsRunnableRecommended implements Runnable{


        private int pageNumber;
        private String usage; // Add this line
        private String season; // Add this line
        private String subCategory; // Add this line
        boolean cancelRequest;

        public RetrieveOutFitsRunnableRecommended( int pageNumber, String usage, String season, String subCategory) {
            this.pageNumber = pageNumber;
            this.usage = usage; // Initialize usage
            this.season = season; // Initialize season
            this.subCategory = subCategory; // Initialize subCategory
            cancelRequest = false;
        }

        @Override
        public void run() {
            // Getting the response objects

            try {
                Response response3 = getRecommended(pageNumber).execute();
                if (cancelRequest){
                    return;
                }
                if (response3.code() == 200){
                    List<OutfitModel> list = new ArrayList<>(((OutfitSearchResponse)response3.body()).getOutfits());
                    if (pageNumber == 1){
                        //sending data to live data
                        // PostValue: used for background thread
                        // setValue: not for background thread
                        mOutfitsRecommended.postValue(list);
                    }else {
                        List<OutfitModel> currentOutfits = mOutfitsRecommended.getValue();
                        currentOutfits.addAll(list);
                        mOutfitsRecommended.postValue(currentOutfits);
                    }
                }else {
                    String error = response3.errorBody().string();
                    Log.v("TAG", "ERROR"+error);
                    mOutfitsRecommended.postValue(null);
                }
            } catch (IOException e) {
                e.printStackTrace();
                mOutfitsRecommended.postValue(null);
            }

        }

        private Call<OutfitSearchResponse> getRecommended( int pageNumber){
            return Servicey.getOutfitApi().getRecommendedOutfits(
                    pageNumber,
                    usage,
                    season,
                    subCategory);
        }

        private void cancelRequest(){
            Log.v("Tag","Cancelling search Request");
            cancelRequest = true;
        }
    }


}
