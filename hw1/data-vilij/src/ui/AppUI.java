package ui;

import actions.AppActions;
import dataprocessors.AppData;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
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
import javafx.geometry.Insets;
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
    private ScatterChart<Number, Number> chart;          // the chart where data will be displayed
    private Button                       displayButton;  // workspace button to display data on the chart
    private TextArea                     textArea;       // text area for new data input
    private boolean                      hasNewText;     // whether or not the text area has any new data since last display
    private String                       scrnshoticonPath;
    
    public ScatterChart<Number, Number> getChart() { return chart; }

    public AppUI(Stage primaryStage, ApplicationTemplate applicationTemplate) {
        super(primaryStage, applicationTemplate);
        this.applicationTemplate = applicationTemplate;
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
    
    public String getTextArea(){
        return textArea.getText();
    }

    private void layout() {
        // input setup
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(11,12,13,14));
        textArea = new TextArea();
        textArea.setPrefRowCount(5);
        textArea.setPrefWidth(300);
        displayButton = new Button(applicationTemplate.manager.getPropertyValue(BUTTON_LABEL_DISPLAY.name()));
        vbox.getChildren().addAll(new Label(applicationTemplate.manager.getPropertyValue(TITLE_LABEL.name())), textArea, displayButton);
        
        // ScatterChart setup
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        chart = new ScatterChart<> (xAxis,yAxis);
        
        workspace = new HBox();
        workspace.getChildren().add(vbox);
        workspace.getChildren().add(chart);
        appPane.getChildren().add(workspace);
    }

    private void setWorkspaceActions() {
        applicationTemplate.setDataComponent(new AppData(applicationTemplate)); 
        displayButton.setOnAction(e -> {
            try {
                if (hasNewText){
                    chart.getData().clear();
                    ((AppData)applicationTemplate.getDataComponent()).clear();
                    ((AppData)applicationTemplate.getDataComponent()).loadData(textArea.getText());
                    ((AppData)applicationTemplate.getDataComponent()).displayData();
                }
                hasNewText = false;
            } catch (Exception ex) {
                applicationTemplate.getDialog(Dialog.DialogType.ERROR).show(applicationTemplate.manager.getPropertyValue(ERROR_LABEL.name()),
                        applicationTemplate.manager.getPropertyValue(FORMAT_ERROR_MESSAGE.name()));
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
        });
    }
}
