package com.kurobana.project.services;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class KeepOnlineService {
	
	//1 minute
	@Scheduled(fixedRate = 1 * 1000 * 60) 
	public void reportCurrentTime() {
		//Every 5 minutes
		if (System.currentTimeMillis() % 30000 == 0) {
			System.out.println(System.currentTimeMillis());
		}
	}
}
