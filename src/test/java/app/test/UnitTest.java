package app.test;

import app.service.ChatBot;
import app.service.MessageSender;
import app.service.MessageService;
import app.util.AdviceApiMessage;
import app.util.Note;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import static app.util.CommonUtils.getStrongUniqueId;
import static app.util.QueueName.NAME;
import static org.junit.Assert.*;

/**
 * author: Szymon Roziewski on 30.07.19 13:34
 * email: szymon.roziewski@gmail.com
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Slf4j
public class UnitTest {
    @Autowired
    private ChatBot chatBot;
    @Autowired
    private MessageService messageService;

    @Test
    public void process(){
        Note note = new Note();
        note.setMessage("ui");
        note.setSerialVersionUID(getStrongUniqueId());
        chatBot.setMessageService(messageService);
        chatBot.process(note);
        List<String> advices = messageService.getAdvices().get(note.getSerialVersionUID());
        assertEquals(6, advices.size());
        assertEquals("It's always the quiet ones.", advices.get(3));
        log.info("process test OK");
    }

    @Test
    public void adviceApiMessage() throws IOException {
        String json = "{\"total_results\": \"3\", \"query\": \"\", \"slips\": [{\"advice\":\"If you think your headphones are dying, check the socket for fluff with a straightened paperclip.\",\"slip_id\":\"121\"},{\"advice\":\"Life can be a lot more interesting inside your head.\",\"slip_id\":\"158\"},{\"advice\":\"When you're looking up at birds flying overhead, keep your mouth closed.\",\"slip_id\":\"28\"}]}";
        Optional<AdviceApiMessage> result = AdviceApiMessage.getAdviceMessage(json);
        assertFalse(result.isEmpty());
        assertEquals(3, result.get().getAdvices().size());
        json = "{\"type\": \"notice\", \"text\": \"No advice slips found matching that search term.\"}";
        result = AdviceApiMessage.getAdviceMessage(json);
        assertTrue(result.isEmpty());
        AdviceApiMessage temporary = new ObjectMapper().readValue(json, AdviceApiMessage.class);
        assertEquals("No advice slips found matching that search term.", temporary.getText());
    }

/*    @Test
    public void testReceive() throws Exception {
        String correlationId = messageSender.sendOrder(getStrongUniqueId());
        String status = messageSender.receiveOrderStatus(correlationId);
        assertEquals("Accepted", status);
    }*/

}
