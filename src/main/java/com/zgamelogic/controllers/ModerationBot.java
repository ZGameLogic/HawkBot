package com.zgamelogic.controllers;

import com.zgamelogic.annotations.DiscordController;
import com.zgamelogic.annotations.DiscordMapping;
import com.zgamelogic.data.messages.Message;
import com.zgamelogic.data.messages.MessageRepository;
import com.zgamelogic.services.ModerationService;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;

import java.util.Date;
import java.util.Optional;

@DiscordController
public class ModerationBot {
    private final MessageRepository messageRepository;
    private final ModerationService moderationService;

    public ModerationBot(MessageRepository messageRepository, ModerationService moderationService) {
        this.messageRepository = messageRepository;
        this.moderationService = moderationService;
    }

    @DiscordMapping
    private void onMessage(MessageReceivedEvent event) {
        Optional<Message> previousMessage = messageRepository.findTopByUserIdOrderByDateDesc(event.getAuthor().getIdLong());
        if(previousMessage.isPresent() && previousMessage.get().getMessage().equals(event.getMessage().getContentDisplay())) {
            spamDetected(event);
            return;
        }
        if(event.getAuthor().isBot()) return;
        messageRepository.save(new Message(
            event.getMessageIdLong(),
            new Date(),
            event.getAuthor().getIdLong(),
            event.getChannel().getIdLong(),
            event.getMessage().getContentDisplay()
        ));
        if(moderationService.isBannedWordIncluded(event.getMessage().getContentDisplay())) {
            // TODO banned word
        }
    }

    @DiscordMapping
    private void onEditMessage(MessageUpdateEvent event) {
        if(event.getAuthor().isBot()) return;
        // TODO post the original message to a guild channel and a new message and pm it
    }

    @DiscordMapping
    private void onDeleteMessage(MessageDeleteEvent event){
        // TODO post the message to a guild channel and a PM
    }

    private void spamDetected(MessageReceivedEvent event){
        // TODO delete both messages
        // TODO PM hawk
        // TODO post in spam channel
        // TODO timeout user
    }
}
