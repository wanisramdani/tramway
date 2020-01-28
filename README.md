# DAC Tramway
Concurrent Programming assignment

See the PlantUML class diagram:
![](tramway-uml.png)

## Problems
### Traffic intersection
```
Car1::enter
===========
p(vide1);
p(feu1);

Car1::leave
===========
p(feu1);
p(vide1);
```

```
Car2::enter
===========
p(vide2);
p(feu2);

Car2::leave
===========
p(feu2);
p(vide2);
```

```
Tram::enter
===========
p(mutex);
passingTrams++;
if (passingTrams == 1) {
  v(mutex);
  p(feu1);
  p(feu2);
} else {
  v(mutex);
}

Tram::leave
===========
p(mutex);
passingTrams--;
if (passingTrams == 0) {
  v(mutex);
  v(feu1);
  v(feu2);
} else {
  v(mutex);
}
```

### Bridge crossing
```
...
```

## JavaFX Animation
- To actually animate `Car`s and `Tram`s: [PathTransition](https://docs.oracle.com/javase/8/javafx/api/javafx/animation/PathTransition.html)

- To `play`, `pause`, and manually progress (`jumpTo` or `playFrom`) animations: [Timeline](https://docs.oracle.com/javase/8/javafx/api/javafx/animation/Timeline.html)

- To update relative/paused trams: [AnimationTimer](https://docs.oracle.com/javase/8/javafx/api/javafx/animation/AnimationTimer.html)

- MAYBE to be notified of Trams' animation progress: [ObservableList](https://docs.oracle.com/javase/8/javafx/api/javafx/collections/ObservableList.html) and/or [KeyFrame](https://docs.oracle.com/javase/8/javafx/api/javafx/animation/KeyFrame.html)
