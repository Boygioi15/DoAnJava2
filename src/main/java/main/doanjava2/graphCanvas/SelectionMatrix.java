package main.doanjava2.graphCanvas;

import javafx.scene.canvas.Canvas;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class SelectionMatrix {
    Setting settingRef;
    Canvas backgroundCanvas;

    private static int range = 5;
    private List<boolean[][]> selectionMatrix = new ArrayList<>();
    int w, h;
    public SelectionMatrix(Setting ref) {
        settingRef = ref;
        backgroundCanvas = new Canvas();
    }
    public int GetNearbyGraphIndex(int mouseX, int mouseY){
        return -1;
    }
    public Pair<Integer,Integer> GetClosePoint(int graphIndex, int mouseX, int mouseY){
        return null;
    }
    public void PlotPoint(int layer, int  x, int y){
        if(x<w&&y<h &&x>0&&y>0){
            selectionMatrix.get(layer)[x][y] = true;
        }
    }
    public void AddNewLayer(int pos){
        selectionMatrix.add(pos,new boolean[w][h]);
    }
    public void RemoveLayer(int pos){
        selectionMatrix.remove(pos);
    }
    public void ClearLayer(int pos){
        selectionMatrix.set(pos,new boolean[w][h]);
    }
    public void InitNewSize(int w, int h){
        for(boolean[][] layer : selectionMatrix){
            layer = new boolean[w][h];
        }
        this.w = w;
        this.h = h;
    }
}
