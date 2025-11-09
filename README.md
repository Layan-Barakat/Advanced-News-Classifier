# Advanced News Classifier 

A **Java + Maven** project that transforms raw news articles into **GloVe-based embeddings**, trains a lightweight **DeepLearning4J (DL4J)** neural network, and predicts the category of each article.  

This project includes a small built-in dataset of HTML news articles and a reduced GloVe (50d) word vector file for efficient local testing.

---

## Features
- Cleans and tokenizes HTML articles (Stanford CoreNLP)
- Converts text into **GloVe embeddings (50d)**  
- Aggregates word vectors into document embeddings
- Trains a **neural network (MultiLayerNetwork)** using DL4J  
- Includes **JUnit tests** for each component  
- Fully offline and self-contained (no external downloads required)

---

## Project Structure
```text
pom.xml
src/
 ├─ main/java/uob/oop/
 │   ├─ AdvancedNewsClassifier.java       # Main training & classification logic
 │   ├─ ArticlesEmbedding.java            # Stores embeddings for articles
 │   ├─ Glove.java                        # Loads GloVe vectors
 │   ├─ HtmlParser.java                   # Extracts text from HTML
 │   ├─ Toolkit.java                      # Utility functions
 │   ├─ Vector.java                       # Vector math operations
 │   ├─ InvalidSizeException.java
 │   └─ InvalidTextException.java
 ├─ main/resources/
 │   ├─ glove.6B.50d_Reduced.csv          # Reduced GloVe file
 │   └─ News/                             # 32 sample news articles (.htm)
 └─ test/java/...                         # JUnit test files
```

---

## Requirements
- Java 17 or higher  
- Maven 3.8+  
- Internet connection **not required** (all resources are local)

---

## How to Build & Run

### 1️) Run tests
```bash
mvn -q test
```

### 2️) Package the project
```bash
mvn -q package
```

### 3️) Run the main class
**Easiest:** Open in IntelliJ / VS Code and run  
`uob.oop.AdvancedNewsClassifier`

**Command line (optional):**
```bash
java -cp "target/AdvancedNewsClassifier-1.0-SNAPSHOT.jar:$(mvn -q -Dexec.classpathScope=runtime -Dexec.executable=echo --non-recursive org.codehaus.mojo:exec-maven-plugin:1.6.0:exec -Dexec.args='%classpath%')" uob.oop.AdvancedNewsClassifier
```

---

## Tech Stack
- Java 17  
- Maven  
- **DL4J (DeepLearning4J)**  
- **ND4J (Numerical library)**  
- **Stanford CoreNLP**  
- **JUnit 5** (Testing)

---

## Core Concepts
- Natural Language Processing (NLP)
- Word vector embeddings (GloVe)
- Neural network classification (DL4J)
- Data cleaning and preprocessing
- Maven build lifecycle and dependency management

---

## Example Output
```text
Loading GloVe vectors...
Processing articles...
Training neural network...
Epoch 1/10 - Accuracy: 0.91
Epoch 10/10 - Accuracy: 0.96
Model ready for predictions!
```

---

## Notes
- The included GloVe file is reduced for faster processing and smaller file size.  
- The 32 HTML news files in `/resources/News/` serve as sample training data.  
- You can expand the dataset or use a larger GloVe model if needed.  

---

## Future Improvements
- Add more NLP features (TF-IDF, sentiment, etc.)
- Implement cross-validation and model evaluation
- Expand dataset for greater generalization

---

## Author
**Layan Barakat**  
University of Birmingham Dubai  
*(Advanced OOP Coursework — News Classification Project)*
