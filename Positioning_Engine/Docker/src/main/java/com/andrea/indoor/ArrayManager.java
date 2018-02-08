package com.andrea.indoor;

import java.util.ArrayList;

public class ArrayManager {

	/**
	 * Calcola il massimo di un array di double
	 * @param arr
	 * @return
	 */
	public static double max(double[] arr)
	{
		double maximum = arr[0];   // start with the first value
		for (int i=0; i<arr.length; i++) {
			if (arr[i] > maximum) {
				maximum = arr[i];   // new maximum
			}
		}
		return maximum;
	}

	/**
	 * Calcola il minimo di un array di double
	 * @param arr
	 * @return
	 */
	public static double min(double[] arr)
	{
		double minimum = arr[0];   // start with the first value
		for (int i=0; i<arr.length; i++) {
			if (arr[i] < minimum) {
				minimum = arr[i];   // new minimum
			}
		}
		return minimum;
	}

	/**
	 * Calcola il massimo di un array di int
	 * @param arr
	 * @return
	 */
	public static int max(int[] arr)
	{
		int maximum = arr[0];   // start with the first value
		for (int i=0; i<arr.length; i++) {
			if (arr[i] > maximum) {
				maximum = arr[i];   // new maximum
			}
		}
		return maximum;
	}

	/**
	 * Calcola il massimo di un array di int
	 * @param arr
	 * @return
	 */
	public static double max(ArrayList<Double> arr)
	{
		double maximum = arr.get(0);   // start with the first value
		for (int i=0; i<arr.size(); i++) {
			if (arr.get(i) > maximum) {
				maximum = arr.get(i);   // new maximum
			}
		}
		return maximum;
	}

	/**
	 * Calcola la media di un array di double
	 * @param arr
	 * @return
	 */
	public static double mean(double[] arr)
	{
		double mean = 0;
		for (int i=0; i<arr.length; i++)
		{mean = mean + arr[i];}
		return (mean / (arr.length));
	}

	/**
	 * Calcola la varianza di un array di double
	 * @param arr
	 * @return
	 */
	public static double var(double[] arr)
	{
		double m = mean(arr);

		double var = 0;
		for (int i=0; i<arr.length; i++)
			var += Math.pow((arr[i] - m), 2);
		return (var / (arr.length));
	}

	/**
	 *
	 * @param arr
	 * @param element
	 * @return
	 * Calcola il prodotto di un vettore per un elemento
	 */
	public static double[] productVectorPerElement(double[] arr , double element)
	{
		double[] _app = new double[arr.length];
		for (int i=0; i<arr.length; i++)
			_app[i] = arr[i] * element;

		return _app;
	}

	/**
	 *
	 * @param arr
	 * @param element
	 * @return
	 * Calcola il prodotto di un vettore per un elemento
	 */
	public static double[] productVectorPerElement(int[] arr , double element)
	{
		double[] _app = new double[arr.length];
		for (int i=0; i<arr.length; i++)
			_app[i] = arr[i] * element;

		return _app;
	}

	public static double[] productVectorPerVector(double[] arr1 , double[] arr2)
	{
		double[] _app = new double[arr1.length];
		for (int i=0; i<arr1.length; i++)
			_app[i] = arr1[i] * arr2[i];

		return _app;
	}

	/**
	 *
	 * @param arr1
	 * @param arr2
	 * @return
	 * Divide <b>arr1 / arr2</b>
	 */
	public static double[] divisionVectorPerVector(double[] arr1 , double[] arr2)
	{
		double[] _app = new double[arr1.length];
		for (int i=0; i<arr1.length; i++)
			_app[i] = arr1[i] / arr2[i];

		return _app;
	}

	/**
	 *
	 * @param arr1
	 * @param arr2
	 * @return
	 * Subtraction of <b>arr1 - arr2</b>
	 */

	public static double[] subctrationVectorPerVector(double[] arr1 , double[] arr2)
	{
		double[] _app = new double[arr1.length];
		for (int i=0; i<arr1.length; i++)
			_app[i] = arr1[i] - arr2[i];

		return _app;
	}

	/**
	 *
	 * @param arr1
	 * @param arr2
	 * @return
	 * Subtraction of <b>arr1 - arr2</b>
	 */

	public static double[] subctrationVectorPerVector(double[] arr1 , int[] arr2)
	{
		double[] _app = new double[arr1.length];
		for (int i=0; i<arr1.length; i++)
			_app[i] = arr1[i] - arr2[i];

		return _app;
	}

