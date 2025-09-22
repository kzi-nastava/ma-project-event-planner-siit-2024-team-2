package com.example.eventplanner.repositories;


import android.util.Log;
import androidx.lifecycle.LiveData;
import com.example.eventplanner.clients.ClientUtils;
import com.example.eventplanner.dto.EventTypeDto;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventTypeRepository {

    public LiveData<List<EventTypeDto>> getEventTypes() {
        Call<List<EventTypeDto>> call = ClientUtils.eventTypeService.getEventTypes();
        call.enqueue(new Callback<List<EventTypeDto>>() {
            @Override
            public void onResponse(Call<List<EventTypeDto>> call, Response<List<EventTypeDto>> response) {
                if (response.isSuccessful()) {
                    Log.e("Home", response.message());
                    Log.e("Home", String.valueOf(response.body().size()));
                    for (EventTypeDto type: response.body()) {
                        Log.e("Home", type.toString());
                    }
                } else {
                    Log.e("Home", "Failed to fetch products. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<EventTypeDto>> call, Throwable t) {
                Log.w("error", t.getMessage());
            }
        });
        return null;
    }
}
