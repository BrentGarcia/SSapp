/**
 * This module acts as the View layer for your application.
 * The 'MediaLibraryGui' class actually builds the Gui with all
 * the components - buttons, text fields, text areas, panels etc.
 * This class should be used to write the logic to add functionality
 * to the Gui components.
 * You are free add more files and further modularize this class's
 * functionality.
 */

import javax.swing.*;
import java.io.*;
import java.nio.file.Paths;
import java.nio.charset.Charset;
import javax.sound.sampled.*;
import java.beans.*;
import java.net.*;
import javax.swing.tree.*;
import javax.swing.event.*;
import javax.swing.text.html.*;
import javax.swing.filechooser.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;
import java.lang.Runtime;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.net.URLConnection;
import java.time.Duration;
import org.json.JSONObject;
import org.json.JSONArray;
import ser321.assign2.lindquis.*;

/*
* Copyright 2020 Brent Garcia,
*
* This software is the intellectual property of the author, and can not be
distributed, used, copied, or
* reproduced, in whole or in part, for any purpose, commercial or otherwise.
The author grants the ASU
* Software Engineering program the right to copy, execute, and evaluate this
work for the purpose of
* determining performance of the author in coursework, and for Software
Engineering program evaluation,
* so long as this copyright and right-to-use statement is kept in-tact in such
use.
* All other uses are prohibited and reserved to the author.
*
* Purpose: An Media Library App that used to store TV Series data from the 
omdb database.
*
* Ser321 Principles of Distributed Software Systems
* see http://pooh.poly.asu.edu/Ser321
* @author Brent Garcia, Tim Lindquist Tim.Lindquist@asu.edu
*
Software Engineering, CIDSE, IAFSE, ASU Poly
* @version April 2020
*/

