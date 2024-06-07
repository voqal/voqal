public class UserService {
    private UserManagement management;

    public void addTestUser() {
        management.addUser("test", "test_pass");
    }
}