/*  Copyright 2011 Heewon Jeon(madjakarta@gmail.com)

This file is part of KoNLP.

KoNLP is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

KoNLP is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with KoNLP.  If not, see <http://www.gnu.org/licenses/>   
*/
package kr.ac.kaist.swrc.jhannanum.plugin.MajorPlugin.MorphAnalyzer.ChartMorphAnalyzer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.StringTokenizer;

import kr.ac.kaist.swrc.jhannanum.comm.Eojeol;
import kr.ac.kaist.swrc.jhannanum.comm.PlainSentence;
import kr.ac.kaist.swrc.jhannanum.comm.SetOfSentences;
import kr.ac.kaist.swrc.jhannanum.plugin.MajorPlugin.MorphAnalyzer.MorphAnalyzer;
import kr.ac.kaist.swrc.jhannanum.share.JSONZipReader;
import kr.ac.kaist.swrc.jhannanum.share.TagSet;

/**
 * @author heewon jeon
 *
 */
public class KoNLPChartMorphAnalyzer implements MorphAnalyzer {
	/** Name of this plug-in. */
	final static private String PLUG_IN_NAME = "KoNLPMorphAnalyzer";

	/** Pre-analyzed dictionary. */
	private AnalyzedDic analyzedDic = null;
	
	/** Default morpheme dictionary. */
	private Trie systemDic = null;
	
	/** Additional morpheme dictionary that users can modify for their own purpose. */
	private Trie userDic = null;
	
	/** Number dictionary, which is actually a automata. */
	private NumberDic numDic = null;
	
	/** Morpheme tag set */
	private TagSet tagSet = null;
	
	/** Connection rules between morphemes. */
	private Connection connection = null;
	
	/** Impossible connection rules. */
	private ConnectionNot connectionNot = null;

	/** Lattice-style morpheme chart. */
	private MorphemeChart chart = null;
	
	/** SIMTI structure for reverse segment position. */
	private Simti simti = null;
	
	/** Eojeol list */
	private LinkedList<Eojeol> eojeolList = null;
	
	/** Post-processor to deal with some exceptions */
	private PostProcessor postProc = null;
	
	/**
	 * Returns the name of the morphological analysis plug-in.
	 * @return the name of the morphological analysis plug-in.
	 */
	public String getName() {
		return PLUG_IN_NAME;
	}
	
	/**
	 * It processes the input plain eojeol by analyzing it or searching the pre-analyzed dictionary.
	 * @param plainEojeol - plain eojeol to analyze
	 * @return the morphologically analyzed eojeol list
	 */
	private Eojeol[] processEojeol(String plainEojeol) {
		String analysis = analyzedDic.get(plainEojeol);

		eojeolList.clear();
		
		if (analysis != null) {
			// the eojeol was registered in the pre-analyzed dictionary
			StringTokenizer st = new StringTokenizer(analysis, "^");
			while (st.hasMoreTokens()) {
				String analyzed = st.nextToken();
				String[] tokens = analyzed.split("\\+|/");
				
				String[] morphemes = new String[tokens.length / 2];
				String[] tags = new String[tokens.length / 2];
				
				for (int i = 0, j = 0; i < morphemes.length; i++) {
					morphemes[i] = tokens[j++];
					tags[i] = tokens[j++];
				}
				Eojeol eojeol = new Eojeol(morphemes, tags);
				eojeolList.add(eojeol);
			}
		} else {
			// analyze the input plain eojeol
			chart.init(plainEojeol);
			chart.analyze();
			chart.getResult();
		}
		
		return eojeolList.toArray(new Eojeol[0]);
	}

	/**
	 * Analyzes the specified plain sentence, and returns all the possible analysis results.
	 * @return all the possible morphological analysis results
	 */
	public SetOfSentences morphAnalyze(PlainSentence ps) {
		StringTokenizer st = new StringTokenizer(ps.getSentence(), " \t");
		
		String plainEojeol = null;
		int eojeolNum = st.countTokens();
		
		ArrayList<String> plainEojeolArray = new ArrayList<String>(eojeolNum);
		ArrayList<Eojeol[]> eojeolSetArray = new ArrayList<Eojeol[]>(eojeolNum);
				
		while (st.hasMoreTokens()) {
			plainEojeol = st.nextToken();
			
			plainEojeolArray.add(plainEojeol);
			eojeolSetArray.add(processEojeol(plainEojeol));
		}
		
		SetOfSentences sos = new SetOfSentences(ps.getDocumentID(), ps.getSentenceID(),
				ps.isEndOfDocument(), plainEojeolArray, eojeolSetArray);

		sos = postProc.doPostProcessing(sos);

		return sos;
	}

	/**
	 * Initializes the Chart-based Morphological Analyzer plug-in.
	 * @param baseDir - the path for base directory, which should have the 'conf' and 'data' directory
	 * @param configFile - the path for the configuration file (relative path to the base directory)
	 */
	public void initialize(String baseDir, String configFile) throws Exception {
		JSONZipReader json = new JSONZipReader(baseDir, configFile);
		tagSet = new TagSet();
		tagSet.init(baseDir, json.getValue("tagset"), TagSet.TAG_SET_KAIST);

		connection = new Connection();
		connection.init(baseDir, json.getValue("connections"), tagSet.getTagCount(), tagSet);

		connectionNot = new ConnectionNot();
		connectionNot.init(baseDir, json.getValue("connections_not"), tagSet);

		analyzedDic = new AnalyzedDic();
		analyzedDic.readDic(baseDir, json.getValue("dic_analyzed"));

		systemDic = new Trie(Trie.DEFAULT_TRIE_BUF_SIZE_SYS);
		systemDic.read_dic(baseDir, json.getValue("dic_system"), tagSet);
		//fixed points
		//userDic = new Trie(Trie.DEFAULT_TRIE_BUF_SIZE_SYS);
		//userDic.read_dic(baseDir, json.getValue("dic_user"),tagSet);

		numDic = new NumberDic();
		simti = new Simti();
		simti.init();
		eojeolList = new LinkedList<Eojeol>();
		
		chart = new MorphemeChart(tagSet, connection, systemDic, null, numDic, simti, eojeolList);
		
		postProc = new PostProcessor();
	}

	/**
	 * It is called right before the work flow ends.
	 */
	public void shutdown() {

		if(tagSet != null){
			tagSet.clear();
			tagSet = null;
		}
		if(connection != null){
			connection.clear();
			connection = null;
		}
		if(connectionNot != null){
			connectionNot.clear();
			connectionNot = null;
		}
		if(analyzedDic != null){
			analyzedDic.clear();
		}
		if(systemDic != null){
			systemDic = null;
		}
		if(userDic != null){
			userDic = null;
		}
		if(numDic != null){
	
			numDic = null;
		}
		if(simti != null){
			simti = null;
		}
		if(eojeolList != null){
			eojeolList.clear();
			eojeolList = null;
		}
		if(chart != null){
			chart = null;
		}
		if(postProc != null){
			postProc = null;
		}
		
	}

	@Override
	public void loadUserDic(String path) throws IOException {
		//set same with System dic size
		userDic = new Trie(Trie.DEFAULT_TRIE_BUF_SIZE_SYS * 4);
		userDic.read_dic(path,tagSet);
		chart.setUserDic(userDic);
	}
}
