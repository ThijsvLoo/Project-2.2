package com.mygdx.mass.Data;

public class Properties {
    private String name, setting;
    final private String separator = ": ";

    public Properties(String name, String setting){
        this.name = name;
        this.setting = setting;
    }

    public Properties(String[] propInfo){
//        System.out.println(propInfo.toString());
        this.name = propInfo[0].toString();
        this.setting = propInfo[1].toString();
//        System.out.println(this.name);
//        System.out.println(this.setting);
    }

    public String getName() {
        return name;
    }

    public String getSetting() {
        return setting;
    }

    public String getSeparator() {
        return separator;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSetting(String setting) {
        this.setting = setting;
    }

    public String getLine(){
        return (getName() + getSeparator() + getSetting());
    }
}

