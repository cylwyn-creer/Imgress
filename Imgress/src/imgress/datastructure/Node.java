package imgress.datastructure;

public class Node {
	
	private Node leftChild;
	private Node rightChild;
	private int freq;
	private int rgbVal;
	private String huffmanCode = "";
	private boolean leaf;
	
	public Node() {}

	public Node(int rgb, int frequency, boolean leaf) {
		
		rgbVal = rgb;
		freq = frequency;
		this.leaf = leaf;
		
	}
	
	public void setLeftChild(Node left) {
		
		leftChild = left;
		
	}
	
	public void setRightChild(Node right) {
		
		rightChild = right;
		
	}
	
	public void setIsLeaf(boolean leaf) {
		
		this.leaf = leaf;
		
	}
	
	public void setHuffmanCode(String code) {
		
		huffmanCode = code;
		
	}
	
	public void setFrequency(int frequency) {
		
		freq = frequency;
		
	}
	
	public void setChar(int rgb) {
		
		rgbVal = rgb;
		
	}
	
	public Node getLeftChild() {
		
		return leftChild;
		
	}
	
	public Node getRightChild() {
		
		return rightChild;
		
	}
	
	public int getFrequency() {
		
		return freq;
		
	}
	
	public boolean isLeaf() {
		
		return leaf;
		
	}
	
	public int getRGBVal() {
		
		return rgbVal;
		
	}
	
	public String getHuffmanCode() {
		
		return huffmanCode;
		
	}
}