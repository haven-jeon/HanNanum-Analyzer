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

/**
 * SIMTI(SIMple Trie Index) library.
 * @author Sangwon Park (hudoni@world.kaist.ac.kr), CILab, SWRC, KAIST
 */
public class Simti {
	/**
	 * The header for SIMTI structure.
	 * @author Sangwon Park (hudoni@world.kaist.ac.kr), CILab, SWRC, KAIST
	 */
	public class HEADI {
		/**
		 * The head of the free nodes.
		 */
		ST_FREE s_free = new ST_FREE();
		
		/**
		 * The head of the data nodes.
		 */
		ST_NODE s_node = new ST_NODE();
		
		/**
		 * The number of data nodes.
		 */
		int n_size;
		
		/**
		 * The number of free nodes.
		 */
		int f_size;
	}
	
	/**
	 * The structure for the free node list.
	 * @author Sangwon Park (hudoni@world.kaist.ac.kr), CILab, SWRC, KAIST
	 */
	public class ST_FREE {
		/**
		 * The number of consecutive free nodes. It should be greater than 0.
		 */
		int size;
		
		/**
		 * The next node in the free node list. 0: end of list, otherwise: the index of the next node
		 */
		int next;
	}
	
	/**
	 * The node for SIMTI structure.
	 * @author Sangwon Park (hudoni@world.kaist.ac.kr), CILab, SWRC, KAIST
	 */
	public class ST_NF {
		/**
		 * The data of this node.
		 */
		ST_NODE node = new ST_NODE();
		
		/**
		 * For managing the free nodes in SIMTI.
		 */
		ST_FREE free = new ST_FREE();
	}
	
	/**
	 * Data node for SIMTI structure.
	 * @author Sangwon Park (hudoni@world.kaist.ac.kr), CILab, SWRC, KAIST
	 */
	public class ST_NODE {
		/**
		 * Key
		 */
		char K;
		
		/**
		 * The number of children of this node.
		 */
		byte CS;
		
		/**
		 * Information.
		 */
		int I;
		
		/**
		 * The index for the children of this node.
		 */
		int child;
	}

	/**
	 * The maximum number of the ST_NF nodes.
	 */
	final public static int ST_NF_DEFAULT = 300000;
	
	/**
	 * The maximum length of a word.
	 */
	final public static int ST_MAX_WORD = 1024;
	
	/**
	 * The end index for search_idx[].
	 */
	public int search_end;
	
	/**
	 * The word to search in the SIMTI structure.
	 */
	public char[] search_word = new char[ST_MAX_WORD];

	/**
	 * The list of indices that have been found.
	 */
	public int[] search_idx = new int[ST_MAX_WORD];

	/**
	 * The head of SIMTI structure.
	 */
	public HEADI head = null;

	/**
	 * The array of ST_NF which the actual data is stored in.
	 */
	public ST_NF[] nf = null;
	
	/**
	 * Constructor.
	 */
	public Simti() {
		head = new HEADI();
		nf = new ST_NF[ST_NF_DEFAULT];
		for (int i = 0; i < ST_NF_DEFAULT; i++) {
			nf[i] = new ST_NF();
		}
	}

	/**
	 * It allocates the available nodes and returns the first index of the list.
	 * @param size - the number of nodes that should be allocated
	 * @return the index of the first node for the allocated list, if failed 0
	 */
	public int alloc(int size) {
		int i, prev_i = 0;

		for (i = this.head.s_free.next; i != 0; i = this.nf[i].free.next) {
			if (this.nf[i].free.size >= size)
				break;
			prev_i = i;
		}

		// there is no free node
		if (i == 0) {
			System.err.format("alloc:NO FREE NODE\n");
			return 0;
		}
		
		if (prev_i == 0) {		// the head node
			if (size == this.nf[i].free.size) 
				this.head.s_free.next = this.nf[i].free.next;
			else {
				this.nf[i + size].free.size = this.nf[i].free.size - size;
				this.head.s_free.next = i + size;
				this.nf[i + size].free.next = this.nf[i].free.next;
			}
		}
		else {					// not the head node
			if (size == this.nf[i].free.size) 
				this.nf[prev_i].free.next = this.nf[i].free.next;
			else	{
				this.nf[i + size].free.size = this.nf[i].free.size - size;
				this.nf[prev_i].free.next = i + size;
				this.nf[i + size].free.next = this.nf[i].free.next;
			}
		}
		this.head.f_size -= size;
		return i;
	}
	
