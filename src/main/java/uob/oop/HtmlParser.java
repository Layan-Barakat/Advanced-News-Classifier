package uob.oop;

public class HtmlParser {
    /***
     * Extract the title of the news from the _htmlCode.
     * @param _htmlCode Contains the full HTML string from a specific news. E.g. 01.htm.
     * @return Return the title if it's been found. Otherwise, return "Title not found!".
     */
    public static String getNewsTitle(String _htmlCode) {
        String titleTagOpen = "<title>";
        String titleTagClose = "</title>";

        int titleStart = _htmlCode.indexOf(titleTagOpen) + titleTagOpen.length();
        int titleEnd = _htmlCode.indexOf(titleTagClose);

        if (titleStart != -1 && titleEnd != -1 && titleEnd > titleStart) {
            String strFullTitle = _htmlCode.substring(titleStart, titleEnd);
            return strFullTitle.substring(0, strFullTitle.indexOf(" |"));
        }

        return "Title not found!";
    }

    /***
     * Extract the content of the news from the _htmlCode.
     * @param _htmlCode Contains the full HTML string from a specific news. E.g. 01.htm.
     * @return Return the content if it's been found. Otherwise, return "Content not found!".
     */
    public static String getNewsContent(String _htmlCode) {
        String contentTagOpen = "\"articleBody\": \"";
        String contentTagClose = " \",\"mainEntityOfPage\":";

        int contentStart = _htmlCode.indexOf(contentTagOpen) + contentTagOpen.length();
        int contentEnd = _htmlCode.indexOf(contentTagClose);

        if (contentStart != -1 && contentEnd != -1 && contentEnd > contentStart) {
            return _htmlCode.substring(contentStart, contentEnd).toLowerCase();
        }

        return "Content not found!";
    }

    public static NewsArticles.DataType getDataType(String _htmlCode) {
        //TODO Task 3.1 - 1.5 Marks
        String openingTag = "<datatype>"; //defining the opening and closing tags.
        String closingTag = "</datatype>";

        int start = _htmlCode.indexOf(openingTag); //locating the positions of the opening and closing tags in the html code array
        int end = _htmlCode.indexOf(closingTag);
        if (end != -1 && start != -1){ //checking if the tags are there or not, if they are it adjusts the start to point to the end of the opening tag
            start += openingTag.length(); //extracts the substring between opening and closing tags
            String dtstring = _htmlCode.substring(start, end).trim(); //removes trailing and leading whitespaces from the extracted substring.
            if (dtstring.equalsIgnoreCase("Training")){ //compares the data type string to the training string (ignoring lower/uppercase) and then if they match returns Training
                return NewsArticles.DataType.Training;
            }
            else { //otherwise it returns Testing.
                return NewsArticles.DataType.Testing;
            }

        }
        return NewsArticles.DataType.Testing; //Please modify the return value.
    } //if the opening and closing tags arent found then it returns the defualt value of testing

    public static String getLabel (String _htmlCode) {
        //TODO Task 3.2 - 1.5 Marks
        String openingtag = "<label>"; //defining the opening and closing tags
        String closingtag = "</label>";
        int start = _htmlCode.indexOf(openingtag) + openingtag.length(); //finding the starting positions and putting it right after the opening tag ends.
        int end = _htmlCode.indexOf(closingtag); //finding the ending position

        if (start != -1 && end != -1){ //checking if the end and start are found
            return _htmlCode.substring(start, end); //If the tags are found the content between the opening and closing tags gets returned.
        } //If the opening and closing tags are not found the method returns -1.
        return "-1"; //Please modify the return value.
    }


}
