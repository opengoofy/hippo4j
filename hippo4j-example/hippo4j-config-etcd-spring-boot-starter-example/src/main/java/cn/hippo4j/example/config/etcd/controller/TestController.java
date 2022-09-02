package cn.hippo4j.example.config.etcd.controller;

import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *@author : wh
 *@date : 2022/9/2 19:18
 *@description:
 */
@RestController
@RequestMapping
public class TestController {


	@Autowired
	private ThreadPoolExecutor messageConsumeDynamicExecutor;

	
	@GetMapping("test")
	public void test() {
		System.out.println(messageConsumeDynamicExecutor.getMaximumPoolSize());
	}
	
}
