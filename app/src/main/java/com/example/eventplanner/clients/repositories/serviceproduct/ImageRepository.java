package com.example.eventplanner.clients.repositories.serviceproduct;

import android.content.Context;
import android.net.Uri;

import androidx.core.util.Pair;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.eventplanner.clients.services.serviceproduct.ImageService;
import com.example.eventplanner.clients.utils.ClientUtils;
import com.example.eventplanner.utils.SimpleCallback;

import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class ImageRepository {

    private final ImageService imageService;

    public ImageRepository() {
        this.imageService = ClientUtils.imageService;
    }

    public LiveData<Pair<String, String>> uploadImage(Context context, Uri imageUri) {
        MutableLiveData<Pair<String, String>> liveData = new MutableLiveData<>();

        try {
            InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
            if (inputStream == null) {
                liveData.setValue(null);
                return liveData;
            }

            byte[] imageData = new byte[inputStream.available()];
            inputStream.read(imageData);
            inputStream.close();

            String fileName = "image_" + System.currentTimeMillis() + ".jpg";

            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), imageData);
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", fileName, requestFile);

            Call<ResponseBody> call = imageService.uploadImage(body);

            call.enqueue(new SimpleCallback<>(
                    response -> {
                        if (response != null && response.isSuccessful() && response.body() != null) {
                            try {
                                liveData.setValue(new Pair<>(response.body().string(), null));
                            } catch (Exception e) {
                                liveData.setValue(null);
                            }
                        } else {
                            liveData.setValue(null);
                        }
                    },
                    error -> {
                        if (error != null && error.second != null)
                            liveData.setValue(new Pair<>(null, error.second));
                        else
                            liveData.setValue(null);
                    }
            ));

        } catch (Exception e) {
            liveData.setValue(null);
        }

        return liveData;
    }
}