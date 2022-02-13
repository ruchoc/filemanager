package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import util.FatUtil;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import static sample.MainController.fat;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        String DURATION_PATH = "disk";
        Parent root = FXMLLoader.load(getClass().getResource("mainView.fxml"));
        primaryStage.setTitle("模拟磁盘文件系统");
        Scene scene = new Scene(root);
        scene.getStylesheets().add("sample/mainUi.css");
        primaryStage.setScene(scene);
        primaryStage.getIcons().add(new Image(FatUtil.MAIN_ICON));
        primaryStage.setResizable(false);
        FatUtil.mainStage= primaryStage;
        primaryStage.show();
        primaryStage.setOnCloseRequest(e ->{
            try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(DURATION_PATH))) {
                outputStream.writeObject(fat);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
