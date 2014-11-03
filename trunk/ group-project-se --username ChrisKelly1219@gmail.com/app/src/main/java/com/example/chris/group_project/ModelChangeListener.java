package com.example.chris.group_project;

/**
 * Created by Jonny on 9/20/14.
 */
public interface ModelChangeListener {

    public void onModelChange(ModelChangeNotifier model);
    public void unregister();
}
