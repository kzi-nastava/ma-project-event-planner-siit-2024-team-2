package com.example.eventplanner.clients.utils;

import android.content.Context;

import com.example.eventplanner.BuildConfig;
import com.example.eventplanner.clients.interceptors.JwtInterceptor;
import com.example.eventplanner.clients.interceptors.UnauthorizedInterceptor;
import com.example.eventplanner.clients.services.auth.AuthService;
import com.example.eventplanner.clients.services.communication.InvitationService;
import com.example.eventplanner.clients.services.communication.NotificationService;
import com.example.eventplanner.clients.services.event.EventService;
import com.example.eventplanner.clients.services.event.EventTypeService;
import com.example.eventplanner.clients.services.order.BookingService;
import com.example.eventplanner.clients.services.serviceproduct.ProductService;
import com.example.eventplanner.clients.services.user.*;
import com.example.eventplanner.clients.services.serviceproduct.ServiceProductCategoryService;
import com.example.eventplanner.clients.services.serviceproduct.ServiceProductService;

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
    private static Retrofit retrofit;

    private static Interceptor getLoggingInterceptor() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return interceptor;
    }

    private static OkHttpClient getClient(Context context){
        JwtTokenProvider jwtTokenProvider = () -> JwtUtils.getJwtToken(context);
        return new OkHttpClient.Builder()
                .connectTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .addInterceptor(getLoggingInterceptor())
                .addInterceptor(new JwtInterceptor(jwtTokenProvider))
                .addInterceptor(new UnauthorizedInterceptor(context))
                .addInterceptor(chain -> {
                    Request request = chain.request().newBuilder()
                            .addHeader("User-Agent", "Mobile-Android")
                            .build();
                    return chain.proceed(request);
                })
                .build();
    }

    public static void init(Context context) {
        retrofit = new Retrofit.Builder()
                .baseUrl(SERVICE_API_PATH)
                .addConverterFactory(GsonConverterFactory.create())
                .client(getClient(context))
                .build();

        // Event
        eventService = retrofit.create(EventService.class);
        eventTypeService = retrofit.create(EventTypeService.class);
        invitationService = retrofit.create(InvitationService.class);

        // Order
        bookingService = retrofit.create(BookingService.class);

        // ServiceProduct
        serviceProductService = retrofit.create(ServiceProductService.class);
        serviceProductCategoryService = retrofit.create(ServiceProductCategoryService.class);
        productService = retrofit.create(ProductService.class);

        // User
        userService = retrofit.create(UserService.class);
        profileService = retrofit.create(ProfileService.class);

        // Auth
        authService = retrofit.create(AuthService.class);

        // Communication
        notificationService = retrofit.create(NotificationService.class);
    }

    // Event
    public static EventService eventService;
    public static EventTypeService eventTypeService;
    public static InvitationService invitationService;

    // Order
    public static BookingService bookingService;

    // ServiceProduct
    public static ServiceProductService serviceProductService;
    public static ServiceProductCategoryService serviceProductCategoryService;
    public static ProductService productService;

    // User
    public static UserService userService;
    public static ProfileService profileService;

    // Auth
    public static AuthService authService;

    // Communication
    public static NotificationService notificationService;
}
