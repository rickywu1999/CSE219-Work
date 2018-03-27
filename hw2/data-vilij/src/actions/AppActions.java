package actions;

import dataprocessors.AppData;
import java.io.File;
import vilij.components.ActionComponent;
import vilij.templates.ApplicationTemplate;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Scanner;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import vilij.components.ConfirmationDialog;
import static settings.AppPropertyTypes.*;
import ui.AppUI;
import vilij.components.Dialog;

/**
 * This is the concrete implementation of the action handlers required by the application.
 *
 * @author Ritwik Banerjee
 */
public final class AppActions implements ActionComponent {

    /** The application to which this class of actions belongs. */
    private final ApplicationTemplate applicationTemplate;

    /** Path to the data file currently active. */
    Path dataFilePath;

    public AppActions(ApplicationTemplate applicationTemplate) {
        this.applicationTemplate = applicationTemplate;
        dataFilePath = null;
    }

    @Override
    public void handleNewRequest() {
        try {
            if (promptToSave()){
                Dialog confirm = applicationTemplate.getDialog(Dialog.DialogType.CONFIRMATION);
                if (((ConfirmationDialog)confirm).getSelectedOption().equals(ConfirmationDialog.Option.YES)){handleSaveRequest();}
                applicationTemplate.getUIComponent().clear();
                applicationTemplate.getDataComponent().clear();
                dataFilePath = null;
            }
        } catch (IOException ex) {
            ((AppUI)applicationTemplate.getUIComponent()).saveOn();
            ((AppUI)applicationTemplate.getUIComponent()).newOn();
        }
    }

