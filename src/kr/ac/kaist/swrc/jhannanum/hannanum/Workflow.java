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

package kr.ac.kaist.swrc.jhannanum.hannanum;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;

import kr.ac.kaist.swrc.jhannanum.comm.PlainSentence;
import kr.ac.kaist.swrc.jhannanum.comm.Sentence;
import kr.ac.kaist.swrc.jhannanum.comm.SetOfSentences;
import kr.ac.kaist.swrc.jhannanum.exception.ResultTypeException;
import kr.ac.kaist.swrc.jhannanum.plugin.MajorPlugin.MorphAnalyzer.MorphAnalyzer;
import kr.ac.kaist.swrc.jhannanum.plugin.MajorPlugin.PosTagger.PosTagger;
import kr.ac.kaist.swrc.jhannanum.plugin.SupplementPlugin.MorphemeProcessor.MorphemeProcessor;
import kr.ac.kaist.swrc.jhannanum.plugin.SupplementPlugin.PlainTextProcessor.PlainTextProcessor;
import kr.ac.kaist.swrc.jhannanum.plugin.SupplementPlugin.PosProcessor.PosProcessor;
import kr.ac.kaist.swrc.jhannanum.thread.MorphAnalyzerThread;
import kr.ac.kaist.swrc.jhannanum.thread.MorphemeProcThread;
import kr.ac.kaist.swrc.jhannanum.thread.PlainTextProcThread;
import kr.ac.kaist.swrc.jhannanum.thread.PosProcThread;
import kr.ac.kaist.swrc.jhannanum.thread.PosTaggerThread;

/**
 * This class is for the HanNanum work flow, which can be set up with more than one
 * HanNanum plug-in. The work flow can be used with the following steps:<br>
 * <br>
 * 		1. Create the work flow using one of the constructors with suitable configurations.<br>
 * 		2. Set the plug-ins up on the work flow regarding the purpose of analysis and the characteristics of input.<br>
 * 		3. Activate the work flow in the multi-thread mode or the single thread mode.<br>
 * 		4. Analyze the target text with the work flow.<br>
 * 		5. Get the result with some relevant data type or string representation.<br>
 * 		6. Repeat the step 4-5 you need it.<br>
 * 		7. Close the work flow when it will not be used anymore.<br>
 * <br>
 * Take a look the demo program - kr.ac.kaist.swrc.jhannanum.demo.WorkflowWithHMMTagger for an example.<br>
 * <br>
 * @author Sangwon Park (hudoni@world.kaist.ac.kr), CILab, SWRC, Kaist
 */
public class Workflow {
	/**
	 * The default value for the maximum number of the supplement plug-ins on each phase.
	 */
	public static int MAX_SUPPLEMENT_PLUGIN_NUM = 8;
	
	/**
	 * The maximum number of the supplement plug-ins of each phase.
	 */
	private int maxSupplementPluginNum = 0;
	
	/**
	 * The flag for the thread mode. true: multi-thread mode, false: single-thread mode.
	 */
	private boolean isThreadMode = false;
	
	/**
	 * The analysis phase of the work flow.
	 */
	private int outputPhaseNum = 0;
	
	/**
	 * The number of the plug-ins for the last phase of the work flow.
	 */
	private int outputQueueNum = 0;
	
	/**
	 * Plug-in thread list.
	 */
	private LinkedList<Thread> threadList = null;
	
	
	/* Major Plugins */
	
	/**
	 * The second phase, major plug-in - morphological analyzer.
	 */  
	private MorphAnalyzer morphAnalyzer = null;
	
	/**
	 * The configuration file for the morphological analyzer.
	 */  
	private String morphAnalyzerConfFile = null;
	
	/**
	 * The third phase, major plug-in - POS tagger.
	 */
	private PosTagger posTagger = null;
	
	/**
	 * The configuration file for the POS tagger.
	 */
	private String posTaggerConfFile = null;
	
	
	/* Supplement Plugins */
	
