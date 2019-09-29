package com.taotao.item.listener;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import com.taotao.item.pojo.Item;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbItemDesc;
import com.taotao.service.ItemService;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class ItemChangeGenHTMLMessageListener implements MessageListener {

	@Autowired
	private FreeMarkerConfigurer config;
	@Autowired
	private ItemService itemService;	
	@Override
	public void onMessage(Message message) {
		// 1.接收消息
		if (message instanceof TextMessage) {
			TextMessage message2 = (TextMessage) message;
			try {
				String text=message2.getText();
				if (StringUtils.isNotBlank(text)){
					// 2.通过接收到的消息转换成商品id ,查询商品的信息
					// 调用商品服务查询商品的信息
					Long itemId=Long.valueOf(text);
					TbItem item=itemService.getItemById(itemId);
					System.out.println(item);
					TbItemDesc itemDesc=itemService.geTbItemDescById(itemId);
					// 3.生成静态页面
					this.genHtml("item.ftl", item, itemDesc);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	// 生成静态页面
	private void genHtml(String templateName, TbItem item, TbItemDesc itemDesc) throws Exception{
		// 从spring容器中获取configuration对象
		Configuration configuration=config.getConfiguration();
		Template template=configuration.getTemplate(templateName);
		// 5.创建模型数据集，设置模型数据一般使用的是map 也可以使用POJO
		Map map = new HashMap<>();
		map.put("item", new Item(item));
		map.put("itemDesc", itemDesc);
		// 6.指定输出的文件的路径，创建FileWriter
		FileWriter writer = new FileWriter(new File("D:/freemarker/item/"+item.getId()+".html"));
		// 7.调用模板对象中的方法进行输出
		// 参数1.指定数据集
		// 参数2.指定生成的文件的全路径（FileWrtier来包装
		template.process(map, writer);
		// 8.流关闭
		writer.close();
		
	}

}
