package model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import util.FatUtil;

public class Folder extends Registry implements Serializable {


    private static final long serialVersionUID = 2L;

    private Date createTime;//创建时间
    private Folder parent;//父文件夹
    private List<Registry> children;//子文件夹

    public Folder(String name, int startBlockNo, String absolutePath, int length,
                  Date createTime, Folder parent, List<Registry> children) {
        super(name, "", absolutePath, FatUtil.DEFAULT_FOLDER_ATTRIBUTE, startBlockNo, length);
        this.createTime = createTime;
        this.parent = parent;
        this.children = children;
    }

    public Folder(String name, String absolutePath, int startBlockNo, Folder parent) {
        this(name, startBlockNo, absolutePath, 1, new Date(), parent, new ArrayList<>());
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

    public boolean hasChild() {
        return !children.isEmpty();
    }

    public List<Registry> getChildren() {
        return children;
    }

    public void setChildren(List<Registry> children) {
        this.children = children;
    }

    public void addChildren(Registry child) {
        this.children.add(child);
    }

    public void removeChildren(Registry child) {
        this.children.remove(child);
    }

    @Override
    public String toString() {
        return getAbsolutePath();
    }
}
