package it.nextre.corsojava.dao.jdbc;

import java.util.ArrayList;
import java.util.List;

public class PagedResult<T> {
	private List<T> content=new ArrayList<T>();
	private int totalElement;
	private int totalPage;
	private int pageSize;
	
	public PagedResult(int pageSize,List<T> content,int totalElement) {
		this.pageSize=pageSize;
		this.totalElement=totalElement;
		this.totalPage=totalElement/pageSize;
		this.content=content;
	}

	public PagedResult() {
	}

	public List<T> getContent() {
		return content;
	}

	public void setContent(List<T> content) {
		this.content = content;
	}

	public int getTotalElement() {
		return totalElement;
	}

	public void setTotalElement(int totalElement) {
		this.totalElement = totalElement;
	}

	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	
	
	public static <T, K> PagedResult<K>  copy(PagedResult<T> old) {
		PagedResult<K> pagedResult = new PagedResult<K>();
		pagedResult.setPageSize(old.pageSize);
		pagedResult.setTotalElement(old.totalElement);
		pagedResult.setTotalPage(old.totalPage);
		return pagedResult;
	}
	
	@Override
	public String toString() {
		StringBuilder sBuilder=new StringBuilder();
		sBuilder.append("content : ");
		for(T elem:content) {
			sBuilder.append("/n "+elem);
		}
		sBuilder.append("/n pagSize : "+pageSize).append("/n totalPag : "+totalPage).append("/n totalElement : "+totalElement);
		return sBuilder.toString();
	}
	
	
	
	
}
