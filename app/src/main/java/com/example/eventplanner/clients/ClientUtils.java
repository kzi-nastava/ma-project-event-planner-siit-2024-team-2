package com.example.eventplanner.clients;

import com.example.eventplanner.BuildConfig;
import com.example.eventplanner.clients.event.EventService;
import com.example.eventplanner.clients.event.EventTypeService;
import com.example.eventplanner.clients.order.BookingService;
import com.example.eventplanner.clients.serviceproduct.ServiceProductService;

import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ClientUtils {

    //EXAMPLE: http://192.168.43.73:8080/api/
    public static final String SERVICE_API_PATH = "http://"+ BuildConfig.IP_ADDR +":8080/api/";

    private static Interceptor getLoggingInterceptor() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return interceptor;
    }

    private static OkHttpClient getClient(){
        return new OkHttpClient.Builder()
                .connectTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .addInterceptor(getLoggingInterceptor())
                .addInterceptor(chain -> {
                    Request request = chain.request().newBuilder()
                            .addHeader("User-Agent", "Mobile-Android")
                            .build();
                    return chain.proceed(request);
                })
                .build();
    }

    public static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(SERVICE_API_PATH)
            .addConverterFactory(GsonConverterFactory.create())
            .client(getClient())
            .build();

    // Event
    public static EventService eventService = retrofit.create(EventService.class);
    public static EventTypeService eventTypeService = retrofit.create(EventTypeService.class);

    // Order
    public static BookingService bookingService = retrofit.create(BookingService.class);

    // ServiceProduct
    public static ServiceProductService serviceProductService = retrofit.create(ServiceProductService.class);

    // User

    // Auth
}
