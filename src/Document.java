
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;

/**
 *
 * @author USER-PC
 */
public class Document extends File{
    public final ArrayList<String> fileCheckingList = new ArrayList<>();
    HashMap<DocumentKey, String> filePlagSection = new HashMap<>();
    String pathName;
    public long cursor = 0;
    public enum DocumentRole {PATTERNSTRING, TEXTSTRING};
    private DocumentRole docRole;
    
    public Document(String pathname) {
        super(pathname);
        addToCheckedFiles(this.getName());
        pathName = pathname;
        //build plagSection from processed documents 
        buildPlagSectionsFromFile();
    }
    
    public void setDocumentRole(DocumentRole d){
        docRole = d;
    }
    
    public DocumentRole getDocumentRole(){
        return docRole;
    }
       
    public final void addToCheckedFiles(String docName){
        fileCheckingList.add(docName);
    }

    public final boolean buildPlagSectionsFromFile(){        
        boolean built = false;
        try{
            //rebuilding plagSection hashMap from external file
            //if this document is a reference document i.e. previously scanned
            if(Arrays.asList(new File("../../DIRECTORY/PlagSections/").list()).contains(this.getName())){
                try (Scanner scanner = new Scanner(new File("../../DIRECTORY/PlagSections/"+getName()))) {
                    while(scanner.hasNext()){
                        String s = "", fileName = scanner.next(), stringPlagSectionIndex; 
                        
                        stringPlagSectionIndex = scanner.next();
//                      extract the plagiarised section's index from a given document
                        if(stringPlagSectionIndex.length() == 3)//if the string containing the plagiarised section index holds a single character
                            stringPlagSectionIndex = stringPlagSectionIndex.charAt(1)+"";
                        else{
                            char[] plagSectIndexNumbers = new char[stringPlagSectionIndex.length() - 2];
                            stringPlagSectionIndex.getChars(1, stringPlagSectionIndex.length() - 1, plagSectIndexNumbers, 0);
                            for(char c: plagSectIndexNumbers)
                                stringPlagSectionIndex += c;
                        }
                        
                        int plagSectionIndex = Integer.parseInt(stringPlagSectionIndex);
                        while(scanner.hasNext())
                            s += scanner.next()+" ";
                        
                        filePlagSection.put(new DocumentKey(fileName, plagSectionIndex), s);
                        System.out.println("Plagiarised Section Built from File: "+s);
                    }
                }
                built = true;
            }else
                built = false;
        }catch(FileNotFoundException ex){}
        return built;
    }
    
    public void printPlagSectionsToFile(){
        try{
            //Writing plagSections hashMap to external file
            try (PrintWriter writer = new PrintWriter("../../DIRECTORY/PlagSections/"+getName())) {
                //Writing plagSections hashMap to external file
                Set<DocumentKey> keys = filePlagSection.keySet();
                keys.stream().forEach((k) -> writer.print(k.getKeyDocName()+" ,"+k.getKeyPlagIndex()+":"+lineSeparator+filePlagSection.remove(k)+lineSeparator));
            }
        }catch(IOException ex){}
    }
    
    private final String lineSeparator = System.getProperty("line.separator");
    
    public class DocumentKey{
        String docName;
        int plagIndex;
        public DocumentKey(String s, int i){
            docName = s;
            plagIndex = i;
        }
        public String getKeyDocName(){return docName;}
        public int getKeyPlagIndex(){return plagIndex;}

        @Override
        public boolean equals(Object o) {
            return (o instanceof DocumentKey ? (docName.equals(((DocumentKey)o).docName) && plagIndex == ((DocumentKey)o).getKeyPlagIndex()) : false);
        }
    }
}