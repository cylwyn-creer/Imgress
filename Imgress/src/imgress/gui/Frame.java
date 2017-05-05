package imgress.gui;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

import imgress.datastructure.Node;
import imgress.datastructure.Tree;
import imgress.datastructure.TreeFrequencyComparator;

public class Frame extends JFrame {
	
	private static final long serialVersionUID = 42L;
	private JMenuBar menuBar;
	private JMenu fileMenu, toolsMenu, helpMenu, trainMenu;
	private JMenuItem openItem, closeItem, exitItem;
	private JMenuItem compressItem, renderItem;
	private JMenuItem helpItem, aboutItem;
	private JMenuItem updateItem, newItem;
	private JFileChooser fileChooser;
	private JDesktopPane desktopPane;
	
	private JInternalFrame imgFrame;
	private JInternalFrame compFrame;
	private JInternalFrame helpFrame;
	private JInternalFrame aboutFrame;
	
	private JFrame frame;
	
	private Comparator<Tree> comparator = new TreeFrequencyComparator();
	private PriorityQueue<Tree> pqueue;
	
	private JLabel origImageL, compImageL;
	private JPanel origImagePanel, compImagePanel;
	private JScrollPane imgPane, compPane, helpPane, aboutPane;
	private ImageIcon origImage, compImage;
	
	private File file;
	private MenuEventHandler handler = new MenuEventHandler(); 
	private HashMap<Integer, Integer> hmap;
	private HashMap<Integer, String> hmap2;
	private HashMap<String, Integer> hmap3;
	
	private Tree huffmanTree;
	private BufferedImage image;
	
	private int width = 0;
	private int height = 0;
	private int bitlength = 0;
	
	private JButton done;
	private JLabel progressL;
	private JProgressBar progress;
	
	private JFrame progressDialog;
	
	private Compress compressTask;
	
	private PropertyChangeHandler property;
	
	private int minCodeLength = 0;
	
