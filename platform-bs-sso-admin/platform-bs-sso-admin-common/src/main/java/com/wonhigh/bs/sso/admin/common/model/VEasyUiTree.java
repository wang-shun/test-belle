package com.wonhigh.bs.sso.admin.common.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * TODO: 增加描述
 * 
 * @author user
 * @date 2017年11月12日 下午4:05:50
 * @version 0.1.0 
 * @copyright wonhigh.cn
 */
public class VEasyUiTree implements Serializable {
	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	 public int id;
	 public int pId;
     public String text; 
     public String state;
     public Boolean open;
     public boolean checked;
     public Object attributes;
     public String iconCls;
     public Integer number;
     public List<VEasyUiTree> children;
     public String unitTypeName;
     public String shopNo;
     public String storenm;
     public String unitCode;
     public Boolean isParent;
     public boolean isLeaf;
     public String getShopNo() {
 		return shopNo;
 	}
    
 	public String getUnitCode() {
		return unitCode;
	}

	public void setUnitCode(String unitCode) {
		this.unitCode = unitCode;
	}

	public void setShopNo(String shopNo) {
 		this.shopNo = shopNo;
 	}
	public String getUnitTypeName() {
		return unitTypeName;
	}
	public void setUnitTypeName(String unitTypeName) {
		this.unitTypeName = unitTypeName;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getPId() {
        return pId;
    }
    public void setPId(int pId) {
        this.pId = pId;
    }
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	
	public boolean isChecked() {
		return checked;
	}
	public void setChecked(boolean checked) {
		this.checked = checked;
	}
	public Object getAttributes() {
		return attributes;
	}
	public void setAttributes(Object attributes) {
		this.attributes = attributes;
	}
	public void setOpen(Boolean open){
	    this.open = open;
	}
	public Boolean getOpen(){
	    return open;
	}
	 public void setIsParent(Boolean isParent){
	       this.isParent = isParent;
	   }
       public Boolean getIsParent(){
           return isParent;
       }
	//添加孩子的方法  
	  
    public Integer getNumber() {
		return number;
	}
	public void setNumber(Integer number) {
		this.number = number;
	}
	public List<VEasyUiTree> getChildren() {
		return children;
	}
	public void setChildren(List<VEasyUiTree> children) {
		this.children = children;
	}
	public   void addChild(VEasyUiTree node){  
       if(this.children == null){  
           children= new ArrayList<VEasyUiTree>();  
           children.add(node);  
       }else{  
           children.add(node);  
       }  
            
    } 
	public void setIconCls(String iconCls){
		this.iconCls = iconCls;
	}
	public String getIconCls(){
		return iconCls;
	}
	public String getStorenm() {
		return storenm;
	}
	public void setStorenm(String storenm) {
		this.storenm = storenm;
	}
	
	public boolean isLeaf() {
		return isLeaf;
	}

	public void setLeaf(boolean isLeaf) {
		this.isLeaf = isLeaf;
	}
}
