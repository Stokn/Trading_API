package com.vulturi.trading.api.backend.stripe;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import org.springframework.stereotype.Service;

@Service
public class StripeClientService {

    private OkHttpClient okHttpClient;
    private ObjectMapper objectMapper = new ObjectMapper();

    private static String baseUrl = "https://api.stripe";

}
