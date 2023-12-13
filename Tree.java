import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.util.HashSet;
import java.util.Set;

public class Tree {
    private DefaultTreeModel treeModel;
    private DefaultMutableTreeNode rootNode;

    private Set<String> userIds; // Store user IDs for uniqueness validation
    private long latestUpdateTime; // Track the latest update time

    public Tree() {
        rootNode = new DefaultMutableTreeNode("Root");
        treeModel = new DefaultTreeModel(rootNode);

        userIds = new HashSet<>();
        latestUpdateTime = 0;
    }

    public DefaultTreeModel getTreeModel() {
        return treeModel;
    }

    public void addUser(String userId) {
        if (!userIds.contains(userId)) {
            userIds.add(userId);

            DefaultMutableTreeNode userNode = new DefaultMutableTreeNode(userId);
            treeModel.insertNodeInto(userNode, rootNode, rootNode.getChildCount());
        }
    }

    public void addGroup(String groupId) {
        DefaultMutableTreeNode groupNode = new DefaultMutableTreeNode(groupId);
        treeModel.insertNodeInto(groupNode, rootNode, rootNode.getChildCount());
    }

    public void addUserToGroup(String userId, String groupId) {
        DefaultMutableTreeNode groupNode = findNode(groupId);
        if (groupNode != null && !userIds.contains(userId)) {
            userIds.add(userId);

            DefaultMutableTreeNode userNode = new DefaultMutableTreeNode(userId);
            treeModel.insertNodeInto(userNode, groupNode, groupNode.getChildCount());
        }
    }

    public int countGroups(String groupId) {
        DefaultMutableTreeNode groupNode = findNode(groupId);
        return (groupNode != null) ? countGroups(groupNode, true) : 0;
    }

    private int countGroups(DefaultMutableTreeNode node, boolean includeGroups) {
        int groupCount = isGroupNode(node) && includeGroups ? 1 : 0;

        for (int i = 0; i < node.getChildCount(); i++) {
            groupCount += countGroups((DefaultMutableTreeNode) node.getChildAt(i), includeGroups);
        }

        return groupCount;
    }

    public String findLastUpdatedUser(DefaultMutableTreeNode node) {
        if (node == null) {
            return null;
        }

        Object userObject = node.getUserObject();
        String lastUpdatedUser = null;

        if (userObject instanceof String) {
            // Check if it's a user, if yes, set lastUpdatedUser to this user
            lastUpdatedUser = (String) userObject;
        }

        // Recursively check child nodes, excluding groups
        for (int i = 0; i < node.getChildCount(); i++) {
            DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) node.getChildAt(i);

            // Skip nodes representing groups
            if (isGroupNode(childNode)) {
                continue;
            }

            String childLastUpdatedUser = findLastUpdatedUser(childNode);

            // Update lastUpdatedUser if the child node has a more recent update
            if (childLastUpdatedUser != null) {
                lastUpdatedUser = childLastUpdatedUser;
            }
        }

        return lastUpdatedUser;
    }

    private boolean isGroupNode(DefaultMutableTreeNode node) {
        return node.getUserObject() instanceof String && ((String) node.getUserObject()).startsWith("Group");
    }

    private DefaultMutableTreeNode findNode(String nodeId) {
        return findNode(rootNode, nodeId);
    }

    private DefaultMutableTreeNode findNode(DefaultMutableTreeNode rootNode, String nodeId) {
        if (rootNode.getUserObject().equals(nodeId)) {
            return rootNode;
        }
        for (int i = 0; i < rootNode.getChildCount(); i++) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) rootNode.getChildAt(i);
            DefaultMutableTreeNode result = findNode(node, nodeId);
            if (result != null) {
                return result;
            }
        }
        return null;
    }
}
