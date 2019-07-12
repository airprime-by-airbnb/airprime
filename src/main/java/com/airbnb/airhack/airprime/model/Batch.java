package com.airbnb.airhack.airprime.model;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = { "batchId" })
public class Batch implements Serializable {

	
	private static final long serialVersionUID = 1L;

	private String batchId;
	private Integer taskersCount;
	private Integer tasksCount;
	private List<Task> tasks;
}
