package com.airbnb.airhack.airprime.controller;

import java.util.Arrays;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.airbnb.airhack.airprime.model.Batch;
import com.airbnb.airhack.airprime.model.Task;

@RestController
@RequestMapping("/api/hooks")
public class TaskController {

	@GetMapping("/incomingTasks")
	public Batch incomingTasks() {
		return new Batch("id1", 5, 50, Arrays.asList(new Task(1, null, 1.333f, 4.4554f, "16:00")));
	}
}
