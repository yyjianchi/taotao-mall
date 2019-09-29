package com.taotao.sso.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taotao.mapper.TbUserMapper;
import com.taotao.pojo.TbUser;
import com.taotao.pojo.TbUserExample;
import com.taotao.pojo.TbUserExample.Criteria;
import com.taotao.sso.service.UserRegisterService;
import com.taotao.taotaoresult.TaotaoResult;
@Service
public class UserRegisterServiceImpl implements UserRegisterService {
	@Autowired
	private TbUserMapper userMapper;
	@Override
	public TaotaoResult checkData(String param, int type) {
		// 1、从tb_user表中查询数据
		TbUserExample example=new TbUserExample();
		Criteria criteria=example.createCriteria();
		// 2、查询条件根据参数动态生成。
		//1、2、3分别代表username、phone、email
		if (type == 1) {
			criteria.andUsernameEqualTo(param);
		} else if (type == 2) {
			criteria.andPhoneEqualTo(param);
		} else if (type == 3) {
			criteria.andEmailEqualTo(param);
		} else {
			return TaotaoResult.build(400, "非法的参数");
		}
		//执行查询
		
		List<TbUser> list=userMapper.selectByExample(example);
		// 3、判断查询结果，如果查询到数据返回false。
		if (list ==null || list.size()==0) {
			// 4、如果没有返回true。
			return TaotaoResult.ok(true);
		}
		// 5、使用TaotaoResult包装，并返回。
		return TaotaoResult.ok(false);
	}
	@Override
	public TaotaoResult register(TbUser user) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
