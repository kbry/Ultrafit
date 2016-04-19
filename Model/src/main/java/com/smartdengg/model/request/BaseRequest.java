package com.smartdengg.model.request;

import com.smartdengg.common.Constants;
import com.smartdengg.ultrafit.annotation.Argument;

/**
 * Created by Joker on 2016/3/28.
 */
public class BaseRequest {

    @Argument(parameter = "key") private String appKey = Constants.APP_KEY;
    @Argument private String dtype = "json";
}