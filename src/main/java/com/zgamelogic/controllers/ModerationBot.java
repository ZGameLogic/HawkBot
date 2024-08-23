package com.zgamelogic.controllers;

import com.zgamelogic.annotations.DiscordController;
import com.zgamelogic.annotations.DiscordMapping;
import com.zgamelogic.data.configurations.BotConfiguration;
import com.zgamelogic.data.messages.Message;
import com.zgamelogic.data.messages.MessageRepository;
import com.zgamelogic.services.ModerationService;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;

import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@DiscordController
public class ModerationBot {
    private final MessageRepository messageRepository;
    private final ModerationService moderationService;
    private final BotConfiguration botConfiguration;

    private TextChannel bannedWordsChannel;

    public ModerationBot(
            MessageRepository messageRepository,
            ModerationService moderationService,
            BotConfiguration botConfiguration
    ) {
        this.messageRepository = messageRepository;
        this.moderationService = moderationService;
        this.botConfiguration = botConfiguration;
    }

    @DiscordMapping
    private void onReady(ReadyEvent event) {
        JDA bot = event.getJDA();
        bot.getGuildById(botConfiguration.getDiscordGuildId()).getTextChannelById(botConfiguration.getBannedWordsChannelId());
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
            event.getMember().timeoutFor(5, TimeUnit.MINUTES).queue();
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
