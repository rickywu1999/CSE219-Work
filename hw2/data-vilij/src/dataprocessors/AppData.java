package dataprocessors;

import actions.AppActions;
import java.io.FileWriter;
import java.io.IOException;
import ui.AppUI;
import vilij.components.DataComponent;
import vilij.templates.ApplicationTemplate;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Scanner;
import vilij.components.Dialog;
import static settings.AppPropertyTypes.*;

/**
 * This is the concrete application-specific implementation of the data component defined by the Vilij framework.
 *
 * @author Ritwik Banerjee
 * @see DataComponent
 */
public class AppData implements DataComponent {

    private TSDProcessor        processor;
    private ApplicationTemplate applicationTemplate;
    private HashMap<Integer,String> lines,storage;
    private int position,storagePos;

    public AppData(ApplicationTemplate applicationTemplate) {
        this.processor = new TSDProcessor();
        this.applicationTemplate = applicationTemplate;
        lines = new HashMap();
        storage = new HashMap();
        storagePos = 0;
    }

    @Override
    public void loadData(Path dataFilePath) {
        String all = "";
        String firstTen = "";
        int lineNum = 0;
        try {
            Scanner sc = new Scanner(dataFilePath);
            while (sc.hasNextLine()){
                String add = "";
                if (lineNum > 0) { add += System.lineSeparator(); }
                String line = sc.nextLine();
                add += line;
                if (lineNum < 10) { firstTen += add; }
                all += add;
                lines.put(lineNum, line);
                lineNum++;
            }
        } catch (IOException ex) {
            applicationTemplate.getDialog(Dialog.DialogType.ERROR).show(applicationTemplate.manager.getPropertyValue(ERROR_LABEL.name()),
                    applicationTemplate.manager.getPropertyValue(RESOURCE_SUBDIR_NOT_FOUND.name()));  
        }
        position = lineNum;
        if (position >= 10) {
            position = 9;
        }
        if (((AppActions)applicationTemplate.getActionComponent()).validate(all)) {
            ((AppUI)applicationTemplate.getUIComponent()).setTextArea(firstTen);
            if (lineNum >= 10){
                applicationTemplate.getDialog(Dialog.DialogType.ERROR).show(applicationTemplate.manager.getPropertyValue(LONG_LOAD_FILE_TITLE.name()), 
                        applicationTemplate.manager.getPropertyValue(LONG_LOAD_FILE.name()) + lineNum); 
            }
        }
    }

    public void loadData(String dataString) throws Exception{
        processor.processString(dataString);
    }

    @Override
    public void saveData(Path dataFilePath) {
        FileWriter fileWriter;
        String info = ((AppUI)applicationTemplate.getUIComponent()).getTextArea();
        info += getAllLines();
        try {
            fileWriter = new FileWriter(dataFilePath.toString());
            fileWriter.write(info);
            fileWriter.close();
        }
        catch (Exception x){
            ((AppUI)applicationTemplate.getUIComponent()).saveOn();
        }
    }
    
    public String getNextLine(){
        if (storagePos != 0){
            storagePos--;
            String ret = storage.get(storagePos);
            return ret;
        }
        position++;
        if (!lines.containsKey(position)){
            return null;
        }
        return lines.get(position);
    }
    
    public void takeBackLine(String line){
        storage.put(storagePos, line);
        storagePos++;
    }
    
    public String getAllLines(){
        String ans = "";
        for(int i = storagePos-1; i >= 0; i--){
            ans += ("/n" + storage.get(i));
        }
        for(int i = position+1;lines.containsKey(i);i++){
            ans += ("/n" + lines.get(i));
        }
        return ans;
    }

    @Override
    public void clear() {
        processor.clear();
    }

    public void displayData() {
        processor.toChartData(((AppUI) applicationTemplate.getUIComponent()).getChart());
    }
}
