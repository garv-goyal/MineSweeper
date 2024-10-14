import java.util.Random;

public class Minefield {
    private final int rows;
    private final int columns;
    private final int totalMines;
    private final Cell[][] cells;

    public Minefield(int rows, int columns, int totalMines) {
        this.rows = rows;
        this.columns = columns;
        this.totalMines = totalMines;
        cells = new Cell[rows][columns];
        initializeCells();
        placeMines();
        calculateAdjacentMines();
    }

    private void initializeCells() {
        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < columns; j++) {
                cells[i][j] = new Cell();
            }
        }
    }

    private void placeMines() {
        Random rand = new Random();
        int minesPlaced = 0;
        while(minesPlaced < totalMines) {
            int x = rand.nextInt(rows);
            int y = rand.nextInt(columns);
            if(!cells[x][y].isMine()) {
                cells[x][y].setMine(true);
                minesPlaced++;
            }
        }
    }

    private void calculateAdjacentMines() {
        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < columns; j++) {
                if(!cells[i][j].isMine()) {
                    int count = 0;
                    for(int x = -1; x <=1; x++) {
                        for(int y = -1; y <=1; y++) {
                            int newX = i + x;
                            int newY = j + y;
                            if(newX >=0 && newX < rows && newY >=0 && newY < columns && cells[newX][newY].isMine()) {
                                count++;
                            }
                        }
                    }
                    cells[i][j].setAdjacentMines(count);
                }
            }
        }
    }

    public Cell getCell(int x, int y) {
        return cells[x][y];
    }

    public int getRows() { return rows; }
    public int getColumns() { return columns; }
    public int getTotalMines() { return totalMines; }
}

