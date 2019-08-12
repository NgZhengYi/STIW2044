package food_system.project.stiw2044.com.stiw2044_project;

public class UserInfo {
    private String email, password, name, phone, address, role;

    public UserInfo(){}

    public UserInfo(String email, String password, String name, String phone, String address, String role) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public String getRole() {
        return role;
    }
}
