package com.airbnb.airhack.airprime.controller;

import java.time.LocalDateTime;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.airbnb.airhack.airprime.model.Batch;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value = "/api/hooks")
@CrossOrigin(origins = "*", methods = { RequestMethod.GET, RequestMethod.OPTIONS, RequestMethod.POST })
public class TaskController {

	@PostMapping(value = "/incomingTasks")
	public Batch incomingTasks(@RequestBody Batch batch) {
		log.info("------------------ " + LocalDateTime.now());
		log.info("------------------ Inside incoming Tasks POST");
		log.info("------------------ Batch : " + batch);

		return batch;
	}

	@GetMapping("/ping")
	public String ping() {
		log.info("------------------ " + LocalDateTime.now());
		log.info("------------------ Inside ping");
		
		return "ping OK";
	}
}
