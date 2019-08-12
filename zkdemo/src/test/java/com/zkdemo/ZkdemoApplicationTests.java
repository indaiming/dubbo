package com.zkdemo;

import com.zkdemo.interfaces.Impl.ZkServiceDiscovery;
import com.zkdemo.interfaces.Impl.ZkServiceRegistryImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ZkdemoApplicationTests {

	private static final String SERVICE_NAME = "daim";

	private static final String SERVER_ADDRESS =  "127.0.0.";

	@Test
	public void contextLoads() {
		ZkServiceRegistryImpl registry = new ZkServiceRegistryImpl();
		registry.init();
		registry.registry(SERVICE_NAME,SERVER_ADDRESS);

		ZkServiceDiscovery discovery = new ZkServiceDiscovery();
		discovery.init();

		while (true){
			discovery.discover(SERVICE_NAME);

		}

	}

}
