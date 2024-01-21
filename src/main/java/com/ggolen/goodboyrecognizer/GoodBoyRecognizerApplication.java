package com.ggolen.goodboyrecognizer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GoodBoyRecognizerApplication implements CommandLineRunner {

    @Autowired
    private CameraCaptureComponent cameraCaptureComponent;

    @Autowired
    private ClarifaiImageRecognition clarifaiImageRecognition;

    @Autowired
    private AudioService audioService;

    @Autowired
    private PredictedConceptsAnalyzator predictedConceptsAnalyzator;

    public static void main(String[] args) {
        SpringApplication.run(GoodBoyRecognizerApplication.class, args);
    }
    @Override
    public void run(String... args) throws Exception {
        System.out.println("Starting...");
        if(predictedConceptsAnalyzator.analyzeConceptsAndAskGPT(clarifaiImageRecognition.recognizeImage(cameraCaptureComponent.captureAndSendImage()))){
            audioService.playRandomAudio();
            System.out.println("Dog recognized!");
        } else {
            System.out.println("No dog recognized");
        }
    };
}

