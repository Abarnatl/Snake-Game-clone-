
package com.mycompany.gameproj;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class Gameproj {

    public static void main(String[] args) {
        String playername=JOptionPane.showInputDialog("Enter Your Name");
        if(playername==null || playername.trim().isEmpty()){
            playername="Anonyms";
    }
         int bw=600;
        int bh=bw;
        JFrame scrn=new JFrame("Snake ");
        scrn.setSize(bh,bw);
        scrn.setVisible(true);
        scrn.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        scrn.setLocationRelativeTo(null);
        scrn.setResizable(false);
        Game game=new Game(bh,bw,playername);
        scrn.add(game);
        scrn.pack();
        scrn.setVisible(true);

        game.requestFocus();
//         public void actionPerformed(ActionEvent e){
//             System.exit(0);
//             scrn.setVisible(true);
//         }
    }
}
