package fr.zelytra;

import net.dv8tion.jda.api.entities.Message;
import org.codehaus.jackson.annotate.JsonIgnore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GameLog {

    private String content;
    private String sender;
    private String player;
    private Date time;
    private LogType type;

    @JsonIgnore
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("[HH:mm:ss]");


    public GameLog(Message message, String type) {
        this.type = new LogType(type);

        Pattern pattern = Pattern.compile("\\[(.*?)\\]");
        Matcher matcher = pattern.matcher(message.getContentDisplay());

        try {
            if (matcher.find()) {
                String timeString = matcher.group(1);
                this.time = dateFormat.parse(timeString);
            }
            if (matcher.find()) {
                this.sender = matcher.group(1);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String[] parts = message.getContentDisplay().split(">>");
        if (parts.length > 1) {
            String[] playerAndContent = parts[1].trim().split("\\s+", 2);
            this.player = playerAndContent[0];
            if (playerAndContent.length > 1) {
                this.content = playerAndContent[1];
            }
        }
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public LogType getType() {
        return type;
    }

    public void LogType(LogType type) {
        this.type = type;
    }

}