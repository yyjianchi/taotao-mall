package com.taotao.content.service;

import java.util.List;

import com.taotao.easyuidatagridresult.EasyUIDataGridResult;
import com.taotao.pojo.TbContent;
import com.taotao.taotaoresult.TaotaoResult;

public interface ContentService {

	public EasyUIDataGridResult getContentList(Long categoryId,int page,int rows);
	
	public TaotaoResult addContent(TbContent content) ;
	
	public TaotaoResult updateContent(TbContent content);
	
	public TaotaoResult deleteContent(String ids);
	
	public List<TbContent> getContentList(long cid);
	
}