	/**
	 * The first phase, supplement plug-ins, plain text processors.
	 */ 
	private PlainTextProcessor[] plainTextProcessors = null;
	
	/**
	 * The configuration files for the plain text processors.
	 */ 
	private String[] plainTextProcessorsConfFiles = null;
	
	/**
	 * The number of the plain text processors.
	 */
	private int plainTextPluginCnt = 0;
	
	/**
	 * The second phase, supplement plug-ins, morpheme processors.
	 */
	private MorphemeProcessor[] morphemeProcessors = null;
	
	/**
	 * The configuration files for the morpheme processors.
	 */
	private String[] morphemeProcessorsConfFiles = null;
	
	/**
	 * The number of the morpheme processors.
	 */
	private int morphemePluginCnt = 0;
	
	/**
	 * The third phase, supplement plug-ins, pos processors.
	 */
	private PosProcessor[] posProcessors = null;
	
	/**
	 * The configuration file for the pos processors.
	 */
	private String[] posProcessorConfFiles = null;
	
	/**
	 * The number of pos processors.
	 */
	private int posPluginCnt = 0;
	
	/**
	 * It is true when the work flow is ready for analysis.
	 */
	private boolean isInitialized = false;
	
	/**
	 * The path for the base directory data and configuration files.
	 */
	private String baseDir = null;
	
	
	/* Communication Queues */
	
	/**
	 * The communication queues for the fist phase plug-ins.
	 */
	ArrayList<LinkedBlockingQueue<PlainSentence>> queuePhase1 = null;
	
	/**
	 * The communication queues for the second phase plug-ins.
	 */
	ArrayList<LinkedBlockingQueue<SetOfSentences>> queuePhase2 = null;
	
	/**
	 * The communication queues for the third phase plug-ins.
	 */
	ArrayList<LinkedBlockingQueue<Sentence>> queuePhase3 = null;
	
	/**
	 * Constructor.
	 * The maximum number of supplement plug-ins for each phase is set up with Workflow.MAX_SUPPLEMENT_PLUGIN_NUM.
	 */
	public Workflow() {
		this.maxSupplementPluginNum = MAX_SUPPLEMENT_PLUGIN_NUM;
		
		plainTextProcessors = new PlainTextProcessor[maxSupplementPluginNum];
		morphemeProcessors = new MorphemeProcessor[maxSupplementPluginNum];
		posProcessors = new PosProcessor[maxSupplementPluginNum];
		plainTextProcessorsConfFiles = new String[maxSupplementPluginNum];
		morphemeProcessorsConfFiles = new String[maxSupplementPluginNum];
		posProcessorConfFiles = new String[maxSupplementPluginNum];
		
		queuePhase1 = new ArrayList<LinkedBlockingQueue<PlainSentence>>(maxSupplementPluginNum);
		queuePhase2 = new ArrayList<LinkedBlockingQueue<SetOfSentences>>(maxSupplementPluginNum + 1);
		queuePhase3 = new ArrayList<LinkedBlockingQueue<Sentence>>(maxSupplementPluginNum + 1);
		
		threadList = new LinkedList<Thread>();
		
		isInitialized = true;
		
		this.baseDir = ".";
	}
	
