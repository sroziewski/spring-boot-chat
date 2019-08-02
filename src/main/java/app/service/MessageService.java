package app.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import static app.util.CommonUtils.stackTraceAsString;

/**
 * author: Szymon Roziewski on 29.07.19 12:33
 * email: szymon.roziewski@gmail.com
 */
@Scope
@Service
@Data
@Slf4j
public class MessageService {
    private ConcurrentLinkedQueue<Map.Entry<String, String>> submittedValues = new ConcurrentLinkedQueue<>();
    private Map<String, List<String>> advices = new ConcurrentHashMap<>();

    @Autowired
    public MessageService() {
    }

    public void waitForAdvices(String uniqueId) {
        while (!this.getAdvices().containsKey(uniqueId)){
            try {
                Thread.sleep(1000);
                log.warn("waitForAdvices is sleeping... advices size: {}", advices.size());
            } catch (InterruptedException e) {
                log.error(stackTraceAsString(e));
                e.printStackTrace();
            }
        }
    }
}
