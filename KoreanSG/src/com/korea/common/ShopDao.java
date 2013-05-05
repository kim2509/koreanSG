package com.korea.common;

import org.json.JSONObject;

public class ShopDao {

	private int seq;
	private String category = "";
	private String shopName = "";
	private String phone = "";
	private String mobile = "";
	private String phone1 = "";
	private String phone2 = "";
	private String address = "";
	private double longitude;
	private double latitude;
	private String email = "";
	private String homepage = "";
	private double metersFromCurrentLocation;
	private String distance = "";
	private boolean bShowPrice;
	private boolean bImageExists;
	private String createDate = "";
	private String updateDate = "";
	private String deleteDate = "";
	private String shopNameEn = "";
	private String categoryEn = "";
	private int nLikes;
	private int nComments;
	
	public ShopDao()
	{
		
	}
	
	public ShopDao( JSONObject obj ) throws Exception
	{
		seq = Integer.parseInt(obj.getString("SHOP_NO"));
        shopName = obj.getString("SHOP_NAME_KR");
        category = obj.getString("CATEGORY_NAME_KR");
        phone = obj.getString("PHONE1");
        phone1 = obj.getString("PHONE2");
        phone2 = obj.getString("PHONE3");
        mobile = obj.getString("MOBILE");
        address = obj.getString("ADDRESS");
        longitude =  Double.parseDouble( obj.getString("LONGITUDE") );
        latitude = Double.parseDouble( obj.getString("LATITUDE") );
        email = obj.getString("EMAIL");
        homepage = obj.getString("HOMEPAGE");
        metersFromCurrentLocation = 0.0;
        distance = "";
        bShowPrice = true;
        bImageExists = false;
        
        if ( !JSONObject.NULL.equals( obj.get("CREATE_DATE")))
    		createDate = obj.getString("CREATE_DATE");
    	if ( !JSONObject.NULL.equals( obj.get("UPDATE_DATE")))
    		updateDate = obj.getString("UPDATE_DATE");
    	if ( !JSONObject.NULL.equals( obj.get("DELETE_DATE")))
    		deleteDate = obj.getString("DELETE_DATE");
	}

	public int getSeq() {
		return seq;
	}
	public void setSeq(int seq) {
		this.seq = seq;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getShopName() {
		return shopName;
	}
	public void setShopName(String shopName) {
		this.shopName = shopName;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getPhone1() {
		return phone1;
	}
	public void setPhone1(String phone1) {
		this.phone1 = phone1;
	}
	public String getPhone2() {
		return phone2;
	}
	public void setPhone2(String phone2) {
		this.phone2 = phone2;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getHomepage() {
		return homepage;
	}
	public void setHomepage(String homepage) {
		this.homepage = homepage;
	}
	public double getMetersFromCurrentLocation() {
		return metersFromCurrentLocation;
	}
	public void setMetersFromCurrentLocation(double metersFromCurrentLocation) {
		this.metersFromCurrentLocation = metersFromCurrentLocation;
	}
	public String getDistance() {
		return distance;
	}
	public void setDistance(String distance) {
		this.distance = distance;
	}
	public boolean isbShowPrice() {
		return bShowPrice;
	}
	public void setbShowPrice(boolean bShowPrice) {
		this.bShowPrice = bShowPrice;
	}
	public boolean isbImageExists() {
		return bImageExists;
	}
	public void setbImageExists(boolean bImageExists) {
		this.bImageExists = bImageExists;
	}
	public String getCreateDate() {
		return createDate;
	}
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
	public String getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(String updateDate) {
		this.updateDate = updateDate;
	}
	public String getDeleteDate() {
		return deleteDate;
	}
	public void setDeleteDate(String deleteDate) {
		this.deleteDate = deleteDate;
	}
	public String getShopNameEn() {
		return shopNameEn;
	}
	public void setShopNameEn(String shopNameEn) {
		this.shopNameEn = shopNameEn;
	}
	public String getCategoryEn() {
		return categoryEn;
	}
	public void setCategoryEn(String categoryEn) {
		this.categoryEn = categoryEn;
	}
	public int getnLikes() {
		return nLikes;
	}
	public void setnLikes(int nLikes) {
		this.nLikes = nLikes;
	}
	public int getnComments() {
		return nComments;
	}
	public void setnComments(int nComments) {
		this.nComments = nComments;
	}
}
