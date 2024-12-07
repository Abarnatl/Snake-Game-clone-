
package com.mycompany.gameproj;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import javax.swing.*;
 import java.util.ArrayList;
 import java.awt.*;
 import java.awt.event.*;
 import java.util.Random;
import java.io.*; 
import java.sql.ResultSet;
import java.sql.SQLException;

public class Game extends JPanel implements ActionListener , KeyListener,Serializable{
    

    JButton reset=new JButton("Retry");
    int bh;
    int bw;
    int tileSize=25;
    Tile food;
    Tile snake;
    double velocityX;
    double velocityY;
    double x;
    double y;
    int delay;
    Random random;
//    Loop gameLoop; 
    Timer gameLoop;
    boolean collision;
    boolean gameOver;
    String playername;
    ArrayList<Tile > body;
    String db;
    Connection con;
    String plyr;
    int score;
   public void connectDatabase(){
       
   
    try{
    Class.forName("com.mysql.cj.jdbc.Driver");
    con=DriverManager.getConnection("jdbc:mysql://localhost:3306/abarna","root","");
  System.out.println("Database connection established successfully.");
    } catch (ClassNotFoundException e) {
        System.out.println("JDBC Driver not found. Make sure the MySQL JDBC driver is in the classpath.");
    } catch (SQLException e) {
        System.out.println("Failed to connect to the database. Error: " + e.getMessage());
    }
   }
    Game(int bh, int bw,String playername){
        this.bh=bh;
        this.bw=bw;
        this.playername=playername;
        setPreferredSize(new Dimension(this.bh,this.bw));
        setBackground(Color.black);
        food=new Tile(3,3);
        snake=new Tile(10,10);
        random=new Random();
        placeFood();
        velocityX=0;
        velocityY=0;
        delay=200;
        
        
        collision=false;
        
        gameOver=false;
        
        body=new ArrayList<Tile>(  );
          
        addKeyListener(this);
        setFocusable(true);
        gameLoop = new Timer(delay,this);
        gameLoop.start();
          // Set the layout manager to null or GridBagLayout for better control
        this.setLayout(null);
        
        // Reset button setup
        reset.setBounds(5 * tileSize, 10 * tileSize, 100, 30);
        reset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetGame();
            }
        }); this.add(reset);
        reset.setVisible(false); // Hide it initially
        connectDatabase();
    }
    private void resetGame() {
        snake = new Tile(10, 10);
        body.clear();
        velocityX = 0;
        velocityY = 0;
        delay = 200;
        gameOver = false;
        placeFood();
        gameLoop.start();
        reset.setVisible(false); // Hide the button after reset
        repaint();
    }
//        ActionListener listener=new Gameproj();
//        reset.addActionListener(listener);
     public void savescore(){
         if(con!=null){
           try{ db="Insert into highScore(name,score)values('"+playername+"','"+body.size()+"')";
            Statement st=con.createStatement();
            int i=st.executeUpdate(db);
     }   
           catch(SQLException ex){
               System.out.println("Error:"+ex.getMessage());
           }
           }else{
             System.err.println("Connection not established");
         }
     }
     public void highScore(){
         if(con!=null){
             try{
                 String query="Select name,score from highscore order by score desc limit 1;";
                 
                 Statement st1=con.createStatement();
                 ResultSet rs=st1.executeQuery(query);
                 if(rs.next()){
                     plyr=rs.getString("name");
                      score=rs.getInt("score");
                 }
             }catch(SQLException se){
                 se.printStackTrace();
             }
         }
     }
      
        
    
