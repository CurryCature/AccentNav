package com.example.accentapp;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class sortJSON {

  public static void main(String[] args) throws Exception {
    String filePath = "/data/user/0/com.example.accentapp/files/response.json";
    String jsonData = new String(Files.readAllBytes(Paths.get(filePath)));
    //String jsonData = "{\"american\": 0.12461716681718826, \"australian\": 0.13232995569705963, \"english\": 0.14486651122570038, \"german\": 0.10987783223390579, \"indian\": 0.13659852743148804, \"irish\": 0.12081635743379593, \"scottish\": 0.117350272834301, \"southernafrica\": 0.11354334652423859}";

    // Detect error
    if (jsonData.contains("\"error\":")) {
      int start = jsonData.indexOf(":") + 3;
      int end = jsonData.lastIndexOf("\"");
      String errorMessage = jsonData.substring(start, end);
      System.out.println("Error: " + errorMessage);
      return;
    }

    // Split the string by commas and remove curly braces
    String[] keyValuePairs = jsonData.substring(1, jsonData.length() - 1).split(",");

    // Create a Map to store key-value pairs
    Map<String, Double> dataMap = new HashMap<>();
    for (String pair : keyValuePairs) {
      String[] keyValue = pair.trim().split(":");
      dataMap.put(keyValue[0].trim().replace("\"", ""), Double.parseDouble(keyValue[1].trim()));
    }

    // Create a custom class to hold key-value pair
    class KeyValue {
      String key;
      double value;

      public KeyValue(String key, double value) {
        this.key = key;
        this.value = value;
      }
    }

    // Convert Map entries to a List of KeyValue objects
    List<KeyValue> keyValueList = new ArrayList<>();
    for (Map.Entry<String, Double> entry : dataMap.entrySet()) {
      keyValueList.add(new KeyValue(entry.getKey(), entry.getValue()));
    }

    // Sort the List by value in descending order
    Collections.sort(keyValueList, new Comparator<KeyValue>() {
      @Override
      public int compare(KeyValue o1, KeyValue o2) {
        return Double.compare(o2.value, o1.value);
      }
    });

    // Separate keys and values into separate lists
    List<String> keys = new ArrayList<>();
    List<String> values = new ArrayList<>();
    for (KeyValue keyValue : keyValueList) {
        keys.add(keyValue.key);
        double doubleValue = keyValue.value * 100;
        DecimalFormat df = new DecimalFormat("0.00");
        values.add(df.format(doubleValue) + "%");
    }

    // Print the keys and values
    System.out.println("Keys: " + keys);
    System.out.println("Values: " + values);
  }
}
