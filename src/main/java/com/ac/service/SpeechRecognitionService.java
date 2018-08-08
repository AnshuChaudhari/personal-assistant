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
    private final IntelligentQueryResponder queryResponder;
    private final VoiceOutputService voiceOutputService;

    public SpeechRecognitionService(IntelligentQueryResponder queryResponder, VoiceOutputService voiceOutputService) {
        mic = new Microphone(FLACFileWriter.FLAC);
        duplex = new GSpeechDuplex("AIzaSyBOti4mM-6x9WDnZIjIeyEU21OpBXqWBgw");
        this.queryResponder = queryResponder;
        this.voiceOutputService = voiceOutputService;
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
                String speechRecognitionResponse = googleResponse.getResponse();
                logger.debug("Listening: " + speechRecognitionResponse);
                if (VoiceOutputService.speaking) {
                    logger.debug("Speaking...");
                } else {

                    // if condition to not listen when matrix is speaking .Use a boolean to know when matrix is speaking
                    if (speechRecognitionResponse != null) {
                        speechRecognitionResponse = speechRecognitionResponse.toLowerCase();
                        if (googleResponse.isFinalResponse()) {
                            logger.debug("Final command:" + speechRecognitionResponse);
                            String answerToSpokenQuery = queryResponder.answerQuery(speechRecognitionResponse);
                            voiceOutputService.speak(answerToSpokenQuery);
                        }

                    }
                }
            }
        });
        logger.info("Initialized Duplex");
    }

}
