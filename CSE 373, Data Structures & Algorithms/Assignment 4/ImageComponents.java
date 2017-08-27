/*
 * ImageComponents.java
 * A4 Solution by Taylor Posey, UWNetID: tposey28
 * All extra credit attempted, except I did not implement UNION-by-size
 * 
 * CSE 373, University of Washington, Autumn 2014.
 * 
 * Starter Code for CSE 373 Assignment 4, Part II.    Starter Code Version 0.3.
 * S. Tanimoto,  Nov. 6, 2014, after feedback from TA Johnson Goh.
 * Minor changes from v0.2: removed blockSize and hashFunctionChoice variables needed in A3,
 *   added a new variable parentID to standardize the way solutions look for A4,
 *   and added stubs for the find and union methods that need to be implemented.
 * 
 */ 

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ByteLookupTable;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.LookupOp;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Stack;

import javax.imageio.ImageIO;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ImageComponents extends JFrame implements ActionListener {
    public static ImageComponents appInstance; // Used in main().

    String startingImage = "gettysburg-address-p1.png";
    BufferedImage biTemp, biWorking, biFiltered; // These hold arrays of pixels.
    Graphics gOrig, gWorking; // Used to access the drawImage method.
    int w; // width of the current image.
    int h; // height of the current image.

    int[][] parentID; // For your forest of up-trees.
    HashMap<Integer, Integer> componentNumber; // Labels the up-trees
    int unionCount; // How many times union was called
    int componentCount; // How many up-trees there are
    int linkTraversalCount; // For comparing performance of compression
    int callsOnFind; // For comparing performance on compression
    
    JPanel viewPanel; // Where the image will be painted.
    JPopupMenu popup;
    JMenuBar menuBar;
    JMenu fileMenu, imageOpMenu, ccMenu, helpMenu;
    JMenuItem loadImageItem, saveAsItem, exitItem;
    JMenuItem lowPassItem, highPassItem, photoNegItem, RGBThreshItem;

    JMenuItem CCItem1, CCItem2, CCItem3, CCItem4;
    JMenuItem aboutItem, helpItem;
    
    JFileChooser fileChooser; // For loading and saving images.
    
    public class Color {
        int r, g, b;

        Color(int r, int g, int b) {
            this.r = r; this.g = g; this.b = b;    		
        }
        
        Color(int pixelID) {
        	int byteRGB = biWorking.getRGB(getXcoord(pixelID), getYcoord(pixelID));
    		this.r = (byteRGB & 0x00ff0000) >> 16;
    		this.g = (byteRGB & 0x0000ff00) >> 8;
    		this.b = byteRGB & 0x000000ff;
        }

        int euclideanSquared(Color c2) {
            return (r - c2.r)*(r - c2.r) + (g - c2.g)*(g - c2.g) + (b - c2.b)*(b - c2.b);
        }
    }
    
    public class Edge implements Comparable<Edge>{
    	int weight;
    	int edgePoint1;
    	int edgePoint2;
    	
    	Edge(int pixelID1, int pixelID2) {
    		this.edgePoint1 = pixelID1;
    		this.edgePoint2 = pixelID2;
			Color color1 = new Color(pixelID1);
			Color color2 = new Color(pixelID2);
			this.weight = color1.euclideanSquared(color2);
    	}

		public int compareTo(Edge e) {
			if(this.weight - e.weight > 0) {
				return 1;
			} else if (this.weight - e.weight < 0) {
				return -1;
			} else {
				return 0;
			}
		}
    }


    // Some image manipulation data definitions that won't change...
    static LookupOp PHOTONEG_OP, RGBTHRESH_OP;
    static ConvolveOp LOWPASS_OP, HIGHPASS_OP;
    
    public static final float[] SHARPENING_KERNEL = { // sharpening filter kernel
        0.f, -1.f,  0.f,
       -1.f,  5.f, -1.f,
        0.f, -1.f,  0.f
    };

    public static final float[] BLURRING_KERNEL = {
        0.1f, 0.1f, 0.1f,    // low-pass filter kernel
        0.1f, 0.2f, 0.1f,
        0.1f, 0.1f, 0.1f
    };
    
    public ImageComponents() { // Constructor for the application.
        setTitle("Image Analyzer"); 
        addWindowListener(new WindowAdapter() { // Handle any window close-box clicks.
            public void windowClosing(WindowEvent e) {System.exit(0);}
        });

        // Create the panel for showing the current image, and override its
        // default paint method to call our paintPanel method to draw the image.
        viewPanel = new JPanel(){public void paint(Graphics g) { paintPanel(g);}};
        add("Center", viewPanel); // Put it smack dab in the middle of the JFrame.

        // Create standard menu bar
        menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        fileMenu = new JMenu("File");
        imageOpMenu = new JMenu("Image Operations");
        ccMenu = new JMenu("Connected Components");
        helpMenu = new JMenu("Help");
        menuBar.add(fileMenu);
        menuBar.add(imageOpMenu);
        menuBar.add(ccMenu);
        menuBar.add(helpMenu);

        // Create the File menu's menu items.
        loadImageItem = new JMenuItem("Load image...");
        loadImageItem.addActionListener(this);
        fileMenu.add(loadImageItem);
        saveAsItem = new JMenuItem("Save as full-color PNG");
        saveAsItem.addActionListener(this);
        fileMenu.add(saveAsItem);
        exitItem = new JMenuItem("Quit");
        exitItem.addActionListener(this);
        fileMenu.add(exitItem);

        // Create the Image Operation menu items.
        lowPassItem = new JMenuItem("Convolve with blurring kernel");
        lowPassItem.addActionListener(this);
        imageOpMenu.add(lowPassItem);
        highPassItem = new JMenuItem("Convolve with sharpening kernel");
        highPassItem.addActionListener(this);
        imageOpMenu.add(highPassItem);
        photoNegItem = new JMenuItem("Photonegative");
        photoNegItem.addActionListener(this);
        imageOpMenu.add(photoNegItem);
        RGBThreshItem = new JMenuItem("RGB Thresholds at 128");
        RGBThreshItem.addActionListener(this);
        imageOpMenu.add(RGBThreshItem);

 
        // Create CC menu stuff.
        CCItem1 = new JMenuItem("Compute Connected Components and Recolor");
        CCItem1.addActionListener(this);
        ccMenu.add(CCItem1);
//         CCItem2 = new JMenuItem("Segment Image and Recolor");
//         CCItem2.addActionListener(this);
//         CCItem2.setEnabled(false);
//         ccMenu.add(CCItem2);
//         CCItem3 = new JMenuItem("Segment Image Using a Maximum Pixel DELTA");
//         CCItem3.addActionListener(this);
//         CCItem3.setEnabled(false);
//         ccMenu.add(CCItem3);
        CCItem4 = new JMenuItem("Segment image, using path compression during FINDs");
        CCItem4.addActionListener(this);
        ccMenu.add(CCItem4);
        
        // Create the Help menu's item.
        aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(this);
        helpMenu.add(aboutItem);
        helpItem = new JMenuItem("Help");
        helpItem.addActionListener(this);
        helpMenu.add(helpItem);

        // Initialize the image operators, if this is the first call to the constructor:
        if (PHOTONEG_OP==null) {
            byte[] lut = new byte[256];
            for (int j=0; j<256; j++) {
                lut[j] = (byte)(256-j); 
            }
            ByteLookupTable blut = new ByteLookupTable(0, lut); 
            PHOTONEG_OP = new LookupOp(blut, null);
        }
        if (RGBTHRESH_OP==null) {
            byte[] lut = new byte[256];
            for (int j=0; j<256; j++) {
                lut[j] = (byte)(j < 128 ? 0: 200);
            }
            ByteLookupTable blut = new ByteLookupTable(0, lut); 
            RGBTHRESH_OP = new LookupOp(blut, null);
        }
        if (LOWPASS_OP==null) {
            float[] data = BLURRING_KERNEL;
            LOWPASS_OP = new ConvolveOp(new Kernel(3, 3, data),
                                        ConvolveOp.EDGE_NO_OP,
                                        null);
        }
        if (HIGHPASS_OP==null) {
            float[] data = SHARPENING_KERNEL;
            HIGHPASS_OP = new ConvolveOp(new Kernel(3, 3, data),
                                        ConvolveOp.EDGE_NO_OP,
                                        null);
        }
        loadImage(startingImage); // Read in the pre-selected starting image.
        setVisible(true); // Display it.
    }
    
    /*
     * Given a path to a file on the file system, try to load in the file
     * as an image.  If that works, replace any current image by the new one.
     * Re-make the biFiltered buffered image, too, because its size probably
     * needs to be different to match that of the new image.
     */
    public void loadImage(String filename) {
        try {
            biTemp = ImageIO.read(new File(filename));
            w = biTemp.getWidth();
            h = biTemp.getHeight();
            viewPanel.setSize(w,h);
            biWorking = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            gWorking = biWorking.getGraphics();
            gWorking.drawImage(biTemp, 0, 0, null);
            biFiltered = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            pack(); // Lay out the JFrame and set its size.
            repaint();
        } catch (IOException e) {
            System.out.println("Image could not be read: "+filename);
            System.exit(1);
        }
    }

    /* Menu handlers
     */
    void handleFileMenu(JMenuItem mi){
        System.out.println("A file menu item was selected.");
        if (mi==loadImageItem) {
            File loadFile = new File("image-to-load.png");
            if (fileChooser==null) {
                fileChooser = new JFileChooser();
                fileChooser.setSelectedFile(loadFile);
                fileChooser.setFileFilter(new FileNameExtensionFilter("Image files", new String[] { "JPG", "JPEG", "GIF", "PNG" }));
            }
            int rval = fileChooser.showOpenDialog(this);
            if (rval == JFileChooser.APPROVE_OPTION) {
                loadFile = fileChooser.getSelectedFile();
                loadImage(loadFile.getPath());
            }
        }
        if (mi==saveAsItem) {
            File saveFile = new File("savedimage.png");
            fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(saveFile);
            int rval = fileChooser.showSaveDialog(this);
            if (rval == JFileChooser.APPROVE_OPTION) {
                saveFile = fileChooser.getSelectedFile();
                // Save the current image in PNG format, to a file.
                try {
                    ImageIO.write(biWorking, "png", saveFile);
                } catch (IOException ex) {
                    System.out.println("There was some problem saving the image.");
                }
            }
        }
        if (mi==exitItem) { this.setVisible(false); System.exit(0); }
    }

    void handleEditMenu(JMenuItem mi){
        System.out.println("An edit menu item was selected.");
    }

    void handleImageOpMenu(JMenuItem mi){
        System.out.println("An imageOp menu item was selected.");
        if (mi==lowPassItem) { applyOp(LOWPASS_OP); }
        else if (mi==highPassItem) { applyOp(HIGHPASS_OP); }
        else if (mi==photoNegItem) { applyOp(PHOTONEG_OP); }
        else if (mi==RGBThreshItem) { applyOp(RGBTHRESH_OP); }
        repaint();
    }

    void handleCCMenu(JMenuItem mi) {
        System.out.println("A connected components menu item was selected.");
        if (mi==CCItem1) { computeConnectedComponents(); }
        if (mi==CCItem2) { 
        	int nregions = 25; // default value.
        	String inputValue = JOptionPane.showInputDialog("Please input the number of regions desired");
        	try {
        		nregions = (new Integer(inputValue)).intValue();
        	}
        	catch(Exception e) {
        		System.out.println(e);
        		System.out.println("That did not convert to an integer. Using the default: 25.");
        	}
        	System.out.println("Max regions is "+nregions);
        	computeSpanningTrees(nregions, -1); //pass in -1 for DELTA so it has no affect
        }
        if (mi==CCItem3) {
        	int delta = 1000; // default value.
        	String inputValue = JOptionPane.showInputDialog("Please input the maximum pixel delta desired");
        	try {
        		delta = (new Integer(inputValue)).intValue();
        	}
        	catch(Exception e) {
        		System.out.println(e);
        		System.out.println("That did not convert to an integer. Using the default: 1000.");
        	}
        	System.out.println("Max delta is "+delta);
        	computeSpanningTrees(h*w + 1, delta); //pass in pixel count + 1 for regionCount so it has no affect
        }
        if (mi==CCItem4) {
        	int choice = 1;
        	String inputValue = JOptionPane.showInputDialog("Will you specify max regions, or a max pixel delta? (1 or 2)");
        	try {
        		choice = (new Integer(inputValue)).intValue();
        	}
        	catch(Exception e) {
        		System.out.println(e);
        		System.out.println("That did not convert to an integer. Using the default: 1.");
        	}
        	if (choice == 1) {
        		int nregions = 25; // default value.
            	inputValue = JOptionPane.showInputDialog("Please input the number of regions desired");
            	try {
            		nregions = (new Integer(inputValue)).intValue();
            	}
            	catch(Exception e) {
            		System.out.println(e);
            		System.out.println("That did not convert to an integer. Using the default: 25.");
            	}
            	System.out.println("Max regions is "+nregions);
            	compressedSpanningTrees(nregions, -1); //pass in -1 for DELTA so it has no affect
        	} else {
        		int delta = 1000; // default value.
            	inputValue = JOptionPane.showInputDialog("Please input the maximum pixel delta desired");
            	try {
            		delta = (new Integer(inputValue)).intValue();
            	}
            	catch(Exception e) {
            		System.out.println(e);
            		System.out.println("That did not convert to an integer. Using the default: 1000.");
            	}
            	System.out.println("Max delta is "+delta);
            	compressedSpanningTrees(h*w + 1, delta); //pass in pixel count + 1 for regionCount so it has no affect
        	}
        }
    }
    
    void handleHelpMenu(JMenuItem mi){
        System.out.println("A help menu item was selected.");
        if (mi==aboutItem) {
            System.out.println("About: Well this is my program.");
            JOptionPane.showMessageDialog(this,
                "Image Components, Starter-Code Version.",
                "About",
                JOptionPane.PLAIN_MESSAGE);
        }
        else if (mi==helpItem) {
            System.out.println("In case of panic attack, select File: Quit.");
            JOptionPane.showMessageDialog(this,
                "To load a new image, choose File: Load image...\nFor anything else, just try different things.",
                "Help",
                JOptionPane.PLAIN_MESSAGE);
        }
    }

    /*
     * Used by Swing to set the size of the JFrame when pack() is called.
     */
    public Dimension getPreferredSize() {
        return new Dimension(w, h+50); // Leave some extra height for the menu bar.
    }

    public void paintPanel(Graphics g) {
        g.drawImage(biWorking, 0, 0, null);
    }
            	
    public void applyOp(BufferedImageOp operation) {
        operation.filter(biWorking, biFiltered);
        gWorking.drawImage(biFiltered, 0, 0, null);
    }

    public void actionPerformed(ActionEvent e) {
        Object obj = e.getSource(); // What Swing object issued the event?
        if (obj instanceof JMenuItem) { // Was it a menu item?
            JMenuItem mi = (JMenuItem)obj; // Yes, cast it.
            JPopupMenu pum = (JPopupMenu)mi.getParent(); // Get the object it's a child of.
            JMenu m = (JMenu) pum.getInvoker(); // Get the menu from that (popup menu) object.
            //System.out.println("Selected from the menu: "+m.getText()); // Printing this is a debugging aid.

            if (m==fileMenu)    { handleFileMenu(mi);    return; }  // Handle the item depending on what menu it's from.
            if (m==imageOpMenu) { handleImageOpMenu(mi); return; }
            if (m==ccMenu)      { handleCCMenu(mi);      return; }
            if (m==helpMenu)    { handleHelpMenu(mi);    return; }
        } else {
            System.out.println("Unhandled ActionEvent: "+e.getActionCommand());
        }
    }

    // Use this to put color information into a pixel of a BufferedImage object.
    void putPixel(BufferedImage bi, int x, int y, int r, int g, int b) {
        int rgb = (r << 16) | (g << 8) | b; // pack 3 bytes into a word.
        bi.setRGB(x,  y, rgb);
    }
    
    // This method will create and count the up-trees of the strict pixel graph, 
    // and then recolor these trees with progressive colors
    void computeConnectedComponents() {
    	System.out.println("Image is " + h +"X" + w + ", or " + h*w + " pixels");
    	initializeTrees();
    	createStrictGraph();
    	System.out.println("The number of times that the method UNION" +
    			" was called for this image is: " + unionCount);
    	mapTrees();
    	System.out.println("The number of connected components in this image is: " + componentCount);
    	progressiveScan();
    	repaint();
    }
    
    // This method will create and count a forest of minimum spanning trees for the image.
    // Recolors these trees with progressive colors.
    // There will either be regionCount number of trees, or enough trees such that the given
    // pixel delta is never surpassed.
    void computeSpanningTrees(int regionCount, int delta) {
    	System.out.println("Image is " + h +"X" + w + ", or " + h*w + " pixels");
    	initializeTrees();
    	System.out.println("Sorting");
        PriorityQ<Edge> edges = sortEdges();
    	System.out.println("Finding minumum spanning forest");
        findSpanningTrees(edges, regionCount, delta);
    	System.out.println("Done finding minimum spanning forest");
    	System.out.println("The number of times that the method UNION" +
    			" was called for this image is: " + unionCount);
    	mapTrees();
    	System.out.println("The number of connected components in this image is: " + componentCount);
    	progressiveScan();
    	repaint();
    	System.out.println("Find was called " + callsOnFind + " times, there were " + linkTraversalCount + 
    			" times, and the average number of link traversals per find was " + linkTraversalCount/callsOnFind);
    }
    
    // This method will create and count a forest of minimum spanning trees for the image.
    // Recolors these trees with progressive colors.
    // There will either be regionCount number of trees, or enough trees such that the given
    // pixel delta is never surpassed.
    void compressedSpanningTrees(int regionCount, int delta) {
    	System.out.println("Image is " + h +"X" + w + ", or " + h*w + " pixels");
    	initializeTrees();
    	System.out.println("Sorting");
        PriorityQ<Edge> edges = sortEdges();
    	System.out.println("Finding minumum spanning forest");
        findCompressedSpanningTrees(edges, regionCount, delta);
    	System.out.println("Done finding minimum spanning forest");
    	System.out.println("The number of times that the method UNION" +
    			" was called for this image is: " + unionCount);
    	mapTrees();
    	System.out.println("The number of connected components in this image is: " + componentCount);
    	progressiveScan();
    	repaint();
    	System.out.println("Find was called " + callsOnFind + " times, there were " + linkTraversalCount + 
    			" times, and the average number of link traversals per find was " + linkTraversalCount/callsOnFind);
    }
    
    // Initializes the up-trees, making each pixel it's own root
    void initializeTrees() {
    	parentID = new int[h][w];
    	for (int i = 0; i < h; i++) {
    		for (int j = 0; j < w; j++) {
    			parentID[i][j] = -1;
    		}
    	}
    }
    
    // Scans image and creates the strict pixel graph
    void createStrictGraph() {
    	unionCount = 0;
    	for (int i = 0; i < h; i++) {
    		for (int j = 0; j < w; j++) {
    			int pixelID = i*w + j;
    			int pixelColor = biWorking.getRGB(j, i);
                // If the pixel to this pixel's right is the same color, union them
    			if (j + 1 != w && pixelColor == biWorking.getRGB(j + 1, i)) {
    				union(find(pixelID), find(pixelID + 1));
    			}
                // If the pixel below this pixel is the same color, union them
    			if (i + 1 != h && pixelColor == biWorking.getRGB(j, i + 1)) {
    				union(find(pixelID), find(pixelID + w));
    			}
    		}
    	}
    }
    
    // Creates the minimum spanning forest using Kruskal's Algorithm
    // There will either be regionCount number of trees, or enough trees such that the given
    // pixel delta is never surpassed.
    void findSpanningTrees(PriorityQ<Edge> edges, int regionCount, int delta) {
    	int treeCount = w*h;
    	Edge e = edges.deleteMin();
    	int weight = e.weight;
    	while ((treeCount > regionCount || weight <= delta) && !edges.isEmpty()) {
    		int root1 = find(e.edgePoint1);
    		int root2 = find(e.edgePoint2);
    		if (root1 != root2) {
    			union(root1, root2);
    			treeCount--;
    		}
    		e = edges.deleteMin();
    		weight = e.weight;
    	}
		edges.printComparisons();
		edges.resetComparisonsCount();
    }
    
    // Creates the minimum spanning forest using Kruskal's Algorithm
    // There will either be regionCount number of trees, or enough trees such that the given
    // pixel delta is never surpassed.
    void findCompressedSpanningTrees(PriorityQ<Edge> edges, int regionCount, int delta) {
    	int treeCount = w*h;
    	Edge e = edges.deleteMin();
    	int weight = e.weight;
    	while ((treeCount > regionCount || weight <= delta) && !edges.isEmpty()) {
    		int root1 = findCompress(e.edgePoint1);
    		int root2 = findCompress(e.edgePoint2);
    		if (root1 != root2) {
    			union(root1, root2);
    			treeCount--;
    		}
    		e = edges.deleteMin();
    		weight = e.weight;
    	}
		edges.printComparisons();
		edges.resetComparisonsCount();
    }

    // Scans image and computes weighted edges between all adjacent pixels.
    // Edges are stored in a PriorityQueue that is then returned.
    PriorityQ<Edge> sortEdges() {
    	ArrayList<Edge> edges = new ArrayList<Edge>();
    	for (int i = 0; i < h; i++) {
    		for (int j = 0; j < w; j++) {
    			int pixelID = i*w + j;
    			if (j + 1 != w) {
    				edges.add(new Edge(pixelID, pixelID + 1));
    			}
    			if (i + 1 != h) {
    				edges.add(new Edge(pixelID, pixelID + w));
    			}
    		}
    	}
    	PriorityQ<Edge> sortedEdges = new PriorityQ<Edge>(edges);
    	return sortedEdges;
    }
    
    // Maps and labels each component
    void mapTrees() {
    	componentCount = 0;
    	componentNumber = new HashMap<Integer, Integer>();
    	for (int i = 0; i < h; i++) {
    		for (int j = 0; j < w; j++) {
    			if (parentID[i][j] == -1) {
    				componentNumber.put(i*w + j, componentCount);
    				componentCount++;
    			}
    		}
    	}
    }

    // Recolors each connected component using progressive colors 
    void progressiveScan() {
    	ProgressiveColors p = new ProgressiveColors();
    	for (int i = 0; i < h; i++) {
    		for (int j = 0; j < w; j++) {
    			int k = componentNumber.get(find(i*w + j));
    			int[] color = p.progressiveColor(k);
    			putPixel(biWorking, j, i, color[0], color[1], color[2]); 
    		}
    	}
    }
    
    // Accepts two pixelIDs, assumes they are roots.
    // Unions the two. The smaller pixelID becomes the parent.
    void union(int pixelID1, int pixelID2) {
    	if (pixelID1 < pixelID2) {
    		parentID[getYcoord(pixelID2)][getXcoord(pixelID2)] = pixelID1;
        	unionCount++;
    	} else if (pixelID1 > pixelID2){
    		parentID[getYcoord(pixelID1)][getXcoord(pixelID1)] = pixelID2;
        	unionCount++;
    	}
    }
    
    // Accepts a pixelID. Traces connected up-tree to the root, and returns
    // that root's pixelID.
    int find(int pixelID) {
    	callsOnFind++;
    	int x = getXcoord(pixelID);
    	int y = getYcoord(pixelID);
    	while (parentID[y][x] != -1) {
        	int parent = parentID[y][x];
    		x = getXcoord(parent);
    		y = getYcoord(parent);
    		linkTraversalCount++;
    	}
    	return y*w + x;
    }      
    
    // Accepts a pixelID. Traces connected up-tree to the root, compresses
    // paths of all children, then returns that root's pixelID.
    int findCompress(int pixelID) {
    	callsOnFind++;
    	int x = getXcoord(pixelID);
    	int y = getYcoord(pixelID);
    	if (parentID[y][x] != -1) {
    		Stack<Integer> pixels = new Stack<Integer>();
        	pixels.add(pixelID);
	    	while (parentID[y][x] != -1) {
	        	int parent = parentID[y][x];
	        	pixels.add(parent);
	    		x = getXcoord(parent);
	    		y = getYcoord(parent);
	    		linkTraversalCount++;
	    	}
	    	int root = y*w + x;
	    	while (!pixels.isEmpty()) {
	    		union(root, pixels.pop());
	    	}
	    	return root;
    	} else {
    		return pixelID;
    	}
    }      
    
    // Accepts a pixelID and returns it's corresponding x coordinate
    int getXcoord(int pixelID) {
    	return pixelID % w;
    }
    
    // Accepts a pixelID and returns it's corresponding y coordinate
    int getYcoord(int pixelID) {
    	return pixelID / w;
    }
    
    /* This main method can be used to run the application. */
    public static void main(String s[]) {
        appInstance = new ImageComponents();
    }
}
