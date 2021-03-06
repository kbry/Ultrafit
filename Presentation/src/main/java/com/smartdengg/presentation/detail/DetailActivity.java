package com.smartdengg.presentation.detail;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import com.smartdengg.common.utils.DensityUtil;
import com.smartdengg.domain.entity.MovieEntity;
import com.smartdengg.presentation.BaseActivity;
import com.smartdengg.presentation.R;
import com.smartdengg.presentation.bitmaps.DateUtil;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

/**
 * Created by SmartDengg on 2016/2/22.
 */
public class DetailActivity extends BaseActivity {

  private static final String START_BOUND = "startBounds";
  private static final String GLOBAL_OFFSET = "globalOffset";
  private static final String ENTITY = "entity";

  @NonNull @BindView(R.id.detail_layout_root_view) protected ViewGroup rootView;

  @NonNull @BindView(R.id.detail_layout_thumb_iv) protected ImageView movieThumbIv;
  @NonNull @BindView(R.id.detail_layout_content_rl) protected RelativeLayout contentLayout;

  @NonNull @BindView(R.id.detail_layout_name_tv) protected TextView movieNameTv;
  @NonNull @BindView(R.id.detail_layout_category_tv) protected TextView movieCategoryTv;
  @NonNull @BindView(R.id.detail_layout_release_tv) protected TextView movieReleaseTv;

  @NonNull @BindView(R.id.detail_layout_area_tv) protected TextView movieCountryTv;
  @NonNull @BindView(R.id.detail_layout_writers_tv) protected TextView movieWritersTv;
  @NonNull @BindView(R.id.detail_layout_director_tv) protected TextView movieDirectorTv;
  @NonNull @BindView(R.id.detail_layout_actor_tv) protected TextView movieActorTv;
  @NonNull @BindView(R.id.detail_layout_sketch_tv) protected TextView movieSketchTv;

  private Rect startBounds;
  private float scale;
  private AnimatorSet animatorSet;

