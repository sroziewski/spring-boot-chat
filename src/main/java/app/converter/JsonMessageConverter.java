package app.converter;

import app.util.Note;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.stereotype.Component;

import javax.jms.*;

/**
 * author: Szymon Roziewski on 29.07.19 19:16
 * email: szymon.roziewski@gmail.com
 */

@Component
public class JsonMessageConverter implements MessageConverter {

    @Autowired
    private ObjectMapper mapper;

    /**
     * Converts message to JSON. Used mostly by {@link org.springframework.jms.core.JmsTemplate}
     */
    @Override
    public Message toMessage(Object object, Session session) throws JMSException, MessageConversionException {
        String json;

        try {
            json = mapper.writeValueAsString(object);
        } catch (Exception e) {
            throw new MessageConversionException("Message cannot be parsed. ", e);
        }
        TextMessage message = session.createTextMessage();
        message.setText(json);
        return message;
    }

    /**
     * Extracts JSON payload for further processing by JacksonMapper.
     */
    @Override
    public Object fromMessage(Message message) throws JMSException, MessageConversionException {

        if(message instanceof ActiveMQTextMessage){
            return ((ActiveMQTextMessage) message).getText();
        }
        else {
            Object obj = ((ObjectMessage) message).getObject();
            Note note = (Note) obj;
            note.setJmsMessageID(message.getJMSMessageID());
            return note;
        }
    }
}
