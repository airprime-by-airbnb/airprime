package com.airbnb.airhack.airprime.helper;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import com.airbnb.airhack.airprime.model.Batch;
import com.airbnb.airhack.airprime.model.Task;
import com.airbnb.airhack.airprime.model.Tasker;

public class Algo {

	public static void main(String[] args) {

		List<Task> tasks = new ArrayList<>();
		tasks.add(new Task(1L, null, 48.85554319120794, 2.3613359633447204, "16:30"));
		tasks.add(new Task(2L, null, 48.85313729018271, 2.32256080014798, "13:15"));
		tasks.add(new Task(3L, null, 48.838453425693785, 2.372673134911582, "21:45"));
		Batch batch = new Batch("234322", 1, 3, tasks);

		// TODO processing
		List<Task> sortedTasks = batch.getTasks().stream().sorted().collect(Collectors.toList());
		int tasksCount = batch.getTasksCount();

		double[][] matriceDistance = new double[tasksCount][tasksCount];
		for (int i = 0; i < sortedTasks.size(); i++) {
			for (int j = 0; j < sortedTasks.size(); j++) {
				matriceDistance[i][j] = DistanceHelper.distance(sortedTasks.get(i).getLat(),
						sortedTasks.get(i).getLng(), sortedTasks.get(j).getLat(), sortedTasks.get(j).getLng(), "K");
				// System.out.println(i + "," + j + " = " +
				// matriceDistance[i][j]);
			}
		}

		double[][] matriceTemps = new double[tasksCount][tasksCount];
		for (int i = 0; i < sortedTasks.size(); i++) {
			for (int j = 0; j < sortedTasks.size(); j++) {
				matriceTemps[i][j] = TimeHelper.processTime(DistanceHelper.distance(sortedTasks.get(i).getLat(),
						sortedTasks.get(i).getLng(), sortedTasks.get(j).getLat(), sortedTasks.get(j).getLng(), "K"));
				System.out.println(i + "," + j + " = " + matriceTemps[i][j]);
			}
		}

		int i = 1;
		List<Tasker> taskers = new LinkedList<>();
		for (Task sortedTask : sortedTasks) {
			if (i <= batch.getTaskersCount()) {
				Tasker t = new Tasker(i, sortedTask.getLat(), sortedTask.getLng(), i, false, sortedTask.getDueTime());
				taskers.add(t);
				sortedTask.setAssignee_id(i);
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

					sortedTask.setAssignee_id(idPerson);
					Tasker currentTasker = taskers.get(idPerson - 1);
					currentTasker.setDueTime(sortedTask.getDueTime());
				}

			}
		}

	}
}
