package com.ggolen.goodboyrecognizer;

import org.springframework.stereotype.Service;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.Random;

@Service
public class AudioService {
    private File[] audioFiles;
    private Random random = new Random();

    public AudioService() {
        File audioDir = new File("path/to/audio");
        audioFiles = audioDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".wav"));
    }

    public void playRandomAudio() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        if (audioFiles == null || audioFiles.length == 0) {
            throw new IllegalStateException("No audio files available");
        }

        File audioFile = audioFiles[random.nextInt(audioFiles.length)];

        try (AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile)) {
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        }
    }
}

