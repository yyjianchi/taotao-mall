package com.taotao.content.service;

import java.util.List;

import com.taotao.easyuitreenode.EasyUITreeNode;
import com.taotao.taotaoresult.TaotaoResult;

public interface ContentCategoryService {

	public List<EasyUITreeNode> getContentCategoryList(long parentId);
	
	public TaotaoResult addContentCategory(long parentId, String name) ;
	
	public TaotaoResult	renameContentCategory(long id,String name);

	public TaotaoResult	deleteContentCategory(long id);
	
}
