//package pacman;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import jess.JessException;

public class PacMan extends JFrame
{
  public PacMan()
  {
    try
    {
      add(new Tabuleiro());
    } catch (JessException ex) {
      Logger.getLogger(PacMan.class.getName()).log(Level.SEVERE, null, ex);
    }
    setTitle("Pacman");
    setDefaultCloseOperation(3);
    setSize(380, 420);
    setLocationRelativeTo(null);
    setVisible(true);
  }

  public static void main(String[] args)
  {
    new PacMan();
  }
}
