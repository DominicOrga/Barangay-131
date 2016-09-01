package javah.util;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * A class that will handle the generation of name nodes within a certain
 * mBox container.
 */
public class NodeNameHandler {

    /**
     * A class that serves as a container for nodes regarding a Node Name, such as a
     * text field for the first, middle and last name, and buttons to fire an event
     * to the Node Name Event Handler.
     *
     * @see NodeNameHandler
     */
    public static class NodeName extends HBox {

        /**
         * An interface to set a listener, the Node Name Handler, for the NodeName Class.
         *
         * @see NodeName
         */
        public interface OnNodeNameListener {
            /**
             * Remove this Node Name from its parent node. The parent node is the mBox passed
             * to the Node Name Handler.
             *
             * @param nodeName
             *        The Node Name to be removed from the Parent Node, which is this one.
             *
             * @see NodeNameHandler
             */
            void onRemoveButtonClicked(NodeName nodeName);

            /**
             * Tells the Node Name Handler containing the Node Name to display another Node
             * Name when possible.
             *
             * @see NodeNameHandler
             */
            void onAddButtonClicked();
        }

        /* Text Fields to allow name input. */
        private TextField mFirstName, mMiddleName, mLastName;

        /* The Combo Box for the auxiliary name. */
        private ComboBox<String> mAuxiliary;

        /**
         * Buttons that are used to fire action events to the Node Name Handler to either
         * remove or show another Node Name.
         *
         * @NodeNameHandler
         */
        private ImageView mRemoveButton, mAddButton;

        /**
         * The event listener for this Node Name. The Listener is a Node Name Handler, that
         * takes action when the Remove Button or Add Button of this Node Name is clicked.
         *
         * @NodeNameHandler
         */
        private OnNodeNameListener mListener;

        /**
         * A constructor that instantiates the nodes, and set the name and button nodes as
         * child nodes of the HBox Node Name Container.
         */
        public NodeName() {
            mFirstName = new TextField();
            mMiddleName = new TextField();
            mLastName = new TextField();

            mFirstName.setPromptText("First Name*");
            mMiddleName.setPromptText("Middle Name*");
            mLastName.setPromptText("Last Name*");

            mAuxiliary = new ComboBox<>();
            mAuxiliary.getItems().addAll("N/A", "Sr", "Jr", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X");
            mAuxiliary.setValue("N/A");

            mRemoveButton = new ImageView();
            mRemoveButton.setFitWidth(25);
            mRemoveButton.setFitHeight(25);

            mAddButton = new ImageView();
            mAddButton.setFitWidth(25);
            mAddButton.setFitHeight(25);

            mRemoveButton.setImage(new Image("res/ic_remove_circle.png"));
            mAddButton.setImage(new Image("res/ic_add_circle.png"));

            getChildren().addAll(mFirstName, mMiddleName, mLastName, mAuxiliary, mRemoveButton, mAddButton);

            // Tell the Node Name Handler to remove this node if this' remove button is
            // clicked.
            mRemoveButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                mListener.onRemoveButtonClicked(this);
            });

