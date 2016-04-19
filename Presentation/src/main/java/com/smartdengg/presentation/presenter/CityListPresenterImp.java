package com.smartdengg.presentation.presenter;

import com.smartdengg.common.UseCase;
import com.smartdengg.model.SimpleSubscriber;
import com.smartdengg.model.entity.CityEntity;
import com.smartdengg.model.errors.WebServiceException;
import com.smartdengg.model.repository.interactor.CityListUseCase;
import com.smartdengg.model.request.CityListRequest;
import com.smartdengg.presentation.views.ListView;
import java.util.List;
import rx.Observable;
import rx.functions.Func0;

/**
 * Created by SmartDengg on 2016/2/22.
 */
public class CityListPresenterImp implements CityListPresenter<CityEntity> {

  private ListView listView;
  private UseCase<CityListRequest, List<CityEntity>> listUseCase;

  private CityListPresenterImp() {
    this.listUseCase = CityListUseCase.<CityListRequest>createdUseCase();
  }

  public static CityListPresenterImp createdPresenter() {
    return new CityListPresenterImp();
  }

  @Override
  public void attachView(ListView<CityEntity> view) {
    this.listView = view;
  }

  @Override
  public void loadData() {
    this.listUseCase.subscribe(new CityListRequest(), new ListSubscriber());
  }

  @Override
  public void detachView() {
    this.listUseCase.unsubscribe();
  }

  @SuppressWarnings("unchecked")
  private void showContent(final List<CityEntity> cityEntities) {

    this.listView.showDataList(Observable.fromCallable(new Func0<List<CityEntity>>() {
      @Override
      public List call() {
        return cityEntities;
      }
    }));
  }

  private void showError(String errorMessage) {
    this.listView.showError(errorMessage);
  }

  private final class ListSubscriber extends SimpleSubscriber<List<CityEntity>> {

    @Override
    public void onError(Throwable e) {
      super.onError(e);
      if (e instanceof WebServiceException) {
        CityListPresenterImp.this.showError(e.getMessage());
      } else {
        CityListPresenterImp.this.showError(null);
      }
    }

    @Override
    public void onNext(List<CityEntity> cityEntities) {
      CityListPresenterImp.this.showContent(cityEntities);
    }
  }
}