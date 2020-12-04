package com.star.pibbledev.home.createmedia.mediapicker.ui.picker.loader.loadercallbacks;

import java.util.List;

public interface FileResultCallback<T> {
    void onResultCallback(List<T> files);
  }