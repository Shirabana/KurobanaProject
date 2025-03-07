package com.kurobana.project.commands;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import com.kurobana.project.entity.Vocabulary;
import com.kurobana.project.repository.VocabularyRepository;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import reactor.core.publisher.Mono;

@Component
public class RandomVocabCommand implements SlashCommand {
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private VocabularyRepository vocabRepo;
	
	@Override
	public String getName() {
		return "rvocab";
	}
	
	public RandomVocabCommand() {}
	
	@Override
	public Mono<Void> handle(ChatInputInteractionEvent event) {
		
		log.info("Random vocab starting up!");
		
		//Variables to use
		Long count = vocabRepo.count();
		Vocabulary vocab = null;
		String content = "";
		
		//Find a random vocabulary word
		int random = (int)(Math.random() * count);
		Page<Vocabulary> vocabPage = vocabRepo.findAll(PageRequest.of(random, 1));
		if (vocabPage.hasContent()) {
			vocab = vocabPage.getContent().get(0);
			
			//Build content message
			content = "Random vocabulary word:";
			content += "\n-----";
			content += "\n**Kanji:** " + vocab.getKanji() + " \n**Kana:** " + vocab.getKana() + " \n**Definition:** " + vocab.getDefinition();
		}
		else {
			content = "Could not find a random vocabulary word.";
		}
		
		//Return a reply to the user when done and successful
		return event.reply()
			.withEphemeral(true)
			.withContent(content);
	}
}
