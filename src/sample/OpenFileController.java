package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import model.*;
import util.FatUtil;

public class OpenFileController {

  @FXML
  private BorderPane borderPane;


  @FXML
  private Button closeButton;

  @FXML
  private Button saveButton;

  @FXML
  private TextArea textArea;
  public static DiskBlock block;
  public static FAT fat;
  public static File file;
  private String newContent, oldContent;
  public static OpenFileController openFileController;

  public void initialize() {
    openFileController = this;
    String temp;
    temp = MainController.contextText;
    textArea.setText(temp);
    if (file.getAttribute() == 0) {
      textArea.setDisable(true);
    } else if (file.getAttribute() == 2) {
      Alerts.createToolTipBox("提示","该文件为系统文件，无法打开！",-1,-1);
    }

  }

  @FXML
  void saveAction(ActionEvent event) {
    action();
  }

  private void action() {
    newContent = textArea.getText();
    oldContent = MainController.contextText;
    if (newContent == null) {
      newContent = "";
    }
    //当前打开的文件
    File openedFile = ((File) block.getRegistry());
    openedFile.setContent(newContent);
    openedFile.setLength(FatUtil.getSize(newContent));
    MainController.fat.reallocBlocks(FatUtil.calculateBlockNum(openedFile.getLength()), MainController.fat.getBlock(openedFile.getStartBlockNo()));
    textArea.setText(newContent);

  }
  public Button getCloseButton() {
    return closeButton;
  }


}