	public Frame() {
		
		super("Imgress");
		
		try {
			UIManager.setLookAndFeel(new NimbusLookAndFeel());
			SwingUtilities.updateComponentTreeUI(this);
		} 
		catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		frame = this;
		
		menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		fileMenu = new JMenu("File");
		fileMenu.setMnemonic('F');
		
		openItem = new JMenuItem("Open PNG Image");
		openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
		openItem.addActionListener(handler);
		
		closeItem = new JMenuItem("Close");
		closeItem.addActionListener(handler);
		closeItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK));
		
		exitItem = new JMenuItem("Exit");
		exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_MASK));
		exitItem.addActionListener(handler);
		
		fileMenu.add(openItem);
		fileMenu.add(closeItem);
		fileMenu.add(exitItem);
		
		toolsMenu = new JMenu("Tools");
		toolsMenu.setMnemonic('T');	
		toolsMenu.addActionListener(handler);
		
		compressItem = new JMenuItem("Compress");
		compressItem.addActionListener(handler);
		compressItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK));
		
		newItem = new JMenuItem("Create New Huffman Tree");
		newItem.addActionListener(handler);
		
		updateItem = new JMenuItem("Update Huffman Tree");
		updateItem.addActionListener(handler);
		
		renderItem = new JMenuItem("Render Huffman Image");
		renderItem.addActionListener(handler);
		renderItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_MASK));
		
		trainMenu = new JMenu("Train");
		trainMenu.setMnemonic('T');	
		
		trainMenu.add(newItem);
		trainMenu.add(updateItem);
		
		toolsMenu.add(trainMenu);
		toolsMenu.add(compressItem);
		toolsMenu.add(renderItem);
		
		helpMenu = new JMenu("Help");
		helpMenu.setMnemonic('H');
		
		aboutItem = new JMenuItem("About");
		aboutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_MASK));
		aboutItem.addActionListener(handler);
		
		helpItem = new JMenuItem("Help");
		helpItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
		helpItem.addActionListener(handler);
		
		helpMenu.add(aboutItem);
		helpMenu.add(helpItem);
		
		menuBar.add(fileMenu);
		menuBar.add(toolsMenu);
		menuBar.add(helpMenu);
		
		desktopPane = new JDesktopPane();
		
		imgFrame = new JInternalFrame("Image", true, true, true, true);
		imgFrame.setSize(600, 400);
		imgFrame.hide();
		imgFrame.setDefaultCloseOperation(HIDE_ON_CLOSE);
		
		origImageL = new JLabel();
		
		origImagePanel = new JPanel();
		origImagePanel.add(origImageL);
		
		imgPane = new JScrollPane(origImagePanel);
		
		imgFrame.add(imgPane);
		
		
		compFrame = new JInternalFrame("Compressed Image", true, true, true, true);
		compFrame.setSize(600, 400);
		compFrame.hide();
		compFrame.setDefaultCloseOperation(HIDE_ON_CLOSE);
		
		compImageL = new JLabel();
		
		compImagePanel = new JPanel();
		compImagePanel.add(compImageL);
		
		compPane = new JScrollPane(compImagePanel);
		
		compFrame.add(compPane);
		
		helpFrame = new JInternalFrame("Help", true, true, true, true);
		helpFrame.setSize(500, 400);
		helpFrame.hide();
		helpFrame.setDefaultCloseOperation(HIDE_ON_CLOSE);
		
		aboutFrame = new JInternalFrame("About", true, true, true, true);
		aboutFrame.setSize(500, 400);
		aboutFrame.hide();
		aboutFrame.setDefaultCloseOperation(HIDE_ON_CLOSE);
		
		desktopPane.add(imgFrame);
		desktopPane.add(compFrame);
		desktopPane.add(helpFrame);
		desktopPane.add(aboutFrame);
		
		add(desktopPane);
		
		setIconImage(new ImageIcon(this.getClass().getResource("/res/image/icon.png")).getImage());
		setSize(800, 500);
		setVisible(true);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		
		addWindowListener(new WindowAdapter() {
			
			@Override
			public void windowClosing(WindowEvent e) {
				
				int button = JOptionPane.showConfirmDialog(null,"Are you sure?", "Warning", JOptionPane.YES_NO_OPTION);
				
				if(button == JOptionPane.YES_OPTION)
					System.exit(0);
				
			}
			
		});
	}
	
	public void initProgressBar() {
		progressDialog = new JFrame();
		
		progressL = new JLabel();
		progressDialog.add(progressL);
		
		progress = new JProgressBar(0, 100);
		progress.setValue(0);
		progress.setStringPainted(true);
		progressDialog.add(progress);
		
		done = new JButton("Done");
		done.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				progressDialog.dispose();
			}
		});
		done.setEnabled(false);
		progressDialog.add(done);
		
		property = new PropertyChangeHandler();
		
		progressDialog.pack();
		progressDialog.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	private class MenuEventHandler implements ActionListener {
		
		public void actionPerformed(ActionEvent event) {
			
			JFileChooser chooser = new JFileChooser();
			chooser.setAcceptAllFileFilterUsed(false);
			int option, button, option2;
			
			if(event.getActionCommand() == exitItem.getActionCommand()) {
				
				button = JOptionPane.showConfirmDialog(null,"Are you sure?", "Warning", JOptionPane.YES_NO_OPTION);
				
				if(button == JOptionPane.YES_OPTION)
					System.exit(0);
				
			}
			
			if(event.getActionCommand() == openItem.getActionCommand()) {
				
				getFileOrDirectory();
				
			}
			
			if(event.getActionCommand() == helpItem.getActionCommand()) {
				
				helpFrame.show();
				try {
					helpFrame.setSelected(true);
				} catch (PropertyVetoException e) {
					e.printStackTrace();
				}
			
				
			}
			
			if(event.getActionCommand() == aboutItem.getActionCommand()) {
				
				aboutFrame.show();
				try {
					aboutFrame.setSelected(true);
				} catch (PropertyVetoException e) {
					e.printStackTrace();
				}
				
			}
			
			if(event.getActionCommand() == newItem.getActionCommand()) {
				
				if(desktopPane.getSelectedFrame() == imgFrame) {
					
					chooser.setDialogTitle("Save in");
					chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					
					option = chooser.showSaveDialog(Frame.this);
					
					if(option == JFileChooser.APPROVE_OPTION) {
						
						long strt = System.currentTimeMillis();
						createNewHuffman(chooser.getSelectedFile());
						long time = System.currentTimeMillis() - strt;
						System.out.println("Done time: " + time);
									
					} 
					
				} else {
					
					JOptionPane.showMessageDialog(frame, "No image selected!", "Imgress", JOptionPane.ERROR_MESSAGE);
					
					
				}
				
			}
			
			if(event.getActionCommand() == updateItem.getActionCommand()) {
				
				if(desktopPane.getSelectedFrame() == imgFrame) {
					
					chooser.setDialogTitle("Update file");
					chooser.setFileFilter(new FileNameExtensionFilter("HUFF File", "huff"));
					
					option = chooser.showOpenDialog(Frame.this);
					
					if(option == JFileChooser.APPROVE_OPTION) {
						
						if(chooser.getSelectedFile().getName().endsWith(".huff")) {
							//update here
						}
						else {
							JOptionPane.showMessageDialog(frame, "File format not supported!", "Invalid file", JOptionPane.ERROR_MESSAGE);
						}
						
					}
					
				} else {
					
					JOptionPane.showMessageDialog(frame, "No image selected!", "Imgress", JOptionPane.ERROR_MESSAGE);
					
				}
				
				
			}
			
			if(event.getActionCommand() == renderItem.getActionCommand()) {
				
				chooser.setDialogTitle("Choose huff file");
				chooser.setFileFilter(new FileNameExtensionFilter("HUFF File", "huff"));
				
				option = chooser.showOpenDialog(Frame.this);
				
				File huffFile = chooser.getSelectedFile();
				
				if(option == JFileChooser.APPROVE_OPTION) {
					
					chooser.setDialogTitle("Choose himg file");
					chooser.setFileFilter(new FileNameExtensionFilter("HIMG File", "himg"));
					
					option2 = chooser.showOpenDialog(Frame.this);
					
					File himgFile = chooser.getSelectedFile();
					
					if(option2 == JFileChooser.APPROVE_OPTION) {
						long strt = System.currentTimeMillis();
						decompress(huffFile, himgFile);
						long time = System.currentTimeMillis() - strt;
						System.out.println("Done time: " + time);
					}
					
				}
				
			}
			
			if(event.getActionCommand() == compressItem.getActionCommand()) {
			
				if(desktopPane.getSelectedFrame() == imgFrame) {
					
					chooser.setDialogTitle("Save in");
					chooser.setFileFilter(new FileNameExtensionFilter("HUFF File", "huff"));
					
					option = chooser.showOpenDialog(Frame.this);
					
					if(option == JFileChooser.APPROVE_OPTION) {
						
						if(chooser.getSelectedFile().getName().endsWith(".huff"))
							try {
								
								image = ImageIO.read(file);
								/*
								long strt = System.currentTimeMillis();
								compress(chooser.getSelectedFile());
								generateBitString(image, chooser.getSelectedFile());
								long time = System.currentTimeMillis() - strt;
								System.out.println("Done time: " + time);
								*/
								
								initProgressBar();
								compressTask = new Compress(chooser.getSelectedFile(), image);
								compressTask.addPropertyChangeListener(property);
								progressDialog.setTitle("Compressing Image...");
								progressDialog.setVisible(true);
								compressTask.execute();
								
							} catch (IOException e) {
								e.printStackTrace();
							}
						else 
							JOptionPane.showMessageDialog(frame, "File format not supported!", "Invalid file", JOptionPane.ERROR_MESSAGE);		
					}
					
				} else {
					
					JOptionPane.showMessageDialog(frame, "No image selected!", "Imgress", JOptionPane.ERROR_MESSAGE);
					
				}
				
			}
			
			if(event.getActionCommand() == closeItem.getActionCommand()) {
				
				if(!desktopPane.getSelectedFrame().isIcon()) 
					desktopPane.getSelectedFrame().hide();
				
			}
			
		}
		
	}
	
	public void getFileOrDirectory() {
		
		fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fileChooser.setFileFilter(new FileNameExtensionFilter("PNG Image", "png"));
		fileChooser.setAcceptAllFileFilterUsed(false);
		
		int result = fileChooser.showOpenDialog(this);
		
		if(result == JFileChooser.CANCEL_OPTION) {
			
			fileChooser.cancelSelection();
			
		} else {
			
			try{
				
				file = fileChooser.getSelectedFile();
				String fileName = file.getName().toUpperCase();
				
				System.out.println("file choosen: " + file + "\nfile: " + fileName);
				
				if((fileName.equals("")) || !fileName.endsWith(".PNG")) {
					
					JOptionPane.showMessageDialog(this, "File format not supported!", "Invalid File", JOptionPane.ERROR_MESSAGE);
					
				} else {
					
					openImage(file);
					
				}
				
			} catch(Exception ex) {
				
				JOptionPane.showMessageDialog(this, "File format not supported!", "Invalid File", JOptionPane.ERROR_MESSAGE);
				
			} 
			
		}

	}
	
	public void createNewHuffman(File dir) {
		
		hmap = new HashMap<Integer, Integer>();
		FileWriter fileWriter;
		BufferedWriter buff;
		String filename = dir.getPath() + "/" + file.getName().substring(0, file.getName().length() - 3) + "huff";
		
		File name = new File(filename);
		
		try {
			
			image = ImageIO.read(file);
			
			int w = image.getWidth();
			int h = image.getHeight();

			int[] dataBuffInt = image.getRGB(0, 0, w, h, null, 0, w); 
			
			int freq;
			
			for(int i = 0; i < dataBuffInt.length; i++) {
				
				if(hmap.containsKey(dataBuffInt[i])) {
					
					freq = hmap.get(dataBuffInt[i]);
					
					hmap.remove(dataBuffInt[i]);
					hmap.put(dataBuffInt[i], freq+1);
					
				} else {
					
					hmap.put(dataBuffInt[i], 1);
					
				}
				
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			
			fileWriter = new FileWriter(name);
			buff = new BufferedWriter(fileWriter);

			Set set = hmap.entrySet();
			Iterator iterator = set.iterator();
			while(iterator.hasNext()) {
				
				Map.Entry entry = (Map.Entry)iterator.next();
				buff.write(entry.getKey() + " " + entry.getValue());
				buff.newLine();
				
			}
			
			buff.close();
			
		} catch (IOException e) {
			
			e.printStackTrace();
			
		}
		
	}
	
	public void updateFile(File huffFile) {
		
		Scanner read;
		hmap = new HashMap<Integer, Integer>();
		
		try {
			
			read = new Scanner(huffFile);
			
			while(read.hasNext()) {
				
				
				
			}
			
			read.close();
			
			
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
			
		}
		
	}
	
	public void loadHuffFile(File huffFile) {
		hmap = new HashMap<Integer, Integer>();
		pqueue = new PriorityQueue<Tree>(10, comparator);
		Scanner read;
		
		try {
			
			read = new Scanner(huffFile);
			
			while(read.hasNext()) {
				int key = read.nextInt();
				int freq = read.nextInt();
				
				hmap.put(key, freq);
				
				Node newNode = new Node(key, freq, true);
				Tree newTree = new Tree();
				newTree.setRoot(newNode);
				
				pqueue.offer(newTree);
				
			}
			
			read.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void compress(File huffFile) throws IOException {
		
		loadHuffFile(huffFile);
		
		huffmanTree = createHuffmanTree(pqueue);
		
		System.out.println("Huffman Coding");
		Set set = hmap2.entrySet();
		Iterator iterator = set.iterator();
		while(iterator.hasNext()) {
			
			Map.Entry entry = (Map.Entry)iterator.next();
			System.out.println(entry.getKey() + " " + entry.getValue());
			
		}
		
	}
	
	public void decompress(File huffFile, File himgFile) {
		
		loadHuffFile(huffFile);
		
		huffmanTree = createHuffmanTree(pqueue);
		
		Set set = hmap2.entrySet();
		Iterator iterator = set.iterator();
		boolean tracked = false;
		
		while(iterator.hasNext()) {
			Map.Entry entry = (Map.Entry)iterator.next();
			
			String val = (String) entry.getValue();
			
			if(tracked == false) {
				minCodeLength = val.length();
				
				tracked = true;
			}
			else {
				if(val.length() < minCodeLength) {
					minCodeLength = val.length();
				}
			}
			
			hmap3.put((String) entry.getValue(), (Integer) entry.getKey());
			
		}
		
		image = render(loadHimgFile(himgFile));

		openHimg(himgFile);
		
	}
	
	public String loadHimgFile(File himgFile) {
		String bitstring = "";
		
		try {
			DataInputStream in = new DataInputStream(new FileInputStream(himgFile));
			FileWriter wr = new FileWriter(new File(himgFile.toString().substring(0, himgFile.toString().length() - 5) + ".new"));
			
			width = in.readInt();
			height = in.readInt();
			
			String temp = "";
			while(true) {

				try {
					temp = Integer.toBinaryString(in.readInt());
				
					int pad = 0;
					if(temp.length() % 31 != 0) {
						pad = 31 - (temp.length() % 31);
					}
				
					for(int a = 0; a < pad; a++) {
						temp = '0' + temp;
					}
				
					bitstring = bitstring + temp;
				}
				catch(EOFException ex) {
					bitlength = Integer.parseInt(temp, 2);
					temp = bitstring.substring(bitstring.length() - (31 + bitlength), bitstring.length() - 31);
					
					bitstring = bitstring.substring(0, bitstring.length() - 62);
					bitstring = bitstring + temp;
					
					break;
				}
			}
			
			wr.write(bitstring);
			in.close();
			wr.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		return bitstring;
	}
	
	public BufferedImage render(String huffmanCode) {
		BufferedImage huffmanImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		int x = 0;
		int y = 0;
		int startIndex = 0;
		
		String code = "";
		/*
		for(int i = 0; i < huffmanCode.length(); i++) {
			System.out.println(i);
			code = huffmanCode.substring(startIndex, i + 1);
			
			if(hmap3.containsKey(code)) {
				huffmanImage.setRGB(x++, y, hmap3.get(code));
				
				startIndex = i + 1;
				
				if(x % width == 0) {
					x = 0;
					y++;
				}
			}
		}
		*/
		int i = minCodeLength;
		while(true) {
			System.out.println(i);
			code = huffmanCode.substring(startIndex, i);
			
			if(hmap3.containsKey(code)) {
				huffmanImage.setRGB(x++, y, hmap3.get(code));
				
				startIndex = i;
				i = startIndex + minCodeLength;
				
				if(x % width == 0) {
					x = 0;
					y++;
				}
				
				if(y != 0 && y % height == 0) {
					break;
				}
			}
			else {
				i++;
			}
		}
		
		return huffmanImage;
	}
	
	public void openImage(File file) {
		
		try{
			
			if(!imgFrame.isVisible()) {
				
				origImage = new ImageIcon(file.getPath());
				
				origImageL.setIcon(origImage);
				
				imgFrame.setTitle(file.getName());
				imgFrame.setVisible(true);
				
			} else {
				
				JOptionPane.showMessageDialog(frame, "Workspace is not available\nclose image first ", "Workspace", JOptionPane.ERROR_MESSAGE);
				
			}
			
		} catch(Exception ex) {
			
			ex.printStackTrace();
			
		}
		
	}
	
	public void openHimg(File himgFile) {
		
		try{
			
			if(!compFrame.isVisible()) {
				
				compImage = new ImageIcon(image);
				
				compImageL.setIcon(compImage);
				
				compFrame.setTitle(himgFile.getName());
				compFrame.setVisible(true);
				
			} else {
				
				JOptionPane.showMessageDialog(frame, "Workspace is not available\nclose image first ", "Workspace", JOptionPane.ERROR_MESSAGE);
				
			}
			
		} catch(Exception ex) {
			
			ex.printStackTrace();
			
		}
		
	}
	
	public Tree createHuffmanTree(PriorityQueue<Tree> queue) {
		
		hmap2 = new HashMap<Integer, String>();
		hmap3 = new HashMap<String, Integer>();
		Tree leftTree = queue.poll();
		Tree rightTree =  queue.poll();
		Tree newTree;
		
		while(rightTree != null) {
			
			newTree = new Tree('\0', leftTree.getRootFreq() + rightTree.getRootFreq(), false);
			newTree.setLeftChild(leftTree.getRoot());
			newTree.setRightChild(rightTree.getRoot());
			
			preOrder(leftTree.getRoot(), "0");
			preOrder(rightTree.getRoot(), "1");
			
			queue.offer(newTree);
			
			leftTree = queue.poll();
			rightTree = queue.poll();
			
			if(rightTree == null) {
				
				break;
				
			}
			
		}
		
		return leftTree;
		
	}
	
	public void preOrder(Node node, String s) {
		
		if(node != null) {
			
			if(node.isLeaf()) {
				
				node.setHuffmanCode(s + node.getHuffmanCode());
				
				hmap2.put(node.getRGBVal(), node.getHuffmanCode());
				
			}
			
			preOrder(node.getLeftChild(), s);
			
			preOrder(node.getRightChild(), s);
			
		}
		
	}
	
	public void generateBitString(BufferedImage img, File dir) {
		
		String bit = "";
		
		String filename = dir.getParent() + "/" + file.getName().substring(0, file.getName().length() - 3) + "himg";
		
		File name = new File(filename);
		
		try {
			DataOutputStream out = new DataOutputStream(new FileOutputStream(name));
			FileWriter wr = new FileWriter(new File(filename.substring(0, filename.length() - 5) + ".txt"));
			
			int w = img.getWidth();
			int h = img.getHeight();
			
			out.writeInt(w);
			out.writeInt(h);

			int[] dataBuffInt = img.getRGB(0, 0, w, h, null, 0, w); 
			
			for(int i = 0; i < dataBuffInt.length; i++) {
				
				if(hmap2.containsKey(dataBuffInt[i])) {
					
					bit = bit + hmap2.get(dataBuffInt[i]);
					
					while(bit.length() >= 31) {
						
						out.writeInt(Integer.parseInt(bit.substring(0, 31), 2));
						wr.write(bit.substring(0, 31));
						bit = bit.substring(31, bit.length());
						
					}
					
				} else {
					
					JOptionPane.showMessageDialog(frame, "Incompatible huff file and image!", "Error", JOptionPane.ERROR_MESSAGE);
					break;
					
				}
				
			}
			
			if(bit.length() > 0) {
				
				out.writeInt(Integer.parseInt(bit, 2));
				wr.write(bit);
				out.writeInt(bit.length());
				
			}
			
			out.close();
			wr.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private class Compress extends SwingWorker<Void, Void> {
		
		private File file;
		private BufferedImage orig;
		
		public Compress(File selected, BufferedImage image) {
			file = selected;
			orig = image;
		}
		
		@Override
		public Void doInBackground() throws IOException {
			
			int progress = 0;
			
			setProgress(progress);
			progressL.setText("Retrieving huffman tree...");
			compress(file);
			progress = 50;
			setProgress(progress);
			progressL.setText("Generating HIMG file...");
			generateBitString(orig, file);
			progress = 100;
			setProgress(progress);
			
			return null;
			
		}
		
		@Override
		public void done() {
			Toolkit.getDefaultToolkit().beep();
			done.setEnabled(true);
			progressL.setText("Done!");
		}
		
	}
	
	private class PropertyChangeHandler implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent e) {
			if("progress" == e.getPropertyName()) {
				int progressV = (Integer) e.getNewValue();
				progress.setValue(progressV);
			}
		}
	}
	
}
