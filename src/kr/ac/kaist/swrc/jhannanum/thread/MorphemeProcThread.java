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

import kr.ac.kaist.swrc.jhannanum.comm.SetOfSentences;
import kr.ac.kaist.swrc.jhannanum.plugin.SupplementPlugin.MorphemeProcessor.MorphemeProcessor;

/**
 * This class makes the morpheme processor plug-in to run on a thread.
 * @author Sangwon Park (hudoni@world.kaist.ac.kr), CILab, SWRC, KAIST
 */
public class MorphemeProcThread extends Thread {
	/** the morpheme processor plug-in */
	private MorphemeProcessor morphProcessor = null;
	
	/** input queue */
	private LinkedBlockingQueue<SetOfSentences> in;
	
	/** output queue */
	private LinkedBlockingQueue<SetOfSentences> out;
	
	/**
	 * Constructor.
	 * @param morphProcessor - the morpheme processor plug-in
	 * @param in - input queue
	 * @param out - output queue
	 */
	public MorphemeProcThread(MorphemeProcessor morphProcessor, LinkedBlockingQueue<SetOfSentences> in, LinkedBlockingQueue<SetOfSentences> out) {
		this.morphProcessor = morphProcessor;
		this.in = in;
		this.out = out;
	}
	
	@Override
	public void run() {
		SetOfSentences sos = null;
		
		try {
			while (true) {
				sos = in.take();
				
				if ((sos = morphProcessor.doProcess(sos)) != null) {
					out.add(sos);
				}
			}
		} catch (InterruptedException e) {
			morphProcessor.shutdown();
		}
	}

}
