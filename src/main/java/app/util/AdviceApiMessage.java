package app.util;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * author: Szymon Roziewski on 29.07.19 22:34
 * email: szymon.roziewski@gmail.com
 */

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@RequiredArgsConstructor()
public class AdviceApiMessage implements Serializable {
    private String type;
    private String text;
    private List<String> advices;

    private AdviceApiMessage(List<String> advices) {
        this.advices = advices;
    }

    @JsonProperty("message")
    private void unpackNested(Map<String,Object> message) {
        this.type = String.valueOf(message.get("type"));
        this.text = String.valueOf(message.get("text"));
    }

    public static Optional<AdviceApiMessage> getAdviceMessage(String json) throws IOException {
        AdviceApiMessage adviceApiMessage = null;
        ObjectMapper mapper = new ObjectMapper();
        AdviceApiMessage temporary = mapper.readValue(json, AdviceApiMessage.class);
        if(temporary.isEmpty()){
            String advicesString = json.split("\"slips\":")[1]
                .substring(0, json.split("\"slips\":")[1].length()-1);
            List<Map<String, String>> data = mapper.readValue(advicesString, new TypeReference<List<Map<String, String>>>(){});
            List<String> advicesTemp = data.stream().map(el -> el.get("advice")).collect(Collectors.toList());
            adviceApiMessage = new AdviceApiMessage(advicesTemp);
        }
        return Optional.ofNullable(adviceApiMessage);
    }

    private boolean isEmpty(){
        return this.type == null;
    }

}
