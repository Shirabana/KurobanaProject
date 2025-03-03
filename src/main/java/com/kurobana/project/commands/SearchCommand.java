package com.kurobana.project.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import reactor.core.publisher.Mono;

@Component
public class SearchCommand implements SlashCommand {
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Override
	public String getName() {
		return "search";
	}
	
	@Override
	public Mono<Void> handle(ChatInputInteractionEvent event) {
		
		log.info("Search starting up!");
		
		//Obtain the search query
		String query = event.getOption("query")
			.flatMap(ApplicationCommandInteractionOption::getValue)
			.map(ApplicationCommandInteractionOptionValue::asString)
			.get();
		
		//Obtain the self value to toggle ephemeral
		boolean self = false;
		if (!event.getOption("self").isEmpty()) {
			self = event.getOption("self")
				.flatMap(ApplicationCommandInteractionOption::getValue)
				.map(ApplicationCommandInteractionOptionValue::asBoolean)
				.get();
		}
		
		//Replace full-width and half-width spaces with %20
		String searchQuery = query.replaceAll("\u0020", "%20");
		searchQuery = searchQuery.replaceAll("\u3000", "%20");
		
		/**
		 * Option to check if the query will only show up for the user or not.
		 * If left blank, it will show up in the chat for all members.
		 */
		return event.reply()
			.withEphemeral(self)
			.withContent("https://www.google.com/search?q=" + searchQuery);
		
	}
}
