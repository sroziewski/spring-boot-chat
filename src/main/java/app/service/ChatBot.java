package app.service;

import app.util.AdviceApiMessage;
import app.util.Note;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static app.util.CommonUtils.getRandomWord;
import static app.util.CommonUtils.stackTraceAsString;
import static org.springframework.http.HttpHeaders.USER_AGENT;

/**
 * author: Szymon Roziewski on 30.07.19 12:23
 * email: szymon.roziewski@gmail.com
 */

@Service
@Slf4j
@Data
public class ChatBot {

    private MessageService messageService;

    @Autowired
    public ChatBot(MessageService messageService) {
        this.messageService = messageService;
    }

    public void process(Note note){
        try {
            String url = String.format("https://api.adviceslip.com/advice/search/%s", getRandomWord(note.getMessage()));
            Optional<AdviceApiMessage> response = sendGet(url);
            if(response.isPresent()){
                log.info(String.format("Received %d advices", response.get().getAdvices().size()));
                messageService.getAdvices().put(note.getSerialVersionUID(), response.get().getAdvices());
                log.info("Found advices: {}", String.join("|", messageService.getAdvices().get(note.getSerialVersionUID())));
            }
            else{
                log.info("Advices not found");
                messageService.getAdvices().put(note.getSerialVersionUID(), new ArrayList<>());
            }
        } catch (Exception e) {
            log.error(stackTraceAsString(e));
        }
    }

    private Optional<AdviceApiMessage> sendGet(String url) throws Exception {

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");

        //add request header
        con.setRequestProperty("User-Agent", USER_AGENT);

        int responseCode = con.getResponseCode();
        log.info("\nSending 'GET' request to URL : " + url);
        log.info("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return AdviceApiMessage.getAdviceMessage(response.toString());
    }

}
