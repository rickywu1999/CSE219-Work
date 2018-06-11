package actions;

import dataprocessors.AppData;
import java.io.File;
import vilij.components.ActionComponent;
import vilij.templates.ApplicationTemplate;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
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
    private Path dataFilePath;

    public AppActions(ApplicationTemplate applicationTemplate) {
        this.applicationTemplate = applicationTemplate;
        dataFilePath = null;
    }

    @Override
    public void handleNewRequest() {
        try {
            if (((AppUI)applicationTemplate.getUIComponent()).getTextArea().isEmpty()){
                ((AppUI)applicationTemplate.getUIComponent()).textBoxSetter();
            }
            else if (!((AppUI)applicationTemplate.getUIComponent()).hasNewText()){
                applicationTemplate.getUIComponent().clear();
                applicationTemplate.getDataComponent().clear();
                ((AppData)applicationTemplate.getDataComponent()).clearData();
                dataFilePath = null;
                ((AppUI)applicationTemplate.getUIComponent()).algorithmRemove();
                ((AppUI)applicationTemplate.getUIComponent()).readOnlyButtonSetter();
                ((AppUI)applicationTemplate.getUIComponent()).readOnlyClicker(0);
                ((AppUI)applicationTemplate.getUIComponent()).scrnshotButtonRemover();
            }
            else if (promptToSave()){
                Dialog confirm = applicationTemplate.getDialog(Dialog.DialogType.CONFIRMATION);
                if (((ConfirmationDialog)confirm).getSelectedOption().equals(ConfirmationDialog.Option.YES)){handleSaveRequest();}
                applicationTemplate.getUIComponent().clear();
                applicationTemplate.getDataComponent().clear();
                ((AppData)applicationTemplate.getDataComponent()).clearData();
                dataFilePath = null;
                ((AppUI)applicationTemplate.getUIComponent()).algorithmRemove();
            }
        } catch (IOException ex) {
            ((AppUI)applicationTemplate.getUIComponent()).newOn();
        }
    }

    @Override
    public void handleSaveRequest() {
        String info = ((AppUI)applicationTemplate.getUIComponent()).getTextArea();
        info += ((AppData)(applicationTemplate.getDataComponent())).getAllLines();
        if (((AppData)applicationTemplate.getDataComponent()).validate(info)){
            if (dataFilePath == null){
                FileChooser fileChooser = new FileChooser();
                fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter(applicationTemplate.manager.getPropertyValue(DATA_FILE_EXT_DESC.name()),
                    "*" + applicationTemplate.manager.getPropertyValue(DATA_FILE_EXT.name())));
                URL dataDirURL  = getClass().getResource("/" + applicationTemplate.manager.getPropertyValue(DATA_RESOURCE_PATH.name()));
                //fileChooser.setInitialDirectory(new File(dataDirURL.getFile()));
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
                ((AppUI)applicationTemplate.getUIComponent()).newText(false);
                if (!(dataFilePath == null)){
                    ((AppUI)applicationTemplate.getUIComponent()).saveClear();
                    ((AppUI)applicationTemplate.getUIComponent()).newText(true);
                }
            }
            catch (Exception x) {((AppUI)applicationTemplate.getUIComponent()).saveOn();}//validate already has error warnings
        }
    }

    @Override
    public void handleLoadRequest() {
        try { 
            ((AppUI)applicationTemplate.getUIComponent()).textBoxSetter();
        } 
        catch (Exception x) {/*this just tries to set textBox if there wasnt already one*/}
        try {
            ((AppUI)applicationTemplate.getUIComponent()).readOnlyButtonRemover();
            applicationTemplate.getDataComponent().clear();
        }
        catch (Exception x) {/*removes readOnly if it wasn't removed already*/}
        ((AppUI)applicationTemplate.getUIComponent()).newOn();
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter(applicationTemplate.manager.getPropertyValue(DATA_FILE_EXT_DESC.name()),
            "*" + applicationTemplate.manager.getPropertyValue(DATA_FILE_EXT.name())));
        URL dataDirURL  = getClass().getResource("/" + applicationTemplate.manager.getPropertyValue(DATA_RESOURCE_PATH.name()));
        //fileChooser.setInitialDirectory(new File(dataDirURL.getFile()));
        try {
            File file = fileChooser.showOpenDialog(new Stage());
            if (file == null){
                ((AppUI)applicationTemplate.getUIComponent()).textBoxRemover();;
            }
            dataFilePath = file.toPath();
        }catch (Exception x){
        //in case they click ESC, validate already handles data errors 
        };
        try{
            ((AppUI)applicationTemplate.getUIComponent()).newText(false);
            ((AppUI)applicationTemplate.getUIComponent()).readOnlyClicker(0);
            ((AppUI)applicationTemplate.getUIComponent()).algorithmRemove();
            applicationTemplate.getDataComponent().loadData(dataFilePath);
            ((AppUI)applicationTemplate.getUIComponent()).saveClear();
            ((AppUI)applicationTemplate.getUIComponent()).metaDataBoxSetter();
            ((AppUI)applicationTemplate.getUIComponent()).algorithmTypeButtonSetter();
            ((AppUI)applicationTemplate.getUIComponent()).readOnlyClicker(1);
            ((AppUI)applicationTemplate.getUIComponent()).clear();
            ((AppUI)applicationTemplate.getUIComponent()).newText(false);
        }
        catch (Exception x){
            //press cancel            
        }
    }

    @Override
    public void handleExitRequest() {
        try {
            if (!((AppUI)applicationTemplate.getUIComponent()).getDoneRunning()){
                Dialog confirm = applicationTemplate.getDialog(Dialog.DialogType.CONFIRMATION);
                confirm.show(applicationTemplate.manager.getPropertyValue(EXIT_WHILE_RUNNING_TITLE.name()),applicationTemplate.manager.getPropertyValue(EXIT_WHILE_RUNNING_WARNING.name()));
                
                if (((ConfirmationDialog)confirm).getSelectedOption().equals(ConfirmationDialog.Option.NO)){return;}
                if (((ConfirmationDialog)confirm).getSelectedOption().equals(ConfirmationDialog.Option.CANCEL)){return;}
            }
            if (((AppUI)applicationTemplate.getUIComponent()).hasNewText()){
                promptToSave();
                Dialog confirm = applicationTemplate.getDialog(Dialog.DialogType.CONFIRMATION);
                if (((ConfirmationDialog)confirm).getSelectedOption().equals(ConfirmationDialog.Option.YES)){handleSaveRequest();}
                if (((ConfirmationDialog)confirm).getSelectedOption().equals(ConfirmationDialog.Option.CANCEL)){return;}
            }
            applicationTemplate.getUIComponent().getPrimaryWindow().close();
        } catch (IOException ex) {
            ((AppUI)applicationTemplate.getUIComponent()).saveOn();
            ((AppUI)applicationTemplate.getUIComponent()).newOn();
        }
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
        URL dataDirURL  = getClass().getResource("/" + applicationTemplate.manager.getPropertyValue(DATA_RESOURCE_PATH.name()));
        //fileChooser.setInitialDirectory(new File(dataDirURL.getFile()));
        try {
            File file = fileChooser.showSaveDialog(new Stage());
            if (file == null){return;}
            picPath = file.toPath();
            ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), "png", new File(picPath.toString()));
        } catch (Exception x) {
            applicationTemplate.getDialog(Dialog.DialogType.ERROR).show(applicationTemplate.manager.getPropertyValue(ERROR_LABEL.name()),
                applicationTemplate.manager.getPropertyValue(FORMAT_ERROR_MESSAGE.name()));
        }
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
    
    public void setDataFilePath(Path p){
        dataFilePath = p;
    }
    
    public Path getDataFilePath(){
        return dataFilePath;
    }
}
