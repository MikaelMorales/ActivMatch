package ch.unil.eda.activmatch.adapter;

import android.view.View;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class CellView<T> {

    private final ViewId.ViewCreator viewCreator;

    private int[] uiElementIds = new int[0];
    private TriConsumer<Integer, T, View> binder = (i, t, v) -> {};

    private Consumer<T> onClickListener = i -> {};
    private BiConsumer<T, View> mainCellViewBinder = (i, v) -> {};

    public CellView(final ViewId.ViewCreator viewCreator) {
        this.viewCreator = viewCreator;
    }

    public CellView(final ViewId.ViewCreator viewCreator, final BiConsumer<T, View> mainCellViewBinder) {
        this.viewCreator = viewCreator;
        this.mainCellViewBinder = mainCellViewBinder;
    }

    public CellView(final ViewId.ViewCreator viewCreator, final Consumer<T> onClickListener) {
        this.viewCreator = viewCreator;
        this.onClickListener = onClickListener;
    }

    public CellView(final ViewId.ViewCreator viewCreator, final int[] uiElementIds, final TriConsumer<Integer, T, View> binder) {
        this.uiElementIds = uiElementIds;
        this.viewCreator = viewCreator;
        this.binder = binder;
    }

    public CellView(final ViewId.ViewCreator viewCreator, final int[] uiElementIds, final TriConsumer<Integer, T, View> binder, final Consumer<T> onClickListener) {
        this.uiElementIds = uiElementIds;
        this.viewCreator = viewCreator;
        this.binder = binder;
        this.onClickListener = onClickListener;
    }

    public CellView(final ViewId.ViewCreator viewCreator, final int[] uiElementIds, final TriConsumer<Integer, T, View> binder, final BiConsumer<T, View> mainCellViewBinder) {
        this.uiElementIds = uiElementIds;
        this.viewCreator = viewCreator;
        this.binder = binder;
        this.mainCellViewBinder = mainCellViewBinder;
    }

    public ViewId.ViewCreator getViewCreator() {
        return viewCreator;
    }

    public int[] getUiElementIds() {
        return uiElementIds;
    }

    public TriConsumer<Integer, T, View> getBinder() {
        return binder;
    }

    public Consumer<T> getOnClickListener() {
        return onClickListener;
    }

    public BiConsumer<T, View> getMainCellViewBinder() {
        return mainCellViewBinder;
    }

}