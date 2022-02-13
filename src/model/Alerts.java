package model;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import util.FatUtil;

import java.util.Objects;

/**
 * @author LIzlg
 * DATA 2021/10/24,星期日
 * @version 1.0
 */
public class Alerts {

  /**
   * 警报窗口的初始化
   */
  public static void createToolTipBox(String title, String content, double x, double y) {
    Dialog<String> dialog = new Dialog<>();
    if (x >= 0) {
      dialog.setX(x);
    }
    if (y >= 0) {
      dialog.setY(y);
    }
    dialog.setTitle(title);
    Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
    if(Objects.equals(stage.getTitle(), "关于开发")){
      dialog.setHeaderText("开发人员");
    }
    stage.getIcons().add(new Image(FatUtil.MAIN_ICON));
    dialog.setContentText(content);
    dialog.getDialogPane().setStyle("-fx-background-color: white;" + "-fx-font-size:16");
    ButtonType okButton = new ButtonType("确定", ButtonBar.ButtonData.OK_DONE);
    dialog.getDialogPane().getButtonTypes().add(okButton);
    dialog.show();
  }
}
