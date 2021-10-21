package com.javarush.games.spaceinvaders;

import com.javarush.engine.cell.*;
import com.javarush.games.spaceinvaders.gameobjects.Bullet;
import com.javarush.games.spaceinvaders.gameobjects.EnemyFleet;
import com.javarush.games.spaceinvaders.gameobjects.PlayerShip;
import com.javarush.games.spaceinvaders.gameobjects.Star;

import java.util.ArrayList;
import java.util.List;

public class SpaceInvadersGame extends Game {
    public static final int WIDTH = 64;
    public static final int HEIGHT = 64;
    private List<Star> stars;
    private EnemyFleet enemyFleet;
    public static final int COMPLEXITY = 5;
    private List<Bullet> enemyBullets;
    private List<Bullet> playerBullets;
    private PlayerShip playerShip;
    private boolean isGameStopped;
    private int animationsCount;
    private static final int PLAYER_BULLETS_MAX = 3;
    private int score;

    @Override
    public void initialize() {
        setScreenSize(WIDTH, HEIGHT);
        createGame();
    }

    @Override
    public void onTurn(int step) {
        moveSpaceObjects();
        check();
        Bullet fire = enemyFleet.fire(this);
        if (fire != null) {
            enemyBullets.add(fire);
        }
        setScore(score);
        drawScene();
    }

    private void createGame() {
        score = 0;
        enemyFleet = new EnemyFleet();
        createStars();
        enemyBullets = new ArrayList<Bullet>();
        playerBullets = new ArrayList<Bullet>();
        playerShip = new PlayerShip();
        isGameStopped = false;
        animationsCount = 0;
        drawScene();
        setTurnTimer(40);
    }

    private void drawScene() {
        drawField();
        playerShip.draw(this);
        enemyBullets.forEach(x -> x.draw(this));
        playerBullets.forEach(x -> x.draw(this));
        enemyFleet.draw(this);
    }

    private void drawField() {
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                setCellValueEx(x, y, Color.BLACK, "");
            }
        }

        for (Star star : stars) {
            star.draw(this);
        }
    }

    private void createStars() {
        stars = new ArrayList<>();
        for (int i = 0; i < 18; i++) {
            int x = getRandomNumber(WIDTH);
            int y = getRandomNumber(HEIGHT);
            stars.add(new Star(x, y));
        }
    }

    private void moveSpaceObjects() {
        enemyFleet.move();
        enemyBullets.forEach(x -> x.move());
        playerBullets.forEach(x -> x.move());
        playerShip.move();
    }

    private void removeDeadBullets() {
        for (Bullet bullet : new ArrayList<>(enemyBullets)) {
            if (!bullet.isAlive || bullet.y >= HEIGHT - 1) {
                enemyBullets.remove(bullet);
            }
        }
        for (Bullet bullet : new ArrayList<>(playerBullets)) {
            if (!bullet.isAlive || bullet.y + bullet.height < 0) {
                playerBullets.remove(bullet);
            }
        }
    }

    private void check() {
        playerShip.verifyHit(enemyBullets);
        score += enemyFleet.verifyHit(playerBullets);
        enemyFleet.verifyHit(playerBullets);
        enemyFleet.deleteHiddenShips();
        removeDeadBullets();
        if (enemyFleet.getShipsCount() == 0) {
            playerShip.win();
            stopGameWithDelay();
        }
        if (enemyFleet.getBottomBorder() >= playerShip.y) {
            playerShip.kill();
        }
        if (!playerShip.isAlive) {
            stopGameWithDelay();
        }
        
    }

    private void stopGame(boolean isWin) {
        isGameStopped = true;
        stopTurnTimer();
        if (isWin) {
            showMessageDialog(Color.BLUE, "You WIN!", Color.GREEN, 40);
        } else {
            showMessageDialog(Color.BLUE, "You LOSE!", Color.RED, 40);
        }
    }

    private void stopGameWithDelay() {
        animationsCount++;
        if (animationsCount >= 10) {
            stopGame(playerShip.isAlive);
        }
    }

    @Override
    public void onKeyPress(Key key) {
        if (key == Key.SPACE && isGameStopped == true) {
            createGame();
        }
        if (key == Key.LEFT) {
            playerShip.setDirection(Direction.LEFT);
        }
        if (key == Key.RIGHT) {
            playerShip.setDirection(Direction.RIGHT);
        }
        if (key == Key.SPACE) {
            Bullet fire = playerShip.fire();
            if (fire != null && playerBullets.size() < PLAYER_BULLETS_MAX) {
                playerBullets.add(fire);
            }
        }
    }

    @Override
    public void onKeyReleased(Key key) {
        if (key == Key.LEFT && playerShip.getDirection() == Direction.LEFT ||
                key == Key.RIGHT && playerShip.getDirection() == Direction.RIGHT) {
            playerShip.setDirection(Direction.UP);
        }
    }

    @Override
    public void setCellValueEx(int x, int y, Color color, String string) {
        if (x < WIDTH-1 && x > 0 && y < HEIGHT-1 && y > 0) {
            super.setCellValueEx(x, y, color, string);
        }
    }
}
