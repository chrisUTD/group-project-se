package com.example.chris.group_project;

/**
 * Interface to provide methods for a model object that provides notifications when the model
 * data changes to any listeners that are registered.
 */
public interface ModelChangeNotifier {
    /**
     * Method called when model needs to notify its listeners
     * @param model
     */
    public void notifyListeners(ModelChangeNotifier model);

    /**
     * Method called when a listener want to register itself for model change events.
     * @param listener listener to register
     */
    public void registerListener(ModelChangeListener listener);

    /**
     * Method called by the listener when it wants to unregister from change events.
     * @param listener
     */
    public void unregisterListener(ModelChangeListener listener);

    /**
     * Method that allows the listener to notify listeners even if no model data has directly been
     * changed.
     */
    public void touch();
}
