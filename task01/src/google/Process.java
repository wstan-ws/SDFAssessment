package google;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Process {

    private int totalCount;

    public void start(String file) throws Exception {
        
        try (FileReader fr = new FileReader(file)) {
            BufferedReader br = new BufferedReader(fr);
        
            Map<String, List<Application>> classified = br.lines()
                .skip(1)
                .map(line -> line.trim().split(","))
                .map (field -> new Application(field[0], field[1].toUpperCase(), (field[2])))
                .collect(Collectors.groupingBy(app -> app.getCategory()));
            
            for (String category : classified.keySet()) {
                List<Application> apps = classified.get(category);
                double maxValue = 0;
                double minValue = 100;
                double avgValue = 0;
                double sum = 0;
                String maxApp = "";
                String minApp = "";
                int categoryCount = 0;
                int invalidCount = 0;
                for (Application app : apps) {
                    if (Double.parseDouble(app.getRating()) > maxValue) {
                        maxValue = Double.parseDouble(app.getRating());
                        maxApp = app.getName();
                    }
                    if (Double.parseDouble(app.getRating()) < minValue) {
                        minValue = Double.parseDouble(app.getRating());
                        minApp = app.getName();
                    }
                    if (app.getRating().equals("NaN")) {
                        invalidCount++;
                    } else {
                        sum += Double.parseDouble(app.getRating());
                    }
                    categoryCount++;
                    totalCount++;
                    avgValue = sum / (categoryCount - invalidCount);
                }
                System.out.printf("Category: %s\n\tHighest: %s, %.2f\n\tLowest: %s, %.2f\n\tAverage: %.2f\n\tCount: %d\n\tDiscarded: %d\n\n", category, maxApp, maxValue, minApp, minValue, avgValue, categoryCount, invalidCount);
            }
            System.out.printf("Total lines in file: %s", totalCount);
        }   
    }
}