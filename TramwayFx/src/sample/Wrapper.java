package sample;

import javafx.animation.PathTransition;
import javafx.scene.shape.Shape;

public class Wrapper {

    public Shape shape;
    public PathTransition pathTransition;

    public Wrapper(Shape shape, PathTransition pathTransition) {
        this.shape = shape;
        this.pathTransition = pathTransition;

    }

}