	/**
	 * It searches the word in the SIMTI structure, and returns the information for the word.
	 * @param word - search word
	 * @return	information for the word, if failed 0
	 */
	public int fetch(char[] word) {
		search(word);
		if (this.search_end != word.length || word.length == 0)
			return 0;
		else return this.nf[this.search_idx[this.search_end-1]].node.I;
	}
	
	/**
	 * Performs binary search.
	 * @param idx - index from the middle item
	 * @param size - size of the list
	 * @param key - key to find
	 * @return index of the item with the specified key, if failed 0 
	 */
	public int binary_search(int idx, char size, char key) {
		int left = 0, right, middle;
		ST_NODE	node;

		right = (int) size - 1;
		while (left <= right) {
			middle = (left + right) / 2;
			node = this.nf[middle + idx].node;
			if (key > node.K)
				left = middle + 1;
			else if (key < node.K)
				right = middle - 1;
			else
				return (idx + middle);
		}
		return 0;
	}

	/**
	 * Deletes the specified word in the SIMTI structure.
	 * @param word - word to delete
	 * @return	1: done successfully, -1: failed, 0: not found
	 */
	public int delete(char[] word) {
		int	i, d, j;
		int	idx, newidx;
		byte size;
		ST_NODE	temp;
		ST_NODE node = new ST_NODE();

		search(word);
		if (this.search_end < word.length || word.length == 0 )	
			return 0;	

		temp= this.nf[this.search_idx[this.search_end-1]].node;

		if (temp.I == 0)
			return 0;

		node_copy(node, temp);

		for (i = this.search_end - 1 ; i > 0 && node.CS == 0 && node.I == 0; i--) {
			this.search_end--;
			if (i == 1) {
				node_copy(node, this.head.s_node);
			} else {
				node_copy(node, this.nf[this.search_idx[i-1]].node);
			}

			if (node.CS == 1) {
				free(node.child, 1);
				node.CS = 0;
				node.child = 0;
			} else {
				idx = node.child;
				d = this.search_idx[i] - idx;	/* distance */
				size = node.CS;

				newidx = alloc(size - 1);
				for (j = 0; j < d; j++) {
					ST_NODE tmp = this.nf[newidx + j].node;
					this.nf[newidx + j].node = this.nf[idx + j].node;
					this.nf[idx + j].node = tmp;
				}
				for (j = 0; j < size - d - 1; j++) {
					ST_NODE tmp = this.nf[newidx + j].node;
					this.nf[newidx + j].node = this.nf[idx + j].node;
					this.nf[idx + j].node = tmp;
				}
				free(idx, size);
				node.CS--;
				node.child = newidx;
			}
			if (i == 1) 
				node_copy(this.head.s_node, node);
			else
				node_copy(this.nf[this.search_idx[i - 1]].node, node);
		}
		return 1;
	}

	/**
	 * It finds a word by traversing the first key from the head until it founds
	 * a node with the information.
	 * @param word - the character array to store the word found
	 * @return the length of the word found, 0: a word cannot be found in this way
	 */
	public int firstkey(char[] word) {
		int i;
		int index;
		byte cs;

		index = this.head.s_node.child;
		cs = this.head.s_node.CS;

		i=0;
		while(cs != 0) {
			word[i] = this.search_word[i] = this.nf[index].node.K;
			this.search_idx[i] = index;
			i++;
			if(this.nf[index].node.I != 0) 
				break;
			cs = this.nf[index].node.CS;
			index = this.nf[index].node.child;
		}
		word[i] = 0;
		return this.search_end = i;
	}
	
