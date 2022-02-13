package model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

public class Registry implements Serializable {

    private static final Long serialVersionUID = 2L;

	private String name;//文件。目录名
    private String type;//文件类型名
    private String absolutePath;//文件。目录绝对路径
    private Byte attribute;//文件。目录属性
    private int startBlockNo;//文件。目录起始盘块
    private int length;//文件长度

    private transient StringProperty nameSP = new SimpleStringProperty();
    private transient StringProperty typeSP = new SimpleStringProperty();
    private transient StringProperty attributeSP = new SimpleStringProperty();
    private transient StringProperty startBlockNoSP = new SimpleStringProperty();
    private transient StringProperty absolutePathSP = new SimpleStringProperty();
    private transient StringProperty lengthSP = new SimpleStringProperty();
    
    public Registry(String name, String type, String absolutePath, byte attribute, int startBlockNo, int length) {
        setName(name);
        setType(type);
        setAbsolutePath(absolutePath);
        setAttribute(attribute);
        setStartBlockNo(startBlockNo);
        setLength(length);
        generateRelativeProperty();
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public Byte getAttribute() {
        return attribute;
    }

    public int getStartBlockNo() {
        return startBlockNo;
    }

    public int getLength() {
        return length;
    }

    public void setName(String name) {
        this.name = name;
        setNameSP(getName());
    }

    public void setType(String type) {
        this.type = type;
        setTypeSP(getType());
    }

    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
        setAbsolutePathSP(getAbsolutePath());
    }

    public void setAttribute(Byte attribute) {
        this.attribute = attribute;
        setAttributeSP(String.valueOf(getAttribute()));
    }

    public void setStartBlockNo(int startBlockNo) {
        this.startBlockNo = startBlockNo;
        setStartBlockNoSP(String.valueOf(getStartBlockNo()));
    }

    public void setLength(int length) {
        this.length = length;
        setLengthSP(String.valueOf(getLength()));
    }

    public String getNameSP() {
        return nameSP.get();
    }

    public StringProperty nameSPProperty() {
        return nameSP;
    }

    public void setNameSP(String nameSP) {
        this.nameSP.set(nameSP);
    }

    public String getTypeSP() {
        return typeSP.get();
    }

    public StringProperty typeSPProperty() {
        return typeSP;
    }

    public void setTypeSP(String typeSP) {
        this.typeSP.set(typeSP);
    }

    public String getAttributeSP() {
    	return attributeSP.get();
    }

    public StringProperty attributeSPProperty(String file) {
        return attributeSP;
    }

    public void setAttributeSP(String attributeSP) {
        this.attributeSP.set(attributeSP);
    }

    public String getStartBlockNoSP() {
        return startBlockNoSP.get();
    }

    public StringProperty startBlockNoSPProperty() {
        return startBlockNoSP;
    }

    public void setStartBlockNoSP(String startBlockNoSP) {
        this.startBlockNoSP.set(startBlockNoSP);
    }

    public String getAbsolutePathSP() {
        return absolutePathSP.get();
    }

    public StringProperty absolutePathSPProperty() {
        return absolutePathSP;
    }

    public void setAbsolutePathSP(String absolutePathSP) {
        this.absolutePathSP.set(absolutePathSP);
    }

    public String getLengthSP() {
        return lengthSP.get();
    }

    public StringProperty lengthSPProperty() {
        return lengthSP;
    }

    public void setLengthSP(String lengthSP) {
        this.lengthSP.set(lengthSP);
    }

    private void generateRelativeProperty() {
        setNameSP(getName());
        setAbsolutePathSP(getAbsolutePath());
        setAttributeSP(String.valueOf(getAttribute()));
        setTypeSP(getType());
        setStartBlockNoSP(String.valueOf(getStartBlockNo()));
        setLengthSP(String.valueOf(getLength()));
    }

    private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        nameSP = new SimpleStringProperty();
        typeSP = new SimpleStringProperty();
        attributeSP = new SimpleStringProperty();
        startBlockNoSP = new SimpleStringProperty();
        absolutePathSP = new SimpleStringProperty();
        lengthSP = new SimpleStringProperty();
        generateRelativeProperty();
    }
}
