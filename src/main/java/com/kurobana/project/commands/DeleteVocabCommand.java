package com.kurobana.project.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kurobana.project.repository.VocabularyRepository;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import reactor.core.publisher.Mono;

@Component
public class DeleteVocabCommand implements SlashCommand {
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private VocabularyRepository vocabRepo;
	
	@Override
	public String getName() {
		return "dvocab";
	}
	
	@Override
	public Mono<Void> handle(ChatInputInteractionEvent event) {
		
		log.info("Delete vocab starting up!");
		
		/**
		 * Obtain the id to delete.
		 * Query should be a long number, so it is parsed into an Long.
		 */
		Long deleteId = event.getOption("id")
			.flatMap(ApplicationCommandInteractionOption::getValue)
			.map(ApplicationCommandInteractionOptionValue::asLong)
			.get();
		
		//Delete the item using the id provided and obtain it if possible
		vocabRepo.deleteById(deleteId);
		
		//Return a reply to the user when done and successful
		return event.reply()
			.withEphemeral(true)
			.withContent("Item deleted with ID: " + deleteId);
	}
}
