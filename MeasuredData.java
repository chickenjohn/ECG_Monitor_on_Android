package com.experiment.chickenjohn.materialdemo;

/**
 * Created by chickenjohn on 2016/3/8.
 */
public class MeasuredData {
    private String typeName;
    private int value;

    public MeasuredData(String typeName, int value){
        super();
        this.typeName = typeName;
        this.value = value;
    }

    public String getTypeName(){
        return typeName;
    }

    public String getValue(){
        return String.valueOf(value);
    }
}