	/**
	 * Frees the nodes from the specified index.
	 * @param idx - the first index of the nodes to delete
	 * @param size - the number of nodes to delete
	 * @return 0: done successfully, -1: failed
	 */
	public int free(int idx, int size) {
		int i, prev_i = 0;
		ST_FREE	start;

		if (size <= 0)
			return -1;
		if (idx <= 0 || idx + size >= this.head.n_size)
			return -1;

		i = this.head.s_free.next;

		// no free node
		if (i == 0)	{
			this.head.s_free.next = idx;
			this.nf[idx].free.size = size;
			this.nf[idx].free.next = 0;
			return 0;
		}

		// idx is the smallest in free
		if (idx < i) {
			this.head.s_free.next = idx;
			if (i == idx + size ) 
			{
				this.nf[idx].free.size = size + this.nf[i].free.size;
				this.nf[idx].free.next = this.nf[i].free.next;
			}
			else {
				this.nf[idx].free.size = size;
				this.nf[idx].free.next = i;
			}
			this.head.f_size += size;
			return 0;
		}

		// otherwise
		while (i != 0 && i < idx) {
			prev_i = i;
			i = this.nf[i].free.next;
		}
		// prev_i != 0
		start = this.nf[prev_i].free;

		// next node is a free node
		if (idx + size == i) {		
			size += this.nf[i].free.size;
			start.next = this.nf[i].free.next;
			this.head.f_size -= this.nf[i].free.size;
		}

		if (prev_i + start.size == idx) {
			start.size += size;
		}
		else {
			this.nf[idx].free.size = size;
			this.nf[idx].free.next = start.next;
			start.next = idx;
		}
		this.head.f_size += size;
		return	0;
	}

	/**
	 * Initializes the SIMTI structure.
	 */
	public void init() {
		search_end = 0;

		head.n_size = ST_NF_DEFAULT;
		head.f_size = ST_NF_DEFAULT - 1;

		head.s_node.K = 0;
		head.s_node.CS = 0;
		head.s_node.I = 0;
		head.s_node.child = 0;

		head.s_free.size = 0;
		head.s_free.next = 1; 

		/* nf[0] is not used */
		nf[1].free.size = ST_NF_DEFAULT - 1; 
		nf[1].free.next = 0; 
	}

	/**
	 * Inserts the word to the SIMTI structure with the specified information.
	 * @param word - word to insert
	 * @param I - information to insert on the word
	 * @return	-1: fail, 0: duplicated, 1: success
	 */
	public int insert(char[] word, int I) {
		int child_index, new_index;
		int i, j, k;
		byte cs;
		ST_NODE parent;
		ST_NODE tmp_node = new ST_NODE();

		tmp_node.child = 0;
		tmp_node.CS = 0;
		tmp_node.I = 0;
		tmp_node.K = 0;

		k = 0;
		if(word.length == 0) 
			return -1;

		search(word);
		k += this.search_end;

		if (this.search_end == 0) { 
			parent = this.head.s_node;
		} else { 
			parent = this.nf[this.search_idx[this.search_end - 1]].node;
		}

		while (k < word.length) {
			cs = parent.CS;
			if (cs == 0) { 			// no child
				new_index = alloc(1);
				node_copy(this.nf[new_index].node, tmp_node);
				this.nf[new_index].node.K = word[k];
				
				parent.CS = 1;
				parent.child = new_index;
				this.search_idx[this.search_end] = new_index;
				this.search_word[this.search_end] = word[k];
				this.search_end++;
				k++;
				parent = this.nf[new_index].node;
			} else {
				new_index = alloc(cs + 1);
				child_index = parent.child;
				for (i = 0; i < cs; i++) {
					if (this.nf[child_index + i].node.K < word[k]) {
						ST_NODE node = this.nf[new_index + i].node;
						this.nf[new_index + i].node = this.nf[child_index + i].node;
						this.nf[child_index + i].node = node;
					} else { 
						break;
					}
				}

				node_copy(this.nf[new_index + i].node, tmp_node);
				this.nf[new_index + i].node.K = word[k];

				this.search_idx[this.search_end] = new_index + i;
				this.search_word[this.search_end] = word[k];
				this.search_end++;
				k++;

				for (j = i; j < cs; j++) {
					ST_NODE node = this.nf[new_index + j + 1].node;
					this.nf[new_index + j + 1].node = this.nf[child_index + j].node;
					this.nf[child_index + j].node = node;
				}
					
				parent.child = new_index;
				parent.CS = (byte)(cs + 1);
				free(child_index, cs);

				parent = this.nf[new_index + i].node; 
			}
		}
		
		if(parent.I == 0) {
			parent.I = I;
			return 1;
		} else {
			return 0;
		}
	}

	/**
	 * Compares the keys of the specified two nodes.
	 * @param a - the first node to compare
	 * @param b - the second node to compare
	 * @return a.key - b.key
	 */
	public int kcomp(ST_NODE a, ST_NODE b) {
		return (int)a.K - (int)b.K;
	}

