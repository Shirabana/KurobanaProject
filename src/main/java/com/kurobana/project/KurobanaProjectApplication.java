package com.kurobana.project;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.presence.ClientActivity;
import discord4j.core.object.presence.ClientPresence;
import discord4j.rest.RestClient;

@SpringBootApplication
@EnableScheduling
public class KurobanaProjectApplication {

	@Value("${token}")
	String token;
	
	public static void main(String[] args) {
		new SpringApplicationBuilder(KurobanaProjectApplication.class)
			.build()
			.run(args);
	}

	@Bean
	public GatewayDiscordClient gatewayDiscordClient() {
		return DiscordClientBuilder.create(token)
				.build()
				.gateway()
				.setInitialPresence(ignore -> ClientPresence.online(ClientActivity.listening("Testing stuff!")))
				.login()
				.block();
	}
	
	@Bean
	public RestClient discordRestClient(GatewayDiscordClient client) {
		return client.getRestClient();
	}
}
