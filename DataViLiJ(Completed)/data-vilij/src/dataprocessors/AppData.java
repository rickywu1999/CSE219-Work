package dataprocessors;

import actions.AppActions;
import algorithm.Algorithm;
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
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

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
    private int                 position,storagePos;
    private String              algorithmType,algorithmName;
    private Algorithm           algo;
    private DataSet             ds;

    public AppData(ApplicationTemplate applicationTemplate) {
        this.processor = new TSDProcessor();
        this.applicationTemplate = applicationTemplate;
        lines = new HashMap();
        storage = new HashMap();
        storagePos = 0;
    }

    @Override
    public void loadData(Path dataFilePath) {
        lines.clear();
        storage.clear();
        String all = "";
        String firstTen = "";
        int lineNum = 0;
        try {
            Scanner sc = new Scanner(dataFilePath);
            while (sc.hasNextLine()){
                String add = "";
                if (lineNum > 0) { add += "\n"; }
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
        if (validate(all)) {
            ((AppUI)applicationTemplate.getUIComponent()).setTextArea(firstTen);
        }
        try {
            ds = DataSet.fromTSDFile(dataFilePath);
        } catch (IOException ex) {
            //do nothing
        }
    }

    public void loadData(Map labels, Map points){
        processor.processData(labels, points, algorithmType);
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
        catch (IOException x){
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
            ans += ("\n" + storage.get(i));
        }
        for(int i = position+1;lines.containsKey(i);i++){
            ans += ("\n" + lines.get(i));
        }
        return ans;
    }

    @Override
    public void clear() {
        processor.clear();
    }
    
    public void clearData(){
        lines.clear();
        storage.clear();
    }
    

    public void displayData() {
        processor.toChartData(((AppUI) applicationTemplate.getUIComponent()).getChart(),algorithmType);
    }
    
    public void setNames(String algorithmType,String algorithmName){
        this.algorithmType = algorithmType;
        this.algorithmName = algorithmName;
    }
    
    public boolean isContinuous(){
        return algo.tocontinue();
    }
    
    public boolean setAlgorithm(String data) throws IOException {
        boolean contRun = true;
        int[] settings = ((AppUI)(applicationTemplate.getUIComponent())).getSettings(algorithmName);
        if (settings[3] == 0){contRun = false;}
        
        ds = DataSet.fromString(data);
        ds.addUi((AppUI)applicationTemplate.getUIComponent());

        /*
        if (algorithmType.equals(applicationTemplate.manager.getPropertyValue(CLASSIFICATION_BUTTON_LABEL_DISPLAY.name()))){
            algo = new RandomClassifier(ds,settings[0],settings[1],contRun,algorithmType);   
        }*/
        try {
            if (algorithmType.equals(applicationTemplate.manager.getPropertyValue(CLASSIFICATION_BUTTON_LABEL_DISPLAY.name()))){
                Class cl = Class.forName(applicationTemplate.manager.getPropertyValue(CLASSIFICATION_SOURCE_PATH.name()) + algorithmName);
                Constructor constructor = cl.getConstructors()[0];
                Object obj = constructor.newInstance(ds,settings[0],settings[1],contRun,algorithmType); 
                algo = (Algorithm)obj;          
            }
            if (algorithmType.equals(applicationTemplate.manager.getPropertyValue(CLUSTERING_BUTTON_LABEL_DISPLAY.name()))){
                Class cl = Class.forName(applicationTemplate.manager.getPropertyValue(CLUSTERING_SOURCE_PATH.name()) + algorithmName);
                Constructor constructor = cl.getConstructors()[0];
                Object obj = constructor.newInstance(ds,settings[0],settings[1],settings[2],contRun,algorithmType); 
                algo = (Algorithm)obj;          
            }
        }catch (ClassNotFoundException x){
            applicationTemplate.getDialog(Dialog.DialogType.ERROR).show(applicationTemplate.manager.getPropertyValue(ERROR_LABEL.name()),
                    applicationTemplate.manager.getPropertyValue(ALGORITHM_CLASS_EXIST_ERROR_MESSAGE.name())); 
            return false;
        } catch (InstantiationException ex) {
            applicationTemplate.getDialog(Dialog.DialogType.ERROR).show(applicationTemplate.manager.getPropertyValue(ERROR_LABEL.name()),
                    applicationTemplate.manager.getPropertyValue(INSTANTIATE_ERROR_MESSAGE.name())); 
            return false;
        } catch (IllegalAccessException ex) {
            applicationTemplate.getDialog(Dialog.DialogType.ERROR).show(applicationTemplate.manager.getPropertyValue(ERROR_LABEL.name()),
                    applicationTemplate.manager.getPropertyValue(ACCESS_ERROR_MESSAGE.name())); 
            return false;
        } catch (IllegalArgumentException | InvocationTargetException ex) {
            applicationTemplate.getDialog(Dialog.DialogType.ERROR).show(applicationTemplate.manager.getPropertyValue(ERROR_LABEL.name()),
                    ex.getMessage()); 
        }
        return true;
    }
    
    public synchronized void runAlgorithm(){
        Thread t = new Thread(algo);
        t.setDaemon(true);
        ((AppUI)applicationTemplate.getUIComponent()).displayButtonRemover();
        ((AppUI)applicationTemplate.getUIComponent()).backButtonDisable();
        t.start();
    }
    
    public void notifyAlgorithm(){
        synchronized(ds){
            ds.setGo(false);
            ds.notifyAll();
        }
    }
    
    public boolean validate(String info){
        if (info.equals("")) {
            applicationTemplate.getDialog(Dialog.DialogType.ERROR).show(
                    applicationTemplate.manager.getPropertyValue(ERROR_LABEL.name()), 
                    applicationTemplate.manager.getPropertyValue(EMPTY_ERROR_MESSAGE.name()));
            ((AppUI)applicationTemplate.getUIComponent()).textBoxRemover();
            return false;
        }
        Scanner sc = new Scanner(info);
        String errorMessage = "";
        int lineNum = 0;
        HashMap dataLabels = new HashMap<>();
        HashMap<String,String> names = new HashMap<>();
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
                            + "\n");
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
                                + "\n");
                    }
                    errorCounts++;
                }
                //instance name is already taken
                else if (dataLabels.containsKey(parts[0])){
                    if (errorCounts < 5){
                        errorMessage += (lineNum + applicationTemplate.manager.getPropertyValue(COLON.name())
                                + parts[0]
                                + applicationTemplate.manager.getPropertyValue(DATALABEL_ERROR_MESSAGE.name())
                                + "\n");
                    }
                    errorCounts++;
                }
                //valid first part is put into a HashMap
                else
                    if (!names.containsKey(parts[1])) {names.put(parts[1],parts[0]);}
                    dataLabels.put(parts[0], parts[2]);
                //data is missing x or y coordinate
                String[] dataPoint = parts[2].split(applicationTemplate.manager.getPropertyValue(COMMA.name()));
                if (dataPoint.length != 2){
                    if (errorCounts < 5){
                        errorMessage += (lineNum + applicationTemplate.manager.getPropertyValue(COLON.name())
                                + applicationTemplate.manager.getPropertyValue(MISSING_DATA_MESSAGE.name())
                                + "\n");
                    }
                    errorCounts++;
                }
                //data points are not numbers
                else{
                    try {
                        Double.parseDouble(dataPoint[0]);
                        Double.parseDouble(dataPoint[1]);
                    }
                    catch (NumberFormatException x){
                        if (errorCounts < 5){
                            errorMessage += (lineNum + applicationTemplate.manager.getPropertyValue(COLON.name())
                                    + applicationTemplate.manager.getPropertyValue(INVALID_DATA_MESSAGE.name())
                                    + "\n");
                        }
                        errorCounts++;
                    }
                }
            }
        }
        if (errorCounts == 0){
            ((AppUI)(applicationTemplate.getUIComponent())).setMetaData(dataLabels,names,((AppActions)(applicationTemplate.getActionComponent())).getDataFilePath());
            return true;
        }
        else {((AppActions)(applicationTemplate.getActionComponent())).setDataFilePath(null);}
        if (errorCounts >= 5)
            errorMessage += ("\n"
                    + (errorCounts-5) 
                    + applicationTemplate.manager.getPropertyValue(REMAINING_ERRORS.name())
                    + "\n");
        if (remindAboutFormat)
            errorMessage = applicationTemplate.manager.getPropertyValue(FORMAT_REMINDER.name()) 
                    + "\n"
                    + errorMessage;
        applicationTemplate.getDialog(Dialog.DialogType.ERROR).show(applicationTemplate.manager.getPropertyValue(ERROR_LABEL.name()), errorMessage);
        return false;
    }
}