  public static void navigateToActivity(@NonNull AppCompatActivity startingActivity,
      @NonNull Rect startBounds, @NonNull Point globalOffset, @NonNull MovieEntity movieEntity) {

    Intent intent = new Intent(startingActivity, DetailActivity.class);

    intent.putExtra(START_BOUND, startBounds);
    intent.putExtra(GLOBAL_OFFSET, globalOffset);
    intent.putExtra(ENTITY, movieEntity);
    startingActivity.startActivity(intent);
    startingActivity.overridePendingTransition(0, 0);
  }

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    DetailActivity.this.initView(savedInstanceState);
  }

  @Override protected int getLayoutId() {
    return R.layout.movie_detail_activity;
  }

  private void initView(Bundle savedInstanceState) {

    if (savedInstanceState == null) {

      ViewTreeObserver viewTreeObserver = this.rootView.getViewTreeObserver();
      if (viewTreeObserver.isAlive()) {
        viewTreeObserver.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
          @Override public boolean onPreDraw() {
            rootView.getViewTreeObserver().removeOnPreDrawListener(this);
            DetailActivity.this.runEnterAnimation(getIntent().getExtras());
            return true;
          }
        });
      }
    }
  }

  @SuppressLint("SetTextI18n") private void runEnterAnimation(Bundle bundle) {

    MovieEntity movieEntity = bundle.getParcelable(ENTITY);
    if (movieEntity == null) return;

    this.movieNameTv.setText(movieEntity.getMovieName());
    this.movieCategoryTv.setText(movieEntity.getMovieCategory());
    this.movieReleaseTv.setText("上映时间：" + DateUtil.coverToDate(movieEntity.getMovieReleaseTime()));

    this.movieCountryTv.setText("地区:" + movieEntity.getMovieCountry());
    this.movieWritersTv.setText("编剧:" + movieEntity.getMovieWriters());
    this.movieDirectorTv.setText("导演:" + movieEntity.getMovieDirector());
    this.movieActorTv.setText("演员:" + movieEntity.getMovieActor());
    this.movieSketchTv.setText("剧情概要:" + movieEntity.getMovieSketch());

    Point globalOffset = bundle.getParcelable(GLOBAL_OFFSET);
    startBounds = bundle.getParcelable(START_BOUND);

    Rect finalBounds = new Rect();
    this.movieThumbIv.getGlobalVisibleRect(finalBounds);
    finalBounds.offset(-globalOffset.x, -globalOffset.y);

    scale = DensityUtil.calculateScale(startBounds, finalBounds);

    this.movieThumbIv.setLayerType(View.LAYER_TYPE_HARDWARE, null);

    ViewCompat.setPivotX(movieThumbIv, 0.0f);
    ViewCompat.setPivotY(movieThumbIv, 0.0f);
    ViewCompat.setAlpha(contentLayout, 0.0f);

    animatorSet = new AnimatorSet();
    animatorSet.play(
        ObjectAnimator.ofFloat(this.movieThumbIv, View.X, startBounds.left, finalBounds.left))
        .with(ObjectAnimator.ofFloat(this.movieThumbIv, View.Y, startBounds.top, finalBounds.top))
        .with(ObjectAnimator.ofFloat(this.movieThumbIv, View.SCALE_X, scale, 1.0f))
        .with(ObjectAnimator.ofFloat(this.movieThumbIv, View.SCALE_Y, scale, 1.0f));
    animatorSet.setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime));
    animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
    animatorSet.addListener(new AnimatorListenerAdapter() {
      @Override public void onAnimationEnd(Animator animation) {

        ViewCompat.animate(contentLayout).alpha(1.0f).withLayer();

        movieThumbIv.setLayerType(View.LAYER_TYPE_NONE, null);
        DetailActivity.this.animatorSet = null;
      }
    });

    Picasso.with(DetailActivity.this)
        .load(movieEntity.getMovieThumbUrl())
        .noFade()
        .into(movieThumbIv, new Callback.EmptyCallback() {
          @Override public void onSuccess() {
            animatorSet.start();
          }
        });
  }

  protected void runExitAnimator() {

    if (animatorSet != null) animatorSet.cancel();

    this.movieThumbIv.setLayerType(View.LAYER_TYPE_HARDWARE, null);

    animatorSet = new AnimatorSet();
    animatorSet.play(ObjectAnimator.ofFloat(this.movieThumbIv, View.X, startBounds.left))
        .with(ObjectAnimator.ofFloat(this.movieThumbIv, View.Y, startBounds.top))
        .with(ObjectAnimator.ofFloat(this.movieThumbIv, View.SCALE_X, scale))
        .with(ObjectAnimator.ofFloat(this.movieThumbIv, View.SCALE_Y, scale));
    animatorSet.setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime));
    animatorSet.setInterpolator(new DecelerateInterpolator());
    animatorSet.addListener(new AnimatorListenerAdapter() {
      @Override public void onAnimationStart(Animator animation) {
        DetailActivity.this.contentLayout.setVisibility(View.GONE);
      }

      @Override public void onAnimationEnd(Animator animation) {

        DetailActivity.this.animatorSet = null;
        DetailActivity.this.movieThumbIv.setLayerType(View.LAYER_TYPE_NONE, null);
        DetailActivity.this.finish();
      }
    });
    animatorSet.start();
  }

  @NonNull @OnClick(R.id.detail_layout_cancel_iv) protected void onCancel() {
    DetailActivity.this.runExitAnimator();
  }

  @Override protected void exit() {
    DetailActivity.this.runExitAnimator();
  }

  @Override protected void onDestroy() {
    super.onDestroy();

    Picasso.with(DetailActivity.this).cancelRequest(this.movieThumbIv);
    if (animatorSet != null && animatorSet.isRunning()) animatorSet.cancel();
  }
}
