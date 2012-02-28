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

package kr.ac.kaist.swrc.jhannanum.share;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * <code>JSONReader</code> is for reading data from the configuration files for each plug-in.
 * 
 * @see <a href="http://json.org">http://json.org</a>
 * @author Sangwon Park (hudoni@world.kaist.ac.kr), CILab, SWRC, KAIST
 */
public class JSONReader {
	/** the path of the json file */
	private String filePath = null;
	
	/** json object */
	private JSONObject json = null;
	
	/**
	 * Constructor.
	 * @param filePath - the file path of the plug-in configuration file
	 * @throws JSONException
	 * @throws IOException
	 */
	public JSONReader(String filePath) throws JSONException, IOException {
		FileReader reader = new FileReader(filePath);
		StringBuffer buf = new StringBuffer();
		char[] cbuf = new char[4096];
		int idx = 0;
		while ((idx = reader.read(cbuf)) != -1) {
			buf.append(cbuf, 0, idx);
		}
		json = new JSONObject(buf.toString());
		
		reader.close();
	}
	
	/**
	 * Returns the name of the plug-in.
	 * @return the name of the plug-in
	 * @throws JSONException
	 */
	public String getName() throws JSONException {
		return json.getString("name");
	}
	
	/**
	 * Returns the version of the plug-in.
	 * @return the version of the plug-in
	 * @throws JSONException
	 */
	public String getVersion() throws JSONException {
		return json.getString("version");
	}
	
	/**
	 * Returns the author of the plug-in
	 * @return the author of the plug-in
	 * @throws JSONException
	 */
	public String getAuthor() throws JSONException {
		String res = "";
		
		JSONArray array = json.getJSONArray("author");
		JSONObject obj = null;
		for (int i = 0; i < array.length(); i++) {
			if (i > 0) {
				res += ", ";
			}
			obj = array.getJSONObject(i);
			if (!obj.getString("email").equals("null")) {
				res += obj.getString("name") + "<" + obj.getString("email") + ">";
			} else {
				res += obj.getString("name");
			}
		}
		return res;
	}
	
	/**
	 * Returns the description of the plug-in
	 * @return the description of the plug-in
	 * @throws JSONException
	 */
	public String getDescription() throws JSONException {
		return json.getString("description");
	}
	
	/**
	 * Returns the type of the plug-in
	 * @return the type of the plug-in
	 * @throws JSONException
	 */
	public String getType() throws JSONException {
		return json.getString("type");
	}
	
	/**
	 * Returns the value mapped with the specified key.
	 * @return the value mapped with the specified key
	 * @throws JSONException
	 */
	public String getValue(String key) throws JSONException {
		return json.getString(key);
	}
	
	/**
	 * Returns the file path of the configuration file.
	 * @return the file path of the configuration file
	 */
	public String getFilePath() {
		return filePath;
	}

	/**
	 * Sets the configuration file for a plug-in.
	 * @param filePath - the configuration file path
	 */
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
}
