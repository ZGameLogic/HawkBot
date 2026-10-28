package com.zgamelogic.monitoring

import com.zgamelogic.discord.annotations.DiscordController
import com.zgamelogic.discord.annotations.mappings.SlashCommandMapping
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData
import org.springframework.context.annotation.Bean
import java.awt.Color
import java.util.concurrent.CompletableFuture

@DiscordController
class Monitoring(private val monitoringService: MonitoringService) {

    @SlashCommandMapping(id = "status")
    fun monitoringCommand(event: SlashCommandInteractionEvent) {
        event.deferReply().queue()
        val servers = mapOf(
            "Modded Minecraft" to ("mod.itshawksnest.stream" to 25568),
            "Vanilla Minecraft" to ("vanilla.itshawksnest.stream" to 25565),
            "Enshrouded" to ("192.168.1.101" to 15636),
            "Windrose" to ("192.168.1.102" to 27015)
        )

        val checks = servers.mapValues { (_, endpoint) ->
            val (host, port) = endpoint
            monitoringService.isOnline(host, port)
        }

        CompletableFuture.allOf(*checks.values.toTypedArray()).whenComplete { _, error ->
            if (error != null) {
                event.hook.editOriginal("Unable to retrieve server statuses. Please try again later.").queue()
                return@whenComplete
            }

            val status = checks.map { (name, result) ->
                val indicator = if (result.join()) "🟢 Online" else "🔴 Offline"
                "$indicator — **$name**"
            }.joinToString("\n")

            val allGood = checks.values.all { it.join() }

            val eb = EmbedBuilder()
            eb.setTitle("Server Status")
            eb.setDescription(status)
            eb.setColor(if(allGood) Color.GREEN else Color.RED)

            event.hook.editOriginalEmbeds(eb.build()).queue()
        }
    }

    @Bean
    fun command(): SlashCommandData {
        return Commands.slash("status", "Gets the status of all the servers I run in the nest")
    }
}