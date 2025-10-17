package com.example.eventplanner.utils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;


public class ObserverTracker {
    private final List<RemovingObserver<?>> observers = new ArrayList<>();

    public ObserverTracker() {}

    private static class RemovingObserver<T> implements Observer<T> {
        private final LiveData<T> source;
        protected final Consumer<Observer<T>> afterChange;
        private final Consumer<T> action;
        public RemovingObserver(final LiveData<T> source, final Consumer<Observer<T>> afterChange, final Consumer<T> action) {
            this.source = source;
            this.afterChange = afterChange;
            this.action = action;
        }

        @Override
        public void onChanged(T result) {
            action.accept(result);
            stopObserving();
            afterChange.accept(this);
        }

        public void stopObserving() {
            source.removeObserver(this);
        }
    }

    /**
     * Observe a LiveData and call action after the first change. The observer is then removed.
     * @param source LiveData to observe
     * @param action Action to call after the first change
     * @param <T> Type of the LiveData
     */
    public <T> void observeOnce(LiveData<T> source, Consumer<T> action) {
        RemovingObserver<T> observer = new RemovingObserver<>(source, observers::remove, action);
        observers.add(observer);
        source.observeForever(observer);
    }

    /**
     * Observe a LiveData and call action after the first change. The observer is then removed.
     * @param source LiveData to observe
     * @param target MutableLiveData to update with new value
     * @param allowNull Whether to emit null on null, or skip emitting
     * @param <T> Type of the LiveData
     */
    public <T> void observeOnce(LiveData<T> source, MutableLiveData<T> target, boolean allowNull) {
        Consumer<T> action = result -> {
            if (allowNull || result != null)
                target.setValue(result);
        };
        observeOnce(source, action);
    }

    /**
     * Stop all observers.
     */
    public void clear() {
        observers.forEach(RemovingObserver::stopObserving);
        observers.clear();
    }
}
