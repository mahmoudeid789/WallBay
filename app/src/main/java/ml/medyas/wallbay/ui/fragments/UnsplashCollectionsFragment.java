package ml.medyas.wallbay.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import ml.medyas.wallbay.R;
import ml.medyas.wallbay.adapters.unsplash.CollectionRecyclerViewAdapter;
import ml.medyas.wallbay.adapters.unsplash.TagsRecyclerViewAdapter;
import ml.medyas.wallbay.databinding.FragmentBaseBinding;
import ml.medyas.wallbay.models.CollectionEntity;
import ml.medyas.wallbay.utils.Utils;
import ml.medyas.wallbay.viewmodels.unsplash.UnsplashCollectionsViewModel;
import ml.medyas.wallbay.viewmodels.unsplash.UnsplashFeaturedCollectionsViewModel;
import ml.medyas.wallbay.viewmodels.unsplash.UnsplashSearchCollectionsViewModel;

import static ml.medyas.wallbay.utils.Utils.FRAGMENT_POSITION;
import static ml.medyas.wallbay.utils.Utils.SEARCH_QUERY;
import static ml.medyas.wallbay.utils.Utils.calculateNoOfColumns;
import static ml.medyas.wallbay.utils.Utils.convertPixelsToDp;

public class UnsplashCollectionsFragment extends Fragment implements CollectionRecyclerViewAdapter.CollectionInterface, TagsRecyclerViewAdapter.OnTagItemClicked {
    private CollectionRecyclerViewAdapter mAdapter;
    private FragmentBaseBinding binding;
    private int position;
    private String query;
    private UnsplashCollectionInterface mListener;


    public static Fragment newInstance(int position) {
        Fragment fragment = new UnsplashCollectionsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(FRAGMENT_POSITION, position);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static Fragment newInstance(int position, String query) {
        Fragment fragment = new UnsplashCollectionsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(FRAGMENT_POSITION, position);
        bundle.putString(SEARCH_QUERY, query);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null) {
            position = getArguments().getInt(FRAGMENT_POSITION);
            if(getArguments().containsKey(SEARCH_QUERY)) {
                query = getArguments().getString(SEARCH_QUERY);
            }
        }

        mAdapter = new CollectionRecyclerViewAdapter(this, this, getContext());
        setUpViewModel();
    }

    private void setUpViewModel() {
        ViewModel mViewModel;

        switch (position) {
            case 3:
                mViewModel = ViewModelProviders.of(this, new UnsplashCollectionsViewModel.UnsplashCollectionsViewModelFactory(getContext()))
                        .get(UnsplashCollectionsViewModel.class);

                ((UnsplashCollectionsViewModel) mViewModel).getNetworkStateLiveData().observe(this, new Observer<Utils.NetworkState>() {
                    @Override
                    public void onChanged(@Nullable Utils.NetworkState networkState) {
                        checkNetStatus(networkState);
                    }
                });

                ((UnsplashCollectionsViewModel) mViewModel).getPagedListLiveData().observe(this, new Observer<PagedList<CollectionEntity>>() {
                    @Override
                    public void onChanged(@Nullable PagedList<CollectionEntity> collectionEntities) {
                        mAdapter.submitList(collectionEntities);
                    }
                });

                break;

            case 4:
                mViewModel = ViewModelProviders.of(this, new UnsplashFeaturedCollectionsViewModel.UnsplashFeaturedCollectionsViewModelFactory(getContext()))
                        .get(UnsplashFeaturedCollectionsViewModel.class);


                ((UnsplashFeaturedCollectionsViewModel) mViewModel).getNetworkStateLiveData().observe(this, new Observer<Utils.NetworkState>() {
                    @Override
                    public void onChanged(@Nullable Utils.NetworkState networkState) {
                        checkNetStatus(networkState);
                    }
                });

                ((UnsplashFeaturedCollectionsViewModel) mViewModel).getPagedListLiveData().observe(this, new Observer<PagedList<CollectionEntity>>() {
                    @Override
                    public void onChanged(@Nullable PagedList<CollectionEntity> collectionEntities) {
                        mAdapter.submitList(collectionEntities);
                    }
                });

                break;

            case 5:
                mViewModel = ViewModelProviders.of(this, new UnsplashSearchCollectionsViewModel.UnsplashSearchCollectionsViewModelFactory(getContext(), query))
                        .get(UnsplashSearchCollectionsViewModel.class);


                ((UnsplashSearchCollectionsViewModel) mViewModel).getNetworkStateLiveData().observe(this, new Observer<Utils.NetworkState>() {
                    @Override
                    public void onChanged(@Nullable Utils.NetworkState networkState) {
                        checkNetStatus(networkState);
                    }
                });

                ((UnsplashSearchCollectionsViewModel) mViewModel).getPagedListLiveData().observe(this, new Observer<PagedList<CollectionEntity>>() {
                    @Override
                    public void onChanged(@Nullable PagedList<CollectionEntity> collectionEntities) {
                        mAdapter.submitList(collectionEntities);
                    }
                });

                break;
        }

    }

