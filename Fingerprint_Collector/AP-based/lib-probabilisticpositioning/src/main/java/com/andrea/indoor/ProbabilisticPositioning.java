package com.andrea.indoor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.StringJoiner;

import com.android.arraymanager.ArrayManager;

public class ProbabilisticPositioning {

	// RADIO MAP //
	private ArrayList<double[]> RSSmeans = null;
	private ArrayList<double[]> RSSvars = null;
	private ArrayList<int[]> coo = null;

	// Number of AP and RP
	private int numAP = 0;
	private int numRP = 0;

	// The path to where to RadioMap must be stored/read
	private String PATH_TO_RADIOMAP = null;
    private String DIRECTORY_TO_RADIOMAP = null;

	public ProbabilisticPositioning(String _path) {
		// Set the path
        this.DIRECTORY_TO_RADIOMAP = _path;
		this.PATH_TO_RADIOMAP = _path + "/" + Constants.RADIO_MAP_NAME;
		System.out.print("Start reading the RadioMap....");
	}

	/** Read the RadioMap.
	 * @throws IOException **/
	public void load() throws IOException{
		
		System.out.print("Start reading the RadioMap....");
		
		BufferedReader br = null;
		FileReader fr = null;

		fr = new FileReader(PATH_TO_RADIOMAP);

		String _singleRadioMapRow = "";
		br = new BufferedReader(new FileReader(PATH_TO_RADIOMAP));

		// Read the number of AP
		numAP = Integer.parseInt(br.readLine());

		// Read the number of RP
		numRP = Integer.parseInt(br.readLine());

		while ((_singleRadioMapRow = br.readLine()) != null) {
			parseSingleRadioMapRow(_singleRadioMapRow);
		}

		if (br != null)
			br.close();

		if (fr != null)
			fr.close();
		
		System.out.println("DONE!");
	}

	private void parseSingleRadioMapRow(String _singleRadioMapRow) {

		// Check just for the first insert.
		// If the load() function has been called, they should be not NULL
		if (coo == null){
			coo = new ArrayList<int[]>();
		}

		if (RSSmeans == null){
			RSSmeans = new ArrayList<double[]>();
		}

		if (RSSvars == null){
			RSSvars = new ArrayList<double[]>();
		}

		String[] ar = _singleRadioMapRow.split(",");

		// Parse x and y coordinate
		int _x = Integer.parseInt(ar[0]);
		int _y = Integer.parseInt(ar[1]);
		coo.add(new int[]{_x, _y});


		double[] _means = new double[numAP];
		double[] _vars = new double[numAP];

		// Insert an offeset = 2 just to skip the x and y coo
		int _offset = 2;
		for (int i = 0; i < numAP; i++) {
			_means[i] = Double.parseDouble(ar[2*i + _offset]);
			_vars[i] = Double.parseDouble(ar[2*i + 1 + _offset]);
		}

		RSSmeans.add(_means);
		RSSvars.add(_vars);

		_means = null;
		_vars = null;
	}


	/** Write the RadioMap in a format suitable for the <i>save()</i> function.
	 * @throws IOException **/
	public void save() throws IOException{
		
		System.out.print("Start writing the RadioMap....");
		
		BufferedWriter bw = null;
		FileWriter fw = null;

		fw = new FileWriter(PATH_TO_RADIOMAP);
		bw = new BufferedWriter(fw);

		// Write the number of AP
		bw.write(numAP + "\n");

		// Write the number of RP
		bw.write(numRP + "\n");

		// Write the RadioMap
		// Every row of the RadioMap is composed as follows:
		/*
			CooX_RP_1,CooY_RP_1,AP_1_mean,AP1_var,AP_2_mean,AP2_var,   ....  AP_N_mean,AP_N_var  
			CooX_RP_2,CooY_RP_2,AP_1_mean,AP1_var,AP_2_mean,AP2_var,   ....  AP_N_mean,AP_N_var
			.
			.
			.
			CooX_RP_L,CooY_RP_L,AP_1_mean,AP1_var,AP_2_mean,AP2_var,   ....  AP_N_mean,AP_N_var
		 */
		StringJoiner _sj = new StringJoiner(",");
		for (int i = 0; i < numRP; i++) { //rows
			_sj.add("" + coo.get(i)[0]);
			_sj.add("" + coo.get(i)[1]);
			for (int j = 0; j < numAP; j++) { //cols
				_sj.add("" + RSSmeans.get(i)[j]);
				_sj.add("" + RSSvars.get(i)[j]);
			}
			bw.write(_sj.toString() + "\n");

			_sj = null;
			_sj = new StringJoiner(",");
		}
		if (bw != null)
			bw.close();

		if (fw != null)
			fw.close();
		
		System.out.println("DONE!");
	}


	public void insertSingleRP(int[] _coo, double[] _rssMean, double[] _rssVars){

		// Check just for the first insert.
		// If the load() function has been called, they should be not NULL
		if (coo == null){
			coo = new ArrayList<int[]>();
		}

		if (RSSmeans == null){
			RSSmeans = new ArrayList<double[]>();
		}

		if (RSSvars == null){
			RSSvars = new ArrayList<double[]>();
		}

		// Update the number of AP (should not change)
		this.numAP = _rssMean.length;

		// Update the number of RP
		this.numRP++;

		coo.add(_coo);

		RSSmeans.add(_rssMean);
		RSSvars.add(_rssVars);
	}

	public double[] getPosition (double[] _means, int _numNeighbors, boolean _weighted) throws IOException{

		//Compute the LogLikelihood (LL)
		double[] _LL = new double[numRP];
		for (int i = 0; i < numRP; i++)
			_LL[i] = computeLikelihood(RSSmeans.get(i), RSSvars.get(i), _means);
		
	return 	computePosition(_LL ,_numNeighbors, _weighted);

	} 


	private double computeLikelihood(double[] means,  double[] vars, double[] input) throws IOException{
		
		int _numAPasInput = input.length;
		if (_numAPasInput != numAP)
			throw new IOException("AP number mismatch!\n");
				
		double _likelihoodSingleRP = 1;
		for (int j = 0; j < numAP; j++) { // ..and for each AP

			if (vars[j] == 0)
				vars[j] = 0.01;

			_likelihoodSingleRP *= ((1 / (    Math.sqrt( (2 * Math.PI * vars[j]))     )) * Math.exp(-((((input[j] - means[j]) * (input[j] - means[j]))) / (2*vars[j]))));
		}
		if (Double.isNaN(_likelihoodSingleRP))
			_likelihoodSingleRP = 0;
		return _likelihoodSingleRP;		
	}


	private double[] computePosition(double[] _rpLikelihood , int neighboors , boolean weighted)
	{
		double[] pos = new double[2];
		int[] _maxima = ArrayManager.findKmaxima(_rpLikelihood, neighboors);

		double _cooX = 0, _cooY = 0, _normalization = 0;

		for (int i = 0; i <neighboors; i++) {
			double _weight = 0;
			if (weighted)
				_weight = _rpLikelihood[_maxima[i]];
			else _weight = 1;

			_cooX += coo.get(_maxima[i])[0] * _weight;
			_cooY += coo.get(_maxima[i])[1] * _weight;
			_normalization += _weight;
		}
		pos[0] = _cooX / _normalization;
		pos[1] = _cooY / _normalization;

		return pos;
	}

}
