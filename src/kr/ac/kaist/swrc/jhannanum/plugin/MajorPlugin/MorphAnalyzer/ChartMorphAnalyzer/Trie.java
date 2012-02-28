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

package kr.ac.kaist.swrc.jhannanum.plugin.MajorPlugin.MorphAnalyzer.ChartMorphAnalyzer;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.StringTokenizer;

import kr.ac.kaist.swrc.jhannanum.share.Code;
import kr.ac.kaist.swrc.jhannanum.share.TagSet;

/**
 * TRIE data structure for morpheme dictionaries.
 * @author Sangwon Park (hudoni@world.kaist.ac.kr), CILab, SWRC, KAIST
 */
public class Trie {
	/**
	 * This class is for managing free nodes in the trie structure.
	 * @author Sangwon Park (hudoni@world.kaist.ac.kr), CILab, SWRC, KAIST
	 */
	public class FREE {	
		/** the size of consecutive free nodes */
		public int size;
		
		/** the next index of free node */
		public int next_idx;
	}
	
	/**
	 * This class is for the information of morpheme
	 * @author Sangwon Park (hudoni@world.kaist.ac.kr), CILab, SWRC, KAIST
	 */
	public class INFO {
		/** morpheme tag ID */
		public int tag;
		
		/** phoneme */
		public int phoneme;
	}
	
	/**
	 * This class is for nodes of trie structure.
	 * @author Sangwon Park (hudoni@world.kaist.ac.kr), CILab, SWRC, KAIST
	 */
	public class TNODE {
		/** key - vowel, consonant, etc. */
		public char	key;
		
		/** the size of children */
		public short child_size;
		
		/** the index for the childern */
		public int child_idx;
		
		/** the list of morpheme information */
		public LinkedList<INFO> info_list = null;
		
		/** for managing free nodes */
		public FREE	free = new FREE();
	}

	/** the default buffer size for the system dictionary */
	final public static int DEFAULT_TRIE_BUF_SIZE_SYS = 1060000;

	/** the default buffer size for the user dictionary */
	final public static int DEFAULT_TRIE_BUF_SIZE_USER = 106000;
	
	/** the index of the free node */
	final public static int FREE_NODE = 0;
	
	/** the index of the start node */
	final public static int START_NODE = 1;

	/** the length of search result */
	public int search_end;

	/** the list for storing the path searched */
	public int[] search_idx = new int[256];
	
	/** the list of keys of the path searched */
	public char[] search_key = new char[256];

	/** the array for trie nodes */
	public TNODE[] trie_buf = null;

	/** the head of free node list */
	public FREE free_head = null;

	/** the head of node list */
	public TNODE node_head = null;
	
	/**
	 * Constructor.
	 * @param buf_size - the maximum number of trie nodes
	 */
	public Trie(int buf_size) {
		search_idx = new int[256];
		search_key = new char[256];
		search_end = 0;

		trie_buf = new TNODE[buf_size];
		for (int i = 0; i < buf_size; i++) {
			trie_buf[i] = new TNODE();
		}

		free_head = trie_buf[FREE_NODE].free;
		node_head = trie_buf[FREE_NODE];
		
		node_head.key = 0;
		node_head.child_size = 0;
		node_head.info_list = new LinkedList<INFO>();
		node_head.child_idx = 0;

		free_head.size = 0;
		free_head.next_idx = 1;

		// the node number 0 is not used
		trie_buf[1].free.size = buf_size - 1;
		trie_buf[1].free.next_idx = FREE_NODE;
	}

	/**
	 * Fetches the specified word.
	 * @param word - the word to fetch
	 * @return the trie node for the specified word
	 */
	public TNODE fetch(char[] word) {
		int idx;
		int x;

		x = search(word);
		if (x == 0) {
			return null;
		} else {
			idx = this.search_idx[x - 1];
			return trie_buf[idx];
		}
	}

