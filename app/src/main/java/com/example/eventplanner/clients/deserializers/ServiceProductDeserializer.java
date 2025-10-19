package com.example.eventplanner.clients.deserializers;

import com.example.eventplanner.model.serviceproduct.Product;
import com.example.eventplanner.model.serviceproduct.Service;
import com.example.eventplanner.model.serviceproduct.ServiceProduct;
import com.example.eventplanner.utils.JsonLog;
import com.example.eventplanner.utils.JsonUtils;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class ServiceProductDeserializer implements JsonDeserializer<ServiceProduct> {

    @Override
    public ServiceProduct deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String type = jsonObject.get("dtype").getAsString();

        switch (type.toLowerCase()) {
            case "service":
                return context.deserialize(json, Service.class);
            case "product":
                return context.deserialize(json, Product.class);
            default:
                return context.deserialize(json, ServiceProduct.class); // fallback
        }
    }
}