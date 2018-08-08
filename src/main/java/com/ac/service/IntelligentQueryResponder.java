package com.ac.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;

@Service
public class IntelligentQueryResponder {

    private static final Logger logger = LoggerFactory.getLogger(IntelligentQueryResponder.class);
    private RestTemplate restTemplate;
    private VoiceOutputService voiceOutputService;

    public IntelligentQueryResponder(final VoiceOutputService voiceOutputService) {
        this.voiceOutputService = voiceOutputService;
        this.restTemplate = new RestTemplateBuilder().errorHandler(new ResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse clientHttpResponse) throws IOException {
                return (
                        clientHttpResponse.getStatusCode().series() == HttpStatus.Series.CLIENT_ERROR
                                || clientHttpResponse.getStatusCode().series() == HttpStatus.Series.SERVER_ERROR);
            }

            @Override
            public void handleError(ClientHttpResponse clientHttpResponse) throws IOException {
                if (clientHttpResponse.getStatusCode().series() == HttpStatus.Series.SERVER_ERROR) {
                    if (clientHttpResponse.getStatusCode() == HttpStatus.NOT_IMPLEMENTED) {
                        voiceOutputService.speak("Sorry I am not able to answer your query");
                    }
                }
            }
        }).build();
    }

    public String answerQuery(final String query) {
        String response = null;
        try {
            StringBuilder querybuilder = new StringBuilder();
            querybuilder.append("https://api.wolframalpha.com/v1/spoken?appid=WPUUX7-R4LHPA8RKL&i=");
            querybuilder.append(query);
          URI uri = URI.create(querybuilder.toString().replace(" ", "+"));
            response = restTemplate.getForObject(uri, String.class);
        } catch (RestClientException e) {
            logger.error(e.getMessage());
        }
        return response;
    }

}
