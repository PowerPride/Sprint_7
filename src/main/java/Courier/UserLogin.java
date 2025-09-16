package Courier;

public class UserLogin {
    private String login;
    private String password;

    public UserLogin(String login, String password) {
        this.login = login;
        this.password = password;

    }
    public static UserLogin fromUser(User user) {
        return new UserLogin(user.getLogin(), user.getPassword());
    }

}
//{
//        "login": "ninja",
//        "password": "1234"
//        }