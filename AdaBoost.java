import java.util.ArrayList; 
import java.util.List;  
public class AdaBoost extends RandomForest {
	static int NO_TREES=30;		//Number of classifers 
	static double alpha[]=new double[32561];	//An array for storing the weights for each instance.
	static double weightedError[]=new double[NO_TREES];	//An array to store weighted error for each classifier.
	static double wHat[]=new double[NO_TREES];			//An array to store wHAT for each classifier.
	static ArrayList<List<Integer>> classifierPredictions=new ArrayList<List<Integer>>();	//ArrayList of ArrayList to store classifier predicitons on train data.
	static ArrayList<List<Integer>> classifierPredictionsOnTestData=new ArrayList<List<Integer>>();//ArrayList of ArrayList to store classifier predicitons on test data
	static int classifiersize=0;
	AdaBoost()
	{  
		for(int i=0;i<32561;i++)
			alpha[i]=1.0/32561; 
	}
	
	public static void calcWeightedError(int d)
	{
		double classifierWeightedError=0;
		double total_alpha=0;
		for(int i=0;i<32561;i++)
		{
			if(Split.b[i]!=classifierPredictions.get(d).get(i))
			{
				classifierWeightedError+=alpha[i];
			}
			total_alpha+=alpha[i];
		}
		weightedError[d]=classifierWeightedError/total_alpha;
	}
	
	public static int calcWHat()
	{
		int minindex=0;
		for(int i=1;i<weightedError.length;i++)
		{
			if(weightedError[i]<weightedError[minindex])
				minindex=i;
		}
		
		wHat[minindex]=0.5*Math.log((1-weightedError[minindex])/weightedError[minindex]);
		//System.out.println("MIn What= "+wHat[minindex]);
		weightedError[minindex]=Double.MAX_VALUE;
		return minindex;
	}
	
	public static  void updateAlpha(int minindex)
	{	
		double sum=0;
		for(int i=0;i<32561;i++)
		{
			if(Split.b[i]!=classifierPredictions.get(minindex).get(i))
				alpha[i]=alpha[i]*(Math.exp(wHat[minindex]));
			else
				alpha[i]=alpha[i]*(Math.exp(-1*wHat[minindex]));
			sum+=alpha[i];
		}
		for(int i=0;i<32561;i++)
			alpha[i]/=sum;
		
	}
	
	public  static List<Integer> adaBoost()
	{
		List<Integer> predictions=new ArrayList<Integer>();
		
		for(int j=0;j<classifierPredictionsOnTestData.get(0).size();j++)
		{
			double signum=0;
			for(int i=0;i<NO_TREES;i++)
			{	
				int ft=classifierPredictionsOnTestData.get(i).get(j);
				if(ft==0)
				
					signum+=wHat[i]*-1;
				else
					signum+=wHat[i]*ft;
			}
			
			predictions.add((int)Math.signum(signum));
		}
		return predictions;
	}
	
	public static void main(String []args)
	{
		long startTime = System.nanoTime();
		AdaBoost ab;
		List<Instance> testInstances=new ArrayList<Instance>();
		List<Instance> completeTrainInstances=new ArrayList<Instance>();
		List<Integer> predictions = new ArrayList<Integer>();
		loadTest("src/test.txt", testInstances);
		loadTest("src/adult.txt", completeTrainInstances);
		Split sp=new Split();
		for(int i=0;i<NO_TREES;i++){
			
			ArrayList<Instance> trainInstances = new ArrayList<Instance>();
			load(trainInstances);
			ab=new AdaBoost();
			ab.learn(trainInstances);
			ab.classifierPredictions.add(ab.classify(completeTrainInstances));
			ab.classifierPredictionsOnTestData.add(ab.classify(testInstances));
			
		}
		
	for(int j=0;j<NO_TREES;j++)
	{
		for(int i=0;i<NO_TREES;i++)
		{
			calcWeightedError(i);
		}
		int minindex=calcWHat();
		updateAlpha(minindex);
	}
	long endTime = System.nanoTime();
	predictions=adaBoost();
	for(int i=0;i<predictions.size();i++)
	{
		if(predictions.get(i)==-1)
			predictions.set(i, 0);
	}
	System.out.println("Learning time taken in seconds is\t"+ (endTime-startTime)/1000000000);
	System.out.println("Accuracy of testdata using AdaBoost technique is = "+computeAccuracy(predictions, testInstances)*100+" %" );
	

	}
	
}
