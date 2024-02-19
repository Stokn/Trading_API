package com.vulturi.trading.api.backend.ubble;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.vulturi.trading.api.models.user.User;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.stream.StreamSupport;

@Service@Slf4j
public class UbbleService {
    private OkHttpClient okHttpClient;
    private ObjectMapper objectMapper = new ObjectMapper();

    private static String baseUrl = "https://api.ubble.ai";


    public UbbleService(){
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    public UbbleIdentificationResponse getIdentification(User user,String identificationId) throws IOException {

        okHttpClient = new OkHttpClient.Builder()
                .build();

        Request identificationRequest = new Request.Builder()
                .get()
                .header("Authorization",Credentials.basic("N6YQLDO2KHCN3L2WOMNU3JV6CS2VV2","0EBE4TFAQB7PWP9UPBZAJU3C3YXS9TAU2Z6D5QV55I0MM203IF"))
                .url(baseUrl + "/identifications/"+identificationId+"/")
                .build();
        Response response = okHttpClient.newCall(identificationRequest).execute();
        log.info("Ubble AI Identification GET Request response status is {}", response.code());
        if (!response.isSuccessful()) {
            throw new RuntimeException("Cannot get identification for user" + user.getUserId() + ". Response code is " + response.code());
        }
        JsonNode jsonNode = objectMapper.readTree(response.body().string());
        Identification identification = objectMapper.readValue(jsonNode.get("data").toString(), Identification.class);
        JsonNode existingDocument = StreamSupport.stream(jsonNode.get("included").spliterator(), false).filter(jNode -> jNode.get("type").asText().equals("documents")).findAny().orElse(null);
        Document document = objectMapper.readValue(existingDocument.get("attributes").toString(), Document.class);
        UbbleIdentificationResponse ubbleIdentificationResponse = new UbbleIdentificationResponse();
        ubbleIdentificationResponse.setData(identification);
        ubbleIdentificationResponse.setDocument(document);
        return ubbleIdentificationResponse;
    }


    public UbbleIdentificationResponse getKycStart(IdentificationCreate identificationCreate) throws IOException {

        okHttpClient = new OkHttpClient.Builder()
                .build();
        RequestBody requestBody = RequestBody.create(objectMapper.writeValueAsString(identificationCreate), MediaType.parse("application/vnd.api+json"));

        Request identificationRequest = new Request.Builder()
                .post(requestBody)
                .header("Authorization",Credentials.basic("N6YQLDO2KHCN3L2WOMNU3JV6CS2VV2","0EBE4TFAQB7PWP9UPBZAJU3C3YXS9TAU2Z6D5QV55I0MM203IF"))
                .header("Accept","application/vnd.api+json")
                .header("Content-Type","application/vnd.api+json")
                .url(baseUrl + "/identifications/")
                .build();
        Response response = okHttpClient.newCall(identificationRequest).execute();
        log.info("Ubble AI Identification Post Request response status is {}", response.code());
        if (!response.isSuccessful()) {
            throw new RuntimeException("Cannot get identification for user" + identificationCreate.getData().getAttributes().getIdentificationForm().getExternalUserId()+ ". Response code is " + response.code());
        }

        return objectMapper.readValue(response.body().string(),UbbleIdentificationResponse.class);
    }










}
