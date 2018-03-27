package ui;

import actions.AppActions;
import dataprocessors.AppData;
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
import static java.io.File.separator;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Insets;
import javafx.scene.chart.LineChart;
import javafx.scene.control.RadioButton;
import vilij.components.Dialog;

/**
 * This is the application's user interface implementation.
 *
 * @author Ritwik Banerjee
 */
public final class AppUI extends UITemplate {

    /** The application to which this class of actions belongs. */
    ApplicationTemplate applicationTemplate;

    @SuppressWarnings("FieldCanBeLocal")
    private Button                       scrnshotButton; // toolbar button to take a screenshot of the data
    private RadioButton                  readOnly;       // radio button to toggle read-only
    private LineChart<Number, Number> chart;          // the chart where data will be displayed
    private Button                       displayButton;  // workspace button to display data on the chart
    private TextArea                     textArea;       // text area for new data input
    private boolean                      hasNewText;     // whether or not the text area has any new data since last display
    private String                       scrnshoticonPath;
    private boolean                      readOnlyBool;
    
    public LineChart<Number, Number> getChart() { return chart; }

    public AppUI(Stage primaryStage, ApplicationTemplate applicationTemplate) {
        super(primaryStage, applicationTemplate);
        primaryScene.getStylesheets().add(getClass().getResource(applicationTemplate.manager.getPropertyValue(CSS_STYLE_SHEET_PATH.name())).toExternalForm());
        this.applicationTemplate = applicationTemplate;
        readOnlyBool = false;
    }

    @Override
    protected void setResourcePaths(ApplicationTemplate applicationTemplate) {
        super.setResourcePaths(applicationTemplate);
        String iconsPath = separator + String.join(separator,
                                             applicationTemplate.manager.getPropertyValue(GUI_RESOURCE_PATH.name()),
                                             applicationTemplate.manager.getPropertyValue(ICONS_RESOURCE_PATH.name()));
        scrnshoticonPath = String.join(separator, iconsPath, applicationTemplate.manager.getPropertyValue(SCREENSHOT_ICON.name()));
    }

    @Override
    protected void setToolBar(ApplicationTemplate applicationTemplate) {
        super.setToolBar(applicationTemplate);
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
        scrnshotButton.setDisable(false);
    }
    
    public void newClear(){newButton.setDisable(true);}
    public void saveClear(){saveButton.setDisable(true);}
    public void newOn(){newButton.setDisable(false);}
    public void saveOn(){saveButton.setDisable(false);}
    
    public String getTextArea(){
        return textArea.getText();
    }
    
    public void setTextArea(String data){
        textArea.clear();
        textArea.setText(data);
        hasNewText = true;
    }
    
    private void layout() {
        // input setup
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(11,12,13,14));
        textArea = new TextArea();
        textArea.setPrefRowCount(10);
        textArea.setPrefWidth(300);
        displayButton = new Button(applicationTemplate.manager.getPropertyValue(BUTTON_LABEL_DISPLAY.name()));
        readOnly = new RadioButton(applicationTemplate.manager.getPropertyValue(READ_ONLY_LABEL.name()));
        vbox.getChildren().addAll(new Label(applicationTemplate.manager.getPropertyValue(TITLE_LABEL.name())), textArea, readOnly, displayButton);
        
        // LineChart setup
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        chart = new LineChart<> (xAxis,yAxis);
        
        workspace = new HBox();
        workspace.getChildren().add(vbox);
        workspace.getChildren().add(chart);
        appPane.getChildren().add(workspace);
    }

    private void setWorkspaceActions() {
        applicationTemplate.setDataComponent(new AppData(applicationTemplate)); 
        displayButton.setOnAction(e -> {
            try {
                String data = textArea.getText() + ((AppData)applicationTemplate.getDataComponent()).getAllLines();
                if (((AppActions)applicationTemplate.getActionComponent()).validate(data)){
                    chart.getData().clear();
                    ((AppData)applicationTemplate.getDataComponent()).clear();
                    try {
                        ((AppData)applicationTemplate.getDataComponent()).loadData(data);
                    } catch (Exception ex) {
                        Logger.getLogger(AppUI.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    ((AppData)applicationTemplate.getDataComponent()).displayData();
                    scrnshotButton.setDisable(false);
                }
                hasNewText = false;
            } catch (Exception ex) {
                applicationTemplate.getDialog(Dialog.DialogType.ERROR).show(applicationTemplate.manager.getPropertyValue(ERROR_LABEL.name()),
                        applicationTemplate.manager.getPropertyValue(FORMAT_ERROR_MESSAGE.name()));
            }
        });
        readOnly.setOnAction(e -> {
           if (!readOnlyBool) {
               textArea.setDisable(true);
               readOnlyBool = true;
           }
           else {
               textArea.setDisable(false);
               readOnlyBool = false;
           }
        });
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
            String[] textLines = textArea.getText().split(System.lineSeparator());
            int pos = textArea.getCaretPosition();
            for  (int i = textLines.length;i < 10;i++){
                String nextLine = ((AppData)(applicationTemplate.getDataComponent())).getNextLine();
                if (nextLine != null){
                    nextLine = System.lineSeparator() + nextLine;
                    textArea.setText(textArea.getText() + nextLine);
                }
            }
            if (textLines.length > 10){
                String a = textLines[0];
                for(int i = 1; i < 10; i++){
                    a += (System.lineSeparator() + textLines[i]);
                }
                if (pos < textArea.getText().length()){
                    ((AppData)(applicationTemplate.getDataComponent())).takeBackLine(textLines[10]);
                }
                textArea.setText(a);
            }   
            textArea.positionCaret(pos);
        });
    }
}
