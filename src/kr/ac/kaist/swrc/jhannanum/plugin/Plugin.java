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

package kr.ac.kaist.swrc.jhannanum.plugin;

/**
 * This interface is for plug-ins that can be set on the HanNanum work flow.
 * @author Sangwon Park (hudoni@world.kaist.ac.kr), CILab, SWRC, KAIST
 */
public interface Plugin {
	/**
	 * This method is called before the work flow starts in order to initialize the plug-in.
	 * A configuration file can be passed to the plug-in, which makes the plug-in more flexible.
	 * @param baseDir - the base directory of HanNanum files
	 * @param configFile - the path for the configuration file
	 * @throws Exception x
	 */
	abstract public void initialize(String baseDir, String configFile) throws Exception;
	
	/**
	 * This method is called before the work flow is closed.
	 */
	abstract public void shutdown();
}
