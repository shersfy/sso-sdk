package org.young.sso.sdk.test;

import org.junit.Test;
import org.young.sso.sdk.utils.DomainUtil;
import org.young.sso.sdk.utils.SsoRSAUtil;

import com.alibaba.fastjson.JSON;

public class TestCases {
    
    @Test
    public void test01() throws Exception {
    	String data = "hello世界";
    	System.out.println(new String(SsoRSAUtil.encrypt(data.getBytes())));
    }
    
    @Test
    public void test02() throws Exception {
    	Object a = 2.33;
    	System.out.println(JSON.parseObject(JSON.toJSONString(a), double.class));
    }
    
    @Test
    public void test03() throws Exception {
    	System.out.println(DomainUtil.getDomainName("https://localhost"));
    	System.out.println(DomainUtil.getDomainName("https://127.0.0.1"));
    	System.out.println(DomainUtil.getDomainName("https://10.32.15.71"));
    	System.out.println(DomainUtil.getDomainName("https://auth.edmpglobal.com"));
    }
    

}
