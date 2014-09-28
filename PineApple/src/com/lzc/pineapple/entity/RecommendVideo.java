
package com.lzc.pineapple.entity;

import java.io.Serializable;
import java.util.List;

public class RecommendVideo implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private List<String> src;

    private String img;

    private String title;

    private String actor;

    private String director;

    private String catalog;
    
    private int price;
    
    private int buy;
    
    private int id;
    

    public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getBuy() {
		return buy;
	}

	public void setBuy(int buy) {
		this.buy = buy;
	}

	public List<String> getSrc() {
        return src;
    }

    public void setSrc(List<String> src) {
        this.src = src;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }
    
    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