//SAVING THIS HERE
public class MediaLibraryApp extends MediaLibraryGui implements
TreeWillExpandListener,
ActionListener,
TreeSelectionListener {

	private static final boolean debugOn = true;
    	private static final String pre = "https://www.omdbapi.com/?apikey=";
	private static String urlOMBD;
	private String url;
	//private MediaLibrary library;
	private SeasonLibrary library;
	private SeasonLibrary sLibrary;
	private String omdbKey;
        private SeriesSeason seriesSeason;
        private Episodes episodes;
	//private SeasonLibrary s
	private boolean searched;

	public MediaLibraryApp(String author, String authorKey) {
        // sets the value of 'author' on the title window of the GUI.
		super(author);
		this.omdbKey = authorKey;
		urlOMBD = pre + authorKey + "&t=";
		//library = new MediaLibraryImpl(); // TODO: this would need to be your SeriesLibraryImpl
		library = new SeasonLibraryImpl();

		// register this object as an action listener for menu item clicks. This will cause
		// my actionPerformed method to be called every time the user selects a menuitem.
		for(int i=0; i<userMenuItems.length; i++){
			for(int j=0; j<userMenuItems[i].length; j++){
				userMenuItems[i][j].addActionListener(this);
			}
		}
		// register this object as an action listener for the Search button. This will cause
		// my actionPerformed method to be called every time the user clicks the Search button
		searchJButt.addActionListener(this);
		try{
			//tree.addTreeWillExpandListener(this);  // add if you want to get called with expansion/contract
			tree.addTreeSelectionListener(this);
			rebuildTree();
		}catch (Exception ex){
			JOptionPane.showMessageDialog(this,"Handling "+
					" constructor exception: " + ex.getMessage());
		}
		try{
			/*
			 * display an image just to show how the album or artist image can be displayed in the
			 * app's window. setAlbumImage is implemented by MediaLibraryGui class. Call it with a
			 * string url to a png file as obtained from an album search.
			 */

			// TODO: set album image here
			setAlbumImage("https://m.media-amazon.com/images/M/MV5BY2FmZTY5YTktOWRlYy00NmIyLWE0ZmQtZDg2YjlmMzczZDZiXkEyXkFqcGdeQXVyNjg4NzAyOTA@._V1_SX300.jpg");
		}catch(Exception ex){
			System.out.println("unable to open image");
		}
		setVisible(true);
	}

	/**
	 * A method to facilitate printing debugging messages during development, but which can be
	 * turned off as desired.
     * @param message Is the message that should be printed.
     * @return void
	 */
	private void debug(String message) {
		if (debugOn)
			System.out.println("debug: "+message);
	}

	/**
	 * Create and initialize nodes in the JTree of the left pane.
	 * buildInitialTree is called by MediaLibraryGui to initialize the JTree.
	 * Classes that extend MediaLibraryGui should override this method to 
	 * perform initialization actions specific to the extended class.
	 * The default functionality is to set base as the label of root.
	 * In your solution, you will probably want to initialize by deserializing
	 * your library and displaying the categories and subcategories in the
	 * tree.
	 * @param root Is the root node of the tree to be initialized.
	 * @param base Is the string that is the root node of the tree.
	 */
	public void buildInitialTree(DefaultMutableTreeNode root, String base){
		//set up the context and base name
		try{
			root.setUserObject(base);
		}catch (Exception ex){
			JOptionPane.showMessageDialog(this,"exception initial tree:"+ex);
			ex.printStackTrace();
		}
	}

	/**
	 * TODO
	 * method to build the JTree of media shown in the left panel of the UI. The
	 * field tree is a JTree as defined and initialized by MediaLibraryGui class.
	 * It is defined to be protected so it can be accessed by extending classes.
	 * This version of the method uses the music library to get the names of
	 * tracks. Your solutions will need to replace this structure with one that
	 * you need for the series/season and Episode. These two classes should store your information. 
	 * Your library (so a changes - or newly implemented MediaLibraryImpl) will store 
	 * and provide access to Series/Seasons and Episodes.
	 * This method is provided to demonstrate one way to add nodes to a JTree based
	 * on an underlying storage structure.
	 * See also the methods clearTree, valueChanged defined in this class, and
	 * getSubLabelled which is defined in the GUI/view class.
	 **/
	public void rebuildTree(){
		tree.removeTreeSelectionListener(this);
		DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
		DefaultMutableTreeNode root = (DefaultMutableTreeNode)model.getRoot();
		clearTree(root, model);
		// put nodes in the tree for all registered with the library

		String[] sTitleList = library.getAllSeriesSeasonTitles();

		//FOR EVERY SERIES
		for (int i = 0; i<sTitleList.length; i++){
			
			SeriesSeason aSS = library.getSeriesSeason(sTitleList[i]);
			String seriesTitle = aSS.title + ": Season " + aSS.season;
			
			Episodes[] sEpisodesList = aSS.getAllEpisodes();
			//FOR EVERY EPISODE
			for (int j = 0; j<sEpisodesList.length; j++){

			    debug("Adding episode with title:"+sEpisodesList[j].title);
			    DefaultMutableTreeNode toAdd = new DefaultMutableTreeNode(sEpisodesList[j].title);
			    DefaultMutableTreeNode subNode = getSubLabelled(root, seriesTitle);
			    if(subNode!=null){ // if seriesSeason subnode already exists
				debug("seriesSeason exists: "+seriesTitle);
				model.insertNodeInto(toAdd, subNode,
						model.getChildCount(subNode));
			    }else{ // album node does not exist
				DefaultMutableTreeNode anAlbumNode =
						new DefaultMutableTreeNode(seriesTitle);
				debug("no album, so add one with name: "+seriesTitle);
				model.insertNodeInto(anAlbumNode, root,
						model.getChildCount(root));
				DefaultMutableTreeNode aSubCatNode = 
						new DefaultMutableTreeNode("aSubCat");
				debug("adding subcat labelled: "+"aSubCat");
				model.insertNodeInto(toAdd,anAlbumNode,
						model.getChildCount(anAlbumNode));
			     }
                        }
		}
		// expand all the nodes in the JTree
		for(int r =0; r < tree.getRowCount(); r++){
			tree.expandRow(r);
		}
		tree.addTreeSelectionListener(this);
	}

    /**
     * Remove all nodes in the left pane tree view.
     *
     * @param root Is the root node of the tree.
     * @param model Is a model that uses TreeNodes.
     * @return void
     */
	private void clearTree(DefaultMutableTreeNode root, DefaultTreeModel model){
		try{
			DefaultMutableTreeNode next = null;
			int subs = model.getChildCount(root);
			for(int k=subs-1; k>=0; k--){
				next = (DefaultMutableTreeNode)model.getChild(root,k);
				debug("removing node labelled:"+(String)next.getUserObject());
				model.removeNodeFromParent(next);
			}
		}catch (Exception ex) {
			System.out.println("Exception while trying to clear tree:");
			ex.printStackTrace();
		}
	}

	public void treeWillCollapse(TreeExpansionEvent tee) {
		debug("In treeWillCollapse with path: "+tee.getPath());
		tree.setSelectionPath(tee.getPath());
	}

	public void treeWillExpand(TreeExpansionEvent tee) {
		debug("In treeWillExpand with path: "+tee.getPath());
	}

	// TODO: 
	// this will be called when you click on a node. 
	// It will update the node based on the information stored in the library
	// this will need to change since your library will be of course totally different
	// extremely simplified! E.g. make sure that you display sensible content when the root,
	// the My Series, the Series/Season, and Episode nodes are selected
	public void valueChanged(TreeSelectionEvent e) {
		try{
			tree.removeTreeSelectionListener(this);
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)
					tree.getLastSelectedPathComponent();
			if(node!=null){
				String nodeLabel = (String)node.getUserObject();
				debug("In valueChanged. Selected node labelled: "+ nodeLabel);
				// is this a terminal node?
				
				// All fields empty to start with
				seriesSeasonJTF.setText(""); 
				genreJTF.setText(""); 
				ratingJTF.setText(""); 
				episodeJTF.setText("");
				summaryJTA.setText("");
		

				DefaultMutableTreeNode root = (DefaultMutableTreeNode)tree.getModel().getRoot(); // get the root
                // First (and only) child of the root (username) node is 'My Series' node.
				DefaultMutableTreeNode mySeries = (DefaultMutableTreeNode)root.getChildAt(0); // mySeries node
				DefaultMutableTreeNode parent = (DefaultMutableTreeNode)node.getParent();

				// TODO when it is an episode change the episode to something and set the rating to the episode rating			
				//If node is an Episode
				if(node.getChildCount()==0 &&
						(node != (DefaultMutableTreeNode)tree.getModel().getRoot())){
					
					//DOUBLE LIBRARY WORK AROUND
					SeriesSeason ss;
					String parentNodeLabel = parent.toString();
					
					if (searched == true) {
						ss = sLibrary.getSeriesSeason(parentNodeLabel);
					}
					//extract Series Title from node label
					else {
						
						int delimeter = parentNodeLabel.indexOf(":");
						String alteredParentNodeLabel = parentNodeLabel.substring(0,delimeter);
						//System.out.println("alteredParentNodeLabel = " + alteredParentNodeLabel);
						ss = library.getSeriesSeason(alteredParentNodeLabel);
					}
					
					Episodes[] episodesArray = ss.getAllEpisodes();
					Episodes ep = new Episodes("blank", "blank", "blank");
					for (int i = 0; i<episodesArray.length; i++){
						if (episodesArray[i].title == nodeLabel){
							ep = episodesArray[i];
						}
   					}					

					// TODO just setting some values so you see how it can be done, they do not fit the fields!
					episodeJTF.setText(nodeLabel); // name of the episode
					ratingJTF.setText(ep.imdbRating); // change to rating of the episode 
					genreJTF.setText(ss.genre); // change to genre of the series from library
					summaryJTA.setText(ss.plot); // change to Plot of library for season
					seriesSeasonJTF.setText(parentNodeLabel); // Change to season name
					setAlbumImage(ss.poster);
					
				}//If node is a Series/Season
				else if (parent == root){ // should be the series/season
					
					//extract Series Title from node label
					//Used Double library work around
					SeriesSeason ss;
					if (searched == true) {
						ss = sLibrary.getSeriesSeason(nodeLabel);
					} else {
						int delimeter = nodeLabel.indexOf(":");
						String alteredNodeLabel = nodeLabel.substring(0,delimeter);
						ss = library.getSeriesSeason(alteredNodeLabel);
					}
					
					seriesSeasonJTF.setText(nodeLabel); // season name
					genreJTF.setText(ss.genre); // genre of the series from library
					ratingJTF.setText(ss.imdbRating); // rating of the season get from library
					episodeJTF.setText(""); // nothing in here since not an episode
					summaryJTA.setText(ss.plot);
					setAlbumImage(ss.poster);
					
				}
			}
		}catch (Exception ex){
			ex.printStackTrace();
		}
		tree.addTreeSelectionListener(this);
	}
	
	
	// TODO: this is where you will need to implement a bunch. So when some action is called the correct thing happens
	public void actionPerformed(ActionEvent e) {
		tree.removeTreeSelectionListener(this);
		if(e.getActionCommand().equals("Exit")) {
			System.exit(0);
		}else if(e.getActionCommand().equals("Save")) {
			boolean savRes = false;
			savRes = library.saveLibraryToFile();
			System.out.println("Save "+((savRes)?"successful":"not implemented"));
		}else if(e.getActionCommand().equals("Restore")) {
			boolean resRes = false;
			resRes = library.restoreLibraryFromFile();
			rebuildTree();
			tree.removeTreeSelectionListener(this);			
			System.out.println("Restore "+((resRes)?"successful":"not implemented"));
		}else if(e.getActionCommand().equals("Series-SeasonAdd")) {
			
			if (searched == true) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
				String nodeLabel = (String)node.getUserObject();
				if (node.getChildCount()==0 &&
						(node != (DefaultMutableTreeNode)tree.getModel().getRoot())){
					DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode)node.getParent();
					nodeLabel = (String)parentNode.getUserObject();
				} 
				SeriesSeason ss = sLibrary.getSeriesSeason(nodeLabel);
				library.addSeriesSeason(ss);
				searched = false;
				rebuildTree();
				tree.removeTreeSelectionListener(this);
			}
		}else if(e.getActionCommand().equals("Search")) { 
            /*
             * In the below API(s) the error response should be appropriately handled
             */

			// with all episodes only display this new series/season with the episodes in tree

			// Doing a fetch two times so that we only get the full series info (with poster, summary, rating) once
			// fetch series info
			String searchReqURL = urlOMBD+seriesSearchJTF.getText().replace(" ", "%20");
			System.out.println("calling fetch with url: "+searchReqURL);
			String json = fetchURL(searchReqURL);

			// fetch season info
			String searchReqURL2 = urlOMBD+seriesSearchJTF.getText().replace(" ", "%20")+"&season="+seasonSearchJTF.getText();
                        System.out.println("calling second fetch with url: "+searchReqURL2);
			String jsonEpisodes = fetchURL(searchReqURL2);

			// Send to back end to create our new SeriesSeason object
                        seriesSeason = new SeriesSeason(json,jsonEpisodes);
			String seriesTitle = seriesSeason.title;

			/* TODO: implement here that this json will be used to create a Season object with the episodes included
			 * This should also then build the tree and display the info in the left side bar (so the new tree with its episodes)
			 * right hand should display the Series information
			 */

			//Second library to serialize and save temporary search data
			sLibrary = new SeasonLibraryImpl();
			sLibrary.addSeriesSeason(seriesSeason);
			
			tree.removeTreeSelectionListener(this);
			DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
			DefaultMutableTreeNode root = (DefaultMutableTreeNode)model.getRoot();
			clearTree(root, model);
			
			Episodes[] sEpisodesList = seriesSeason.getAllEpisodes();
			//FOR EVERY EPISODE
			for (int j = 0; j<sEpisodesList.length; j++){
			    DefaultMutableTreeNode toAdd = new DefaultMutableTreeNode(sEpisodesList[j].title);
			    DefaultMutableTreeNode subNode = getSubLabelled(root, seriesTitle);
			    if(subNode!=null){ // if seriesSeason subnode already exists
				debug("seriesSeason exists: "+seriesTitle);
				model.insertNodeInto(toAdd, subNode,
				model.getChildCount(subNode));
			    }else{ // album node does not exist
				DefaultMutableTreeNode anAlbumNode =
					new DefaultMutableTreeNode(seriesTitle);
				debug("no album, so add one with name: "+seriesTitle);
				model.insertNodeInto(anAlbumNode, root,
						model.getChildCount(root));
				DefaultMutableTreeNode aSubCatNode = 
						new DefaultMutableTreeNode("aSubCat");
				debug("adding subcat labelled: "+"aSubCat");
				model.insertNodeInto(toAdd,anAlbumNode,
						model.getChildCount(anAlbumNode));
				}
			}
			// expand all the nodes in the JTree
			for(int r =0; r < tree.getRowCount(); r++){
				tree.expandRow(r);
			}
			searched = true;

		}else if(e.getActionCommand().equals("Tree Refresh")) {
			rebuildTree();
			tree.removeTreeSelectionListener(this);
		}else if(e.getActionCommand().equals("Series-SeasonRemove")) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
			String nodeLabel = (String)node.getUserObject();
			if (node.getChildCount()==0 &&
						(node != (DefaultMutableTreeNode)tree.getModel().getRoot())){
				DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode)node.getParent();
				nodeLabel = (String)parentNode.getUserObject();
				int delimeter = nodeLabel.indexOf(":");
				nodeLabel = nodeLabel.substring(0,delimeter);

			} else {
				int delimeter = nodeLabel.indexOf(":");
				nodeLabel = nodeLabel.substring(0,delimeter);
			}
			SeriesSeason ssToRemove = library.getSeriesSeason(nodeLabel);
			library.removeSeriesSeason(ssToRemove);
			rebuildTree();
			tree.removeTreeSelectionListener(this);
		}
		tree.addTreeSelectionListener(this);
	}

	/**
	 *
	 * A method to do asynchronous url request printing the result to System.out
	 * @param aUrl the String indicating the query url for the OMDb api search
	 *
	 **/
	public void fetchAsyncURL(String aUrl){
		try{
			HttpClient client = HttpClient.newHttpClient();
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(aUrl))
					.timeout(Duration.ofMinutes(1))
					.build();
			client.sendAsync(request, BodyHandlers.ofString())
			.thenApply(HttpResponse::body)
			.thenAccept(System.out::println)
			.join();
		}catch(Exception ex){
			System.out.println("Exception in fetchAsyncUrl request: "+ex.getMessage());
		}
	}

	/**
	 *
	 * a method to make a web request. Note that this method will block execution
	 * for up to 20 seconds while the request is being satisfied. Better to use a
	 * non-blocking request.
	 * @param aUrl the String indicating the query url for the OMDb api search
	 * @return the String result of the http request.
	 *
	 **/
	public String fetchURL(String aUrl) {
		StringBuilder sb = new StringBuilder();
		URLConnection conn = null;
		InputStreamReader in = null;
		try {
			URL url = new URL(aUrl);
			conn = url.openConnection();
			if (conn != null)
				conn.setReadTimeout(20 * 1000); // timeout in 20 seconds
			if (conn != null && conn.getInputStream() != null) {
				in = new InputStreamReader(conn.getInputStream(),
						Charset.defaultCharset());
				BufferedReader br = new BufferedReader(in);
				if (br != null) {
					int ch;
					// read the next character until end of reader
					while ((ch = br.read()) != -1) {
						sb.append((char)ch);
					}
					br.close();
				}
			}
			in.close();
		} catch (Exception ex) {
			System.out.println("Exception in url request:"+ ex.getMessage());
		} 
		return sb.toString();
	}

	public static void main(String args[]) {
		String name = "first.last";
		String key = "use-your-last.ombd-key";
		if (args.length >= 2){
			//System.out.println("java -cp classes:lib/json.lib ser321.assign2.lindquist."+
			//                   "MediaLibraryApp \"Lindquist Music Library\" lastFM-Key");
			name = args[0];
			key = args[1];
		}
		try{
			//System.out.println("calling constructor name "+name);
			MediaLibraryApp mla = new MediaLibraryApp(name,key);
		}catch (Exception ex){
			ex.printStackTrace();
		}
	}
}
