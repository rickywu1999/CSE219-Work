package ui;

import actions.AppActions;
import dataprocessors.AppData;
import java.io.File;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import vilij.templates.ApplicationTemplate;
import vilij.templates.UITemplate;
import static settings.AppPropertyTypes.*;
import static java.io.File.*;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import vilij.components.Dialog;


/**
 * This is the application's user interface implementation.
 *
 * @author Ritwik Banerjee
 */
public final class AppUI extends UITemplate{

    /** The application to which this class of actions belongs. */
    ApplicationTemplate applicationTemplate;

    @SuppressWarnings("FieldCanBeLocal")
    private Button                       scrnshotButton; // toolbar button to take a screenshot of the data
    private Button                       runButton;
    private RadioButton                  readOnly;       // radio button to toggle read-only
    private LineChart<Number, Number>    chart;          // the chart where data will be displayed
    private Button                       displayButton;  // workspace button to display data on the chart
    private Button                       clusteringButton;
    private Button                       classificationButton;
    private Button                       configButton;
    private Button                       backButton;
    private TextArea                     textArea;       // text area for new data input
    private TextArea                     metaDataArea;
    private boolean                      hasNewText;     // whether or not the text area has any new data since last display
    private String                       scrnshoticonPath;
    private String                       runiconPath;
    private boolean                      readOnlyBool;
    private String                       metaData;
    private VBox                         algorithmBox;
    private VBox                         textBox;
    private VBox                         leftSideBox;
    private VBox                         metaDataBox;
    private HashMap<String,int[]>        configSettings;
    private boolean                      doneRunning;
    private int                          instNum;
    private Label                        algorithmTypeLabel;
    
    public LineChart<Number, Number> getChart() { return chart; }

    public AppUI(Stage primaryStage, ApplicationTemplate applicationTemplate) {
        super(primaryStage, applicationTemplate);
        primaryScene.getStylesheets().add(getClass().getResource(applicationTemplate.manager.getPropertyValue(CSS_STYLE_SHEET_PATH.name())).toExternalForm());
        this.applicationTemplate = applicationTemplate;
        readOnlyBool = false;
        configSettings = new HashMap();
        hasNewText = false;
    }

    @Override
    protected void setResourcePaths(ApplicationTemplate applicationTemplate) {
        super.setResourcePaths(applicationTemplate);
        String iconsPath = "/" + String.join(separator,
                                             applicationTemplate.manager.getPropertyValue(GUI_RESOURCE_PATH.name()),
                                             applicationTemplate.manager.getPropertyValue(ICONS_RESOURCE_PATH.name()));
        scrnshoticonPath = String.join(separator, iconsPath, applicationTemplate.manager.getPropertyValue(SCREENSHOT_ICON.name()));
        runiconPath = String.join(separator, iconsPath, applicationTemplate.manager.getPropertyValue(RUN_ICON.name()));
    }

    @Override
    protected void setToolBar(ApplicationTemplate applicationTemplate) {
        super.setToolBar(applicationTemplate);
        newOn();
        scrnshotButton = setToolbarButton(scrnshoticonPath, applicationTemplate.manager.getPropertyValue(SCREENSHOT_TOOLTIP.name()), true);
        toolBar.getItems().add(scrnshotButton);
    }

    @Override
    protected void setToolbarHandlers(ApplicationTemplate applicationTemplate) {
        applicationTemplate.setActionComponent(new AppActions(applicationTemplate));
        newButton.setOnAction(e -> applicationTemplate.getActionComponent().handleNewRequest());
        saveButton.setOnAction(e -> applicationTemplate.getActionComponent().handleSaveRequest());
        loadButton.setOnAction(e -> applicationTemplate.getActionComponent().handleLoadRequest());
        exitButton.setOnAction(e -> applicationTemplate.getActionComponent().handleExitRequest());
        printButton.setOnAction(e -> applicationTemplate.getActionComponent().handlePrintRequest());
        scrnshotButton.setOnAction(e -> {
            try {
                ((AppActions)(applicationTemplate.getActionComponent())).handleScreenshotRequest();
            } catch (IOException ex) {
                applicationTemplate.getDialog(Dialog.DialogType.ERROR).show(applicationTemplate.manager.getPropertyValue(ERROR_LABEL.name()),
                    applicationTemplate.manager.getPropertyValue(FORMAT_ERROR_MESSAGE.name()));
            }
        });
    }

