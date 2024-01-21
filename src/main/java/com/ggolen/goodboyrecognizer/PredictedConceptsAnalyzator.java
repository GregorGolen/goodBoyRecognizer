package com.ggolen.goodboyrecognizer;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.clarifai.grpc.api.*;
import com.clarifai.channel.ClarifaiChannel;
import com.clarifai.credentials.ClarifaiCallCredentials;

@Component
public class PredictedConceptsAnalyzator {
    private static final String USER_ID = "openai";
    private static final String APP_ID = "chat-completion";
    private static final String MODEL_ID = "gpt-4-turbo";
    private static final String MODEL_VERSION_ID = "182136408b4b4002a920fd500839f2c8";

    private static String PAT;
    @Value("${clarifai.api.key}")
    public void setApiKey(String apiKey) {
        this.PAT = apiKey;
    }

    boolean analyzeConceptsAndAskGPT(String concepts) {
        String question = "Create a JSON response with a key 'isDog' and value 'yes' or 'no' indicating whether there is a dog in these concepts: " + concepts + "?";
        V2Grpc.V2BlockingStub stub = V2Grpc.newBlockingStub(ClarifaiChannel.INSTANCE.getGrpcChannel())
                .withCallCredentials(new ClarifaiCallCredentials(PAT));

        try {
            MultiOutputResponse response = stub.postModelOutputs(
                    PostModelOutputsRequest.newBuilder()
                            .setUserAppId(UserAppIDSet.newBuilder().setUserId(USER_ID).setAppId(APP_ID))
                            .setModelId(MODEL_ID)
                            .setVersionId(MODEL_VERSION_ID)
                            .addInputs(
                                    Input.newBuilder().setData(
                                            Data.newBuilder().setText(
                                                    Text.newBuilder().setRaw(question)
                                            )
                                    )
                            )
                            .build()
            );
            return isDog(response.getOutputs(0).getData().getText().getRaw());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isDog(String response) {
        try {
            String jsonPart = response.substring(response.indexOf("{"), response.lastIndexOf("}") + 1);
            JSONObject outputsJson = new JSONObject(jsonPart);
            return outputsJson.optString("isDog", "no").equalsIgnoreCase("yes");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

