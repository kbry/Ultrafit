package com.example.domain;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.example.common.ultrafit.RequestEntity;
import com.example.common.ultrafit.UltraParser;
import com.orhanobut.logger.Logger;
import java.util.Map;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.subscriptions.Subscriptions;

/**
 * Created by SmartDengg on 2016/2/22.
 */
public abstract class UseCase<R, S> {

  private Subscription subscription = Subscriptions.empty();

  @SuppressWarnings("unchecked") public void subscribe(final R requestEntity, Observer<S> useCaseSubscriber) {

    subscription = Observable.fromCallable(new Func0<R>() {
      @Override public R call() {
        return requestEntity;
      }
    }).concatMap(new Func1<R, Observable<S>>() {
      @Override public Observable<S> call(R r) {

        RequestEntity requestEntity = UltraParser.createParser(r).parseRequestEntity();
        Logger.d("Begin Request!!! \nType : %s \n" + "URL : %s \n" + "Params : %s \n", //
                 requestEntity.getRestType().name(), requestEntity.getUrl(), requestEntity.getQueryMap());

        return UseCase.this.interactor(requestEntity.getUrl(), requestEntity.getQueryMap());
      }
    }).onBackpressureBuffer().take(1).filter(new Func1<S, Boolean>() {
      @Override public Boolean call(S s) {
        return !subscription.isUnsubscribed();
      }
    }).subscribe(useCaseSubscriber);
  }

  public void unsubscribe() {
    if (!subscription.isUnsubscribed()) {
      subscription.unsubscribe();
    }
  }

  @CheckResult protected abstract Observable<S> interactor(@NonNull String url, @NonNull Map params);
}
