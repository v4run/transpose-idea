import java.util.ArrayList;

class Transposee {
    private ArrayList<String> list;
    private boolean havingSelection;

    boolean isHavingSelection() {
        return havingSelection;
    }

    void hasSelection() {
        this.havingSelection = true;
    }

    ArrayList<String> getList() {
        return list;
    }

    void setList(ArrayList<String> list) {
        this.list = list;
    }
}
