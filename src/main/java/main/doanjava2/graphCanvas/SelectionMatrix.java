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
    public int GetNearbyGraphIndex(int mouseX, int mouseY) {
        // Kiểm tra nếu tọa độ chuột nằm trong giới hạn
        if (mouseX < 0 || mouseY < 0 || mouseX >= w || mouseY >= h) {
            return -1;
        }
        for (int layer = 0; layer < selectionMatrix.size(); layer++) {
            boolean[][] currentLayer = selectionMatrix.get(layer);
        }
        // Lặp qua từng lớp
        for (int layer = 0; layer < selectionMatrix.size(); layer++) {
            boolean[][] currentLayer = selectionMatrix.get(layer);
            // Kiểm tra các điểm xung quanh trong phạm vi đã chỉ định
            for (int dx = -range; dx <= range; dx++) {
                for (int dy = -range; dy <= range; dy++) {
                    int checkX = mouseX + dx;
                    int checkY = mouseY + dy;

                    // Đảm bảo các điểm nằm trong giới hạn
                    if (checkX >= 0 && checkX < w && checkY >= 0 && checkY < h) {
                        if (currentLayer[checkX][checkY]) {
                            return layer; // Trả về chỉ mục lớp nếu tìm thấy điểm
                        }
                    }
                }
            }
        }
        // Trả về -1 nếu không tìm thấy điểm nào gần đó
        return -1;
    }

    public Pair<Integer,Integer> GetClosePoint(int graphIndex, int mouseX, int mouseY){
        // Tìm ma trận lớp tương ứng
        boolean[][] currentLayer = selectionMatrix.get(graphIndex);

        int closestX = -1;
        int closestY = -1;
        double minDistance = Double.MAX_VALUE;

        // Lặp qua tất cả các điểm trên đồ thị và tìm điểm gần nhất
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                if (currentLayer[x][y]) {
                    // Tính khoảng cách Euclidean giữa điểm trên đồ thị và tọa độ chuột
                    double distance = Math.sqrt(Math.pow(mouseX - x, 2) + Math.pow(mouseY - y, 2));
                    if (distance < minDistance) {
                        minDistance = distance;
                        closestX = x;
                        closestY = y;
                    }
                }
            }
        }

        // Trả về cặp tọa độ của điểm gần nhất trên đồ thị
        if (closestX != -1 && closestY != -1) {
            return new Pair<>(closestX, closestY);
        } else {
            return null; // Trả về null nếu không tìm thấy điểm gần nhất
        }
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
        for(int i = 0;i<selectionMatrix.size();i++){
            selectionMatrix.set(i,new boolean[w][h]);
        }
        this.w = w;
        this.h = h;
    }
}
