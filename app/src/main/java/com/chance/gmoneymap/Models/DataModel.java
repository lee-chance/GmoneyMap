package com.chance.gmoneymap.Models;

public class DataModel {
    private String CMPNM_NM, INDUTYPE_NM, REFINE_ROADNM_ADDR, REFINE_LOTNO_ADDR,
            TELNO, REFINE_WGS84_LAT, REFINE_WGS84_LOGT;

    public DataModel() {
    }

    public DataModel(String CMPNM_NM, String INDUTYPE_NM, String REFINE_ROADNM_ADDR, String REFINE_LOTNO_ADDR, String TELNO, String REFINE_WGS84_LAT, String REFINE_WGS84_LOGT) {
        this.CMPNM_NM = CMPNM_NM;
        this.INDUTYPE_NM = INDUTYPE_NM;
        this.REFINE_ROADNM_ADDR = REFINE_ROADNM_ADDR;
        this.REFINE_LOTNO_ADDR = REFINE_LOTNO_ADDR;
        this.TELNO = TELNO;
        this.REFINE_WGS84_LAT = REFINE_WGS84_LAT;
        this.REFINE_WGS84_LOGT = REFINE_WGS84_LOGT;
    }

    public String getCMPNM_NM() {
        return CMPNM_NM;
    }

    public String getINDUTYPE_NM() {
        return INDUTYPE_NM;
    }

    public String getREFINE_ROADNM_ADDR() {
        return REFINE_ROADNM_ADDR;
    }

    public String getREFINE_LOTNO_ADDR() {
        return REFINE_LOTNO_ADDR;
    }

    public String getTELNO() {
        return TELNO;
    }

    public String getREFINE_WGS84_LAT() {
        return REFINE_WGS84_LAT;
    }

    public String getREFINE_WGS84_LOGT() {
        return REFINE_WGS84_LOGT;
    }
}