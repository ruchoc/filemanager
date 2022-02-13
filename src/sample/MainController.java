package sample;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import model.*;
import model.File;
import util.FatUtil;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainController {

  public MenuButton helpButton;
  public ImageView helpImage;
  public SplitPane splitPane1;
  public SplitPane splitPane2;
  public AnchorPane downPane1;
  public Label time;
  public Button foldButton;
  public ImageView foldImageView;
  @FXML
  private ImageView backImage;
  @FXML
  private Button renovateButton;
  @FXML
  private FlowPane flowPane;

  @FXML
  private TextArea addressText;

  @FXML
  private ScrollPane rightPane;

  @FXML
  private AnchorPane upPane;

  @FXML
  private Pane leftPane;

  @FXML
  private SplitPane splitPane;

  @FXML
  private FlowPane middlePane;

  @FXML
  private AnchorPane downPane;

  @FXML
  private Text nowCatalogue;

  @FXML
  private TreeView<String> treeView;

  @FXML
  private TableView<DiskBlock> blockTable;   //磁盘块图表

  @FXML
  private TableColumn<DiskBlock, String> noCol;

  @FXML
  private TableColumn<DiskBlock, String> indexCol;

  @FXML
  private TableColumn<DiskBlock, String> registryCol;

  @FXML
  private TableView<File> openedTable; //打开文件信息图表

  @FXML
  private TableColumn<File, String> nameCol;

  @FXML
  private TableColumn<File, String> flagCol;

  @FXML
  private TableColumn<File, String> diskCol;

  @FXML
  private TableColumn<File, String> pathCol;

  @FXML
  private TableColumn<File, String> lengthCol;


  //新建文件或文件夹的起名对话框舞台界面
  public static Stage newFileStage = new Stage();
  public static Label chooseIcon;

  private ContextMenu contextMenu, contextMenu2;
  private MenuItem createFileItem, createFolderItem;
  private MenuItem openItem, delItem, renameItem, proItem;
  private TreeItem<String> rootNode, recentNode;
  private Map<String, TreeItem<String>> pathMap;
  private Label[] icons;
  private ObservableList<DiskBlock> dataBlock;

  public ObservableList<File> getDataOpened() {
    return dataOpened;
  }

  private ObservableList<File> dataOpened;

  private static final String DURATION_PATH = "disk";
  public static FAT fat;

  public int getIndex() {
    return index;
  }

  private int index;


  private String recentPath;
  private Stage stage;//确认删除

  public static String contextText;
  public static MainController mainController;
  private Image foldImage;
  private Image spreadImage;

  public void setStage(Stage stage) {
    this.stage = stage;
  }

  public Stage getStage() {
    return stage;
  }

  public void initialize() {
    FAT.openedFiles = FXCollections.observableArrayList(new ArrayList<File>());
    mainController = this;
    try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(DURATION_PATH))) {
      fat = (FAT) inputStream.readObject();
    } catch (Exception e) {
      e.printStackTrace();
    }
    if (fat == null) {
      fat = new FAT();
    }
    recentPath = "C:";
    initFrame();
    initStyle();
    bandProperty();
    showTime();

  }

  private void showTime() {
    DateFormat df = new SimpleDateFormat("yyyy.MM.dd hh:mm:ss");
    EventHandler<ActionEvent> eventHandler = e -> {
      time.setText(df.format(new Date()));
    };
    Timeline animation = new Timeline(new KeyFrame(Duration.millis(1000), eventHandler));
    animation.setCycleCount(Timeline.INDEFINITE);
    animation.play();
  }

  private void bandProperty() {
    openedTable.prefWidthProperty().bind(flowPane.widthProperty());
    openedTable.prefHeightProperty().bind(flowPane.heightProperty());
    treeView.prefWidthProperty().bind(flowPane.widthProperty());

  }

  private void initStyle() {
    splitPane1.setDividerPosition(0, 0.7);
    splitPane1.setDividerPosition(1, 0.1);
    Image help = new Image(FatUtil.help_IMG);
    helpImage.setImage(help);
    helpImage.setPreserveRatio(true);
    helpButton.setBackground(Background.EMPTY);
    Image back = new Image(FatUtil.BACK_IMG);
    backImage.setImage(back);
    helpButton.setTooltip(new Tooltip("帮助"));
    renovateButton.setTooltip(new Tooltip("跳转"));
    foldButton.setBackground(Background.EMPTY);
    foldButton.setTooltip(new Tooltip("折叠磁盘分配表"));
    foldImage = new Image(FatUtil.foldImage);
    foldImageView.setImage(foldImage);
  }

  private void initFrame() {
    middlePane.setStyle("-fx-background-color: #ffffff;" + "-fx-border-color: #ffffff;");
    middlePane.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent me) -> {
      if (me.getButton() == MouseButton.SECONDARY && !contextMenu2.isShowing()) {
        contextMenu.show(flowPane, me.getScreenX(), me.getScreenY());
        //展示选择项
      } else {
        contextMenu.hide();
        //隐藏选择项
      }
    });

    initContextMenu();
    menuItemSetOnAction();
    initTreeView();
    initTables();

  }


  private void initContextMenu() {

    createFileItem = new MenuItem("新建文件");
    createFolderItem = new MenuItem("新建文件夹");

    openItem = new MenuItem("打开");
    delItem = new MenuItem("删除");
    renameItem = new MenuItem("重命名");
    proItem = new MenuItem("属性");

    contextMenu = new ContextMenu(createFileItem, createFolderItem);
    contextMenu2 = new ContextMenu(openItem, delItem, renameItem, proItem);
  }

  private void menuItemSetOnAction() {
    createFileItem.setOnAction(ActionEvent -> {
      NewNameControl.isFile = true;
      //返回磁盘的第一块空闲块
      int no = fat.createFile(recentPath);
      if (no == FatUtil.ERROR) {
        //磁盘满了
        Alerts.createToolTipBox("提示信息", "磁盘已满，无法新建文件", -1, -1);
      } else {
        try {
          newFileStage = new Stage();
          Parent root = FXMLLoader.load(getClass().getResource("newNameView.fxml"));
          newFileStage.setTitle("新建文件");
          newFileStage.setScene(new Scene(root));
          newFileStage.getIcons().add(new Image(FatUtil.FILE_IMG));
          newFileStage.show();
        } catch (Exception e) {
          e.printStackTrace();
        }

      }
    });

    createFolderItem.setOnAction(ActionEvent -> {
      NewNameControl.isFolder = true;
      //返回磁盘的第一块空闲块
      int no = fat.createFolder(recentPath);
      if (no == FatUtil.ERROR) {
        //磁盘满了
        Alerts.createToolTipBox("提示信息", "磁盘已满，无法新建文件", -1, -1);
      } else {
        try {
          newFileStage = new Stage();
          Parent root = FXMLLoader.load(getClass().getResource("newNameView.fxml"));
          newFileStage.setTitle("新建文件夹");
          newFileStage.setScene(new Scene(root));
          newFileStage.getIcons().add(new Image(FatUtil.FOLDER_IMG));
          newFileStage.show();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });

    renameItem.setOnAction(actionEvent -> {
      NewNameControl.isRename = true;
      try {
        //当前要重命名的文件或文件夹的名字
        String currentFileName = fat.getFolder(recentPath).getChildren().get(index).getName();
        newFileStage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("newNameView.fxml"));
        newFileStage.setTitle("重名名");
        NewNameControl.newNameControl.getTextField().setText(currentFileName);
        NewNameControl.newNameControl.oldName=currentFileName;///
        newFileStage.setScene(new Scene(root));
        newFileStage.show();
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
    openItem.setOnAction(Accordion -> {
      onOpen();
    });

    backImage.setOnMouseClicked(event -> {
      if (!recentPath.equals("C:")) {
        String temp1 = recentPath.substring(recentPath.lastIndexOf(("/")) + 1);
        String lastPath = recentPath.substring(0, recentPath.length() - temp1.length() - 1);
        recentPath = lastPath;
        middlePane.getChildren().removeAll(middlePane.getChildren());
        addIcon(fat.getStartBlockList(recentPath), recentPath);
        addressText.setText(recentPath);
      } else {
        Alerts.createToolTipBox("提示", "当前目录为根目录", -1, -1);
      }
    });
    backImage.setOnMouseEntered(mouseEvent -> {
      backImage.setFitWidth(50);
      backImage.setFitHeight(50);
    });
    backImage.setOnMouseExited(MouseEvent -> {
      backImage.setFitWidth(40);
      backImage.setFitHeight(40);

    });
    helpButton.getItems().get(0).setOnAction(event -> {
      Alerts.createToolTipBox("模拟文件系统说明", "磁盘块中各值的说明：\n     0--代表磁盘块空闲；\n     255--代表文件或" +
              "文件夹占用的磁盘块以该磁盘块结束；\n     1~254--代表文件或文件夹占用的磁盘块的下一个磁盘块的块号。", -1, -1);
    });
    helpButton.getItems().get(1).setOnAction(event -> {
      Alerts.createToolTipBox("提示", "输入目录格式：C:/文件名/文件名", -1, -1);
    });
    helpButton.getItems().get(2).setOnAction(event -> {
      Alerts.createToolTipBox("关于开发", "xuan，run，wei，lizlg，tao", -1, -1);
    });
    foldButton.setOnAction(event -> {

      if (foldImageView.getImage() == foldImage) {
        spreadImage = new Image(FatUtil.spreadImage);
        foldImageView.setImage(spreadImage);
        foldButton.setTooltip(new Tooltip("展开磁盘块分配表"));
        blockTable.setMinWidth(20);
        splitPane1.setDividerPosition(0, 1);
        splitPane1.setDividerPosition(1, 0);
      } else if (foldImageView.getImage() == spreadImage) {
        foldImage = new Image(FatUtil.foldImage);
        foldImageView.setImage(foldImage);
        foldButton.setTooltip(new Tooltip("折叠磁盘块分配表"));
        blockTable.setMinWidth(270);
        splitPane1.setDividerPosition(0, 0.7);
        splitPane1.setDividerPosition(1, 0.3);

      }
    });

    renovateButton.setOnAction(ActionEvent -> {
      DiskBlock[] Blocks = fat.getDiskBlocks();
      String recentPath2 = addressText.getText();
      int i;
      for (i = 2; i < 100; i++) {
        if (Blocks[i].getNextNo() != 0) {
          if (Blocks[i].getRegistry().getAbsolutePath().equals(recentPath2)) {
            recentPath = recentPath2;
            middlePane.getChildren().removeAll(middlePane.getChildren());
            addIcon(fat.getStartBlockList(recentPath), recentPath);
            break;
          }
        }

      }
      if (recentPath.equals(addressText.getText())) {
      } else if (i >= 100) {
        Alerts.createToolTipBox("错误提示", "输入路径格式有误，请输入正确的路径！", -1, -1);
      }

    });

    delItem.setOnAction(ActionEvent -> {
      List<Registry> temp = fat.getFolder(recentPath).getChildren();//当前目录下的目录项
      DiskBlock[] blocks = fat.getDiskBlocks();//FAT
      String absolutePath = temp.get(index).getAbsolutePath();
      int i = 2;
      while (i < 128) {
        if (blocks[i].getNextNo() != 0) {
          if (blocks[i].getRegistry().getAbsolutePath().equals(absolutePath)) {
            fat.delete(blocks[i]);
          }
        }
        i++;
      }
      middlePane.getChildren().removeAll(middlePane.getChildren());
      addIcon(fat.getStartBlockList(recentPath), recentPath);
      initTreeView();


    });

    proItem.setOnAction(ActionEvent -> {
      onOpenPro();
    });

  }


  public void initTreeView() {

    ImageView image = new ImageView(FatUtil.FOLDER_IMG);
    image.setFitWidth(30);
    image.setFitHeight(40);
    image.setPreserveRatio(true);
    rootNode = new TreeItem<>("C:", image);
    rootNode.setExpanded(true);

    recentNode = rootNode;
    pathMap = new HashMap<>();
    pathMap.put("C:", rootNode);

    treeView = new TreeView<String>(rootNode);
    leftPane.getChildren().add(treeView);
    treeView.setPrefWidth(211);
    treeView.setPrefHeight(681);
    treeView.setStyle("-fx-background-color: #ffffff;");
    recentPath = "C:";
    List<Registry> folders = fat.getFolder(recentPath).getChildren();
    int n = folders.size();
    for (int i = 0; i < n; i++) {
      if (folders.get(i) instanceof Folder) {
        initTreeNode(folders.get(i).getAbsolutePath(), folders.get(i).getName(), rootNode);
      }
    }


    treeView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<String>>() {
      @Override
      public void changed(ObservableValue<? extends TreeItem<String>> observableValue, TreeItem<String> oldItem, TreeItem<String> newItem) {
        String tempPath = newItem.getValue();
        if (!newItem.getValue().equals("C:")) {
          while (!newItem.getParent().getValue().equals("C:")) {
            newItem = newItem.getParent();
            tempPath = newItem.getValue() + "/" + tempPath;

          }
          tempPath = "C:/" + tempPath;
        }
        recentPath = tempPath;
        addressText.setText(tempPath);
        middlePane.getChildren().removeAll(middlePane.getChildren());
        addIcon(fat.getStartBlockList(recentPath), recentPath);

      }
    });

    recentPath = addressText.getText();
    middlePane.getChildren().removeAll(middlePane.getChildren());
    addIcon(fat.getStartBlockList(recentPath), recentPath);

  }

  private void initTreeNode(String newPath, String name, TreeItem<String> parentNode) {
    TreeItem<String> newNode = addNode(parentNode, newPath);
    List<Registry> folders = fat.getFolder(newPath).getChildren();
    for (int i = 0; i < parentNode.getChildren().size(); i++) {
      String tempPath = parentNode.getChildren().get(i).getValue();
      if (parentNode.getChildren().get(i).getValue().equals(name)) {
        for (int j = 0; j < folders.size(); j++) {
          if (folders.get(j) instanceof Folder) {
            initTreeNode(folders.get(j).getAbsolutePath(),
                    folders.get(j).getName(), parentNode.getChildren().get(i));
          }
        }
      }
    }

  }


  private TreeItem<String> addNode(TreeItem<String> parentNode, String newPath) {
    String value = newPath.substring(newPath.lastIndexOf("/") + 1);
    ImageView image = new ImageView(FatUtil.FOLDER_IMG);
    image.setFitWidth(30);
    image.setFitHeight(40);
    image.setPreserveRatio(true);
    image.setSmooth(true);
    TreeItem<String> newNode = new TreeItem<String>(value, image);
    parentNode.getChildren().add(newNode);
    newNode.setExpanded(true);
    pathMap.put(newPath, newNode);
    return newNode;

  }

  public void removeNode(TreeItem<String> recentNode, String remPath) {

    recentNode.getChildren().remove(pathMap.get(remPath));
    pathMap.remove(remPath);
  }

  public TreeItem<String> getRecentNode() {
    return recentNode;
  }

  public void addIcon(List<DiskBlock> bList, String path) {
    List<Registry> filesAndFoldersList = fat.getFolder(recentPath).getChildren();
    int n = filesAndFoldersList.size();
    icons = new Label[n];
    for (int i = 0; i < n; i++) {

      if (filesAndFoldersList.get(i) instanceof Folder) {
        ImageView image = new ImageView(FatUtil.FOLDER_IMG);
        image.setFitWidth(70);
        image.setFitHeight(90);
        image.setPreserveRatio(true);
        icons[i] = new Label((filesAndFoldersList.get(i)).getName(), image);
        icons[i].setTooltip(new Tooltip("文件夹名字：" + filesAndFoldersList.get(i).getName()));
      } else {
        ImageView image = new ImageView(FatUtil.FILE_IMG);
        image.setFitWidth(60);
        image.setFitHeight(90);
        image.setPreserveRatio(true);
        icons[i] = new Label(filesAndFoldersList.get(i).getName(), image);
        icons[i].setTooltip(new Tooltip("文件名字：" + filesAndFoldersList.get(i).getName()));
      }
      icons[i].getTooltip().setStyle(" -fx-background-color: #1296db;");
      icons[i].setContentDisplay(ContentDisplay.TOP);
      icons[i].setPrefSize(80, 100);
      icons[i].setAlignment(Pos.BOTTOM_CENTER);
      icons[i].setWrapText(true);
      middlePane.getChildren().add(icons[i]);
      icons[i].setOnMouseEntered(new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent event) {
          ((Label) event.getSource()).setStyle("-fx-background-color: #87CEFA;");
        }
      });
      icons[i].setOnMouseExited(new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent event) {
          ((Label) event.getSource()).setStyle("-fx-background-color: #ffffff;");
        }
      });
      icons[i].addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent e) -> {
        Label src = (Label) e.getSource();
        for (int j = 0; j < n; j++) {
          if (src == icons[j]) {
            index = j;
          }
        }
        if (e.getButton() == MouseButton.SECONDARY && e.getClickCount() == 1) {
          contextMenu2.show(src, e.getScreenX(), e.getScreenY());
        } else if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {
          onOpen();
        } else {
          contextMenu2.hide();
        }
      });

    }

  }

  public void initTables() {
    dataBlock = FXCollections.observableArrayList(fat.getDiskBlocks());
    dataOpened = fat.getOpenedFiles();

    noCol.setCellValueFactory(new PropertyValueFactory<DiskBlock, String>("noSP"));
    indexCol.setCellValueFactory(new PropertyValueFactory<DiskBlock, String>("nextNoSP"));
    registryCol.setCellValueFactory(new PropertyValueFactory<DiskBlock, String>("registrySP"));

    nameCol.setCellValueFactory(new PropertyValueFactory<File, String>("nameSP"));
    flagCol.setCellValueFactory(new PropertyValueFactory<File, String>("attributeSP"));
    diskCol.setCellValueFactory(new PropertyValueFactory<File, String>("startBlockNoSP"));
    pathCol.setCellValueFactory(new PropertyValueFactory<File, String>("absolutePathSP"));
    lengthCol.setCellValueFactory(new PropertyValueFactory<File, String>("lengthSP"));


    blockTable.setItems(dataBlock);
    blockTable.setEditable(false);

    openedTable.setItems(dataOpened);

  }

  private void onOpenPro() {
    List<Registry> temp = fat.getFolder(recentPath).getChildren();
    DiskBlock[] Blocks = fat.getDiskBlocks();
    for (int i = 2; i < 128; i++) {
      if (Blocks[i].getRegistry() == null) {
        continue;
      }
      if (Blocks[i].getRegistry().getAbsolutePath().equals(temp.get(index).getAbsolutePath())) {
        if (Blocks[i].getRegistry() instanceof File) {
          PropertyController.block = Blocks[i];
          PropertyController.fat = fat;
          PropertyController.file = (File) temp.get(index);
          Stage stageTemp = new Stage();
          Parent root = null;
          try {
            root = FXMLLoader.load(getClass().getResource("PropertyView.fxml"));

          } catch (IOException e) {
            e.printStackTrace();
          }
          stageTemp.setTitle("文件属性");
          stageTemp.getIcons().add(new Image(FatUtil.MAIN_ICON));
          stageTemp.setScene(new Scene(root));
          stageTemp.show();
          break;
        } else {
          PropertyController.block = Blocks[i];
          PropertyController.fat = fat;
          PropertyController.file = (Folder) temp.get(index);
          Stage stageTemp = new Stage();
          Parent root = null;
          try {
            root = FXMLLoader.load(getClass().getResource("PropertyView.fxml"));
          } catch (IOException e) {
            e.printStackTrace();
          }
          stageTemp.setScene(new Scene(root));
          stageTemp.show();
          stageTemp.getIcons().add(new Image(FatUtil.MAIN_ICON));
          break;
        }
      }
    }
  }

  private void onOpen() {
    List<Registry> temp = fat.getFolder(recentPath).getChildren();
    DiskBlock[] Blocks = fat.getDiskBlocks();
    for (int i = 2; i < 128; i++) {
      if (Blocks[i].getRegistry() == null) {
        continue;
      }
      if (Blocks[i].getRegistry().getAbsolutePath().equals(temp.get(index).getAbsolutePath())) {
        if (Blocks[i].getRegistry() instanceof File) {
          OpenFileController.block = Blocks[i];
          OpenFileController.fat = fat;
          OpenFileController.file = (File) temp.get(index);
          //每个窗口打开的文件不一样，每个窗口对应一个file，故下面的窗口关闭请求中不能把OpenFile.file传进removeOpenfile方法里
          File file = (File) temp.get(index);
          contextText = ((File) Blocks[i].getRegistry()).getContent();
          Stage stageTemp = new Stage();
          Parent root = null;
          Byte num = 2;
          if (!temp.get(index).getAttribute().equals(num)) {
            fat.addOpenedFile(Blocks[i]);
            try {
              root = FXMLLoader.load(getClass().getResource("openFileView.fxml"));
            } catch (IOException e) {
              e.printStackTrace();
            }
            stageTemp.setScene(new Scene(root));
            stageTemp.getIcons().add(new Image(FatUtil.MAIN_ICON));
            stageTemp.setTitle(file.getName());
            stageTemp.show();

            OpenFileController.openFileController.getCloseButton().setOnMouseClicked(event -> {
              file.setOpen(false);
              removeOpenFile(file);
              stageTemp.close();
            });
            stageTemp.setOnCloseRequest(new EventHandler<WindowEvent>() {
              @Override
              public void handle(WindowEvent event) {
                file.setOpen(false);
                removeOpenFile(file);
              }
            });
            break;
          } else {
            Alerts.createToolTipBox("提示", "无法打开文件！", -1, -1);
          }

        } else {
          String newPath = Blocks[i].getRegistry().getAbsolutePath();
          recentPath = newPath;
          int len = recentNode.getChildren().size();
          middlePane.getChildren().removeAll(middlePane.getChildren());
          addIcon(fat.getStartBlockList(newPath), newPath);
          addressText.setText(recentPath);
        }

      }
    }
  }

  public FlowPane getMiddlePane() {
    return middlePane;
  }


  public String getRecentPath() {
    return recentPath;
  }

  public void removeOpenFile(File file) {
    for (File openedFile : dataOpened) {
      if (file == openedFile) {
        dataOpened.remove(openedFile);
        break;
      }
    }
  }


  public void helpAction(ActionEvent actionEvent) {
  }
}
