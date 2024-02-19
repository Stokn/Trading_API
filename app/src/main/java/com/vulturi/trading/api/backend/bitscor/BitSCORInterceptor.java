package com.vulturi.trading.api.backend.bitscor;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;

@Slf4j
class BitSCORInterceptor implements Interceptor {

    private String clientId;

    private String clientSecret;

    public BitSCORInterceptor(String clientId, String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }
    private String getToken() throws IOException {
        OkHttpClient client = new OkHttpClient()
                .newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.Companion.create("grant_type=client_credentials&client_id=" + clientId + "&client_secret=" + clientSecret, mediaType);
        Request request = new Request.Builder()
                .url("https://bitscor.auth.eu-west-3.amazoncognito.com/oauth2/token")
                .method("POST", body)
                .build();
        Response response = client.newCall(request).execute();
        HashMap responseMap = new ObjectMapper().readValue(response.body().byteStream(), HashMap.class);
        return (String) responseMap.get("access_token");
    }


    @NotNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        log.info(String.format("Sending request %s on %s%n%s",
                request.url(), chain.connection(), request.headers()));
        request = request.newBuilder().header("Authorization", "Bearer " + getToken()).build();
        return chain.proceed(request);
    }
}