package com.star.pibbledev.home.createmedia.mediapicker.ui.picker.component;

import androidx.recyclerview.widget.RecyclerView;

import com.star.pibbledev.home.createmedia.mediapicker.ui.picker.models.BaseFile;

import java.util.ArrayList;
import java.util.List;


public abstract class SelectableAdapter<VH extends RecyclerView.ViewHolder, T extends BaseFile>
        extends RecyclerView.Adapter<VH> implements Selectable<T> {

    private List<T> items;

    private List<T> selectedPhotos;

    public SelectableAdapter(List<T> items, List<String> selectedPaths) {
        this.items = items;
        selectedPhotos = new ArrayList<>();

        addPathsToSelections(selectedPaths);
    }

    private void addPathsToSelections(List<String> selectedPaths) {
        if (selectedPaths == null)
            return;

        for (int index = 0; index < items.size(); index++) {
            for (int jindex = 0; jindex < selectedPaths.size(); jindex++) {
                if (items.get(index).getPath().equals(selectedPaths.get(jindex))) {
                    selectedPhotos.add(items.get(index));
                }
            }
        }
    }


    /**
     * Indicates if the item at position where is selected
     *
     * @param photo Photo of the item to check
     * @return true if the item is selected, false otherwise
     */
    @Override
    public boolean isSelected(T photo) {
        return selectedPhotos.contains(photo);
    }


    /**
     * Toggle the selection status of the item at a given position
     *
     * @param photo Photo of the item to toggle the selection status for
     */
    @Override
    public void toggleSelection(T photo) {
        if (selectedPhotos.contains(photo)) {
            selectedPhotos.remove(photo);
        } else {
            selectedPhotos.add(photo);
        }
    }

    @Override
    public void getSelection(T photo) {
        selectedPhotos.clear();
        selectedPhotos.add(photo);
    }

    /**
     * Clear the selection status for all items
     */
    @Override
    public void clearSelection() {
        selectedPhotos.clear();
    }

    @Override
    public int getSelectedItemCount() {
        return selectedPhotos.size();
    }

    public void setData(List<T> items) {
        this.items = items;
    }

    public List<T> getItems() {
        return items;
    }

    public List<T> getSelectedPhotos() {
        return selectedPhotos;
    }

}