	/**
	 * Gets the trie node on the specified index
	 * @param idx - index on the trie structure
	 * @return the trie node on the index
	 */
	public TNODE get_node(int idx) {
		return trie_buf[idx];
	}

	/**
	 * Allocates the trie nodes available as the specified size
	 * @param size - the number of nodes to allocate
	 * @return the start index of the allocated nodes, 0: failed to allocate
	 */
	public int node_alloc(int size) {
		int idx;
		int pidx;

		if (size <= 0) {
			System.err.println("node alloc: wrong size");
			return 0;
		}

		pidx = FREE_NODE;

		for (idx = free_head.next_idx; idx != FREE_NODE; idx = trie_buf[idx].free.next_idx) {
			if (trie_buf[idx].free.size >= size) {
				break;
			}
			pidx = idx;
		}

		if (idx == 0) {
			System.err.println("node alloc: no space");
			return 0;
		}

		if (pidx == FREE_NODE) {
			if (size == trie_buf[idx].free.size) {
				free_head.next_idx = trie_buf[idx].free.next_idx;
			} else {
				trie_buf[idx + size].free.size = trie_buf[idx].free.size - size;
				trie_buf[idx + size].free.next_idx = trie_buf[idx].free.next_idx;
				free_head.next_idx = idx + size;
			}
		} else {
			if(size == trie_buf[idx].free.size) {
				trie_buf[pidx].free.next_idx = trie_buf[idx].free.next_idx;
			} else {
				trie_buf[idx + size].free.size = trie_buf[idx].free.size - size;
				trie_buf[idx + size].free.next_idx = trie_buf[idx].free.next_idx;
				trie_buf[pidx].free.next_idx = idx + size;
			}
		}
		
		return idx;
	}

	/**
	 * It frees the nodes from the specified index.
	 * @param fidx - the start index of node group to free
	 * @param size - the number of nodes to free
	 */
	public void node_free(int fidx, int size)
	{
		int idx, pidx = 0;
		FREE start;

		if (size <= 0 || fidx <= FREE_NODE) {
			System.err.println("node_free: wrong parameter");
			System.exit(0);
		}

		idx = free_head.next_idx;
		if (idx == FREE_NODE) {
			// if there was no free nodes, simply updates the header pointer with the new free node
			free_head.next_idx = fidx;					// the start index of free nodes		
			trie_buf[fidx].free.size = size;			// the size of free nodes
			trie_buf[fidx].free.next_idx = FREE_NODE;	// the end of the free node list
			return;
		}

		if (fidx < idx) {
			// the new free nodes are in front compared to the existing free node list
			free_head.next_idx	= fidx;
			if (idx == fidx + size) {
				// if they are consecutive, they are merged.
				trie_buf[fidx].free.size = size + trie_buf[idx].free.size;
				trie_buf[fidx].free.next_idx = trie_buf[idx].free.next_idx;
			} else {
				// if they are not consecutive, they are separated
				trie_buf[fidx].free.size = size;
				trie_buf[fidx].free.next_idx = idx;
			}
			return;
		}
		
		// checks the location of the existing free node list and merge them
		while (idx != FREE_NODE && idx < fidx) {
			pidx = idx;
			idx = trie_buf[idx].free.next_idx;
		}
		start = trie_buf[pidx].free;

		if (fidx + size == idx) {
			// free nodes in back
			size += trie_buf[idx].free.size;
			start.next_idx = trie_buf[idx].free.next_idx;
		}

		if (pidx + start.size == fidx) {
			// free nodes in front
			start.size += size;
		} else {
			// merges the free nodes
			trie_buf[fidx].free.size = size;
			trie_buf[fidx].free.next_idx = start.next_idx;
			start.next_idx = fidx;
		}
	}

