package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import model.DiskBlock;
import model.FAT;
//import model.Path;


public class DeleteController {
    @FXML
    private Button confirm;

    @FXML
    private Pane mainPane;

    @FXML
    private Button unconfirmed;

    @FXML
    void shutdown(ActionEvent event) {
        stage.hide();
    }

    @FXML
    void Confirm(ActionEvent event) {
        //yesButton();
        stage.hide();
    }


    public static FAT fat;
    public static Stage stage;
    public static DiskBlock block;

    public DeleteController(){

    }
    public void initialize(){

    }

    //private void yesButton(){
    //    Path thisPath = null;
    //    Controller temp = new Controller();
    //    if (block.getRegistry() instanceof Folder) {
    //        thisPath = ((Folder)block.getRegistry()).getPath();
    //    }
    //    int res = fat.delete(block);
    //    if (res == 0){
    //        tempPath = thisPath;
    //        System.out.println("删除文件夹成功");
    //    }
    //    else if (res == 1){
    //        System.out.println("删除文件成功");
    //    }
    //    else if (res == 2){
    //        System.out.println("文件夹不为空");
    //    } else{
    //        System.out.println("文件未关闭");
    //    }
    //}
}
