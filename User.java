import java.util.ArrayList;
import java.util.List;

public class User {
    private String userId;
    private List<User> followers;
    private List<User> following;
    private List<Tweet> newsFeed;
    private long creationTime;
    private long lastUpdateTime;

    public User(String userId) {
        this.userId = userId;
        this.followers = new ArrayList<>();
        this.following = new ArrayList<>();
        this.newsFeed = new ArrayList<>();
        this.creationTime = System.currentTimeMillis();
        this.lastUpdateTime = this.creationTime;
    }

    public String getUserId() {
        return userId;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void follow(User userToFollow) {
        if (!this.following.contains(userToFollow)) {
            this.following.add(userToFollow);
            userToFollow.addFollower(this);
            updateLastUpdateTime();
        }
    }

    public List<User> getFollowing() {
        return following;
    }

    private void addFollower(User follower) {
        if (!this.followers.contains(follower)) {
            this.followers.add(follower);
        }
    }

    public void postTweet(String message) {
        Tweet tweet = new Tweet(this, message);
        this.newsFeed.add(0, tweet); // Add to the start of the feed
        for (User follower : followers) {
            follower.receiveTweet(tweet);
        }
        updateLastUpdateTime();
    }

    public List<Tweet> getNewsFeed() {
        return newsFeed;
    }

    private void receiveTweet(Tweet tweet) {
        this.newsFeed.add(0, tweet);
        updateLastUpdateTime();
    }

    private void updateLastUpdateTime() {
        this.lastUpdateTime = System.currentTimeMillis();
    }

    // Additional methods to get followers, following, and news feed
}
