package fr.zelytra;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Hello world!
 */
public class Main extends ListenerAdapter {
    private static String token = "MTEyNDI0NzgyNzQ2MDEzMjkzNA.GTIO7N.SwMtzQ48l1oJDb2O8klXjlvT0BOFI1f6GVJ6uY";

    public static void main(String[] args) {
        JDA jda = JDABuilder.createLight(token, Collections.emptyList())
                .addEventListeners(new Main())
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .setActivity(Activity.playing("Type /test"))
                .build();


        jda.updateCommands().addCommands(
                Commands.slash("test", "At your own risk !").addOption(OptionType.STRING, "log-type", "The type to bind channel's message", true)
        ).queue();


    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        // make sure we handle the right command
        System.out.println("command detected");
        if (event.getName().equals("test")) {
            event.deferReply().queue();

            // get the message channel
            MessageChannel channel = event.getChannel();

            // create a MessageHistory object
            MessageHistory history = new MessageHistory(channel);

            // create a list to store the messages
            List<Message> mess = new ArrayList<>();

            // loop to retrieve messages
            boolean noMoreMessages = false;
            while (!noMoreMessages) {
                try {
                    List<Message> messages = history.retrievePast(100).complete();
                    if (messages.isEmpty()) {
                        noMoreMessages = true;
                    } else {
                        mess.addAll(messages);
                        System.out.println("Fetched " + mess.size() + " messages so far");
                    }
                } catch (Exception e) {
                    // error handling
                    e.printStackTrace();
                    noMoreMessages = true;
                }
            }

            // write the messages to a file
            String fileName = "logs-" + event.getOptionsByName("log-type") + ".txt";
            try (PrintWriter writer = new PrintWriter(new FileWriter(fileName, false))) {

                ObjectMapper mapper = new ObjectMapper();
                List<GameLog> logs = new ArrayList<>();
                mess.forEach(x -> logs.add(new GameLog(x, event.getOptionsByName("log-type").toString())));

                try {
                    String json = mapper.writeValueAsString(logs);
                    writer.println(json);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } catch (IOException e) {
                // error handling
                e.printStackTrace();
                System.out.println("Error writing to file");
            }

            System.out.println("Finished fetching messages. Total fetched: " + mess.size());

            // respond with the file
            File file = new File(fileName);
            event.getHook().sendMessage("Here are the messages").addFiles(FileUpload.fromData(file)).queue();
        }
    }


}
