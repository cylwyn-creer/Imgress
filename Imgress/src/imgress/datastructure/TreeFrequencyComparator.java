package imgress.datastructure;

import java.util.Comparator;

public class TreeFrequencyComparator implements Comparator<Tree>{
	
	@Override
	public int compare(Tree t1, Tree t2) {
		
		if(t1.getRootFreq() < t2.getRootFreq())
			return -1;
		if(t1.getRootFreq() > t2.getRootFreq())
			return 1;
		
		return 0;
		
	}
	
}
