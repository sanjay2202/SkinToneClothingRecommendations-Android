package com.example.capstone;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.capstone.Utils.Credentials;
import com.example.capstone.Utils.OutfitApi;
import com.example.capstone.response.OutfitResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TrialRoomActivity extends AppCompatActivity {

    ImageView triedOnOutfitImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trial_room);

        triedOnOutfitImg = findViewById(R.id.triedOnOutfit);

        // Initialize Retrofit and the API interface
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Credentials.BASE_URL)  // Replace with your Flask app's IP
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        OutfitApi outfitApi = retrofit.create(OutfitApi.class);

        // Fetch the image data from the Flask server
        Call<OutfitResponse> call = outfitApi.getTriedOutfit(); // Use the modified response class
        call.enqueue(new Callback<OutfitResponse>() {
            @Override
            public void onResponse(Call<OutfitResponse> call, Response<OutfitResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    OutfitResponse outfitResponse = response.body();
                    String base64Image = outfitResponse.getImage();

                    // Decode the base64 image string and display it in the ImageView
                    byte[] decodedImage = Base64.decode(base64Image, Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(decodedImage, 0, decodedImage.length);
                    triedOnOutfitImg.setImageBitmap(bitmap);
                } else {
                    Toast.makeText(TrialRoomActivity.this, "Failed to fetch image", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OutfitResponse> call, Throwable t) {
                Toast.makeText(TrialRoomActivity.this, "Failed to fetch image", Toast.LENGTH_SHORT).show();
            }
        });
    }


}