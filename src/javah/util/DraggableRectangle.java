package javah.util;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.util.Arrays;

/**
 * The class to create a draggable square for image cropping.
 */
public class DraggableRectangle extends Rectangle {

    /* The radius of the handles to manipulate the rectangle. */
    protected double handleRadius = 10;

    /* Circles used to manipulate the rectangle. */
    protected Circle mResizeHandleNW, mResizeHandleSE, mMoveHandle;

    /**
     * The aspect ratio of the rectangle, so that resizing the rectangle will always
     * maintain the given ratio.
     */
    protected double mAspectRatio;

    /**
     * A constructor that takes the containing boundary of the rectangle. Initializes
     * the handles and binds it to this rectangle.
     *
     * @param boundaryWidth
     *        The width boundary of this rectangle's parent node.
     * @param boundaryHeight
     *        This height boundary of this rectangle's parent node.
     */
    public DraggableRectangle(int boundaryWidth, int boundaryHeight) {
        super(0, 0, 50, 50);

        this.setFill(Color.TRANSPARENT);
        this.setStroke(Color.WHITE);

        // top left resize handle:
        mResizeHandleNW = new Circle(handleRadius, Color.GOLD);
        // bind to top left corner of Rectangle:
        mResizeHandleNW.centerXProperty().bind(this.xProperty());
        mResizeHandleNW.centerYProperty().bind(this.yProperty());

        // bottom right resize handle:
        mResizeHandleSE = new Circle(handleRadius, Color.GOLD);
        // bind to bottom right corner of Rectangle:
        mResizeHandleSE.centerXProperty().bind(this.xProperty().add(this.widthProperty()));
        mResizeHandleSE.centerYProperty().bind(this.yProperty().add(this.heightProperty()));

        // move handle:
        mMoveHandle = new Circle(handleRadius, Color.GOLD);
        // bind to bottom center of Rectangle:
        mMoveHandle.centerXProperty().bind(this.xProperty().add(this.widthProperty().divide(2)));
        mMoveHandle.centerYProperty().bind(this.yProperty().add(this.heightProperty()));

        // force circles to live in same parent as rectangle:
        this.parentProperty().addListener((obs, oldParent, newParent) -> {
            for (Circle c : Arrays.asList(mResizeHandleNW, mResizeHandleSE, mMoveHandle)) {
                Pane currentParent = (Pane)c.getParent();
                if (currentParent != null) {
                    currentParent.getChildren().remove(c);
                }
                ((Pane)newParent).getChildren().add(c);
            }
        });

        mResizeHandleNW.setOnMouseDragged(event -> {
            double deltaX = event.getX() - this.getX();
            double newWidth = this.getWidth() - deltaX;
            double newHeight = newWidth / mAspectRatio;
            double newX = event.getX();
            double newY = this.getY() + (this.getHeight() - newHeight);

            // Drag HandleNW if the handle is still inside the pane and the HandleNW is not touching HandleSE.
            if (newX > handleRadius && newY > handleRadius
                    && newX < this.getX() + this.getWidth() - handleRadius
                    && newY < this.getY() + this.getHeight() - handleRadius) {
                this.setX(newX);
                this.setY(newY);

                this.setWidth(newWidth);
                this.setHeight(newHeight);
            }
        });

        mResizeHandleSE.setOnMouseDragged(event -> {
            double deltaX = event.getX() - (this.getX() + this.getWidth());
            double newWidth = this.getWidth() + deltaX;
            double newHeight = newWidth / mAspectRatio;
            double newX = this.getX() + newWidth;
            double newY = this.getY() + newHeight;

            // Drag HandleSE if the handle is still inside the pane and the HandleSE is not touching HandleNW.
            if ( newX < boundaryWidth - handleRadius && newY < boundaryHeight - handleRadius &&
                    newX > this.getX() + handleRadius && newY > this.getY() + handleRadius) {
                this.setWidth(this.getWidth() + deltaX);
                this.setHeight(this.getWidth() / mAspectRatio);
            }
        });

        mMoveHandle.setOnMouseDragged(event -> {
            double deltaX = event.getX() - (this.getX() + this.getWidth() / 2);
            double deltaY = event.getY() - (this.getHeight() + this.getY());

            // New HandleNW Coordinates.
            double newX1 = this.getX() + deltaX;
            double newY1 = this.getY() + deltaY;

            // New HandleSE Coordinates.
            double newX2 = newX1 + this.getWidth();
            double newY2 = newY1 + this.getHeight();

            // Drag the square along the x-axis if it does not touch either of the edges.
            if (newX1 > handleRadius && newX2 < boundaryWidth - handleRadius)
                this.setX(newX1);

            // Drag the square along the y-axis if it does not touch either of the edges.
            if (newY1 > handleRadius && newY2 < boundaryHeight - handleRadius)
                this.setY(newY1);
        });

        mResizeHandleNW.visibleProperty().bind(this.visibleProperty());
        mResizeHandleSE.visibleProperty().bind(this.visibleProperty());
        mMoveHandle.visibleProperty().bind(this.visibleProperty());
    }

    /**
     * Assign the aspect ratio of the rectangle for resizing.
     *
     * @param width
     *        The width of the rectangle.
     * @param height
     *        The height of the rectangle.
     */
    public void setAspectRatio(double width, double height) {
        mAspectRatio = width / height;
    }
}
