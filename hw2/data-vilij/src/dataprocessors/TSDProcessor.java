package dataprocessors;

import javafx.geometry.Point2D;
import javafx.scene.chart.XYChart;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

/**
 * The data files used by this data visualization applications follow a tab-separated format, where each data point is
 * named, labeled, and has a specific location in the 2-dimensional X-Y plane. This class handles the parsing and
 * processing of such data. It also handles exporting the data to a 2-D plot.
 * <p>
 * A sample file in this format has been provided in the application's <code>resources/data</code> folder.
 *
 * @author Ritwik Banerjee
 * @see XYChart
 */
public final class TSDProcessor {

    public static class InvalidDataNameException extends Exception {

        private static final String NAME_ERROR_MSG = "All data instance names must start with the @ character.";

        public InvalidDataNameException(String name) {
            super(String.format("Invalid name '%s'." + NAME_ERROR_MSG, name));
        }
    }

    private Map<String, String>  dataLabels;
    private Map<String, Point2D> dataPoints;
    private double x_min,x_max,y_sum,x_count;

    public TSDProcessor() {
        dataLabels = new HashMap<>();
        dataPoints = new HashMap<>();
        x_min = Integer.MAX_VALUE;
        x_max = Integer.MIN_VALUE;
        x_count = 0;
        y_sum = 0;
    }

    /**
     * Processes the data and populated two {@link Map} objects with the data.
     *
     * @param tsdString the input data provided as a single {@link String}
     * @throws Exception if the input string does not follow the <code>.tsd</code> data format
     */
    public void processString(String tsdString) throws Exception {
        AtomicBoolean hadAnError   = new AtomicBoolean(false);
        StringBuilder errorMessage = new StringBuilder();
        Stream.of(tsdString.split("\n"))
              .map(line -> Arrays.asList(line.split("\t")))
              .forEach(list -> {
                  try {
                      String   name  = checkedname(list.get(0));
                      String   label = list.get(1);
                      String[] pair  = list.get(2).split(",");
                      Point2D  point = new Point2D(Double.parseDouble(pair[0]), Double.parseDouble(pair[1]));
                      dataLabels.put(name, label);
                      dataPoints.put(name, point);
                      x_count++;
                      if (x_min > Double.parseDouble(pair[0])){x_min = Double.parseDouble(pair[0]);}
                      if (x_max < Double.parseDouble(pair[0])){x_max = Double.parseDouble(pair[0]);}
                      y_sum += Double.parseDouble(pair[1]);
                  } catch (Exception e) {
                      errorMessage.setLength(0);
                      errorMessage.append(e.getClass().getSimpleName()).append(": ").append(e.getMessage());
                      hadAnError.set(true);
                  }
              });
        if (errorMessage.length() > 0)
            throw new Exception(errorMessage.toString());
    }

    /**
     * Exports the data to the specified 2-D chart.
     *
     * @param chart the specified chart
     */
    void toChartData(XYChart<Number, Number> chart) {
        XYChart.Series<Number, Number> average = new XYChart.Series<>();
        System.out.println(x_count);
        System.out.println(y_sum);
        average.getData().add(new XYChart.Data<>(x_min,y_sum/x_count));
        average.getData().add(new XYChart.Data<>(x_max,y_sum/x_count));
        chart.getData().add(average);
        
        Set<String> labels = new HashSet<>(dataLabels.values());
        for (String label : labels) {
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName(label);
            dataLabels.entrySet().stream().filter(entry -> entry.getValue().equals(label)).forEach(entry -> {
                Point2D point = dataPoints.get(entry.getKey());
                XYChart.Data data = new XYChart.Data<>(point.getX(), point.getY());
                data.setNode(new HoverPane(entry.getKey().substring(1,entry.getKey().length())));
                series.getData().add(data);
            });
            chart.getData().add(series);
        }
    }
    
    void clear() {
        dataPoints.clear();
        dataLabels.clear();
        x_min = Integer.MAX_VALUE;
        x_max = Integer.MIN_VALUE;
        x_count = 0;
        y_sum = 0;
    }

    private String checkedname(String name) throws InvalidDataNameException {
        if (!name.startsWith("@"))
            throw new InvalidDataNameException(name);
        return name;
    }

    private static class HoverPane extends StackPane {
        public HoverPane(String label) {
            final Label l= new Label(label);
            l.setMinSize(100,30);
            
            setOnMouseEntered(e -> {
               getChildren().add(l);
               setCursor(Cursor.CROSSHAIR);
            });
            
            setOnMouseExited(e -> {
               getChildren().clear();
               setCursor(Cursor.DEFAULT);
            });
        }
    }
}
