package com.lakithrathnayake.myapplication04.greendao.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class Items {
    @Id
    private Long id;
    private Integer item_id;
    private String item_code;
    private String item_desc;

    @Generated(hash = 1160869598)
    public Items(Long id, Integer item_id, String item_code, String item_desc) {
        this.id = id;
        this.item_id = item_id;
        this.item_code = item_code;
        this.item_desc = item_desc;
    }

    @Generated(hash = 1040818858)
    public Items() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getItem_id() {
        return item_id;
    }

    public void setItem_id(Integer item_id) {
        this.item_id = item_id;
    }

    public String getItem_code() {
        return item_code;
    }

    public void setItem_code(String item_code) {
        this.item_code = item_code;
    }

    public String getItem_desc() {
        return item_desc;
    }

    public void setItem_desc(String item_desc) {
        this.item_desc = item_desc;
    }
}
