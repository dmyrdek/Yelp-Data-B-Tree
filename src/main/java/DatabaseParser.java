import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.util.List;

public class DatabaseParser {

    public List<Business> businessesParser() throws IOException{
        List<Business> businesses = null;
        BufferedReader reader = new BufferedReader(new FileReader("fixed_dataset/business.json"));


        try {
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            TypeToken<List<Business>> token = new TypeToken<List<Business>>(){};
            businesses = gson.fromJson(reader, token.getType());

            //System.out.println(businesses.get(0).getName());

        } catch( Exception ex ){
            if(reader != null){ reader.close(); }
        }

        return businesses;
    }

    /*public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("fixed_dataset/business.json"));
        try {
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();


            TypeToken<List<Business>> token = new TypeToken<List<Business>>(){};
            List<Business> businesses = gson.fromJson(reader, token.getType());


            System.out.println(businesses.get(0).getName());

            String tempCategories[];
            for(int i = 0; i < businesses.length; i++){
                tempCategories = businesses[i].getCategories();
                System.out.println("\n" + businesses[i].getName());
                for(int j = 0; j < tempCategories.length; j++){
                    System.out.println(tempCategories[j]);
                }
            }

        } catch( Exception ex ){
            if(reader != null){ reader.close(); }
        }
    } */
}
