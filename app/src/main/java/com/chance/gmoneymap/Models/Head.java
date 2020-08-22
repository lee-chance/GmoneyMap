package com.chance.gmoneymap.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Head {

    @SerializedName("list_total_count")
    @Expose
    private Integer listTotalCount;
    @SerializedName("RESULT")
    @Expose
    private RESULT rESULT;
    @SerializedName("api_version")
    @Expose
    private String apiVersion;

    public Integer getListTotalCount() {
        return listTotalCount;
    }

    public void setListTotalCount(Integer listTotalCount) {
        this.listTotalCount = listTotalCount;
    }

    public RESULT getRESULT() {
        return rESULT;
    }

    public void setRESULT(RESULT rESULT) {
        this.rESULT = rESULT;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

}