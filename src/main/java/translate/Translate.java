package translate;

import com.google.gson.*;
import com.squareup.okhttp.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This is a sample Java stand alone application that
 * Translate texts using Translation API V3
 */
public class Translate {
    //Subscription Key from the Azure Portal Translation Service
    String subscriptionKey = "your subscription key (key1 for your service from the portal)";

    //API endpoint
    String url = "https://api.cognitive.microsofttranslator.com/translate?api-version=3.0&to=";

    private static String DELIMITER = "\\|";
    private static String INPUT_FILE_PATH = "src/main/resources/input/";
    private static String RESULTS_FILE_PATH = "src/main/resources/results/";

    // Instantiates the OkHttpClient.
    OkHttpClient client = new OkHttpClient();

    private static List<String> inputData = null;
    private static List<String> outputData = null;

    /**
     *
     * @throws IOException
     */
    private static void loadInputData(String inFile) throws IOException {

        String inputFile =  INPUT_FILE_PATH+inFile;

        inputData = new ArrayList<>();

        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        String currentLine;

        while((currentLine = reader.readLine()) != null){
            inputData.add(currentLine);
        }
        reader.close();
    }

    /**
     *
     * @throws IOException
     */
    private static void loadInputDataFromBlob() throws IOException {
       ////TODO:
    }

    /**
     *
     * @throws IOException
     */
    private static void writeResultsToBlob() throws IOException {
        ////TODO:
    }

    /**
     * This function performs a POST request.
     * @param text
     * @param to
     * @return
     * @throws IOException
     */
    public String Post(String text, String to) throws IOException {

        MediaType mediaType = MediaType.parse("application/json");

        RequestBody body = RequestBody.create(mediaType,
                "[{\n\t\"Text\": \""+  text +"\"\n}]");

        Request request = new Request.Builder()
                .url(url+to+"").post(body)
                .addHeader("Ocp-Apim-Subscription-Key", subscriptionKey)
                .addHeader("Content-type", "application/json").build();
                ///Add this for custom translation based on your custom training content.
                //Ex: .addHeader("content", "Technology")
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    /**
     * This function prettifies the json response.
     * @param json_text
     * @return
     */
    public static String prettify(String json_text) {
        JsonParser parser = new JsonParser();
        JsonElement json = parser.parse(json_text);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(json);
    }

    /**
     * Method to Translate the text
     * @throws IOException
     */
    public static void translateText() throws IOException {
        Translate translateRequest = new Translate();
        outputData = new ArrayList<>();

        for(String inputDataline : inputData) {

            String[] lineArr = inputDataline.split(DELIMITER);

            //Key line which does the translation text
            String response = translateRequest.Post(lineArr[0], lineArr[1]);
            outputData.add(lineArr[0]+"|"+ prettify(response)+"\n");
            //System.out.println(prettify(response));
        }
    }

    /**
     * Write the Translated text to the output file
     * @throws IOException
     */
    private static void writeOutputData(String fileName) throws IOException {

        String outputFile =  RESULTS_FILE_PATH+"results_"+fileName;
        FileWriter writer = new FileWriter(outputFile);

        BufferedWriter bufferedWriter = new BufferedWriter(writer);
        bufferedWriter.write(outputData.toString());
        bufferedWriter.close();
    }

    /**
     * Main method for the translation logic
     * @param args
     */
    public static void main(String[] args) {
        try {

            if(args.length == 0) {
                System.out.println("Please provide an input file name");
                return;
            }
            String fileName = args[0];

            System.out.println("Input file provided for Translation is " + fileName);
            //Load Input data
            loadInputData(fileName);

            System.out.println("Translating the contents using Translation Text API v3.0... ");
            //translateText
            translateText();

            //Write output
            writeOutputData(fileName);
            System.out.println("Completed the translation. Please check the output!");

        } catch (Exception e) {
            e.printStackTrace();
            //System.out.println(e);
        }
    }
}
