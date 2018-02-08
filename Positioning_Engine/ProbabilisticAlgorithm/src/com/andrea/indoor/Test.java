package com.andrea.indoor;

import java.io.IOException;

public class Test {

	public static void main(String[] args) {

		// Modify this string with your local path
		final String PATH = "/Users/xuandu/Development/Positioning/Andrea";

		// Instantiate the object ProbabilisticPositioning
		ProbabilisticPositioning myPositioning = new ProbabilisticPositioning(PATH);

				/////////////////////// Example 1 ////////////////////////
				// Read the RadioMap, simulate a WiFi scan and compute the position

		// Load the RadioMap
		try {
			myPositioning.load();
		} catch (IOException e) {
			System.out.println("RadioMap NOT FOUND!");
			e.printStackTrace();
			return;
		}

		// Simulate a WiFi scan
		double[] _rssScan = new double[]{-75, -65, -91, -71, -63};

		// Get the position
		double[] coo = new double[2];
		try {
			coo = myPositioning.getPosition(_rssScan, 2, true);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		System.out.println("Coo X: " + coo[0] + " Coo Y: " + coo[1]);
				/////////////////////////////////////////////////////////////




			/////////////////////// Example 2 ////////////////////////
			// Read the RadioMap, insert a new ReferencePoint and save the updated map again.
//		int[] RPcoo = null;
//		double[] means = null, vars = null;
//
//		RPcoo = new int[2];
//		RPcoo[0] = 0; RPcoo[1] = 0;
//
//		means = new double[5];
//		means[0] = -35; means[1] = -45; means[2] = -63; means[3] = -45; means[4] = -63;
//
//		vars = new double[5];
//		vars[0] = 6.98; vars[1] = 2.25; vars[2] = 7.5; vars[3] = 2.25; vars[4] = 7.5;
//
//		myPositioning.insertSingleRP(RPcoo, means, vars);
//
//		try {
//			myPositioning.save();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
			/////////////////////////////////////////////////////////////


	}

}