	/**
	 * It checks the children of the node on the specified index whether a child has the key.
	 * @param key - key to search 
	 * @param idx - the index of the parent node
	 * @return the index of the child node which has the key, 0: not found
	 */
	public int node_look(char key, int idx) {
		TNODE parent;

		if (idx == 1) {
			parent = node_head;
		} else {
			parent = trie_buf[idx];
		}

		for (int i = parent.child_idx; i < parent.child_idx + parent.child_size; i++) {
			if(trie_buf[i].key == key) {
				return i;
			}
		}
		return 0;
	}

	/**
	 * It writes the data in trie structure to the specified file.
	 * @param tagSet - the morpheme tag set used in the trie structure
	 */
	public void print_result(TagSet tagSet) {
		try {
			PrintWriter pw = new PrintWriter("data/kE/output.txt");
			for (int k = 0; k < node_head.child_size; k++) {
				print_trie(pw, node_head.child_idx + k, 0, tagSet);
			}
			for (int ii = free_head.next_idx; ii != 0; ii = trie_buf[ii].free.next_idx) {
				pw.print("[n:" + ii + " s:" + trie_buf[ii].free.size + "] ");
			}
			pw.println();
			pw.flush();
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * It prints the trie structure by recursive call.
	 * @param pw - for printing the trie structure
	 * @param idx - the index of trie node
	 * @param depth - the depth of current node
	 * @param tagSet - the morpheme tag set used in the trie structure
	 */
	public void print_trie(PrintWriter pw, int idx, int depth, TagSet tagSet) {
		for (int i = 0; i < depth; i++) {
			pw.print("\t");
		}
		pw.print(idx + ":" + Code.toCompatibilityJamo(trie_buf[idx].key) + " ");
		if (trie_buf[idx].info_list != null) {
			for (int k = 0; k < trie_buf[idx].info_list.size(); k++) {
				pw.print("t:" + tagSet.getTagName(trie_buf[idx].info_list.get(k).tag) + " ");
			}
		}
		pw.println();
		for (int i = 0; i < trie_buf[idx].child_size; i++) {
			print_trie(pw, trie_buf[idx].child_idx + i, depth + 1, tagSet);
		}
	}

	/**
	 * It reads the morpheme dictionary file, and initializes the trie structure.
	 * @param dictionaryFileName - the file path of the morpheme dictionary
	 * @param tagSet - the morpheme tag set
	 * @throws IOException
	 */
	public void read_dic(String dictionaryFileName, TagSet tagSet) throws IOException{
		String str = "";

		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(dictionaryFileName)));
		INFO[] info_list = new INFO[255];
		for (int i = 0; i < 255; i++) {
			info_list[i] = new INFO();
		}

		while((str = in.readLine()) != null){
			str.trim();
			if(str.equals("")){
				continue;
			}

			StringTokenizer tok	= new StringTokenizer(str, "\t ");
			String word = tok.nextToken();
			int isize = 0;

			while (tok.hasMoreTokens()) {
				String data = tok.nextToken();
				StringTokenizer tok2 = new StringTokenizer(data, ".");
				String curt = tok2.nextToken();
				int x = tagSet.getTagID(curt);
				if (x == -1) {
					System.err.println("read_dic:tag error");
					continue;
				}

				if(tok2.hasMoreTokens()){
					info_list[isize].phoneme = (short)tagSet.getIrregularID(tok2.nextToken());
				}else{
					info_list[isize].phoneme = TagSet.PHONEME_TYPE_ALL;
				}

				info_list[isize].tag = x;
				isize++;
			}
			info_list[isize].tag = 0;
			info_list[isize].phoneme = 0;

			char[] word3 = Code.toTripleArray(word);
			for(int i = 0; i < isize; i++){
				store(word3, info_list[i]);
			}
		}
	}
	
