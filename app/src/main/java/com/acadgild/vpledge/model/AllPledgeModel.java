package com.acadgild.vpledge.model;

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
    private int pledge_user_id;
    private int pledge_units_completed;

    public String getPledge_image_url() {
        return pledge_image_url;
    }

    public void setPledge_image_url(String pledge_image_url) {
        this.pledge_image_url = pledge_image_url;
    }

    private String pledge_image_url;

    public int getPledge_units_completed() {
        return pledge_units_completed;
    }

    public void setPledge_units_completed(int pledge_units_completed) {
        this.pledge_units_completed = pledge_units_completed;
    }

    public int getPledge_user_id() {
        return pledge_user_id;
    }

    public void setPledge_user_id(int pledge_user_id) {
        this.pledge_user_id = pledge_user_id;
    }

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
