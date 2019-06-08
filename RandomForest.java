import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
public class RandomForest extends ID3 {
	public static int [][]classification = new int[16281][2];	
	HashSet h=new HashSet(); 
	Random random=new Random((long)System.currentTimeMillis());
	ArrayList selected;
	RandomForest()
	{ 
	 
 		while(h.size()!=4)
		{
			int randomNumber=random.nextInt(14);
			h.add(randomNumber);
		}
		
	    selected=new ArrayList(h);
	 }
	void expand(Node node, ImpurityFunction impurityFunction, int depth)  {
		double maxGain = -100000;
		int maxGainDecision = -1;
		int num = node.instances.size();
		int ftsNum = 4;
		int mcount[][] = new int[50][2];
		int parentPos = 0, parentNeg = 0;
		for (int i = 0; i < node.instances.size(); i++) {
			if (node.instances.get(i).label == 1) {
				parentPos++;
			} else {
				parentNeg++;
			}
		}
		/* Iterate over all attributes, find the best attribute */
		for (int s = 0; s < selected.size(); ++s) {

			int count[][] = new int[ID3.features[(int)selected.get(s)]+1][2];
			for (Instance t : node.instances) {
				if (t.label == 1)
				{	
					if(t.fts[(int)selected.get(s)]==0)
					{
						for(int j=1;j<=ID3.features[(int)selected.get(s)];j++)
						{
							count[j][1]++;
						}
					}
					else
						count[t.fts[(int)selected.get(s)]][1]++;
						
				}
					
				else
				{
					if(t.fts[(int)selected.get(s)]==0)
					{
						for(int j=1;j<=ID3.features[(int)selected.get(s)];j++)
						{
							count[j][0]++;
						}
					}
					else{
						count[t.fts[(int)selected.get(s)]][0]++;
					}
				}
		}
			double gain = impurityFunction.calc(parentPos, parentNeg);
			for (int i = 1; i <= ID3.features[(int)selected.get(s)]; i++) { 
				gain -= 1.0 * (count[i][0] + count[i][1])
						/ (parentPos + parentNeg)
						* impurityFunction.calc(count[i][0], count[i][1]);
			}

			if (gain > maxGain) {
				maxGain = gain;
				maxGainDecision = (int)selected.get(s);
				for (int i = 0; i <= ID3.features[(int)selected.get(s)]; i++) {
					mcount[i][0] = count[i][0];
					mcount[i][1] = count[i][1];
				}
			}

		}

			if (maxGain > 1e-10) {
			node.testFts = maxGainDecision;

			ArrayList<ArrayList<Instance>> ts = new ArrayList<ArrayList<Instance>>();
			for (int i = 0; i <= ID3.features[maxGainDecision]; ++i) {
				ts.add(new ArrayList<Instance>());
			}

			for (Instance t : node.instances)
			{
				if(t.fts[maxGainDecision]==0)
				{
					for(int i=1;i <= ID3.features[maxGainDecision];i++)
						ts.get(i).add(t);
				}
				else	
					ts.get(t.fts[maxGainDecision]).add(t);
			}
				

			/* Grow the tree recursively */
			for (int i = 1; i <= ID3.features[maxGainDecision]; i++) {
				if (maxGainDecision == 16 && i == 2) {
					int x = 0;
				}
				if (ts.get(i).size() > 0) {
					node.children[i] = new Node(node, ts.get(i));
					expand(node.children[i], impurityFunction, depth + 1);
				}
			}
		}
	}
	
	public static void load(List<Instance> trainInstances) {
		int UNIQEID = 0;
		ArrayList<String> instances=DataSet.getInstance();
		
		Random random=new Random((long)System.currentTimeMillis());

		for(int i=0 ; i<32561  ;i++)	//Picking randonly N examples (32561) examples with replacement to construct each tree
		{
			int instance_number=random.nextInt(32561);
			String line=instances.get(instance_number);
			Instance ins = new Instance(line, UNIQEID++);
			trainInstances.add(ins);
		}
		
		}
		public static void loadTest(String testfile,List<Instance> testInstances)
	{
		int UNIQEID=0;
		try {
			BufferedReader br = new BufferedReader(new FileReader(testfile));
			String line;
			while ((line = br.readLine()) != null && !line.equals("")) {
				Instance ins = new Instance(line, UNIQEID++);
				testInstances.add(ins);
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	 */
	public void treeDecision(List<Instance> testInstances) {
			int i=0;
			for (Instance t : testInstances) {
			int predictedCategory = root.classify(t);
			if(predictedCategory!=-1)
			classification[i][predictedCategory]++;
			i++;
		}
		
	}
	/*
	Usage: 
	 * javac ID3
	 * javac RandomForest
	 * java RandomForest
	 */
public static void main(String []args)
{
	long startTime = System.nanoTime();
	RandomForest rf;
	ArrayList<Instance> testInstances = new ArrayList<Instance>();
	loadTest("src/test.txt", testInstances);
	Split sp=new Split();
	
	for(int i=0;i<100;i++){		//Random Forest with specified number of trees.
		
	rf=new RandomForest();
	ArrayList<Instance> trainInstances = new ArrayList<Instance>();
	load(trainInstances);
	rf.learn(trainInstances);
	rf.treeDecision(testInstances);
		
}
long endTime = System.nanoTime();
ArrayList<Integer> predictions=new ArrayList<Integer>();
for(int i=0;i<classification.length;i++)
	{
		if(classification[i][0]>classification[i][1])
			predictions.add(0);
		else
			predictions.add(1);
		
	}
	{
		System.out.println("Learning time taken in seconds is\t"+ (endTime-startTime)/1000000000);
		System.out.println("ID3 with full tree on test\t"
				+ computeAccuracy(predictions, testInstances)*100+" %");
	}
}
	
}