    @Override
    public void initialize() {
        layout();
        setWorkspaceActions();
    }

    @Override
    public void clear() {
        textArea.clear();
        chart.getData().clear();
        newClear();
        saveClear();
    }
    
    public void newClear(){newButton.setDisable(true);}
    public void saveClear(){saveButton.setDisable(true);}
    public void loadClear(){loadButton.setDisable(true);}
    public void newOn(){newButton.setDisable(false);}
    public void saveOn(){saveButton.setDisable(false);}
    public void loadOn(){loadButton.setDisable(false);}
    
    public String getTextArea(){
        return textArea.getText();
    }
    
    public void setTextArea(String data){
        textArea.clear();
        textArea.setText(data);
    }
   
    public void textBoxSetter() {
        try{
            textBox.getChildren().addAll(new Label(applicationTemplate.manager.getPropertyValue(TITLE_LABEL.name())), textArea);
        }
        catch (IllegalArgumentException e){
            //do nothing
        }
        readOnlyButtonSetter();
    }
    
    public void textBoxRemover(){
        textBox.getChildren().clear();
    }
    
    public void readOnlyButtonSetter(){
        textBox.getChildren().add(readOnly);
    }
    
    public void readOnlyButtonRemover(){
        textBox.getChildren().remove(readOnly);
    }
    
    private void backButtonSetter(){
        algorithmBox.getChildren().add(backButton);
        backButton.setOnAction(e -> {
            algorithmBox.getChildren().clear();
            algorithmTypeButtonSetter();
        });
    }
    
    public void backButtonDisable(){
        algorithmBox.setDisable(true);
    }
    
    public void backButtonEnable(){
        algorithmBox.setDisable(false);
    }
    
    
    private void algorithmButtonSetter(String algorithmType, String algorithmPath){
        File folder = new File(algorithmPath);
        File[] listOfFiles = folder.listFiles();
        ComboBox comboBox = new ComboBox();
        if (listOfFiles == null || listOfFiles.length <= 0) {
            applicationTemplate.getDialog(Dialog.DialogType.ERROR).show(applicationTemplate.manager.getPropertyValue(ERROR_LABEL.name()),
                applicationTemplate.manager.getPropertyValue(NO_ALGORITHM_ERROR_MESSAGE.name()));
            return;
        }
        
        algorithmBox.getChildren().clear();
        for (File i: listOfFiles){
            comboBox.getItems().add(i.getName().replace(".java",""));
        }
        comboBox.setOnAction(e -> {
            algorithmBox.getChildren().remove(displayButton);
            if (comboBox.getValue().toString() != null){
                ((AppData)(applicationTemplate.getDataComponent())).setNames(algorithmType, comboBox.getValue().toString());
                algorithmBox.getChildren().remove(configButton);
                algorithmBox.getChildren().remove(backButton);
                algorithmBox.getChildren().remove(runButton);
                configButtonSetter(algorithmType, comboBox.getValue().toString());
                backButtonSetter();
            }
        });
        algorithmBox.getChildren().addAll(new Label(applicationTemplate.manager.getPropertyValue(ALGORITHM_CHOOSE_LABEL.name())),comboBox);
        backButtonSetter();
    }
    
    public void algorithmTypeButtonSetter(){
        try {
            algorithmBox.getChildren().add(algorithmTypeLabel);
        }
        catch(IllegalArgumentException e){
            //do nothing
        }
        if (getLineNums() >= 2) {algorithmBox.getChildren().add(clusteringButton);}
        if (instNum == 2) {algorithmBox.getChildren().add(classificationButton);}
    }
    
    private int getLineNums(){
        String[] lines = textArea.getText().split("\n");
        return lines.length;
    }
    
