package com.example.ultrafit.presenter;

import com.example.domain.MovieListUseCase;
import com.example.domain.UseCase;
import com.example.model.bean.entity.MovieEntity;
import com.example.model.bean.request.MovieIdRequest;
import com.example.ultrafit.views.ListView;
import java.util.List;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func0;

/**
 * Created by SmartDengg on 2016/2/22.
 */
public class MovieListPresenterImp implements MovieListPresenter<MovieEntity> {

  private ListView listView;
  private UseCase<MovieIdRequest, List<MovieEntity>> listUseCase;

  private MovieListPresenterImp() {
    this.listUseCase = MovieListUseCase.createdUseCase();
  }

  public static MovieListPresenterImp createdPresenter() {
    return new MovieListPresenterImp();
  }

  @Override public void attachView(ListView<MovieEntity> view) {
    this.listView = view;
  }

  @Override public void loadData(String cityId) {
    this.listUseCase.subscribe(new MovieIdRequest(Integer.parseInt(cityId)), new ListSubscriber());
  }

  @Override public void detachView() {
    this.listUseCase.unsubscribe();
  }

  @SuppressWarnings("unchecked") private void showContent(final List<MovieEntity> movieEntities) {

    this.listView.showDataList(Observable.fromCallable(new Func0<List<MovieEntity>>() {
      @Override public List call() {
        return movieEntities;
      }
    }));
  }

  private void showError(String errorMessage) {
    this.listView.showError(errorMessage);
  }

  private final class ListSubscriber extends Subscriber<List<MovieEntity>> {

    @Override public void onCompleted() {
    }

    @Override public void onError(Throwable e) {
      MovieListPresenterImp.this.showError(e.getMessage());
    }

    @Override public void onNext(List<MovieEntity> movieEntities) {
      MovieListPresenterImp.this.showContent(movieEntities);
    }
  }
}