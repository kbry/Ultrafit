package com.smartdengg.ultra;

import com.smartdengg.ultra.annotation.HttpGet;
import com.smartdengg.ultra.annotation.HttpPost;

/**
 * 创建时间: 2017/03/23 12:13 <br>
 * 作者: dengwei <br>
 * 描述:
 */
public class UltraParser<Request> {

  private Request request;

  public static <Request> UltraParser<Request> createWith(Request request) {

    Utils.checkNotNull(request, "request == null");

    Class<?> clazz = request.getClass();
    HttpGet httpGet = clazz.getAnnotation(HttpGet.class);
    HttpPost httpPost = clazz.getAnnotation(HttpPost.class);
    if (httpGet == null && httpPost == null) {
      throw Utils.methodError(clazz, "%s lack of HTTP annotation, neither @HttpGet nor @HttpPost",
          clazz.getName());
    }

    return new UltraParser<>(request);
  }

  private UltraParser(Request request) {
    this.request = request;
  }

  public RequestEntity<Request> parse() {
    return new RequestEntityBuilder<>(request).build();
  }
}
