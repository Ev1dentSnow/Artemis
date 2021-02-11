/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package artemis.Views;

import artemis.Views.LoginView;

import java.awt.Graphics;
import java.awt.Image;
import javax.swing.JPanel;

/**
 *
 * @author moagimoja
 */
public class BackgroundPanel extends JPanel {
    
    Image backimg = LoginView.getImg();
    
    public BackgroundPanel(){
        
    }
    
    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        g.drawImage(backimg, 0, 0, this.getWidth(), this.getHeight(), null);
    }
    
}
