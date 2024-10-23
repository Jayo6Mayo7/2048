import kotlin.random.URandomKt;

import java.util.ArrayList;
import java.util.List;

public class twentyFortyEight {
    int spawnOne = 2;
    int spawnOneChance = 90;
    int spawnTwo = 2;
    int spawnTwoChance = 10;

    static class OcuSquare {
        boolean occupied = false;
        boolean combined = false;
    }
    static class ProtoSquare {
        int X;
        int Y;
    }
    static List<OcuSquare> list1 = new ArrayList<>(4);
    static List<OcuSquare> ocuGrid = new ArrayList<>(4);

    static void occupy(int x) {
        ocuGrid.get(x).occupied = true;
    }
    static void unoccupy(int x) {
        ocuGrid.get(x).occupied = true;
    }
    static void startSquare() {
        while (true) {

        }
    }
    //put real stuff here lol
    public static void main(String args[]) {
        OcuSquare ocuSquare = new OcuSquare();
        list1.add(ocuSquare);
        list1.add(ocuSquare);
        list1.add(ocuSquare);
        list1.add(ocuSquare);
        ocuGrid.add(list1.get(1));
        ocuGrid.add(list1.get(1));
        ocuGrid.add(list1.get(1));
        ocuGrid.add(list1.get(1));
        ocuGrid.add(list1.get(1));
        ocuGrid.add(list1.get(1));
        ocuGrid.add(list1.get(1));
        ocuGrid.add(list1.get(1));
        ocuGrid.add(list1.get(1));
        ocuGrid.add(list1.get(1));
        ocuGrid.add(list1.get(1));
        ocuGrid.add(list1.get(1));
        ocuGrid.add(list1.get(1));
        ocuGrid.add(list1.get(1));
        ocuGrid.add(list1.get(1));
        ocuGrid.add(list1.get(1));

        occupy(15);
    }
}
