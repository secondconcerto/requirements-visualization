package com.app.requirements.visualization.web.models;

import java.util.List;

public class Requirements {

    public List<String> stringList;
    public List<String> columnList;

    public List<String> getColumnList() {
        return columnList;
    }

    public void setColumnList(List<String> columnList) {
        this.columnList = columnList;
    }

    public List<String> getStringList() {
        return stringList;
    }

    public void setStringList(List<String> requirements) {
        this.stringList = requirements;
    }

  /*  public static boolean isValidPoem(Requirements requirements) {
        return requirements != null && !Collections.emptyList(requirements.getRequirements());
    }*/
}
