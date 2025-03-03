package com.kurobana.project.listeners;

import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Component;

import com.kurobana.project.commands.SlashCommand;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class SlashCommandListener {
	private final Collection<SlashCommand> commands;

    public SlashCommandListener(List<SlashCommand> slashCommands, GatewayDiscordClient client) {
        commands = slashCommands;

        client.on(ChatInputInteractionEvent.class, this::handle).subscribe();
    }


    public Mono<Void> handle(ChatInputInteractionEvent event) {
        /**
         * 1. Convert the list to a flux so it can be iterated through
         * 2. Filter the commands and get the matching command
         * 3. Let the command class handle the logic
         */
    	return Flux.fromIterable(commands)
            .filter(command -> command.getName().equals(event.getCommandName()))
            .next()
            .flatMap(command -> command.handle(event));
    }
}
