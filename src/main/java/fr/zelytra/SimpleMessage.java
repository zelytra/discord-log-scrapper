package fr.zelytra;

import net.dv8tion.jda.api.entities.Message;

public class SimpleMessage {
    public String id;
    public String content;
    public String author;

    public SimpleMessage(Message message) {
        this.id = message.getId();
        this.content = message.getContentDisplay();
        this.author = message.getAuthor().getName();
    }
}
