import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

class Node {

	Node parent;
	Node children[];

	/**
	 * The test feature for internal node and it has value from 0-14 [0==>'Age',1==>'Occupation' etc.]
	 */
	int testFts;

	int numOfFts;

	List<Instance> instances;

	int predictedLabel = -1;

	Node(Node parent, List<Instance> instances) {
		this.parent = parent;
		children = new Node[50];	//Maximum number of features for any attribute

		this.instances = instances;
		numOfFts = Instance.ATTRIBUTES;
		testFts = -1;

		int count[] = { 0, 0 };
		for (Instance t : this.instances)
			count[t.label]++;
		predictedLabel = count[0] > count[1] ? 0 : 1;
	}
	/*Classifies the test examples after bulding the tree*/

	public int classify(Instance t) {
		
		if (testFts == -1) {
			return predictedLabel;
		} else {
			if (children[t.fts[testFts]] != null) {
				return children[t.fts[testFts]].classify(t);
			} else {
				return -1;// cannot decide; return parent label
			}
		}
	}
}

/*Encapsulates the data of each instance*/
class Instance {
	static int ATTRIBUTES = 14; 
	int label; 				
	Mapper map=new Mapper();  
	int fts[]=new int[ATTRIBUTES];		
	int uniqueId;						
	
	/* Class constructor takes in a example from file and processes it*/
	public Instance(String line, int id) {		
		int i=0;
		this.uniqueId = id;
		line=line.replaceAll("\\s","");
		
		StringTokenizer st=new StringTokenizer(line,",");
		
		while(st.hasMoreTokens())
		{
			if(i!=0 && i!=2 && i!=4 && i!=10 && i!=11 && i!=12)	
			{
				
				String token=st.nextToken();
				if(!(token.equals("<=50K")||token.equals(">50K")))
					fts[i]=(int)map.list.get(i).get(token);
				else
				{
					if(token.equals("<=50K"))
						label=0;
					else
						label=1;
				}
		
			}
			else
			{
				if(i==0)
				{
					String token=st.nextToken();
					if(Integer.parseInt(token)<=Split.split[0])
					{
						fts[i]=0;
					}
					else
						fts[i]=1;
				}
				if(i==2)
				{
					String token=st.nextToken();
					if(Integer.parseInt(token)<=Split.split[1])
					{
						fts[i]=0;
					}
					else
						fts[i]=1;
				}
				
				if(i==4)
				{
					String token=st.nextToken();
					if(Integer.parseInt(token)<=Split.split[2])
					{
						fts[i]=0;
					}
					else
						fts[i]=1;
				}
				
				if(i==10)
				{
					String token=st.nextToken();
					if(Integer.parseInt(token)<=Split.split[3])
					{
						fts[i]=0;
					}
					else
						fts[i]=1;
				}
				
				if(i==11)
				{
					String token=st.nextToken();
					if(Integer.parseInt(token)<=Split.split[4])
					{
						fts[i]=0;
					}
					else
						fts[i]=1;
				}
				
				if(i==12)
				{
					String token=st.nextToken();
					if(Integer.parseInt(token)<=Split.split[5])
					{
						fts[i]=0;
					}
					else
						fts[i]=1;
				}
					
			}
			i++;
		}
		
	}
}
class Split{
	static double split[]=new double[6];
	static int a[];
	static int b[];
	Split()
	{
		DataSet data=new DataSet();
		int i;
		int k=0;
		a=new int[32561];
		b=new int[32561];
		for(i=0;i<14;i++)
		{
			if(i==0 || i==2 || i==4 || i==10 || i==11 || i==12)
			{
				for(int j=0;j<data.arr.size();j++)
				{
					if(data.arr.get(j).size()!=0)
					{
					a[j]=Integer.parseInt(data.arr.get(j).get(i));
					if(data.arr.get(j).get(14).equals("<=50K"))
						b[j]=0;
					else
						b[j]=1;
					}
				}
					Gini gn = new Gini();
				split[k++]=gn.gini(a,b);
			}
		}
	}
}

public class ID3 {
	Node root;
	static int features[]={2,8,2,16,2,7,14,6,5,2,2,2,2,41};
	public abstract static class ImpurityFunction {
		public abstract double calc(int a, int b);
	}

