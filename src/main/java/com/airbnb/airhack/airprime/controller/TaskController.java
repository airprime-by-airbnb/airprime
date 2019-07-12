package com.airbnb.airhack.airprime.controller;

import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

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

import com.airbnb.airhack.airprime.helper.DistanceHelper;
import com.airbnb.airhack.airprime.helper.TimeHelper;
import com.airbnb.airhack.airprime.model.Batch;
import com.airbnb.airhack.airprime.model.Task;
import com.airbnb.airhack.airprime.model.Tasker;

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
		List<Task> sortedTasks = batch.getTasks().stream().sorted().collect(Collectors.toList());
		int tasksCount = batch.getTasksCount();
		
		double[][] matriceDistance = new double[tasksCount][tasksCount];
		for (int i = 0; i < sortedTasks.size(); i++) {
			for (int j = 0; j < sortedTasks.size(); j++) {
				matriceDistance[i][j] = DistanceHelper.distance(sortedTasks.get(i).getLat(), sortedTasks.get(i).getLng(), sortedTasks.get(j).getLat(), sortedTasks.get(j).getLng(), "K");
			}
		}
		
		double[][] matriceTemps = new double[tasksCount][tasksCount];
		for (int i = 0; i < sortedTasks.size(); i++) {
			for (int j = 0; j < sortedTasks.size(); j++) {
				matriceTemps[i][j] = TimeHelper.processTime(DistanceHelper.distance(sortedTasks.get(i).getLat(),
						sortedTasks.get(i).getLng(), sortedTasks.get(j).getLat(), sortedTasks.get(j).getLng(), "K"));
//				System.out.println(i + "," + j + " = " + matriceTemps[i][j]);
			}
		}

		int i = 1;
		List<Tasker> taskers = new LinkedList<>();
		for (Task sortedTask : sortedTasks) {
			if (i <= batch.getTaskersCount()) {
				Tasker t = new Tasker(i, sortedTask.getLat(), sortedTask.getLng(), i, false, sortedTask.getDueTime());
				taskers.add(t);
				sortedTask.setAssigneeId(i);
				i++;
			} else {
				int idPerson = -1;
				double min = Integer.MAX_VALUE;
				for (int j = 0; j < taskers.size(); j++) {
					Tasker t = taskers.get(i);
					double travel = matriceTemps[t.getPoint()][sortedTask.getId().intValue()];
					double arrivedTime = t.getNextAvailability() + travel;
					if (arrivedTime <= sortedTask.getMinutesFromDueTime()) {
						if (travel < min) {
							min = travel;
							idPerson = j;
						}
					}
				}
				if (idPerson != -1) {

					sortedTask.setAssigneeId(idPerson);
					Tasker currentTasker = taskers.get(idPerson - 1);
					currentTasker.setDueTime(sortedTask.getDueTime());
				}

			}
		}
		
		batch.setTasks(sortedTasks);

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
