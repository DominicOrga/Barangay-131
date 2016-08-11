package javah.util;

import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.util.List;

/**
 * This class will break down the kagawad holder nodes to an intuitive kagawad holder.
 */
public class KagawadHolder {
    private TextField mFirstName, mMiddleName, mLastName;
    private ImageView mRemoveButton, mAddButton;

    private Node mKagawadHolderPane;

    public KagawadHolder(Node kagawadHolderPane) {
        mKagawadHolderPane = kagawadHolderPane;

        List<Node> kagawadHolder = ((Pane) kagawadHolderPane).getChildren();

        mFirstName = (TextField) kagawadHolder.get(0);
        mMiddleName = (TextField) kagawadHolder.get(1);
        mLastName = (TextField) kagawadHolder.get(2);
        mRemoveButton = (ImageView) kagawadHolder.get(3);
        mAddButton = (ImageView) kagawadHolder.get(4);
    }

    public TextField getFirstNameField() {
        return mFirstName;
    }

    public TextField getMiddleNameField() {
        return mMiddleName;
    }

    public TextField getLastNameField() {
        return mLastName;
    }

    public ImageView getRemoveButton() {
        return mRemoveButton;
    }

    public ImageView getAddButton() {
        return mAddButton;
    }

    public Node getNode() {
        return mKagawadHolderPane;
    }

    public void setVisible(boolean b) {
        mKagawadHolderPane.setVisible(b);
    }

    public void setManaged(boolean b) {
        mKagawadHolderPane.setManaged(b);
    }
}