	/**
	 * Constructor.
	 * The maximum number of supplement plug-ins for each phase is set up with Workflow.MAX_SUPPLEMENT_PLUGIN_NUM.
	 * @param baseDir - the path for base directory, which should have the 'conf' and 'data' directory
	 */
	public Workflow(String baseDir) {
		this.maxSupplementPluginNum = MAX_SUPPLEMENT_PLUGIN_NUM;
		
		plainTextProcessors = new PlainTextProcessor[maxSupplementPluginNum];
		morphemeProcessors = new MorphemeProcessor[maxSupplementPluginNum];
		posProcessors = new PosProcessor[maxSupplementPluginNum];
		plainTextProcessorsConfFiles = new String[maxSupplementPluginNum];
		morphemeProcessorsConfFiles = new String[maxSupplementPluginNum];
		posProcessorConfFiles = new String[maxSupplementPluginNum];
		
		queuePhase1 = new ArrayList<LinkedBlockingQueue<PlainSentence>>(maxSupplementPluginNum);
		queuePhase2 = new ArrayList<LinkedBlockingQueue<SetOfSentences>>(maxSupplementPluginNum + 1);
		queuePhase3 = new ArrayList<LinkedBlockingQueue<Sentence>>(maxSupplementPluginNum + 1);
		
		threadList = new LinkedList<Thread>();
		
		isInitialized = true;
		
		this.baseDir = baseDir;
	}
	
	/**
	 * Constructor.
	 * @param baseDir - the path for base directory, which should have the 'conf' and 'data' directory
	 * @param maxSupplementPluginNum - the maximum number of supplement plug-ins for each phase
	 */
	public Workflow(String baseDir, int maxSupplementPluginNum) {
		this.maxSupplementPluginNum = maxSupplementPluginNum;
		
		plainTextProcessors = new PlainTextProcessor[maxSupplementPluginNum];
		morphemeProcessors = new MorphemeProcessor[maxSupplementPluginNum];
		posProcessors = new PosProcessor[maxSupplementPluginNum];
		plainTextProcessorsConfFiles = new String[maxSupplementPluginNum];
		morphemeProcessorsConfFiles = new String[maxSupplementPluginNum];
		posProcessorConfFiles = new String[maxSupplementPluginNum];
		
		queuePhase1 = new ArrayList<LinkedBlockingQueue<PlainSentence>>(maxSupplementPluginNum);
		queuePhase2 = new ArrayList<LinkedBlockingQueue<SetOfSentences>>(maxSupplementPluginNum + 1);
		queuePhase3 = new ArrayList<LinkedBlockingQueue<Sentence>>(maxSupplementPluginNum + 1);
		
		threadList = new LinkedList<Thread>();
		
		isInitialized = true;
		
		this.baseDir = baseDir;
	}
	
	/** 
	 * Sets the morphological analyzer plug-in, which is the major plug-in on second phase,
	 * on the work flow.
	 * @param ma - the morphological analyzer plug-in
	 * @param configFile - the path for the configuration file (relative path to the base directory)
	 */
	public void setMorphAnalyzer(MorphAnalyzer ma, String configFile) {
		morphAnalyzer = ma;
		morphAnalyzerConfFile = baseDir + "/" + configFile;
	}
	
	/**
	 * Sets the POS tagger plug-in, which is the major plug-in on the third phase, on the work flow.
	 * @param tagger - the POS tagger plug-in
	 * @param configFile - the path for the configuration file (relative path to the base directory)
	 */
	public void setPosTagger(PosTagger tagger, String configFile) {
		posTagger = tagger;
		posTaggerConfFile = baseDir + "/" + configFile;
	}
	
	/**
	 * Appends the plain text processor plug-in, which is the supplement plug-in on the first phase, on the work flow.
	 * @param plugin - the plain text processor plug-in
	 * @param configFile - the path for the configuration file (relative path to the base directory)
	 */
	public void appendPlainTextProcessor(PlainTextProcessor plugin, String configFile) {
		plainTextProcessorsConfFiles[plainTextPluginCnt] = baseDir + "/" + configFile;
		plainTextProcessors[plainTextPluginCnt++] = plugin;
	}
	
	/**
	 * Appends the morpheme processor plug-in, which is the supplement plug-in on the second phase, on the work flow.
	 * @param plugin - the morpheme processor plug-in
	 * @param configFile - the path for the configuration file (relative path to the base directory)
	 */
	public void appendMorphemeProcessor(MorphemeProcessor plugin, String configFile) {
		morphemeProcessorsConfFiles[morphemePluginCnt] = baseDir + "/" + configFile;
		morphemeProcessors[morphemePluginCnt++] = plugin;
	}
	
