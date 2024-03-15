package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import javax.swing.JFrame;

import view.SudokuView;

public class Sudoku {

    static JFrame frame;
    static SudokuView b;
    private static int[][] grid;  // gtri của lưới ban đầu
    private static int[][] temp;  // giá trị của lưới sau khi thay đổi
    private static Random random = new Random();
    private static int level = 2;

    public static void setlevel(int lv) {
        level = lv;
    }

    public static ArrayList<Integer> getRandomNum() {
        ArrayList<Integer> numbers = new ArrayList<Integer>();
        for (Integer i = 1; i < 10; i++) {
            numbers.add(i);
        }
        Collections.shuffle(numbers);
        return numbers;
    }

    public static int[][] getList_nullTF(int[][] grid) {
        int sizenullTF = 0;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (grid[i][j] == 0) {
                    sizenullTF++;
                }
            }
        }
        int[][] list_nullTF = new int[sizenullTF][2];
        int count = 0;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (grid[i][j] == 0) {
                    list_nullTF[count][0] = i;
                    list_nullTF[count][1] = j;
                    count++;
                }
            }
        }
        return list_nullTF;
    }

    public static boolean valid(int i, int j, int[][] grid) {
        // Check row
        for (int column = 0; column < 9; column++) {
            if (column != j && grid[i][column] == grid[i][j]) {
                return false;
            }
        }
        // Check column
        for (int row = 0; row < 9; row++) {
            if (row != i && grid[row][j] == grid[i][j]) {
                return false;
            }
        }
        // Check grid 3x3
        for (int row = (i / 3) * 3; row < (i / 3) * 3 + 3; row++) {
            for (int col = (j / 3) * 3; col < (j / 3) * 3 + 3; col++) {
                if (row != i && col != j && grid[row][col] == grid[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean Backtracking(int[][] grid) {
        int[][] list_nullTF = getList_nullTF(grid); //tạo mảng listNull, lấy ds các ô trống 
        int k = 0; //duyệt k qua từng ô 
        boolean found = false; //tìm giải pháp. 
        while (!found) { //1. giải 
            int i = list_nullTF[k][0]; // lấy toạ độ ô trống dòng i
            int j = list_nullTF[k][1]; //lấy tọa độ ô trống cột j

            if (grid[i][j] == 0) { //2. gán gtri cho ô trống = 1 và bắt đầu giải
                grid[i][j] = 1;
            }
            if (valid(i, j, grid)) { //3. kiểm tra gtri là hợp lệ 
                if (k + 1 == list_nullTF.length) {  //4. nếu các ô trống đã được giải. thoát khỏi thuật toán
                    found = true;
                } else { // ngược lại tăng k để duyệt ô tiếp theo
                    k++;
                }
            } else if (grid[i][j] < 9) { //5. giá trị của ô nhỏ hơn 9, thử giá trị tiếp theo = cách tăng giá trị của ô lên 1 
                grid[i][j] = grid[i][j] + 1;
            } else { // 6. Nếu gtr ô hiện tại = 9, thực hiện quay lui 
                while (grid[i][j] == 9) {
                    grid[i][j] = 0;
                    if (k == 0) { // k đã quay lại ô đầu tiên mà vẫn ko tìm được giải pháp, trả về F để kết thúc thuật toán
                        return false;
                    }
                    k--; // k được di chuyển ngược lại ô trước đó, để thử giá trị khác
                    i = list_nullTF[k][0];
                    j = list_nullTF[k][1];
                }
                grid[i][j] = grid[i][j] + 1; // thử giá trị khác sau khi quay lui 
            }
        }
        return true;
    }

    public static void setNum() {
        int k = 0;
        ArrayList<Integer> rdnumber = getRandomNum();

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                grid[i][j] = 0;
                if (((j + 2) % 2) == 0 && ((i + 2) % 2) == 0) {  // Nếu cả chỉ số dòng (i) và chỉ số cột (j) đều chẵn, gán giá trị từ danh sách rdnumber. 
                    grid[i][j] = rdnumber.get(k);
                    k++; // Tăng k và nếu k đạt đến 9, đặt lại nó về 0.           
                    if (k == 9) { // Nếu đã gán hết 9 giá trị, đặt lại k về 0
                        k = 0;
                    }
                }
            }
        }
        Backtracking(grid);
        int rd = random.nextInt(level);
        int c = 0; //Biến kiểm soát việc xóa ngẫu nhiên một số ô từ bảng Sudoku
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                temp[i][j] = 0;
                if (c < rd) {  // Nếu c vẫn nhỏ hơn rd, tăng c và tiếp tục vòng lặp
                    c++;
                    continue;
                } else { // Nếu c đã đạt đến rd, đặt lại c về 0, sinh rd mới, và gán giá trị từ grid sang temp
                    rd = random.nextInt(level);
                    c = 0;
                    temp[i][j] = grid[i][j];
                    // Sao chép giá trị từ ô tương ứng trong mảng grid sang mảng temp, hiệu quả là bao gồm ô này trong bảng Sudoku đã được sửa đổi.
                }
            }
        }
        // Đặt giá trị bảng Sudoku và cập nhật giao diện người dùngy(grid, temp);
        b.setarray(grid, temp);
        b.setTextLabel();
    }

    public static void main(String[] args) {
        grid = new int[9][9];
        temp = new int[9][9];
        frame = new JFrame();
        frame.setResizable(false);
        frame.setLocation(320, 60);
        frame.setSize(900, 600);
        frame.setTitle("Game Sudoku");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        b = new SudokuView();
        frame.setContentPane(b);
        frame.setVisible(true);
    }
}
