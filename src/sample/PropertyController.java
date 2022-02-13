package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.text.Text;
import model.*;

import java.util.List;

public class PropertyController {

    @FXML
    private Label fileName;

    @FXML
    private Text text3;

    @FXML
    private Text text4;

    @FXML
    private Text text1;

    @FXML
    private Text text2;

    @FXML
    private Text text5;

    @FXML
    private Text text6;

    @FXML
    private RadioButton hide;

    @FXML
    private RadioButton onlyRead;

    @FXML
    void ReadAction(ActionEvent event) {
        Byte num=0;
        if (onlyRead.isSelected()){
            num=0;
            file.setAttribute(num);
        }else if(hide.isSelected()){
            num=2;
            file.setAttribute(num);
        }else {
            num=4;
            file.setAttribute(num);
        }
    }

    @FXML
    void hideAction(ActionEvent event) {
        Byte num=0;
        if (onlyRead.isSelected()){
            num=0;
            file.setAttribute(num);
        }else if(hide.isSelected()){
            num=2;
            file.setAttribute(num);
        }else {
            num=4;
            file.setAttribute(num);
        }
    }

    public static DiskBlock block;
    public static FAT fat;
    public static Registry file;

    public void initialize(){

        fileName.setText(file.getName());

        if(file.getType().equals(".f")){
            text1.setText("文件");
        }else{
            text1.setText("文件夹");
        }
        text2.setText(file.getAbsolutePath());

        if (file instanceof File){
            File file1 = (File)file;
            showFile(file1);
        }else{
            Folder folder1 = (Folder)file;
            showFolder(folder1);
        }

    }

    public void showFile(File file){
        String ss=file.getContent();
        byte[] buff=ss.getBytes();
        double f=buff.length;
        f=f/1024;
        text3.setText(Math.ceil(f)+"kb");
        text4.setText(Math.ceil(f)+"kb");
        text5.setText(file.getCreateTime());
        int no = file.getAttribute();
        if(no==0){
            onlyRead.setSelected(true);
        }else if(no==2){
            hide.setSelected(true);
        }
    }

    public void showFolder(Folder folder){
        List<Registry> temp = fat.getFolder(folder.getAbsolutePath()).getChildren();
        String ss="";
        for(int i=0;i< temp.size();i++){
            if (temp.get(i) instanceof File){
                ss=ss+((File) temp.get(i)).getContent();
            }
            else{
                ss=ss+FolderString(temp.get(i).getAbsolutePath());
            }
        }
        byte[] buff=ss.getBytes();
        double f=buff.length;
        f=f/1024;
        text3.setText(Math.ceil(f)+"kb");
        text4.setText(Math.ceil(f)+"kb");
        text5.setText(folder.getCreateTime());
        int no = folder.getAttribute();
        if(no==0){
            onlyRead.setSelected(true);
        }else if(no==2){
            hide.setSelected(true);
        }
    }

    private String FolderString(String path){
        List<Registry> temp = fat.getFolder(path).getChildren();
        String ss="";
        for(int i=0;i< temp.size();i++){
            if (temp.get(i) instanceof File){
                ss=ss+((File) temp.get(i)).getContent();
            }
            else{
                ss=ss+FolderString(temp.get(i).getAbsolutePath());
            }
        }
        return ss;
    }
}
