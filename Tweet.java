import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tweet {
    private User author;
    private String message;
    private Date timestamp;

    private static Map<User, List<String>> userMessages = new HashMap<>();

    public Tweet(User author, String message) {
        this.author = author;
        this.message = message;
        this.timestamp = new Date(); // Set the timestamp to the current time

        // Update userMessages map
        if (!userMessages.containsKey(author)) {
            userMessages.put(author, new ArrayList<>());
        }
        userMessages.get(author).add(message);
    }

    public User getUser() {
        return author;
    }

    public String getMessage() {
        return message;
    }

    public static int countMessages(User user) {
        List<String> messages = userMessages.get(user);

        if (messages == null) {
            return 0;
        }

        return messages.size();
    }

    public static List<Tweet> getUserViewTweets(User user) {
        List<String> messages = getUserMessages(user);
        List<Tweet> tweets = new ArrayList<>();

        // Convert messages to Tweet objects (assuming a simple constructor)
        for (String message : messages) {
            tweets.add(new Tweet(user, message));
        }

        return tweets;
    }

    public static double calculatePositivePercentage(User user) {
        List<String> messages = userMessages.get(user);

        if (messages == null || messages.isEmpty()) {
            return 0.0;
        }

        int positiveCount = 0;

        // Positive words
        List<String> positiveWords = List.of("happy", "good", "great");

        for (String message : messages) {
            if (containsPositiveWord(message, positiveWords)) {
                positiveCount++;
            }
        }

        return ((double) positiveCount / messages.size()) * 100.0;
    }

    private static boolean containsPositiveWord(String message, List<String> positiveWords) {
        // Check if the message contains any positive word
        for (String positiveWord : positiveWords) {
            if (message.toLowerCase().contains(positiveWord.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    // Helper method to get messages for a user
    public static List<String> getUserMessages(User user) {
        return userMessages.getOrDefault(user, new ArrayList<>());
    }
}
