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

package kr.ac.kaist.swrc.jhannanum.demo;

import java.io.FileNotFoundException;
import java.io.IOException;

import kr.ac.kaist.swrc.jhannanum.hannanum.Workflow;
import kr.ac.kaist.swrc.jhannanum.plugin.MajorPlugin.MorphAnalyzer.ChartMorphAnalyzer.ChartMorphAnalyzer;
import kr.ac.kaist.swrc.jhannanum.plugin.MajorPlugin.PosTagger.HmmPosTagger.HMMTagger;
import kr.ac.kaist.swrc.jhannanum.plugin.SupplementPlugin.MorphemeProcessor.SimpleMAResult09.SimpleMAResult09;
import kr.ac.kaist.swrc.jhannanum.plugin.SupplementPlugin.MorphemeProcessor.SimpleMAResult22.SimpleMAResult22;
import kr.ac.kaist.swrc.jhannanum.plugin.SupplementPlugin.MorphemeProcessor.UnknownMorphProcessor.UnknownProcessor;
import kr.ac.kaist.swrc.jhannanum.plugin.SupplementPlugin.PlainTextProcessor.InformalSentenceFilter.InformalSentenceFilter;
import kr.ac.kaist.swrc.jhannanum.plugin.SupplementPlugin.PlainTextProcessor.SentenceSegmentor.SentenceSegmentor;
import kr.ac.kaist.swrc.jhannanum.plugin.SupplementPlugin.PosProcessor.SimplePOSResult09.SimplePOSResult09;
import kr.ac.kaist.swrc.jhannanum.plugin.SupplementPlugin.PosProcessor.SimplePOSResult22.SimplePOSResult22;

/**
 * This is a demo program of HanNanum that helps users to understand how to set up
 * HanNanum plug-ins manually for own purpose. It basically uses a work flow with
 * 5 plug-ins for morphological analysis and POS tagging, which is good for general
 * use, but you can test other work flows referring the commented codes. <br>
 * <br>
 * Used plug-ins: SentenceSegmentor, InformalSentenceFilter, ChartMorphAnalyzer, UnknownProcessor, and HMMTagger.<br>
 * <br>
 * It performs POS tagging for a Korean document with the following procedure:<br>
 * 		1. Create a work flow for morphological analysis and POS tagging with 5 plug-ins.<br>
 * 		2. Activate the work flow in multi-thread mode.<br>
 * 		3. Analyze a document that consists of several sentences.<br>
 * 		4. Print the result on the console.<br>
 * 		5. Repeats the procedure 3~4 with activated work flow.<br>
 * 		6. Close the work flow.<br>
 * <br>
 * @author Sangwon Park (hudoni@world.kaist.ac.kr), CILab, SWRC, KAIST
 */
public class ManualWorkflowSetUp {

	/**
	 * The main method of this demo program.
	 * @param args
	 */
	public static void main(String[] args) {
		Workflow workflow = new Workflow();
		
		try {
			/* Setting up the work flow */
			/* Phase1. Supplement Plug-in for analyzing the plain text */
			workflow.appendPlainTextProcessor(new SentenceSegmentor(), null);
			workflow.appendPlainTextProcessor(new InformalSentenceFilter(), null);
			
			/* Phase2. Morphological Analyzer Plug-in and Supplement Plug-in for post processing */
			workflow.setMorphAnalyzer(new ChartMorphAnalyzer(), "conf/plugin/MajorPlugin/MorphAnalyzer/ChartMorphAnalyzer.json");
			workflow.appendMorphemeProcessor(new UnknownProcessor(), null);
			
			/*
			 * For simpler morphological analysis result with 22 tags, decomment the following line.
			 * Notice: If you use SimpleMAResult22 plug-in, POSTagger will not work correctly.
			 *         So don't add phase3 plug-ins after SimpleMAResult22.
			 */
//			workflow.appendMorphemeProcessor(new SimpleMAResult22(), null);
			
			/*
			 * For simpler morphological analysis result with 9 tags, decomment the following line.
			 * Notice: If you use SimpleMAResult09 plug-in, POSTagger will not work correctly.
			 *         So don't add phase3 plug-ins after SimpleMAResult09.
			 */
//			workflow.appendMorphemeProcessor(new SimpleMAResult09(), null);
			
			/* Phase3. Part Of Speech Tagger Plug-in and Supplement Plug-in for post processing */
			workflow.setPosTagger(new HMMTagger(), "conf/plugin/MajorPlugin/PosTagger/HmmPosTagger.json");
			
			/* For extracting nouns only, decomment the following line. */
//			workflow.appendPosProcessor(new NounExtractor(), null);
			
			/* For simpler POS tagging result with 22 tags, decomment the following line. */
//			workflow.appendPosProcessor(new SimplePOSResult22(), null);
			
			/* For simpler POS tagging result with 9 tags, decomment the following line. */
//			workflow.appendPosProcessor(new SimplePOSResult09(), null);

			/* Activate the work flow in the thread mode */
			workflow.activateWorkflow(true);
			
			/* Analysis using the work flow */
			String document = "한나눔 형태소 분석기는 KLDP에서 제공하는 공개 소프트웨어 프로젝트 사이트에 등록되어 있다.";
			
			workflow.analyze(document);
			System.out.println(workflow.getResultOfDocument());
			
			/* Once a work flow is activated, it can be used repeatedly. */
			document = "日時: 2010년 7월 30일 오후 1시\n"
				+ "場所: Coex Conference Room\n";
			
			workflow.analyze(document);
			System.out.println(workflow.getResultOfDocument());
			
			/* Close the work flow */
			workflow.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		/* Shutdown the workflow */
		workflow.close();  	
	}
}


