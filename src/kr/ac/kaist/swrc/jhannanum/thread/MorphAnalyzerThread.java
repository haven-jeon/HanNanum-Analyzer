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

package kr.ac.kaist.swrc.jhannanum.thread;

import java.util.concurrent.LinkedBlockingQueue;

import kr.ac.kaist.swrc.jhannanum.comm.PlainSentence;
import kr.ac.kaist.swrc.jhannanum.comm.SetOfSentences;
import kr.ac.kaist.swrc.jhannanum.plugin.MajorPlugin.MorphAnalyzer.MorphAnalyzer;

/**
 * This class makes the morphological analyzer plug-in to run on a thread.
 * @author Sangwon Park (hudoni@world.kaist.ac.kr), CILab, SWRC, KAIST
 */
public class MorphAnalyzerThread extends Thread {
	/** the morphological analyzer plug-in */
	private MorphAnalyzer ma = null;
	
	/** input queue */
	private LinkedBlockingQueue<PlainSentence> in;
	
	/** output queue */
	private LinkedBlockingQueue<SetOfSentences> out;
	
	/**
	 * Constructor.
	 * @param ma - the morphological analyzer plug-in
	 * @param in - input queue
	 * @param out - output queue
	 */
	public MorphAnalyzerThread(MorphAnalyzer ma, LinkedBlockingQueue<PlainSentence> in, LinkedBlockingQueue<SetOfSentences> out) {
		this.ma = ma;
		this.in = in;
		this.out = out;
	}
	
	@Override
	public void run() {
		PlainSentence ps = null;
		SetOfSentences sos = null;
		
		try {
			while (true) {
				ps = in.take();
				
				if ((sos = ma.morphAnalyze(ps)) != null) {
					out.add(sos);
				}
			}
		} catch (InterruptedException e) {
			ma.shutdown();
		}
	}
}