	/**
	 * Searches the specified word in the SIMTI structure, and stores the information found.
	 * @param word - word to search
	 * @param I_buffer - array to store the information found
	 * @return the number of words found, 0: no words were found
	 */
	public int lookup(char[] word, int[] I_buffer) {
		int	i;
		if (search(word) == 0) {
			return 0;
		}
		else {
			for (i = 0; i < this.search_end; i++) {
				I_buffer[i] = this.nf[this.search_idx[i]].node.I;
			}
		}
		return this.search_end;
	}
	
	/**
	 * It finds a word by traversing the children, siblings, and parent of the last node
	 * of the previous search.
	 * @param word - found word
	 * @return the length of the word found, 0: no word can be found in this way
	 */
	public int nextkey(char[] word) {
		int i;
		int index;
		byte cs;
		ST_NODE parent;

		if (this.search_end <= 0) 
			return 0;
		for (i = 0; i < this.search_end; i++) 
			word[i] = this.search_word[i];

		// i equals with the search_end
		index = this.search_idx[i - 1]; 		
		if (i == 1)
			parent = this.head.s_node;
		else
			parent = this.nf[this.search_idx[i - 2]].node;

		cs = this.nf[index].node.CS;

		// parent -. index -. child
		//          sibling        
		while (i > 0) {
			if (cs != 0) { 			// there is a child
				parent = this.nf[index].node;
				index = this.nf[index].node.child;
				cs = this.nf[index].node.CS;

				word[i] = this.search_word[i] = this.nf[index].node.K;
				this.search_idx[i] = index;
				i++;
				if (this.nf[index].node.I != 0) { 
					break;
				}
			} else if ( index < parent.child + parent.CS - 1) { 	// there is a sibling
				index++;
				cs = this.nf[index].node.CS;

				word[i - 1] = this.search_word[i - 1] = this.nf[index].node.K;
				this.search_idx[i - 1] = index;
				if (this.nf[index].node.I != 0) { 
					break;
				}
			} else { 	// there is no child and sibling
				i--;
				if (i <= 0) {
					i = 0;
					break;
				}
				index = this.search_idx[i - 1];
				if (i == 1) {
					parent = this.head.s_node;
				}
				else {
					parent = this.nf[this.search_idx[i - 2]].node;
				}
				cs = 0;
			}
		}
		word[i] = 0;
		return this.search_end = i;
	}
	
	/**
	 * Copies a node.
	 * @param n1 - destination node to copy
	 * @param n2 - source node to copy
	 */
	private void node_copy(ST_NODE n1, ST_NODE n2) {
		n1.child = n2.child;
		n1.CS = n2.CS;
		n1.I = n2.I;
		n1.K = n2.K;
	}

	/**
	 * Replaces the information for the specified word.
	 * @param word - the word to change its information
	 * @param I - new information for the word
	 * @return	1: done successfully, -1: failed
	 */
	public int replace(char[] word, short I) {
		int i = 0;

		if(word.length == 0) 
			return -1;

		search(word);
		i += this.search_end;

		if(this.search_end == 0 || i < word.length) 
			return -1;
		else 
		{
			this.nf[this.search_idx[this.search_end - 1]].node.I = I;
			return 1;
		}
	}

	/**
	 * Searches the specified word in the SIMTI structure.
	 * @param word - word to search
	 * @return the number of words found, 0: not found
	 */
	public int search(char[] word)	{
		int i, j, k;
		ST_NODE tmpnode = new ST_NODE();
		ST_NODE rnode = null;
		int child;
		byte cs;

		for (i = 0, j = 0; j < word.length && i < this.search_end; i++) {
			if (word[j] == this.search_word[i])
				j++;
			else break;
		}

		this.search_end = i;
		if (this.search_end == 0 ) {
			cs = this.head.s_node.CS;
			child = this.head.s_node.child;
		} else {
			child = this.search_idx[this.search_end-1];
			cs = this.nf[child].node.CS;
			child = this.nf[child].node.child;
		}
		while (j < word.length && cs != 0) {
			tmpnode.K=word[j];
			rnode = null;

			for (k = child; k < child + cs; k++){
				if (tmpnode.K == this.nf[k].node.K){
					rnode = this.nf[k].node;
					break;
				}
			}

			if (rnode == null) break;
			else {
				this.search_word[this.search_end] = word[j];
				this.search_idx[this.search_end] = k;
				this.search_end++;
				j++;
				child = this.nf[k].node.child;
				cs = this.nf[k].node.CS;
			}
		}
		return this.search_end;
	}
}
