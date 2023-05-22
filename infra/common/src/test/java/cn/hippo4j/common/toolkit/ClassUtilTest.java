package cn.hippo4j.common.toolkit;


import org.junit.Assert;
import org.junit.Test;

/**
 * test for {@link ClassUtil}
 */
public class ClassUtilTest {

    @Test
    public void testGetClassLoader(){
        ClassLoader expectedClassLoader = Thread.currentThread().getContextClassLoader();
        ClassLoader actualClassLoader = ClassUtil.getClassLoader(ClassUtilTest.class);
        Assert.assertEquals(expectedClassLoader, actualClassLoader);

        expectedClassLoader = ClassUtilTest.class.getClassLoader();
        actualClassLoader = ClassUtil.getClassLoader(null);
        Assert.assertEquals(expectedClassLoader, actualClassLoader);

        expectedClassLoader = ClassLoader.getSystemClassLoader();
        actualClassLoader = ClassUtil.getClassLoader(String.class);
        Assert.assertEquals(expectedClassLoader, actualClassLoader);
    }

}
