package com.example.capstone.Fragments;

import static android.app.Activity.RESULT_OK;

import static kotlin.io.ByteStreamsKt.readBytes;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
//import com.example.capstone.ApiResponse;
//import com.example.capstone.ApiService;
import com.example.capstone.LoginActivity;
import com.example.capstone.MainActivity;
import com.example.capstone.Model.Users;
import com.example.capstone.R;
import com.example.capstone.Utils.Credentials;
import com.example.capstone.Utils.OutfitApi;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;


public class ProfileFragment extends Fragment {

    Button logout;
    TextView username,age,gender;
    ImageView imageView;
    DatabaseReference reference;
    FirebaseUser fuser;


//    private ApiService apiService;


    //Profile
    StorageReference storageReference;
    private static final int IMAGE_REQUEST =1;
    private Uri imageUri;
    private StorageTask uploadTask;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.Theme_ProfileFragment);
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        imageView = view.findViewById(R.id.profile_image2);
        username = view.findViewById(R.id.usernamee);
        age = view.findViewById(R.id.age);
        gender = view.findViewById(R.id.gender);
        logout = view.findViewById(R.id.logout);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(requireContext(), LoginActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        //Profile Image ref in storage
        storageReference = FirebaseStorage.getInstance().getReference("uploads");

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("MyUsers").child(fuser.getUid());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Credentials.BASE_URL) // Replace with your base URL
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        OutfitApi outfitApi = retrofit.create(OutfitApi.class);



//        apiService = retrofit.create(ApiService.class);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users user = snapshot.getValue(Users.class);
                username.setText("Username: "+user.getUsername());
                age.setText("Age: "+user.getAge());
                gender.setText("Gender: "+user.getGender());

                String Age = user.getAge();
                String Gender = user.getGender();
                String ImageUrl = user.getImageURL();

                if (user.getImageURL().equals("default")){
                    imageView.setImageResource(R.mipmap.ic_launcher);
                }else {
                    Glide.with(getContext()).load(user.getImageURL()).into(imageView);
                }

                // Prepare the data to send to Flask app
                HashMap<String, String> userData = new HashMap<>();
                userData.put("Age", Age);
                userData.put("Gender", Gender);
                userData.put("ImageUrl", ImageUrl);

                // Call the API to send data to Flask app
                Call<Void> call = outfitApi.uploadProfileImageData(userData);
                call.enqueue(new Callback<Void>() {


                    @Override
                    public void onResponse(Call<Void> call, retrofit2.Response<Void> response) {

                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        // Handle failure if needed
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectImage();
            }
        });


        return view;
    }

    private void SelectImage() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(i, IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap =  MimeTypeMap.getSingleton();
        return  mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

    }

    private void UploadMyImage(){
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Uploading");
        progressDialog.show();

        if (imageUri != null){
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    +"."+getFileExtension(imageUri));

            uploadTask = fileReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                    if (!task.isSuccessful()){
                        throw task.getException();
                    }

                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {

                    if (task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        String mUri = downloadUri.toString();

                        reference = FirebaseDatabase.getInstance().getReference("MyUsers").child(fuser.getUid());

                        HashMap<String, Object> map = new HashMap<>();
                        map.put("imageURL", mUri);
                        reference.updateChildren(map);
                        progressDialog.dismiss();

                        // Create a HashMap to send the image URL to the server
                        HashMap<String, String> imageURLMap = new HashMap<>();
                        imageURLMap.put("imageURL", mUri);

                        // Send the image URL to your Flask backend
//                        sendImageURLToServer(imageURLMap);

                    }else {
                        Toast.makeText(getContext(), "Failed!", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
        }else {
            Toast.makeText(getContext(), "No Image Selected", Toast.LENGTH_SHORT).show();
        }

        


    }

    private void sendImageURLToServer(HashMap<String, String> imageURLMap) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Credentials.BASE_URL)  // Replace with your Flask app's IP
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        OutfitApi outfitApi = retrofit.create(OutfitApi.class);

        Call<Void> call = outfitApi.uploadProfileImage(imageURLMap);
        call.enqueue(new Callback<Void>() {

            @Override
            public void onResponse(Call<Void> call, retrofit2.Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("ImageUpload", "Image URL sent to server successfully.");
                } else {
                    Log.e("ImageUpload", "Image URL upload failed.");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("ImageUpload", "Failed to send image URL to server.", t);
            }
        });
    }


