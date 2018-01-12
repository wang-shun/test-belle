package com.wonhigh.bs.sso.admin.common.model;

import java.io.Serializable;
import java.util.Date;

/**
 * 请写出类的用途
 * 
 * @author user
 * @date 2016-05-26 14:35:07
 * @version 1.0.0
 * @copyright (C) 2013 WonHigh Information Technology Co.,Ltd All Rights
 *            Reserved.
 * 
 *            The software for the WonHigh technology development, without the
 *            company's written consent, and any other individuals and
 *            organizations shall not be used, Copying, Modify or distribute the
 *            software.
 * 
 */
public class OrgUnit implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer unitId;
	private String unitCode;

	/**
     * 
     */
	private Integer categoryId;

	/**
     * 
     */
	private Integer orgStatus;

	/**
     * 
     */
	private Integer unitLevelId;

	private String unitLevelName;
	
	private String createUser;

	/**
     * 
     */
	private String name;

	private String enName;

	/**
     * 
     */
	private String fullName;

	/**
     * 
     */
	private Integer complement;

	private Integer sumComplement;
	/**
     * 
     */
	private Integer totalEmployeeCount;

	/**
     * 
     */
	private String createPerson;

	/**
     * 
     */
	private Date createTime;

	/**
     * 
     */
	private String effectiveTime;

	/**
     * 
     */
	private String endTime;

	/**
     * 
     */
	private Integer sort;

	private String orgName;

	private Integer parentId;

	/**
     * 
     */
	private String costCenter;

	/**
     * 
     */
	private String province;

	/**
     * 
     */
	private String companyNo;

	/**
     * 
     */
	private String companyName;

	/**
     * 
     */
	private String region;

	/**
     * 
     */
	private Integer delflag;
	/**
     * 
     */
	private String distributeCode;

	/**
     * 
     */
	private String shopProperty;

	/**
     * 
     */
	private String shopType;

	/**
     * 
     */
	private String shopLocation;

	/**
     * 
     */
	private Integer shopCapacity;

	/**
     * 
     */
	private String shopDate;

	/**
     * 
     */
	private String drawalDate;

	/**
     * 
     */
	private String storeName;
	/**
     * 
     */
	private String parentUnitId;

	private String parentUnitCode;

	/**
     * 
     */
	private String parentUnitName;

	/**
     * 
     */
	private Integer parentUnitLevelId;
	/**
     * 
     */
	private String parentUnitPerson;

	/**
     * 
     */
	private String topUnitId;

	/**
     * 
     */
	private String topUnitName;

	/**
     * 
     */
	private Integer topUnitLevelId;

	/**
     * 
     */
	private String topUnitPerson;
	/**
     * 
     */
	private String positionCode;

	/**
     * 
     */
	private String isAllowExceed;

	/**
     * 
     */
	private String positionName;

	/**
     * 
     */
	private Integer jobId;

	/**
     * 
     */
	private Integer positionType;

	/**
     * 
     */
	private Integer number;

	/**
     * 
     */
	private Integer isPrincipal;

	/**
     * 
     */
	private Integer isDelete;

	/**
     * 
     */
	private String employeeName;

	private Integer jobGradeId;

	private String shopNo;
	private Integer unitTypeId;

	private String unitTypeName;

	private String marketName;
	/**
     * 
     */
	private Integer costCenterId;
	/**
     * 
     */
	private Integer orgType;

	/**
     * 
     */
	private String functionalProperty;

	/**
     * 
     */
	private Integer functionalPropertyId;

	/**
     * 
     */
	private String cityNo;
	/**
     * 
     */
	private String locationCity;

	private String zoneNo;
	private String zoneName;

	/**
     * 
     */
	private String provinceNo;
	/**
     * 
     */
	private String provinceName;

	/**
     * 
     */
	private String managerCity;
	/**
     * 
     */
	private String managerCityNo;

	/**
     * 
     */
	private String bizCity;
	/**
     * 
     */
	private String bizCityNo;

	/**
     * 
     */
	private String opertateCity;

	/**
     * 
     */
	private String centerCityId;

	/**
     * 
     */
	private String centerCity;

	/**
     * 
     */
	private String brand;

	/**
     * 
     */
	private String otherBrand;

	/**
     * 
     */
	private String bussinessType;

	/**
     * 
     */
	private Integer bussinessTypeId;

	/**
     * 
     */
	private String address;

	/**
	 * 员工编号
	 */
	private Integer employeeCode;

	private Integer orgPropertyId;
	private Integer shopPropertyId;
	private Integer membershipId;
	private Integer id;

	private Integer chargeEmployeeId;

	private String chargeEmployeeName;

	private String parentName;

	private String storeCode;

	private String jobName;

	private Date iEntryDate;

	private String url;

	private Integer acturyCompCount;
	private Integer sumActuryCompCount;
	private String positionFunctionName;
	private String positionTypeName;
	private String storeLevel;

	private Integer oaId;

	public String getEffectiveTime() {
		return effectiveTime;
	}

	public String getEndTime() {
		return endTime;
	}

	/**
	 * 
	 * {@linkplain #categoryId}
	 * 
	 * @return the value of org_unit.Category_ID
	 */
	public Integer getCategoryId() {
		return categoryId;
	}

	/**
	 * 
	 * {@linkplain #categoryId}
	 * 
	 * @param categoryId
	 *            the value for org_unit.Category_ID
	 */
	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}

	public Integer getOrgStatus() {
		return orgStatus;
	}

	public void setOrgStatus(Integer orgStatus) {
		this.orgStatus = orgStatus;
	}

	/**
	 * 
	 * {@linkplain #name}
	 * 
	 * @return the value of org_unit.Name
	 */
	public String getName() {
		return name;
	}

	/**
	 * 
	 * {@linkplain #name}
	 * 
	 * @param name
	 *            the value for org_unit.Name
	 */
	public void setName(String name) {
		this.name = name;
	}

	public String getEnName() {
		return enName;
	}

	public void setEnName(String enName) {
		this.enName = enName;
	}

	/**
	 * 
	 * {@linkplain #fullName}
	 * 
	 * @return the value of org_unit.Full_Name
	 */
	public String getFullName() {
		return fullName;
	}

	/**
	 * 
	 * {@linkplain #fullName}
	 * 
	 * @param fullName
	 *            the value for org_unit.Full_Name
	 */
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	/**
	 * 
	 * {@linkplain #complement}
	 * 
	 * @return the value of org_unit.complement
	 */
	public Integer getComplement() {
		return complement;
	}

	/**
	 * 
	 * {@linkplain #complement}
	 * 
	 * @param complement
	 *            the value for org_unit.complement
	 */
	public void setComplement(Integer complement) {
		this.complement = complement;
	}

	/**
	 * 
	 * {@linkplain #totalEmployeeCount}
	 * 
	 * @return the value of org_unit.totalEmployeeCount
	 */
	public Integer getTotalEmployeeCount() {
		return totalEmployeeCount;
	}

	/**
	 * 
	 * {@linkplain #totalEmployeeCount}
	 * 
	 * @param orgCount
	 *            the value for org_unit.totalEmployeeCount
	 */
	public void setTotalEmployeeCount(Integer totalEmployeeCount) {
		this.totalEmployeeCount = totalEmployeeCount;
	}

	/**
	 * 
	 * {@linkplain #createPerson}
	 * 
	 * @return the value of org_unit.create_person
	 */
	public String getCreatePerson() {
		return createPerson;
	}

	/**
	 * 
	 * {@linkplain #createPerson}
	 * 
	 * @param createPerson
	 *            the value for org_unit.create_person
	 */
	public void setCreatePerson(String createPerson) {
		this.createPerson = createPerson;
	}

	/**
	 * 
	 * {@linkplain #createTime}
	 * 
	 * @return the value of org_unit.Create_Time
	 */
	public Date getCreateTime() {
		return createTime;
	}

	/**
	 * 
	 * {@linkplain #createTime}
	 * 
	 * @param createTime
	 *            the value for org_unit.Create_Time
	 */
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	/**
	 * 
	 * {@linkplain #sort}
	 * 
	 * @return the value of org_unit.sort
	 */
	public Integer getSort() {
		return sort;
	}

	/**
	 * 
	 * {@linkplain #sort}
	 * 
	 * @param sort
	 *            the value for org_unit.sort
	 */
	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public Integer getDelflag() {
		return delflag;
	}

	public void setDelflag(Integer delflag) {
		this.delflag = delflag;
	}

	public Integer getUnitId() {
		return unitId;
	}

	public void setUnitId(Integer unitId) {

		this.unitId = unitId;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	public String getCostCenter() {
		return costCenter;
	}

	public void setCostCenter(String costCenter) {
		this.costCenter = costCenter;
	}

	public Integer getOrgType() {
		return orgType;
	}

	public void setOrgType(Integer orgType) {
		this.orgType = orgType;
	}

	public String getFunctionalProperty() {
		return functionalProperty;
	}

	public void setFunctionalProperty(String functionalProperty) {
		this.functionalProperty = functionalProperty;
	}

	public String getLocationCity() {
		return locationCity;
	}

	public void setLocationCity(String locationCity) {
		this.locationCity = locationCity;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getOpertateCity() {
		return opertateCity;
	}

	public void setOpertateCity(String opertateCity) {
		this.opertateCity = opertateCity;
	}

	public String getCenterCity() {
		return centerCity;
	}

	public void setCenterCity(String centerCity) {
		this.centerCity = centerCity;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getOtherBrand() {
		return otherBrand;
	}

	public void setOtherBrand(String otherBrand) {
		this.otherBrand = otherBrand;
	}

	public String getBussinessType() {
		return bussinessType;
	}

	public void setBussinessType(String bussinessType) {
		this.bussinessType = bussinessType;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getDistributeCode() {
		return distributeCode;
	}

	public void setDistributeCode(String distributeCode) {
		this.distributeCode = distributeCode;
	}

	public String getShopProperty() {
		return shopProperty;
	}

	public void setShopProperty(String shopProperty) {
		this.shopProperty = shopProperty;
	}

	public String getShopType() {
		return shopType;
	}

	public void setShopType(String shopType) {
		this.shopType = shopType;
	}

	public String getShopLocation() {
		return shopLocation;
	}

	public void setShopLocation(String shopLocation) {
		this.shopLocation = shopLocation;
	}

	public Integer getShopCapacity() {
		return shopCapacity;
	}

	public void setShopCapacity(Integer shopCapacity) {
		this.shopCapacity = shopCapacity;
	}

	public String getStoreName() {
		return storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}

	public String getParentUnitId() {
		return parentUnitId;
	}

	public void setParentUnitId(String parentUnitId) {
		this.parentUnitId = parentUnitId;
	}

	public String getParentUnitName() {
		return parentUnitName;
	}

	public void setParentUnitName(String parentUnitName) {
		this.parentUnitName = parentUnitName;
	}

	public String getParentUnitPerson() {
		return parentUnitPerson;
	}

	public void setParentUnitPerson(String parentUnitPerson) {
		this.parentUnitPerson = parentUnitPerson;
	}

	public String getTopUnitId() {
		return topUnitId;
	}

	public void setTopUnitId(String topUnitId) {
		this.topUnitId = topUnitId;
	}

	public String getTopUnitName() {
		return topUnitName;
	}

	public void setTopUnitName(String topUnitName) {
		this.topUnitName = topUnitName;
	}

	public String getTopUnitPerson() {
		return topUnitPerson;
	}

	public void setTopUnitPerson(String topUnitPerson) {
		this.topUnitPerson = topUnitPerson;
	}

	public String getPositionCode() {
		return positionCode;
	}

	public void setPositionCode(String positionCode) {
		this.positionCode = positionCode;
	}

	public String getIsAllowExceed() {
		return isAllowExceed;
	}

	public void setIsAllowExceed(String isAllowExceed) {
		this.isAllowExceed = isAllowExceed;
	}

	public String getPositionName() {
		return positionName;
	}

	public void setPositionName(String positionName) {
		this.positionName = positionName;
	}

	public Integer getJobId() {
		return jobId;
	}

	public void setJobId(Integer jobId) {
		this.jobId = jobId;
	}

	public Integer getPositionType() {
		return positionType;
	}

	public void setPositionType(Integer positionType) {
		this.positionType = positionType;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public Integer getIsPrincipal() {
		return isPrincipal;
	}

	public void setIsPrincipal(Integer isPrincipal) {
		this.isPrincipal = isPrincipal;
	}

	public Integer getIsDelete() {
		return isDelete;
	}

	public void setIsDelete(Integer isDelete) {
		this.isDelete = isDelete;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public Integer getJobGradeId() {
		return jobGradeId;
	}

	public void setJobGradeId(Integer jobGradeId) {
		this.jobGradeId = jobGradeId;
	}

	public String getShopNo() {
		return shopNo;
	}

	public void setShopNo(String shopNo) {
		this.shopNo = shopNo;
	}

	public String getUnitCode() {
		return unitCode;
	}

	public void setUnitCode(String unitCode) {
		this.unitCode = unitCode;
	}

	public Integer getUnitTypeId() {
		return unitTypeId;
	}

	public void setUnitTypeId(Integer unitTypeId) {
		this.unitTypeId = unitTypeId;
	}

	public String getUnitTypeName() {
		return unitTypeName;
	}

	public void setUnitTypeName(String unitTypeName) {
		this.unitTypeName = unitTypeName;
	}

	public String getMarketName() {
		return marketName;
	}

	public void setMarketName(String marketName) {
		this.marketName = marketName;
	}

	public Integer getCostCenterId() {
		return costCenterId;
	}

	public void setCostCenterId(Integer costCenterId) {
		this.costCenterId = costCenterId;
	}

	public Integer getFunctionalPropertyId() {
		return functionalPropertyId;
	}

	public void setFunctionalPropertyId(Integer functionalPropertyId) {
		this.functionalPropertyId = functionalPropertyId;
	}

	public String getCityNo() {
		return cityNo;
	}

	public void setCityNo(String cityNo) {
		this.cityNo = cityNo;
	}

	public String getZoneNo() {
		return zoneNo;
	}

	public void setZoneNo(String zoneNo) {
		this.zoneNo = zoneNo;
	}

	public String getZoneName() {
		return zoneName;
	}

	public void setZoneName(String zoneName) {
		this.zoneName = zoneName;
	}

	public String getProvinceNo() {
		return provinceNo;
	}

	public void setProvinceNo(String provinceNo) {
		this.provinceNo = provinceNo;
	}

	public String getProvinceName() {
		return provinceName;
	}

	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}

	public String getManagerCity() {
		return managerCity;
	}

	public void setManagerCity(String managerCity) {
		this.managerCity = managerCity;
	}

	public String getManagerCityNo() {
		return managerCityNo;
	}

	public void setManagerCityNo(String managerCityNo) {
		this.managerCityNo = managerCityNo;
	}

	public String getBizCity() {
		return bizCity;
	}

	public void setBizCity(String bizCity) {
		this.bizCity = bizCity;
	}

	public String getBizCityNo() {
		return bizCityNo;
	}

	public void setBizCityNo(String bizCityNo) {
		this.bizCityNo = bizCityNo;
	}

	public String getCenterCityId() {
		return centerCityId;
	}

	public void setCenterCityId(String centerCityId) {
		this.centerCityId = centerCityId;
	}

	public Integer getBussinessTypeId() {
		return bussinessTypeId;
	}

	public void setBussinessTypeId(Integer bussinessTypeId) {
		this.bussinessTypeId = bussinessTypeId;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Integer getEmployeeCode() {
		return employeeCode;
	}

	public void setEmployeeCode(Integer employeeCode) {
		this.employeeCode = employeeCode;
	}

	public Integer getOrgPropertyId() {
		return orgPropertyId;
	}

	public void setOrgPropertyId(Integer orgPropertyId) {
		this.orgPropertyId = orgPropertyId;
	}

	public Integer getShopPropertyId() {
		return shopPropertyId;
	}

	public void setShopPropertyId(Integer shopPropertyId) {
		this.shopPropertyId = shopPropertyId;
	}

	public Integer getMembershipId() {
		return membershipId;
	}

	public void setMembershipId(Integer membershipId) {
		this.membershipId = membershipId;
	}

	public void setEffectiveTime(String effectiveTime) {
		this.effectiveTime = effectiveTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getShopDate() {
		return shopDate;
	}

	public void setShopDate(String shopDate) {
		this.shopDate = shopDate;
	}

	public String getDrawalDate() {
		return drawalDate;
	}

	public void setDrawalDate(String drawalDate) {
		this.drawalDate = drawalDate;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getChargeEmployeeId() {
		return chargeEmployeeId;
	}

	public void setChargeEmployeeId(Integer chargeEmployeeId) {
		this.chargeEmployeeId = chargeEmployeeId;
	}

	public String getChargeEmployeeName() {
		return chargeEmployeeName;
	}

	public void setChargeEmployeeName(String chargeEmployeeName) {
		this.chargeEmployeeName = chargeEmployeeName;
	}

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	public String getStoreCode() {
		return storeCode;
	}

	public void setStoreCode(String storeCode) {
		this.storeCode = storeCode;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public Date getiEntryDate() {
		return iEntryDate;
	}

	public void setiEntryDate(Date iEntryDate) {
		this.iEntryDate = iEntryDate;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getParentUnitCode() {
		return parentUnitCode;
	}

	public void setParentUnitCode(String parentUnitCode) {
		this.parentUnitCode = parentUnitCode;
	}

	public Integer getUnitLevelId() {
		return unitLevelId;
	}

	public void setUnitLevelId(Integer unitLevelId) {
		this.unitLevelId = unitLevelId;
	}

	public String getUnitLevelName() {
		return unitLevelName;
	}

	public void setUnitLevelName(String unitLevelName) {
		this.unitLevelName = unitLevelName;
	}

	public Integer getParentUnitLevelId() {
		return parentUnitLevelId;
	}

	public void setParentUnitLevelId(Integer parentUnitLevelId) {
		this.parentUnitLevelId = parentUnitLevelId;
	}

	public Integer getTopUnitLevelId() {
		return topUnitLevelId;
	}

	public void setTopUnitLevelId(Integer topUnitLevelId) {
		this.topUnitLevelId = topUnitLevelId;
	}

	public Integer getActuryCompCount() {
		return acturyCompCount;
	}

	public void setActuryCompCount(Integer acturyCompCount) {
		this.acturyCompCount = acturyCompCount;
	}

	public String getCompanyNo() {
		return companyNo;
	}

	public void setCompanyNo(String companyNo) {
		this.companyNo = companyNo;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getPositionFunctionName() {
		return positionFunctionName;
	}

	public void setPositionFunctionName(String positionFunctionName) {
		this.positionFunctionName = positionFunctionName;
	}

	public String getPositionTypeName() {
		return positionTypeName;
	}

	public void setPositionTypeName(String positionTypeName) {
		this.positionTypeName = positionTypeName;
	}

	public String getStoreLevel() {
		return storeLevel;
	}

	public void setStoreLevel(String storeLevel) {
		this.storeLevel = storeLevel;
	}

	public Integer getSumComplement() {
		return sumComplement;
	}

	public void setSumComplement(Integer sumComplement) {
		this.sumComplement = sumComplement;
	}

	public Integer getSumActuryCompCount() {
		return sumActuryCompCount;
	}

	public void setSumActuryCompCount(Integer sumActuryCompCount) {
		this.sumActuryCompCount = sumActuryCompCount;
	}

	/**
	 * 组织负责人
	 */
	private Integer orgOwnerEmployeeId;
	private String orgOwnerEmployeeName;

	public Integer getOrgOwnerEmployeeId() {
		return orgOwnerEmployeeId;
	}

	public void setOrgOwnerEmployeeId(Integer orgOwnerEmployeeId) {
		this.orgOwnerEmployeeId = orgOwnerEmployeeId;
	}

	public String getOrgOwnerEmployeeName() {
		return orgOwnerEmployeeName;
	}

	public void setOrgOwnerEmployeeName(String orgOwnerEmployeeName) {
		this.orgOwnerEmployeeName = orgOwnerEmployeeName;
	}

	/**
	 * 总实际编制人数
	 */

	private Integer acturyTotalempCount;

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public Integer getActuryTotalempCount() {
		return acturyTotalempCount;
	}

	public void setActuryTotalempCount(Integer acturyTotalempCount) {
		this.acturyTotalempCount = acturyTotalempCount;
	}

	public Integer getOaId() {
		return oaId;
	}

	public void setOaId(Integer oaId) {
		this.oaId = oaId;
	}
}