
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import javafx.beans.property.SimpleDoubleProperty;


/**
 *
 * @author USER-PC
 */
public class StringMatch_KR {
    public static int searchLength = 10;
    public final int minimalMatchLength = 10;
    public SimpleDoubleProperty comparisonProgress = new SimpleDoubleProperty(0);
    final Document doc1, doc2;
    final long patternStringFileSize;
    
    public StringMatch_KR(Document doc1, Document doc2){
        this.doc1 = doc1;
        this.doc2 = doc2;
        patternStringFileSize = getFileSize(doc1);
    }
    
    public void compare(){
        try{
            String patternString, textString; 
            Scanner patternStringScanner = new Scanner(doc1, "utf-8");
            doc1.setDocumentRole(Document.DocumentRole.PATTERNSTRING);
            doc2.setDocumentRole(Document.DocumentRole.TEXTSTRING);

                while(patternStringScanner.hasNext()){

                    patternString = scanString(patternStringScanner, doc1, false);
                    System.out.println("PatternString: " +patternString);

                    Scanner textStringScanner = new Scanner(doc2, "utf-8");
                    while(textStringScanner.hasNext()){
                        textString = scanString(textStringScanner, doc2, false);
                        System.out.println("TextString: "+textString);

                        if(patternString.equals(textString)){
                            while(patternString.equals(textString)){
                                
                                patternString = scanString(patternStringScanner, doc1, true);
                                textString = scanString(textStringScanner, doc2, true);
                                patternString = markString(patternString);
                                textString = markString(textString);
                                System.out.println("PatternString PD: " +patternString);
                                System.out.println("TextString PD: "+textString);
                            }

                            if(doc1.filePlagSection.isEmpty())
                                //add the plagiarised section to the hashmap of the patternString, with the textString as index
                                doc1.filePlagSection.put(doc1.new DocumentKey(doc2.getName(), 0), patternString);
                            else
                                doc1.filePlagSection.putIfAbsent(doc1.new DocumentKey(doc2.getName(), doc1.filePlagSection.keySet().size()), patternString);
                        }
                    }
                    doc2.addToCheckedFiles(doc1.getName());
                    doc1.addToCheckedFiles(doc2.getName());

                    System.out.println("File Size:"+patternStringFileSize);
                    comparisonProgress.set((0.01 * doc1.cursor) / patternStringFileSize);
                }

            doc1.printPlagSectionsToFile();
            doc2.printPlagSectionsToFile();

            doc1.filePlagSection.values().stream().forEach((s) -> System.out.println("PlagSections: "+s));
        }catch(IOException ex){}
    }
    
    public final char markStart= '\u033D', markEnd = '\u0353';
    public String markString(String s){
        if(s.startsWith(markStart+"")){
            s = s.substring(0, s.length()-1);
            s += ""+markEnd;
        }else
            s = markStart+" "+s+""+markEnd;
        
        return s;
    }
    
    public final long getFileSize(Document d){
        String string = "";
        try{
            Scanner scanner = new Scanner(d);
            while(scanner.hasNext())
                string += scanner.next()+" ";
            
        }catch(FileNotFoundException ex){}
        return string.length();        
    }
    
    String textOutputString = "", patternOutputString = ""; 
    public void setCursorPosition(Document d, String s){
        int output = 0;
        for(char c: s.toCharArray())
            output += (byte)c;
        d.cursor += output;
    }
    
    public String scanString(Scanner s, Document d, boolean b) throws FileNotFoundException{
        int k = 0; String lastString, outputString;
        
        if(d.getDocumentRole().equals(Document.DocumentRole.PATTERNSTRING))
            outputString = patternOutputString;
        else
            outputString = textOutputString;
        
        if(b){
            lastString = (s.hasNext() ? s.next()+" " : "");
            outputString += lastString;
            setCursorPosition(d, lastString);
        }
        else{
            searchLength = minimalMatchLength; 
            if(s.hasNext()){
                if(outputString.isEmpty() || outputString.equals(textOutputString) || outputString.equals(patternOutputString) && outputString.split(" ").length > searchLength){ 
                    outputString = "";
                    while(k < searchLength && s.hasNext()){
                        outputString += s.next()+" ";
                        k++;
                    }
                    setCursorPosition(d, outputString);
                }else if(outputString.equals(patternOutputString)){
                    lastString = s.next()+" ";
                    outputString += lastString;
                    String[] sArray = outputString.split(" ");
                    outputString = outputString.substring(sArray[0].length() + 1, outputString.length());
                    setCursorPosition(d, lastString);
                }
            }
        }
        
        if(d.getDocumentRole().equals(Document.DocumentRole.PATTERNSTRING))
            patternOutputString = outputString;
        else
            textOutputString = outputString;
        
        return outputString;
    }
    
    public void setSearchLength(int x){       searchLength = x;    }
    
    public int getSearchLength(){        return searchLength;    }

}