package sample;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.FlowPane;
import javafx.stage.Modality;
import model.*;
import model.File;
import util.FatUtil;
import java.util.List;



/**
 * @author lizlg
 * DATA 2021/9/16
 */
public class NewNameControl {
  @FXML
  private TextField textField;
  @FXML
  private Button btnEnsure;
  @FXML
  private Button btnCancel;

  public static NewNameControl newNameControl;
  private String recentPath = MainController.mainController.getRecentPath();
  public static boolean isFile = false;
  public static boolean isFolder = false;
  public static boolean isRename = false;

  public String oldName;///
  
  public void initialize() {
    newNameControl = this;


    MainController.newFileStage.setOnCloseRequest(event -> {
      isRename = false;
      isFolder = false;
      isFile = false;
    });
    MainController.newFileStage.getIcons().add(new Image(FatUtil.MAIN_ICON));
    MainController.newFileStage.initOwner(FatUtil.mainStage);
    MainController.newFileStage.initModality(Modality.WINDOW_MODAL);
  }

  @FXML
  public void actionEnsure() {
    createNewName();
    isRename = false;
    isFolder = false;
    isFile = false;
  }

  @FXML
  public void actionCancel() {
    isRename = false;
    isFolder = false;
    isFile = false;
    MainController.newFileStage.close();
  }

  public TextField getTextField() {
    return this.textField;

  }

  public void createNewName() {
    //找到父目录
    Folder parentPath = MainController.fat.getFolder(recentPath);
    String newFileName = NewNameControl.newNameControl.getTextField().getText();
    //名字是否重复
    boolean isRepeatName = false;
    // 非法输入，匹配返回false
    String validInput = "[^$./]+";
    if (newFileName.length() == 0) {
      Alerts.createToolTipBox("错误提示", "名字不能为空，请重新输入！", -1, -1);
      return;
    } else {
      //查询是否重名
      while (!isRepeatName) {
        for (Registry registry : parentPath.getChildren()) {
          //重名
          if (registry.getName().equals(newFileName)) {
            isRepeatName = true;
            break;
          }
        }
        break;
      }
      //查询命名是否非法，匹配返回false
      if (!newFileName.matches(validInput)) {
        Alerts.createToolTipBox("错误提示", "名字输入非法，请重新输入！", -1, -1);
        return;
      }
      //如果名字重复
      if (isRepeatName) {
        Alerts.createToolTipBox("错误提示", "名字重复，请重新输入！", -1, -1);
        return;
      }
    }
    String absolutePath = recentPath + "/" + newFileName;
    int startBlockNo = MainController.fat.searchEmptyDiskBlockNo();

    if (isRename) {///
    	List<Registry> chilerenList = parentPath.getChildren();
    	for(Registry renameRegistry : chilerenList) {
    		System.out.println(newNameControl.oldName+" "+renameRegistry.getName());
    		if(renameRegistry.getName().equals(newNameControl.oldName)) {
    			if(renameRegistry instanceof File) {
    				renameRegistry.setName(newFileName);
        			renameRegistry.setAbsolutePath(absolutePath);
    			} else {
    				renameRegistry.setName(newFileName);
        			renameRegistry.setAbsolutePath(absolutePath);
        			List<Registry> registrieslList = ((Folder)renameRegistry).getChildren();
        			if(registrieslList!=null) {
        			  for(Registry registry : registrieslList) {
        				  if(registry instanceof Folder) {
        					  renameFolder((Folder)registry);
        				  }
        			  }
        			}
    				renameFolder((Folder)renameRegistry);//递归重命名子文件夹
    			}
    		}
    	}
    }
    
    //下面是新建文件或文件夹的代码
    DiskBlock diskBlock2 = MainController.fat.getDiskBlocks()[startBlockNo];
    if (isFile) {
      File newFile = new File(newFileName, absolutePath, startBlockNo, parentPath);
      diskBlock2.allocBlock(255, newFile);
      parentPath.addChildren(newFile);
    }

    if (isFolder) {
      Folder folder = new Folder(newFileName, absolutePath, startBlockNo, parentPath);
      diskBlock2.setRegistry(folder);
      diskBlock2.setNo(startBlockNo);
      diskBlock2.setNextNo(FatUtil.END);
      parentPath.addChildren(folder);
    }
    //更新面板的文件和文件夹内容
    FlowPane middlePane = MainController.mainController.getMiddlePane();
    MainController.mainController.initTables();
    middlePane.getChildren().removeAll(middlePane.getChildren());
    MainController.mainController.addIcon(MainController.fat.getStartBlockList(recentPath), recentPath);
    MainController.mainController.initTreeView();
    MainController.newFileStage.close();
  }
  
  private void renameFolder(Folder folder) {///
	  folder.setAbsolutePath(folder.getParent().getAbsolutePath()+"/"+folder.getName());
	  List<Registry> registrieslList = folder.getChildren();
	  if(registrieslList!=null) {
		  for(Registry registry : registrieslList) {
			  if(registry instanceof Folder) {
				  renameFolder((Folder)registry);
			  }
		  }
	  }
  }
}
