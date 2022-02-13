package util;

import javafx.stage.Stage;
import model.File;
import model.Folder;
import model.Registry;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class FatUtil {

  public static final String BACK_IMG = "res/back.png";
  public static final String FILE_IMG = "res/file2.png";
  public static final String FOLDER_IMG = "res/folder2.png";
  public static final String MAIN_ICON = "res/mainIcon.png";
  public static final String help_IMG = "res/helpIcon.png";
  public static final String foldImage="res/foldIcon.png";
  public static final String spreadImage="res/spreadIcon.png";
  public static final int END = 255;
  public static final int ERROR = 128;
  public static final int FREE = 0;

  public static final int SUCCESS = 254;
  public static final int PARENT_NOT_FOUND_ERROR = 130;
  public static final int INSUFFICIENT_SPACE_ERROR = 131;
  public static final int OPENED_FILE_ERROR = 132;
  public static final int UNKNOWN_ERROR = 134;

  public static final String DEFAULT_TYPE = ".f";
  public static final Byte NORMAL_FILE_ATTRIBUTE = 4;
  public static final Byte DEFAULT_FILE_ATTRIBUTE = NORMAL_FILE_ATTRIBUTE;
  public static final Byte DEFAULT_FOLDER_ATTRIBUTE = 8;
  public static Stage mainStage;

  public static int calculateBlockNum(int contentLength) {
    int num = (int) Math.ceil((double) contentLength / 64);
    if (num == 0) {//计算出盘块数量为零时，应分配一个
      num = 1;
    }
    return num;
  }

  public static int getSize(String content) {
    return content.getBytes(StandardCharsets.UTF_8).length;
  }

  public static int getFolderSize(Folder folder) {
    List<Registry> children = folder.getChildren();
    int size = 0;
    for (Object child : children) {
      if (child instanceof File) {
        size += getSize(((File) child).getContent());
      } else {
        size += getFolderSize((Folder) child);
      }
    }
    return size;
  }
}
