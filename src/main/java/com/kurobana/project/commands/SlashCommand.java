package com.kurobana.project.commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import reactor.core.publisher.Mono;

/**
 * Basic interface for slash commands
 */
public interface SlashCommand {
	
	//To get the name of the command
	String getName();
	
	//To handle the logic of each command
	Mono<Void> handle(ChatInputInteractionEvent event);
	
}
