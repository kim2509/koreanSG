package com.korea.common;

import org.json.JSONObject;

public class ShopLikeDao {

	private int shopLikeNo;
    private int userNo;
    private int shopNo;
    private String isUse;
    private String createDate;
    private String updateDate;
    private String deleteDate;
    
    public ShopLikeDao()
    {
    	
    }
    
    public ShopLikeDao( JSONObject obj ) throws Exception
    {
    	shopLikeNo = obj.getInt("SHOP_LIKE_NO");
    	userNo = obj.getInt("USER_NO");
    	shopNo = obj.getInt("SHOP_NO");
    	isUse = obj.getString("IS_USE");
    	
    	if ( !JSONObject.NULL.equals( obj.get("CREATE_DATE")))
    		createDate = obj.getString("CREATE_DATE");
    	if ( !JSONObject.NULL.equals( obj.get("UPDATE_DATE")))
    		updateDate = obj.getString("UPDATE_DATE");
    	if ( !JSONObject.NULL.equals( obj.get("DELETE_DATE")))
    		deleteDate = obj.getString("DELETE_DATE");
    }
    
	public int getShopLikeNo() {
		return shopLikeNo;
	}
	public void setShopLikeNo(int shopLikeNo) {
		this.shopLikeNo = shopLikeNo;
	}
	public int getUserNo() {
		return userNo;
	}
	public void setUserNo(int userNo) {
		this.userNo = userNo;
	}
	public int getShopNo() {
		return shopNo;
	}
	public void setShopNo(int shopNo) {
		this.shopNo = shopNo;
	}
	public String getIsUse() {
		return isUse;
	}
	public void setIsUse(String isUse) {
		this.isUse = isUse;
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
    
    
}