//    private void sendImageToServerUsingApiService(Uri imageUri) throws IOException {
//        if (apiService == null) {
//            Log.e("API_SERVICE", "ApiService is null. Make sure to initialize it.");
//            return;
//        }
//
//        // Convert the selected image to a MultipartBody.Part
//        ContentResolver contentResolver = getContext().getContentResolver();
//        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
//        String fileExtension = mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(imageUri));
//
//        // Get the InputStream for the image
//        InputStream inputStream = getContext().getContentResolver().openInputStream(imageUri);
//
//        // Create the RequestBody from the InputStream
//        RequestBody requestBody = RequestBody.create(MediaType.parse("image/" + fileExtension), readBytes(inputStream));
//        MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image", "profile_image", requestBody);
//
//        // Use the ApiService to upload the image
//        Call<ApiResponse> call = apiService.uploadImage(imagePart);
//        call.enqueue(new Callback<ApiResponse>() {
//
//
//            @Override
//            public void onResponse(Call<ApiResponse> call, retrofit2.Response<ApiResponse> response) {
//                if (response.isSuccessful()) {
//                    ApiResponse apiResponse = response.body();
//                    if (apiResponse != null) {
//                        // Handle the response from the server if needed
//                        Log.d("API_RESPONSE", apiResponse.getMessage());
//                    }
//                } else {
//                    // Handle unsuccessful response
//                    Log.e("API_RESPONSE", "Failed to upload image. Response code: " + response.code());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ApiResponse> call, Throwable t) {
//                // Handle network or other errors
//                Log.e("API_RESPONSE", "Upload image failed. Error: " + t.getMessage());
//            }
//        });
//    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null  && data.getData() != null ){
            imageUri = data.getData();

            if (uploadTask != null && uploadTask.isInProgress()){
                Toast.makeText(getContext(), "Upload in Progress", Toast.LENGTH_SHORT).show();
            }else {
//                try {
//                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), imageUri);
//                    sendImageToServerUsingApiService(bitmap);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    Log.e("SEND_IMAGE", "Image processing failed. Exception: " + e.getMessage());
//                    Toast.makeText(getContext(), "Image processing failed", Toast.LENGTH_SHORT).show();
//                }
                UploadMyImage();

            }
        }
    }

//    private void sendImageToServerUsingApiService(Bitmap image) {
//        if (apiService == null) {
//            Log.e("API_SERVICE", "ApiService is null. Make sure to initialize it.");
//            return;
//        }
//
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
//        byte[] imageBytes = byteArrayOutputStream.toByteArray();
//
//        RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpeg"), imageBytes);
//        MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image", "image.jpg", requestBody);
//
//        Call<ApiResponse> call = apiService.uploadImage(imagePart);
//
//        call.enqueue(new Callback<ApiResponse>() {
//            @Override
//            public void onResponse(Call<ApiResponse> call, retrofit2.Response<ApiResponse> response) {
//                if (response.isSuccessful()) {
//                    ApiResponse apiResponse = response.body();
//                    if (apiResponse != null) {
//                        // Handle the response from the server if needed
//                        Log.d("API_RESPONSE", apiResponse.getMessage());
//                    }
//                } else {
//                    // Handle unsuccessful response
//                    Log.e("API_RESPONSE", "Failed to upload image. Response code: " + response.code());
//                }
//            }
//
//
//
//            @Override
//            public void onFailure(Call<ApiResponse> call, Throwable t) {
//                // Handle network or other errors
//                Log.e("API_RESPONSE", "Upload image failed. Error: " + t.getMessage());
//            }
//        });
//    }
}

