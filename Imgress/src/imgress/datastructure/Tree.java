package imgress.datastructure;

public class Tree {

	private Node root;
	
	public Tree() {}
	
	public Tree(int rgb, int frequency, boolean leaf) {
		
		root = new Node(rgb, frequency, leaf);
		
	}
	
	public void setRoot(Node node) {
		
		root = node;
		
	}
	
	public void setRightChild(Node node) {
		
		root.setRightChild(node);
		
	}
	
	public void setLeftChild(Node node) {
		
		root.setLeftChild(node);
		
	}
	
	public Node getRoot() {
		
		return root;
		
	}
	
	public int getRootRGB() {
		
		return root.getRGBVal();
		
	}
	
	public int getRootFreq() {
		
		return root.getFrequency();
		
	}
	
}