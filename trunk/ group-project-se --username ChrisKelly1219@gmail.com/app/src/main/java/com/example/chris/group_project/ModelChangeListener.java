package com.example.chris.group_project;

/**
 * Interface to provide methods for model listener.
 */
public interface ModelChangeListener {
    /**
     * Method called by watched object when the model changes.
     * @param model reference to the changed model.
     */
    public void onModelChange(ModelChangeNotifier model);

    /**
     * Method for removing the listener from the watched objects list of listeners.
     */
    public void unregister();
}
