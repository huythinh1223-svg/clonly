package user.code;

public abstract class User {
    private String id;
    private String username;
    private String password;
    private String fullname;
    private String email;
    private boolean isverify = false; // dùng để kiểm tra tính xác thực của tài khoản.
// Định nghĩa hàm Role
    private enum Role {
        BIDDER,
        SELLER,
        ADMIN
    }
// tạo đối tượng role
    public Role role;

    public User ( String id, String username , String password, String fullname, String email, Role role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.fullname = fullname;
        this.email = email;
        this.role = role;

    }
// get, set id
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

// get ,set username

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

// get, set passworld

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

// get , set full name
    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

// get , set email
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

// get, set Role
    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

//get,set verify


    public boolean isIsverify() {
        return isverify;
    }

    public void setIsverify(boolean isverify) {
        this.isverify = isverify;
    }

    // lớp trừu tuong the hiện thông tin
    public abstract void displayInfo();
// lớp trừu tượng hiển thị vai trò của một đối tượng
    public abstract void getPermissions();
}
