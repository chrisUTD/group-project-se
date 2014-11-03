package com.example.chris.group_project;

/**
 * Created by Jonny on 9/20/14.
 */
public interface ModelChangeNotifier {
    public void notifyListeners(ModelChangeNotifier model);
    public void registerListener(ModelChangeListener listener);
    public void unregisterListener(ModelChangeListener listener);
    public void touch();
}
