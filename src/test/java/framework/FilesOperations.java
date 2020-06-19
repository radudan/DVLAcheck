package framework;

import org.apache.log4j.Logger;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FilesOperations {
    private static Logger log = LogConfig.getLoger(FilesOperations.class);

    public List<String> readFile(String filePath) {
        List<String> cellsData= new ArrayList<>();
        try {
            File myObj = new File(filePath);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                cellsData.add(data);
            }

            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return cellsData;
    }

    public List<HashMap<String, String>> getCarDetails(List<String> carData) {
        List<HashMap<String, String>> results = new ArrayList<>();
        String[] headers = carData.get(0).split(",");
        carData.remove(0);
        for(int i = 0; i < carData.size(); i++) {
            String[] carDetail = carData.get(i).split(",");
            HashMap<String, String> carDetails = new HashMap<String, String>();
            for(int j = 0; j < carDetail.length; j++)
                carDetails.put(headers[j], carDetail[j]);
            results.add(carDetails);
        }
        return results;
    }

    public List<HashMap<String, String>> extractRegistration(String stringData, Pattern p) {
        Matcher m = p.matcher(stringData);
        List<HashMap<String, String>> results = new ArrayList<>();
        while (m.find()) {
            String theGroup = m.group().replace(" ","");
            HashMap<String, String> carDetails = new HashMap<String, String>();
            carDetails.put("REGISTRATION", theGroup);
            results.add(carDetails);
        }
        return  results;
    }
}
