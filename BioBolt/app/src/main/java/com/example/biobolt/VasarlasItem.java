package com.example.biobolt;

public class VasarlasItem {
    private String id;
    private String name;
    private String info;
    private String price;
    private float ratedInfo;
    private int imageResource;
    private int cartInCount;

    public VasarlasItem() {}

    public VasarlasItem(String name, String info, String price, float ratedInfo, int imageResource, int cartInCount) {
        this.name = name;
        this.info = info;
        this.price = price;
        this.ratedInfo = ratedInfo;
        this.imageResource = imageResource;
        this.cartInCount = cartInCount;
    }

    public String getName() {
        return name;
    }

    public String getInfo() {
        return info;
    }

    public String getPrice() {
        return price;
    }

    public float getRatedInfo() {
        return ratedInfo;
    }

    public int getImageResource() {
        return imageResource;
    }

    public int getCartInCount() {
        return cartInCount;
    }

    public String _get_ID(){
        return id;
    }

    public void setId(String id){
        this.id = id;
    }
}
