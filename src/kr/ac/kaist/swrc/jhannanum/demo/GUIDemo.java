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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import kr.ac.kaist.swrc.jhannanum.hannanum.Workflow;
import kr.ac.kaist.swrc.jhannanum.plugin.MajorPlugin.MorphAnalyzer.ChartMorphAnalyzer.ChartMorphAnalyzer;
import kr.ac.kaist.swrc.jhannanum.plugin.MajorPlugin.PosTagger.HmmPosTagger.HMMTagger;
import kr.ac.kaist.swrc.jhannanum.plugin.SupplementPlugin.MorphemeProcessor.SimpleMAResult09.SimpleMAResult09;
import kr.ac.kaist.swrc.jhannanum.plugin.SupplementPlugin.MorphemeProcessor.SimpleMAResult22.SimpleMAResult22;
import kr.ac.kaist.swrc.jhannanum.plugin.SupplementPlugin.MorphemeProcessor.UnknownMorphProcessor.UnknownProcessor;
import kr.ac.kaist.swrc.jhannanum.plugin.SupplementPlugin.PlainTextProcessor.InformalSentenceFilter.InformalSentenceFilter;
import kr.ac.kaist.swrc.jhannanum.plugin.SupplementPlugin.PlainTextProcessor.SentenceSegmentor.SentenceSegmentor;
import kr.ac.kaist.swrc.jhannanum.plugin.SupplementPlugin.PosProcessor.NounExtractor.NounExtractor;
import kr.ac.kaist.swrc.jhannanum.plugin.SupplementPlugin.PosProcessor.SimplePOSResult09.SimplePOSResult09;
import kr.ac.kaist.swrc.jhannanum.plugin.SupplementPlugin.PosProcessor.SimplePOSResult22.SimplePOSResult22;
import kr.ac.kaist.swrc.jhannanum.share.JSONReader;

import org.json.JSONException;

/**
 * This is a GUI-based demo program of the HanNanum that helps users to understand the concept
 * of work flow and use the HanNanum library easily. It shows a window that has components for plug-in pool,
 * work flow, plug-in information, work flow controls, input text and analysis result. Users can easily
 * set up various work flows by drag-and-drop the plug-ins from the plug-in pool to the work flow component,
 * and see the analysis result. <br>
 * <br>
 * Users can use this GUI demo program by following procedure:<br>
 * 		1. Browse the plug-ins in the plug-in pool. The brief information about the selected plug-in will be
 *         displayed so that you can refer the information when set the work flow up.<br>
 *      2. Drag and drop plug-ins from the plug-in pool to the list in the work flow considering the phase
 *         and type of the plug-in.<br>
 *         - If you want to remove the plug-in on the work flow, simply double click it.<br>
 *         - If you change the order of the supplement plug-ins in each phase, simply drag and drop a plug-in
 *           to the position you want.<br>
 *      3. Once you finish to set the work flow up, choose 'Multi-thread Mode' or 'Single-thread Mode'.<br>
 *      4. Click 'Activate the work flow' button.<br>
 *      5. Type or copy-and-past text that you want to analyze into the 'Input Text' area.<br>
 *      6. Click 'Analyze Text' button.<br>
 *      7. You can see the analysis result on 'Result' area.<br>
 *      8. Repeat 5~7 to analyze other text with the activated work flow.<br>
 *         Or click 'Close the work flow' button and go to step 1 to set up new work flow.<br>
 * <br>
 * @author Sangwon Park (hudoni@world.kaist.ac.kr), CILab, SWRC, KAIST
 */
public class GUIDemo {
	private JFrame mainFrame = 	null;
	private JMenuBar menuBar = null;
	private JMenu menuFile = null;
	private JMenu menuHelp = null;
	private JMenuItem menuItemFileOpen = null;
	private JMenuItem menuItemHelp = null;
    private JTree tree;
    private HashMap<String,String> pluginInfoMap = null;

    private JList listPluginMajor2= null;
    private JList listPluginMajor3= null;
    private JList listPluginSupplement1 = null;
    private JList listPluginSupplement2 = null;
    private JList listPluginSupplement3 = null;
    
    private DefaultListModel listModelMajor2 = null;
    private DefaultListModel listModelMajor3 = null;
    private DefaultListModel listModelSupplement1 = null;
    private DefaultListModel listModelSupplement2 = null;
    private DefaultListModel listModelSupplement3 = null;
    
    private JTextArea areaPluginInfo = null;
    
    private JTextArea inputTextArea = null;
    private JTextArea outputTextArea = null;
    
    private JSplitPane splitPaneTop = null;
    private JSplitPane splitPaneBottom = null;
    
    private JRadioButton radioMultiThread = null;
    private JRadioButton radioSingleThread = null;
     	
    private JButton buttonActivate = null;
    private JButton buttonAnalysis = null;
    private JButton buttonReset = null;
    
    private PluginInfo selectedPlugin = null;
    private PluginInfo tempPlugin = null;
    