	/**
	 * Appends the POS processor plug-in, which is the supplement plug-in on the third phase, on the work flow.
	 * @param plugin - the plain POS processor plug-in
	 * @param configFile - the path for the configuration file (relative path to the base directory)
	 */
	public void appendPosProcessor(PosProcessor plugin, String configFile) {
		posProcessorConfFiles[posPluginCnt] = baseDir + "/" + configFile;
		posProcessors[posPluginCnt++] = plugin;
	}
	
	/**
	 * It activates the work flow with the plug-ins that were set up. The work flow can be activated in
	 * the thread mode where each plug-in works on its own thread. It may show better performance
	 * in the machines with multi-processor.
	 * 
	 * @param threadMode - true: multi-thread mode, false: sigle thread mode
	 * @throws Exception 
	 */
	public void activateWorkflow(boolean threadMode) throws Exception {
		if (threadMode) {
			isThreadMode = true;
			
			// initialize the first phase supplement plug-ins and the communication queues
			LinkedBlockingQueue<PlainSentence> in1 = null;
			LinkedBlockingQueue<PlainSentence> out1 = new LinkedBlockingQueue<PlainSentence>();
			
			queuePhase1.add(out1);
			
			for (int i = 0; i < plainTextPluginCnt; i++) {
				in1 = out1;
				out1 = new LinkedBlockingQueue<PlainSentence>();
				queuePhase1.add(out1);
				
				plainTextProcessors[i].initialize(baseDir, plainTextProcessorsConfFiles[i]);
				threadList.add(new PlainTextProcThread(plainTextProcessors[i], in1, out1));
			}
			
			if (morphAnalyzer == null) {
				outputPhaseNum = 1;
				outputQueueNum = plainTextPluginCnt;
				runThreads();
				return;
			}
			in1 = out1;
			
			// initialize the second phase major plug-in and the communication queues
			LinkedBlockingQueue<SetOfSentences> in2 = null;
			LinkedBlockingQueue<SetOfSentences> out2 = new LinkedBlockingQueue<SetOfSentences>();
			
			queuePhase2.add(out2);
			morphAnalyzer.initialize(baseDir, morphAnalyzerConfFile);
			
			threadList.add(new MorphAnalyzerThread(morphAnalyzer, in1, out2));
			
			// initialize the second phase supplement plug-ins and the communication queues
			for (int i = 0; i < morphemePluginCnt; i++) {
				in2 = out2;
				out2 = new LinkedBlockingQueue<SetOfSentences>();
				
				queuePhase2.add(out2);
				morphemeProcessors[i].initialize(baseDir, morphemeProcessorsConfFiles[i]);
				
				threadList.add(new MorphemeProcThread(morphemeProcessors[i], in2, out2));
			}
			
			if (posTagger == null) {
				outputPhaseNum = 2;
				outputQueueNum = morphemePluginCnt;
				runThreads();
				return;
			}
			in2 = out2;
			
			// initialize the third phase major plug-in and the communication queues
			LinkedBlockingQueue<Sentence> in3 = null;
			LinkedBlockingQueue<Sentence> out3 = new LinkedBlockingQueue<Sentence>();
			
			posTagger.initialize(baseDir, posTaggerConfFile); 
			queuePhase3.add(out3);
			
			threadList.add(new PosTaggerThread(posTagger, in2, out3));
			
			// initialize the third phase supplement plug-ins and the communication queues
			for (int i = 0; i < posPluginCnt; i++) {
				in3 = out3;
				out3 = new LinkedBlockingQueue<Sentence>();
				
				queuePhase3.add(out3);
				posProcessors[i].initialize(baseDir, posProcessorConfFiles[i]);
				
				threadList.add(new PosProcThread(posProcessors[i], in3, out3));
			}
			
			outputPhaseNum = 3;
			outputQueueNum = posPluginCnt;
			runThreads();
		} else {
			isThreadMode = false;
			
			// initialize the first phase supplement plug-ins and the communication queues
			queuePhase1.add(new LinkedBlockingQueue<PlainSentence>());
			
			for (int i = 0; i < plainTextPluginCnt; i++) {
				plainTextProcessors[i].initialize(baseDir, plainTextProcessorsConfFiles[i]);
				queuePhase1.add(new LinkedBlockingQueue<PlainSentence>());
			}
			
			if (morphAnalyzer == null) {
				outputPhaseNum = 1;
				outputQueueNum = plainTextPluginCnt;
				return;
			}
			
			// initialize the second phase major plug-in and the communication queue
			morphAnalyzer.initialize(baseDir, morphAnalyzerConfFile);
			queuePhase2.add(new LinkedBlockingQueue<SetOfSentences>());
			
			// initialize the second phase supplement plug-ins and the communication queues
			for (int i = 0; i < morphemePluginCnt; i++) {
				morphemeProcessors[i].initialize(baseDir, morphemeProcessorsConfFiles[i]);
				queuePhase2.add(new LinkedBlockingQueue<SetOfSentences>());
			}
			
			if (posTagger == null) {
				outputPhaseNum = 2;
				outputQueueNum = morphemePluginCnt;
				return;
			}
			
			// initialize the third phase major plug-in and the communication queue
			posTagger.initialize(baseDir, posTaggerConfFile);
			queuePhase3.add(new LinkedBlockingQueue<Sentence>());
			
			// initialize the third phase supplement plug-in and the communication queues
			for (int i = 0; i < posPluginCnt; i++) {
				posProcessors[i].initialize(baseDir, posProcessorConfFiles[i]);
				queuePhase3.add(new LinkedBlockingQueue<Sentence>());
			}
			
			outputPhaseNum = 3;
			outputQueueNum = posPluginCnt;
		}
	}
	
