import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class UserView extends JFrame {
    private UserController userController;
    private JTextArea tweetTextArea;
    private JButton postTweetButton;
    private JTextField followUserField;
    private JButton followUserButton;
    private JTextArea followingListView;
    private JTextArea tweetListView;
    private JLabel creationTimeLabel;
    private JLabel lastUpdateTimeLabel;

    public UserView(UserController userController) {
        this.userController = userController;
        initializeComponents();
        setupLayout();
        addListeners();
        updateUserInfo();
    }

    private void initializeComponents() {
        tweetTextArea = new JTextArea(5, 20);
        postTweetButton = new JButton("Post Tweet");
        followUserField = new JTextField(20);
        followUserButton = new JButton("Follow User");
        followingListView = new JTextArea(5, 20);
        tweetListView = new JTextArea(10, 30);
        tweetListView.setEditable(false); // Make it read-only
        creationTimeLabel = new JLabel();
        lastUpdateTimeLabel = new JLabel();
    }

    private void setupLayout() {
        setTitle("User View");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("User ID: " + userController.getUser().getUserId()));
        topPanel.add(new JLabel("Creation Time: " + formatTimestamp(userController.getUser().getCreationTime())));
        topPanel.add(new JLabel("Last Update Time: " + formatTimestamp(userController.getUser().getLastUpdateTime())));
        topPanel.add(followUserField);
        topPanel.add(followUserButton);

        JPanel followingPanel = new JPanel(new BorderLayout());
        followingPanel.setBorder(BorderFactory.createTitledBorder("Current Following"));
        followingPanel.add(new JScrollPane(followingListView), BorderLayout.CENTER);

        JPanel tweetPanel = new JPanel(new BorderLayout());
        tweetPanel.setBorder(BorderFactory.createTitledBorder("Tweet Message"));
        tweetPanel.add(new JScrollPane(tweetTextArea), BorderLayout.NORTH);

        JPanel tweetButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        tweetButtonPanel.add(postTweetButton);
        tweetPanel.add(tweetButtonPanel, BorderLayout.CENTER);

        JPanel tweetListPanel = new JPanel(new BorderLayout());
        tweetListPanel.setBorder(BorderFactory.createTitledBorder("Tweet List"));
        tweetListPanel.add(new JScrollPane(tweetListView), BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);
        add(followingPanel, BorderLayout.CENTER);
        add(tweetPanel, BorderLayout.SOUTH);
        add(tweetListPanel, BorderLayout.EAST);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private String formatTimestamp(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date(timestamp));
    }

    private void addListeners() {
        postTweetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = tweetTextArea.getText();
                if (!message.isEmpty()) {
                    userController.postNewTweet(message);
                    tweetTextArea.setText("");
                    updateTweetListView();
                }
            }
        });

        followUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String usernameToFollow = followUserField.getText();
                if (!usernameToFollow.isEmpty()) {
                    User userToFollow = new User(usernameToFollow);
                    userController.followUser(userToFollow);
                    followUserField.setText("");
                    updateFollowingListView();
                    updateUserInfo(); // Update the "Follow User" button text
                }
            }
        });
    }

    private List<Tweet> getTweetsForUser(String userName) {
        User user = getUserByName(userName);

        if (user != null) {
            return Tweet.getUserViewTweets(user);
        } else {
            return List.of();
        }
    }

    private User getUserByName(String userName) {
        // Implement this method based on how you retrieve a User object by user name
        // For example, you might have a UserDatabase class or a similar mechanism
        return null; // Replace this with your actual implementation
    }

    private void updateUserInfo() {
        // Update "Follow User" button text
        followUserButton.setText("Follow User");
    }

    private void updateFollowingListView() {
        StringBuilder following = new StringBuilder();
        for (User followingUser : userController.getUser().getFollowing()) {
            following.append(followingUser.getUserId()).append("\n");
        }
        followingListView.setText(following.toString());
    }

    private void updateTweetListView() {
        StringBuilder tweets = new StringBuilder();
        for (Tweet tweet : userController.getUser().getNewsFeed()) {
            tweets.append(tweet.getUser().getUserId()).append(": ").append(tweet.getMessage()).append("\n");
        }
        tweetListView.setText(tweets.toString());
    }

    // Main method for testing
    public static void main(String[] args) {
        User testUser = new User("testUser");
        UserController userController = new UserController(testUser);
        new UserView(userController);
    }
}