    private void configButtonSetter(String algorithmType, String algorithmName){
        algorithmBox.getChildren().add(configButton);
        configButton.setOnAction(e ->{
            configDialog(algorithmType, algorithmName);
            displayButtonRemover();
        });
    }
    
    public void displayButtonSetter(){
        try{
            algorithmBox.getChildren().add(displayButton);
        } catch(IllegalArgumentException x){
            //do nothing
        }
    }
    
    public void displayButtonRemover(){
        algorithmBox.getChildren().remove(displayButton);
    }
    
    public void runButtonSetter(){
        try{
            algorithmBox.getChildren().add(runButton);
            runButton.setOnAction(e -> handleRunButton());
        } catch (IllegalArgumentException e){
            //do nothing
        }
    }
    
    public void runButtonRemover(){
        algorithmBox.getChildren().remove(runButton);
    }
    
    public void scrnshotButtonSetter(){
        scrnshotButton.setDisable(false);
    }
    
    public void scrnshotButtonRemover(){
        scrnshotButton.setDisable(true);
    }
    
    public void metaDataBoxSetter(){
        metaDataArea.setText(metaData);
        metaDataArea.setEditable(false);
        metaDataBox.getChildren().add(metaDataArea);
    }
    
    public void readOnlyClicker(int wantedState){
        if (!readOnly.isSelected() && wantedState == 1){readOnly.fire();}
        if (readOnly.isSelected() && wantedState == 0){readOnly.fire();}
    }
    
    private void configDialog(String algorithmType, String algorithmName){
        Stage config = new Stage();
        config.setTitle(applicationTemplate.manager.getPropertyValue(CONFIG_BUTTON_LABEL_DISPLAY.name()));
        VBox vbox = new VBox();
        TextField iterations = new TextField();
        TextField interval = new TextField();
        TextField clusterNum = new TextField();
        RadioButton continuous = new RadioButton(applicationTemplate.manager.getPropertyValue(CONTINUOUS_BUTTON_LABEL_DISPLAY.name()));
        int[] settings = new int[4];
        settings[3] = 0;
        if (configSettings.containsKey(algorithmName)){
            iterations.setText(configSettings.get(algorithmName)[0] + "");
            interval.setText(configSettings.get(algorithmName)[1] + "");
            clusterNum.setText(configSettings.get(algorithmName)[2] + "");
            if (configSettings.get(algorithmName)[3] == 1){
                settings[3] = 1;
                continuous.setSelected(true);
            }
        }
        Button ok = new Button(applicationTemplate.manager.getPropertyValue(OK.name()));
        if(algorithmType.equals(applicationTemplate.manager.getPropertyValue(CLUSTERING_BUTTON_LABEL_DISPLAY.name()))){
            vbox.getChildren().addAll(new Label(applicationTemplate.manager.getPropertyValue(CLUSTERNUM_LABEL_DISPLAY.name())),
                clusterNum);
        }
        vbox.getChildren().addAll(new Label(applicationTemplate.manager.getPropertyValue(ITERATIONS_LABEL_DISPLAY.name())),
                iterations,
                new Label(applicationTemplate.manager.getPropertyValue(INTERVALS_LABEL_DISPLAY.name())),
                interval,
                continuous,
                ok);
        Scene scene = new Scene(vbox);
        config.setScene(scene);
        config.show();
        
        continuous.setOnAction(e -> {
            int i = settings[3];
            if (i == 1){settings[3] = 0;}
            if (i == 0){settings[3] = 1;}
        });
        ok.setOnAction(e -> {
           try {
               int a = Integer.parseInt(iterations.getText());
               if (a>=Integer.MAX_VALUE) {settings[0] = 1;}
               if (a>=1){settings[0] = a;}
               
           }
           catch (NumberFormatException x){
               settings[0] = 1;
           }
           
           try {
               int b = Integer.parseInt(interval.getText());
               if (b>=Integer.MAX_VALUE) {settings[1] = 1;}
               if (b>=1){settings[1] = b;}
           }
           catch (NumberFormatException x){
               settings[1] = 1;
           }
           
           //interval shouldn't be larger than iterations
           if (settings[1] > settings[0]){
               settings[1] = settings[0];
           }

           try {
               int c = Integer.parseInt(clusterNum.getText());
               if (c == 3){settings[2] = 3;}
               else if(c>=4){settings[2] = 4;}
               else {settings[2] = 2;}
           }
           catch (NumberFormatException x){
               settings[2] = 2;
           }
           configSettings.put(algorithmName,settings);
           config.close();
           displayButtonSetter();
        });
    }
    
