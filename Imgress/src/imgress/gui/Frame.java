package imgress.gui;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyVetoException;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
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
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
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
	private JScrollPane imgPane, compPane;
	private ImageIcon origImage, compImage;
	
	private File file;
	private MenuEventHandler handler = new MenuEventHandler(); 
	private HashMap<Integer, Integer> hmap;
	private HashMap<Integer, String> hmap2;
	private HashMap<String, Integer> hmap3;
	
	@SuppressWarnings("unused")
	private Tree huffmanTree;
	private BufferedImage image;
	
	private int width = 0;
	private int height = 0;
	
	private int minCodeLength = 0;
	private JLabel shortcut, instructions, aboutText;
	
	private JTextArea status;
	private JScrollPane statusPane;
	
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
		imgFrame.hide();
		imgFrame.setDefaultCloseOperation(HIDE_ON_CLOSE);
		
		origImageL = new JLabel();
		
		origImagePanel = new JPanel();
		origImagePanel.add(origImageL);
		
		imgPane = new JScrollPane(origImagePanel);
		
		imgFrame.add(imgPane);
		
		
		compFrame = new JInternalFrame("Compressed Image", true, true, true, true);
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
		
		createHelpText();
		createAboutText();
		
		desktopPane.add(imgFrame);
		desktopPane.add(compFrame);
		desktopPane.add(helpFrame);
		desktopPane.add(aboutFrame);
		
		desktopPane.setBounds(5, 5, 985, 475);
		
		add(desktopPane);
		
		setIconImage(new ImageIcon(this.getClass().getResource("/res/image/icon.png")).getImage());
		setSize(1000, 650);
		setLayout(null);
		setResizable(false);
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
		
		displayDetails();
		
	}
	
	public void displayDetails() {
		status = new JTextArea();
		status.setEditable(false);
		status.setBorder(BorderFactory.createTitledBorder("Status"));
		status.setWrapStyleWord(true);
		
		statusPane = new JScrollPane(status);
		statusPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		statusPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		statusPane.setBounds(5, 480, 985, 115);
		add(statusPane);
	}
	
	public void createHelpText() {
		
		helpFrame.setLayout(null);
		
		shortcut = new JLabel("<html><h3><center>Shortcut Keys</h3></center>"
				+ "Ctrl + O (Open)<br> Ctrl + X (Close) <br>  Ctrl + C " +
				"(Compress) <br> Ctrl + R (Render) <br> Ctrl + A (About) <br>"
				+ "F1 (Help) <br> Alt + F (File) <br> Alt + T (Tools) <br>"
				+ "Alt + H (Help Menu)</html>", SwingConstants.CENTER);
		shortcut.setFont(new Font("Courier New", Font.BOLD, 12));
		shortcut.setBounds(50, -50, 200, 300);
		
		instructions = new JLabel("<html><b>How to Compress Image</b><br>"
				+ "1. Opening an Image <br> &nbsp&nbsp File -> Open -> Select .png image <br>"
				+ "2. Train an Image <br> &nbsp&nbsp Tools -> Train -> Create New Huffman Tree -> Select Directory or <br>"
				+ "&nbsp&nbsp Tools -> Train -> Update Huffman Tree -> Select Directory <br>"
				+ "3. Compress Image <br> &nbsp&nbsp Tools -> Compress -> Select .huff file <br>"
				+ "4. Render Huffman Image <br> &nbsp&nbsp Tools -> Render Huffman Image -> Select .huff file -> "
				+ "Select .himg file </html>");
		
		instructions.setFont(new Font("Courier New", Font.BOLD, 12));
		instructions.setBounds(300, -40, 300, 300);
		
		helpFrame.add(shortcut);
		helpFrame.add(instructions);
		
	}
	
	public void createAboutText() {
		
		aboutFrame.setLayout(new FlowLayout());
		
		JLabel iconL = new JLabel(new ImageIcon(this.getClass().getResource("/res/image/icon2.png")));
		iconL.setBounds(0, 0, 100, 100);
		
		aboutText = new JLabel("<html>Program: Imgress <br> Version: 1.0 <br> Developers:"
				+ " Creer, Cylwyn Ronald M. <br> Dublin, Rodney O. <br> Description: "
				+ "This program is used <br> for image compression (i.e. PNG image) <br> using Huffman coding. "
				+ "<br><br> &nbsp&nbsp&nbsp _____________________________ <br> &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp "
				+ "Copyright &copy 2017 <br> &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp All rights reserved.</html>", SwingConstants.CENTER);
		aboutText.setFont(new Font("Courier New", Font.BOLD, 12));
		aboutText.setBounds(70, 10, 200, 200);
		
		aboutFrame.add(iconL);
		aboutFrame.add(aboutText);
		
	}
	
	private class MenuEventHandler implements ActionListener {
		
		public void actionPerformed(ActionEvent event) {
			
			JFileChooser chooser = new JFileChooser();
			chooser.setAcceptAllFileFilterUsed(false);
			int option, button, option2;
			
			status.setText("");
			if(event.getActionCommand() == exitItem.getActionCommand()) {
				
				button = JOptionPane.showConfirmDialog(null,"Are you sure?", "Warning", JOptionPane.YES_NO_OPTION);
				
				if(button == JOptionPane.YES_OPTION)
					System.exit(0);
				
			}
			
			if(event.getActionCommand() == openItem.getActionCommand()) {
				
				getFileOrDirectory();
				
			}
			
			if(event.getActionCommand() == helpItem.getActionCommand()) {
				
				if(!helpFrame.isShowing()) {
					helpFrame.show();
					helpFrame.setSize(650, 300);
					helpFrame.setLocation((desktopPane.getWidth() - 650) / 2, (desktopPane.getHeight() - 300) / 2);
					try {
						helpFrame.setSelected(true);
					} catch (PropertyVetoException e) {
						e.printStackTrace();
					}
				}
				
			}
			
			if(event.getActionCommand() == aboutItem.getActionCommand()) {
				
				if(!aboutFrame.isShowing()) {
					aboutFrame.show();
					aboutFrame.setSize(300, 300);
					aboutFrame.setLocation((desktopPane.getWidth() - 300) / 2, (desktopPane.getHeight() - 300) / 2);
					try {
						aboutFrame.setSelected(true);
					} catch (PropertyVetoException e) {
						e.printStackTrace();
					}
				}
				
			}
			
			if(event.getActionCommand() == newItem.getActionCommand()) {
				
				if(desktopPane.getSelectedFrame() == imgFrame) {
					
					chooser.setDialogTitle("Save in");
					chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					
					option = chooser.showSaveDialog(Frame.this);
					
					if(option == JFileChooser.APPROVE_OPTION) {
						
						status.append("Creating new huffman tree...\n");
						createNewHuffman(chooser.getSelectedFile());
						status.append("New huffman tree created as : " + file.getName().substring(0, file.getName().length() - 4) + ".huff");

					} 
					
				} else {
					
					JOptionPane.showMessageDialog(frame, "No image selected!", "Imgress", JOptionPane.ERROR_MESSAGE);
					
					
				}
				
			}
			
			if(event.getActionCommand() == updateItem.getActionCommand()) {
				
				if(desktopPane.getSelectedFrame() == imgFrame) {
					
					chooser.setDialogTitle("Save in");
					chooser.setFileFilter(new FileNameExtensionFilter("HUFF File", "huff"));
					
					option = chooser.showOpenDialog(Frame.this);
					
					if(option == JFileChooser.APPROVE_OPTION) {
						
						if(chooser.getSelectedFile().getName().endsWith(".huff"))
							try {
								
								status.append("Updating existing huffman tree...\n");
								pqueue = new PriorityQueue<Tree>(10, comparator);
								updateFile(chooser.getSelectedFile());
								status.append("Huffman tree updated : " + file.getName().substring(0, file.getName().length() - 4) + ".huff");
								
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
						
						decompress(huffFile, himgFile);
						
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
								compress(chooser.getSelectedFile());
								generateBitString(image, chooser.getSelectedFile());
								
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
				
				if(desktopPane.getSelectedFrame() != null) 
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
			
			status.append("Getting image's pixel information...\n");
			int[] dataBuffInt = image.getRGB(0, 0, w, h, null, 0, w);
			status.append("Image's pixel information gathered.\n");
			
			int freq;
			
			status.append("Checking each pixel's frequency...\n");
			for(int i = 0; i < dataBuffInt.length; i++) {
				
				if(hmap.containsKey(dataBuffInt[i])) {
					
					freq = hmap.get(dataBuffInt[i]);
					
					hmap.remove(dataBuffInt[i]);
					hmap.put(dataBuffInt[i], freq+1);
					
				} else {
					
					hmap.put(dataBuffInt[i], 1);
					
				}
				
			}
			status.append("Pixel distribution recorded.\n");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			
			status.append("Saving huffman tree as .huff file...\n");
			fileWriter = new FileWriter(name);
			buff = new BufferedWriter(fileWriter);

			Set<Entry<Integer, Integer>> set = hmap.entrySet();
			Iterator<Entry<Integer, Integer>> iterator = set.iterator();
			while(iterator.hasNext()) {
				
				@SuppressWarnings("rawtypes")
				Map.Entry entry = (Map.Entry)iterator.next();
				buff.write(entry.getKey() + " " + entry.getValue());
				buff.newLine();
				
			}
			
			buff.close();
			
		} catch (IOException e) {
			
			e.printStackTrace();
			
		}
		
	}
	
	public void updateFile(File huffFile) throws IOException {
		
		Scanner read;
		FileWriter fileWriter;
		BufferedWriter buff;
		hmap = new HashMap<Integer, Integer>();
		
		try {
			
			read = new Scanner(huffFile);
			
			
			status.append("Retrieving huffman tree data...\n");
			while(read.hasNext()) {
				
				hmap.put(read.nextInt(), read.nextInt());
				
			}
			status.append("Huffman tree data retrieved successfully.\n");
			
			read.close();
			
			image = ImageIO.read(file);
			
			int w = image.getWidth();
			int h = image.getHeight();

			int[] dataBuffInt = image.getRGB(0, 0, w, h, null, 0, w); 
			
			int freq;

			status.append("Updating system...\n");
			for(int i = 0; i < dataBuffInt.length; i++) {
				
				if(hmap.containsKey(dataBuffInt[i])) {
					
					freq = hmap.get(dataBuffInt[i]);
					
					hmap.remove(dataBuffInt[i]);
					hmap.put(dataBuffInt[i], freq+1);
					
				} else {
					
					hmap.put(dataBuffInt[i], 1);
					
				}
				
			}
			
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
			
		}
		
		try {
			
			fileWriter = new FileWriter(huffFile);
			buff = new BufferedWriter(fileWriter);

			Set<Entry<Integer, Integer>> set = hmap.entrySet();
			Iterator<Entry<Integer, Integer>> iterator = set.iterator();
			while(iterator.hasNext()) {
				
				@SuppressWarnings("rawtypes")
				Map.Entry entry = (Map.Entry)iterator.next();
				buff.write(entry.getKey() + " " + entry.getValue());
				buff.newLine();
				
			}
			
			buff.close();
			
		} catch (IOException e) {
			
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
		
	}
	
	public void decompress(File huffFile, File himgFile) {
		
		loadHuffFile(huffFile);
		
		huffmanTree = createHuffmanTree(pqueue);
		
		Set<Entry<Integer, String>> set = hmap2.entrySet();
		Iterator<Entry<Integer, String>> iterator = set.iterator();
		boolean tracked = false;
		
		while(iterator.hasNext()) {
			@SuppressWarnings("rawtypes")
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
			
			byte[] byteData = new byte[(int) himgFile.length()];
			
			in.readFully(byteData);
			
			byte[] arrW = new byte[4];
			byte[] arrH = new byte[4];
			byte[] bitL = new byte[4];
			
			System.arraycopy(byteData, 0, arrW, 0, 4);
			System.arraycopy(byteData, 4, arrH, 0, 4);
			System.arraycopy(byteData, byteData.length - 4, bitL, 0, 4);
			
			ByteBuffer buf = ByteBuffer.wrap(arrW);
			width = buf.getInt();
			buf = ByteBuffer.wrap(arrH);
			height = buf.getInt();
			buf = ByteBuffer.wrap(bitL);
			int bitStringLength = buf.getInt();
			
			byte[] real = new byte[byteData.length - 12];
			System.arraycopy(byteData, 8, real, 0, byteData.length - 12);
			
			BigInteger bigInt = new BigInteger(real);
			bitstring = bigInt.toString(2);
			
			int pad = 0;
			if(bitstring.length() % (real.length * 8) != 0) {
				pad = (real.length * 8) - (bitstring.length() % (real.length * 8));
			}
		
			for(int a = 0; a < pad; a++) {
				bitstring = '0' + bitstring;
			}
			
			String temp = bitstring.substring(bitstring.length() - (32 + bitStringLength), bitstring.length() - 32);
			
			bitstring = bitstring.substring(0, bitstring.length() - 32);
			bitstring = bitstring + temp;
			
			in.close();
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
		
		String code = "";
		
		for(int a = 0; a < huffmanCode.length(); a++) {
			if(a % 32 != 0) {
				code = code + huffmanCode.charAt(a);
			}
			
			if(hmap3.containsKey(code)) {
				huffmanImage.setRGB(x++, y, hmap3.get(code));
				
				code = "";
				
				if(x % width == 0) {
					x = 0;
					y++;
				}
				
				if(y != 0 && y % height == 0) {
					break;
				}
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
				
				if(origImage.getIconWidth() < desktopPane.getWidth() && origImage.getIconHeight() < desktopPane.getHeight()) {
					
					imgFrame.setSize(origImage.getIconWidth() + 60, origImage.getIconHeight() + 60);
					imgFrame.setLocation((desktopPane.getWidth() - imgFrame.getWidth()) / 2, (desktopPane.getHeight() - imgFrame.getHeight()) / 2);
				
				} else {
					
					imgFrame.setLocation(0, 0);
					imgFrame.setSize(desktopPane.getWidth(), desktopPane.getHeight());
					
				}
				
				
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
				
				if(compImage.getIconWidth() < desktopPane.getWidth() && compImage.getIconHeight() < desktopPane.getHeight()) {
					
					compFrame.setSize(compImage.getIconWidth() + 60, compImage.getIconHeight() + 60);
					compFrame.setLocation((desktopPane.getWidth() - compFrame.getWidth()) / 2, (desktopPane.getHeight() - compFrame.getHeight()) / 2);
				
				} else {
					
					compFrame.setLocation(0, 0);
					compFrame.setSize(desktopPane.getWidth(), desktopPane.getHeight());
					
				}
				
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
						bit = bit.substring(31, bit.length());
						
					}
					
				} else {
					
					JOptionPane.showMessageDialog(frame, "Incompatible huff file and image!", "Error", JOptionPane.ERROR_MESSAGE);
					break;
					
				}
				
			}
			
			if(bit.length() > 0) {
				
				out.writeInt(Integer.parseInt(bit, 2));
				out.writeInt(bit.length());
				
			}
			
			out.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
}
