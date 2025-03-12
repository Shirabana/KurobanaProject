package com.kurobana.project.registrar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import discord4j.common.JacksonResources;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.rest.RestClient;
import discord4j.rest.service.ApplicationService;

@Component
public class GlobalCommandRegistrar implements ApplicationRunner {
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	private final RestClient client;
	
	@Value("${guilds}")
	private List<String> guildIds;
	
	//From the Bean
	public GlobalCommandRegistrar(RestClient client) {
		this.client = client;
	}
	
	//Ran once each start up
	@Override
	public void run(ApplicationArguments args) throws IOException {
		
		log.info("Starting up!");
		
		final JacksonResources d4jMapper = JacksonResources.create();
		
		PathMatchingResourcePatternResolver matcher = new PathMatchingResourcePatternResolver();
		final ApplicationService applicationService = client.getApplicationService();
		final long applicationId = client.getApplicationId().block();
        
		//Get commands json from the resources folder and iterate through each guild to add them
		for (String guildId : guildIds) {
			List<ApplicationCommandRequest> commands = new ArrayList<>();
			for (Resource resource : matcher.getResources("commands/*.json")) {
				ApplicationCommandRequest request = d4jMapper.getObjectMapper()
					.readValue(resource.getInputStream(), ApplicationCommandRequest.class);
	        	
				commands.add(request);
	        	
				applicationService.createGuildApplicationCommand(applicationId, Long.valueOf(guildId), request)
	        		.doOnNext(ignore -> log.debug("Successfully registered Guild Command"))
	        		.doOnError(e -> log.error("Failed to register Guild Command - " + e ))
	        		.subscribe();
	        
			}
		}
	}
}
