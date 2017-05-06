package com.sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.*;
import java.util.*;

import static com.sample.CsvFileWriter.COMMA_DELIMITER;
import static com.sample.CsvFileWriter.writeCsvFileClassAndAttributes;
import static com.sample.GlobalData.*;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("WPS Demo");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }


    public static void main(String[] args) throws Exception {
        parseData(trainingData, TRAINING_DATA_FILE);
        parseData(validationData, VAILDATION_DATA_FILE);
        trainingDataCount = trainingData.size();
        validationDataCount = validationData.size();

        // use KNN classifier
        KnnAlgorithm.prepareKnnDataParallel();
        for (int k = 1; k <= 20; k++) {
            KnnAlgorithm.doPrediction(k, false);
        }

        // use Naive Bayes classifier
//        prepareNaiveBayesData();
//        NaiveBayesAlgorithm.doPrediction();

        // Weka algorithms

//        parseHeaders();
//        writeCsvFileClassAndAttributes(TRAINING_DATA_FILE_FLOOR, trainingData, FINGERPRINT_HEADER_FLOOR, true);
//        writeCsvFileClassAndAttributes(VAILDATION_DATA_FILE_FLOOR, validationData, FINGERPRINT_HEADER_FLOOR, true);
//        WekaAlgorithm.prepareFloorData(TRAINING_DATA_FILE_FLOOR, VAILDATION_DATA_FILE_FLOOR);
//        writeCsvFileClassAndAttributes(TRAINING_DATA_FILE_BUILDING_ID, trainingData, FINGERPRINT_HEADER_FLOOR, false);
//        writeCsvFileClassAndAttributes(VAILDATION_DATA_FILE_BUILDING_ID, validationData, FINGERPRINT_HEADER_FLOOR, false);
//        WekaAlgorithm.prepareBuildingIdData(TRAINING_DATA_FILE_BUILDING_ID, VAILDATION_DATA_FILE_BUILDING_ID);

//        WekaAlgorithm.prepareFloorData("humanTrainingData.csv", "humanValidationData.csv");
//        WekaAlgorithmNaiveBayes.trainClassifier();
//        WekaAlgorithmNaiveBayes.doPrediction();

        // use Naive Bayes classifier from Weka
//        WekaAlgorithmNaiveBayes.trainClassifier();
//        WekaAlgorithmNaiveBayes.doPrediction();

        // use Random Forest classifier from Weka
//        WekaAlgorithmRandomForest.trainClassifier();
//        WekaAlgorithmRandomForest.doPrediction();

        // use J48 classifier from Weka
//        WekaAlgorithmJ48.trainClassifier();
//        WekaAlgorithmJ48.doPrediction();

        launch(args);
    }

    private static void parseData(List<Fingerprint> data, String dataFile) {
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";

        try {
            InputStream is = App.class.getClassLoader().getResourceAsStream(dataFile);
            br = new BufferedReader(new InputStreamReader(is));

            line = br.readLine();

            while ((line = br.readLine()) != null) {
                String[] example = line.split(cvsSplitBy); // use comma as separator

                int [] wapSignalIntensities = new int [WAPS_COUNT];
                for (int i = 0; i < WAPS_COUNT; i++) {
                    int signal = Integer.parseInt(example[i]);
                    if (signal == 100) signal = -200;
                    wapSignalIntensities[i] = signal;
                }
                double longitude = Double.parseDouble(example[WAPS_COUNT]);
                double latitude = Double.parseDouble(example[WAPS_COUNT + 1]);
                int floor = Integer.parseInt(example[WAPS_COUNT + 2]);
                int buildingId = Integer.parseInt(example[WAPS_COUNT + 3]);

                data.add(new Fingerprint(wapSignalIntensities, longitude, latitude, floor, buildingId));
            }
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Info: FileNotFoundException", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Info: IOException", JOptionPane.ERROR_MESSAGE);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(null, e.getMessage(), "Info: IOException", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private static void parseHeaders() {
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";

        try {
            InputStream is = App.class.getClassLoader().getResourceAsStream(VAILDATION_DATA_FILE);
            br = new BufferedReader(new InputStreamReader(is));

            line = br.readLine();
            String[] header = line.split(cvsSplitBy);
            for (int i = 0; i < WAPS_COUNT; i++) {
                FINGERPRINT_HEADER_FLOOR += header[i] + COMMA_DELIMITER;
                FINGERPRINT_HEADER_BUILDING_ID += header[i] + COMMA_DELIMITER;
            }
            FINGERPRINT_HEADER_FLOOR += header[WAPS_COUNT + 2];
            FINGERPRINT_HEADER_BUILDING_ID += header[WAPS_COUNT + 3];

        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Info: FileNotFoundException", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Info: IOException", JOptionPane.ERROR_MESSAGE);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(null, e.getMessage(), "Info: IOException", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}