            // Tell the Node Name Handler to add another Node Name if possible.
            mAddButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                mListener.onAddButtonClicked();
            });
        }

        /**
         * Set the Node Name Handler listener for this Node Name.
         *
         * @param listener
         *        The listener for this Node Name.
         */
        public void setListener(OnNodeNameListener listener) {
            mListener = listener;
        }

        /**
         * Set the remove button of this Node Name to either visible or not.
         *
         * @param bool
         *        The boolean to determine to either hide or show the remove button.
         */
        public void setRemoveButtonVisible(boolean bool) {
            mRemoveButton.setVisible(bool);
        }

        /**
         * Set the add button of this Node Name to either visible or not.
         *
         * @param bool
         *        The boolean to determine to either hide or show the add button.
         */
        public void setAddButtonVisible(boolean bool) {
            mAddButton.setVisible(bool);
        }

        /**
         * Clear all the string within this Node Name's text fields. Also, reset the
         * Auxiliary to N/A.
         */
        public void clear() {
            mFirstName.clear();
            mMiddleName.clear();
            mLastName.clear();
            mAuxiliary.setValue("N/A");
        }

        public String getAuxiliary() {
            return mAuxiliary.getValue();
        }

        public void setAuxiliary(String aux) {
            mAuxiliary.setValue(aux);
        }

        public String getLastName() {
            return mLastName.getText();
        }

        public void setLastName(String name) {
            mLastName.setText(name);
        }

        public String getMiddleName() {
            return mMiddleName.getText();
        }

        public void setMiddleName(String name) {
            mMiddleName.setText(name);
        }

        public String getFirstName() {
            return mFirstName.getText();
        }
        public void setFirstName(String name) {
            mFirstName.setText(name);
        }
    }

    /**
     * A Vbox passed to this Node Name Handler by constructor where all Node Names are
     * added.
     */
    private VBox mBox;

    /* The total number of Node Names to be handled by this Node Name Handler. */
    private int mSize;

    /* An array for the Node Names. */
    private NodeName[] mNodeNames;

    /**
     * An array containing the positions of each name nodes within the mBox container.
     * Each index represents the node name at the Node Name array with the same index.
     * If an element is equal to 0, then the Node Name corresponding with that element
     * is considered as not visible. Position elements of the array is i > 0.
     */
    private int[] mNodeNamePositions;

    /**
     * An integer that represents the highest position from the Node Names Position
     * array. Initial value is 1, since at least one node should be visible.
     */
    private int mNodeNameHighestPos = 1;



    /**
     * A contructor that takes hold of the mBox to be populated with a number of Node
     * names equal to the given Limit.
     *
     * @param box
     *        The vertical box to be populated with node names.
     * @param size
     *        The total number of node names to generate.
     *
     * @see NodeName
     */
    public NodeNameHandler(VBox box, int size) {
        mBox = box;
        mSize = size;
        mNodeNames = new NodeName[size];
        mNodeNamePositions = new int[size];

        // At least one node name is visible by default.
        box.getChildren().add(mNodeNames[0]);
        mNodeNames[0].setRemoveButtonVisible(false);
        mNodeNamePositions[0] = 1;

        for (int i = 0; i < size; i++) {
            NodeName nodeName = new NodeName();

            final int j = i;
            nodeName.setListener(new NodeName.OnNodeNameListener() {
                @Override
                public void onRemoveButtonClicked(NodeName nodeName) {
                    // Remove the Node Name itself from the mBox if its remove button is clicked.
                    box.getChildren().remove(nodeName);
                    nodeName.clear();

                    // Decrement the Node Name Highest Position.
                    mNodeNameHighestPos--;

                    // Get the Node Name position.
                    int position = mNodeNamePositions[j];

                    // The node name index with the highest position.
                    int nodeNameHighPosIndex = -1;

                    for (int i = 0; i < size; i++) {
                        // Determine the index of the Node Name with the Highest Position.
                        if (mNodeNamePositions[i] == mNodeNameHighestPos)
                            nodeNameHighPosIndex = i;

                        // If a node name position is greater than the one removed, then move it backwards.
                        if (mNodeNamePositions[i] > position)
                            mNodeNamePositions[i]--;

                    }

                    // If only one Node Name is visible, then hide the remove button of that node name
                    // to prevent removing all Node Names.
                    if (mNodeNameHighestPos == 1)
                        mNodeNames[nodeNameHighPosIndex].setRemoveButtonVisible(false);

                    // Show the add button of the Node Name with the highest position, If the remove
                    // button is pressed, that is.
                    mNodeNames[nodeNameHighPosIndex].setAddButtonVisible(true);

                    // The Node Name position is set to 0, indicating that it is hidden.
                    mNodeNamePositions[j] = 0;
                }

                @Override
                public void onAddButtonClicked() {

                }
            });

            mNodeNames[i] = nodeName;
        }
    }

    /**
     * Set a non-visible Node Name visible from the Node Names Array by adding it to
     * the mBox, given with the highest position.
     *
     * @return the Node Name shown. Null if no other Node Names can be shown.
     */
    private NodeName showNodeName() {
        // All Node Names are already shown. Therefore, return null.
        if (mNodeNameHighestPos == mSize)
            return null;

        int nodeNameHighestPosIndex = -1;
        int nodeNameInvisibleIndex = -1;

        for (int i = 0; i < mSize; i++) {
            // Find the index of the Node Name with the preceding highest position.
            if (mNodeNamePositions[i] == mNodeNameHighestPos)
                nodeNameHighestPosIndex = i;

            // Find the closest invisible node name which will serve as the candidate to be
            // shown. If the element of an array at a certain index is equal to 0, then that
            // is an invisible node name.
            if (mNodeNamePositions[i] == 0 && nodeNameInvisibleIndex == -1)
                nodeNameInvisibleIndex = i;

            // If both the Node Name Highest Index and Node Name Invisible Index is found,
            // then cancel the loop.
            if (nodeNameInvisibleIndex != -1 && nodeNameHighestPosIndex != -1)
                break;
        }

        // Only show the remove button for the Node Name with the preceding Highest Position.
        mNodeNames[nodeNameHighestPosIndex].setAddButtonVisible(false);
        mNodeNames[nodeNameHighestPosIndex].setRemoveButtonVisible(true);

        // Increment the Node Name Highest Position index to pass to the Node Name to be
        // made visible, which is the first invisible Node Name.
        mNodeNameHighestPos++;

        // Assign the Highest Position to the first invisible Node Name, and make sure to show its remove button, and
        // add button if some of the Node Names are still invisible.
        mNodeNamePositions[nodeNameInvisibleIndex] = mNodeNameHighestPos;

        mNodeNames[nodeNameInvisibleIndex].setRemoveButtonVisible(true);
        mNodeNames[nodeNameInvisibleIndex].setAddButtonVisible(!(mNodeNameHighestPos == mSize));

        // Add the Node Name to the VBox to ensure that it is visible.
        mBox.getChildren().add(mNodeNames[nodeNameInvisibleIndex]);

        return mNodeNames[nodeNameInvisibleIndex];
    }

    /**
     * Add a name to the node name handler. Maximum number of names to be added is
     * equal to the size of the Node Name Handler. If a node name is available to
     * accept the name, then show the node name with the name. If a node name is
     * not available, the name addition is ignored.
     *
     * @param firstName
     *        The first name.
     * @param middleName
     *        The middle name.
     * @param lastName
     *        The last name.
     * @param auxiliary
     *        The auxiliary of the name.
     */
    public void addName(String firstName, String middleName, String lastName, String auxiliary) {
        NodeName nodeName = showNodeName();

        if (nodeName == null) return;

        nodeName.setFirstName(firstName);
        nodeName.setMiddleName(middleName);
        nodeName.setLastName(lastName);
        nodeName.setAuxiliary(auxiliary);
    }

    /**
     * Hide all the Node Names.
     */
    public void removeNames() {
        for (int i = 0; i < mSize; i++) {
            mNodeNamePositions[i] = 0;
            mNodeNames[i].clear();
        }

        mBox.getChildren().clear();
    }

}
