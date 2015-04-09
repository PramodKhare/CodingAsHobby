package airdel.a3;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import airdel.a3.util.Parser;
import au.com.bytecode.opencsv.CSVWriter;

/**
 * DelayPredicter - uses model.m to predict if a flight represented by every single line in
 * predict.csv will be delayed or not and modifies the predict.csv with prediction values
 * 
 * @author Pramod Khare, Ryan Millay, Nikit Waghela
 * @Created Tue Feb 24 19:35:48 EST 2015
 * @Modified
 */
public class DelayPredicter {
    private final Map<String, Integer> keypairPercentageMap = new HashMap<String, Integer>();
    private final Pattern pattern = Pattern.compile("\\t+");
    private final Parser parser = new Parser(',');
    private String mFile;
    private String pFile;
    private File modelFile;
    private File predictFile;
    private File tmpPredictCSVFile;

    public DelayPredicter() {}

    public DelayPredicter(final String mFile, final String pFile) {
        this.mFile = mFile;
        this.pFile = pFile;
    }

    /**
     * Predict program -> takes two files as input - model.m and predict.csv Outputs the modified
     * predict.csv with the arrival delay column filled
     * 
     * @param args
     * @throws Exception
     */
    public void predictArrivalDelay() throws Exception {
        BufferedReader br = null;
        CSVWriter writer = null;
        try {
            // First check if both model.m and predict.csv exists
            validateFiles();

            // Read model.m file into HashMap <String, Integer>
            // Load the whole model.m file into HashMap
            loadModelIntoMap(modelFile);

            // Create a new temporary predict csv file - name it like "predict_tmp231313343.csv"
            createTempPredictCSVFile();

            writer = new CSVWriter(new FileWriter(tmpPredictCSVFile));

            // Read predict.csv one line at a time
            br = new BufferedReader(new FileReader(predictFile));

            String line = null;
            String[] valuesToWriteToNewCSV = null;
            // Read predict file line by line, parse it using parser
            while ((line = br.readLine()) != null) {
                valuesToWriteToNewCSV = processLine(line);
                // print the line with prediction value for ArrDel15
                writer.writeNext(valuesToWriteToNewCSV);
                writer.flush();
            }

            // Delete the old predict.csv file and Rename the tmp file to predict.csv
            String predictFileName = predictFile.getName();

            if (predictFile.delete()) {
                tmpPredictCSVFile.renameTo(new File(predictFileName));
            } else {
                System.out.println("Unable to delete " + predictFileName);
                System.out.println("Predictions stored in file - " + tmpPredictCSVFile.getName());
            }
        } catch (final IOException e) {
            System.err.println("Failed to load prediction model - " + e.getMessage());
            throw e;
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
                if (writer != null) {
                    writer.close();
                }
            } catch (final IOException ex) {
                System.err.println("Unable to close file-streams");
                throw ex;
            }
        }
    }

    /**
     * Validate if both model.m and predict.csv files exists, if not exit giving proper message
     */
    private void validateFiles() {
        // First check if both model.m and predict.csv exists
        this.modelFile = new File(this.mFile);
        this.predictFile = new File(this.pFile);
        if (!modelFile.exists() || !predictFile.exists()) {
            System.err.println("Please verify input files exists, then try again.");
            System.exit(-1);
        }
    }

    /**
     * Creates a temporary file which stores all the predicted results.
     * 
     * This temporary file will be created in the same directory of given predict.csv
     * 
     * This file will replace the original predict.csv file in the end
     * 
     * @throws IOException
     */
    private void createTempPredictCSVFile() throws IOException {
        // Get predict.csv 's directory path
        String predictDirPath =
                this.predictFile.getCanonicalPath().substring(0,
                        this.predictFile.getCanonicalPath().lastIndexOf(File.separatorChar) + 1);
        // Construct the temporary predict.csv file name - make it dynamic with timestamp
        this.tmpPredictCSVFile =
                new File(predictDirPath + "predict_tmp" + System.currentTimeMillis() + ".csv");
        if (!tmpPredictCSVFile.createNewFile()) {
            System.err.println("Unable to create temporary file, check you have proper permisions");
            System.exit(-1);
        }
    }

    /**
     * Load the model.m file and load it into keypair-to-delay-percentage hashmap
     * 
     * @Model.m => each line contains keypair value each space separated with delay percentage
     * @param modelFile
     * @throws IOException
     */
    private void loadModelIntoMap(final File modelFile) throws IOException {
        BufferedReader br = null;
        //
        try {
            br = new BufferedReader(new FileReader(modelFile));

            String line = null;
            // Read model.m file line by line
            while ((line = br.readLine()) != null) {
                // Split the line along the whitespace(tab, space) characters
                String[] tokens = pattern.split(line);
                keypairPercentageMap.put(tokens[0], Integer.parseInt(tokens[1]));
            }
        } catch (final IOException e) {
            System.err.println("Failed to load prediction model - " + e.getMessage());
            throw e;
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (final IOException ex) {
                System.err.println("Unable to close file-streams");
                throw ex;
            }
        }
    }

    /**
     * This function will parse the input csv line from predict.csv file, then checks the results
     * from model.m using Parser.KEYS.
     * 
     * And update the CSV ArrDel15 value to either 1 or 0.
     * 
     * @param line
     * @return
     */
    private String[] processLine(String line) {
        String[] result = null;
        try {
            // parse the line
            parser.parse(line);
            if (!parser.isValid()) {
                // Return the row as is - as it is in invalid format
                return parser.getValues();
            }

            Integer percentage = 0;
            boolean willFlightBeDelayed = false;
            // For each keypair, check the HashMap of key with arrival-delay-percentage
            for (String[] key : Parser.KEYS) {
                percentage = keypairPercentageMap.get(parser.getKeyValuePairs(key));
                // If the percentage is greater than or equals to 5000 i.e. 50.00%
                if (null != percentage && percentage >= 5000) {
                    willFlightBeDelayed = true;
                    break;
                }
            }
            parser.setArrDel15(willFlightBeDelayed ? 1 : 0);

            // Get the String values array for each header
            result = parser.getValues();
        } catch (final Exception e) {
            System.out.println("Error While processing the line, skipping it");
        }
        return result;
    }
}
