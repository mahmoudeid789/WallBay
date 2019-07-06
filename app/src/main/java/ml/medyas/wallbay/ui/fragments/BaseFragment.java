package ml.medyas.wallbay.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.disposables.Disposable;
import ml.medyas.wallbay.R;
import ml.medyas.wallbay.adapters.ActionModeCallback;
import ml.medyas.wallbay.adapters.BaseAdapter;
import ml.medyas.wallbay.databinding.FragmentBaseBinding;
import ml.medyas.wallbay.models.ImageEntity;
import ml.medyas.wallbay.services.WallpaperService;
import ml.medyas.wallbay.utils.GlideApp;
import ml.medyas.wallbay.utils.Utils;
import ml.medyas.wallbay.widget.WallbayWidget;

import static ml.medyas.wallbay.utils.Utils.calculateNoOfColumns;
import static ml.medyas.wallbay.utils.Utils.convertPixelsToDp;


public abstract class BaseFragment extends Fragment  implements BaseAdapter.onImageItemClicked, ActionModeCallback.onActionModeInterface{
    private OnBaseFragmentInteractions mListener;
    private BaseAdapter mAdapter;
    private FragmentBaseBinding binding;
    public static boolean inSelection = false;
    private ActionModeCallback actionModeCallback;
    private ActionMode actionMode;
    private Snackbar snackbar;

    private TextView sheetText;
    private ImageView sheetImage;

    public BaseFragment() {
        // Required empty public constructor
    }

