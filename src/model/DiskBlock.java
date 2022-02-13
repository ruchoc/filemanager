package model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import util.FatUtil;


public class DiskBlock implements Serializable {

    private static final long serialVersionUID = 2L;
    //实现Serializable接口的目的是为类可持久化，比如在网络传输或本地存储，为系统的分布和异构部署提供先决条件。若没有序列化，现在我们所熟悉的远程调用，对象数据库都不可能存在，
    //serialVersionUID适用于java序列化机制。

    private int no;
    private int nextNo;

    private Registry registry;

    private transient StringProperty noSP = new SimpleStringProperty();
    private transient StringProperty nextNoSP = new SimpleStringProperty();
    private transient StringProperty registrySP = new SimpleStringProperty();

    public String getNoSP() {
        return noSP.get();
    }

    public StringProperty noSPProperty() {
        return noSP;
    }

    public void setNoSP(String noSP) {
        this.noSP.set(noSP);
    }

    public String getNextNoSP() {
        return nextNoSP.get();
    }

    public StringProperty nextNoSPProperty() {
        return nextNoSP;
    }

    public void setNextNoSP(String nextNoSP) {
        this.nextNoSP.set(nextNoSP);
    }

    public String getRegistrySP() {
        return registrySP.get();
    }

    public StringProperty registrySPProperty() {
        return registrySP;
    }

    public void setRegistrySP(String registrySP) {
        this.registrySP.set(registrySP);
    }

    private void generateRelativeProperty() {
        setNoSP(String.valueOf(getNo()));
        setNextNoSP(String.valueOf(getNextNo()));
        setRegistrySP(getRegistry()==null?"":getRegistry().getName());
    }

    public DiskBlock(int no, int nextNo, Registry registry) {
        this.no = no;
        this.nextNo = nextNo;
        this.registry = registry;
        generateRelativeProperty();
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
        setNoSP(String.valueOf(no));
    }

    public int getNextNo() {
        return nextNo;
    }

    public void setNextNo(int nextNo) {
        this.nextNo = nextNo;
        setNextNoSP(String.valueOf(nextNo));
    }

    public Registry getRegistry() {
        return registry;
    }

    public void setRegistry(Registry registry) {
        this.registry = registry;
        if (registry != null) {
            this.registrySP.bind(registry.nameSPProperty());
        } else {
            this.registrySP.unbind();//移除被选元素的事件处理程序
            setRegistrySP(null);
        }


    }

    public void allocBlock(int nextNo, Registry registry) {
        setNextNo(nextNo);
        setRegistry(registry);
    }

    public void clearBlock() {
        setNextNo(FatUtil.FREE);
        setRegistry(null);
    }

    public boolean isFree() {
        return nextNo == 0;
    }

    private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        noSP = new SimpleStringProperty();
        nextNoSP = new SimpleStringProperty();
        registrySP = new SimpleStringProperty();

        generateRelativeProperty();
    }
}
