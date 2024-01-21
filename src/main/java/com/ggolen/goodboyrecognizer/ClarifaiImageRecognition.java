package com.ggolen.goodboyrecognizer;

import com.clarifai.grpc.api.*;
import com.clarifai.channel.ClarifaiChannel;
import com.clarifai.credentials.ClarifaiCallCredentials;
import com.clarifai.grpc.api.status.StatusCode;
import com.google.protobuf.ByteString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;


@Component
public class ClarifaiImageRecognition {

    private static final String USER_ID = "clarifai";
    private static final String APP_ID = "main";
    private static final String MODEL_ID = "general-image-recognition";

    private static String PAT;
    @Value("${clarifai.api.key}")
    public void setApiKey(String apiKey) {
        this.PAT = apiKey;
    }

    public String recognizeImage(byte[] imageBytes) {
        V2Grpc.V2BlockingStub stub = V2Grpc.newBlockingStub(ClarifaiChannel.INSTANCE.getGrpcChannel())
                .withCallCredentials(new ClarifaiCallCredentials(PAT));

        try {
            MultiOutputResponse response = stub.postModelOutputs(
                    PostModelOutputsRequest.newBuilder()
                            .setUserAppId(UserAppIDSet.newBuilder().setUserId(USER_ID).setAppId(APP_ID))
                            .setModelId(MODEL_ID)
                            .addInputs(
                                    Input.newBuilder().setData(
                                            Data.newBuilder().setImage(
                                                    Image.newBuilder().setBase64(ByteString.copyFrom(imageBytes))
                                            )
                                    )
                            )
                            .build()
            );

            if (response.getStatus().getCode() != StatusCode.SUCCESS) {
                throw new RuntimeException("API request failed, status: " + response.getStatus());
            }

            Output output = response.getOutputs(0);
            String result = output.getData().getConceptsList().stream()
                    .map(concept -> String.format("%s %.2f", concept.getName(), concept.getValue()))
                    .collect(Collectors.joining("\n", "Predicted concepts:\n", ""));

            System.out.println("Predicted concepts delivered");
            return result;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "No concepts recognized";
    }
}

