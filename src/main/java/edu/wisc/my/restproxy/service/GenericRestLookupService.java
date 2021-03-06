package edu.wisc.my.restproxy.service;

import java.util.Map;

/**
 * @deprecated see {@link RestProxyService}
 */
@Deprecated
public interface GenericRestLookupService {
  /**
   * Gets information from the provided key and attributes
   * @param key the key that is used to lookup the endpoint.properties
   * @param attributes the list of attributes to use in the query
   * @return the object that is returned from the service
   */
  public Object getStuff(String key, Map<String, String> attributes);
}
