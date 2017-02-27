package com.example.shopkeeper.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Tony on 2017/2/18.
 */

public class Product extends Category implements Parcelable{

    private ArrayList<ProductDescription> mDescription;
    private ArrayList<String> OthersImagesURL;
    private double mPrice;
    private double mTaxe;

    public Product( int mId, int mParentId, String mName,double mPrice,double mTaxe, String mImageURL){
        super(mId, mParentId, mName, mImageURL);
        this.mPrice = mPrice;
        this.mTaxe = mTaxe;
        mDescription = new ArrayList<>();
        OthersImagesURL = new ArrayList<>();
        this.product = true;
    }

    public Product( int mId, int mParentId, String mName,double mPrice,double mTaxe, String mImageURL, String mainDescription){
        super(mId, mParentId, mName, mImageURL);
        this.mPrice = mPrice;
        this.mTaxe = mTaxe;
        mDescription = new ArrayList<>();
        OthersImagesURL = new ArrayList<>();
        this.product = true;
        AddProductDescription(new ProductDescription("Description :",mainDescription));
    }

    // "De-parcel object
    public Product(Parcel in)
    {
        super(in);
        mPrice = in.readDouble();
        mTaxe = in.readDouble();
        mDescription = new ArrayList<>();
        OthersImagesURL = new ArrayList<>();
        in.readTypedList(mDescription,ProductDescription.CREATOR);
        in.readStringList(OthersImagesURL);
        this.product = true;
    }

    // Creator
    public static final Parcelable.Creator CREATOR
            = new Parcelable.Creator() {
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeDouble(mPrice);
        dest.writeDouble(mTaxe);
        dest.writeTypedList(mDescription);
        dest.writeStringList(OthersImagesURL);
    }

    public void AddImage(String ImageUrl)
    {
        OthersImagesURL.add(ImageUrl);
    }

    public void RemoveImage(int i)
    {
        OthersImagesURL.remove(i);
    }

    public void SetImageAsMainImage(int i)
    {
        mImageURL = OthersImagesURL.get(i);
    }

    public int GetOthersImagesSize()
    {
        return OthersImagesURL.size();
    }

    public String GetOthersImages(int i)
    {
        return OthersImagesURL.get(i);
    }

    public ProductDescription getmDescription(int i) {
        return mDescription.get(i);
    }

    public int GetProductDescriptionSize()
    {
        return mDescription.size();
    }

    public void AddProductDescription(ProductDescription data)
    {
        mDescription.add(data);
    }

    public void RemoveProductDescription(int i)
    {
        mDescription.remove(i);
    }

    @Override
    public double getmPrice() {
        return mPrice;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public double getmTaxe() {
        return mTaxe;
    }

    public void setmTaxe(double mtaxe) {
        this.mTaxe = mtaxe;
    }

    public void setmPrice(double mPrice) {
        this.mPrice = mPrice;
    }
}
