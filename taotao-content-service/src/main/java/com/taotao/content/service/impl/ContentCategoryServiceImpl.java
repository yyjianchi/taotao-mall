package com.taotao.content.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taotao.content.service.ContentCategoryService;
import com.taotao.easyuitreenode.EasyUITreeNode;
import com.taotao.mapper.TbContentCategoryMapper;
import com.taotao.pojo.TbContentCategory;
import com.taotao.pojo.TbContentCategoryExample;
import com.taotao.pojo.TbContentCategoryExample.Criteria;
import com.taotao.taotaoresult.TaotaoResult;

@Service
public class ContentCategoryServiceImpl implements ContentCategoryService {
	
	@Autowired 
	private TbContentCategoryMapper contentCategoryMapper;

	@Override
	public List<EasyUITreeNode> getContentCategoryList(long parentId) {
		// 1、取查询参数id，parentId
		// 2、根据parentId查询tb_content_category，查询子节点列表。
		TbContentCategoryExample example=new TbContentCategoryExample();
		//设置查询条件
		Criteria criteria=example.createCriteria();
		criteria.andParentIdEqualTo(parentId);
		//执行查询
		// 3、得到List<TbContentCategory>
		List<TbContentCategory> list=contentCategoryMapper.selectByExample(example);
		// 4、把列表转换成List<EasyUITreeNode>
		List<EasyUITreeNode> resultList=new ArrayList<>();
		for (TbContentCategory tbContentCategory : list) {
			EasyUITreeNode node=new EasyUITreeNode();
			node.setId(tbContentCategory.getId());
			node.setText(tbContentCategory.getName());
			node.setState(tbContentCategory.getIsParent()?"closed":"open");
			//添加到列表
			resultList.add(node);
		}
		return resultList;
	}

	@Override
	public TaotaoResult addContentCategory(long parentId, String name) {
		// 1、接收两个参数：parentId、name
		// 2、向tb_content_category表中插入数据。
		// a)创建一个TbContentCategory对象
		TbContentCategory contentCategory=new TbContentCategory();
		// b)补全TbContentCategory对象的属性
		contentCategory.setIsParent(false);
		contentCategory.setName(name);
		contentCategory.setParentId(parentId);
		//排列序号，表示同级类目的展现次序，如数值相等则按名称次序排列。取值范围:大于零的整数
		contentCategory.setSortOrder(1);
		//状态。可选值:1(正常),2(删除)
		contentCategory.setStatus(1);
		contentCategory.setCreated(new Date());
		contentCategory.setUpdated(new Date());
		// c)向tb_content_category表中插入数据
		contentCategoryMapper.insert(contentCategory);
		
		TbContentCategory category=contentCategoryMapper.selectByPrimaryKey(parentId);
		// 3、判断父节点的isparent是否为true，不是true需要改为true。
		if (!category.getIsParent()) {
			category.setIsParent(true);
			//更新父节点
			contentCategoryMapper.updateByPrimaryKey(category);
		}
		// 4、需要主键返回。
		// 5、返回TaotaoResult，其中包装TbContentCategory对象
		
		return TaotaoResult.ok(contentCategory);
	}

	@Override
	public TaotaoResult renameContentCategory(long id, String name) {
		// 1、接收两个参数：当前节点ID、更改后的名称name
		//	2.根据ID查询内容分类
		TbContentCategory currentCategory=contentCategoryMapper.selectByPrimaryKey(id);
		//	3.修改表中分类名称
		currentCategory.setName(name);
		contentCategoryMapper.updateByPrimaryKey(currentCategory);
		
		
		return TaotaoResult.ok(currentCategory);
	}

	@Override
	public TaotaoResult deleteContentCategory(long id) {
		//1.根据ID查询内容分类
		TbContentCategory currentCategory=contentCategoryMapper.selectByPrimaryKey(id);
		//2.删除当前分类
		contentCategoryMapper.deleteByPrimaryKey(id);
		//3.查询父节点下是否还有子节点
		TbContentCategoryExample example=new TbContentCategoryExample();
		Criteria criteria=example.createCriteria();
		criteria.andParentIdEqualTo(currentCategory.getParentId());
		List<TbContentCategory> list=contentCategoryMapper.selectByExample(example);
		if(list.isEmpty()){
			TbContentCategory parentCategory=contentCategoryMapper.selectByPrimaryKey(currentCategory.getParentId());
			parentCategory.setIsParent(false);
			contentCategoryMapper.updateByPrimaryKey(parentCategory);
		}
		
		return TaotaoResult.ok(currentCategory);
		
	}
	
	
}
