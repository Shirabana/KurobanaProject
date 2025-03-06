package com.kurobana.project.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import com.kurobana.project.entity.Vocabulary;
import com.kurobana.project.repository.VocabularyRepository;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import reactor.core.publisher.Mono;

@Component
public class InsertVocabCommand implements SlashCommand {
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private VocabularyRepository vocabRepo;
	
	@Override
	public String getName() {
		return "ivocab";
	}
	
	public InsertVocabCommand() {}
	
//	public InsertVocabCommand(VocabularyRepository vocabRepo) {
//		this.vocabRepo = vocabRepo;
//	}
	
	@Override
	public Mono<Void> handle(ChatInputInteractionEvent event) {
		
		log.info("Insert vocab starting up!");
		
		/**
		 * Obtain the fields to enter into the database.
		 * Kanji: Not required. This field can be empty as not all words have a Kanji equivalent. 
		 * Kana: Required.
		 * Definition: Required. Separated by commas
		 */
		String kanji = "";
		if (!event.getOption("kanji").isEmpty()) {
			kanji = event.getOption("kanji")
				.flatMap(ApplicationCommandInteractionOption::getValue)
				.map(ApplicationCommandInteractionOptionValue::asString)
				.get();
		}
		
		String kana = event.getOption("kana")
			.flatMap(ApplicationCommandInteractionOption::getValue)
			.map(ApplicationCommandInteractionOptionValue::asString)
			.get();
		
		String definition = event.getOption("definition")
			.flatMap(ApplicationCommandInteractionOption::getValue)
			.map(ApplicationCommandInteractionOptionValue::asString)
			.get();

		/**
		 * Kanji is unique so if it is a duplicate, an error will be thrown
		 */
		try {
			vocabRepo.save(new Vocabulary(kanji, kana, definition));
		}
		catch (DataIntegrityViolationException d) {
			
			log.error("Error: " + d);
			
			return event.reply()
				.withEphemeral(true)
				.withContent("Could not add " + kanji + " / " + kana + " to the database as it was a duplicate.");
		}
		
		//Return a reply to the user when done and successful
		return event.reply()
			.withEphemeral(true)
			.withContent("Successfully added " + kanji + " / " + kana + " to the database.");
	}
}
