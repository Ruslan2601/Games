package com.javarush.games.minesweeper;

import com.javarush.engine.cell.Color;
import com.javarush.engine.cell.Game;

import java.util.ArrayList;
import java.util.List;

public class MinesweeperGame extends Game {
    private static final int SIDE = 9;
    private GameObject[][] gameField = new GameObject[SIDE][SIDE];
    private static final String MINE = "\uD83D\uDCA3";
    private static final String FLAG = "\uD83D\uDEA9";
    private int countFlags;
    private boolean isGameStopped;
    private int countMinesOnField;
    private int countClosedTiles = SIDE * SIDE;
    private int score;


    @Override
    public void initialize() {
        setScreenSize(SIDE, SIDE);
        createGame();
    }

    private void createGame() {
        for (int y = 0; y < SIDE; y++) {
            for (int x = 0; x < SIDE; x++) {
                setCellValue(x, y, "");
                boolean isMine = getRandomNumber(10) < 1;
                if (isMine) {
                    countMinesOnField++;
                }
                gameField[y][x] = new GameObject(x, y, isMine);
                setCellColor(x, y, Color.ORANGE);
            }
        }
        countMineNeighbors();
        countFlags = countMinesOnField;
    }

    private List<GameObject> getNeighbors(GameObject gameObject) {
        List<GameObject> result = new ArrayList<>();
        for (int y = gameObject.y - 1; y <= gameObject.y + 1; y++) {
            for (int x = gameObject.x - 1; x <= gameObject.x + 1; x++) {
                if (y < 0 || y >= SIDE) {
                    continue;
                }
                if (x < 0 || x >= SIDE) {
                    continue;
                }
                if (gameField[y][x] == gameObject) {
                    continue;
                }
                result.add(gameField[y][x]);
            }
        }
        return result;
    }

    private void countMineNeighbors() {
        for (int i = 0; i < SIDE; i++) {
            for (int j = 0; j < SIDE; j++) {
                if (gameField[i][j].isMine) {
                    continue;
                } else {
                    int x = getNeighbors(gameField[i][j]).size();
                    int f = 0;
                    for (int k = 0; k < x; k++) {
                        if (getNeighbors(gameField[i][j]).get(k).isMine) {
                            f++;
                        }
                    }
                    gameField[i][j].countMineNeighbors = f;
                }
            }

        }
    }

    private void openTile(int x, int y) {
        GameObject gameObject = gameField[y][x];
        if (!gameObject.isOpen && !gameObject.isFlag && !isGameStopped) {
            gameObject.isOpen = true;
            countClosedTiles--;
            setCellColor(x, y, Color.GREEN);
            if (gameObject.isMine) {
                setCellValueEx(gameObject.x, gameObject.y, Color.RED, MINE);
                gameOver();
                return;
            } else {
                score += 5;
                if (gameObject.countMineNeighbors == 0) {
                    List<GameObject> noMine = getNeighbors(gameObject);
                    setCellValue(x, y, "");
                    for (GameObject cell : noMine) {
                        openTile(cell.x, cell.y);
                    }
                } else {
                    setCellNumber(x, y, gameObject.countMineNeighbors);
                }
                if (countClosedTiles == countMinesOnField) {
                    win();
                }
            }
        }
        setScore(score);
    }

    private void markTile(int x, int y) {
        GameObject gameObject = gameField[y][x];
        if (!gameObject.isOpen && !isGameStopped) {
            if (!gameObject.isFlag && countFlags != 0) {
                gameObject.isFlag = true;
                countFlags--;
                setCellValue(x, y, FLAG);
                setCellColor(x, y, Color.YELLOW);
            } else if (gameObject.isFlag) {
                gameObject.isFlag = false;
                countFlags++;
                setCellValue(x, y, "");
                setCellColor(x, y, Color.ORANGE);
            }
        }
    }

    private void restart() {
        isGameStopped = false;
        countMinesOnField = 0;
        countClosedTiles = SIDE * SIDE;
        score = 0;
        setScore(score);
        createGame();
    }

    private void gameOver() {
        isGameStopped = true;
        showMessageDialog(Color.RED, "Game over", Color.BLACK, 40);
    }

    private void win() {
        isGameStopped = true;
        showMessageDialog(Color.RED, "You WIN", Color.BLACK, 40);
    }

    @Override
    public void onMouseRightClick(int x, int y) {
        markTile(x, y);
    }

    @Override
    public void onMouseLeftClick(int x, int y) {
        if (isGameStopped) {
            restart();
            return;
        }
        openTile(x, y);
    }
}