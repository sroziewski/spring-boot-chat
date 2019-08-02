package app.util;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * author: Szymon Roziewski on 29.07.19 12:47
 * email: szymon.roziewski@gmail.com
 */
@Data
@NoArgsConstructor
public class Note implements Serializable {
    private String nickname="";
    private String message="";
    private String jmsMessageID;
    private String serialVersionUID;

    public Note(Note note, String serialVersionUID) {
        this.nickname = note.getNickname();
        this.message = note.getMessage();
        this.serialVersionUID = serialVersionUID;
    }
}
