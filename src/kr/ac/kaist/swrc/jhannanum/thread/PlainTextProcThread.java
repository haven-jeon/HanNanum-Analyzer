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
import kr.ac.kaist.swrc.jhannanum.plugin.SupplementPlugin.PlainTextProcessor.PlainTextProcessor;

/**
 * This class makes the plain text processor plug-in to run on a thread.
 * @author Sangwon Park (hudoni@world.kaist.ac.kr), CILab, SWRC, KAIST
 */
public class PlainTextProcThread extends Thread {
	/** the plain text processor plug-in */
	private PlainTextProcessor plainTextProcessor = null;
	
	/** input queue */
	private LinkedBlockingQueue<PlainSentence> in;
	
	/** output queue */
	private LinkedBlockingQueue<PlainSentence> out;
	
	/**
	 * Constructor.
	 * @param plainTextProcessor - the plain text processor plug-in
	 * @param in - input queue
	 * @param out - output queue
	 */
	public PlainTextProcThread(PlainTextProcessor plainTextProcessor, LinkedBlockingQueue<PlainSentence> in, LinkedBlockingQueue<PlainSentence> out) {
		this.plainTextProcessor = plainTextProcessor;
		this.in = in;
		this.out = out;
	}
	
	@Override
	public void run() {
		PlainSentence ps = null;
		
		try {
			while (true) {
				ps = in.take();
				
				if ((ps = plainTextProcessor.doProcess(ps)) != null) {
					out.add(ps);
				}
				
				while (plainTextProcessor.hasRemainingData()) {
					if ((ps = plainTextProcessor.doProcess(null)) != null) {
						out.add(ps);
					}
				}
				
				if ((ps = plainTextProcessor.flush()) != null) {
					out.add(ps);
				}
			}
		} catch (InterruptedException e) {
			plainTextProcessor.shutdown();
		}
	}
}
