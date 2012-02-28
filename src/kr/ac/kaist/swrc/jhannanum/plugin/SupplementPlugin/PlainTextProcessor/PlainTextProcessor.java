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

package kr.ac.kaist.swrc.jhannanum.plugin.SupplementPlugin.PlainTextProcessor;

import kr.ac.kaist.swrc.jhannanum.comm.PlainSentence;
import kr.ac.kaist.swrc.jhannanum.plugin.Plugin;

/**
 * The plug-in interface is for Plain Text Processor, which performs pre-processing of morphological analysis. </br>
 * </br>
 * - Phase: The First Phase </br>
 * - Type: Supplement Plug-in </br>
 * </br>
 * @author Sangwon Park (hudoni@world.kaist.ac.kr), CILab, SWRC, KAIST
 */
public interface PlainTextProcessor extends Plugin {
	/**
	 * It performs pre-processing of the plain text before the input text were delivered to
	 * the morphological analyzer.
	 * @param ps - the plain text
	 * @return the result plain sentence after processing
	 */
	abstract public PlainSentence doProcess(PlainSentence ps);
	
	/**
	 * It checks if there are some remaining text. If it returns true, the HanNanum work flow
	 * will not give more data to this plug-in by passing null for doProcess(). It's because 
	 * from the next phase the processing unit should be just one sentence. This mechanism allows
	 * the plug-in not to manage am input buffer.
	 * @return true: there are some remaining data, false: all given text were processed
	 */
	abstract public boolean hasRemainingData();
	
	/**
	 * It returns the text which has been stored in the internal buffer. This method is called
	 * by HanNanum work flow only if hasRemainingData() returns true.
	 * @return the data in the internal buffer, if the internal buffer is empty, null is returned
	 */
	abstract public PlainSentence flush();
}
