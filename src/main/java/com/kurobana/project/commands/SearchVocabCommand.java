package com.kurobana.project.commands;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kurobana.project.entity.Vocabulary;
import com.kurobana.project.repository.VocabularyRepository;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import reactor.core.publisher.Mono;

@Component
public class SearchVocabCommand implements SlashCommand {
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private VocabularyRepository vocabRepo;
	
	@Override
	public String getName() {
		return "svocab";
	}
	
	public SearchVocabCommand() {}
	
//	public InsertVocabCommand(VocabularyRepository vocabRepo) {
//		this.vocabRepo = vocabRepo;
//	}
	
	@Override
	public Mono<Void> handle(ChatInputInteractionEvent event) {
		
		log.info("Search vocab starting up!");
		
		/**
		 * Obtain the fields to search for.
		 * Query should be a number, so it is parsed into an integer.
		 */
		int choice = Integer.parseInt(event.getOption("choice")
			.flatMap(ApplicationCommandInteractionOption::getValue)
			.map(ApplicationCommandInteractionOptionValue::asString)
			.get());
		
		String query = event.getOption("query")
			.flatMap(ApplicationCommandInteractionOption::getValue)
			.map(ApplicationCommandInteractionOptionValue::asString)
			.get();
		
		
		//Search the database
		List<Vocabulary> result = new ArrayList<>();
		switch (choice) {
			case 1:
				result = vocabRepo.findByKanjiContaining(query);
				break;
			case 2:
				result = vocabRepo.findByKanaContaining(query);
				break;
			case 3:
				result = vocabRepo.findByDefinitionContaining(query);
				break;
		}
		
		//Build the text to return in chat
		String content;
		if (!result.isEmpty()) {
			content = "Here are the search results: ";
			for(Vocabulary v : result) {
				content += "\n-----";
				content += "\n**Kanji:** " + v.getKanji() + " \n**Kana:** " + v.getKana() + " \n**Definition:** " + v.getDefinition();
				
				if (content.length() > 500) {
					content += "\nAnd more...";
					break;
				}
			}
		}
		else {
			content = "Nothing found.";
		}
		
		//Return a reply to the user when done and successful
		return event.reply()
			.withEphemeral(true)
			.withContent(content);
	}
}
