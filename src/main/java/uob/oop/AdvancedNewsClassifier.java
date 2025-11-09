package uob.oop;

import org.apache.commons.lang3.time.StopWatch;
import org.deeplearning4j.datasets.iterator.utilty.ListDataSetIterator;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.WorkspaceMode;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AdvancedNewsClassifier {
    public Toolkit myTK = null;
    public static List<NewsArticles> listNews = null;
    public static List<Glove> listGlove = null;
    public List<ArticlesEmbedding> listEmbedding = null;
    public MultiLayerNetwork myNeuralNetwork = null;

    public final int BATCHSIZE = 10;

    public int embeddingSize = 0;
    private static StopWatch mySW = new StopWatch();

    public AdvancedNewsClassifier() throws IOException {
        myTK = new Toolkit();
        myTK.loadGlove();
        listNews = myTK.loadNews();
        listGlove = createGloveList();
        listEmbedding = loadData();
    }

    public static void main(String[] args) throws Exception {
        mySW.start();
        AdvancedNewsClassifier myANC = new AdvancedNewsClassifier();

        myANC.embeddingSize = myANC.calculateEmbeddingSize(myANC.listEmbedding);
        myANC.populateEmbedding();
        myANC.myNeuralNetwork = myANC.buildNeuralNetwork(2);
        myANC.predictResult(myANC.listEmbedding);
        myANC.printResults();
        mySW.stop();
        System.out.println("Total elapsed time: " + mySW.getTime());
    }

    public List<Glove> createGloveList() {
        List<Glove> listResult = new ArrayList<>();
        //TODO Task 6.1 - 5 Marks
        List<String> vocablist = Toolkit.listVocabulary; //initialising lists
        List<double[]> listVectors = Toolkit.listVectors;
        if (vocablist != null && listVectors != null) { //checks if they are null
            for (int i = 0; i < vocablist.size(); i++) { //if they are not null it iterates through vocablist
                String words = vocablist.get(i); //for each word at index i
                if (!stopwords(words)) { //checks if the word is a stopword
                    Vector vec = new Vector(listVectors.get(i)); //if its not a stop word, then it creates a vector object and a glove object
                    Glove glove = new Glove(words, vec); //uses words and vector to make the glove obj
                    listResult.add(glove); //it adds the glove object to the listresult.
                }
            }
        }
        return listResult;
    }
    private boolean stopwords(String word) { //stopwords method that goes through an array of stop words (Toolkit.STOPWORDS) and returns true if the word is a stop word.
        for (int i = 0; i < Toolkit.STOPWORDS.length; i++) {
            if (Toolkit.STOPWORDS[i].equals(word)) {
                return true;
            }
        }
        return false;
    }


    public static List<ArticlesEmbedding> loadData() {
        List<ArticlesEmbedding> listEmbedding = new ArrayList<>();
        for (NewsArticles news : listNews) {
            ArticlesEmbedding myAE = new ArticlesEmbedding(news.getNewsTitle(), news.getNewsContent(), news.getNewsType(), news.getNewsLabel());
            listEmbedding.add(myAE);
        }
        return listEmbedding;
    }

    public int calculateEmbeddingSize(List<ArticlesEmbedding> _listEmbedding) {
        int intMedian = -1;
        //TODO Task 6.2 - 5 Marks
        List<Integer> doclength = calculateDocumentLengths(_listEmbedding);//this line calls the calculateDocumentLengths method, which iterates over the _listEmbedding and calculates the number of words in each article using the calculateWordsWithGlove method. The lengths are stored in the doclength list
        doclength = heapsort(doclength); //the heapsort method is called to sort the doclength list in ascending order
        //calculating the median
        int size = doclength.size();
        if (size % 2 == 0) {// if the length of the list is even it calculates the median by averaging the two middle elements
            int mid1 = doclength.get(size / 2);
            int mid2 = doclength.get((size / 2) + 1);
            int n = mid1 + mid2;
            intMedian = n/2;
            return intMedian;
        } else {// if the length of the list is odd it directly takes the middle element as the median
            intMedian = doclength.get((size + 1) / 2);
            return intMedian;
        }
    }

    private List<Integer> calculateDocumentLengths(List<ArticlesEmbedding> _listEmbedding) { //calculates the number of words in each article using the calculateWordsWithGlove method and returns a list of document lengths
        List<Integer> lengthsofdocs = new ArrayList<>();
        for (int i = 0; i < _listEmbedding.size(); i++) {
            ArticlesEmbedding embedding = _listEmbedding.get(i);
            int length = calculateWordsWithGlove(embedding);
            lengthsofdocs.add(length);
        }
        return lengthsofdocs;
    }

    private int calculateWordsWithGlove(ArticlesEmbedding embedding) { //splits the text of an article into words and counts the number of words that are present in the toolkit
        String articleText = embedding.getNewsContent();
        String[] words = articleText.split(" ");
        int n = 0;
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            if (Toolkit.listVocabulary.contains(word.toLowerCase())) {
                n++;
            }
        }
        return n;
    }

    private List<Integer> heapsort(List<Integer> list) { //builds a max heap and then repeatedly extracts the maximum element to achieve a sorted list
        int n = list.size();// Build max heap
        for (int i = n / 2 - 1; i >= 0; i--) {
            heapify(list, n, i);
        }
        for (int i = n - 1; i > 0; i--) {
            int temp = list.get(0);
            list.set(0, list.get(i));
            list.set(i, temp);
            heapify(list, i, 0);
        }
        return list;
    }
    private void heapify(List<Integer> list, int n, int i) { //maintains the heap property in the process of building the heap or extracting elements
        int largest = i;
        int left = 2 * i + 1;
        int right = 2 * i + 2;
        if (left < n && list.get(left) > list.get(largest)) {
            largest = left;
        }
        if (right < n && list.get(right) > list.get(largest)) {
            largest = right;
        }
        if (largest != i) {
            int swap = list.get(i);
            list.set(i, list.get(largest));
            list.set(largest, swap);
            heapify(list, n, largest);
        }
    }
    public void populateEmbedding() {
        //TODO Task 6.3 - 10 Marks
        for (int i = 0; i < listEmbedding.size(); i++) { //goes through each element of listEmbedding
            ArticlesEmbedding embedded = listEmbedding.get(i); // retrieves the ArticlesEmbedding object at index i and assigns it to the variable embedded
            try { //contains a call to the getEmbedding method on the ArticlesEmbedding object to execute this method, and if it encounters any issues, it will be caught by the catch blocks
                embedded.getEmbedding();
            } catch (InvalidSizeException e) { // if the getEmbedding method throws an invalidsizeexception this block is executed
                embedded.setEmbeddingSize(embeddingSize); //it sets the embedding size using the setEmbeddingSize method
            } catch (InvalidTextException e) { // if an exception gets thrown then
                embedded.getNewsContent(); //the getNewsContent method on the ArticlesEmbedding object is called.
            } catch (Exception e) { //any other exception occurs during the execution of the getEmbedding method, this block is executed.
                e.printStackTrace(); //prints the stack trace
            }
        }
    }
    public DataSetIterator populateRecordReaders(int _numberOfClasses) throws Exception {
        ListDataSetIterator myDataIterator = null;
        List<DataSet> listDS = new ArrayList<>();
        INDArray inputNDArray = null;
        INDArray outputNDArray = null;
        //TODO Task 6.4 - 8 Marks
        for (ArticlesEmbedding articlesembedding : listEmbedding) { //iterating through each ArticlesEmbedding object
            if (NewsArticles.DataType.Training.equals(articlesembedding.getNewsType())) { //checks if the NewsType of the current ArticlesEmbedding object is equal to NewsArticles.DataType.Training //used to filter out only those articles that have the data type set to training
                inputNDArray = articlesembedding.getEmbedding(); //if the data type is training it retrieves the embedding data from the ArticlesEmbedding object and assigns it to inputNDArray
                int[][] array = new int[1][_numberOfClasses]; //generates a label array based on the news label of the ArticlesEmbedding object the label is a one-hot encoded array where a 1 is set at the index corresponding to the label value, and 0 is set at other indices
                String label = articlesembedding.getNewsLabel();
                for (int i = 0; i < _numberOfClasses; i++) {
                    array[0][i] = label.equalsIgnoreCase(Integer.toString(i + 1)) ? 1 : 0;
                }
                outputNDArray = Nd4j.createFromArray(array); //creates an ND4J array
                DataSet myDataSet = new DataSet(inputNDArray, outputNDArray); //creates a DataSet object with the input and output arrays and adds it to a list
                listDS.add(myDataSet); //this list will store all the DataSet objects created in the loop
            }
        }
        return new ListDataSetIterator(listDS, BATCHSIZE); //returns a listdatasetiterator that is initialized with the list of DataSet objects and the specified batch size
    }
    public MultiLayerNetwork buildNeuralNetwork(int _numOfClasses) throws Exception {
        DataSetIterator trainIter = populateRecordReaders(_numOfClasses);
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(42)
                .trainingWorkspaceMode(WorkspaceMode.ENABLED)
                .activation(Activation.RELU)
                .weightInit(WeightInit.XAVIER)
                .updater(Adam.builder().learningRate(0.02).beta1(0.9).beta2(0.999).build())
                .l2(1e-4)
                .list()
                .layer(new DenseLayer.Builder().nIn(embeddingSize).nOut(15)
                        .build())
                .layer(new OutputLayer.Builder(LossFunctions.LossFunction.HINGE)
                        .activation(Activation.SOFTMAX)
                        .nIn(15).nOut(_numOfClasses).build())
                .build();
        MultiLayerNetwork model = new MultiLayerNetwork(conf);
        model.init();
        for (int n = 0; n < 100; n++) {
            model.fit(trainIter);
            trainIter.reset();
        }
        return model;
    }
    public List<Integer> predictResult(List<ArticlesEmbedding> _listEmbedding) throws Exception {
        List<Integer> listResult = new ArrayList<>();
        //TODO Task 6.5 - 8 Marks
        for (int i = 0; i < _listEmbedding.size(); i++) { // iterates through each embedding in the list
            ArticlesEmbedding embeded = _listEmbedding.get(i); // retrieves the current embedding and puts it into index i
            if (embeded.getNewsType() == NewsArticles.DataType.Testing) { // check if the news type is for testing
                INDArray embedding = embeded.getEmbedding(); // if it is for testing it gets the document embedding from the current embedding
                int label = myNeuralNetwork.predict(embedding)[0];// predicts the label using the neural network function
                listResult.add(label);// adds the predicted label to the result list
                embeded.setNewsLabel(Integer.toString(label + 1)); // sets the news label in the embedding using the predicted label + 1
            }
        }
        return listResult; // returns the list of predicted labels
    }
    public void printResults() {
        //TODO Task 6.6 - 6.5 Marks
        int labelmax = calculateMaxLabel(listEmbedding); // calculates the maximum label using a method
        List<String>[] result = new List[labelmax]; // creating an array of lists that  stores the titles
        for (int i = 0; i < result.length; i++) { // initialises each list in the array
            result[i] = new ArrayList<>();
        }
        for (int i = 0; i < listEmbedding.size(); i++) { // goes through each embedding in the list
            ArticlesEmbedding embedding = listEmbedding.get(i); //gets the current embedding
            if (NewsArticles.DataType.Testing.equals(embedding.getNewsType())) { // checks if the news type is for testing or for training
                int l = Integer.parseInt(embedding.getNewsLabel()); // if it is testing then it extracts the label from the embedding and convert it to an integer
                if (l > 0 && l <= labelmax) { // checking if the label is within the valid range
                    result[l - 1].add(embedding.getNewsTitle());// if it is whithin valid range then it gets the news title to the corresponding group
                } else { //otherwise it prints an error message
                    System.err.println("Invalid label: " + embedding.getNewsLabel());
                }
            }
        }
        for (int i = 0; i < result.length; i++) { // goes through each group and print titles
            if (!result[i].isEmpty()) {
                System.out.println("Group " + (i + 1));
                int titleListSize = result[i].size();
                for (int j = 0; j < titleListSize; j++) {
                    String title = result[i].get(j); // gets the title for the current index
                    System.out.println(title);
                }
            }
        }
    }

    private int calculateMaxLabel(List<ArticlesEmbedding> listEmbedding) {
        int maxlabel = Integer.MIN_VALUE; // initializes the maximum label to the minimum value
        int listsize = listEmbedding.size(); // initializes the size of the list to listsize
        for (int i = 0; i < listsize; i++) { // goes through each embedding in the list
            ArticlesEmbedding embedding = listEmbedding.get(i);// retrieves the current embedding
            if (embedding.getNewsType().equals(NewsArticles.DataType.Testing)) { // checks  if the news type is for testing
                int label = Integer.parseInt(embedding.getNewsLabel());  // if the news type is for testing then it extracts the label from the embedding and convert it to an integer
                maxlabel = Math.max(maxlabel, label); // updates the maximum label if the current label is greater than it
            }
        }
        return maxlabel; //return statemt
    }
}
