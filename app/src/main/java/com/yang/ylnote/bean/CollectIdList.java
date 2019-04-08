package com.yang.ylnote.bean;

import java.io.Serializable;

public class CollectIdList implements Serializable {

    /**
     * note_id : note_20181129230511, note_20181203180730
     */

    private String note_id;

    public CollectIdList(){}


    public String getNote_id() {
        return note_id;
    }

    public void setNote_id(String note_id) {
        this.note_id = note_id;
    }
}
