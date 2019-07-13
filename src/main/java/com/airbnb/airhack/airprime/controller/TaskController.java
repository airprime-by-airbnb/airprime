package com.airbnb.airhack.airprime.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.airbnb.airhack.airprime.helper.DistanceHelper;
import com.airbnb.airhack.airprime.helper.TimeHelper;
import com.airbnb.airhack.airprime.model.Batch;
import com.airbnb.airhack.airprime.model.Task;
import com.airbnb.airhack.airprime.model.Tasker;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

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
	
		log.info("------------ " + batch.getTasksCount());
		log.info("------------ " + batch);
		Batch processedBatch = batch;
		// TODO processing
		List<Task> sortedTasks = batch.getTasks().stream().sorted().collect(Collectors.toList());
		int tasksCount = batch.getTasksCount();

		double[][] matriceDistance = new double[tasksCount][tasksCount];
		for (int i = 0; i < sortedTasks.size(); i++) {
			for (int j = 0; j < sortedTasks.size(); j++) {
				matriceDistance[i][j] = DistanceHelper.distance(sortedTasks.get(i).getLat(),
						sortedTasks.get(i).getLng(), sortedTasks.get(j).getLat(), sortedTasks.get(j).getLng(), "K");
			}
		}

		double[][] matriceTemps = new double[tasksCount][tasksCount];
		for (int i = 0; i < sortedTasks.size(); i++) {
			for (int j = 0; j < sortedTasks.size(); j++) {
				matriceTemps[i][j] = TimeHelper.processTime(DistanceHelper.distance(sortedTasks.get(i).getLat(),
						sortedTasks.get(i).getLng(), sortedTasks.get(j).getLat(), sortedTasks.get(j).getLng(), "K"));
				// System.out.println(i + "," + j + " = " + matriceTemps[i][j]);
			}
		}

		int i = 0;
		List<Tasker> taskers = new LinkedList<>();
		for (Task sortedTask : sortedTasks) {
			if (i < batch.getTaskersCount()) {
				Tasker t = new Tasker(i, sortedTask.getLat(), sortedTask.getLng(), i, false, sortedTask.getDueTime());
				taskers.add(t);
				sortedTask.setAssigneeId(i);

			} else {
				int idPerson = -1;
				double min = Integer.MAX_VALUE;
				for (int j = 0; j < taskers.size(); j++) {
					Tasker t = taskers.get(j);
					double travel = matriceTemps[t.getPoint()][i];
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
					Tasker currentTasker = taskers.get(idPerson);
					currentTasker.setPoint(i);
					currentTasker.setDueTime(sortedTask.getDueTime());
				}

			}
			i++;
		}
		batch.setTasks(sortedTasks);


		log.info("------------output");
		log.info("---------------" + batch);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Bearer " + "3IgAUjz5f3Rntu1WefSt6vTXZ5C7dgEi3cU75t4L2hJSCNLIkaXsDid4gdWl");

		HttpEntity<Batch> entity = new HttpEntity<Batch>(batch, headers);
		String result = restTemplate.postForObject("http://airhack-api.herokuapp.com/api/submitTasks", entity,
				String.class);

		return processedBatch;
	}

	@GetMapping("/markers")
	public JsonNode getMarkers() throws FileNotFoundException, IOException {
		File f = new File("src/main/resources/finalbatch.json");

		BufferedReader br = new BufferedReader(new FileReader(f));
		String json = "";
		String line = br.readLine();

		ObjectMapper mapper = new ObjectMapper();
		JsonNode node = mapper.readTree(line);

		return node;
		// JsonNode subNode = node.get("tasks");
		//
		// ArrayNode newNode = mapper.createArrayNode();
		// int i = 0;
		// for (JsonNode n : subNode) {
		//
		// if (i < 50) {
		//
		// if(i>10) {
		// ((ObjectNode)n).remove("assigneeId");
		// ((ObjectNode)n).put("assigneeId", 2);
		// }
		// int status = 3;
		//
		// if(n.get("assigneeId").isNull()) {
		//
		// status = 3;
		// } else {
		// status = new Random().nextInt(3);
		// }
		// ((ObjectNode)n).put("status", status);
		// newNode.add(n);
		// } else {
		// break;
		// }
		// i++;
		// }
		// ObjectNode n = ((ObjectNode)node);
		// n.remove("tasks");
		// n.set("tasks", newNode);
		// n.remove("tasksCount");
		// n.put("tasksCount", 50);
		// BufferedWriter bw = new BufferedWriter(new FileWriter(new
		// File("src/main/resources/finalbatch.json")));
		// System.out.println(n.toString());
		// bw.write(n.toString());
		// return n;
	}

	@GetMapping("/batch")
	public JsonNode getByStatus(@RequestParam("status") int status) throws FileNotFoundException, IOException {
		JsonNode batch = getMarkers();
		ArrayNode newNode = new ObjectMapper().createArrayNode();
		for (JsonNode n : batch.get("tasks")) {
			if (n.get("status").intValue() == status) {
				newNode.add(n);
			}
		}
		((ObjectNode) batch).remove("tasks");
		((ObjectNode) batch).set("tasks", newNode);
		return batch;
	}

	@GetMapping("/ping")
	public Batch ping() {
		log.info("------------------ " + LocalDateTime.now());
		log.info("------------------ Inside ping");

		return new Batch();
	}
}