    private void checkNetStatus(Utils.NetworkState networkState) {
        if (networkState == Utils.NetworkState.LOADED) {
            binding.statusLayout.itemLoad.setVisibility(View.GONE);
            binding.baseRecyclerView.setVisibility(View.VISIBLE);

        } else if (networkState == Utils.NetworkState.EMPTY) {
            Toast.makeText(getContext(), getResources().getString(R.string.error_getting_data), Toast.LENGTH_SHORT).show();

        } else if (networkState == Utils.NetworkState.FAILED) {
            if (mAdapter.getCurrentList() == null || mAdapter.getCurrentList().size() == 0) {
                binding.statusLayout.netError.setVisibility(View.VISIBLE);
                binding.statusLayout.itemLoad.setVisibility(View.GONE);
                Snackbar.make(binding.statusLayout.netError, getResources().getString(R.string.net_error), Snackbar.LENGTH_INDEFINITE)
                        .setAction("Retry", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                binding.statusLayout.netError.setVisibility(View.GONE);
                            }
                        }).show();
            } else {
                Snackbar.make(binding.statusLayout.netError, getResources().getString(R.string.faild_to_load), Snackbar.LENGTH_LONG).setAction(getResources().getString(R.string.retry), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                }).show();
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_base, container, false);

        binding.statusLayout.slideShowPlay.hide();

        int spanCount = 1;
        if(getResources().getDimension(R.dimen.collection_width) > 200) {
            spanCount = calculateNoOfColumns(getContext(), convertPixelsToDp(getResources().getDimension(R.dimen.collection_width), getContext()));
        }

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), spanCount);

        binding.baseRecyclerView.setLayoutManager(layoutManager);
        binding.baseRecyclerView.setHasFixedSize(false);
        binding.baseRecyclerView.setAdapter(mAdapter);
        binding.baseRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                    if (dy > 0 && binding.statusLayout.slideShowPlay.getVisibility() == View.VISIBLE) {
                        binding.statusLayout.slideShowPlay.hide();
                    } else if (dy < 0 && binding.statusLayout.slideShowPlay.getVisibility() != View.VISIBLE) {
                        binding.statusLayout.slideShowPlay.show();
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



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof UnsplashCollectionInterface) {
            mListener = (UnsplashCollectionInterface) context;
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

    @Override
    public void onTagItemClicked(String query) {
        mListener.onAddFragment(UnsplashCollectionsFragment.newInstance(5, query));
        mListener.updateToolbarTitle(query);
    }

    @Override
    public void onCollectionItemClicked(CollectionEntity collectionEntity) {
        mListener.onAddFragment(UnsplashDefaultVPFragment.newInstance(4, collectionEntity.getId()));
        mListener.updateToolbarTitle(collectionEntity.getTitle());
    }




    public interface UnsplashCollectionInterface {
        void onAddFragment(Fragment fragment);
        void updateToolbarTitle(String title);
    }
}
