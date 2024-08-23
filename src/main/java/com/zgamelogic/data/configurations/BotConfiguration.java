package com.zgamelogic.data.configurations;

import lombok.Getter;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

@Getter
@Configuration
public class BotConfiguration {
    private final long discordGuildId;
    private final long bannedWordsChannelId;

    public BotConfiguration(
            @Value("${discord.guild-id}") long discordGuildId,
            @Value("${discord.banned-words-channel-id}") long bannedWordsChannelId
    ) {
        this.discordGuildId = discordGuildId;
        this.bannedWordsChannelId = bannedWordsChannelId;
    }
}
