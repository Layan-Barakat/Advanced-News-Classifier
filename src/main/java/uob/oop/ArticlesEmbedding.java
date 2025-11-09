package uob.oop;

import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.pipeline.*;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import java.util.Properties;


public class ArticlesEmbedding extends NewsArticles {
    private int intSize = -1;
    private String processedText = "";

    private INDArray newsEmbedding = Nd4j.create(0);

    public ArticlesEmbedding(String _title, String _content, NewsArticles.DataType _type, String _label) {
        //TODO Task 5.1 - 1 Mark
        super(_title,_content,_type,_label);
    }

    public void setEmbeddingSize(int _size) {
        //TODO Task 5.2 - 0.5 Marks
        this.intSize = _size;
    }

    public int getEmbeddingSize(){
        return intSize;
    }

    @Override
    public String getNewsContent(){
        //TODO Task 5.3 - 10 Marks
        if (processedText.isEmpty()) {
            processedText = super.getNewsContent();
            processedText = Stopwords_Removal(Lemmatization(textCleaning(processedText.toLowerCase())));
            return processedText.trim();
        } else {
            return processedText.trim();
        }
    }
    private static String Lemmatization(String _content){
        Properties properties = new Properties(); // set up pipeline properties
        properties.setProperty("annotators", "tokenize, ssplit, pos, lemma"); // set the list of annotators to run, these annotators will be applied to the text during processing
        StanfordCoreNLP pipeline = new StanfordCoreNLP(properties);// build pipeline
        CoreDocument doc = pipeline.processToCoreDocument(_content);// create a document object
        StringBuilder lemmatizedText = new StringBuilder(); // display tokens
        for (CoreLabel lol : doc.tokens()) { //go through the tokens in the document
            lemmatizedText.append(lol.lemma()).append(" "); //adds a space between each lemmatization
        }
        return lemmatizedText.toString().trim(); //returns the lemmatized text as a string and trims it to remove unwanted spaces around the words
    }
    private static String Stopwords_Removal(String _content){
        StringBuilder sb = new StringBuilder();
        String[] wordsList = _content.split(" ");
        for (int i = 0; i < wordsList.length; i++) {
            String word = wordsList[i];
            if (!isStopWord(word)) {
                sb.append(word).append(" ");
            }
        }
        return sb.toString().trim();
    }
    private static boolean isStopWord(String word) {
        for (int i = 0; i < Toolkit.STOPWORDS.length; i++) {
            String stopword = Toolkit.STOPWORDS[i];
            if (stopword.equals(word)) {
                return true;
            }
        }
        return false;
    }
    public INDArray getEmbedding() throws Exception {
        //TODO Task 5.4 - 20 Marks
        if (newsEmbedding.isEmpty()) { // checking if the newsEmbedding is empty
            if (intSize == -1) {
                throw new InvalidSizeException("Invalid size");
            }
            if (processedText.isEmpty()) {
                // System.out.println("**Getembedding Process Terminated**");
                throw new InvalidTextException("Invalid text");
            }
            // List<String> words = new ArrayList<>();
            String[] string = processedText.split(" ");
            newsEmbedding = Nd4j.create(intSize, AdvancedNewsClassifier.listGlove.get(0).getVector().getVectorSize());
            int processed = 0;
            for (int i = 0; i < string.length; i++) {
                String word = string[i];
                Glove glove = linearSearch(word.toLowerCase()); // finding the Glove embedding for the current word using linear search and changing it to lower case
                if (glove != null) {
                    newsEmbedding.putRow(processed, Nd4j.create(glove.getVector().getAllElements())); // adding the Glove vector to the newsEmbedding matrix
                    processed++;
                    if (processed >= intSize) {
                        break;
                    }
                }
            }
            return Nd4j.vstack(newsEmbedding.mean(1));  // returns the mean of the rows of the newsEmbedding matrix
        } else {
            return Nd4j.vstack(newsEmbedding.mean(1));// returnsthe mean of the rows of the non-empty newsEmbedding matrix
        }
    }
    private Glove linearSearch(String t) {
        for (int i = 0; i < AdvancedNewsClassifier.listGlove.size(); i++) { // goes through each Glove in the list using a normal for loop
            Glove g = AdvancedNewsClassifier.listGlove.get(i);
            if (g.getVocabulary().equalsIgnoreCase(t)) { // checking if the vocabulary of the current Glove is equal to the target string
                return g;
            }
        }
        return null; // Return null if the word is not found in the list
    }
    /***
     * Clean the given (_content) text by removing all the characters that are not 'a'-'z', '0'-'9' and white space.
     * @param _content Text that need to be cleaned.
     * @return The cleaned text.
     */
    private static String textCleaning(String _content) {
        StringBuilder sbContent = new StringBuilder();

        for (char c : _content.toLowerCase().toCharArray()) {
            if ((c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || Character.isWhitespace(c)) {
                sbContent.append(c);
            }
        }

        return sbContent.toString().trim();
    }
}
