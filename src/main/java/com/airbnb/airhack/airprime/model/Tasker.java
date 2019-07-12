package com.airbnb.airhack.airprime.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
@EqualsAndHashCode(of = { "id" })
public class Tasker {

	private long id;
	private double lat;
	private double lng;
	private int point;
	private int nextAvailability;
	private boolean isFree = true;

	public Tasker(long id, double lat, double lng, int point, boolean isFree, String dueTime) {
		super();
		this.id = id;
		this.lat = lat;
		this.lng = lng;
		this.point = point;
		this.nextAvailability = plusMinutes(dueTime, 30); // +30
		this.isFree = isFree;
	}

	private static final int plusMinutes(String dueTime, int minutes) {
		String[] s = dueTime.split(":");
		return Integer.parseInt(s[0]) * 60 + Integer.parseInt(s[1]) + minutes;
	}

}
