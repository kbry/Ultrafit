package com.smartdengg.ultra;

/**
 * Created by Joker on 2016/6/28.
 */
abstract class UltraHandler<T> {

  abstract void process(RequestEntity<T> requestEntity, T value) throws Exception;
}
