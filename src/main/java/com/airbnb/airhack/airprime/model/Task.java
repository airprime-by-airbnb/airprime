package com.airbnb.airhack.airprime.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(of = { "id" })
public class Task implements Serializable, Comparable<Task> {

	private static final long serialVersionUID = 1L;

	private Long id;
	@JsonProperty("assignee_id")
	private Integer assigneeId;
	private double lat;
	private double lng;
	private String dueTime;

	public double getMinutesFromDueTime() {
		return Integer.parseInt(dueTime.split(":")[0]) * 60 + Integer.parseInt(dueTime.split(":")[1]);
	}

	public static void main(String[] args) {
		Task t1 = new Task(1L, null, 1.3f, 1.6f, "16:45");
		Task t2 = new Task(1L, null, 1.3f, 1.6f, "15:45");
		Task t3 = new Task(1L, null, 1.3f, 1.6f, "1:56");

		List<Task> tasks = new ArrayList<>();
		tasks.add(t1);
		tasks.add(t2);
		tasks.add(t3);

		System.out.println(tasks);
		Collections.sort(tasks);
		System.out.println(tasks);

		System.out.println(tasks.stream().sorted().collect(Collectors.toList()));

	}

	@Override
	public int compareTo(Task o) {
		String[] s1 = this.getDueTime().split(":");
		String[] s2 = o.getDueTime().split(":");
		return new Integer((Integer.parseInt(s1[0]) * 60 + Integer.parseInt(s1[1])))
				.compareTo(new Integer((Integer.parseInt(s2[0]) * 60 + Integer.parseInt(s2[1]))));
	}
}
