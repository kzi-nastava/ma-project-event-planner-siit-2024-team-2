package com.example.eventplanner.clients.deserializers;

import com.example.eventplanner.model.event.EventReview;
import com.example.eventplanner.model.review.Review;
import com.example.eventplanner.model.serviceproduct.Product;
import com.example.eventplanner.model.serviceproduct.Service;
import com.example.eventplanner.model.serviceproduct.ServiceProduct;
import com.example.eventplanner.model.serviceproduct.ServiceProductReview;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class ReviewDeserializer implements JsonDeserializer<Review> {

    @Override
    public Review deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        JsonElement event = jsonObject.get("event");

        if (event != null)
            return context.deserialize(json, EventReview.class);
        else {
            JsonElement serviceProduct = jsonObject.get("serviceProduct");
            if (serviceProduct == null)
                return context.deserialize(json, Review.class);
            else
                return context.deserialize(json, ServiceProductReview.class);
        }
    }
}