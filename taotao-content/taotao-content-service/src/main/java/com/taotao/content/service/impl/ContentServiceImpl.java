package com.taotao.content.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.taotao.content.jedis.JedisClient;
import com.taotao.content.service.ContentService;
import com.taotao.easyuidatagridresult.EasyUIDataGridResult;
import com.taotao.jsonutil.JsonUtils;
import com.taotao.mapper.TbContentMapper;
import com.taotao.pojo.TbContent;
import com.taotao.pojo.TbContentExample.Criteria;
import com.taotao.taotaoresult.TaotaoResult;
import com.taotao.pojo.TbContentExample;
@Service
public class ContentServiceImpl implements ContentService {
	@Autowired
	private TbContentMapper tbContentMapper;
	@Override
	public EasyUIDataGridResult getContentList(Long categoryId,int page, int rows) {
		//设置分页信息
		PageHelper.startPage(page, rows);
		//执行查询
		TbContentExample example=new TbContentExample();
		Criteria criteria=example.createCriteria();
		criteria.andCategoryIdEqualTo(categoryId);
		List<TbContent> list=tbContentMapper.selectByExample(example);
		//取分页信息
		PageInfo<TbContent> pageInfo=new PageInfo<>(list);
		EasyUIDataGridResult result=new EasyUIDataGridResult();
		Long total=pageInfo.getTotal();
		
		result.setTotal(new Integer(total.toString()));
		result.setRows(list);
		return result;
	}
	@Override
	public TaotaoResult addContent(TbContent content) {
		//补全属性
		content.setCreated(new Date());
		content.setUpdated(new Date());
		//插入数据
		tbContentMapper.insert(content);
		jedisClient.hdel(CONTENT_KEY, content.getCategoryId().toString());
		return TaotaoResult.ok();
	}
	@Override
	public TaotaoResult updateContent(TbContent content) {
		content.setCreated(new Date());
		content.setUpdated(new Date());
		tbContentMapper.updateByPrimaryKey(content);
		jedisClient.hdel(CONTENT_KEY, content.getCategoryId().toString());
		return TaotaoResult.ok();
		
	}
	@Override
	public TaotaoResult deleteContent(String ids) {
		String [] strings=ids.split(",");
	
		for (String id : strings) {
			TbContent content=tbContentMapper.selectByPrimaryKey(Long.valueOf(id));
			tbContentMapper.deleteByPrimaryKey(Long.valueOf(id));
			jedisClient.hdel(CONTENT_KEY, content.getCategoryId().toString());
		}

		return TaotaoResult.ok();
	}
	
	
	@Autowired
	private JedisClient jedisClient;	
	@Value("${CONTENT_KEY}")
	private String CONTENT_KEY;
	@Override
	public List<TbContent> getContentList(long cid) {
		//查询缓存
		try {
			String json=jedisClient.hget(CONTENT_KEY, cid+"");
			//判断json是否为空
			if (StringUtils.isNotBlank(json)) {
				System.out.println("有缓存！！！");
				List<TbContent> list=JsonUtils.jsonToList(json, TbContent.class);
				return list;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		//根据cid查询内容列表
		TbContentExample example = new TbContentExample();
		//设置查询条件
		Criteria criteria = example.createCriteria();
		criteria.andCategoryIdEqualTo(cid);
		//执行查询
		List<TbContent> list = tbContentMapper.selectByExample(example);
		//查询结果添加到缓存中去
		try {
			System.out.println("没有缓存！！！！！");
			jedisClient.hset(CONTENT_KEY, cid+"", JsonUtils.objectToJson(list));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;	
	}

}
