import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class MinefieldGUI extends JFrame {
    private final int rows = 16;
    private final int columns = 16;
    private final int totalMines = 10;
    private final JButton[][] buttons = new JButton[rows][columns];
    private final Minefield minefield;
    private boolean gameOver;
    private int cellsRevealed;
    private int flagsPlaced;
    private JLabel mineCounterLabel;
    private JLabel timerLabel;
    private Timer timer;
    private int elapsedTime;

    public MinefieldGUI() {
        minefield = new Minefield(rows, columns, totalMines);
        gameOver = false;
        cellsRevealed = 0;
        flagsPlaced = 0;
        elapsedTime = 0;

        setTitle("Minesweeper");
        setSize(800, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        initializeTopPanel();
        initializeMinefieldPanel();
        initializeTimer();

        setVisible(true);
    }

    private void initializeTopPanel() {
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());

        mineCounterLabel = new JLabel("Mines: " + totalMines);
        mineCounterLabel.setFont(new Font("Arial", Font.BOLD, 24));
        mineCounterLabel.setHorizontalAlignment(SwingConstants.CENTER);

        timerLabel = new JLabel("Time: 0");
        timerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        timerLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JButton resetButton = new JButton("Restart");
        resetButton.setFont(new Font("Arial", Font.BOLD, 18));
        resetButton.addActionListener(e -> restartGame());

        topPanel.add(mineCounterLabel, BorderLayout.WEST);
        topPanel.add(resetButton, BorderLayout.CENTER);
        topPanel.add(timerLabel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);
    }

    private void initializeMinefieldPanel() {
        JPanel minefieldPanel = new JPanel();
        minefieldPanel.setLayout(new GridLayout(rows, columns));

        for(int i =0; i < rows; i++) {
            for(int j=0; j < columns; j++) {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(40,40));
                button.setFont(new Font("Arial", Font.BOLD, 16));
                button.setMargin(new Insets(0,0,0,0));
                button.setFocusPainted(false);
                button.addMouseListener(new CellMouseListener(i, j));
                buttons[i][j] = button;
                minefieldPanel.add(button);
            }
        }

        add(minefieldPanel, BorderLayout.CENTER);
    }

    private void initializeTimer() {
        timer = new Timer(1000, e -> {
            elapsedTime++;
            timerLabel.setText("Time: " + elapsedTime);
        });
        timer.start();
    }

    private void restartGame() {
        dispose();
        SwingUtilities.invokeLater(() -> new MinefieldGUI());
    }

    private void revealAllMines() {
        for(int i=0; i < rows; i++) {
            for(int j=0; j < columns; j++) {
                if(minefield.getCell(i,j).isMine()) {
                    buttons[i][j].setText("💣");
                    buttons[i][j].setForeground(Color.RED);
                }
                buttons[i][j].setEnabled(false);
            }
        }
    }

    private void updateMineCounter() {
        mineCounterLabel.setText("Mines: " + (totalMines - flagsPlaced));
    }

    private void checkWinCondition() {
        if(cellsRevealed == (rows * columns - totalMines)) {
            timer.stop();
            JOptionPane.showMessageDialog(this, "Congratulations! You won in " + elapsedTime + " seconds.");
            revealAllMines();
            gameOver = true;
        }
    }

    private void revealCell(int x, int y) {
        if(x <0 || x >= rows || y <0 || y >= columns) return;
        Cell cell = minefield.getCell(x, y);
        JButton button = buttons[x][y];

        if(cell.isRevealed() || cell.isFlagged()) return;

        cell.setRevealed(true);
        button.setEnabled(false);
        cellsRevealed++;

        if(cell.isMine()) {
            button.setText("💣");
            button.setForeground(Color.RED);
            revealAllMines();
            timer.stop();
            JOptionPane.showMessageDialog(this, "Game Over! You hit a mine.");
            gameOver = true;
            return;
        }

        if(cell.getAdjacentMines() > 0) {
            button.setText(String.valueOf(cell.getAdjacentMines()));
            button.setForeground(getColorForNumber(cell.getAdjacentMines()));
        } else {
            // Empty cell, recurse to reveal adjacent cells
            for(int i=-1; i<=1; i++) {
                for(int j=-1; j<=1; j++) {
                    if(i !=0 || j !=0) {
                        revealCell(x + i, y + j);
                    }
                }
            }
        }

        checkWinCondition();
    }

    private Color getColorForNumber(int number) {
        return switch (number) {
            case 1 -> Color.BLUE;
            case 2 -> new Color(0,128,0);
            case 3 -> Color.RED;
            case 4 -> new Color(128,0,128);
            case 5 -> new Color(128,0,0);
            case 6 -> Color.CYAN;
            case 7 -> Color.BLACK;
            case 8 -> Color.GRAY;
            default -> Color.BLACK;
        }; 
    }

    private class CellMouseListener extends MouseAdapter {
        private final int x;
        private final int y;

        public CellMouseListener(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if(gameOver) return;
            if(SwingUtilities.isLeftMouseButton(e)) {
                revealCell(x, y);
            } else if(SwingUtilities.isRightMouseButton(e)) {
                toggleFlag(x, y);
            }
        }
    }

    private void toggleFlag(int x, int y) {
        Cell cell = minefield.getCell(x, y);
        JButton button = buttons[x][y];

        if(cell.isRevealed()) return;

        if(!cell.isFlagged()) {
            if(flagsPlaced < totalMines) {
                cell.setFlagged(true);
                button.setText("🚩");
                button.setForeground(Color.BLUE);
                flagsPlaced++;
            }
        } else {
            cell.setFlagged(false);
            button.setText("");
            flagsPlaced--;
        }
        updateMineCounter();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MinefieldGUI());
    }
}
