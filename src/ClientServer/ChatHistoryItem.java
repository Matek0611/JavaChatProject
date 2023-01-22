package ClientServer;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

public class ChatHistoryItem {
    private final Instant sentDate;
    private String userName;
    private String data;

    public ChatHistoryItem(String userName, String data) {
        this.userName = userName;
        this.data = data;
        this.sentDate = Instant.now();
    }

    public ChatHistoryItem(Instant sentDate, String userName, String data) {
        this.sentDate = sentDate;
        this.userName = userName;
        this.data = data;
    }

    public Instant getSentDate() {
        return sentDate;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        if (data != null && data.startsWith("@U:")) {
            return data;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT )
                        .withLocale(Locale.of("pl", "PL"))
                        .withZone(ZoneId.systemDefault());

        return "[" + formatter.format(sentDate) + "] @" + userName + ": " + data;
    }
}