	/**
	 * It starts the threads for each plug-in on the work flow, when the work flow was activated
	 * with the multi-thread mode.
	 */
	private void runThreads() {
		for (Thread th : threadList) {
			th.start();
		}
	}
	
	/**
	 * It ends the threads for each plug-in on the work flow. The shutdown() methods of each plug-in
	 * are called before they end.
	 */
	public void close() {
		if (isThreadMode) {
			for (Thread th : threadList) {
				th.interrupt();
			}
			threadList.clear();
		}
	}
	
	/**
	 * It removes the plug-ins on the work flow.
	 */
	public void clear() {
		close();
		
		if (isInitialized) {
			queuePhase1.clear();
			queuePhase2.clear();
			queuePhase3.clear();
			isThreadMode = false;
			outputPhaseNum = 0;
			outputQueueNum = 0;
			plainTextPluginCnt = 0;
			morphemePluginCnt = 0;
			posPluginCnt = 0;
			morphAnalyzer = null;
			posTagger = null;
		}
	}
	
	/**
	 * It adds the specified input text to the input queue of the work flow. After this method,
	 * you are allowed to get the analysis result by using one of the following methods:
	 * 
	 * 	- getResultOfSentence() : to get the result for one sentence at the front of result queue
	 *  - getResultOfDocument() : to get the entire result for all sentences
	 *  
	 *  If the input document is not small, getResultOfDocument() may show lower performance, and it
	 *  could be better to call getResultOfSentence() repeatedly. You need to pay attention on this.
	 *  
	 * @param document - sequence of sentences separated with newlines.
	 */
	public void analyze(String document) {
		String[] strArray = document.split("\n");
		LinkedBlockingQueue<PlainSentence> queue = queuePhase1.get(0);
		
		if (queue == null) {
			return;
		}
		
		for (int i = 0; i < strArray.length - 1; i++) {
			queue.add(new PlainSentence(0, i, false, strArray[i].trim()));
		}
		queue.add(new PlainSentence(0, strArray.length - 1, true, strArray[strArray.length - 1].trim()));
		
		if (!isThreadMode) {
			analyzeInSingleThread(); 
		}
	}
	
