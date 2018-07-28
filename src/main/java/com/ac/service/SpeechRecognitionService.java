package com.ac.service;

import com.darkprograms.speech.microphone.Microphone;
import com.darkprograms.speech.recognizer.GSpeechDuplex;
import com.darkprograms.speech.recognizer.GSpeechResponseListener;
import com.darkprograms.speech.recognizer.GoogleResponse;
import net.sourceforge.javaflacencoder.FLACFileWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.sound.sampled.LineUnavailableException;

@Service
public class SpeechRecognitionService {

    private static final Logger logger = LoggerFactory.getLogger(SpeechRecognitionService.class);
    private final Microphone mic;
    private final GSpeechDuplex duplex;

    public SpeechRecognitionService() {
        mic = new Microphone(FLACFileWriter.FLAC);
        duplex = new GSpeechDuplex("AIzaSyBOti4mM-6x9WDnZIjIeyEU21OpBXqWBgw");
        initializeDuplex();
    }

    @Async
    public void startSpeechRecognition() {
        try {
            duplex.recognize(mic.getTargetDataLine(), mic.getAudioFormat());
        } catch (LineUnavailableException e) {
            logger.error(e.getMessage());
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
        }
    }

    public void stopSpeechRecognition() {
        mic.close();
        duplex.stopSpeechRecognition();
    }



    private void initializeDuplex() {
        duplex.setLanguage("en");
        duplex.addResponseListener(new GSpeechResponseListener() {

            public void onResponse(GoogleResponse googleResponse) {
                 //Get the response from Google Cloud
                String output = googleResponse.getResponse();
                logger.debug("Listening: " + output);
                if (VoiceOutputService.speaking) {
                    logger.debug("Speaking...");
                } else {

                    // if condition to not listen when matrix is speaking .Use a boolean to know when matrix is speaking
                    if (output != null) {
                        output = output.toLowerCase();
                        if (googleResponse.isFinalResponse()) {
                            logger.debug("Final command:" + output);
                            //makeDecision(output);
                        }

                    }
                }
            }
        });
        logger.info("Initialized Duplex");
    }

}