//    boolean gameOver(Tile tile1)throws 
    
    boolean collision(Tile tile1,Tile tile2){
        return(tile1.x==tile2.x&&tile1.y==tile2.y);
        
  }
    
    public void move(){
        
         if(collision(snake,food)){
             if(delay>50){
                 delay-=5;
                 gameLoop.setDelay(delay);
             }
            body.add(new Tile(food.x,food.y));
            placeFood();
            
        }
         for(int i=body.size()-1;i>=0;i--){
        Tile   snakePart=body.get(i);
        if(i==0){
            snakePart.x=snake.x;
            snakePart.y=snake.y;
        }else{
            Tile prevPart=body.get(i-1);
            snakePart.x=prevPart.x;
            snakePart.y=prevPart.y;
        }
        }
          
        snake.x+=velocityX;
        snake.y+=velocityY;
        
        for(int i=0;i<body.size();i++){
            Tile snakePart=body.get(i);
           if(collision(snake,snakePart)){
               gameOver=true;
           }
        }
        if(snake.x*tileSize<0 || snake.y*tileSize<0 
                ||snake.x*tileSize>bh || snake.y*tileSize>bw){
               gameOver=true;
        }
       
       
    }

    @Override
    public void actionPerformed(ActionEvent e) {
              x+=velocityX;
              y+=velocityY;
               move();
               repaint() ;  
                 
               if(gameOver){
                   gameLoop.stop();  
                   savescore();
                   highScore();
                    reset.setVisible(true);
               }
             
    }
    

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode()==KeyEvent.VK_UP&&velocityY!=1){
            velocityX=0;
            velocityY=-1;
        }
        else  if(e.getKeyCode()==KeyEvent.VK_DOWN&&velocityY!=-1){
            velocityX=0;
            velocityY=1;
        }
        else  if(e.getKeyCode()==KeyEvent.VK_RIGHT&&velocityX!=-1){
            velocityX=1;
            velocityY=0;
        }
        else  if(e.getKeyCode()==KeyEvent.VK_LEFT&&velocityX!=1){
            velocityX=-1;
            velocityY=0;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}
    private class Tile{
        int x; int y;
        Tile (int x,int y){
            this.x=x;
            this.y=y;
            
        }
        
        
    }
    
    
    public void placeFood(){
        food.x=random.nextInt(bw/tileSize);
        food.y=random.nextInt(bh/tileSize);
    }
 
    
    public void paint(Graphics g){
        super.paint(g);
        draw(g);
    }
    public void draw(Graphics g){
        //Food
        g.setColor(Color.red);
        g.fill3DRect( food.x*tileSize,food.y*tileSize, tileSize, tileSize,true);
        
        //Grid
//        g.setColor(Color.WHITE);
//        for(int i=0;i<bh/tileSize;i++){
//        g.drawLine(i*tileSize, 0, i*tileSize, bh);
//        g.drawLine(0, i*tileSize, bw, i*tileSize);
        
        //Snake
//        }
         g.setColor(Color.GREEN);
        g.fill3DRect( snake.x*tileSize,snake.y*tileSize, tileSize, tileSize,true);
        for(int i=0;i<body.size();i++){
              Tile snakePart=body.get(i);
              g.fill3DRect(snakePart.x*tileSize,snakePart.y*tileSize,tileSize,tileSize,true);
                }
        g.setFont(new Font("Times New Roman",Font.BOLD,24));
        g.setColor(Color.red);
        if(gameOver){
        g.drawString("Game Over ",11*tileSize,9*tileSize);
        g.setFont(new Font("Times New Roman",Font.BOLD,20));
        g.setColor(Color.white);
        g.drawString(playername+" Your Score is  "+String.valueOf(body.size()) ,10*tileSize,11*tileSize );
       g.setFont(new Font("Times New Roman",Font.BOLD,20));
        g.setColor(Color.white);
        g.drawString("HighScore : "+score,11*tileSize, 12*tileSize);
        //Reset button
         g.setColor(Color.WHITE);
        reset.setFont(new Font("Arial",Font.BOLD,16));
         reset.setBackground(Color.blue);
        reset.setBounds(13*tileSize, 13*tileSize, 100, 30);
        add(reset);
       
        }
       
        else{
            g.setFont(new Font("Arial",Font.BOLD,16));

             g.setColor(Color.WHITE);
            g.drawString("Score  :"+String.valueOf(body.size()), 2*tileSize, 2*tileSize);
        }
       
        }  
       
}
   