	/**
	 * It adds the specified input text to the input queue of the work flow. After this method,
	 * you are allowed to get the analysis result by using one of the following methods:
	 * 
	 * 	- getResultOfSentence() : to get the result for one sentence at the front of result queue
	 *  - getResultOfDocument() : to get the entire result for all sentences
	 *  
	 *  If the input document is not small, getResultOfDocument() may show lower performance, and it
	 *  could be better to call getResultOfSentence() repeatedly. You need to pay attention on this.
	 *  
	 * @param document - the path for the text file to be analyzed
	 * @throws IOException
	 */
	public void analyze(File document) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(document));
		LinkedBlockingQueue<PlainSentence> queue = queuePhase1.get(0);
		
		if (queue == null) {
			return;
		}
		
		String line = null;
		int i = 0;
		
		while ((line = br.readLine()) != null) {
			if (br.ready()) {
				queue.add(new PlainSentence(0, i++, false, line.trim()));
			} else {
				queue.add(new PlainSentence(0, i++, true, line.trim()));
				break;
			}
		}
		
		br.close();
		
		if (!isThreadMode) {
			analyzeInSingleThread(); 
		}
	}
	
	/**
	 * Returns the analysis result for one sentence at the top of the result queue. You can call this method
	 * repeatedly to get the result for remaining sentences in the input document. If there is no result,
	 * this method will be blocked until a new result comes.
	 * 
	 * It stores the specified object with the analysis result. The return type of the object depends on the
	 * analysis phase of the work flow so you must give the relevant type of parameter.
	 * 
	 * In this way, you can get the analysis result with a relevant object, so you don't need to parse the result string
	 * again. If you just want to see the result, consider to use "String getResultOfSentence()" instead.
	 * 
	 * @param <T> - One of PlainSentence (for the first phase), Sentence (for the second phase), and SetOfSentences (for the third phase).
	 * @param a - the object to get the result
	 * @return the analysis result for one sentence at front
	 * @throws ResultTypeException
	 */
	@SuppressWarnings("unchecked")
	public <T> T getResultOfSentence(T a) throws ResultTypeException {
		Object objClass = a.getClass();
		
		try {
			if (PlainSentence.class.equals(objClass)) {
				if (outputPhaseNum != 1) {
					throw new ResultTypeException(outputPhaseNum);
				}
				LinkedBlockingQueue<PlainSentence> queue = queuePhase1.get(outputQueueNum);
				a = (T)queue.take();
			} else if (SetOfSentences.class.equals(objClass)) {
				if (outputPhaseNum != 2) {
					throw new ResultTypeException(outputPhaseNum);
				}
				LinkedBlockingQueue<SetOfSentences> queue = queuePhase2.get(outputQueueNum);
				a = (T)queue.take();
			} else if (Sentence.class.equals(objClass)) {
				if (outputPhaseNum != 3) {
					throw new ResultTypeException(outputPhaseNum);
				}
				LinkedBlockingQueue<Sentence> queue = queuePhase3.get(outputQueueNum);
				a = (T)queue.take();
			} else {
				throw new ResultTypeException(outputPhaseNum);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		return a;
	}
	
	/**
	 * Returns the analysis result for one sentence at the top of the result queue. You can call this method
	 * repeatedly to get the result for remaining sentences in the input document. If there is no result,
	 * this method will be blocked until a new result comes.
	 * 
	 * It returns the sting representation of the result. If you want to reuse the result, the string should be
	 * parsed, which requires extra program codes and causes overhead. To solve this problem, consider to use 
	 * "<T> T getResultOfSentence(T a)" instead.
	 * 
	 * @return the string representation of the analysis result for one sentence at front
	 * @throws ResultTypeException
	 */
	public String getResultOfSentence() {
		String res = null;
		
		try {
			switch (outputPhaseNum) {
			case 1:
				LinkedBlockingQueue<PlainSentence> out1 = queuePhase1.get(outputQueueNum);
				res = out1.take().toString();
				break;
			case 2:
				LinkedBlockingQueue<SetOfSentences> out2 = queuePhase2.get(outputQueueNum);
				res = out2.take().toString();
				break;
			case 3:
				LinkedBlockingQueue<Sentence> out3 = queuePhase3.get(outputQueueNum);
				res = out3.take().toString();
				break;
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		return res;
	}
	
	/**
	 * Returns the analysis result list for all sentence in the result. When you use this method,
	 * you need to pay attention on the size of the data. If the size of data is big, it may show
	 * lower performance than using getResultOfSentence() repeatedly.
	 * 
	 * The return type of the object depends on the analysis phase of the work flow so you must give
	 * the relevant type of parameter. In this way, you can get the analysis result with a relevant
	 * object, so you don't need to parse the result string again. If you just want to see the result,
	 * consider to use "String getResultOfDocument()" instead.
	 * 
	 * @param <T> - One of PlainSentence (for the first phase), Sentence (for the second phase), and SetOfSentences (for the third phase).
	 * @param a - the object to specify the return type
	 * @return the list of the analysis result for all sentences in the document
	 * @throws ResultTypeException
	 */
	@SuppressWarnings("unchecked")
	public <T> LinkedList<T> getResultOfDocument(T a) throws ResultTypeException {
		Object objClass = a.getClass();
		LinkedList<T> list = new LinkedList<T>();
		
		try {
			if (PlainSentence.class.equals(objClass)) {
				if (outputPhaseNum != 1) {
					throw new ResultTypeException(outputPhaseNum);
				}
				LinkedBlockingQueue<PlainSentence> queue = queuePhase1.get(outputQueueNum);
				while (true) {
					PlainSentence ps = queue.take();
					list.add((T)ps);
					if (ps.isEndOfDocument()) {
						break;
					}
				}
			} else if (SetOfSentences.class.equals(objClass)) {
				if (outputPhaseNum != 2) {
					throw new ResultTypeException(outputPhaseNum);
				}
				LinkedBlockingQueue<SetOfSentences> queue = queuePhase2.get(outputQueueNum);
				while (true) {
					SetOfSentences sos = queue.take();
					list.add((T)sos);
					if (sos.isEndOfDocument()) {
						break;
					}
				}
			} else if (Sentence.class.equals(objClass)) {
				if (outputPhaseNum != 3) {
					throw new ResultTypeException(outputPhaseNum);
				}
				LinkedBlockingQueue<Sentence> queue = queuePhase3.get(outputQueueNum);
				while (true) {
					Sentence sent = queue.take();
					list.add((T)sent);
					if (sent.isEndOfDocument()) {
						break;
					}
				}
			} else {
				throw new ResultTypeException(outputPhaseNum);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 * Returns the analysis result list for all sentence in the result. When you use this method,
	 * you need to pay attention on the size of the data. If the size of data is big, it may show
	 * lower performance than using getResultOfSentence() repeatedly.
	 * 
	 * It returns the sting representation of the result. If you want to reuse the result, the string should be
	 * parsed, which requires extra program codes and causes overhead. To solve this problem, consider to use 
	 * "<T> LinkedList<T> getResultOfDocument(T a)" instead.
	 * 
	 * @return the list of the analysis result for all sentences in the document
	 * @throws ResultTypeException
	 */
	public String getResultOfDocument() {
		StringBuffer buf = new StringBuffer();
		
		try {
			switch (outputPhaseNum) {
			case 1:
				LinkedBlockingQueue<PlainSentence> out1 = queuePhase1.get(outputQueueNum);
				while (true) {
					PlainSentence ps = out1.take();
					buf.append(ps);
					buf.append('\n');
					
					if (ps.isEndOfDocument()) {
						break;
					}
				}
				break;
			case 2:
				LinkedBlockingQueue<SetOfSentences> out2 = queuePhase2.get(outputQueueNum);
				while (true) {
					SetOfSentences sos = out2.take();
					buf.append(sos);
					buf.append('\n');
					
					if (sos.isEndOfDocument()) {
						break;
					}
				}
				break;
			case 3:
				LinkedBlockingQueue<Sentence> out3 = queuePhase3.get(outputQueueNum);
				while (true) {
					Sentence sent = out3.take();
					buf.append(sent);
					buf.append('\n');
					
					if (sent.isEndOfDocument()) {
						break;
					}
				}
				break;
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		return buf.toString();
	}
	
	/**
	 * Analyze the text in the single thread. 
	 */
	private void analyzeInSingleThread() {
		// first phase
		if (plainTextPluginCnt == 0) {
			return;
		}
		
		LinkedBlockingQueue<PlainSentence> inQueue1 = null;
		LinkedBlockingQueue<PlainSentence> outQueue1 = null;
		PlainSentence ps = null;
		outQueue1 = queuePhase1.get(0);
		
		for (int i = 0; i < plainTextPluginCnt; i++) {
			inQueue1 = outQueue1;
			outQueue1 = queuePhase1.get(i+1);
			
			while ((ps = inQueue1.poll()) != null) {
				if ((ps = plainTextProcessors[i].doProcess(ps)) != null) {
					outQueue1.add(ps);
				}
				
				while (plainTextProcessors[i].hasRemainingData()) {
					if ((ps = plainTextProcessors[i].doProcess(null)) != null) {
						outQueue1.add(ps);
					}
				}
				
				if ((ps = plainTextProcessors[i].flush()) != null) {
					outQueue1.add(ps);
				}
			}
		}
		
		// second phase
		if (morphAnalyzer == null) {
			return;
		}
		
		LinkedBlockingQueue<SetOfSentences> inQueue2 = null;
		LinkedBlockingQueue<SetOfSentences> outQueue2 = null;
		SetOfSentences sos = null;
		inQueue1 = outQueue1;
		outQueue2 = queuePhase2.get(0);
		
		while ((ps = inQueue1.poll()) != null) {
			if ((sos = morphAnalyzer.morphAnalyze(ps)) != null) {
				outQueue2.add(sos);
			}
		}
		
		if (morphemePluginCnt == 0) {
			return;
		}
		
		for (int i = 0; i < morphemePluginCnt; i++) {
			inQueue2 = outQueue2;
			outQueue2 = queuePhase2.get(i+1);

			while ((sos = inQueue2.poll()) != null) {
				if ((sos = morphemeProcessors[i].doProcess(sos)) != null) {
					outQueue2.add(sos);
				}
			}
		}
		
		// third phase
		if (posTagger == null) {
			return;
		}
		
		LinkedBlockingQueue<Sentence> inQueue3 = null;
		LinkedBlockingQueue<Sentence> outQueue3 = null;
		Sentence sent = null;
		inQueue2 = outQueue2;
		outQueue3 = queuePhase3.get(0);
		
		while ((sos = inQueue2.poll()) != null) {
			if ((sent = posTagger.tagPOS(sos)) != null) {
				outQueue3.add(sent);
			}
		}
		
		if (posPluginCnt == 0) {
			return;
		}
		
		for (int i = 0; i < posPluginCnt; i++) {
			inQueue3 = outQueue3;
			outQueue3 = queuePhase3.get(i+1);

			while ((sent = inQueue3.poll()) != null) {
				if ((sent = posProcessors[i].doProcess(sent)) != null) {
					outQueue3.add(sent);
				}
			}
		}
	}
}
