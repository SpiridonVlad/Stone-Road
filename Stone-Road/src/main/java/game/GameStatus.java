package game;


import java.io.Serializable;
import java.util.List;

public record GameStatus(List<Boolean> lines, List<Integer> circles, int turn, boolean startedGame) implements Serializable {
}
