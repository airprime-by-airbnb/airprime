package com.airbnb.airhack.airprime.model;

import java.io.Serializable;

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
public class Task implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	@JsonProperty("assignee_id")
	private Integer assigneeId;
	private float lat;
	private float lng;
	private String dueTime;
}
