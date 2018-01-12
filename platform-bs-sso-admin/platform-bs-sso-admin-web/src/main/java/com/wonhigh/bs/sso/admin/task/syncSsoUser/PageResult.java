package com.wonhigh.bs.sso.admin.task.syncSsoUser;

/**
 * TODO: 增加描述
 * 
 * @author user
 * @date 2017年12月7日 上午11:21:17
 * @version 0.1.0 
 * @copyright wonhigh.cn
 */
public class PageResult {
	public int currentPage;
    public int pageSize;
    public int totalPage;
    public int totalSize;
    public int successSize;
    public int failures;
	public int getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public int getTotalPage() {
		return totalPage;
	}
	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}
	public int getTotalSize() {
		return totalSize;
	}
	public void setTotalSize(int totalSize) {
		this.totalSize = totalSize;
	}
	public int getSuccessSize() {
		return successSize;
	}
	public void setSuccessSize(int successSize) {
		this.successSize = successSize;
	}
	public int getFailures() {
		return failures;
	}
	public void setFailures(int failures) {
		this.failures = failures;
	}
}
