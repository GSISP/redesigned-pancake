package com.acadgild.mypledge.ipledge.model;

/**
 * Created by pushp_000 on 5/12/2016.
 */
public class AllPledgeModel {

    private int id;
    private String name;
    private String description;
    private int status ;
    private int points;
    private int pledge_unit_id;
    private int pledge_unit_quantity;
    private int progress_auto_update;
    private boolean already_taken;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getPledge_unit_id() {
        return pledge_unit_id;
    }

    public void setPledge_unit_id(int pledge_unit_id) {
        this.pledge_unit_id = pledge_unit_id;
    }

    public int getPledge_unit_quantity() {
        return pledge_unit_quantity;
    }

    public void setPledge_unit_quantity(int pledge_unit_quantity) {
        this.pledge_unit_quantity = pledge_unit_quantity;
    }

    public int getProgress_auto_update() {
        return progress_auto_update;
    }

    public void setProgress_auto_update(int progress_auto_update) {
        this.progress_auto_update = progress_auto_update;
    }

    public boolean isAlready_taken() {
        return already_taken;
    }

    public void setAlready_taken(boolean already_taken) {
        this.already_taken = already_taken;
    }
}
