package app.util;

import org.apache.commons.codec.digest.DigestUtils;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * author: Szymon Roziewski on 30.07.19 13:03
 * email: szymon.roziewski@gmail.com
 */
public class CommonUtils {
    public static String stackTraceAsString(Exception e) {
        return List.of(e.getStackTrace()).stream().map(StackTraceElement::toString).collect(Collectors.joining("\n"));
    }

    public static String getRandomWord(String words){
        Random rand = new Random();
        List<String> list = List.of(words.split("\\s+"));
        return list.get(rand.nextInt(list.size()));
    }

    public static String getStrongUniqueId(){
        String id = String.format("%s%s%s", UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString());
        return DigestUtils.sha256Hex(id);
    }
}
