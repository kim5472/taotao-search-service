package com.taotao.search.service.impl;

import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.springframework.beans.factory.annotation.Autowired;

import com.taotao.common.pojo.SearchResult;
import com.taotao.search.dao.SearchDao;
import com.taotao.search.service.SearchService;

public class SearchServiceImpl implements SearchService{
	@Autowired
	private SearchDao searchDao;
	@Override
	public SearchResult search(String queryString, int page, int rows) {
		
		//根据查询条件拼装查询对象
		//创建一个SolrQuery对象
		SolrQuery query = new SolrQuery();
		//设置分页
		if (page<1)page=1;
		query.setStart((page-1)*rows);
		if(rows<1)rows=10;
		query.setRows(rows);
		//设置默认搜索域
		query.set("df", "item_title");
		// 设置高亮
		query.setHighlight(true);
		//设置高亮显示
		query.addHighlightField("item_title");
		query.setHighlightSimplePre("<font color='red'>");
		query.setHighlightSimplePost("</font>");
		//调用dao执行查询
		SearchResult searchResult = null;
		try {
			searchResult = searchDao.search(query);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		long recordCount = searchResult.getRecordCount();
		long pages = recordCount / rows;
		if (recordCount % rows>0) {
			pages++;
		}
		searchResult.setTotalPages(pages);
		
		
		// TODO Auto-generated method stub
		return searchResult;
	}

}
