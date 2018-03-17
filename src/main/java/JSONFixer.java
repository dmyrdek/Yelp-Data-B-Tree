import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileReader;
import java.util.List;

public class JSONFixer {
    public void fix(String directory) throws IOException{
        StringBuilder fixedJSON = new StringBuilder();
        BufferedReader reader = new BufferedReader( new FileReader(directory));
        String outputName = "fixed_dataset" + directory.substring(11);
        FileWriter writer = new FileWriter(outputName);

        try {

            fixedJSON.append("[\n");
            for( String line = reader.readLine(); line != null; line = reader.readLine() ){
                fixedJSON.append(line).append(",\n");
            }
            fixedJSON.setLength(fixedJSON.length()-2);
            fixedJSON.append(']');


        } catch(Exception ex ){
            if(reader != null){ reader.close(); }
        }

        writer.write(fixedJSON.toString());
        writer.close();
    }

    public void fixPhotos(String directory, List<Business> businesses) throws IOException{
        StringBuilder fixedJSON = new StringBuilder();
        BufferedReader reader = new BufferedReader( new FileReader(directory));
        String outputName = "fixed_dataset" + directory.substring(11);
        FileWriter writer = new FileWriter(outputName);

        try {

            fixedJSON.append("[\n");
            for( String line = reader.readLine(); line != null; line = reader.readLine() ){
                String businessID = line.substring(line.indexOf("business_id\": \""), line.indexOf("\", \"label\": "));
                String businessName = "";
                businessID = businessID.substring(15);
                for (Business b : businesses){
                    if (businessID.equals(b.getBusiness_id())){
                        System.out.println("Photo business id : " + businessID + " Matched with " + b.getBusiness_id() + " aka: " + b.getName());
                        businessName = b.getName();
                    }
                }
                //System.out.println(businessID);
                fixedJSON.append(line.substring(0, line.length() - 1)).append(", \"name\": \"" + businessName + "\"},\n");
                //line.substring(0, line.length()-1);
            }
            fixedJSON.setLength(fixedJSON.length()-2);
            fixedJSON.append(']');
            //System.out.println(fixedJSON.toString());


        } catch(Exception ex ){
            if(reader != null){ reader.close(); }
        }

        writer.write(fixedJSON.toString());
        writer.close();
    }

    public static void main(String[] args) throws IOException{
        JSONFixer jsonFixer = new JSONFixer();
        //jsonFixer.fixPhotos("raw_dataset/photos.json");
        //jsonFixer.fix("raw_dataset/business.json");
        //jsonFixer.fix("raw_dataset/checkin.json");
        //jsonFixer.fix("raw_dataset/photos.json");
        //jsonFixer.fix("raw_dataset/review.json");
        //jsonFixer.fix("raw_dataset/tip.json");
        //jsonFixer.fix("raw_dataset/user.json");
    }
}
