package com.example.labrat;

public class KinematicsGovernor {
	public static float vectorMagnitude(float[] values) {
		float sum = 0;
		for (int i = 0;i < values.length;i++) {
			sum += Math.pow(values[i], 2);
		}
		return sum;
	}
}
