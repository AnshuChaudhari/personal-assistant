package com.ac.service;

import com.darkprograms.speech.synthesiser.SynthesiserV2;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Random;

@Service
public class VoiceOutputService {

    private static final Logger logger = LoggerFactory.getLogger(VoiceOutputService.class);
    volatile static boolean speaking = false;
    private SynthesiserV2 synthesizer;
    private Random random;

    public VoiceOutputService() {
        this.synthesizer = new SynthesiserV2("AIzaSyBOti4mM-6x9WDnZIjIeyEU21OpBXqWBgw");
        this.random = new Random();
    }

    @Async
    public void speak(final String text) {
        if (VoiceOutputService.speaking) {
            try {
                int n = random.nextInt(20) + 10;
                Thread.sleep(1000 * n);
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
        try {
            VoiceOutputService.speaking = true;
            synthesizer.setPitch(8.00);
            // Create a JLayer instance
            AdvancedPlayer player = new AdvancedPlayer(synthesizer.getMP3Data(text));
            logger.debug("Speaking");
            player.play();
            VoiceOutputService.speaking = false;

        } catch (IOException | JavaLayerException e) {
            logger.error(e.getMessage());
        }
    }



}
