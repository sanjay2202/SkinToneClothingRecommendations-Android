package com.example.capstone.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.widget.SearchView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
//import android.widget.SearchView;
import android.widget.Spinner;


//import com.example.capstone.ApiResponse;
//import com.example.capstone.ApiService;
import com.example.capstone.Adapter.OnOutfitListener;
import com.example.capstone.Adapter.OutfitRecyclerView;
import com.example.capstone.Model.OutfitModel;
import com.example.capstone.OutfitDetails;
import com.example.capstone.R;
import com.example.capstone.Utils.Credentials;
import com.example.capstone.viewModels.OutfitListViewModel;

import java.util.List;


public class CatalogFragment extends Fragment implements OnOutfitListener {

//    ImageView testImg;
//    Button hitApi;
//    boolean imageAvailable = false; // Flag to track image availability

    private Spinner usageSpinner;
    private Spinner seasonSpinner;
    private Spinner subCategorySpinner;
    private RecyclerView recyclerView;
    private OutfitRecyclerView outfitRecyclerAdapter;

    private Button generateRecommendation;

    boolean isRandom = true;
    boolean isRecommended = false;

    Toolbar toolbar;

    String base64Image; // Store the base64 image data

    //ViewModel
    private OutfitListViewModel outfitListViewModel;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_catalog, container, false);

        // Define spinner data arrays
        String[] usageData = {"","Casual", "Formal", "Sports","Ethnic"};
        String[] seasonData = {"","Fall", "Winter", "Summer"};
        String[] subCategoryData = {"","Topwear", "Bottomwear", "Saree", "Dress","Apparel Set" };


        usageSpinner = view.findViewById(R.id.spinner_usage);
        seasonSpinner = view.findViewById(R.id.spinner_season);
        subCategorySpinner = view.findViewById(R.id.spinner_sub_category);

        // Set up ArrayAdapter for spinners (You'll need to provide appropriate data)
        ArrayAdapter<String> usageAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, usageData);
        ArrayAdapter<String> seasonAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, seasonData);
        ArrayAdapter<String> subCategoryAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, subCategoryData);

        // Set dropdown layout style
        usageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        seasonAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Set adapters to spinners
        usageSpinner.setAdapter(usageAdapter);
        seasonSpinner.setAdapter(seasonAdapter);
        subCategorySpinner.setAdapter(subCategoryAdapter);

        toolbar = view.findViewById(R.id.toolbar);

        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);


        generateRecommendation = view.findViewById(R.id.generateRecommendations);
        generateRecommendation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Credentials.RANDOM = false;      // Set to false to display recommended outfits
                Credentials.RECOMMENDED = true;  // Set to true to display recommended outfits
                String selectedUsage = usageSpinner.getSelectedItem().toString();
                String selectedSeason = seasonSpinner.getSelectedItem().toString();
                String selectedSubCategory = subCategorySpinner.getSelectedItem().toString();
                outfitListViewModel.searchOutfitRecommended(
                        1,
                        selectedUsage,
                        selectedSeason,
                        selectedSubCategory);  // Fetch recommended outfits
            }
        });

//        testImg = view.findViewById(R.id.testImg);
//        hitApi = view.findViewById(R.id.hitApi);

//        hitApi.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                performImageRequest();
//            }
//        });



