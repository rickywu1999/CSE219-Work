package actions;

import java.io.File;
import java.io.FileWriter;
import vilij.components.ActionComponent;
import vilij.templates.ApplicationTemplate;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
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
    }

    @Override
    public void handleNewRequest() {
        try {
            if (promptToSave()){
                Dialog confirm = applicationTemplate.getDialog(Dialog.DialogType.CONFIRMATION);
                if (((ConfirmationDialog)confirm).getSelectedOption().equals(ConfirmationDialog.Option.NO)){
                    applicationTemplate.getUIComponent().clear();
                    applicationTemplate.getDataComponent().clear();
                }
                else {
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.setTitle(applicationTemplate.manager.getPropertyValue(SAVE_UNSAVED_WORK_TITLE.name()));
                    fileChooser.getExtensionFilters().add(
                            new FileChooser.ExtensionFilter(applicationTemplate.manager.getPropertyValue(DATA_FILE_EXT_DESC.name()),
                            "*" + applicationTemplate.manager.getPropertyValue(DATA_FILE_EXT.name())));
                    fileChooser.setInitialDirectory(new File(applicationTemplate.manager.getPropertyValue(DATA_RESOURCE_PATH.name())));
                    try {
                        File file = fileChooser.showSaveDialog(new Stage());
                        FileWriter fileWriter = new FileWriter(file);
                        fileWriter.write(((AppUI)applicationTemplate.getUIComponent()).getTextArea());
                        fileWriter.close();
                        applicationTemplate.getUIComponent().clear();
                        applicationTemplate.getDataComponent().clear();
                    }catch (Exception x){
                        //in case they click ESC,
                    };
                }
            }
        } catch (IOException ex) {
            applicationTemplate.getDialog(Dialog.DialogType.ERROR).show(applicationTemplate.manager.getPropertyValue(ERROR_LABEL.name()),
                        applicationTemplate.manager.getPropertyValue(RESOURCE_SUBDIR_NOT_FOUND.name()));
        }
    }

    @Override
    public void handleSaveRequest() {
        // TODO: NOT A PART OF HW 1
    }

    @Override
    public void handleLoadRequest() {
        // TODO: NOT A PART OF HW 1
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
        // TODO: NOT A PART OF HW 1
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
        return !(((ConfirmationDialog)confirm).getSelectedOption().equals(ConfirmationDialog.Option.CANCEL));
    }
}
