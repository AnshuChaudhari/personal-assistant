package com.ac;

import com.ac.service.SpeechRecognitionService;
import com.ac.service.VoiceOutputService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;

@Service
public class PersonalAssistantRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(PersonalAssistantRunner.class);
    private SpeechRecognitionService speechRecognitionService;
    private VoiceOutputService voiceOutputService;

    public PersonalAssistantRunner(SpeechRecognitionService speechRecognitionService, VoiceOutputService voiceOutputService) {
        this.speechRecognitionService = speechRecognitionService;
        this.voiceOutputService = voiceOutputService;
    }

    @Override
    public void run(String... strings) throws Exception {
        welcomeUser();
        initializeUiWindow();
        logger.info("Started speech recognition service");
    }

    public void welcomeUser() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);

        logger.debug("H:" + hour + ", M:" + min);
        if (hour >= 8 && hour <= 12) {
            voiceOutputService.speak("Good Morning. Have a pleasant morning");
        }
        if (hour >= 13 && hour <= 17) {
            voiceOutputService.speak("Good Afternoon!");
        }
        if (hour >= 17 && hour <= 24) {
            voiceOutputService.speak("Good Evening!");
        }
        if (hour >= 24 && min < 60) {
            voiceOutputService.speak("Hello!");
        }
    }

    private void initializeUiWindow() {
        JFrame frame = new JFrame("Jarvis Speech API DEMO");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JTextArea response = new JTextArea();
        response.setEditable(false);
        response.setWrapStyleWord(true);
        response.setLineWrap(true);

        final JButton listen = new JButton("Listen");
        final JButton stop = new JButton("Stop");
        stop.setEnabled(false);

        listen.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                speechRecognitionService.startSpeechRecognition();
                listen.setEnabled(false);
                stop.setEnabled(true);
            }
        });
        stop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                speechRecognitionService.stopSpeechRecognition();
                listen.setEnabled(true);
                stop.setEnabled(false);
            }
        });
        JLabel infoText = new JLabel(
                "<html>" +
                        "<div style=\"text-align: center;\">" +
                        "   Hit listen and watch your voice be translated into text.\n" +
                        "</html>",

                0);
        frame.getContentPane().add(infoText);
        infoText.setAlignmentX(0.5F);
        JScrollPane scroll = new JScrollPane(response);
        frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), 1));
        frame.getContentPane().add(scroll);
        JPanel recordBar = new JPanel();
        frame.getContentPane().add(recordBar);
        recordBar.setLayout(new BoxLayout(recordBar, 0));
        recordBar.add(listen);
        recordBar.add(stop);
        frame.setVisible(true);
        frame.pack();
        frame.setSize(500, 500);
        frame.setLocationRelativeTo(null);
    }
}