	/**
	 * It searches the specified word on the trie structure.
	 * @param word - word to search
	 * @return the length of the path searched, 0: not found
	 */
	public int search(char[] word){
		TNODE rnode;
		int child;
		short cs;
		char key;

		int widx = 0;
		int nidx = 0;
		int i = 0;

		// cache - it reuses the previous search result, if available
		for (i = 0; widx < word.length && i < this.search_end; i++) {
			if (word[i] == this.search_key[i]) {
				widx++;
			} else {
				break;
			}
		}

		this.search_end	= i;
		if (this.search_end == 0) {
			// some of data in cache is used
			cs = node_head.child_size;
			child = node_head.child_idx;
			nidx = 0;
		} else {
			// without previous search result
			child = search_idx[search_end - 1];
			cs = trie_buf[child].child_size;
			child = trie_buf[child].child_idx;
			nidx = search_idx[search_end - 1];
		}
		
		while (widx < word.length) {
			if (cs == 0) {
				return 0;
			}
			
			// checks the children of the node
			key = word[widx];
			rnode = null;
			nidx = 0;
			for (int j = child; j < child + cs; j++) {
				if (key	== trie_buf[j].key) {
					rnode = trie_buf[j];
					nidx = j;
					break;
				}
			}

			if (rnode == null) {
				// matching finished
				break;
			} else {
				// matching not finished
				search_key[search_end] = key;
				search_idx[search_end] = nidx;
				search_end++;
				widx++;
				child = trie_buf[nidx].child_idx;
				cs = trie_buf[nidx].child_size;
			}
		}
		
		if (trie_buf[nidx].info_list == null || trie_buf[nidx].info_list.size() == 0) {
			return 0;
		} else {
			return search_end;
		}
	}
	
	/**
	 * It stores the specified word in the trie structure.
	 * @param word - the word to store
	 * @param inode - the information of the word
	 * @return 0: done, -1: failed to store
	 */
	public int store(char[] word, INFO inode) {
		int child_index, new_index;
		int i, j;
		int widx;

		TNODE parent;

		if(word.length == 0) {
			return -1;
		}


		// it first searches the trie structure with the word
		search(word);
		
		// it stores the part of the word not in the structure
		widx = search_end;
		if (search_end == 0) {
			parent = node_head;
		} else {
			parent = trie_buf[search_idx[search_end - 1]];
		}

		while (widx < word.length) {
			char c = word[widx];
			short cs = parent.child_size;
			if (cs == 0) {
				// if it has no child, allocates a new child
				new_index = node_alloc(1);
				trie_buf[new_index].key = c;
				trie_buf[new_index].child_idx = 0;
				trie_buf[new_index].child_size = 0;
				parent.child_size = 1;
				parent.child_idx = new_index;
				search_idx[search_end] = new_index;
				search_key[search_end] = c;
				search_end++;
				widx++;
				parent = trie_buf[new_index];
			} else {
				// if it has more than one child, allocates (cs + 1) nodes, and copy the existing children
				new_index = node_alloc(cs + 1);
				child_index = parent.child_idx;
				for (i = 0; i < cs; i++) {
					if (trie_buf[child_index + i].key < c) {
						TNODE tmp = trie_buf[new_index + i]; 
						trie_buf[new_index + i] = trie_buf[child_index + i];
						trie_buf[child_index + i] = tmp;
					} else {
						break;
					}
				}
				trie_buf[new_index + i].key = c;
				trie_buf[new_index + i].child_idx = 0;
				trie_buf[new_index + i].child_size = 0;
				search_idx[search_end]	= new_index + i;
				search_key[search_end]	= c;
				search_end++;
				widx++;

				for(j = i; j < cs; j++){
					TNODE tmp = trie_buf[new_index + j + 1];
					trie_buf[new_index + j + 1]	= trie_buf[child_index + j];
					trie_buf[child_index + j] = tmp;
				}

				parent.child_idx = new_index;
				parent.child_size = (short)(cs + 1);

				node_free(child_index, cs);
				parent = trie_buf[new_index + i];
			}
		}

		// inserts the information to the word
		if(parent.info_list == null) {
			parent.info_list = new LinkedList<INFO>();
		}
		
		INFO in = new INFO();
		in.phoneme = inode.phoneme;
		in.tag = inode.tag;

		parent.info_list.add(in);
		
		return 0;
	}
}