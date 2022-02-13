package model;

import javafx.collections.ObservableList;
import util.FatUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class FAT implements Serializable {

  private static final long serialVersionUID = 2L;


  private DiskBlock[] diskBlocks;
  public static transient ObservableList<File> openedFiles;
  private Folder rootFolder;

  public FAT() {
    rootFolder = new Folder("root", "root", 0, null);
    diskBlocks = new DiskBlock[128];
    diskBlocks[0] = new DiskBlock(0, 1, rootFolder);
    diskBlocks[1] = new DiskBlock(1, FatUtil.END, rootFolder);
    for (int i = 2; i < 128; i++) {
      diskBlocks[i] = new DiskBlock(i, FatUtil.FREE, null);
    }

  }

  public void addOpenedFile(DiskBlock diskBlock) {
    if (!(diskBlock.getRegistry() instanceof File))
      return;
    File file = (File) diskBlock.getRegistry();
    openedFiles.add(file);
    file.setOpen(true);
  }


  //判断指定盘块中的文件是否打开
  public boolean isOpenedFile(DiskBlock diskBlock) {
    if (!(diskBlock.getRegistry() instanceof File))
      return false;
    File file = (File) diskBlock.getRegistry();
    return file.isOpen();
  }

  //在指定路径下创建文件夹
  public int createFolder(String parentPath) { //所有path都是绝对路径，此处也不例外

    //找到父目录
    Folder parent = getFolder(parentPath);
    if (parent == null)
      return FatUtil.PARENT_NOT_FOUND_ERROR;

    //判断父目录是否有足够空间
    if (parent.getChildren().size() >= 8) {
      return FatUtil.INSUFFICIENT_SPACE_ERROR;
    }
    return searchEmptyDiskBlockNo();

  }

  //在指定路径下创建文件
  public int createFile(String parentPath) {
    //找到父目录
    Folder parent = getFolder(parentPath);
    if (parent == null)
      return FatUtil.PARENT_NOT_FOUND_ERROR;

    //判断父目录是否有足够空间
    if (parent.getChildren().size() >= 8) {
      return FatUtil.INSUFFICIENT_SPACE_ERROR;
    }
    return searchEmptyDiskBlockNo();

  }

  public void getNewFileName() {

  }

  public DiskBlock[] getDiskBlocks() {
    return diskBlocks;
  }


  //返回第一个空闲的盘块号
  public int searchEmptyDiskBlockNo() {
    for (int i = 2; i < diskBlocks.length; i++) {
      if (diskBlocks[i].isFree()) {
        return i;
      }
    }
    return FatUtil.ERROR;
  }

  //按盘块号查找盘块
  public DiskBlock getBlock(int index) {
    return diskBlocks[index];
  }

  //计算已使用盘块数
  public int useBlocksCount() {
    int cnt = 0;
    for (DiskBlock diskBlock : diskBlocks) {
      if (!diskBlock.isFree())
        ++cnt;
    }
    return cnt;
  }

  //文件长度变更时重新分配盘块
  public int reallocBlocks(int length, DiskBlock diskBlock) {
    File file = (File) diskBlock.getRegistry();

    int oldLength = 0;
    for (int no = file.getStartBlockNo(); no != FatUtil.END; no = diskBlocks[no].getNextNo()) {
      ++oldLength;
    }

    if (length > oldLength) {
      int addBLockNum = length - oldLength;
      if (freeBlocksCount() < addBLockNum)
        return FatUtil.INSUFFICIENT_SPACE_ERROR; //磁盘空间不足

      int preNo = file.getStartBlockNo();
      for (int no = file.getStartBlockNo(); no != FatUtil.END; no = diskBlocks[no].getNextNo()) {
        preNo = no;
      }

      while (addBLockNum > 0) {
        int freeBlockNo = searchEmptyDiskBlockNo();
        DiskBlock freeBlock = diskBlocks[freeBlockNo];
        diskBlocks[preNo].setNextNo(freeBlockNo);
        freeBlock.allocBlock(FatUtil.END, file);
        preNo = freeBlockNo;
        --addBLockNum;
      }
    } else if (length < oldLength) {
      int preNo = file.getStartBlockNo();
      int no = preNo;
      for (int i = 0; i < length; i++) {
        no = diskBlocks[no].getNextNo();
      }
      preNo = no - 1;
      diskBlocks[preNo].setNextNo(FatUtil.END);
      preNo = no;
      no = diskBlocks[no].getNextNo();
      diskBlocks[preNo].clearBlock();
      while (no != FatUtil.END) {
        preNo = no;
        no = diskBlocks[no].getNextNo();
        diskBlocks[preNo].clearBlock();
      }

    }
    return FatUtil.SUCCESS;
  }

  public  ObservableList<File>  getOpenedFiles() {
    return openedFiles;
  }

  //计算空闲盘块数
  public int freeBlocksCount() {
    int cnt = 0;
    for (DiskBlock diskBlock : diskBlocks) {
      if (diskBlock.isFree())
        ++cnt;
    }
    return cnt;
  }

  //返回指定路径指向的文件夹
  public Folder getFolder(String path) {
    String[] pathNodes = path.split("/");
    Folder parent = rootFolder;
    for (int i = 1; i < pathNodes.length; ++i) {
      String tempName = pathNodes[i];
      List<Registry> children = parent.getChildren();
      boolean finded = false;
      for (Registry child : children) {
        if (child instanceof Folder) {
          Folder tempFolder = (Folder) child;
          if (tempFolder.getName().equals(tempName)) {
            parent = tempFolder;
            finded = true;
            break;
          }
        }
      }
      if (!finded)
        return null;
    }
    return parent;
  }

  //给出路径名返回路径对象
  public File getFile(String path) {
    int splitIndex = path.lastIndexOf("/");
    String parentPath = path.substring(0, splitIndex);
    String name = path.substring(splitIndex + 1);
    Folder parent = getFolder(parentPath);
    if (parent == null)
      return null;
    for (Registry child : parent.getChildren()) {
      if (child instanceof File) {
        if (((File) child).getName().equals(name)) {
          return (File) child;
        }
      }
    }
    return null;
  }

  //返回指定目录下所有文件夹和文件的起始盘块
  public List<DiskBlock> getStartBlockList(String patentPath) {
    List<DiskBlock> startBlockList = new ArrayList<>();
    Folder parent = getFolder(patentPath);
    if (parent == null)
      return startBlockList;
    for (Registry child : parent.getChildren()) {
      int startBlockNo = child instanceof File ? ((File) child).getStartBlockNo() : ((Folder) child).getStartBlockNo();
      DiskBlock startBlock = diskBlocks[startBlockNo];
      startBlockList.add(startBlock);
    }
    return startBlockList;
  }

  public int delete(DiskBlock diskBlock) {
    if (diskBlock.getRegistry() instanceof File) {
      if (isOpenedFile(diskBlock)) {
        //文件被打开不能删除
        return FatUtil.OPENED_FILE_ERROR;
      }
      File file = (File) diskBlock.getRegistry();
      Folder parent = file.getParent();
      //将文件删除
      parent.removeChildren(file);
      //把文件存放的磁盘删除信息
      diskBlock.clearBlock();

      return FatUtil.SUCCESS;
    } else if (diskBlock.getRegistry() instanceof Folder) {
      Folder folder = (Folder) diskBlock.getRegistry();
      if (folder.getChildren().size() != 0) {
        List<Registry> children = folder.getChildren();
        while (children.size() != 0) {
          Registry child = children.get(0);
          for (int j = 2; j < 128; j++) {
            if (child == diskBlocks[j].getRegistry()) {
              delete(diskBlocks[j]);
            }
          }
        }
      }
      Folder parent = folder.getParent();
      parent.removeChildren(folder);

      diskBlock.clearBlock();

      return FatUtil.SUCCESS;
    } else {
      if (diskBlock.getRegistry() == null)
        return FatUtil.SUCCESS;
      return FatUtil.UNKNOWN_ERROR;
    }
  }
}
