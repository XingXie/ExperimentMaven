package com.xingxie.java8;

import java.util.List;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public class Streams {
	private enum Status {
		OPEN, CLOSED
	};

	private static final class Task {
		private final Status status;
		private final Integer points;

		Task(final Status status, final Integer points) {
			this.status = status;
			this.points = points;
		}

		public Integer getPoints() {
			return points;
		}

		public Status getStatus() {
			return status;
		}

		@Override
		public String toString() {
			return String.format("[%s, %d]", status, points);
		}
	}

	/**
	 * Stream operations are divided into intermediate and terminal operations.
	 * Intermediate operations return a new stream. They are always lazy, executing an intermediate operation such as
	 * filter does not actually perform any filtering, but instead creates a new stream that, when traversed, contains
	 * the elements of the initial stream that match the given predicate
	 * Terminal operations, such as forEach or sum, may traverse the stream to produce a result or a side-effect. After
	 * the terminal operation is performed, the stream pipeline is considered consumed, and can no longer be used. In
	 * almost all cases, terminal operations are eager, completing their traversal of the underlying data source.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		final Collection<Task> tasks = Arrays.asList(new Task(Status.OPEN, 5), new Task(Status.OPEN, 13), new Task(
				Status.CLOSED, 8));

		// Calculate total points of all active tasks using sum()
		final long totalPointsOfOpenTasks = tasks.stream().filter(task -> task.getStatus() == Status.OPEN)
				.mapToInt(Task::getPoints).sum();

		System.out.println("Total points: " + totalPointsOfOpenTasks);

		// parallele
		// Calculate total points of all tasks
		final double totalPoints = tasks.stream().parallel().map(task -> task.getPoints()) // or map( Task::getPoints )
				.reduce(0, Integer::sum);

		System.out.println("Total points (all tasks): " + totalPoints);

		// Group tasks by their status
		final Map<Status, List<Task>> map = tasks.stream().collect(Collectors.groupingBy(Task::getStatus));
		System.out.println(map);

		// Calculate the weight of each tasks (as percent of total points)
		final Collection<String> result = tasks.stream() // Stream< String >
				.mapToInt(Task::getPoints) // IntStream
				.asLongStream() // LongStream
				.mapToDouble(points -> points / totalPoints) // DoubleStream
				.boxed() // Stream< Double >
				.mapToLong(weigth -> (long) (weigth * 100)) // LongStream
				.mapToObj(percentage -> percentage + "%") // Stream< String>
				.collect(Collectors.toList()); // List< String >

		System.out.println(result);
	}
}