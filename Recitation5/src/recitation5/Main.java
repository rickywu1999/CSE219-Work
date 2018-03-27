import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Line Chart Sample");

        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Number of Month");
        yAxis.setLabel("Stock Price");

        final LineChart lineChart = new LineChart(xAxis, yAxis);

        lineChart.setTitle("Stock Monitoring, 2010");
        
        XYChart.Series series = new XYChart.Series();
        series.setName("My portfolio");
        
        series.getData().add(new XYChart.Data(1, 23));
        series.getData().add(new XYChart.Data(2, 14));
        series.getData().add(new XYChart.Data(3, 15));
        series.getData().add(new XYChart.Data(4, 24));
        series.getData().add(new XYChart.Data(5, 34));
        series.getData().add(new XYChart.Data(6, 36));
        series.getData().add(new XYChart.Data(7, 22));
        series.getData().add(new XYChart.Data(8, 45));
        series.getData().add(new XYChart.Data(9, 43));
        series.getData().add(new XYChart.Data(10, 17));
        series.getData().add(new XYChart.Data(11, 29));
        series.getData().add(new XYChart.Data(12, 25));
        
        XYChart.Series series2 = new XYChart.Series();
        series2.setName("Portfolio 2");
        series2.getData().add(new XYChart.Data(1, 33));
        series2.getData().add(new XYChart.Data(2, 34));
        series2.getData().add(new XYChart.Data(3, 25));
        series2.getData().add(new XYChart.Data(4, 44));
        series2.getData().add(new XYChart.Data(5, 39));
        series2.getData().add(new XYChart.Data(6, 16));
        series2.getData().add(new XYChart.Data(7, 55));
        series2.getData().add(new XYChart.Data(8, 54));
        series2.getData().add(new XYChart.Data(9, 48));
        series2.getData().add(new XYChart.Data(10, 27));
        series2.getData().add(new XYChart.Data(11, 37));
        series2.getData().add(new XYChart.Data(12, 29));
        
        XYChart.Series series3 = new XYChart.Series();
        series3.setName("Portfolio 3");
        int i = 0;
        int[] sum = new int[12];
        for (Object a: series.getData()){
            sum[i] = (int)(((XYChart.Data)a).getYValue());
            i++;
        }
        i = 0;
        for (Object a: series2.getData()){
            sum[i] += (int)(((XYChart.Data)a).getYValue());
            i++;
        }
        i = 0;
        for (int x = 1; x<=12;x++){
            series3.getData().add(new XYChart.Data(x,sum[i]));
            i++;
        }

        Scene scene = new Scene(lineChart, 800, 600);
        lineChart.getData().addAll(series,series2,series3);
        lineChart.setCreateSymbols(false);

        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
