package app.service;

import app.util.Note;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.JmsHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.jms.ConnectionFactory;
import javax.jms.TextMessage;
import java.util.Objects;

import static app.util.QueueName.NAME;

/**
 * author: Szymon Roziewski on 29.07.19 13:06
 * email: szymon.roziewski@gmail.com
 */
@Service
@Slf4j
public class MessageReceiver {
    private final ConnectionFactory connectionFactory;
    private JmsTemplate jmsTemplate;

    private final ChatBot chatBot;

    @Autowired
    public MessageReceiver(ConnectionFactory connectionFactory, ChatBot chatBot) {
        this.connectionFactory = connectionFactory;
        this.chatBot = chatBot;
    }

    @PostConstruct
    public void init() {
        this.jmsTemplate = new JmsTemplate(connectionFactory);
    }

    @JmsListener(destination = NAME, containerFactory = "myFactory")
    public void receiveMessage(Note note) {
        String nickname = Objects.requireNonNull(note).getNickname();
        String text = Objects.requireNonNull(note).getMessage();
        log.info(String.format("Received message: %s", note.getJmsMessageID()));
        log.info(String.format("Received message content: (%s, %s) with id: %s", nickname, text, note.getSerialVersionUID()));
        chatBot.process(note);
    }

/*    @JmsListener(destination = NAME, containerFactory = "myFactory")
    public void receiveOrder(String orderNumber,
                             @Header(JmsHeaders.MESSAGE_ID) String messageId) {
        log.info("received OrderNumber='{}' with MessageId='{}'",
                orderNumber, messageId);

        log.info("sending Status='Accepted' with CorrelationId='{}'",
                messageId);

        jmsTemplate.send(NAME, messageCreator -> {
            TextMessage message = messageCreator.createTextMessage(orderNumber);
            message.setJMSCorrelationID(messageId);
            return message;
        });
    }*/
}
