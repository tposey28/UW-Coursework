/*
 * ImageAnalyzer.java
 * A3 Solution by Taylor Posey. UWNetID: tposey28
 * 
 * 
 * See also the file CustomHashtable.java for use in the extra credit options.
 * CSE 373, University of Washington, Autumn 2014.
 * 
 * Starter Code for CSE 373 Assignment 3, Part II.    Starter Code Version 1.0.
 * S. Tanimoto,  with contributions from J. Goh, Oct 21, 2014.
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

//import ImageAnalyzer.Block;

public class ImageAnalyzer extends JFrame implements ActionListener {
	public static ImageAnalyzer appInstance; // Used in main().

	String startingImage = "UW-Campus-1961.jpg";
	BufferedImage biTemp, biWorking, biFiltered; // These hold arrays of pixels.
	Graphics gOrig, gWorking; // Used to access the drawImage method.
	int w; // width of the current image.
	int h; // height of the current image.
	int blockSize; // Controls how much colors are grouped during palette building.
	int hashFunctionChoice; // Either 1, 2, or 3. Controls whether to use h1, h2, or h3.

	public HashMap<Block, Integer> javaHashMap; // For storing blocks:weights
	public CustomHashtable<Block, Integer> customHashtable; // Used only if extra credit options are implemented.

	public ArrayList<Block> sortedBlocks; // to store sorted blocks(list L)
	public Color[] palette;	// stores the first U elements of list L
	public int[][] encodedPixels;	// to store the value each pixel in the image is encoded to

	JPanel viewPanel; // Where the image will be painted.
	JPopupMenu popup;
	JMenuBar menuBar;
	JMenu fileMenu, imageOpMenu, paletteMenu, encodeMenu, hashingMenu, helpMenu;
	JMenuItem loadImageItem, saveAsItem, exitItem;
	JMenuItem lowPassItem, highPassItem, photoNegItem, RGBThreshItem;
	JMenuItem createPItem2, createPItem4, createPItem16, createPItem256, selectBItem4, selectBItem8, selectBItem16;
	JMenuItem encodeSSItem, encodeFItem, decodeItem;
	JMenuItem hashFunctionItem1, hashFunctionItem2, hashFunctionItem3;
	JMenuItem hashtableItem1, hashtableItem2, hashtableItem3;
	JMenuItem aboutItem, helpItem;

	JFileChooser fileChooser; // For loading and saving images.

	public class Color {
		int r, g, b; // color coordinates

		Color(int r, int g, int b) {
			this.r = r; this.g = g; this.b = b;    		
		}
		
		// Computes and returns the Euclidean distance to the coordinates
		// of the given second Color
		double euclideanDistance(Color c2) {
			return Math.sqrt((r - c2.r)*(r - c2.r) + 
							 (g - c2.g)*(g - c2.g) + 
							 (b - c2.b)*(b - c2.b));
		}
      
      public String toString() {
			return "( "+r+", "+g+", "+b+")";
		}
	}

	public class Block {
		int br, bg, bb; // block coordinates
		
		// Constructs a Block for the given Color. The block depends on
		// the given size, chosen by the user
		public Block(Color pixel) {
			this.br = pixel.r / blockSize;
			this.bg = pixel.g / blockSize;
			this.bb = pixel.b / blockSize;
		}

		// Computes and returns this block's representative color. The
		// resulting color depends on the size of the block, given by the
		// user
		public Color repColor() {
			int s = blockSize;
			return new Color(s*br + s/2, s*bg + s/2, s*bb + s/2);
		}
		
		// Returns this Block's hash value. The hash function used depends
		// on the function the user has chosen
		public int hashCode() {
			if (hashFunctionChoice == 1) {
				return h1(this);
			} else if (hashFunctionChoice == 2) {
				return h2(this);
			} else if (hashFunctionChoice == 3) {
				return h3(this);
			} else {
				return -1; // This should never happen.
			}
		}
		
		// Returns a String representation of this Block
		public String toString() {
			return "( "+br+", "+bg+", "+bb+")";
		}
		
		// Returns a boolean representing if this Block is equivalent to
		// the given Object
		public boolean equals(Object obj) {
			if (obj instanceof Block) {
				Block b2 = (Block) obj;
				return br == b2.br && bg == b2.bg && bb == b2.bb;
			} else {
        	 	return false;
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

	public ImageAnalyzer() { // Constructor for the application.
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
		paletteMenu = new JMenu("Palettes");
		encodeMenu = new JMenu("Encode");
		hashingMenu = new JMenu("Hashing");
		helpMenu = new JMenu("Help");
		menuBar.add(fileMenu);
		menuBar.add(imageOpMenu);
		menuBar.add(paletteMenu);
		menuBar.add(encodeMenu);
		menuBar.add(hashingMenu);
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

		// Create the Palette menu items.
		createPItem2 = new JMenuItem("Create Palette of Size 2");
		createPItem2.addActionListener(this);
		paletteMenu.add(createPItem2);
		createPItem4 = new JMenuItem("Create Palette of Size 4");
		createPItem4.addActionListener(this);
		paletteMenu.add(createPItem4);
		createPItem16 = new JMenuItem("Create Palette of Size 16");
		createPItem16.addActionListener(this);
		paletteMenu.add(createPItem16);
		createPItem256 = new JMenuItem("Create Palette of Size 256");
		createPItem256.addActionListener(this);
		paletteMenu.add(createPItem256);
		selectBItem4 = new JCheckBoxMenuItem("Set block size to 4x4x4", true);
		selectBItem4.addActionListener(this);
		paletteMenu.add(selectBItem4);
		selectBItem8 = new JCheckBoxMenuItem("Set block size to 8x8x8");
		selectBItem8.addActionListener(this);
		paletteMenu.add(selectBItem8);
		selectBItem16 = new JCheckBoxMenuItem("Set block size to 16x16x16");
		selectBItem16.addActionListener(this);
		paletteMenu.add(selectBItem16);

		// Create the Encode menu items.
		encodeSSItem = new JMenuItem("Encode: Slow and Simple");
		encodeSSItem.addActionListener(this);
		encodeMenu.add(encodeSSItem);
		encodeSSItem.setEnabled(false);

		encodeFItem = new JMenuItem("Encode: Fast");
		encodeFItem.addActionListener(this);
		encodeMenu.add(encodeFItem);
		encodeFItem.setEnabled(false);

		decodeItem = new JMenuItem("Decode");
		decodeItem.addActionListener(this);
		encodeMenu.add(decodeItem);
		decodeItem.setEnabled(false);

		// Create the Hashing menu items.
		hashFunctionItem1 = new JCheckBoxMenuItem("Use Hash Function H1");
		hashFunctionItem1.addActionListener(this);
		hashingMenu.add(hashFunctionItem1);

		hashFunctionItem2 = new JCheckBoxMenuItem("Use Hash Function H2");
		hashFunctionItem2.addActionListener(this);
		hashingMenu.add(hashFunctionItem2);

		hashFunctionItem3 = new JCheckBoxMenuItem("Use Hash Function H3", true);
		hashFunctionItem3.addActionListener(this);
		hashingMenu.add(hashFunctionItem3);

		hashtableItem1 = new JCheckBoxMenuItem("Use Java's Hashtable class.", true);
		hashtableItem1.addActionListener(this);
		hashingMenu.add(hashtableItem1);
		hashtableItem1.setEnabled(false);

		hashtableItem2 = new JCheckBoxMenuItem("Use custom hashtable class and linear probing.");
		hashtableItem2.addActionListener(this);
		hashingMenu.add(hashtableItem2);
		hashtableItem2.setEnabled(false);

		hashtableItem3 = new JCheckBoxMenuItem("Use custom hashtable class and quadratic probing.");
		hashtableItem3.addActionListener(this);
		hashingMenu.add(hashtableItem3);
		hashtableItem3.setEnabled(false);

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
		hashFunctionChoice = 3; // Default hash function number is 1.
		blockSize = 4; // Default blockSize is 4x4x4
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

	void handlePaletteMenu(JMenuItem mi){
		System.out.println("A palette menu item was selected.");
		if (mi==createPItem2) { buildPalette(2); }
		else if (mi==createPItem4) { buildPalette(4); }
		else if (mi==createPItem16) { buildPalette(16); }
		else if (mi==createPItem256) { buildPalette(256); }
		else if (mi==selectBItem4) { setBlockSize(4); }
		else if (mi==selectBItem8) { setBlockSize(8); }
		else if (mi==selectBItem16) { setBlockSize(16); }
	}

	void handleEncodeMenu(JMenuItem mi){
		System.out.println("An encode menu item was selected.");
		if (mi==encodeSSItem) { encodeSlowAndSimple(); }
		else if (mi==encodeFItem) { encodeFast(); }
		else if (mi==decodeItem) { decode(); }
	}

	void handleHashingMenu(JMenuItem mi){
		System.out.println("A hashing menu item was selected.");
		if (mi==hashFunctionItem1) { setHashFunctionChoice(1); }
		else if (mi==hashFunctionItem2) { setHashFunctionChoice(2); }
		else if (mi==hashFunctionItem3) { setHashFunctionChoice(3); }
	}

	void handleHelpMenu(JMenuItem mi){
		System.out.println("A help menu item was selected.");
		if (mi==aboutItem) {
			System.out.println("About: Well this is my program.");
			JOptionPane.showMessageDialog(this,
					"Image Analyzer, Starter-Code Version.",
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
			if (m==paletteMenu) { handlePaletteMenu(mi); return; }
			if (m==encodeMenu)  { handleEncodeMenu(mi);  return; }
			if (m==hashingMenu) { handleHashingMenu(mi); return; }
			if (m==helpMenu)    { handleHelpMenu(mi);    return; }
		} else {
			System.out.println("Unhandled ActionEvent: "+e.getActionCommand());
		}
	}
	
	// Sets hash function using given integer
	// Assumes valid input
	public void setHashFunctionChoice(int hc) {
		hashFunctionChoice = hc;
		System.out.println("Hash function choice is now "+hashFunctionChoice);
		// Uncheck all menu items that are not for newly chosen function
		if (hc != 1) {
			((JCheckBoxMenuItem)hashFunctionItem1).setState(false);
		}
		if (hc != 2) {
			((JCheckBoxMenuItem)hashFunctionItem2).setState(false);
		}
		if (hc != 3) {
			((JCheckBoxMenuItem)hashFunctionItem3).setState(false);
		}
	}

	// Sets block size using given integer
	// Assumes valid input
	public void setBlockSize(int bs) {
		blockSize = bs;
		// Uncheck all menu items that are not for newly chosen block size
		if (bs != 4) {
			((JCheckBoxMenuItem)selectBItem4).setState(false);
		}
		if (bs != 8) {
			((JCheckBoxMenuItem)selectBItem8).setState(false);
		}
		if (bs != 16) {
			((JCheckBoxMenuItem)selectBItem16).setState(false);
		}
	}

	// Creates the color palette needed for encryption and decryption
	// Palette size is determined by given integer, assumes valid input
	public void buildPalette(int paletteSize) {
		long startTime = System.nanoTime();
		// Create our (block, count) hash map
		javaHashMap = new HashMap<Block, Integer>();
		// Convert image to 2D array of RGB pixels
		Color[][] pixels = storeCurrPixels(biWorking);
		for (int i = 0; i < h; i++) { // For each pixel column
			for (int j = 0; j < w; j++) { // look at each pixel
				// compute pixel's block, then add to hash map with updated count
				Block currBlock = new Block(pixels[i][j]);
				if (!javaHashMap.containsKey(currBlock)) {
					javaHashMap.put(currBlock, 1);
				} else {
					int count = javaHashMap.get(currBlock);
					javaHashMap.put(currBlock, count++);
				}
			}
		}
		// Sort blocks based on frequency, create palette with representative colors.
		// If, for some reason, there are less colors than the given palette size,
		// creates palette with the maximum number of colors available
		sortedBlocks = sort(javaHashMap);
		if (sortedBlocks.size() < paletteSize) {
			palette = new Color[sortedBlocks.size()];
		} else {
			palette = new Color[paletteSize];
		}
		for (int i = 0; i < palette.length; i++) {
			Color repColor = sortedBlocks.get(i).repColor();
			palette[i] = repColor;
		}
		// Enable encode options
		encodeSSItem.setEnabled(true);
		encodeFItem.setEnabled(true);
		// Report statistics
		System.out.println("Current hash function: " + hashFunctionChoice);
		System.out.println("Number of pixels in image: " + (h * w));
		System.out.println("Number of distinct color bins: " + javaHashMap.keySet().size());
		System.out.println("Time to build table: " + (System.nanoTime() - startTime));
		System.out.println();
	}

	// returns a sorted(largest weight to smallest weight) ArrayList of the blocks in HashMap<Block, Integer>
	public ArrayList<Block> sort(final HashMap<Block, Integer> map) {
		ArrayList<Block> arr = new ArrayList<>();
		for (Block b : map.keySet()) {
			arr.add(b);
		}
		Collections.sort(arr, new Comparator<Block> (){
			public int compare(Block b1, Block b2) {
				return map.get(b2) - map.get(b1);
			}
		});
		return arr;
	}

	// Encodes the current image "slowly", by finding, for each pixel, the
	// palette color that minimizes distance and encoding accordingly
	public void encodeSlowAndSimple() {
		long startTime = System.nanoTime();
		// Initialize encoded array, and convert image into a Color array
		// of RGB values
		encodedPixels = new int[h][w];
		Color[][] pixels = storeCurrPixels(biWorking);
		for (int i = 0; i < h; i++) { // For each pixel column
			for (int j = 0; j < w; j++) { // look at each pixel
				// and minimize the distance, noting the palette index
				int repIndex = -1;
				double minDist = 442;
				for (int k = 0; k < palette.length; k++) {
					double dist = pixels[i][j].euclideanDistance(palette[k]);
					if (minDist > dist) {
						minDist = dist;
						repIndex = k;
					}
				} // Add this to the encoded image array
				encodedPixels[i][j] = repIndex;
			}
		}
		// Enable decode and fast encode options, disable slow encode
		decodeItem.setEnabled(true);
		encodeFItem.setEnabled(true);
		encodeSSItem.setEnabled(false);
		// Report statistics
		System.out.println("Time to \"slowly\" encrypt image: " + (System.nanoTime() - startTime));
		System.out.println();
		}
	
	// Encodes the current image "quickly", by finding for each block the 
	// palette color that minimizes distance, mapping the two, and then
	// encoding accordingly
	public void encodeFast() {  
		long startTime = System.nanoTime();
		// The most frequent blocks were used to create the palette, and
		// thus can be easily mapped to
		for (int i = 0; i < palette.length; i++) {
			javaHashMap.put(sortedBlocks.get(i), i);
		}
		// For the rest, map them to the palette color that is closest
		for (int i = palette.length; i < sortedBlocks.size(); i++) {
			int repIndex = -1;
			double minDist = 442;
			Color repColor = sortedBlocks.get(i).repColor();
			for (int j = 0; j < palette.length; j++) {
				double dist = repColor.euclideanDistance(palette[j]);
				if (minDist > dist) {
					minDist = dist;
					repIndex = j;
				}
			}
			javaHashMap.put(sortedBlocks.get(i), repIndex);
		}
		// Initialize encoded array, and convert image into a Color array
		// of RGB values
		encodedPixels = new int[h][w];
		Color[][] pixels = storeCurrPixels(biWorking);
		for (int i = 0; i < h; i++) { // For each pixel column
			for (int j = 0; j < w; j++) { // look at each pixel
				// and encode it using the newly formed mappings
				Block temp = new Block(pixels[i][j]);
				encodedPixels[i][j] = javaHashMap.get(temp);
			}
		}
		// Enable decode and slow encode option, disable fast encode
		decodeItem.setEnabled(true);
		encodeSSItem.setEnabled(true);
		encodeFItem.setEnabled(false);
		// Report statistics
		System.out.println("Time to \"quickly\" encrypt image: " + (System.nanoTime() - startTime));
		System.out.println();
	}

	// Decodes encrypted image using palette, updates application image
	public void decode() {
		long startTime = System.nanoTime();
		// Convert image to an array of RGB values
		Color[][] originalPixels = storeCurrPixels(biWorking);
		for (int i = 0; i < h; i++) { // For each pixel column
			for (int j = 0; j < w; j++) { // look at each pixel
				// and decode it with the palette
				putPixel(biWorking, j, i, palette[encodedPixels[i][j]]);
			}
		}
		// Update the application image
		repaint();
		// Disable decoding and encoding
		decodeItem.setEnabled(false);
		encodeSSItem.setEnabled(false);
		encodeFItem.setEnabled(false);
		// Report statistics
		System.out.println("Time to decode image: " + (System.nanoTime() - startTime));
		System.out.println("Average per-pixel encoding error = " + computeError(originalPixels, biWorking));
	}

	// Returns an array of Colors based on the pixels from a BufferedImage
	Color[][] storeCurrPixels(BufferedImage bi) {
		Color[][] pixels = new Color[h][w];
		for (int row = 0; row < h; row++) {
			for (int col = 0; col < w; col++) {
				int rgb = bi.getRGB(col, row);
				int red = (rgb & 0x00ff0000) >> 16;
				int green = (rgb & 0x0000ff00) >> 8;
				int blue = rgb & 0x000000ff;

				pixels[row][col] = new Color(red, green, blue);
			}
		}
		return pixels;
	}

	// Computes the average pixel encoding error between a pixel array and the pixels in a BufferedImage
	double computeError(Color[][] pixels, BufferedImage bi) {
		double totalError = 0.0;
		for (int row = 0; row < h; row++) {
			for (int col = 0; col < w; col++) {
				int rgb = bi.getRGB(col, row);
				int red = (rgb & 0x00ff0000) >> 16;
				int green = (rgb & 0x0000ff00) >> 8;
				int blue = rgb & 0x000000ff;

				totalError += pixels[row][col].euclideanDistance(new Color(red, green, blue));
			}
		}
		return totalError / (h * w);
	}

	// Use this to put a color into a pixel of a BufferedImage object.
	void putPixel(BufferedImage bi, int x, int y, Color c) {
		int rgb = (c.r << 16) | (c.g << 8) | c.b; // pack 3 bytes into a word.
		bi.setRGB(x,  y, rgb);
	}
	
	// First hash function option
	public int h1(Block b) {
		return b.br ^ b.bg ^ b.bb;
	}
	
	// First hash function option
	public int h2(Block b) {
		return 1024 * b.br + 32 * b.bg + b.bb;
	}
	
	// First hash function option
	public int h3(Block b) {
		return b.toString().hashCode();
	}

	/* This main method can be used to run the application. */
	public static void main(String s[]) {
		appInstance = new ImageAnalyzer();
	}
}
