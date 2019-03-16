package ch.unil.eda.activmatch.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;


public class GenericAdapter<T> extends RecyclerView.Adapter<GenericAdapter<T>.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        SparseArray<View> views = new SparseArray<>();
        View cell;
        int type;

        ViewHolder(View itemView, int viewType) {
            super(itemView);
            type = viewType;

            cell = itemView;

            int [] ids = uiElementIds.get(viewType);
            for (int i : ids) {
                views.put(i, itemView.findViewById(i));
            }

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        private T getCurrent() {
            int pos = getAdapterPosition();
            if (pos == RecyclerView.NO_POSITION)
                return null;
            return items.get(pos);
        }

        @Override
        public void onClick(View view) {
            T c = getCurrent();
            if (c == null)
                return;
            onClickListener.get(type).accept(c);
        }

        @Override
        public boolean onLongClick(View view) {
            T c = getCurrent();
            if (c == null)
                return false;
            return onLongClickListener.get(type).apply(c, view);
        }

    }

    private final List<T> items = new ArrayList<>();

    private final SparseArray<int[]> uiElementIds = new SparseArray<>();
    private final SparseArray<ViewId.ViewCreator> viewCreator = new SparseArray<>();
    private final SparseArray<TriConsumer<Integer, T, View>> binder = new SparseArray<>();

    private Function<T, Integer> viewTypeMapper = s -> 0;
    private final SparseArray<Consumer<T>> onClickListener = new SparseArray<>();
    private final SparseArray<BiFunction<T, View, Boolean>> onLongClickListener = new SparseArray<>();
    private final SparseArray<BiConsumer<T, View>> cellViewBinder = new SparseArray<>();

    public GenericAdapter(CellView<T> defaultDefiner) {
        super();
        setCellDefinerForType(0, defaultDefiner);
    }

    public void setCellDefinerForType(int type, CellView<T> definer) {
        uiElementIds.put(type, definer.getUiElementIds());
        viewCreator.put(type, definer.getViewCreator());
        binder.put(type, definer.getBinder());
        onClickListener.put(type, definer.getOnClickListener());
        onLongClickListener.put(type, (a, b) -> false);
        cellViewBinder.put(type, definer.getMainCellViewBinder());
    }

    public void setLongClickListenerForType(int type, BiFunction<T, View, Boolean> onLongClickListener) {
        this.onLongClickListener.put(type, onLongClickListener);
    }

    public void setItems(List<T> items) {
        this.items.clear();
        this.items.addAll(items);
    }

    public void setViewTypeMapper(Function<T, Integer> viewTypeMapper) {
        this.viewTypeMapper = viewTypeMapper;
    }

    @Override
    public int getItemViewType(int position) {
        return viewTypeMapper.apply(items.get(position));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = viewCreator.get(viewType).createView(parent);
        return new ViewHolder(v, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final T item = items.get(position);
        final int type = viewTypeMapper.apply(item);

        cellViewBinder.get(type).accept(item, holder.cell);

        for(int i = 0, nsize = holder.views.size(); i < nsize; i++) {
            int id = holder.views.keyAt(i);
            View view = holder.views.valueAt(i);
            binder.get(type).accept(id, item, view);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void onItemDismiss(int position) {
        items.remove(position);
        notifyItemRemoved(position);
    }

    public void onItemAdd(int position, T e) {
        if(e != null) {
            items.add(position, e);
            notifyItemInserted(position);
        }
    }

    public void onItemUpdate(int position, T e) {
        if(e != null) {
            items.set(position, e);
            notifyItemChanged(position);
        }
    }

    public List<T> getItems() {
        return items;
    }

}