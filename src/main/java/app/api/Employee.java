package app.api;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Employee {
    @JsonProperty("id")
    private String id;


    @JsonProperty("employee_name")
    private String name;


    @JsonProperty("employee_salary")
    private String salary;


    @JsonProperty("employee_age")
    private String age;


    @JsonProperty("profile_image")
    private String profileImage;


    public Employee() { }


    public Employee(String id, String name, String salary, String age, String profileImage) {
        this.id = id;
        this.name = name;
        this.salary = salary;
        this.age = age;
        this.profileImage = profileImage;
    }


    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSalary() { return salary; }
    public void setSalary(String salary) { this.salary = salary; }
    public String getAge() { return age; }
    public void setAge(String age) { this.age = age; }
    public String getProfileImage() { return profileImage; }
    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }


    @Override
    public String toString() {
        return "Employee{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", salary='" + salary + '\'' +
                ", age='" + age + '\'' +
                ", profileImage='" + profileImage + '\'' +
                '}';
    }
}

