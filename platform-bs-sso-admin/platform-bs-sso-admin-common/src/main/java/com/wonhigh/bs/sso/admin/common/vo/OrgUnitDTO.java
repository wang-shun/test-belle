package com.wonhigh.bs.sso.admin.common.vo;

import java.io.Serializable;

/**
 * 
 * TODO: 机构组织对应数据库表----org_unit
 * 
 * @author zhang.rq
 * @date 2017年11月7日 上午11:47:01
 * @version 0.1.0 
 * @copyright wonhigh.cn
 */
public class OrgUnitDTO extends OrgUnitNew implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 子节点数
     */
    private Integer childNodeCount;

    /**
     * 商铺编码
     */
    private String shopNo;

    /**
     * 区域编码
     */
    private String zoneNo;

    /**
     * 公司编码
     */
    private String companyNo;

    /**
     * 
     * {@linkplain #company}
     *
     * @return the value of org_property.company
     */
    public String getCompanyNo() {
        return companyNo;
    }

    /**
     * 
     * {@linkplain #company}
     * @param company the value for org_property.company
     */
    public void setCompanyNo(String companyNo) {
        this.companyNo = companyNo;
    }

    public String getZoneNo() {
        return zoneNo;
    }

    public void setZoneNo(String zoneNo) {
        this.zoneNo = zoneNo;
    }

    public String getShopNo() {
        return shopNo;
    }

    public void setShopNo(String shopNo) {
        this.shopNo = shopNo;
    }

    public Integer getChildNodeCount() {
        return childNodeCount;
    }

    public void setChildNodeCount(Integer childNodeCount) {
        this.childNodeCount = childNodeCount;
    }

    @Override
    public String toString() {
        return super.toString() + "OrgUnitDTO [childNodeCount=" + childNodeCount + ", shopNo=" + shopNo + ", zoneNo="
                + zoneNo + ", companyNo=" + companyNo + "]";
    }

}