    private boolean multiThreadMode = true;
    private boolean activated = false;
    
    private Workflow workflow = null;

	
	/**
	 * Run this demo program.
	 * @param args
	 */
	public static void main(String[] args) {
		GUIDemo demo = new GUIDemo();
		demo.run();
	}
	
	/**
	 * Sets the GUI up and launch the demo.
	 */
	public void run() {
		///////////////////////////////////////////////////////////////////
		// Basic setting for the mainFrame												
		///////////////////////////////////////////////////////////////////
		mainFrame = new JFrame();
		
		Toolkit kit = mainFrame.getToolkit();
		Dimension windowSize = kit.getScreenSize();
		
		mainFrame.setBounds(windowSize.width / 20, windowSize.height / 20,
				windowSize.width * 18 / 20, windowSize.height * 18 / 20);
		mainFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		mainFrame.setTitle("HanNanum Korean Morphological Analyzer - A Plug-in Component based System (GUI Demo)");
		
		Font font = new Font("MonoSpaced", Font.PLAIN, 12);
		UIManager.put("TextArea.font", font);
		
		///////////////////////////////////////////////////////////////////
		// Layout setting for the mainFrame						
		///////////////////////////////////////////////////////////////////
		mainFrame.setLayout(new BorderLayout());
		mainFrame.getContentPane().add(createPaneCenter(), BorderLayout.CENTER);
		mainFrame.getContentPane().add(createPaneNorth(), BorderLayout.NORTH);
		
		///////////////////////////////////////////////////////////////////
		// Menu Setting											
		///////////////////////////////////////////////////////////////////		
		menuBar = new JMenuBar();
		menuFile = new JMenu("File");
		menuItemFileOpen = new JMenuItem("Open", KeyEvent.VK_O);
		menuHelp = new JMenu("Help");
		menuItemHelp = new JMenuItem("Help", KeyEvent.VK_H);
		
		menuItemFileOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.ALT_MASK));
		menuItemHelp.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, ActionEvent.ALT_MASK));
		
		menuBar.add(menuFile);
		menuBar.add(menuHelp);
		menuFile.add(menuItemFileOpen);
		menuHelp.add(menuItemHelp);
		mainFrame.setJMenuBar(menuBar);
		
		///////////////////////////////////////////////////////////////////
		// Event Handler Setting											
		///////////////////////////////////////////////////////////////////
		menuItemFileOpen.addActionListener(new SharedActionHandler());
		menuItemHelp.addActionListener(new SharedActionHandler());
		buttonActivate.addActionListener(new SharedActionHandler());
		buttonAnalysis.addActionListener(new SharedActionHandler());
		buttonReset.addActionListener(new SharedActionHandler());
		radioMultiThread.addActionListener(new SharedActionHandler());
		radioSingleThread.addActionListener(new SharedActionHandler());
		
		listPluginMajor2.addMouseListener(new PluginListMouseListener(listPluginMajor2, listModelMajor2));
		listPluginMajor3.addMouseListener(new PluginListMouseListener(listPluginMajor3, listModelMajor3));
		listPluginSupplement1.addMouseListener(new PluginListMouseListener(listPluginSupplement1, listModelSupplement1));
		listPluginSupplement2.addMouseListener(new PluginListMouseListener(listPluginSupplement2, listModelSupplement2));
		listPluginSupplement3.addMouseListener(new PluginListMouseListener(listPluginSupplement3, listModelSupplement3));
		
		listPluginMajor2.setTransferHandler(new PluginTransferHandler(PluginInfo.PHASE2, PluginInfo.MAJOR));
		listPluginMajor3.setTransferHandler(new PluginTransferHandler(PluginInfo.PHASE3, PluginInfo.MAJOR));
		listPluginSupplement1.setTransferHandler(new PluginTransferHandler(PluginInfo.PHASE1, PluginInfo.SUPPLEMENT));
		listPluginSupplement2.setTransferHandler(new PluginTransferHandler(PluginInfo.PHASE2, PluginInfo.SUPPLEMENT));
		listPluginSupplement3.setTransferHandler(new PluginTransferHandler(PluginInfo.PHASE3, PluginInfo.SUPPLEMENT));
		
		listPluginSupplement1.setDropMode(DropMode.ON_OR_INSERT);
		listPluginSupplement2.setDropMode(DropMode.ON_OR_INSERT);
		listPluginSupplement3.setDropMode(DropMode.ON_OR_INSERT);
		listPluginMajor2.setDropMode(DropMode.ON_OR_INSERT);
		listPluginMajor3.setDropMode(DropMode.ON_OR_INSERT);
		
		listPluginMajor2.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listPluginMajor3.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listPluginSupplement1.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listPluginSupplement2.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listPluginSupplement3.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		listPluginMajor2.setDragEnabled(true);
		listPluginMajor3.setDragEnabled(true);
		listPluginSupplement1.setDragEnabled(true);
		listPluginSupplement2.setDragEnabled(true);
		listPluginSupplement3.setDragEnabled(true);
		
		tree.setDragEnabled(true);
		tempPlugin = new PluginInfo("", 0, 0);
		
		workflow = new Workflow();
			
		// Show the main frame on the screen
		mainFrame.setVisible(true);
		
		for (int i = 0; i < tree.getRowCount(); i++) {
	         tree.expandRow(i);
		}
		
		splitPaneTop.setDividerLocation(0.3);
		splitPaneBottom.setDividerLocation(0.5);
	}
	
	/**
	 * Setting of the split panel for plug-in pool and the work flow.
	 * @return the split panel
	 */
	private JComponent createPaneNorth() {
        splitPaneTop = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPaneTop.setLeftComponent(createPluginPool());
        splitPaneTop.setRightComponent(createWorkflow());
        splitPaneTop.setOneTouchExpandable(true);

		return splitPaneTop;
	}
	
	/**
	 * Setting of the plug-in tree view.
	 * @return the plug-in tree view
	 */
	private JComponent createPluginPool() {
        DefaultMutableTreeNode top = new DefaultMutableTreeNode("HanNanum Plug-in Pool");
        createPluginNodes(top);
        loadPluginInformation();

        tree = new JTree(top);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        
        tree.addTreeSelectionListener(new PluginTreeSelectionListener());

        tree.putClientProperty("JTree.lineStyle", "Horizontal");
        

        JScrollPane treeView = new JScrollPane(tree);

        return treeView;
	}
	
	/**
	 * Setting of the each plug-in which is in the tree view.
	 * @param top - default top tree node
	 */
    private void createPluginNodes(DefaultMutableTreeNode top) {
        DefaultMutableTreeNode phase = null;
        DefaultMutableTreeNode type = null;

        phase = new DefaultMutableTreeNode("Phase1 Plug-in. Plain Text Processing");
        type = new DefaultMutableTreeNode("Supplement Plugin");
        type.add(new DefaultMutableTreeNode(new PluginInfo("InformalSentenceFilter", PluginInfo.PHASE1, PluginInfo.SUPPLEMENT)));
        type.add(new DefaultMutableTreeNode(new PluginInfo("SentenceSegmentor", PluginInfo.PHASE1, PluginInfo.SUPPLEMENT)));
        phase.add(type);
        top.add(phase);
        
        phase = new DefaultMutableTreeNode("Phase2 Plug-in. Morphological Analysis");
        type = new DefaultMutableTreeNode("Major Plug-in");
        type.add(new DefaultMutableTreeNode(new PluginInfo("ChartMorphAnalyzer", PluginInfo.PHASE2, PluginInfo.MAJOR)));
        phase.add(type);
        type = new DefaultMutableTreeNode("Supplement Plug-in");
        type.add(new DefaultMutableTreeNode(new PluginInfo("UnknownMorphProcessor", PluginInfo.PHASE2, PluginInfo.SUPPLEMENT)));
        type.add(new DefaultMutableTreeNode(new PluginInfo("SimpleMAResult09", PluginInfo.PHASE2, PluginInfo.SUPPLEMENT)));
        type.add(new DefaultMutableTreeNode(new PluginInfo("SimpleMAResult22", PluginInfo.PHASE2, PluginInfo.SUPPLEMENT)));
        
        phase.add(type);
        top.add(phase);
        
        phase = new DefaultMutableTreeNode("Phase3 Plug-in. Part Of Speech Tagging");
        type = new DefaultMutableTreeNode("Major Plug-in");
        type.add(new DefaultMutableTreeNode(new PluginInfo("HmmPosTagger", PluginInfo.PHASE3, PluginInfo.MAJOR)));
        phase.add(type);
        type = new DefaultMutableTreeNode("Supplement Plug-in");
        type.add(new DefaultMutableTreeNode(new PluginInfo("NounExtractor", PluginInfo.PHASE3, PluginInfo.SUPPLEMENT)));
        type.add(new DefaultMutableTreeNode(new PluginInfo("SimplePOSResult09", PluginInfo.PHASE3, PluginInfo.SUPPLEMENT)));
        type.add(new DefaultMutableTreeNode(new PluginInfo("SimplePOSResult22", PluginInfo.PHASE3, PluginInfo.SUPPLEMENT)));
        phase.add(type);
        top.add(phase);
    }
    
    /**
     * It loads the meta information for each plug-in from the configuration files.
     */
    private void loadPluginInformation() {
    	try {
			pluginInfoMap = new HashMap<String, String>();
			pluginInfoMap.put("InformalSentenceFilter", getPluginAbstarct("conf/plugin/SupplementPlugin/PlainTextProcessor/InformalSentenceFilter.json"));
			pluginInfoMap.put("SentenceSegmentor", getPluginAbstarct("conf/plugin/SupplementPlugin/PlainTextProcessor/SentenceSegmentor.json"));
			pluginInfoMap.put("ChartMorphAnalyzer", getPluginAbstarct("conf/plugin/MajorPlugin/MorphAnalyzer/ChartMorphAnalyzer.json"));
			pluginInfoMap.put("UnknownMorphProcessor", getPluginAbstarct("conf/plugin/SupplementPlugin/MorphemeProcessor/UnknownMorphProcessor.json"));
			pluginInfoMap.put("SimpleMAResult09", getPluginAbstarct("conf/plugin/SupplementPlugin/MorphemeProcessor/SimpleMAResult09.json"));
			pluginInfoMap.put("SimpleMAResult22", getPluginAbstarct("conf/plugin/SupplementPlugin/MorphemeProcessor/SimpleMAResult22.json"));
			pluginInfoMap.put("HmmPosTagger", getPluginAbstarct("conf/plugin/MajorPlugin/PosTagger/HmmPosTagger.json"));
			pluginInfoMap.put("NounExtractor", getPluginAbstarct("conf/plugin/SupplementPlugin/PosProcessor/NounExtractor.json"));
			pluginInfoMap.put("SimplePOSResult09", getPluginAbstarct("conf/plugin/SupplementPlugin/PosProcessor/SimplePOSResult09.json"));
			pluginInfoMap.put("SimplePOSResult22", getPluginAbstarct("conf/plugin/SupplementPlugin/PosProcessor/SimplePOSResult22.json"));
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    /**
     * Returns the abstract text for a plug-in from the specified configuration file.
     * @param filePath - the configuration file for the plug-in written in JSON format
     * @return the abstract text for the specified plug-in
     * @throws JSONException
     * @throws IOException
     */
    private String getPluginAbstarct(String filePath) throws JSONException, IOException {
    	JSONReader json = new JSONReader(filePath);
        String res = null;
        
        res = String.format("* Name: %s\n* Type: %s\n* Version: %s\n* Author: %s\n* Description: %s\n",
        		json.getName(), json.getType(), json.getVersion(), json.getAuthor(), json.getDescription());
        return res;
    }
    
    /**
     * This class has the properties of a HanNanum plug-in.
     * 
     * @author Sangwon Park (hudoni@world.kaist.ac.kr), CILab, SWRC, KAIST
     */
    private class PluginInfo {
    	/**
    	 * A plug-in for the first phase on the work flow.
    	 */
    	public static final int PHASE1 = 1;
    	
    	/**
    	 * A plug-in for the second phase one the work flow.
    	 */
    	public static final int PHASE2 = 2;
    	
    	/**
    	 * A plug-in for the third phase on the work flow.
    	 */
    	public static final int PHASE3 = 3;
    	
    	/**
    	 * A plug-in that is a major.
    	 */
    	public static final int MAJOR = 1;
    	
    	/**
    	 * A plug-in that is a supplement.
    	 */
    	public static final int SUPPLEMENT = 2;
    	
    	/**
    	 * The name of the plug-in.
    	 */
        public String name = null;
        
        /**
         * The phase of the plug-in on the work flow. PHASE1 or PHASE2 or PHASE3.
         */
        public int phase = 0;
        
        /**
         * The type of the plug-in. MAJOR or SUPPLEMENT.
         */
        public int type = 0;

        /**
         * Constructor.
         * @param name - the name of the plug-in
         * @param phase - the phase of the plug-in on the work flow. PHASE1 or PHASE2 or PHASE3.
         * @param type - the type of the plug-in. MAJOR or SUPPLEMENT.
         */
        public PluginInfo(String name, int phase, int type) {
        	this.name = name;
        	this.phase = phase;
        	this.type = type;
        }

        /**
         * Returns the name of the plug-in.
         */
        public String toString() {
            return name;
        }
    }
    
    /**
     * Event listener for the plug-in selection on the tree view.
     * 
     * @author Sangwon Park (hudoni@world.kaist.ac.kr), CILab, SWRC, KAIST
     */
    private class PluginTreeSelectionListener implements TreeSelectionListener {
    	/**
    	 * It is called when the new plug-in was selected.
    	 */
		public void valueChanged(TreeSelectionEvent e) {
	        DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();

			if (node == null) {
				selectedPlugin = null;
				return;
			}
			
			Object nodeInfo = node.getUserObject();
			
			if (node.isLeaf()) {
				selectedPlugin = (PluginInfo)nodeInfo;
				areaPluginInfo.setText(pluginInfoMap.get((selectedPlugin.name)));
			} else {
				selectedPlugin = null;
			}
		}
    }
    
    /**
     * Event listener for the plug-in selection on the list for the work flow.
     * 
     * @author Sangwon Park (hudoni@world.kaist.ac.kr), CILab, SWRC, KAIST
     */
    private class PluginListMouseListener implements MouseListener {
    	/**
    	 * The plug-in list where this listener is going to work.
    	 */
    	private JList list = null;
    	
    	/**
    	 * The model of plug-in list where this listener is going to work.
    	 */
    	private DefaultListModel listModel = null;
    	
    	/**
    	 * Constructor.
    	 * @param list - the plug-in list for the work flow
    	 * @param listModel - the model for the plug-in list
    	 */
    	public PluginListMouseListener(JList list, DefaultListModel listModel) {
    		this.list = list;
    		this.listModel = listModel;
    	}
    	
    	/**
    	 * It is called when an mouse click event occur on the list.
    	 */
		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() == 2) {
				listModel.remove(this.list.locationToIndex(e.getPoint()));
			} else {
				try {
					areaPluginInfo.setText(pluginInfoMap.get((String)listModel.get(this.list.locationToIndex(e.getPoint()))));
				} catch (Exception e1) {
				}
			}
		}

		/**
    	 * It is called when an mouse enter event occur on the list.
    	 */
		@Override
		public void mouseEntered(MouseEvent e) {
		}

		/**
    	 * It is called when an mouse exited event occur on the list.
    	 */
		@Override
		public void mouseExited(MouseEvent e) {
		}

		/**
    	 * It is called when an mouse pressed event occur on the list.
    	 */
		@Override
		public void mousePressed(MouseEvent e) {
		}

		/**
    	 * It is called when an mouse released event occur on the list.
    	 */
		@Override
		public void mouseReleased(MouseEvent e) {
		}
    }
    
    /**
     * Returns the panel for the work flow.
     * 
     * @return the panel for the work flow
     */
	private JComponent createWorkflow() {
		JPanel workflowPanel = new JPanel(new GridLayout(1, 3));
		
	    listModelMajor2 = new DefaultListModel();
	    listModelMajor3 = new DefaultListModel();
	    listModelSupplement1 = new DefaultListModel();
	    listModelSupplement2 = new DefaultListModel();
	    listModelSupplement3 = new DefaultListModel();
	    
	    listPluginMajor2 = new JList(listModelMajor2);
	    listPluginMajor3 = new JList(listModelMajor3);
		listPluginSupplement1 = new JList(listModelSupplement1);
		listPluginSupplement2 = new JList(listModelSupplement2);
		listPluginSupplement3 = new JList(listModelSupplement3);

		// phase1
		JPanel phasePanel = new JPanel(new GridLayout(1,1));
		phasePanel.setBorder(BorderFactory.createTitledBorder("Phase1. Plain Text Processing"));
		JPanel listPanel = new JPanel(new GridLayout(1,1));
		listPanel.setBorder(BorderFactory.createTitledBorder("Supplement Plug-in"));
		JScrollPane scroll = new JScrollPane();
		scroll.setViewportView(listPluginSupplement1);
		listPanel.add(scroll);
		phasePanel.add(listPanel);
		workflowPanel.add(phasePanel);
		
		// phase2
		phasePanel = new JPanel(new GridLayout(1,2));
		phasePanel.setBorder(BorderFactory.createTitledBorder("Phase2. Morphological Analysis"));
		listPanel = new JPanel(new GridLayout(1,1));
		listPanel.setBorder(BorderFactory.createTitledBorder("Major Plug-in"));
		scroll = new JScrollPane();
		scroll.setViewportView(listPluginMajor2);
		listPanel.add(scroll);
		phasePanel.add(listPanel);
		
		listPanel = new JPanel(new GridLayout(1,1));
		listPanel.setBorder(BorderFactory.createTitledBorder("Supplement Plug-in"));
		scroll = new JScrollPane();
		scroll.setViewportView(listPluginSupplement2);
		listPanel.add(scroll);
		phasePanel.add(listPanel);
		
		workflowPanel.add(phasePanel);		
		
		// phase3
		phasePanel = new JPanel(new GridLayout(1,2));
		phasePanel.setBorder(BorderFactory.createTitledBorder("Phase3. Part Of Speech Tagging"));
		listPanel = new JPanel(new GridLayout(1,1));
		listPanel.setBorder(BorderFactory.createTitledBorder("Major Plug-in"));
		scroll = new JScrollPane();
		scroll.setViewportView(listPluginMajor3);
		listPanel.add(scroll);
		phasePanel.add(listPanel);
		
		listPanel = new JPanel(new GridLayout(1,1));
		listPanel.setBorder(BorderFactory.createTitledBorder("Supplement Plug-in"));
		scroll = new JScrollPane();
		scroll.setViewportView(listPluginSupplement3);
		listPanel.add(scroll);
		phasePanel.add(listPanel);
		
		workflowPanel.add(phasePanel);
		
		/////////////////////////////////////////////////////////////////////////////////////
		
		JPanel controlPanel = new JPanel(new GridLayout(4,1));
		controlPanel.setBorder(BorderFactory.createTitledBorder("Workflow Control"));
		buttonActivate = new JButton("Activate the workflow");
		buttonAnalysis = new JButton("Analyze Text");
		buttonReset = new JButton("Close the workflow");
		
		JPanel threadPanel = new JPanel(new GridLayout(1,1));
		radioMultiThread = new JRadioButton("Mutli-thread Mode", true);
		radioSingleThread = new JRadioButton("Single-thread Mode", false);
		threadPanel.setBorder(BorderFactory.createTitledBorder("Thread Mode"));
		threadPanel.add(radioMultiThread);
		threadPanel.add(radioSingleThread);
		ButtonGroup groupThread = new ButtonGroup();
		groupThread.add(radioMultiThread);
		groupThread.add(radioSingleThread);
		
		controlPanel.add(threadPanel);
		controlPanel.add(buttonActivate);
		controlPanel.add(buttonAnalysis);
		controlPanel.add(buttonReset);
		
		buttonAnalysis.setEnabled(false);
		buttonReset.setEnabled(false);
		
		/////////////////////////////////////////////////////////////////////////////////////
		
		JPanel pluginInfoPanel = new JPanel(new GridLayout(1,1));
		pluginInfoPanel.setBorder(BorderFactory.createTitledBorder("Plug-in Information"));
		areaPluginInfo = new JTextArea();
		areaPluginInfo.setLineWrap(true);
		scroll = new JScrollPane();
		scroll.setViewportView(areaPluginInfo);
		pluginInfoPanel.add(scroll);
		
		JPanel infoPanel = new JPanel(new GridLayout(1,2));
		infoPanel.add(pluginInfoPanel);
		infoPanel.add(controlPanel);
		
		JPanel panel = new JPanel(new GridLayout(2, 1));
		workflowPanel.setBorder(BorderFactory.createTitledBorder("HanNanum Workflow"));
		panel.add(workflowPanel);
		panel.add(infoPanel);

        return panel;
	}

	/**
	 * Returns a panel for the input and output text areas.
	 * 
	 * @return a panel for the input and output text areas
	 */
	private JComponent createPaneCenter() {
		splitPaneBottom = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		JPanel panel = new JPanel(new GridLayout(1,1));
		panel.setBorder(BorderFactory.createTitledBorder("Input Text"));
		inputTextArea = new JTextArea();
		JScrollPane scroll = new JScrollPane();
		scroll.setViewportView(inputTextArea);
		panel.add(scroll);
		splitPaneBottom.setLeftComponent(panel);
	    
	    panel = new JPanel(new GridLayout(1,1));
		panel.setBorder(BorderFactory.createTitledBorder("Result"));
		outputTextArea = new JTextArea();
		scroll = new JScrollPane();
		scroll.setViewportView(outputTextArea);
		panel.add(scroll);
		splitPaneBottom.setRightComponent(panel);
		
		splitPaneBottom.setOneTouchExpandable(true);
	    
		return splitPaneBottom;
	}
	
	/**
	 * The event handler that is used in various purpose.
	 * 
	 * @author Sangwon Park (hudoni@world.kaist.ac.kr), CILab, SWRC, KAIST
	 */
	private class SharedActionHandler implements ActionListener {
		/**
		 * Performs an action depending on the given action event.
		 */
		public void actionPerformed(ActionEvent e) {
			try {
				Object source = e.getSource();

				if (source == menuItemFileOpen) {
					fileOpen();
				} 
				else if (source == menuItemHelp) {
					help();
				}
				else if (source == buttonActivate) {
					initWorkflow();
					activateWorkflow();
				}
				else if (source == buttonAnalysis) {
					analyzeText();
				}
				else if (source == buttonReset) {
					reset();
				} else if (source == radioMultiThread) {
					multiThreadMode = true;
				} else if (source == radioSingleThread) {
					multiThreadMode = false;
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		
		/**
		 * Reads a text file, and use the text as the input data.
		 */
		private void fileOpen() {
			JFileChooser chooser = new JFileChooser();
			
			chooser.setCurrentDirectory(new java.io.File("."));
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			chooser.setAcceptAllFileFilterUsed(false);
			
			if (chooser.showOpenDialog(mainFrame) == JFileChooser.APPROVE_OPTION) {
				File selectedFile = chooser.getSelectedFile();
				
				try {
					BufferedReader br = new BufferedReader(new FileReader(selectedFile));
					String line = null;
					while ((line = br.readLine()) != null) {
						inputTextArea.append(line + "\n");
					}
					br.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		/**
		 * Show 'help' window.
		 */
		private void help() {
			String helpStr = "  This is a GUI-based demo program of the HanNanum that helps users to understand the concept\n" +
							 "of work flow and use the HanNanum library easily. This consists of components for plug-in pool,\n" +
							 "work flow, plug-in information, work flow controls, input text and analysis result. Users can\n" +
							 "easily set up various work flows by drag-and-drop the plug-ins from the plug-in pool to the work\n" +
							 "flow component, and see the analysis result.\n\n" +

							 "  Users can use this GUI demo program by following procedure:\n\n" +
						 	 "  1. Browse the plug-ins in the plug-in pool. The brief information about the selected plug-in will be\n" +
						     "    displayed so that you can refer the information when set the work flow up.\n\n" +
						     "  2. Drag and drop plug-ins from the plug-in pool to the list in the work flow considering the phase\n" +
						     "    and type of the plug-in.\n " +
						     "    - If you want to remove the plug-in on the work flow, simply double click it.\n " +
						     "    - If you change the order of the supplement plug-ins in each phase, simply drag and drop a plug-in\n" +
						     "      to the position you want.\n\n" +
						     "  3. Once you finish to set the work flow up, choose 'Multi-thread Mode' or 'Single-thread Mode'.\n\n" +
						     "  4. Click 'Activate the work flow' button.\n\n" +
						     "  5. Type or copy-and-past text that you want to analyze into the 'Input Text' area.\n\n" +
						     "  6. Click 'Analyze Text' button.\n\n" +
						     "  7. You can see the analysis result on 'Result' area.\n\n" +
						     "  8. Repeat 5~7 to analyze other text with the activated work flow.\n" +
						     "    Or click 'Close the work flow' button and go to step 1 to set up new work flow. \n\n" +
						 
						     "  Author: Sangwon Park (hudoni@world.kaist.ac.kr), CILab, SWRC, KAIST\n";		
			 JOptionPane.showMessageDialog(mainFrame, helpStr);
		}
		
		/**
		 * Initialize the work flow with the sequence of the plug-ins that the user selected.
		 */
		private void initWorkflow() {
			/* Clear the previous work flow */
			workflow.clear();

			/* Set the work flow up */
			String pluginName = null;
			
			// phase1 plug-in
			for (int i = 0; i < listModelSupplement1.size(); i++) {
				pluginName = (String)listModelSupplement1.get(i);
				if (pluginName.equals("InformalSentenceFilter")) {
					workflow.appendPlainTextProcessor(new InformalSentenceFilter(), null);
				} else if (pluginName.equals("SentenceSegmentor")) {
					workflow.appendPlainTextProcessor(new SentenceSegmentor(), null);
				}
			}
			
			// phase2 plug-in
			if (listModelMajor2.size() > 0) {
				pluginName = (String)listModelMajor2.get(0);
				if (pluginName.equals("ChartMorphAnalyzer")) {
					workflow.setMorphAnalyzer(new ChartMorphAnalyzer(), "conf/plugin/MajorPlugin/MorphAnalyzer/ChartMorphAnalyzer.json");
				}
			} else {
				return;
			}
			
			for (int i = 0; i < listModelSupplement2.size(); i++) {
				pluginName = (String)listModelSupplement2.get(i);
				if (pluginName.equals("UnknownMorphProcessor")) {
					workflow.appendMorphemeProcessor(new UnknownProcessor(), null);
				} else if (pluginName.equals("SimpleMAResult09")) {
					workflow.appendMorphemeProcessor(new SimpleMAResult09(), null);
				} else if (pluginName.equals("SimpleMAResult22")) {
					workflow.appendMorphemeProcessor(new SimpleMAResult22(), null);
				}
			}
			
			// phase3 plug-in
			if (listModelMajor3.size() > 0) {
				pluginName = (String)listModelMajor3.get(0);
				if (pluginName.equals("HmmPosTagger")) {
					workflow.setPosTagger(new HMMTagger(), "conf/plugin/MajorPlugin/PosTagger/HmmPosTagger.json");
				}
			} else {
				return;
			}
			for (int i = 0; i < listModelSupplement3.size(); i++) {
				pluginName = (String)listModelSupplement3.get(i);
				if (pluginName.equals("NounExtractor")) {
					workflow.appendPosProcessor(new NounExtractor(), null);
				} else if (pluginName.equals("SimplePOSResult09")) {
					workflow.appendPosProcessor(new SimplePOSResult09(), null);
				} else if (pluginName.equals("SimplePOSResult22")) {
					workflow.appendPosProcessor(new SimplePOSResult22(), null);
				}
			}
		}
		
		/**
		 * Activates the work flow.
		 */
		private void activateWorkflow() {
			try {
				workflow.activateWorkflow(multiThreadMode);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			buttonAnalysis.setEnabled(true);
			buttonReset.setEnabled(true);
			buttonActivate.setEnabled(false);
			radioMultiThread.setEnabled(false);
			radioSingleThread.setEnabled(false);
			listPluginSupplement1.setEnabled(false);
			listPluginSupplement2.setEnabled(false);
			listPluginSupplement3.setEnabled(false);
			listPluginMajor2.setEnabled(false);
			listPluginMajor3.setEnabled(false);
			
			activated = true;
		}
		
		/**
		 * Analyze the input text with the activated work flow.
		 */
		private void analyzeText() {
			String text = inputTextArea.getText();
			
			if (text != null && text.length() > 0) {
				workflow.analyze(inputTextArea.getText());
				outputTextArea.setText(workflow.getResultOfDocument());
				
				buttonReset.setEnabled(true);
				buttonActivate.setEnabled(false);
			} else {
				outputTextArea.setText("");
			}
		}
		
		/**
		 * Reset the work flow and the demo program.
		 */
		private void reset() {
			workflow.clear();
			
			buttonActivate.setEnabled(true);
			buttonAnalysis.setEnabled(false);
			buttonReset.setEnabled(false);
			radioMultiThread.setEnabled(true);
			radioSingleThread.setEnabled(true);
			listPluginSupplement1.setEnabled(true);
			listPluginSupplement2.setEnabled(true);
			listPluginSupplement3.setEnabled(true);
			listPluginMajor2.setEnabled(true);
			listPluginMajor3.setEnabled(true);
			
			activated = false;
		}
	}
	
	/**
	 * Handler for the drag-and-drop functionality of plug-in from the plug-in pool to
	 * the work flow.
	 * 
	 * @author Sangwon Park (hudoni@world.kaist.ac.kr), CILab, SWRC, KAIST
	 */
	private class PluginTransferHandler extends TransferHandler {
		/**
		 * serialVersionUID
		 */
		private static final long serialVersionUID = 1L;
		
		/**
		 * The phase of the plug-in on the work flow.
		 * PluginInfo.PHASE1 or PluginInfo.PHASE2 or PluginInfo.PHASE3.
		 */
		private int phase = 0;
		
		/**
		 * The type of the plug-in.
		 * PluginInfo.MAJOR or PluginInfo.SUPPLEMENT.
		 */
		private int type = 0;
		
		/**
		 * Constructor.
		 * @param phase - the phase of the plug-in. PluginInfo.PHASE1 or PluginInfo.PHASE2 or PluginInfo.PHASE3.
		 * @param type - the type of the plug-in. PluginInfo.MAJOR or PluginInfo.SUPPLEMENT.
		 */
		public PluginTransferHandler(int phase, int type) {
			this.phase = phase;
			this.type = type;
		}
		
		/**
		 * Returns true when it is possible to drag and drop the plug-in.
		 */
        public boolean canImport(TransferHandler.TransferSupport info) {
        	if (!activated && selectedPlugin != null && phase == selectedPlugin.phase && type == selectedPlugin.type) {
        		return true;
        	}
            return false;
        }

        /**
         * It transfers the meta data of the plug-in from the plug-in pool to the work flow list.
         * 
         * @return true: transference was done correctly, otherwise false
         */
        public boolean importData(TransferHandler.TransferSupport info) {
            if (!info.isDrop()) {
                return false;
            }
            
            if (!info.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                return false;
            }
            
            JList.DropLocation dl = (JList.DropLocation)info.getDropLocation();
            DefaultListModel listModel = null;
            int index = dl.getIndex();
            
            switch (selectedPlugin.phase) {
            case PluginInfo.PHASE1:
            	listModel = (DefaultListModel)listPluginSupplement1.getModel();
            	break;
            case PluginInfo.PHASE2:
            	if (selectedPlugin.type == PluginInfo.MAJOR) {
            		listModel = (DefaultListModel)listPluginMajor2.getModel();
            		listModel.clear();
            		index = 0;
            	} else if (selectedPlugin.type == PluginInfo.SUPPLEMENT) {
            		listModel = (DefaultListModel)listPluginSupplement2.getModel();
            	}
            	break;
            case PluginInfo.PHASE3:
            	if (selectedPlugin.type == PluginInfo.MAJOR) {
            		listModel = (DefaultListModel)listPluginMajor3.getModel();
            		listModel.clear();
            		index = 0;
            	} else if (selectedPlugin.type == PluginInfo.SUPPLEMENT) {
            		listModel = (DefaultListModel)listPluginSupplement3.getModel();
            	}
            	break;
            }
            
            if (listModel == null) {
            	return false;
            }
            
            try {
            	Transferable t = info.getTransferable();
                String data = (String)t.getTransferData(DataFlavor.stringFlavor);
                int pIndex = listModel.indexOf(data);
                
                listModel.add(index, data);

                if (pIndex != -1) {
	                if (pIndex >= index) {
	                	pIndex++;
	                }
	                listModel.remove(pIndex);
                }
            } catch (Exception e) {
            	return false;
            }
            
            return false;
        }
        
        /**
         * Returns the type of action at the source component.
         */
        public int getSourceActions(JComponent c) {
            return COPY;
        }
        
        /**
         * Make it possible to transfer the selected plug-in.
         * Just one plug-in can be transfered, but it can be improved.
         */
        protected Transferable createTransferable(JComponent c) {
            JList list = (JList)c;
            Object[] values = list.getSelectedValues();
    
            StringBuffer buff = new StringBuffer();

            if (values.length >= 0) {
                Object val = values[0];
                buff.append(val);
            }
            
            tempPlugin.name = buff.toString();
            tempPlugin.phase = phase;
            tempPlugin.type = type;
            selectedPlugin = tempPlugin;
            
            return new StringSelection(buff.toString());
        }
    }
}