		public static ImpurityFunction impurity_entropy = new ImpurityFunction() {
		public double calc(int a, int b) {
			double pa = a / ((double) a + (double) b);
			double pb = b / ((double) a + (double) b);

			double res = 0;
			if (a > 0)
				res += -pa * Math.log(pa);
			if (b > 0)
				res += -pb * Math.log(pb);

			return res / Math.log(2);
		}
	};

		public  Node generate(List<Instance> instances, ImpurityFunction f) {
		Node root = new Node(null, instances);
		expand(root, f,  0);
		return root;
	}
	
	void expand(Node node, ImpurityFunction impurityFunction, int depth) {
		double maxGain = -100000;
		int maxGainDecision = -1;
		int num = node.instances.size();
		int ftsNum = Instance.ATTRIBUTES;
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
		for (int s = 0; s < node.numOfFts; ++s) {
			int count[][] = new int[ID3.features[s]+1][2];
			for (Instance t : node.instances) {
				if (t.label == 1)
				{	
					if(t.fts[s]==0)
					{
						for(int j=1;j<=ID3.features[s];j++)
						{
							count[j][1]++;
						}
					}
					else
						count[t.fts[s]][1]++;
						
				}
					
				else
				{
					if(t.fts[s]==0)
					{
						for(int j=1;j<=ID3.features[s];j++)
						{
							count[j][0]++;
						}
					}
					else{
						count[t.fts[s]][0]++;
					}
				}
					
			}
			double gain = impurityFunction.calc(parentPos, parentNeg);
			for (int i = 1; i <= ID3.features[s]; i++) { 
				gain -= 1.0 * (count[i][0] + count[i][1])
						/ (parentPos + parentNeg)
						* impurityFunction.calc(count[i][0], count[i][1]);
			
			}
			if (gain > maxGain) {						/*Finding attribute with maximum gain*/
				maxGain = gain;
				maxGainDecision = s;
				for (int i = 0; i <= ID3.features[s]; i++) {
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
				else	{
					ts.get(t.fts[maxGainDecision]).add(t);}
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
	public void learn(List<Instance> instances) {
		this.root = generate(instances, ID3.impurity_entropy);
	}
	
	public List<Integer> classify(List<Instance> testInstances) {				
		List<Integer> predictions = new ArrayList<Integer>();
		for (Instance t : testInstances) {
			int predictedCategory = root.classify(t);
			predictions.add(predictedCategory);
		}
		return predictions;
	}
	
	public static void load(String trainfile, String testfile,
			List<Instance> trainInstances, List<Instance> testInstances) {
		int UNIQEID = 0;
		try {
			BufferedReader br = new BufferedReader(new FileReader(trainfile));
			String line;
			while ((line = br.readLine()) != null) {
				Instance ins = new Instance(line, UNIQEID++);
				trainInstances.add(ins);
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			BufferedReader br = new BufferedReader(new FileReader(testfile));
			String line;
			while ((line = br.readLine()) != null) {
				Instance ins = new Instance(line, UNIQEID++);
				testInstances.add(ins);
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static double computeAccuracy(List<Integer> predictions,
			List<Instance> testInstances) {
		if (predictions.size() != testInstances.size()) {
			return 0;
		} else {
			int right = 0, wrong = 0;
			for (int i = 0; i < predictions.size(); i++) {
				if (predictions.get(i) == null) {
					wrong++;
				} else if (predictions.get(i) == testInstances.get(i).label) {
					right++;
				} else {
					wrong++;
				}
			}
			return right * 1.0 / (right + wrong);
		}
	}

	/**Usage: 
	 * javac ID3
	 * java ID3*/
	public static void main(String[] args) {
		long startTime = System.nanoTime();
		Split sp=new Split();
		ArrayList<Instance> trainInstances = new ArrayList<Instance>();
		ArrayList<Instance> testInstances = new ArrayList<Instance>();
		load("src/adult.txt", "src/test.txt", trainInstances,
				testInstances);
		{	
			ID3 id3 = new ID3();
			id3.learn(trainInstances);
			long endTime = System.nanoTime();
			System.out.println("Learning time taken in seconds is\t"+ (endTime-startTime)/1000000000);
			List<Integer> trainpredictions = id3.classify(trainInstances);
			System.out.println("ID3 with full tree on training\t"
					+ id3.computeAccuracy(trainpredictions, trainInstances)*100+" %");
			List<Integer> predictions = id3.classify(testInstances);
		
			System.out.println("Accuracy using ID3 on testdata is\t"
					+ id3.computeAccuracy(predictions, testInstances)*100+" %");
		}
	}

}