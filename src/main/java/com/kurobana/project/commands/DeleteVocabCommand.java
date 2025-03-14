package com.kurobana.project.commands;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kurobana.project.entity.Vocabulary;
import com.kurobana.project.repository.VocabularyRepository;

import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.GuildMessageChannel;
import discord4j.core.spec.MessageCreateSpec;
import reactor.core.publisher.Mono;

@Component
public class DeleteVocabCommand implements SlashCommand {
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	private Long deleteId;
	private Optional<Vocabulary> v;
	
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
		deleteId = event.getOption("id")
			.flatMap(ApplicationCommandInteractionOption::getValue)
			.map(ApplicationCommandInteractionOptionValue::asLong)
			.get();
		
		//Defer event.reply() so it doesn't time out while doing everything
		event.deferReply().withEphemeral(true).block();
		
		//Get the messageId to filter for it later on
		Optional<Snowflake> messageId = event.getInteraction().getMessageId();
		
		//Before doing anything, try to find it to store it in a variable for viewing
		v = vocabRepo.findById(deleteId);
		if (v.isEmpty()) {
			return event.reply()
				.withEphemeral(true)
				.withContent("Item with ID: " + deleteId + " was not found.");
		}
		
		//Preparation for confirmation
		GatewayDiscordClient client = event.getClient();
		Button confirmButton = Button.primary("confirm", "Confirm");
		Button denyButton = Button.danger("deny", "Deny");
		List<Button> buttonList = new ArrayList<>();
		buttonList.add(confirmButton);
		buttonList.add(denyButton);
		
		//Send a message with buttons
		return client.getChannelById(event.getInteraction().getChannelId())
			.ofType(GuildMessageChannel.class)
			.flatMap(e -> { 
				
			//Build main message asking for confirmation
			Mono<Message> mainReply = e.createMessage(MessageCreateSpec.builder()
				.addComponent(ActionRow.of(buttonList))
				.content("Are you sure you want to delete ID: " + deleteId + "?")
				.build());
			
			//Create listener for the buttons
			Mono<Void> tempListener = client.on(ButtonInteractionEvent.class, bEvent -> {
				log.info("Temp listener confirmation starting up!");
				
				//Defer interactions so Discord doesn't immediately time them out
				bEvent.deferEdit().withEphemeral(true).block();
				
				if (bEvent.getCustomId().equals("confirm")) {
					
					//Delete the message with buttons
					bEvent.deleteFollowup(bEvent.getMessageId()).subscribe();
					
					//Build the content for the delete item message
					String content = "Item deleted: "
							+ "\n-----"
							+ "\n**ID:** " + v.get().getId() 
							+ "\n**Kanji:** " + v.get().getKanji() 
							+ "\n**Kana:** " + v.get().getKana()
							+ "\n**Definition:** " + v.get().getDefinition();
					
					//Delete the item
					vocabRepo.deleteById(deleteId);
					
					//Finally, send the message back
					return event.editReply(content)
						.filter(message -> message.getMessageReference().equals(messageId))
						.then();
					
				}
				else if (bEvent.getCustomId().equals("deny")) {
					
					//Delete the message with buttons
					bEvent.deleteFollowup(bEvent.getMessageId()).subscribe();
					
					//Finally, send the message back
					return event.editReply("Nothing was deleted.")
						.filter(message -> message.getMessageReference().equals(messageId))
						.then();
				}
				else {
					return Mono.empty(); 
				}
			}).timeout(Duration.ofMinutes(1))
			.onErrorResume(TimeoutException.class, ignore -> Mono.empty())
			.then();
			
			return mainReply.then(tempListener);
		});
	}
}
