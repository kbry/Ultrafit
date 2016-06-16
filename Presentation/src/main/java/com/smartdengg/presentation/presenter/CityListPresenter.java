package com.smartdengg.presentation.presenter;

import com.smartdengg.presentation.views.ViewInterface;

/**
 * Created by SmartDengg on 2016/2/22.
 */
public interface CityListPresenter<T> extends Presenter<ViewInterface<T>> {

  void loadData();
}
