/*  Copyright 2010, 2011 Semantic Web Research Center, KAIST

This file is part of JHanNanum.

JHanNanum is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

JHanNanum is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with JHanNanum.  If not, see <http://www.gnu.org/licenses/>   */

package kr.ac.kaist.swrc.jhannanum.plugin.MajorPlugin.PosTagger.HmmPosTagger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;

/**
 * This class is for statistic data which is important to the Hidden Markov Model.
 * @author Sangwon Park (hudoni@world.kaist.ac.kr), CILab, SWRC, KAIST
 */
public class ProbabilityDBM {
	/** hash table */
	private Hashtable<String,double[]> table = null;
	
	/**
	 * Constructor.
	 * @param fileName - the name of the file which has statistic data
	 * @throws IOException
	 */
	public ProbabilityDBM(String fileName) throws IOException {
		table = new Hashtable<String,double[]>();
		init(fileName);
	}
	
	/**
	 * Cleans the hash table.
	 */
	public void clear() {
		table.clear();
	}
	
	/**
	 * Gets the probability data to which specified key mapped.
	 * @param key - the key of probability data
	 * @return the probability data to which specified key mapped
	 */
	public double[] get(String key) {
		return table.get(key);
	}
	
	/**
	 * It loads the probability data from the specified file.
	 * @param fileName - the path of the file which has the probability data
	 * @throws IOException
	 */
	private void init(String fileName) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		String line = null;
		String[] tokens = null;
		double[] numbers = null;
		
		while ((line = br.readLine()) != null) {
			tokens = line.split(" ");
			
			numbers = new double[tokens.length - 1];
			
			for (int i = 0; i < tokens.length - 1; i++) {
				numbers[i] = Double.parseDouble(tokens[i + 1]);
			}
			
			if (tokens == null || tokens[0] == null || numbers == null ) {
				System.out.println("hi");
			}
			
			table.put(tokens[0], numbers);
		}
		br.close();
	}
}
