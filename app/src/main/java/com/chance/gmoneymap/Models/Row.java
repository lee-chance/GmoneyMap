package com.chance.gmoneymap.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Row implements Parcelable {

    @SerializedName("CMPNM_NM")
    @Expose
    private String shopName;
    @SerializedName("INDUTYPE_CD")
    @Expose
    private Object iNDUTYPECD;
    @SerializedName("BIZCOND_NM")
    @Expose
    private Object bIZCONDNM;
    @SerializedName("INDUTYPE_NM")
    @Expose
    private String categoryName;
    @SerializedName("TELNO")
    @Expose
    private String telNumber;
    @SerializedName("REGION_MNY_NM")
    @Expose
    private Object rEGIONMNYNM;
    @SerializedName("BRNHSTRM_MNY_USE_POSBL_YN")
    @Expose
    private Object bRNHSTRMMNYUSEPOSBLYN;
    @SerializedName("CARD_MNY_USE_POSBL_YN")
    @Expose
    private Object cARDMNYUSEPOSBLYN;
    @SerializedName("MOBILE_MNY_USE_POSBL_YN")
    @Expose
    private Object mOBILEMNYUSEPOSBLYN;
    @SerializedName("DATA_STD_DE")
    @Expose
    private String dATASTDDE;
    @SerializedName("REFINE_LOTNO_ADDR")
    @Expose
    private String locationAddress;
    @SerializedName("REFINE_ROADNM_ADDR")
    @Expose
    private String roadAddress;
    @SerializedName("REFINE_ZIP_CD")
    @Expose
    private String rEFINEZIPCD;
    @SerializedName("REFINE_WGS84_LOGT")
    @Expose
    private String longitude;
    @SerializedName("REFINE_WGS84_LAT")
    @Expose
    private String latitude;
    @SerializedName("SIGUN_CD")
    @Expose
    private String cityCode;
    @SerializedName("SIGUN_NM")
    @Expose
    private String cityName;

    protected Row(Parcel in) {
        shopName = in.readString();
        categoryName = in.readString();
        telNumber = in.readString();
        dATASTDDE = in.readString();
        locationAddress = in.readString();
        roadAddress = in.readString();
        rEFINEZIPCD = in.readString();
        longitude = in.readString();
        latitude = in.readString();
        cityCode = in.readString();
        cityName = in.readString();
    }

    public Row(String shopName, String categoryName, String telNumber, String roadAddress, String locationAddress, String longitude, String latitude) {
        this.shopName = shopName;
        this.categoryName = categoryName;
        this.telNumber = telNumber;
        this.locationAddress = locationAddress;
        this.roadAddress = roadAddress;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public static final Creator<Row> CREATOR = new Creator<Row>() {
        @Override
        public Row createFromParcel(Parcel in) {
            return new Row(in);
        }

        @Override
        public Row[] newArray(int size) {
            return new Row[size];
        }
    };

    public String getShopName() {
        return shopName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getTelNumber() {
        return telNumber;
    }

    public String getLocationAddress() {
        return locationAddress;
    }

    public String getRoadAddress() {
        return roadAddress;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(shopName);
        dest.writeString(categoryName);
        dest.writeString(telNumber);
        dest.writeString(dATASTDDE);
        dest.writeString(locationAddress);
        dest.writeString(roadAddress);
        dest.writeString(rEFINEZIPCD);
        dest.writeString(longitude);
        dest.writeString(latitude);
        dest.writeString(cityCode);
        dest.writeString(cityName);
    }
}