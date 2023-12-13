public class UserController {
    private User user;

    public UserController(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void followUser(User userToFollow) {
        user.follow(userToFollow);
    }

    public void postNewTweet(String message) {
        user.postTweet(message);
    }

    // Additional methods to retrieve user information
}
