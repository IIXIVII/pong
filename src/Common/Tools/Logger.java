package Common.Tools;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {

    public enum LogType {
        SUCCESS("\u001B[32m[SUCCESS]\u001B[0m"),   // Vert
        INFO("\u001B[34m[INFO]\u001B[0m"),         // Bleu
        WARNING("\u001B[33m[WARNING]\u001B[0m"),   // Jaune
        ERROR("\u001B[31m[ERROR]\u001B[0m"),       // Rouge
        DEBUG("\u001B[35m[BEBUG]\u001B[0m"),       // Magenta
        UNKNOWN("\u001B[0m[UNKNOWN]\u001B[0m"),;    // Sans couleur

        private final String label;

        LogType(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    public static void log(String msg, LogType type, String headder) {
        String time = FORMATTER.format(LocalDateTime.now());
        String logMessage = String.format(
                "%s\u001B[33m %s \u001B[0m-\u001B[2m %s \u001B[0m- %s",
                type.getLabel(),
                time,
                headder.toUpperCase(),
                msg
        );
        System.out.println(logMessage);
    }

}
