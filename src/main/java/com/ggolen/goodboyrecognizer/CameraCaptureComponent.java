package com.ggolen.goodboyrecognizer;

import com.github.sarxos.webcam.Webcam;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@Component
public class CameraCaptureComponent {

    private static final Logger LOGGER = Logger.getLogger(CameraCaptureComponent.class.getName());

    private ScheduledExecutorService executor;
    private Webcam webcam;

    @PostConstruct
    public void init() {
        webcam = Webcam.getDefault();
        webcam.open();
        executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(this::captureAndSendImage, 0, 18, TimeUnit.SECONDS);
    }

    byte[] captureAndSendImage() {
        byte[] imageBytes = new byte[0];
        try {
            BufferedImage image = webcam.getImage();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "PNG", outputStream);
            imageBytes = outputStream.toByteArray();
            System.out.println("Image captured");
        } catch (IOException e) {
            LOGGER.warning("Error capturing image: " + e.getMessage());
        }
        return imageBytes;
    }

    @PreDestroy
    public void destroy() {
        if (webcam != null) {
            webcam.close();
        }
        if (executor != null) {
            executor.shutdown();
        }
    }
}
