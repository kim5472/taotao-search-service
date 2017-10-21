package com.taotao.search.listener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;

import com.taotao.common.pojo.SearchItem;
import com.taotao.search.mapper.SearchItemMapper;

/**
 * 监听商品添加时间，同步索引库
 * @author Administrator
 *
 */

public class ItemAddMessageListener implements MessageListener{

	@Autowired
	private SearchItemMapper searchItemMapper;
	@Autowired
	private SolrServer solrServer;
	
	@Override
	public void onMessage(Message message) {
		//从消息中取商品id
		TextMessage textMessage = (TextMessage)message;
		try {
			String text = textMessage.getText();
			long itemId = Long.parseLong(text);
			//根据商品id查询数据，取商品消息
			//等待事务提交
			Thread.sleep(1000);
			//创建文档对象
			SearchItem searchItem = searchItemMapper.getItemById(itemId);
			//向文档中添加域
			SolrInputDocument document = new SolrInputDocument();
			document.addField("id", searchItem.getId());
			document.addField("item_title", searchItem.getTitle());
			document.addField("item_sell_point", searchItem.getSell_point());
			document.addField("item_price", searchItem.getPrice());
			document.addField("item_image", searchItem.getImage());
			document.addField("item_category_name", searchItem.getCategory_name());
			document.addField("item_desc", searchItem.getItem_desc());
			//把文件对象写入索引库
			solrServer.add(document);
			//提交
			solrServer.commit();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
