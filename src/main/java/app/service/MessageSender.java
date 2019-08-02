package app.service;

import app.util.Note;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import static app.util.QueueName.NAME;

/**
 * author: Szymon Roziewski on 29.07.19 13:04
 * email: szymon.roziewski@gmail.com
 */
@Service
@Slf4j
@Data
public class MessageSender {
    private final ConnectionFactory connectionFactory;
    private JmsTemplate jmsTemplate;

    @Autowired
    public MessageSender(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @PostConstruct
    public void init() {
        this.jmsTemplate = new JmsTemplate(connectionFactory);
    }

    public Note sendMessage(String queueName, Note note) {
        String nickname = Objects.requireNonNull(note).getNickname();
        String text = Objects.requireNonNull(note).getMessage();
        log.info(String.format("sending message: (%s, %s) with id: %s", nickname, text, note.getSerialVersionUID()));
        jmsTemplate.send(queueName, session -> session.createObjectMessage(note));
        return note;
    }

/*    public String receiveOrderStatus(String correlationId) {
        String status = (String) jmsTemplate.receiveSelectedAndConvert(NAME, String.format("JMSCorrelationID=%s", correlationId));
        log.info("receive Status='{}' for CorrelationId='{}'", status, correlationId);
        return status;
    }

   public String sendOrder(String orderNumber) throws JMSException {
        final AtomicReference<Message> message = new AtomicReference<>();

        jmsTemplate.convertAndSend(NAME, orderNumber, messagePostProcessor -> {
            message.set(messagePostProcessor);
            return messagePostProcessor;
        });

        String messageId = message.get().getJMSMessageID();
        log.info("sending OrderNumber='{}' with MessageId='{}'",
                orderNumber, messageId);
        return messageId;
    }*/

}