    @Override
    public void handleSaveRequest() {
        String info = ((AppUI)applicationTemplate.getUIComponent()).getTextArea();
        info += ((AppData)(applicationTemplate.getDataComponent())).getAllLines();
        if (validate(info)){
            if (dataFilePath == null){
                FileChooser fileChooser = new FileChooser();
                fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter(applicationTemplate.manager.getPropertyValue(DATA_FILE_EXT_DESC.name()),
                    "*" + applicationTemplate.manager.getPropertyValue(DATA_FILE_EXT.name())));
                fileChooser.setInitialDirectory(new File(applicationTemplate.manager.getPropertyValue(DATA_RESOURCE_PATH.name())));
                try {
                    File file = fileChooser.showSaveDialog(new Stage());
                    if (file == null){return;}
                    dataFilePath = file.toPath();
                }catch (Exception x){
                    applicationTemplate.getDialog(Dialog.DialogType.ERROR).show(applicationTemplate.manager.getPropertyValue(ERROR_LABEL.name()),
                        applicationTemplate.manager.getPropertyValue(FORMAT_ERROR_MESSAGE.name()));
                    ((AppUI)applicationTemplate.getUIComponent()).saveOn();
                };
            }
            try { 
                applicationTemplate.getDataComponent().saveData(dataFilePath);
                if (!(dataFilePath == null)){
                    ((AppUI)applicationTemplate.getUIComponent()).saveClear();
                }
            }
            catch (Exception x) {((AppUI)applicationTemplate.getUIComponent()).saveOn();}//validate already has error warnings
        }
    }

    @Override
    public void handleLoadRequest() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter(applicationTemplate.manager.getPropertyValue(DATA_FILE_EXT_DESC.name()),
            "*" + applicationTemplate.manager.getPropertyValue(DATA_FILE_EXT.name())));
        fileChooser.setInitialDirectory(new File(applicationTemplate.manager.getPropertyValue(DATA_RESOURCE_PATH.name())));
        try {
            File file = fileChooser.showOpenDialog(new Stage());
            if (file == null){return;}
            dataFilePath = file.toPath();
        }catch (Exception x){
        //in case they click ESC, validate already handles data errors 
        };
        try{
            applicationTemplate.getDataComponent().loadData(dataFilePath);
            ((AppUI)applicationTemplate.getUIComponent()).saveClear();
        }
        catch (Exception x){
            //press cancel            
        }
    }

    @Override
    public void handleExitRequest() {
        applicationTemplate.getUIComponent().getPrimaryWindow().close();
    }

    @Override
    public void handlePrintRequest() {
        // TODO: NOT A PART OF HW 1
    }

    public void handleScreenshotRequest() throws IOException {
        WritableImage snapshot = ((AppUI)applicationTemplate.getUIComponent()).getChart().snapshot(null, null);
        Path picPath;
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter(applicationTemplate.manager.getPropertyValue(PICTURE_FILE_EXT_DESC.name()),
                "*" + applicationTemplate.manager.getPropertyValue(PICTURE_FILE_EXT.name())));
        fileChooser.setInitialDirectory(new File(applicationTemplate.manager.getPropertyValue(DATA_RESOURCE_PATH.name())));
        try {
            File file = fileChooser.showSaveDialog(new Stage());
            if (file == null){return;}
            picPath = file.toPath();
            ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), "png", new File(picPath.toString()));
        } catch (Exception x) {
            applicationTemplate.getDialog(Dialog.DialogType.ERROR).show(applicationTemplate.manager.getPropertyValue(ERROR_LABEL.name()),
                applicationTemplate.manager.getPropertyValue(FORMAT_ERROR_MESSAGE.name()));
        };
    }

    /**
     * This helper method verifies that the user really wants to save their unsaved work, which they might not want to
     * do. The user will be presented with three options:
     * <ol>
     * <li><code>yes</code>, indicating that the user wants to save the work and continue with the action,</li>
     * <li><code>no</code>, indicating that the user wants to continue with the action without saving the work, and</li>
     * <li><code>cancel</code>, to indicate that the user does not want to continue with the action, but also does not
     * want to save the work at this point.</li>
     * </ol>
     *
     * @return <code>false</code> if the user presses the <i>cancel</i>, and <code>true</code> otherwise.
     */
    private boolean promptToSave() throws IOException {
        Dialog confirm = applicationTemplate.getDialog(Dialog.DialogType.CONFIRMATION);
        confirm.show(applicationTemplate.manager.getPropertyValue(SAVE_UNSAVED_WORK_TITLE.name()), 
                applicationTemplate.manager.getPropertyValue(SAVE_UNSAVED_WORK.name()));
        try {
            return !(((ConfirmationDialog)confirm).getSelectedOption().equals(ConfirmationDialog.Option.CANCEL));
        } catch (Exception x){
            return false;
        }
    }
    public boolean validate(String info){
        Scanner sc = new Scanner(info);
        String errorMessage = "";
        int lineNum = 0;
        HashMap dataLabels = new HashMap<>();
        boolean remindAboutFormat = false;
        int errorCounts = 0;
        while (sc.hasNextLine()){
            lineNum++;
            String[] parts = sc.nextLine().split("\t");
            //missing all three parts of a line
            if (parts.length != 3){
                if (errorCounts < 5){
                    errorMessage += (lineNum + applicationTemplate.manager.getPropertyValue(COLON.name())
                            + applicationTemplate.manager.getPropertyValue(PARSEFORMAT_ERROR_MESSAGE.name()) 
                            + System.lineSeparator());
                }
                remindAboutFormat = true;
                errorCounts++;
            }
            else {
                //instance does not start with @
                if (parts[0].substring(0,1).equals(applicationTemplate.manager.getPropertyValue(AT_SIGN.name()))){
                    if (errorCounts < 5){
                        errorMessage += (lineNum + applicationTemplate.manager.getPropertyValue(COLON.name())
                                + applicationTemplate.manager.getPropertyValue(DATALABEL_ERROR_MESSAGE.name())
                                + System.lineSeparator());
                    }
                    errorCounts++;
                }
                //instance name is already taken
                else if (dataLabels.containsKey(parts[0])){
                    if (errorCounts < 5){
                        errorMessage += (lineNum + applicationTemplate.manager.getPropertyValue(COLON.name())
                                + parts[0]
                                + applicationTemplate.manager.getPropertyValue(DATALABEL_ERROR_MESSAGE.name())
                                + System.lineSeparator());
                    }
                    errorCounts++;
                }
                //valid first part is put into a HashMap
                else
                    dataLabels.put(parts[0], parts[2]);
                //data is missing x or y coordinate
                String[] dataPoint = parts[2].split(applicationTemplate.manager.getPropertyValue(COMMA.name()));
                if (dataPoint.length != 2){
                    if (errorCounts < 5){
                        errorMessage += (lineNum + applicationTemplate.manager.getPropertyValue(COLON.name())
                                + applicationTemplate.manager.getPropertyValue(MISSING_DATA_MESSAGE.name())
                                + System.lineSeparator());
                    }
                    errorCounts++;
                }
                //data points are not numbers
                else{
                    try {
                        Integer.parseInt(dataPoint[0]);
                        Integer.parseInt(dataPoint[1]);
                    }
                    catch (Exception x){
                        if (errorCounts < 5){
                            errorMessage += (lineNum + applicationTemplate.manager.getPropertyValue(COLON.name())
                                    + applicationTemplate.manager.getPropertyValue(INVALID_DATA_MESSAGE.name())
                                    + System.lineSeparator());
                        }
                        errorCounts++;
                    }
                }
            }
        }
        if (errorCounts == 0){return true;}
        else {dataFilePath = null;}
        if (errorCounts >= 5)
            errorMessage += (System.lineSeparator()
                    + (errorCounts-5) 
                    + applicationTemplate.manager.getPropertyValue(REMAINING_ERRORS.name())
                    + System.lineSeparator());
        if (remindAboutFormat)
            errorMessage = applicationTemplate.manager.getPropertyValue(FORMAT_REMINDER.name()) 
                    + System.lineSeparator()
                    + errorMessage;
        applicationTemplate.getDialog(Dialog.DialogType.ERROR).show(applicationTemplate.manager.getPropertyValue(ERROR_LABEL.name()), errorMessage);
        return false;
    }
}
