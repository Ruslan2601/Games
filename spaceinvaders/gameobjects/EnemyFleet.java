package com.javarush.games.spaceinvaders.gameobjects;

import com.javarush.engine.cell.*;
import com.javarush.games.spaceinvaders.Direction;
import com.javarush.games.spaceinvaders.ShapeMatrix;
import com.javarush.games.spaceinvaders.SpaceInvadersGame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EnemyFleet {
    private static final int ROWS_COUNT = 3;
    private static final int COLUMNS_COUNT = 10;
    private static final int STEP = ShapeMatrix.ENEMY.length + 1;
    private List<EnemyShip> ships;
    private Direction direction = Direction.RIGHT;

    public EnemyFleet() {
        createShips();
    }

    public void draw(Game game) {
        for (EnemyShip ship : ships) {
            ship.draw(game);
        }
    }

    private void createShips() {
        ships = new ArrayList<>();
        for (int x = 0; x < COLUMNS_COUNT; x++) {
            for (int y = 0; y < ROWS_COUNT; y++) {
                ships.add(new EnemyShip(x * STEP, y * STEP + 12));
            }
        }
        ships.add(new Boss( STEP * COLUMNS_COUNT / 2 - ShapeMatrix.BOSS_ANIMATION_FIRST.length / 2 - 1, 5));
    }

    private double getLeftBorder() {
        double min = ships.get(0).x;
        for (EnemyShip i : ships) {
            if (i.x < min) {
                min = i.x;
            }
        }
        return min;
    }

    private double getRightBorder() {
        double max = ships.get(0).x + ships.get(0).width;
        for (EnemyShip i : ships) {
            if (i.x + i.width > max) {
                max = i.x + i.width;
            }
        }
        return max;
    }

    private double getSpeed() {
        return Math.min(2.0, 3.0 / ships.size());
    }

    public void move() {
        if (ships.size() != 0) {
            if (Direction.LEFT == direction && getLeftBorder() < 0) {
                direction = Direction.RIGHT;
                ships.forEach(x -> x.move(Direction.DOWN, getSpeed()));
            } else if (Direction.RIGHT == direction && getRightBorder() > SpaceInvadersGame.WIDTH) {
                direction = Direction.LEFT;
                ships.forEach(x -> x.move(Direction.DOWN, getSpeed()));
            } else {
                getSpeed();
                ships.forEach(x -> x.move(direction, getSpeed()));
            }
        }
    }

    public Bullet fire(Game game) {
        if (ships.size() == 0) {
            return null;
        }
        if (game.getRandomNumber(100 / SpaceInvadersGame.COMPLEXITY) > 0) {
            return null;
        }
        return ships.get(game.getRandomNumber(ships.size())).fire();
    }

    public int verifyHit(List<Bullet> bullets) {
       
        if (bullets.isEmpty()) {
            return 0;
        }

        int score = 0;
        for (Bullet bullet : bullets) {
            for (EnemyShip ship : ships) {
                if (ship.isAlive && bullet.isAlive && ship.isCollision(bullet)) {
                    ship.kill();
                    bullet.kill();
                    score += ship.score;
                }
            }
        }
        return score;
    }

    public void deleteHiddenShips() {
        for (EnemyShip ship : new ArrayList<>(ships)) {
            if (!ship.isVisible()) {
                ships.remove(ship);
            }
        }
    }

    public double getBottomBorder() {
        double max = 0;
        for(EnemyShip ship : ships) {
            if (ship.y + ship.height > max) {
                max = ship.y + ship.height;
            }
        }
        return max;
    }

    public int getShipsCount() {
        return ships.size();
    }
}
