package sudtechnologies.analyticsdemo.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User implements Serializable{

    public static final String NAME = "name";
    public static final String LAST_NAME = "last_name";
    public static final String EMAILS = "emails";
    public static final String EMAIL = "email";

    private String name;
    private String lastName;
    private List<String> emails;

    public User() {
    }

    public User(String name, String lastName, List<String> emails) {
        this.name = name;
        this.lastName = lastName;
        this.emails = emails;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<String> getEmails() {
        return emails;
    }

    public void setEmails(List<String> emails) {
        this.emails = emails;
    }

    public Map<String, Object> toMap() {

        Map<String, Object> user = new HashMap<>();
        user.put(NAME, name);
        user.put(LAST_NAME, lastName);

        Map<String, Object> emails = new HashMap<>();
        for(String email: this.emails){
            emails.put(EMAIL, email);
        }

        user.put(EMAILS,emails);

        return user;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", lastName='" + lastName + '\'' +
                ", emails=" + emails +
                '}';
    }
}
