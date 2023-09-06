package com.example.capstone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.capstone.Model.OutfitModel;
import com.example.capstone.Utils.Credentials;
import com.example.capstone.Utils.OutfitApi;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OutfitDetails extends AppCompatActivity {

    private ImageView imageViewDetails;
    private TextView titleDetails, articleType, usage, subCategory, season;

    private static final int PICK_IMAGE_REQUEST = 2;
    private Uri imageUri;
    Button tryOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outfit_details);

        imageViewDetails = findViewById(R.id.imageViewDetails);
        titleDetails = findViewById(R.id.textView_title_details);
        articleType = findViewById(R.id.articleType);
        usage = findViewById(R.id.usage);
        subCategory = findViewById(R.id.subCategory);
        season = findViewById(R.id.season);
        tryOn = findViewById(R.id.TryOn);

        tryOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendImageURLToServer(imageUri.toString());
                Intent intent = new Intent(OutfitDetails.this, TrialRoomActivity.class);
                startActivity(intent);
            }
        });
        
        GetDataFromIntent();
    }

    private void sendImageURLToServer(String imageURL) {
        HashMap<String, String> imageURLMap = new HashMap<>();
        imageURLMap.put("imageUrl", imageURL);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Credentials.BASE_URL)  // Replace with your Flask app's IP
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        OutfitApi outfitApi = retrofit.create(OutfitApi.class);

        Call<Void> call = outfitApi.uploadTryOnOutfit(imageURLMap);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("ImageUpload", "Image URL sent to server successfully.");
                    Toast.makeText(OutfitDetails.this, "Try On image sent to server", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("ImageUpload", "Image URL upload failed.");
                    Toast.makeText(OutfitDetails.this, "Failed to send Try On image to server", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("ImageUpload", "Failed to send image URL to server.", t);
                Toast.makeText(OutfitDetails.this, "Failed to send Try On image to server", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void GetDataFromIntent() {
        if (getIntent().hasExtra("outfit")){
            OutfitModel outfitModel = getIntent().getParcelableExtra("outfit");
            Log.v("Tagy", "Incoming Intent "+outfitModel.getProductDisplayName());

            titleDetails.setText(outfitModel.getProductDisplayName());
            articleType.setText("Article: "+outfitModel.getArticleType());
            subCategory.setText("Category: "+outfitModel.getSubCategory());
            usage.setText("Usage: "+outfitModel.getUsage());
            season.setText("Season: "+outfitModel.getSeason());

            Glide.with(this)
                    .load(outfitModel.getImage_url())
                    .into(imageViewDetails);

            imageUri = Uri.parse(outfitModel.getImage_url()); // Store the image URI for later use
            Log.d("ImageUpload", "Image URI: " + imageUri);

        }
    }



}