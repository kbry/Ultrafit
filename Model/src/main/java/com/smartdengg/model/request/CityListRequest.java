package com.smartdengg.model.request;

import com.smartdengg.common.Constants;
import com.smartdengg.ultra.annotation.HttpGet;

/**
 * Created by SmartDengg on 2016/2/24.
 */
@HttpGet(stringUrl = Constants.CITY_URL,LOG = true)
public class CityListRequest extends BaseRequest {

}
