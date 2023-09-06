package com.example.capstone.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.capstone.Model.OutfitModel;
import com.example.capstone.repositories.OutfitRepository;

import java.util.List;

public class OutfitListViewModel extends ViewModel {
    // This class is used for viewModel

    private OutfitRepository outfitRepository;

    public OutfitListViewModel() {
        outfitRepository = OutfitRepository.getInstance();
    }

    public LiveData<List<OutfitModel>> getOutfits(){
        return outfitRepository.getOutfits();
    }

    public LiveData<List<OutfitModel>> getRandom(){
        return outfitRepository.getRandom();
    }

    public LiveData<List<OutfitModel>> getRecommended(){
        return outfitRepository.getRecommended();
    }

    public void searchOutfitApi(String query, int pageNumber, String usage, String season, String subCategory) {
        outfitRepository.searchOutfitApi(query, pageNumber, usage, season, subCategory);
    }

    public void searchOutfitRandom(int pageNumber){
        outfitRepository.searchOutfitRandom(pageNumber);
    }

    public void searchOutfitRecommended(int pageNumber,String usage, String season, String subCategory){
        outfitRepository.searchOutfitRecommended(pageNumber, usage, season, subCategory);
    }

    public void searchNextPage(int pageNumber,String usage, String season, String subCategory){
        outfitRepository.searchNextPage(pageNumber,usage,season,subCategory);
    }


}
