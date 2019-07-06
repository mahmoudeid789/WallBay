package ml.medyas.wallbay.adapters;

import android.util.SparseBooleanArray;

import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ml.medyas.wallbay.models.ImageEntity;

public abstract class SelectableClass extends PagedListAdapter<ImageEntity, RecyclerView.ViewHolder> {
    private SparseBooleanArray selectedItems = new SparseBooleanArray();
    private SparseBooleanArray addedToFav = new SparseBooleanArray();
    private PagedListAdapter adapter;

    protected SelectableClass() {
        super(ImageEntity.DIFF_CALLBACK);
    }

    public PagedListAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(PagedListAdapter adapter) {
        this.adapter = adapter;
    }

    /**
     * Indicates if the item at position position is selected
     *
     * @param position Position of the item to check
     * @return true if the item is selected, false otherwise
     */
    public boolean isSelected(int position) {
        return selectedItems.get(position);
    }

    /**
     * Toggle the selection status of the item at a given position
     *
     * @param position Position of the item to toggle the selection status for
     */
    public void toggleSelection(int position) {

        if (selectedItems.get(position, false)) {
            selectedItems.delete(position);
        } else {
            selectedItems.put(position, true);
        }
        adapter.notifyItemChanged(position);
    }

    /**
     * Select all adapter items
     */

    public void selectAll() {
        if (getItemCount() == getSelectedItems().size()) {
            selectedItems.clear();
        } else {
            for (int i = 0; i < getItemCount(); i++) {
                selectedItems.put(i, true);
            }
        }
        adapter.notifyDataSetChanged();
    }

    public String getLastSelectedItem() {
        if (getSelectedItems().size() == 0) {
            return "";
        }
        ImageEntity imageEntity = (ImageEntity) adapter.getCurrentList().get(selectedItems.keyAt(selectedItems.size() - 1));
        return imageEntity.getPreviewImage();
    }

    /**
     * Clear the selection status for all items
     */
    public void clearSelection() {
        List<Integer> selection = getSelectedItems();
        selectedItems.clear();
        for (Integer i : selection) {
            adapter.notifyItemChanged(i);
        }
    }

    /**
     * Count the selected items
     *
     * @return Selected items count
     */
    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    /**
     * Indicates the list of selected items
     *
     * @return List of selected items ids
     */
    public List<Integer> getSelectedItems() {
        List<Integer> items = new ArrayList<>(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); ++i) {
            items.add(selectedItems.keyAt(i));
        }
        return items;
    }

    /*
            Added to Favorite items
     */

    public void addToFav(int position) {
        addedToFav.put(position, true);
    }

    public boolean isAddedToFav(int position) {
        return addedToFav.get(position);
    }

    public void addSelectedToFav() {
        for (int i = 0; i < selectedItems.size(); i++) {
            addedToFav.put(selectedItems.keyAt(i), true);
        }
        notifySelection();
    }

    private void notifySelection() {
        List<Integer> selection = getSelectedItems();
        for (Integer i : selection) {
            adapter.notifyItemChanged(i);
        }
    }

}