    public void setMetaData(HashMap dataLabels, HashMap names,Path dataFilePath){
        metaData = "";
        metaData += dataLabels.size();
        metaData += applicationTemplate.manager.getPropertyValue(INSTANCE_NUMBER_TEXT.name());
        metaData += names.size();
        metaData += applicationTemplate.manager.getPropertyValue(LABEL_NUMBER_TEXT.name());
        if (dataFilePath != null) {
            metaData += "\n" + dataFilePath + "\n";
        }
        else {
            metaData += "\n" + applicationTemplate.manager.getPropertyValue(NO_FILE_TEXT.name()) + "\n";
        }
        metaData += applicationTemplate.manager.getPropertyValue(LABEL_LIST_TEXT.name());
        metaData += "\n";
        for(Object a: names.keySet()){
            metaData += "- " + (String)a + "\n";
        }
        instNum = names.keySet().size();
    }
    
    public int[] getSettings(String algorithmName){
        return configSettings.get(algorithmName);
    }
       
    public void algorithmRemove(){
        algorithmBox.getChildren().clear();
        metaDataBox.getChildren().clear();
    }
    
    public void newText(boolean has){
        hasNewText = has;
    }
    
    public boolean hasNewText(){
        return hasNewText;
    }
    
    
    private void handleToggleButton(){
        if(readOnly.isSelected() && !((AppData)applicationTemplate.getDataComponent()).validate(getTextArea()
                        +((AppData)applicationTemplate.getDataComponent()).getAllLines())){
            readOnly.setSelected(false);
            return;
        }
        if (!readOnlyBool) {
            textArea.setDisable(true);
            readOnlyBool = true;
            algorithmTypeButtonSetter();
            metaDataBoxSetter();
           }
        else {
            textArea.setDisable(false);
            readOnlyBool = false;
            algorithmRemove();
        }
    }
    
    private void handleRunButton(){
        scrnshotButton.setDisable(true);
        ((AppData)applicationTemplate.getDataComponent()).notifyAlgorithm();
        scrnshotButton.setDisable(false);
    }
    
    private void layout() {
        leftSideBox = new VBox();
        leftSideBox.setPrefSize(400, 400);
        
        // input setup
        textBox = new VBox();
        textBox.setPadding(new Insets(11,12,13,14));
        textArea = new TextArea();
        textArea.setPrefRowCount(10);
        textArea.setPrefWidth(300);
        displayButton = new Button(applicationTemplate.manager.getPropertyValue(BUTTON_LABEL_DISPLAY.name()));
        readOnly = new RadioButton(applicationTemplate.manager.getPropertyValue(READ_ONLY_LABEL.name()));
        textBox.setSpacing(5);
        leftSideBox.getChildren().add(textBox);
        
        //metaData Node Setup
        metaDataArea = new TextArea();
        metaDataArea.setPrefWidth(300);
        metaDataArea.setWrapText(true);
        metaDataBox = new VBox();
        metaDataBox.setPadding(new Insets(11,12,13,14));
        leftSideBox.getChildren().add(metaDataBox);
        
        //choosing algorithm type setup
        algorithmBox = new VBox();
        algorithmBox.setPadding(new Insets(11,12,13,14));
        algorithmBox.setSpacing(5);
        clusteringButton = new Button(applicationTemplate.manager.getPropertyValue(CLUSTERING_BUTTON_LABEL_DISPLAY.name()));
        classificationButton = new Button(applicationTemplate.manager.getPropertyValue(CLASSIFICATION_BUTTON_LABEL_DISPLAY.name()));
        configButton = new Button(applicationTemplate.manager.getPropertyValue(CONFIG_BUTTON_LABEL_DISPLAY.name()));
        runButton = setToolbarButton(runiconPath, applicationTemplate.manager.getPropertyValue(RUN_TOOLTIP.name()), false);
        backButton = new Button(applicationTemplate.manager.getPropertyValue(BACK_BUTTON_LABEL_DISPLAY.name()));
        leftSideBox.getChildren().add(algorithmBox);
        
        // LineChart setup
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setForceZeroInRange(false);
        yAxis.setForceZeroInRange(false);
        chart = new LineChart<> (xAxis,yAxis);
        chart.setAnimated(false);
        
        workspace = new HBox();
        workspace.getChildren().add(leftSideBox);
        workspace.getChildren().add(chart);
        appPane.getChildren().add(workspace);
        
        doneRunning = false;
        algorithmTypeLabel = new Label(applicationTemplate.manager.getPropertyValue(ALGORITHM_TYPE_LABEL.name()));
    }

