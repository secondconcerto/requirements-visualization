package com.app.requirements.visualization.web.models;

import java.util.List;
import java.util.Set;

public class Requirements {

    public Set<String> stringList;
    public Set<String> columnList;
    public Set<String> keyPhrases;
    public Set<String> UIList;

    public Set<String> getColumnList() {
        return columnList;
    }

    public void setColumnList(Set<String> columnList) {
        this.columnList = columnList;
    }

    public Set<String> getStringList() {
        return stringList;
    }

    public void setStringList(Set<String> requirements) {
        this.stringList = requirements;
    }

    public Set<String> getUIList() {
        return UIList;
    }

    public void setUIList(Set<String> UIList) {
        this.UIList = UIList;
    }

    public Set<String> getKeyPhrases() {
        return keyPhrases;
    }

    public void setKeyPhrases(Set<String> keyPhrases) {
        this.keyPhrases = keyPhrases;
    }
}
