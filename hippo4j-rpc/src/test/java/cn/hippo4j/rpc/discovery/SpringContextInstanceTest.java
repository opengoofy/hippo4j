package cn.hippo4j.rpc.discovery;

import cn.hippo4j.common.config.ApplicationContextHolder;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = {InstanceModel.class, ApplicationContextHolder.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class SpringContextInstanceTest {

    Instance instance = new SpringContextInstance();

    @Test
    public void getInstance() {
        Object obj = instance.getInstance(InstanceModel.class);
        Assert.assertNotNull(obj);
        Assert.assertEquals(obj.getClass(), InstanceModel.class);
    }

    @Test
    public void testGetInstance() {
        Object obj = instance.getInstance("instanceModel");
        Assert.assertNotNull(obj);
        Assert.assertEquals(obj.getClass(), InstanceModel.class);
    }
}