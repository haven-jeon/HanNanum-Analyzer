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

import kr.ac.kaist.swrc.jhannanum.comm.Sentence;
import kr.ac.kaist.swrc.jhannanum.plugin.SupplementPlugin.PosProcessor.PosProcessor;

/**
 * This class makes the POS processor plug-in to run on a thread.
 * @author Sangwon Park (hudoni@world.kaist.ac.kr), CILab, SWRC, KAIST
 */
public class PosProcThread extends Thread {
	/** the POS processor plug-in */
	private PosProcessor posProcessor = null;
	
	/** input queue */
	private LinkedBlockingQueue<Sentence> in;
	
	/** output queue */
	private LinkedBlockingQueue<Sentence> out;
	
	/**
	 * Constructor.
	 * @param posProcessor - the POS processor plug-in
	 * @param in - input queue
	 * @param out - output queue
	 */
	public PosProcThread(PosProcessor posProcessor, LinkedBlockingQueue<Sentence> in, LinkedBlockingQueue<Sentence> out) {
		this.posProcessor = posProcessor;
		this.in = in;
		this.out = out;
	}
	
	@Override
	public void run() {
		Sentence sent = null;
		
		try {
			while (true) {
				sent = in.take();
				
				if ((sent = posProcessor.doProcess(sent)) != null) {
					out.add(sent);
				}
			}
		} catch (InterruptedException e) {
			posProcessor.shutdown();
		}
	}
}
