package com.example.chris.group_project;



/**
 * Created by nahuecht on 10/20/2014.
 */
public class Contact
{
    //temp change
    private int id;
    private String firstName;
    private String middleName;
    private String lastName;
    private ArrayList<String> phoneNumbers;
    private ArrayList<String> emailAddresses;
    private ArrayList<Integer> groups;
    private String pictureUri;

    public Contact(String name, String phone)
    {
    }
    /*The search in phone numbers could be trickey to do with the use of
    parenthesis in phone numbers usually. may need a different storage method
    perhaps some regex trickerey could work well here.
     */
    public boolean search(String term)
    {
        if (firstName.equalsIgnoreCase(term))
        {
            return true;
        }
        else if(middleName.equalsIgnoreCase(term))
        {
            return true;
        }
        else if(lastName.equalsIgnoreCase(term))
        {
            return true;
        }
        else if(phoneNumbers.contains(term))
        {
            return true;
        }
        else if(emailAddresses.contains(term))
        {
            return true;
        }
        else if(groups.contains(Integer.parseInt(term)))
        {
            return true;
        }
        return false;
    }
    public int getId(){return id;}
    public void setId(int id){this.id = id;}

    public String getFirstName(){return firstName;}
    public void setFirstName(String firstName){this.firstName = firstName;}

    public String getMiddleName(){return middleName;}
    public void setMiddleName(String midddleName){this.middleName = midddleName;}

    public String getLastName(){return lastName;}
    public void setLastName(String lastName){this.lastName = lastName;}

    public ArrayList<String> getPhoneNumbers(){return phoneNumbers;}
    public void setPhoneNumbers(ArrayList<String> phoneNumbers){this.phoneNumbers = phoneNumbers;}

    public ArrayList<String> getEmailAddresses(){return emailAddresses;}
    public void setEmailAddresses(ArrayList<String> emailAddresses){this.emailAddresses = emailAddresses;}

    public ArrayList<Integer> getGroups(){return groups;}
    public void setGroups(ArrayList<Integer> groups){this.groups = groups;}

    public String getPictureUri(){return pictureUri;}
    public void setPictureUri(String pictureUri){this.pictureUri = pictureUri;}
}