//        hitApi.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                searchOutfitApi("Men",1);
//            }
//        });

        recyclerView = view.findViewById(R.id.recyclerView);

        outfitListViewModel = new ViewModelProvider(this).get(OutfitListViewModel.class);


        ObserveAnyChange();
        ObserveRandomOutfits();
        ObserveRecommendedOutfits();
        SetupSearchView(view);
        // Observe for searched outfits
        observeSearchedOutfits();
        ConfigureRecyclerView();




        // Getting random Outfits
        outfitListViewModel.searchOutfitRandom(1);




        return view;
    }

    private void ObserveRecommendedOutfits() {
        outfitListViewModel.getRecommended().observe(getViewLifecycleOwner(), new Observer<List<OutfitModel>>() {
            @Override
            public void onChanged(List<OutfitModel> outfitModels) {
                // Observing for any data change
                if (outfitModels != null){
                    for (OutfitModel outfitModel : outfitModels){
                        //Get data in log
                        //Log.v("Tagy", "onChanged: " +outfitModel.getProductDisplayName());

                        outfitRecyclerAdapter.setmOutfits(outfitModels);
                    }
                }

            }
        });
    }

    // Observe any change for random Outfits
    private void ObserveRandomOutfits() {
        outfitListViewModel.getRandom().observe(getViewLifecycleOwner(), new Observer<List<OutfitModel>>() {
            @Override
            public void onChanged(List<OutfitModel> outfitModels) {
                // Observing for any data change
                if (outfitModels != null){
                    for (OutfitModel outfitModel : outfitModels){
                        //Get data in log
                        //Log.v("Tagy", "onChanged: " +outfitModel.getProductDisplayName());

                        outfitRecyclerAdapter.setmOutfits(outfitModels);
                    }
                }

            }
        });
    }

    // Observing any data change
    private void ObserveAnyChange(){
        outfitListViewModel.getOutfits().observe(getViewLifecycleOwner(), new Observer<List<OutfitModel>>() {
            @Override
            public void onChanged(List<OutfitModel> outfitModels) {
                // Observing for any data change
                if (outfitModels != null){
                    for (OutfitModel outfitModel : outfitModels){
                        //Get data in log
                        //Log.v("Tagy", "onChanged: " +outfitModel.getProductDisplayName());

                        outfitRecyclerAdapter.setmOutfits(outfitModels);
                    }
                }

            }
        });
    }

    private void observeSearchedOutfits() {
        outfitListViewModel.getOutfits().observe(getViewLifecycleOwner(), new Observer<List<OutfitModel>>() {
            @Override
            public void onChanged(List<OutfitModel> outfitModels) {
                if (!isRandom) {
                    // Update your recycler view adapter with searched outfits
                    outfitRecyclerAdapter.setmOutfits(outfitModels);
                }
            }
        });
    }

    private void SetupSearchView(View view) {
        final SearchView searchView = view.findViewById(R.id.search_view);

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                return false;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String selectedUsage = usageSpinner.getSelectedItem().toString();
                String selectedSeason = seasonSpinner.getSelectedItem().toString();
                String selectedSubCategory = subCategorySpinner.getSelectedItem().toString();
                outfitListViewModel.searchOutfitApi(
                        // The search string
                        query,
                        1,
                        selectedUsage,
                        selectedSeason,
                        selectedSubCategory
                );
                return false;

            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        searchView.setOnSearchClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                isRandom = false;
                isRecommended = false;



            }

        });

    }



//    private void searchOutfitApi(String query, int pageNumber){
//        outfitListViewModel.searchOutfitApi(query, pageNumber);
//    }

//    private void GetRetrofitResponse() {
//        OutfitApi outfitApi = Servicey.getOutfitApi();
//
//        Call<OutfitSearchResponse> responseCall = outfitApi.searchOutfit(
//                "Turtle Check Men Navy Blue Shirt",
//                1
//        );
//
//        responseCall.enqueue(new Callback<OutfitSearchResponse>() {
//            @Override
//            public void onResponse(Call<OutfitSearchResponse> call, Response<OutfitSearchResponse> response) {
//                if (response.code() == 200){
//                    Log.v("Tag", "the response" + response.body().toString());
//
//                    List<OutfitModel> outfits = new ArrayList<>(response.body().getOutfits());
//
//                    for (OutfitModel outfit: outfits){
//                        Log.v("Tag", "The Product" + outfit.getSubCategory());
//                    }
//                }else {
//                    try {
//                        Log.v("Tag", "ERROR" + response.errorBody().toString());
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//
//            }
//
//            @Override
//            public void onFailure(Call<OutfitSearchResponse> call, Throwable t) {
//
//            }
//        });
//
//
//    }
//
//    private void GetRetrofitResponseAccordingToID(){
//    OutfitApi outfitApi = Servicey.getOutfitApi();
//    Call<OutfitModel> responseCall = outfitApi.getOutfit();
//
//    responseCall.enqueue(new Callback<OutfitModel>() {
//        @Override
//        public void onResponse(Call<OutfitModel> call, Response<OutfitModel> response) {
//            if (response.code() == 200){
//                OutfitModel outfit = response.body();
//                Log.v("Tagg", "the response" + outfit.getSubCategory());
//
//
//            }else {
//                try {
//                    Log.v("Tag", "ERROR" + response.errorBody().toString());
//                } catch (Exception e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        }
//
//        @Override
//        public void onFailure(Call<OutfitModel> call, Throwable t) {
//
//        }
//    });
//    }

    // Initializing the recyclerView
    private void ConfigureRecyclerView(){
        outfitRecyclerAdapter = new OutfitRecyclerView( this);

        recyclerView.setAdapter(outfitRecyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                String selectedUsage = usageSpinner.getSelectedItem().toString();
                String selectedSeason = seasonSpinner.getSelectedItem().toString();
                String selectedSubCategory = subCategorySpinner.getSelectedItem().toString();
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1)){
                    // Here we need to display the next search results
                    outfitListViewModel.searchNextPage(2,selectedUsage, selectedSeason, selectedSubCategory);
                }
            }
        });
    }



    @Override
    public void onOutfitClick(int position) {
        //Toast.makeText(requireContext(), "The Position" +position, Toast.LENGTH_SHORT).show();

        try {
            Intent intent = new Intent(requireContext(), OutfitDetails.class);
            intent.putExtra("outfit", outfitRecyclerAdapter.getSelectedOutfit(position));
            startActivity(intent);
        } catch (Exception e) {
            Log.e("CatalogFragment", "Error while starting OutfitDetails activity", e);
        }
    }



}