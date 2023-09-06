package com.example.capstone.Model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class OutfitModel implements Parcelable {
    // Model class for our outfits

    private String productDisplayName;

    private String image_url;
    private String articleType;
    private String gender;
    private String masterCategory;
    private String season;
    private String subCategory;
    private String usage;
    private int year;

    public OutfitModel(String productDisplayName, String image_url, String articleType, String gender, String masterCategory, String season, String subCategory, String usage, int year) {
        this.productDisplayName = productDisplayName;
        this.image_url = image_url;
        this.articleType = articleType;
        this.gender = gender;
        this.masterCategory = masterCategory;
        this.season = season;
        this.subCategory = subCategory;
        this.usage = usage;
        this.year = year;
    }

    protected OutfitModel(Parcel in) {
        productDisplayName = in.readString();
        image_url = in.readString();
        articleType = in.readString();
        gender = in.readString();
        masterCategory = in.readString();
        season = in.readString();
        subCategory = in.readString();
        usage = in.readString();
        year = in.readInt();
    }

    public static final Creator<OutfitModel> CREATOR = new Creator<OutfitModel>() {
        @Override
        public OutfitModel createFromParcel(Parcel in) {
            return new OutfitModel(in);
        }

        @Override
        public OutfitModel[] newArray(int size) {
            return new OutfitModel[size];
        }
    };

    public String getProductDisplayName() {
        return productDisplayName;
    }

    public String getImage_url() {
        return image_url;
    }

    public String getArticleType() {
        return articleType;
    }

    public String getGender() {
        return gender;
    }

    public String getMasterCategory() {
        return masterCategory;
    }

    public String getSeason() {
        return season;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public String getUsage() {
        return usage;
    }

    public int getYear() {
        return year;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(productDisplayName);
        dest.writeString(image_url);
        dest.writeString(articleType);
        dest.writeString(gender);
        dest.writeString(masterCategory);
        dest.writeString(season);
        dest.writeString(subCategory);
        dest.writeString(usage);
        dest.writeInt(year);
    }

    @Override
    public String toString() {
        return "OutfitModel{" +
                "productDisplayName='" + productDisplayName + '\'' +
                ", image_url='" + image_url + '\'' +
                ", articleType='" + articleType + '\'' +
                ", gender='" + gender + '\'' +
                ", masterCategory='" + masterCategory + '\'' +
                ", season='" + season + '\'' +
                ", subCategory='" + subCategory + '\'' +
                ", usage='" + usage + '\'' +
                ", year=" + year +
                '}';
    }
}
