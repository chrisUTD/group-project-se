import com.example.chris.group_project.Group;

import java.util.ArrayList;

/**
 * Created by nahuecht on 10/20/2014.
 */
public class GroupManager
{
    private ArrayList<Group> groups;
    private GroupManager instance;

    public GroupManager getInstance()
    {
        return instance;
    }

    //i think this is whats up but this the right thing but i have
    public ArrayList<Group> groups()
    {
        return groups;
    }
}