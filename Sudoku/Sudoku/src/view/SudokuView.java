package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import model.Sudoku;

public class SudokuView extends javax.swing.JPanel {

    //Khai báo các biến 
    Sudoku game;
    private static JTextField[][] jTF;
    public StopWatch stopWatch;
    private boolean paused;
    private final JLabel jLabel_time;   // bảng chạy thời gian trò chơi
    private final JButton jButton_pause, jButton_resume; // button lệnh 
    // private final JButton jButton_resume;
    private static JPanel[][] paneles;
    private JPanel jPanel_Main, //JPanel chứa ma trận
            jPanel_Control, //JPanel chứa các nút lệnh
            jPanel_Time;		//JPanel chứa các nút và bảng thời gian

    private JButton btnNew, btnCheck, btnSolve,
            btnReset, btnExit, btnSave;         //Các button lệnh
    private JRadioButton btnEasy, btnMid, btnHard;      //Các button level

    private String level = "Easy";

    private int[][] temp = new int[9][9];   //Mảng lưu ma trận đề
    private int[][] grid = new int[9][9];   //Mảng lưu ma trận đã giải
    private JButton btnAboutGame;

    public JTextField newtextfield() {
        // Cài đặt giao diện
        JTextField j = new JTextField("");
        j.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        j.setFont(new Font(Font.DIALOG, Font.BOLD, 28));
        j.setHorizontalAlignment(JTextField.CENTER);
        // Đổi màu khi di chuyển chuột
        j.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (j.isEditable()) {
                    ((JTextField) e.getSource()).setBorder(BorderFactory.createLineBorder(Color.decode("#00aeef")));
                    ((JTextField) e.getSource()).setBackground(Color.cyan);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (j.isEditable()) {
                    ((JTextField) e.getSource()).setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
                    ((JTextField) e.getSource()).setBackground(Color.white);
                }
            }
        });
        /*------------------------------------------------*/
        j.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (j.isEditable()) {
                    ((JTextField) e.getSource()).setForeground(Color.decode("#3366FF"));
                } else {
                    ((JTextField) e.getSource()).setForeground(Color.black);
                }
            }
        });
        return j;
    }

    public SudokuView() {
        initComponents();
        //Cài đặt giao diện của panel chính
        jPanel_Main = new JPanel();
        jPanel_Main.setLayout(new GridLayout(3, 3));
        jPanel_Main.setBackground(Color.black);
        setLayout(new BorderLayout());
        add(jPanel_Main);
        stopWatch = new StopWatch();
        jTF = new JTextField[9][9];
        paneles = new JPanel[3][3];
        // khởi tạo khối 3x3
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                paneles[i][j] = new JPanel();
                paneles[i][j].setBorder(BorderFactory.createLineBorder(Color.black));
                paneles[i][j].setLayout(new GridLayout(3, 3));
                jPanel_Main.add(paneles[i][j]);
            }
        }
        //Thêm ô textfield vào các khối 3x3
        for (int n = 0; n < 9; n++) {
            for (int i = 0; i < 9; i++) {
                jTF[n][i] = newtextfield();
                int fm = (n + 1) / 3;
                if ((n + 1) % 3 > 0) {
                    fm++;
                }
                int cm = (i + 1) / 3;
                if ((i + 1) % 3 > 0) {
                    cm++;
                }
                paneles[fm - 1][cm - 1].add(jTF[n][i]);
            }
        }

        /*---------------------------------------------------------------------------*/
        //Cài đặt Panel chứa các button điều khiển
        jPanel_Control = new JPanel();
        jPanel_Control.setBorder(BorderFactory.createEmptyBorder(5, 10, 20, 10));
        jPanel_Control.setLayout(new GridLayout(6, 3, 5, 5));

        // Cài đặt Panel Time
        jPanel_Time = new JPanel();
        jPanel_Time.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jPanel_Time.setLayout(new GridLayout(1, 3, 5, 5));
        /*---------------------------------------------------------------------------*/
 /*
            - Cài đặt button phía tay phải 
        
        /--------------------------------------------------------------------------- */
        //Khi click vào button này, ma trận mới cho level đang chọn sẽ hiện ra thay thế ma trận cũ 
        btnNew = new JButton("Mới");
        btnNew.setBackground(Color.decode("#C0C0C0"));
        btnNew.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        btnNew.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refresh();
                Sudoku.setNum();
                enabled_btton();
                startTimer();
                btnSave.setEnabled(false);
            }
        });
        // khi click vào thì ô đã nhập sẽ được xóa đi 
        btnReset = new JButton("Đặt Lại");
        btnReset.setBackground(Color.decode("#C0C0C0"));
        btnReset.setBorder(null);
        btnReset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reset();
                startTimer();
            }
        });
        // khi click vào, chương trình sẽ kiểm tra các ô số
        btnCheck = new JButton("Kiểm Tra");
        btnCheck.setBackground(Color.decode("#C0C0C0"));
        btnCheck.setBorder(null);
        btnCheck.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int count = 0;
                for (int i = 0; i < 9; i++) {
                    for (int j = 0; j < 9; j++) {
                        if (!jTF[i][j].isEditable()) {
                            continue;
                        } else if (jTF[i][j].getText().equals(String.valueOf(grid[i][j]))) {
                            jTF[i][j].setBackground(Color.decode("#caf0f8"));
                        } else if (jTF[i][j].getText().isEmpty()) {
                            jTF[i][j].setBackground(Color.WHITE);
                            continue;
                        } else {
                            jTF[i][j].setBackground(Color.RED);
                            count++;
                            if (count >= 3) {
                                stopWatch.pause();
                                JOptionPane.showMessageDialog(null, "Bạn đã thua vì sai quá 3 lần!!!", "Notification", JOptionPane.INFORMATION_MESSAGE);
                                System.exit(0);
                            }
                        }
                    }
                }
                if (win()) {
                    stopWatch.pause();
                    JOptionPane.showMessageDialog(null, "Chiến thắng!!!. Bạn thắng ở mức: " + level
                            + "\n Bạn đã hoàn thành lúc: " + jLabel_time.getText() + "");
                    btnSave.setEnabled(true);
                    

                }
            }
        });
        // khi click vào các ô trống của ma trận đề sẽ được giải
        btnSolve = new JButton("Giải");
        btnSolve.setBackground(Color.decode("#C0C0C0"));
        btnSolve.setBorder(null);
        btnSolve.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (int i = 0; i < 9; i++) {
                    for (int j = 0; j < 9; j++) {
                        jTF[i][j].setText(String.valueOf(grid[i][j]));
                    }
                }
                stopWatch.pause();
                btnSave.setEnabled(true);
                btnSolve.setEnabled(false);
                btnCheck.setEnabled(false);
            }
        });
        // Khi click vào thì chi tiết ma trận sẽ được lưu vào máy
        btnSave = new JButton("Lưu");
        btnSave.setBackground(Color.decode("#C0C0C0"));
        btnSave.setBorder(null);
        btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                in(dinhdang());
                btnSave.setEnabled(false);
                stopWatch.pause();
                paused = true;
            }
        });

        // Khi click vào trò chơi sẽ đóng cửa sổ chương trình
        btnExit = new JButton("Thoát");
        btnExit.setBackground(Color.decode("#C0C0C0"));
        btnExit.setBorder(null);
        btnExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int click = JOptionPane.showConfirmDialog(null, "Bạn có chắc muốn thoát?", "Thông báo!", 2, JOptionPane.OK_CANCEL_OPTION);
                if (click == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });
        //**************************************************************//
        // Cài đặt các buttonradio level 
        //**************************************************************//
        btnEasy = new JRadioButton("Dễ");
        btnEasy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refresh();
                Sudoku.setlevel(2);
                Sudoku.setNum();
                startTimer();
                enabled_btton();
                btnSave.setEnabled(false);
                level = "Easy";
            }
        });

        btnMid = new JRadioButton("Trung bình");
        btnMid.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refresh();
                Sudoku.setlevel(4);
                Sudoku.setNum();
                startTimer();
                enabled_btton();
                btnSave.setEnabled(false);
                level = "Medium";
            }
        });
        btnHard = new JRadioButton("Khó");
        btnHard.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refresh();
                Sudoku.setlevel(7);
                Sudoku.setNum();
                startTimer();
                enabled_btton();
                btnSave.setEnabled(false);
                level = "Hard";
            }
        });
        //**************************************************************//
        // Cài đặt bảng thời gian và các button phía trên ma trận
        //**************************************************************//
        Font font_time = new Font("Times New Roman", Font.BOLD, 20);
        JPanel jPanel_South = new JPanel(new GridLayout(1, 4));
        jLabel_time = new JLabel("00:00:000", JLabel.CENTER);
        jLabel_time.setFont(font_time);
        // Click vào thì bảng thời gian sẽ ngừng và hiển thị thông báo cho người chơi
        jButton_pause = new JButton("Dừng");
        jButton_pause.setBackground(Color.decode("#C0C0C0"));
        jButton_pause.setBorder(null);
        jButton_pause.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopWatch.pause();
                paused = true;
                int click = JOptionPane.showConfirmDialog(null, "Trò chơi đã được dừng. Bạn muốn tiếp tục?", "Thông báo!", 2, JOptionPane.OK_CANCEL_OPTION);
                if (click == JOptionPane.OK_OPTION) {
                    stopWatch.pause();
                    startTimer1();
                    paused = false;
                    jButton_resume.setEnabled(false);
                } else {
                    jButton_resume.setEnabled(false);
                }
                jButton_resume.setEnabled(true);
            }
        });
        // khi click vào bảng thời gian lúc dừng trước đó sẽ được chạy tiếp 
        jButton_resume = new JButton("Chơi tiếp");
        jButton_resume.setBackground(Color.decode("#C0C0C0"));
        jButton_resume.setBorder(null);
        jButton_resume.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopWatch.resume();
                startTimer1();
                paused = false;
                jButton_resume.setEnabled(false);
            }
        });

        btnAboutGame = new JButton("Giới thiệu");
        btnAboutGame.setBackground(Color.decode("#C0C0C0"));
        btnAboutGame.setBorder(null);
        btnAboutGame.setVisible(true);
        btnAboutGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "Đây là cách chơi game Sudoku:\n + Game có 9 hàng và 9 cột và có 9 ô 3x3\n + Người dùng phải nhập giá trị vào các ô số trống sao cho hợp lệ\n"
                        + " + Quy tắc trò chơi: giá trị nhập không được trùng trên mỗi hàng, cột và khối 3x3, các ô số nhập vào phải là 1 đến 9");
            }
        });

        /*---------------------------------------------------------------------------*/
        //Tạo nhóm cho các RadioButton để mỗi thời điểm chỉ chọn 1 level
        ButtonGroup btnLevel = new ButtonGroup();
        btnLevel.add(btnEasy);
        btnLevel.add(btnMid);
        btnLevel.add(btnHard);

        //Cài đặt mặc định khi khởi chạy chương trình là chọn Radiobutton level dễ
        btnEasy.setSelected(true);

        //Thêm các button điều khiển vào panel điều khiển
        JPanel jPanel_North = new JPanel(new GridLayout(1, 2, 10, 10));
        JPanel jPanel_EAST = new JPanel(new GridLayout(1, 2, 10, 10));

        jPanel_Control.add(btnNew);
        jPanel_Control.add(btnReset);
        jPanel_Control.add(btnCheck);
        jPanel_Control.add(btnSolve);
        jPanel_Control.add(btnSave);
        jPanel_Control.add(btnExit);

        jPanel_Time.add(jButton_pause);
        jPanel_Time.add(jButton_resume);
        jPanel_Time.add(jLabel_time);

        jPanel_South.add(btnEasy);
        jPanel_South.add(btnMid);
        jPanel_South.add(btnHard);
        jPanel_South.add(btnAboutGame);

        jPanel_EAST.add(jPanel_Control);
        jPanel_North.add(jPanel_Time);

        //Cài đặt khóa các button điều khiển Giải, KIỂM TRA, LƯU khi khởi chạy chương trình
        disenabled_btton();

        //Thêm panel điều khiển lên trên panel chính
        add(jPanel_North, "North");
        add(jPanel_South, "South");
        add(jPanel_EAST, "East");
    }

    /*---------------------------------------------------------------------------*/
    //Cài đặt 2 mảng ban đầu chứa ma trận
    public void setarray(int[][] grid, int[][] temp) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                this.temp[i][j] = temp[i][j];
                this.grid[i][j] = grid[i][j];
            }
        }
    }

    //Gán giá trị vào các ô đề của ma trận Sudoku
    public void setTextLabel() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (this.temp[i][j] != 0) {
                    jTF[i][j].setText(String.valueOf(this.temp[i][j]));
                    jTF[i][j].setEditable(false);
                    jTF[i][j].setBackground(Color.decode("#caf0f8"));
                } else {
                    jTF[i][j].setText("");
                }
            }
        }
    }

    //Hàm đặt lại các ô của ma trận
    public static void refresh() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                jTF[i][j].setForeground(Color.black);
                jTF[i][j].setEditable(true);
                jTF[i][j].setBackground(Color.WHITE);
            }
        }
    }

    //Hàm xóa hết các giá trị, người dùng nhập vào để chơi lại với ma trận hiện tại
    public void reset() {
        refresh();
        setTextLabel();
        btnSolve.setEnabled(true);
        btnCheck.setEnabled(true);
        btnSave.setEnabled(false);
    }

    // hàm bảng thời gian bắt đầu từ 00:00:000
    private void startTimer() {
        Thread thread = new Thread(new Runnable() {
            public void run() {
                stopWatch.start();
                while (true) {
                    if (!paused) {
                        final String timeString = new SimpleDateFormat("mm:ss:SSS").format(stopWatch.getElapsedTime());
                        jLabel_time.setText("" + timeString);
                    }
                }
            }
        });
        jButton_resume.setEnabled(false);
        thread.start();
    }

    // hàm bảng thời gian chạy tiếp tục với thời gian dừng trước đó.
    private void startTimer1() {
        Thread thread = new Thread(new Runnable() {
            public void run() {
                stopWatch.resume();
                while (true) {
                    if (!paused) {
                        final String timeString = new SimpleDateFormat("mm:ss:SSS").format(stopWatch.getElapsedTime());
                        jLabel_time.setText("" + timeString);
                    }
                }
            }
        });
        jButton_resume.setEnabled(false);
        thread.start();
    }

    //Hàm lưu thông tin ma trận ra file txt sau khi hoàn thành trò chơi 
    private void in(String dinhdang) {
        //Tạo thư mục để lưu file.
        String link = "E:\\SudokuSave\\";
        File location = new File(link);

        if (!location.exists()) {
            location.mkdir();
        }
        try {
            Date dt = new Date();
            SimpleDateFormat d = new SimpleDateFormat("dd-MM-yyyy hh-mm-ss");
            String time = d.format(dt);
            String tenfile = "Matrix_9x9(" + time.trim() + ")";
            System.out.println(tenfile);
            File fileDir = new File(link + tenfile + ".txt");
            try (Writer out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(fileDir), "UTF8"))) {
                out.append(dinhdang);
                out.flush();
                out.close();
            }
            JOptionPane.showMessageDialog(null, "Ma trận số ván game đã lưu.\n Bạn có thể tìm tại: " + link, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Lỗi liên kết. Kiểm tra lại!");
            System.out.println(e.getMessage());
        }
    }

    //Định dạng cho nội dung file txt lưu ma trận được in ra
    private String dinhdang() {
        Date dt = new Date();
        SimpleDateFormat d = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        String time = d.format(dt);

        String matran = "Time: ";  // d/m/y thời gian
        matran += time += "\n";
        matran += "Level: " + level + "\n\n"; // cấp độ chơi
        matran += "Finish at: " + jLabel_time.getText() + "\n\n"; //thời gian kết thúc 
        matran += "Puzzle\n\n"; 
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                matran += temp[i][j] + "  ";
            }
            matran += "\n";
        }

        matran += "\n   --------------------   \n";
        matran += "Solved\n";
        for (int i = 0; i < 9; i++) {
            matran += "\n";
            for (int j = 0; j < 9; j++) {
                matran += grid[i][j];
                matran += "  ";
            }
        }
        return matran;
    }

    private boolean win() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (!jTF[i][j].isEditable()) {
                    continue;
                } else if (jTF[i][j].getText().equals(String.valueOf(grid[i][j]))) {
                    continue;
                } else {
                    return false;
                }
            }
        }
        return true;
    }

    //Hàm khóa chức năng các button
    private void disenabled_btton() {
        btnSave.setEnabled(false);
        btnSolve.setEnabled(false);
        btnCheck.setEnabled(false);
        btnReset.setEnabled(false);
    }

    private void enabled_btton() {
        btnSolve.setEnabled(true);
        btnCheck.setEnabled(true);
        btnReset.setEnabled(true);
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 300, Short.MAX_VALUE)
        );
    }
}
