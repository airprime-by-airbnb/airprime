package com.airbnb.airhack.airprime.controller;

import java.net.URISyntaxException;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.airbnb.airhack.airprime.model.Batch;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value = "/api/hooks")
@CrossOrigin(origins = "*", methods = { RequestMethod.GET, RequestMethod.OPTIONS, RequestMethod.POST })
public class TaskController {

	@Autowired
	private RestTemplate restTemplate;

	@PostMapping(value = "/incomingTasks")
	public Batch incomingTasks(@RequestBody Batch batch) throws RestClientException, URISyntaxException {
		log.info("------------------ " + LocalDateTime.now());
		log.info("------------------ Inside incoming Tasks POST");
		log.info("------------------ Batch : " + batch);

		Batch processedBatch = batch;
		log.info("------------------ " + LocalDateTime.now());
		log.info("------------------ Processing");

		// TODO processing

		log.info("------------------ " + LocalDateTime.now());
		log.info("------------------ End of processing");

		log.info("------------------ " + LocalDateTime.now());
		log.info("------------------ Send request to /api/submitTasks");

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Bearer " + "3IgAUjz5f3Rntu1WefSt6vTXZ5C7dgEi3cU75t4L2hJSCNLIkaXsDid4gdWl");

		HttpEntity<Batch> entity = new HttpEntity<Batch>(batch, headers);
		String result = restTemplate.postForObject("http://airhack-api.herokuapp.com/api/submitTasks", entity,
				String.class);

		log.info("------------------ " + LocalDateTime.now());
		log.info("------------------ Response " + result);
		return processedBatch;
	}

	@GetMapping("/ping")
	public String ping() {
		log.info("------------------ " + LocalDateTime.now());
		log.info("------------------ Inside ping");

		return "ping OK";
	}
}
