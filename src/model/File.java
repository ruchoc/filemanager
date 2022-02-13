package model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import util.FatUtil;

public class File extends Registry implements Serializable {

    private static final long serialVersionUID = 2L;

    private String content;//内容
    private Folder parent;//父文件夹
    private Date createTime;//创建时间
    private boolean isOpen;//打开标志

    public File(String name, String type, String absolutePath, byte attribute, int startBlockNo, int len,
                String content, Folder parent, Date createTime, boolean isOpen) {
        super(name, type, absolutePath, attribute, startBlockNo, len);
        this.content = content;
        this.parent = parent;
        this.createTime = createTime;
        this.isOpen = isOpen;
    }

    public File(String name, String absolutePath, int startBlockNo, Folder parent) {
        this(name, FatUtil.DEFAULT_TYPE, absolutePath, FatUtil.DEFAULT_FILE_ATTRIBUTE, startBlockNo, 1,
                "", parent, new Date(), false);
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreateTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日  HH:mm:ss");
        return simpleDateFormat.format(createTime);
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Folder getParent() {
        return parent;
    }

    public void setParent(Folder parent) {
        this.parent = parent;
    }

    public boolean hasParent() {
        return parent != null;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    @Override
    public String toString() {
        return getAbsolutePath();
    }
}
