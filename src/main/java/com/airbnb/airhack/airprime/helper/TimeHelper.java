package com.airbnb.airhack.airprime.helper;

public class TimeHelper {

	//km/h
	private static final int VITESSE = 10; 
	
	/**
	 * 
	 * @param distance km
	 * @return min
	 */
	public static double processTime(double distance) {
		return (distance / VITESSE) * 60;
	}
	
	public static void main(String[] args) {
		
//		System.out.println(processTime(1D));
	}
}
