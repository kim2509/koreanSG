package com.korea.common;

import org.json.JSONObject;

public class ShopCommentDao {

	private int shopCommentNo;
    private int userNo;
    private int shopNo;
    private String comment;
    private String isUse;
    private String createDate;
    private String updateDate;
    private String deleteDate;
    
    public ShopCommentDao()
    {
    	
    }
    
    public ShopCommentDao( JSONObject obj ) throws Exception
    {
    	shopCommentNo = obj.getInt("SHOP_COMMENT_NO");
        userNo = obj.getInt("USER_NO");
        shopNo = obj.getInt("SHOP_NO");
        comment = obj.getString("COMMENT");
        isUse = obj.getString("IS_USE");
        if ( !JSONObject.NULL.equals( obj.get("CREATE_DATE")))
    		createDate = obj.getString("CREATE_DATE");
    	if ( !JSONObject.NULL.equals( obj.get("UPDATE_DATE")))
    		updateDate = obj.getString("UPDATE_DATE");
    	if ( !JSONObject.NULL.equals( obj.get("DELETE_DATE")))
    		deleteDate = obj.getString("DELETE_DATE");
    }
    
	public int getShopCommentNo() {
		return shopCommentNo;
	}
	public void setShopCommentNo(int shopCommentNo) {
		this.shopCommentNo = shopCommentNo;
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
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
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
