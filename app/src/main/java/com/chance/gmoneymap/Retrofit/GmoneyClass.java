package com.chance.gmoneymap.Retrofit;

import java.util.List;

import com.chance.gmoneymap.Models.RegionMnyFacltStu;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GmoneyClass {

    @SerializedName("RegionMnyFacltStus")
    @Expose
    private List<RegionMnyFacltStu> regionMnyFacltStus = null;

    public List<RegionMnyFacltStu> getRegionMnyFacltStus() {
        return regionMnyFacltStus;
    }

    public void setRegionMnyFacltStus(List<RegionMnyFacltStu> regionMnyFacltStus) {
        this.regionMnyFacltStus = regionMnyFacltStus;
    }

}