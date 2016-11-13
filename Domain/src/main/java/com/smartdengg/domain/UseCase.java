package com.smartdengg.domain;

import com.smartdengg.ultra.core.RequestEntity;
import com.smartdengg.ultra.core.UltraParserFactory;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import rx.Observable;
import rx.Observer;
import rx.Single;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.subscriptions.Subscriptions;

/**
 * Created by SmartDengg on 2016/2/22.
 */
public abstract class UseCase<Request, Response> {

  private Subscription subscription = Subscriptions.empty();

  private final Observable.Transformer<Response, Response> transformer;

  protected UseCase(Observable.Transformer<Response, Response> transformer) {
    this.transformer = transformer;
  }

  public void subscribe(final Request request, Observer<Response> useCaseSubscriber) {
    this.subscribe(request, useCaseSubscriber, null);
  }

  public void subscribe(final Request request, Observer<Response> useCaseSubscriber,
      final Action1<RequestEntity> action) {

    /**be care of ConnectableObservable!!!*/
    this.subscription = Observable.defer(new Func0<Observable<RequestEntity<Request>>>() {
      @Override public Observable<RequestEntity<Request>> call() {
        return new UltraParserFactory<>(request).parseRequestEntity().asObservable();
      }
    }).doOnNext(new Action1<RequestEntity<Request>>() {
      @Override public void call(RequestEntity<Request> requestEntity) {
        if (action != null) action.call(requestEntity);
      }
    }).concatMap(new Func1<RequestEntity<Request>, Observable<Response>>() {
      @Override public Observable<Response> call(RequestEntity<Request> requestEntity) {
        return UseCase.this.interactor(requestEntity.getUrl(), requestEntity.getParamMap());
      }
    }).onBackpressureDrop().compose(transformer).subscribe(useCaseSubscriber);
  }

  @SuppressWarnings("unchecked")
  public void subscribe(final Request request, final Action1<? super Response> onSuccess,
      final Action1<Throwable> onError) {
    this.subscribe(request, onSuccess, onError, null);
  }

  public void subscribe(final Request request, final Action1<? super Response> onSuccess,
      final Action1<Throwable> onError, final Action1<RequestEntity> action) {

    /**you can also use the operator {@link rx.Observable.toSingle}, it's simpler*/
    this.subscription = Single.defer(new Func0<Single<RequestEntity<Request>>>() {
      @Override public Single<RequestEntity<Request>> call() {
        return new UltraParserFactory<>(request).parseRequestEntity().asSingle();
      }
    }).map(new Func1<RequestEntity<Request>, RequestEntity<Request>>() {
      @Override public RequestEntity<Request> call(RequestEntity<Request> requestEntity) {
        if (action != null) action.call(requestEntity);
        return requestEntity;
      }
    }).flatMap(new Func1<RequestEntity<Request>, Single<Response>>() {
      @Override public Single<Response> call(RequestEntity<Request> requestEntity) {
        return UseCase.this.interactorSingle(requestEntity.getUrl(), requestEntity.getParamMap());
      }
    }).subscribe(onSuccess, onError);
  }

  public void unsubscribe() {
    if (!subscription.isUnsubscribed()) subscription.unsubscribe();
  }

  protected Single<Response> interactorSingle(@NotNull String url,
      @NotNull Map<String, String> params) {
    throw new IllegalArgumentException("If you use the 'Single', you can not call super");
  }

  protected abstract Observable<Response> interactor(@NotNull String url,
      @NotNull Map<String, String> params);
}
