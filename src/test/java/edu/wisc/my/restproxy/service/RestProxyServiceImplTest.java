/**
 * 
 */
package edu.wisc.my.restproxy.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.servlet.HandlerMapping;

import edu.wisc.my.restproxy.ProxyRequestContext;
import edu.wisc.my.restproxy.dao.RestProxyDao;

/**
 * Tests for {@link RestProxyServiceImpl}.
 * 
 * @author Nicholas Blair
 */
@RunWith(MockitoJUnitRunner.class)
public class RestProxyServiceImplTest {

  private MockEnvironment env = new MockEnvironment();
  @Mock private RestProxyDao proxyDao;
  @InjectMocks private RestProxyServiceImpl proxy = new RestProxyServiceImpl();
 
  @Before
  public void setup() {
      proxy.setEnv(env);
  }
  /**
   * Control experiment for {@link RestProxyServiceImpl#proxyRequest(String, HttpServletRequest)}, confirms
   * expected behavior for successful, simple request.
   */
  @Test
  public void proxyRequest_control() {
    final Object result = new Object();
    
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setMethod("GET");
    request.setAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE, "/control/foo");
    env.setProperty("control.uri", "http://destination");

    //note the resourceKey ('control' in this context) is stripped from the uri
    ProxyRequestContext expected = new ProxyRequestContext("control").setUri("http://destination/foo");
    
    when(proxyDao.proxyRequest(expected)).thenReturn(result);
    assertEquals(result, proxy.proxyRequest("control", request));
  }
  
  /**
   * Experiment for {@link RestProxyServiceImpl#proxyRequest(String, HttpServletRequest)}, confirms
   * expected behavior when the configuration contains credentials.
   */
  @Test
  public void proxyRequest_withCredentials() {
    final Object result = new Object();
    
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setMethod("GET");
    env.setProperty("withCredentials.uri", "http://localhost/foo");
    env.setProperty("withCredentials.username", "user");
    env.setProperty("withCredentials.password", "pass");
    ProxyRequestContext expected = new ProxyRequestContext("withCredentials").setUri("http://localhost/foo")
        .setUsername("user").setPassword("pass");
    
    when(proxyDao.proxyRequest(expected)).thenReturn(result);
    assertEquals(result, proxy.proxyRequest("withCredentials", request));
  }
  
  /**
   * Experiment for {@link RestProxyServiceImpl#proxyRequest(String, HttpServletRequest)}, confirms
   * expected behavior when the configuration contains a request for additional headers with static values.
   */
  @Test
  public void proxyRequest_withAdditionalHeader() {
    final Object result = new Object();
    
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setAttribute("wiscedupvi", "UW111A111");
    request.setMethod("GET");
    env.setProperty("withAdditionalHeaders.uri", "http://localhost/foo");
    env.setProperty("withAdditionalHeaders.proxyHeaders", "Some-Header: staticvalue");
    ProxyRequestContext expected = new ProxyRequestContext("withAdditionalHeaders").setUri("http://localhost/foo");
    expected.getHeaders().put("Some-Header", "staticvalue");
    
    when(proxyDao.proxyRequest(expected)).thenReturn(result);
    assertEquals(result, proxy.proxyRequest("withAdditionalHeaders", request));
  }
  /**
   * Experiment for {@link RestProxyServiceImpl#proxyRequest(String, HttpServletRequest)}, confirms
   * expected behavior when the configuration contains a request for additional headers, the values
   * containing placeholders.
   */
  @Test
  public void proxyRequest_withAdditionalHeaders_andPlaceholders() {
    final Object result = new Object();
    
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setAttribute("wiscedupvi", "UW111A111");
    request.setMethod("GET");
    env.setProperty("withAdditionalHeaders2.uri", "http://localhost/foo");
    env.setProperty("withAdditionalHeaders2.proxyHeaders", "On-Behalf-Of: {wiscedupvi}");
    ProxyRequestContext expected = new ProxyRequestContext("withAdditionalHeaders2").setUri("http://localhost/foo");
    expected.getHeaders().put("On-Behalf-Of", "UW111A111");
    
    when(proxyDao.proxyRequest(expected)).thenReturn(result);
    assertEquals(result, proxy.proxyRequest("withAdditionalHeaders2", request));
  }
  
  /**
   * Experiment for {@link RestProxyServiceImpl#proxyRequest(String, HttpServletRequest)}, confirms
   * expected behavior when the request path contains additional fragments.
   */
  @Test
  public void proxyRequest_withAdditionalPath() {
    final Object result = new Object();
    
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setMethod("GET");
    request.setAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE, "api/v2/employee/123");
    env.setProperty("withAdditionalPath.uri", "http://localhost/foo");
    ProxyRequestContext expected = new ProxyRequestContext("withAdditionalPath").setUri("http://localhost/foo/api/v2/employee/123");
    
    when(proxyDao.proxyRequest(expected)).thenReturn(result);
    assertEquals(result, proxy.proxyRequest("withAdditionalPath", request));
  }
}
