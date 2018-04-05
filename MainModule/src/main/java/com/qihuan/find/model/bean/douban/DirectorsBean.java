package com.qihuan.find.model.bean.douban;

import java.io.Serializable;

public class DirectorsBean extends PersonBean implements Serializable {
    private String alt;
    /**
     * small : http://img3.douban.com/img/celebrity/small/230.jpg
     * large : http://img3.douban.com/img/celebrity/large/230.jpg
     * medium : http://img3.douban.com/img/celebrity/medium/230.jpg
     */


    public String getAlt() {
        return alt;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }

    public AvatarsBean getAvatars() {
        return avatars;
    }

    public void setAvatars(AvatarsBean avatars) {
        this.avatars = avatars;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


}
