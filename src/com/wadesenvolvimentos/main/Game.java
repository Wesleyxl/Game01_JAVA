package com.wadesenvolvimentos.main;

import com.wadesenvolvimentos.entites.Entity;
import com.wadesenvolvimentos.entites.Player;
import com.wadesenvolvimentos.graphics.SpriteSheet;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Game extends Canvas implements Runnable {

    // variables
    public static JFrame frame;

    private boolean isRunning = true;
    private final int SCALE = 4;
    private final int WIDTH = 160;
    private final int HEIGHT = 120;

    private Thread thread;
    private final BufferedImage image;
    public List<Entity> entities;
    public SpriteSheet spriteSheet;

    // Method constructor
    public Game()
    {
        this.setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
        initFrame();

        // initializing objects
        image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        entities = new ArrayList<Entity>();
        spriteSheet = new SpriteSheet("/res/spriteSheet.png");

        // add player
        Player player = new Player(0, 0, 16, 16, spriteSheet.getSprite(32, 0, 16, 16));
        entities.add(player);
    }

    // frame
    public void initFrame()
    {
        frame = new JFrame("Game #1");
        frame.add(this);
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    // Game Start
    public synchronized void start()
    {
        thread = new Thread(this);
        isRunning = true;
        thread.start();
    }

    // Game Stop
    public synchronized void stop()
    {
        isRunning = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public static void main (String[] args)
    {
        Game game = new Game();
        game.start();
    }

    // Game Logic
    public void tick()
    {
        for (int i = 0; i < entities.size(); i++)
        {
            Entity e = entities.get(i);
            e.tick();
        }
    }

    // Game render
    public void render()
    {
        BufferStrategy bs = this.getBufferStrategy();
        if (bs == null)
        {
            this.createBufferStrategy(3);
            return;
        }

        Graphics g = image.getGraphics();
        g.setColor(new Color(19,19,19));
        g.fillRect(0, 0, WIDTH, HEIGHT);

        /* Game Render */

        for (int i = 0; i < entities.size(); i++)
        {
            Entity e = entities.get(i);
            e.render(g);
        }

        g.dispose();
        g = bs.getDrawGraphics();
        g.drawImage(image, 0, 0, WIDTH * SCALE, HEIGHT * SCALE, null);
        bs.show();
    }

    // Game looping
    @Override
    public void run()
    {
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;

        int frames = 0;
        double timer = System.currentTimeMillis();

        while (isRunning)
        {
            long now  = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;

            if (delta >= 1)
            {
                tick();
                render();
                frames++;
                delta--;
            }

            if (System.currentTimeMillis() - timer >= 1000)
            {
                System.out.println("FPS " + frames);
                frames = 0;
                timer += 1000;
            }
        }
        stop();
    }
}