    public abstract void setUpViewModel();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_base, container, false);

        mAdapter = new BaseAdapter(this, getContext());

        actionModeCallback = new ActionModeCallback(this, true);
        binding.statusLayout.slideShowPlay.hide();

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(calculateNoOfColumns(getContext(), convertPixelsToDp(getResources().getDimension(R.dimen.item_width), getContext())), StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);

        binding.baseRecyclerView.setLayoutManager(layoutManager);
        binding.baseRecyclerView.setHasFixedSize(false);
        binding.baseRecyclerView.setAdapter(mAdapter);
        binding.baseRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!inSelection) {
                    if (dy > 0 && binding.statusLayout.slideShowPlay.getVisibility() == View.VISIBLE) {
                        binding.statusLayout.slideShowPlay.hide();
                    } else if (dy < 0 && binding.statusLayout.slideShowPlay.getVisibility() != View.VISIBLE) {
                        binding.statusLayout.slideShowPlay.show();
                    }
                }
            }
        });

        binding.statusLayout.slideShowPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAdapter.getCurrentList().size() == 0) {
                    Toast.makeText(getContext(), getResources().getString(R.string.wait_for_load), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), getResources().getString(R.string.not_implemented), Toast.LENGTH_SHORT).show();
                }
            }
        });

        return binding.getRoot();
    }

    public void checkNetworkStatus(Utils.NetworkState networkState, final boolean replace) {
        if (networkState == Utils.NetworkState.LOADED) {
            getItemLoad().setVisibility(View.GONE);
            getRecyclerView().setVisibility(View.VISIBLE);

        } else if (networkState == Utils.NetworkState.EMPTY) {
            Toast.makeText(getContext(), getString(R.string.error_getting_data), Toast.LENGTH_SHORT).show();

        } else if (networkState == Utils.NetworkState.FAILED) {
            if (getAdapter().getCurrentList() == null ||getAdapter().getCurrentList().size() == 0) {
                getNetError().setVisibility(View.VISIBLE);
                getItemLoad().setVisibility(View.GONE);
                setSnackbar(Snackbar.make(getNetError(), R.string.net_error, Snackbar.LENGTH_INDEFINITE)
                        .setAction(getResources().getString(R.string.retry), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                getNetError().setVisibility(View.GONE);
                                if(replace) {
                                    mListener.reCreateFragment(ForYouFragment.newInstance());
                                }
                            }
                        }));
                getSnackbar().show();
            } else {
                Snackbar.make(getNetError(), R.string.faild_to_load, Snackbar.LENGTH_LONG).setAction(R.string.retry, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(replace) {
                            mListener.reCreateFragment(ForYouFragment.newInstance());
                        }
                    }
                }).show();
            }
        }
    }

    public RecyclerView getRecyclerView() {
        return binding.baseRecyclerView;
    }
    public LottieAnimationView getItemLoad() {
        return binding.statusLayout.itemLoad;
    }
    public LottieAnimationView getNetError() {
        return binding.statusLayout.netError;
    }
    public LottieAnimationView getListEmpty() {
        return binding.statusLayout.listEmpty;
    }
    public FloatingActionButton getSlideShowPlay() {
        return binding.statusLayout.slideShowPlay;
    }
    public Snackbar getSnackbar() {
        return  snackbar;
    }
    public void setSnackbar(Snackbar snackbar) {
        this.snackbar = snackbar;
    }
    public BaseAdapter getAdapter() {
        return mAdapter;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnBaseFragmentInteractions) {
            mListener = (OnBaseFragmentInteractions) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /*
            Adapter callbacks
    */
    @Override
    public void onItemClicked(ImageEntity imageEntity, ImageView itemImage, int adapterPosition) {
        if (inSelection) {
            if (actionMode != null) {
                toggleSelection(adapterPosition);
            }
        } else {
            mListener.onItemClicked(imageEntity, adapterPosition, itemImage);
        }
    }

    @Override
    public boolean onItemLongClicked(int position) {
        inSelection = true;
        mAdapter.notifyDataSetChanged();
        binding.statusLayout.slideShowPlay.hide();

        if (!binding.selectedSheet.isInflated()) {
            binding.selectedSheet.getViewStub().inflate();
            binding.selectedSheet.getRoot().setVisibility(View.VISIBLE);

            sheetText = binding.selectedSheet.getRoot().findViewById(R.id.selected_items);
            sheetImage = binding.selectedSheet.getRoot().findViewById(R.id.selected_image);
        }
        binding.selectedSheet.getRoot().setVisibility(View.VISIBLE);
        if (actionMode == null) {
            Toolbar toolbar = ((AppCompatActivity) getActivity()).findViewById(R.id.toolbar);
            actionMode = toolbar.startActionMode(actionModeCallback);
        }
        toggleSelection(position);
        return true;
    }

    private void toggleSelection(int position) {
        mAdapter.toggleSelection(position);
        int count = mAdapter.getSelectedItemCount();

        if (count == 0) {
            actionMode.finish();
        } else {
            sheetText.setText(String.valueOf(count));
            GlideApp.with(this)
                    .load(mAdapter.getLastSelectedItem())
                    .into(sheetImage);

            actionMode.invalidate();
        }
    }

    @Override
    public void onItemAddToFavorite(ImageEntity imageEntity) {
        mListener.onAddToFavorite(imageEntity).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onComplete() {
                WallbayWidget.sendRefreshBroadcast(getContext());
                Toast.makeText(getContext(), getString(R.string.add_fav), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Throwable e) {

            }
        });
    }

    /*
            ActionMode callbacks
     */
    @Override
    public void onDestroyMode() {
        binding.statusLayout.slideShowPlay.show();
        binding.selectedSheet.getRoot().setVisibility(View.GONE);
        inSelection = false;
        actionMode = null;
        mAdapter.clearSelection();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onMenuFavClicked() {
        List<ImageEntity> imageEntities = new ArrayList<>();
        for (Integer i : mAdapter.getSelectedItems()) {
            imageEntities.add(mAdapter.getCurrentList().get(i));
        }
        mListener.onAddToFavorite(imageEntities).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onComplete() {
                Toast.makeText(getContext(), getString(R.string.add_succ), Toast.LENGTH_SHORT).show();
                mAdapter.addSelectedToFav();
                WallbayWidget.sendRefreshBroadcast(getContext());
                actionMode.finish();
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(getContext(), getString(R.string.add_error), Toast.LENGTH_SHORT).show();
                actionMode.finish();
            }
        });
    }

    @Override
    public void onMenuDownClicked() {
        List<String> urls = new ArrayList<>();
        for (Integer i : mAdapter.getSelectedItems()) {
            urls.add(mAdapter.getCurrentList().get(i).getOriginalUrl());
        }
        WallpaperService.bulkWallpaperDownload(getContext(), urls);
        Toast.makeText(getContext(), getString(R.string.downloading), Toast.LENGTH_SHORT).show();
        actionMode.finish();
    }

    @Override
    public void onMenuRemoveClicked() {
        // Leave empty
    }

    @Override
    public void onSelectAll() {
        mAdapter.selectAll();
        sheetText.setText(String.valueOf(mAdapter.getSelectedItemCount()));
        GlideApp.with(this)
                .load(mAdapter.getLastSelectedItem())
                .into(sheetImage);
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnBaseFragmentInteractions {
        Completable onAddToFavorite(ImageEntity imageEntity);

        Completable onAddToFavorite(List<ImageEntity> imageEntities);

        void onItemClicked(ImageEntity imageEntity, int position, ImageView imageView);

        void reCreateFragment(Fragment newInstance);
    }
}