	/**
	 *
	 * @param arr1
	 * @param arr2
	 * @return
	 * Subtraction of <b>arr1 - arr2</b>
	 */

	public static double[] subctrationVectorPerVector(int[] arr1 , int[] arr2)
	{
		double[] _app = new double[arr1.length];
		for (int i=0; i<arr1.length; i++)
			_app[i] = arr1[i] - arr2[i];

		return _app;
	}


	public static double sumOfVector(double[] arr)
	{
		double _sum = 0;
		for (int i=0; i<arr.length; i++)
			_sum += arr[i];

		return _sum;
	}


	public static int[] FindIndexPreOrder(int[] OriginalArr , Integer[] DescendentArr)
	{
		int [] index = new int[DescendentArr.length];

		for (int i=0; i<DescendentArr.length; i++){
			index[i] = FindElementInArray(OriginalArr, DescendentArr[i]);
			OriginalArr[index[i]] = Integer.MAX_VALUE; //Lo sostituisco in modo che non possa piu essere contato
		}
		return index;
	}

	public static ArrayList<Integer> FindOccurenciesIntoArray(int[] arr , int value)
	{
		ArrayList<Integer> occ = new ArrayList<Integer>();

		while(true)
		{
			int index = FindElementInArray(arr, value);
			if (index != -1){
				occ.add(index);
				arr[index] = Integer.MAX_VALUE;
			}

			else {break;}
		}

		if (occ.size() == 0){
			return null;}

		return occ;
	}

	public static int FindNumberOfOccurenciesIntoArray(int[] arr , int value)
	{
		int occ = 0;
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] == value) {occ++;}
		}
		return occ;
	}

	public static int FindNumberOfOccurenciesIntoArray(Integer[] arr , int value)
	{
		int occ = 0;
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] == value) {occ++;}
		}
		return occ;
	}

	public static int FindElementInArray(int[] arr , int element)
	{
		int index = -1;

		for (int i=0; i<arr.length; i++)
		{
			if(arr[i] == element){
				index = i;}
		}
		return index;
	}

	public static ArrayList<Integer> FindElementInArrayAboveThr(double[] arr , double thr)
	{
		ArrayList<Integer> dec = new ArrayList<Integer>();

		for (int i = 0; i < arr.length; i++) {
			if (arr[i] >= thr)
				dec.add(i);
		}
		return dec;
	}

	public static int findElementInArray(double[] arr , double element)
	{
		int index = -1;

		for (int i=0; i<arr.length; i++)
		{
			if(arr[i] == element){
				index = i;}
		}
		return index;
	}

	public static int findElementInArray(ArrayList<Double> arr , double element)
	{
		int index = -1;

		for (int i=0; i<arr.size(); i++)
		{
			if(arr.get(i) == element){
				index = i;}
		}
		return index;
	}

	public static int[] FindIndexPreOrder(Double[] OriginalArr , Double[] DescendentArr)
	{
		//Lo copio in modo da non modificare OriginalArr
		Double[] app = new Double[OriginalArr.length];
		for (int i = 0; i < app.length; i++) {
			app[i] = OriginalArr[i];
		}



		int [] index = new int[DescendentArr.length];

		for (int i=0; i<DescendentArr.length; i++){
			index[i] = FindElementInArray(app, DescendentArr[i]);
			app[index[i]] = Double.MAX_VALUE; //Lo sostituisco in modo che non possa piu essere contato
		}
		return index;
	}

	public static int FindElementInArray(Double[] arr , double element)
	{
		int index = -1;

		for (int i=0; i<arr.length; i++)
		{
			if(arr[i] == element){
				index = i;}
		}
		return index;
	}

	/**
	 *
	 * @param arr
	 * @param numberOfmaxima
	 * @return
	 *
	 * Restituisce i primi numberOfmaxima dell'array arr
	 */
	public static int[] findKmaxima(double[] arr , int numberOfmaxima)
	{
		int[] _indexOfMaxima = new int[numberOfmaxima];

		for (int i = 0; i < numberOfmaxima; i++) { //Cycle over the number of maxima to find
			int _index = findElementInArray(arr, max(arr));
			_indexOfMaxima[i] = _index;
			arr[_index] = Double.MIN_VALUE;
		}
		return _indexOfMaxima;
	}

}