    private synchronized void setWorkspaceActions() {
        applicationTemplate.setDataComponent(new AppData(applicationTemplate)); 
        displayButton.setOnAction(e -> {
            doneRunning = false;
            chart.getData().clear();
            ((AppData)applicationTemplate.getDataComponent()).clear();
            String data = getTextArea() + ((AppData)applicationTemplate.getDataComponent()).getAllLines();
            try {
                ((AppData)applicationTemplate.getDataComponent()).setAlgorithm(data);
            } catch (IOException ex) {
                applicationTemplate.getDialog(Dialog.DialogType.ERROR).show(applicationTemplate.manager.getPropertyValue(ERROR_LABEL.name()),
                    applicationTemplate.manager.getPropertyValue(EMPTY_ERROR_MESSAGE.name()));
            }
            if (!((AppData)applicationTemplate.getDataComponent()).isContinuous()){
                runButtonSetter();
            }
            ((AppData)applicationTemplate.getDataComponent()).runAlgorithm();
            scrnshotButton.setDisable(false);  
        });
        readOnly.setOnAction(e -> handleToggleButton());
        textArea.setOnKeyReleased(e -> {
            if(!textArea.getText().isEmpty()){
                newButton.setDisable(false);
                saveButton.setDisable(false);
            }
            else {
                newButton.setDisable(true);
                saveButton.setDisable(true);
            }
            hasNewText = true;
            String[] textLines = textArea.getText().split("\n");
            int pos = textArea.getCaretPosition();
            for  (int i = textLines.length;i < 10;i++){
                String nextLine = ((AppData)(applicationTemplate.getDataComponent())).getNextLine();
                if (nextLine != null){
                    nextLine = "\n" + nextLine;
                    textArea.setText(textArea.getText() + nextLine);
                }
            }
            if (textLines.length > 10){
                String a = textLines[0];
                for(int i = 1; i < 10; i++){
                    a += ("\n" + textLines[i]);
                }
                if (pos < textArea.getText().length()){
                    ((AppData)(applicationTemplate.getDataComponent())).takeBackLine(textLines[10]);
                }
                textArea.setText(a);
            }   
            textArea.positionCaret(pos);
        });
        clusteringButton.setOnAction(e-> algorithmButtonSetter(applicationTemplate.manager.getPropertyValue(CLUSTERING_BUTTON_LABEL_DISPLAY.name()),
                applicationTemplate.manager.getPropertyValue(CLUSTERING_ALGORITHM_NAMES.name())));
        classificationButton.setOnAction(e-> algorithmButtonSetter(applicationTemplate.manager.getPropertyValue(CLASSIFICATION_BUTTON_LABEL_DISPLAY.name()),
                applicationTemplate.manager.getPropertyValue(CLASSIFICATION_ALGORITHM_NAMES.name())));
    }

    public void dataChanged(Map labels, Map points) {
        chart.getData().clear();
        ((AppData)(applicationTemplate.getDataComponent())).loadData(labels,points);
        ((AppData)(applicationTemplate.getDataComponent())).displayData();
    }
    
    public void setDoneRunning(boolean run){doneRunning = run;}
    
    public boolean getDoneRunning(){return doneRunning;}
}
