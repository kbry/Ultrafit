package com.example.ultrafit.presenter;

import com.example.ultrafit.views.ListView;

/**
 * Created by SmartDengg on 2016/2/22.
 */
public interface MovieListPresenter<T> extends Presenter<ListView<T>> {

  void loadData(String cityId);
}