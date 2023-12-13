import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.util.*;
import java.util.List;

public class AdminControlPanel extends JFrame {
    private JTree userGroupTree;
    private JButton addUserButton, addGroupButton, openUserViewButton, showUserTotalButton, showGroupTotalButton,
            showMessageTotalButton, showPositivePercentageButton, validateIdsButton, findLastUpdatedUserButton;

    private Tree tree = new Tree();
    private UserView userView; // You need to initialize this

    public AdminControlPanel() {
        initializeComponents();
        setUpLayout();
        addActionListeners();
    }

    private void initializeComponents() {
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Root");
        userGroupTree = new JTree(rootNode);
        addUserButton = new JButton("Add User");
        addGroupButton = new JButton("Add Group");
        openUserViewButton = new JButton("Open User View");
        showUserTotalButton = new JButton("Show User Total");
        showGroupTotalButton = new JButton("Show Group Total");
        showMessageTotalButton = new JButton("Show Message Total");
        showPositivePercentageButton = new JButton("Show Positive Percentage");
        validateIdsButton = new JButton("Validate IDs");
        findLastUpdatedUserButton = new JButton("Find Last Updated User");
    }

    private void setUpLayout() {
        setLayout(new BorderLayout());
        add(new JScrollPane(userGroupTree), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(addUserButton);
        buttonPanel.add(addGroupButton);
        buttonPanel.add(openUserViewButton);
        buttonPanel.add(showUserTotalButton);
        buttonPanel.add(showGroupTotalButton);
        buttonPanel.add(showMessageTotalButton);
        buttonPanel.add(showPositivePercentageButton);
        buttonPanel.add(validateIdsButton);
        buttonPanel.add(findLastUpdatedUserButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addActionListeners() {
        addUserButton.addActionListener(e -> {
            String userName = JOptionPane.showInputDialog(this, "Enter new user name:", "Add User",
                    JOptionPane.PLAIN_MESSAGE);
            if (userName != null && !userName.trim().isEmpty()) {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) userGroupTree
                        .getLastSelectedPathComponent();
                if (selectedNode == null) {
                    selectedNode = (DefaultMutableTreeNode) userGroupTree.getModel().getRoot();
                }
                DefaultMutableTreeNode userNode = new DefaultMutableTreeNode(userName);
                selectedNode.add(userNode);
                ((DefaultTreeModel) userGroupTree.getModel()).reload(selectedNode);
            }
        });

        addGroupButton.addActionListener(e -> {
            String groupName = JOptionPane.showInputDialog(this, "Enter new group name:", "Add Group",
                    JOptionPane.PLAIN_MESSAGE);
            if (groupName != null && !groupName.trim().isEmpty()) {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) userGroupTree
                        .getLastSelectedPathComponent();
                if (selectedNode == null) {
                    selectedNode = (DefaultMutableTreeNode) userGroupTree.getModel().getRoot();
                }
                DefaultMutableTreeNode groupNode = new DefaultMutableTreeNode(groupName);
                selectedNode.add(groupNode);
                ((DefaultTreeModel) userGroupTree.getModel()).reload(selectedNode);
            }
        });

        openUserViewButton.addActionListener(e -> {
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) userGroupTree.getLastSelectedPathComponent();

            if (selectedNode != null) {
                Object userObject = selectedNode.getUserObject();

                if (userObject instanceof String) {
                    String userName = (String) userObject;
                    User selectedUser = new User(userName);

                    SwingUtilities.invokeLater(() -> {
                        userView = new UserView(new UserController(selectedUser));
                        userView.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                        userView.setSize(1000, 500);
                        userView.setLocationRelativeTo(null);
                        userView.setVisible(true);
                    });
                } else {
                    JOptionPane.showMessageDialog(this, "Please select a user to view.", "Invalid Selection",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a user to view.", "Invalid Selection",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        showUserTotalButton.addActionListener(e -> {
            int userTotal = countUsers((DefaultMutableTreeNode) userGroupTree.getModel().getRoot());
            JOptionPane.showMessageDialog(this, "Total Users: " + userTotal, "User Total",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        showGroupTotalButton.addActionListener(e -> {
            int groupTotal = countGroups((DefaultMutableTreeNode) userGroupTree.getModel().getRoot(), true);
            JOptionPane.showMessageDialog(this, "Total Groups: " + groupTotal, "Group Total",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        showMessageTotalButton.addActionListener(e -> {
            int messageTotal = countMessages((DefaultMutableTreeNode) userGroupTree.getModel().getRoot());
            JOptionPane.showMessageDialog(this, "Total Messages: " + messageTotal, "Message Total",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        showPositivePercentageButton.addActionListener(e -> {
            double positivePercentage = calculatePositivePercentage(
                    (DefaultMutableTreeNode) userGroupTree.getModel().getRoot());
            JOptionPane.showMessageDialog(this, "Positive Percentage: " + positivePercentage + "%",
                    "Positive Percentage",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        validateIdsButton.addActionListener(e -> {
            validateIds();
        });

        findLastUpdatedUserButton.addActionListener(e -> {
            String lastUpdatedUser = tree
                    .findLastUpdatedUser((DefaultMutableTreeNode) userGroupTree.getModel().getRoot());
            if (lastUpdatedUser != null) {
                JOptionPane.showMessageDialog(this, "Last Updated User: " + lastUpdatedUser,
                        "Last Updated User", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "No updates found.", "Last Updated User",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }

    private int countUsers(DefaultMutableTreeNode node) {
        if (node == null) {
            return 0;
        }

        int userCount = 0;

        Object userObject = node.getUserObject();
        if (userObject instanceof String) {
            // If the node represents a user, increment the user count
            userCount++;
        }

        // Traverse child nodes, counting users
        for (int i = 0; i < node.getChildCount(); i++) {
            userCount += countUsers((DefaultMutableTreeNode) node.getChildAt(i));
        }

        return userCount;
    }

    private int countGroups(DefaultMutableTreeNode node, boolean includeGroups) {
        if (node == null) {
            return 0;
        }

        int groupCount = isGroupNode(node) && includeGroups ? 1 : 0;

        for (int i = 0; i < node.getChildCount(); i++) {
            groupCount += countGroups((DefaultMutableTreeNode) node.getChildAt(i), includeGroups);
        }

        return groupCount;
    }

    private boolean isGroupNode(DefaultMutableTreeNode node) {
        // Implement the logic to determine if the node represents a group
        return node.getUserObject() instanceof String && ((String) node.getUserObject()).startsWith("Group");
    }

    private int countMessages(DefaultMutableTreeNode node) {
        if (node == null || !(node.getUserObject() instanceof String)) {
            return 0;
        }

        String userName = (String) node.getUserObject();
        return Tweet.countMessages(new User(userName));
    }

    private double calculatePositivePercentage(DefaultMutableTreeNode node) {
        if (node == null || !(node.getUserObject() instanceof String)) {
            return 0.0;
        }

        String userName = (String) node.getUserObject();

        // Assuming there is a method in UserView to get the displayed tweets
        List<Tweet> tweets = getUserViewTweets(userName);

        if (tweets.isEmpty()) {
            return 0.0;
        }

        int positiveCount = 0;
        int totalMessages = tweets.size();

        // List of positive words
        List<String> positiveWords = Arrays.asList("happy", "good", "great");

        for (Tweet tweet : tweets) {
            String message = tweet.getMessage().toLowerCase();
            for (String positiveWord : positiveWords) {
                if (message.contains(positiveWord)) {
                    positiveCount++;
                    break; // Break once a positive word is found in the message
                }
            }
        }

        // Calculate positive percentage
        return ((double) positiveCount / totalMessages) * 100.0;
    }

    // Helper method to get tweets from UserView (replace this with your actual
    // implementation)
    private List<Tweet> getUserViewTweets(String userName) {
        return Tweet.getUserViewTweets(new User(userName));
    }

    private void validateIds() {
        Set<String> allIds = new HashSet<>();
        if (!validateIds((DefaultMutableTreeNode) userGroupTree.getModel().getRoot(), allIds)) {
            JOptionPane.showMessageDialog(this, "Validation Failed: Duplicate or space-containing IDs found!",
                    "ID Validation", JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Validation Passed: All IDs are unique and space-free.",
                    "ID Validation", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private boolean validateIds(DefaultMutableTreeNode node, Set<String> allIds) {
        if (node == null) {
            return true;
        }

        Object userObject = node.getUserObject();
        if (userObject instanceof String) {
            String id = (String) userObject;
            if (allIds.contains(id) || id.contains(" ")) {
                return false; // Duplicate or space-containing ID found
            }
            allIds.add(id);
        }

        for (int i = 0; i < node.getChildCount(); i++) {
            if (!validateIds((DefaultMutableTreeNode) node.getChildAt(i), allIds)) {
                return false;
            }
        }

        return true;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AdminControlPanel frame = new AdminControlPanel();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setTitle("Admin Control Panel");
            frame.setSize(1400, 800);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
