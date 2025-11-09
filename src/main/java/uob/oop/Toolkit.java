package uob.oop;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class Toolkit {
    public static List<String> listVocabulary = null;
    public static List<double[]> listVectors = null;
    private static final String FILENAME_GLOVE = "glove.6B.50d_Reduced.csv";

    public static final String[] STOPWORDS = {"a", "able", "about", "across", "after", "all", "almost", "also", "am", "among", "an", "and", "any", "are", "as", "at", "be", "because", "been", "but", "by", "can", "cannot", "could", "dear", "did", "do", "does", "either", "else", "ever", "every", "for", "from", "get", "got", "had", "has", "have", "he", "her", "hers", "him", "his", "how", "however", "i", "if", "in", "into", "is", "it", "its", "just", "least", "let", "like", "likely", "may", "me", "might", "most", "must", "my", "neither", "no", "nor", "not", "of", "off", "often", "on", "only", "or", "other", "our", "own", "rather", "said", "say", "says", "she", "should", "since", "so", "some", "than", "that", "the", "their", "them", "then", "there", "these", "they", "this", "tis", "to", "too", "twas", "us", "wants", "was", "we", "were", "what", "when", "where", "which", "while", "who", "whom", "why", "will", "with", "would", "yet", "you", "your"};

    public void loadGlove() throws IOException {
        BufferedReader myReader = null;
        //TODO Task 4.1 - 5 marks
        listVocabulary = new ArrayList<>(); //creating 2 new array lists.
        listVectors = new ArrayList<>();

        try{
            File gloveFilePath = Toolkit.getFileFromResource(FILENAME_GLOVE);//retrieving the file path
            FileReader filereader = new FileReader(gloveFilePath); //creating a filereader and a buffered reader to read the file line by line
            myReader = new BufferedReader(filereader);
            String lines;

            while ((lines = myReader.readLine()) != null) { //inside the while loop each line is split by commas to separate the word and its vector components
                String[] Line = lines.split(","); //parsing each line

                if (Line.length > 0) { //if the lines have a length larger than 0
                    String word = Line[0]; //making the first element be word, adding it to the vocabulary list
                    listVocabulary.add(word);
                }

                double[] Vector = new double[Line.length - 1]; //represents the vector components, which are parsed to doubles and stored in an array
                for (int i = 1; i < Line.length; i++) {
                    Vector[i - 1] = Double.parseDouble(Line[i]);
                }
                listVectors.add(Vector);//the array is added to listvectors.
            }
        }
        catch (IOException e){ //if an exception occurs an error message is printed.
            System.out.println("Error" + e.getMessage());
        }
        catch (URISyntaxException e){ //if a syntaxexception occurs while obtaining the file resource a runtimeexception is thrown
            throw new RuntimeException(e);
        }
        finally { //ensures that the bufferedreader is closed.
            myReader.close();
        }
    }

    private static File getFileFromResource(String fileName) throws URISyntaxException {
        ClassLoader classLoader = Toolkit.class.getClassLoader();
        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException(fileName);
        } else {
            return new File(resource.toURI());
        }
    }


    public List<NewsArticles> loadNews() {
        List<NewsArticles> listNews = new ArrayList<>();
        //TODO Task 4.2 - 5 Marks
        String folderPath = "src/main/resources/News"; // specifying the folder path and file extension
        String extensionHtml = ".htm";
        File folder = new File(folderPath);
        if (folder.isDirectory()) { // checking if the path corresponds to a directory if it does then it lists all files in the directory
            File[] files = folder.listFiles();
            if (files != null) {  // checking if there are files in the directory then if there are files then it goes through each file in the directory
                for (File file : files) {
                    if (file.isFile() && file.getName().endsWith(extensionHtml)) { // checking if the current file is a regular file and has the specified HTML extension
                        try (FileReader fileReader = new FileReader(file);
                             BufferedReader myReader = new BufferedReader(fileReader)) {
                            StringBuilder contentBuilder = new StringBuilder();
                            String line;
                            while ((line = myReader.readLine()) != null) {
                                contentBuilder.append(line).append("\n");
                            }
                            // parsing HTML content and creating a NewsArticles object
                            String htmlContent = contentBuilder.toString();
                            String htmlTitle = HtmlParser.getNewsTitle(htmlContent);
                            String htmlContentText = HtmlParser.getNewsContent(htmlContent);
                            NewsArticles.DataType htmlDataType = HtmlParser.getDataType(htmlContent);
                            String htmlLabel = HtmlParser.getLabel(htmlContent);
                            NewsArticles newsArticles = new NewsArticles(htmlTitle, htmlContentText, htmlDataType, htmlLabel);
                            listNews.add(newsArticles); // adding the NewsArticles object to the list
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }
        return listNews;
    }
    public static List<String> getListVocabulary() {
        return listVocabulary;
    }
    public static List<double[]> getlistVectors() {
        return listVectors;
    }
}