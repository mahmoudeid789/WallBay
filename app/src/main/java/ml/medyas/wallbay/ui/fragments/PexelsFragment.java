package ml.medyas.wallbay.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.core.view.ViewCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import ml.medyas.wallbay.R;
import ml.medyas.wallbay.adapters.pexels.PexelsViewPagerAdapter;
import ml.medyas.wallbay.databinding.FragmentPexelsBinding;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnPexelsFragmentInteractions} interface
 * to handle interaction events.
 * Use the {@link PexelsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PexelsFragment extends Fragment {
    private OnPexelsFragmentInteractions mListener;
    public static final String TAG = "ml.medyas.wallbay.ui.fragments.PexelsFragment";

    public PexelsFragment() {
        // Required empty public constructor
    }

    public static PexelsFragment newInstance() {
        return new PexelsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FragmentPexelsBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_pexels, container, false);
        binding.defaultLayout.viewPager.setAdapter(new PexelsViewPagerAdapter(getChildFragmentManager(), getContext()));
        binding.defaultLayout.tabLayout.setupWithViewPager(binding.defaultLayout.viewPager);
        binding.defaultLayout.viewPager.setCurrentItem(0);
        ViewCompat.setElevation(binding.defaultLayout.viewPager, 4 * getResources().getDisplayMetrics().density);

        return binding.getRoot();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);

        final MenuItem myActionMenuItem = menu.findItem( R.id.menu_search);
        final SearchView searchView = (SearchView) myActionMenuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if( ! searchView.isIconified()) {
                    searchView.setIconified(true);
                }
                myActionMenuItem.collapseActionView();

                searchQuery(query);

                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }

    private void searchQuery(String text) {
        if(!text.equals("")) {
            mListener.onAddFragment(PexelsViewPagerFragment.newInstance(2, text));
            mListener.updateToolbarTitle(text);
        } else {
            Toast.makeText(getContext(), getString(R.string.provide_query), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_favorite) {
            mListener.onAddFragment(FavoriteFragment.newInstance());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPexelsFragmentInteractions) {
            mListener = (OnPexelsFragmentInteractions) context;
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
    public interface OnPexelsFragmentInteractions {

        void onAddFragment(Fragment newInstance);

        void updateToolbarTitle(String text);
    }
}
