//package pacman;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;
import jess.JessException;
import nrc.fuzzy.FuzzyException;
import nrc.fuzzy.FuzzyValue;
import nrc.fuzzy.FuzzyVariable;
import nrc.fuzzy.LeftLinearFunction;
import nrc.fuzzy.RightLinearFunction;
import nrc.fuzzy.TriangleFuzzySet;
import nrc.fuzzy.jess.FuzzyRete;
import nrc.fuzzy.RightLinearFuzzySet;

public class Tabuleiro extends JPanel
  implements ActionListener
{
  Dimension d;
  Font fonte = new Font("Helvetica", 1, 14);
  FontMetrics fontpequena;
  FontMetrics fontgrande;
  Image ii;
  Color dotcor = new Color(192, 192, 0);
  Color cortabuleiro;
  boolean nojogo = false;
  boolean morreu = false;

  final int tamanhobloco = 24;
  final int nroblocos = 15;
  final int tamanhotela = 360;
  final int pacanimdelay = 2;
  final int pacmananimcont = 4;
  final int maxfantasmas = 12;
  final int pacmanvelocidade = 4;

  int pacanimcont = 2;
  int pacanimdir = 1;
  int pacmananimpos = 0;
  int nrofantasmas = 1;
  int nrofantasmasAZUL = 1;
  int pacsesq;
  int pontos;
  int contadormortes;
  int[] dx;
  int[] dy;
  int[] fantasmax;
  int[] fantasmay;
  int[] fantasmadx;
  int[] fantasmady;
  int[] fantasmaspeed;
  int[] fantasmaax;
  int[] fantasmaay;
  int[] fantasmaadx;
  int[] fantasmaady;
  int[] fantasmaaspeed;
  Image fantasma, fantasma2, pacman1, pacman2cima, pacman2esq,
        pacman2dir, pacman2baixo, pacman3cima, pacman3baixo, pacman3esq,
        pacman3dir, pacman4cima, pacman4baixo, pacman4esq,  pacman4dir;
  int pacmanx;
  int pacmany;
  int pacmandx;
  int pacmandy;
  int reqdx;
  int reqdy;
  int viewdx;
  int viewdy;
  final short[] dadosnivel = 
    { 19, 26, 26, 26, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 22,
      21, 0,  0,  0,  17, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20,
      21, 0,  0,  0,  17, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20,
      21, 0,  0,  0,  17, 16, 16, 24, 16, 16, 16, 16, 16, 16, 20,
      17, 18, 18, 18, 16, 16, 20, 0,  17, 16, 16, 16, 16, 16, 20,
      17, 16, 16, 16, 16, 16, 20, 0,  17, 16, 16, 16, 16, 24, 20,
      25, 16, 16, 16, 24, 24, 28, 0,  25, 24, 24, 16, 20, 0,  21,
      1,  17, 16, 20, 0,  0,  0,  0,  0,  0,  0,  17, 20, 0,  21,
      1,  17, 16, 16, 18, 18, 22, 0,  19, 18, 18, 16, 20, 0,  21,
      1,  17, 16, 16, 16, 16, 20, 0,  17, 16, 16, 16, 20, 0,  21,
      1,  17, 16, 16, 16, 16, 20, 0,  17, 16, 16, 16, 20, 0,  21,
      1,  17, 16, 16, 16, 16, 16, 18, 16, 16, 16, 16, 20, 0,  21,
      1,  17, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20, 0,  21,
      1,  25, 24, 24, 24, 24, 24, 24, 24, 24, 16, 16, 16, 18, 20,
      9,  8,  8,  8,  8,  8,  8,  8,  8,  8,  25, 24, 24, 24, 28 };


                                    
  final int[] velovalidas = { 1, 2, 3, 4, 6, 8 };
  final int velomax = 6;

  int veloatual = 3;
  int[] dadostela;
  Timer timer;
  FuzzyVariable saidaDistancia;
  FuzzyVariable saidaComportamento;
  FuzzyRete engine = new FuzzyRete();
  RightLinearFunction rlf = new RightLinearFunction();
  LeftLinearFunction llf = new LeftLinearFunction();
  RightLinearFunction tf = new RightLinearFunction();

  public Tabuleiro() throws JessException
  {
    getImagens();

    addKeyListener(new TAdapter());


    this.dadostela = new int['á'];
    this.cortabuleiro = new Color(5, 100, 5);
    setFocusable(true);

    this.d = new Dimension(400, 400);

    setBackground(Color.black);
    setDoubleBuffered(true);

    this.fantasmaax = new int[12];
    this.fantasmax = new int[12];
    this.fantasmaadx = new int[12];
    this.fantasmadx = new int[12];
    this.fantasmaay = new int[12];
    this.fantasmay = new int[12];
    this.fantasmaady = new int[12];
    this.fantasmady = new int[12];
    this.fantasmaaspeed = new int[12];
    this.fantasmaspeed = new int[12];
    this.dx = new int[4];
    this.dy = new int[4];
    this.timer = new Timer(40, this);
    this.timer.start();
    try
    {
      String clpFile = "(batch " + "regras.clp)";
      engine.executeCommand(clpFile);
      engine.executeCommand("(reset)");
      engine.executeCommand("(run 1)");
      saidaDistancia = (FuzzyVariable)engine.fetch("DISTPACFUZZY").externalAddressValue(null);
      int x=0,y;
      y=x+1;
    }
    catch (JessException je) {
      System.err.println(je);
    }
  }

  public void addNotify() {
    super.addNotify();
    iniciarJogo();
  }

  public void fazerAnimacao()
  {
    this.pacanimcont -= 1;
    if (this.pacanimcont <= 0) {
      this.pacanimcont = 2;
      this.pacmananimpos += this.pacanimdir;
      if ((this.pacmananimpos == 3) || (this.pacmananimpos == 0))
        this.pacanimdir = (-this.pacanimdir);
    }
  }

  public void jogar(Graphics2D g2d)
  {
    if (this.morreu) {
      morte();
    } else {
      moverPacman();
      desenharPacman(g2d);
      movefantasmas(g2d);
      movefantasmasA(g2d);
      checarTabuleiro();
    }
  }

  public void mostrarIntro(Graphics2D g2d)
  {
    g2d.setColor(new Color(0, 32, 48));
    g2d.fillRect(50, 150, 260, 50);
    g2d.setColor(Color.white);
    g2d.drawRect(50, 150, 260, 50);

    String s = "Pressione s para começar.";
    Font small = new Font("Helvetica", 1, 14);
    FontMetrics metr = getFontMetrics(small);

    g2d.setColor(Color.white);
    g2d.setFont(small);
    g2d.drawString(s, (360 - metr.stringWidth(s)) / 2, 180);
  }

  public void desenharPontos(Graphics2D g)
  {
    g.setFont(this.fonte);
    g.setColor(new Color(96, 128, 255));
    String s = "pontos: " + this.pontos;
    g.drawString(s, 276, 376);
    for (int i = 0; i < this.pacsesq; ++i)
      g.drawImage(this.pacman3esq, i * 28 + 8, 361, this);
  }

  public void checarTabuleiro()
  {
    short i = 0;
    boolean finished = true;

    while ((i < 225) && (finished))
    {
      finished = false;
      i = (short)(i + 1);
    }

    if (finished) {
      this.pontos += 50;

      if (this.nrofantasmas < 12)
        this.nrofantasmas += 1;
      if (this.veloatual < 6)
        this.veloatual += 1;
      nivelIniciar();
    }
  }

  public void morte()
  {
    this.pacsesq -= 1;
    if (this.pacsesq == 0)
      this.nojogo = false;
    nivelContinuar();
  }

  public void movefantasmas(Graphics2D g2d)
  {
    for (short i = 0; i < this.nrofantasmas; i = (short)(i + 1)) {
      if ((this.fantasmax[i] % 24 == 0) && (this.fantasmay[i] % 24 == 0)) {
        int pos = this.fantasmax[i] / 24 + 15 * (this.fantasmay[i] / 24);
        int count = 0;
        if (((this.dadostela[pos] & 0x1) == 0) && (this.fantasmadx[i] != 1)) {
          this.dx[count] = -1;
          this.dy[count] = 0;
          ++count;
        }
        if (((this.dadostela[pos] & 0x2) == 0) && (this.fantasmady[i] != 1)) {
          this.dx[count] = 0;
          this.dy[count] = -1;
          ++count;
        }
        if (((this.dadostela[pos] & 0x4) == 0) && (this.fantasmadx[i] != -1)) {
          this.dx[count] = 1;
          this.dy[count] = 0;
          ++count;
        }
        if (((this.dadostela[pos] & 0x8) == 0) && (this.fantasmady[i] != -1)) {
          this.dx[count] = 0;
          this.dy[count] = 1;
          ++count;
        }

        if (count == 0) {
          if ((this.dadostela[pos] & 0xF) == 15) {
            this.fantasmadx[i] = 0;
            this.fantasmady[i] = 0;
          } else {
            this.fantasmadx[i] = (-this.fantasmadx[i]);
            this.fantasmady[i] = (-this.fantasmady[i]);
          }
        } else {
          count = (int)(Math.random() * count);
          if (count > 3)
            count = 3;
          this.fantasmadx[i] = this.dx[count];
          this.fantasmady[i] = this.dy[count];
        }
      }

      this.fantasmax[i] += this.fantasmadx[i] * this.fantasmaspeed[i];
      this.fantasmay[i] += this.fantasmady[i] * this.fantasmaspeed[i];
      desenharFantasma(g2d, this.fantasmax[i] + 1, this.fantasmay[i] + 1);

      if ((this.pacmanx <= this.fantasmax[i] - 12) || (this.pacmanx >= this.fantasmax[i] + 12) || (this.pacmany <= this.fantasmay[i] - 12) || (this.pacmany >= this.fantasmay[i] + 12) || (!this.nojogo)) {
        continue;
      }

      this.morreu = true;
      this.contadormortes = 64;
    }
  }

  public void movefantasmasA(Graphics2D g2d)
  {
    int count1 = 0;
    double distancia = 0.0D;

    String mudarComport = "";
    boolean hunter = false; boolean shy = false;
    this.fantasma2 = new ImageIcon(Tabuleiro.class.getResource("GhostScared1.gif")).getImage();
    for (int i = 0; i < this.nrofantasmasAZUL; ++i)
    {
      distancia = Math.sqrt((this.pacmanx - this.fantasmaax[i]) * (this.pacmanx - this.fantasmaax[i]) + (this.pacmany - this.fantasmaay[i]) * (this.pacmany - this.fantasmaay[i]));
      FuzzyValue inputDist;
      try
      {
        FuzzyValue.setConfineFuzzySetsToUOD(true);
        inputDist = new FuzzyValue(saidaDistancia, new TriangleFuzzySet(distancia, distancia, distancia));

        FuzzyValue.setConfineFuzzySetsToUOD(false);
      }
      catch (FuzzyException fe)
      {
        System.err.println(fe);
        return;
      }

      try
      {
        engine.store("DIST1", inputDist);
        engine.executeCommand("(assert (dist (fetch DIST1)) )");
        engine.executeCommand("(run)");
        mudarComport = engine.fetch("COMPORT").stringValue(null);
      }
      catch (JessException je)
      {
        System.err.println(je);
      }

      if (mudarComport.equals("HUNTER")) {
        hunter = true;
        this.fantasma2 = new ImageIcon(Tabuleiro.class.getResource("GhostEvil.gif")).getImage();
      }
      else if (mudarComport.equals("SHY")) {
        shy = true;
        this.fantasma2 = new ImageIcon(Tabuleiro.class.getResource("GhostShy.gif")).getImage();
      }

      if (shy == true) {
        if ((this.fantasmaax[i] % 24 == 0) && (this.fantasmaay[i] % 24 == 0)) {
          int pos1 = this.fantasmaax[i] / 24 + 15 * (this.fantasmaay[i] / 24);
          count1 = 0;
          if (((this.dadostela[pos1] & 0x1) == 0) && (this.fantasmaadx[i] != 1)) {
            this.dx[count1] = -1;
            this.dy[count1] = 0;
            ++count1;
          }
          if (((this.dadostela[pos1] & 0x2) == 0) && (this.fantasmaady[i] != 1)) {
            this.dx[count1] = 0;
            this.dy[count1] = -1;
            ++count1;
          }
          if (((this.dadostela[pos1] & 0x4) == 0) && (this.fantasmaadx[i] != -1)) {
            this.dx[count1] = 1;
            this.dy[count1] = 0;
            ++count1;
          }
          if (((this.dadostela[pos1] & 0x8) == 0) && (this.fantasmaady[i] != -1)) {
            this.dx[count1] = 0;
            this.dy[count1] = 1;
            ++count1;
          }

          if (count1 == 0) {
            if ((this.dadostela[pos1] & 0xF) == 15) {
              this.fantasmaadx[i] = 0;
              this.fantasmaady[i] = 0;
            } else {
              this.fantasmaadx[i] = (-this.fantasmaadx[i]);
              this.fantasmaady[i] = (-this.fantasmaady[i]);
            }
          } else {
            count1 = (int)(Math.random() * count1);
            if (count1 > 3)
              count1 = 3;
            this.fantasmaadx[i] = this.dx[count1];
            this.fantasmaady[i] = this.dy[count1];
          }
        }

        this.fantasmaaspeed[0] = 2;
        this.fantasmaax[i] += this.fantasmaadx[i] * this.fantasmaaspeed[i];
        this.fantasmaay[i] += this.fantasmaady[i] * this.fantasmaaspeed[i];
      }
      else if (hunter == true)
      {
        this.fantasmaaspeed[0] = 6;
      }
      else {
        if ((this.fantasmaax[i] % 24 == 0) && (this.fantasmaay[i] % 24 == 0)) {
          int pos1 = this.fantasmaax[i] / 24 + 15 * (this.fantasmaay[i] / 24);
          count1 = 0;
          if (((this.dadostela[pos1] & 0x1) == 0) && (this.fantasmaadx[i] != 1)) {
            this.dx[count1] = -1;
            this.dy[count1] = 0;
            ++count1;
          }

          if (((this.dadostela[pos1] & 0x2) == 0) && (this.fantasmaady[i] != 1)) {
            this.dx[count1] = 0;
            this.dy[count1] = -1;
            ++count1;
          }
          if (((this.dadostela[pos1] & 0x4) == 0) && (this.fantasmaadx[i] != -1)) {
            this.dx[count1] = 1;
            this.dy[count1] = 0;
            ++count1;
          }
          if (((this.dadostela[pos1] & 0x8) == 0) && (this.fantasmaady[i] != -1)) {
            this.dx[count1] = 0;
            this.dy[count1] = 1;
            ++count1;
          }

          if (count1 == 0) {
            if ((this.dadostela[pos1] & 0xF) == 15) {
              this.fantasmaadx[i] = 0;
              this.fantasmaady[i] = 0;
            } else {
              this.fantasmaadx[i] = (-this.fantasmaadx[i]);
              this.fantasmaady[i] = (-this.fantasmaady[i]);
            }
          } else {
            count1 = (int)(Math.random() * count1);
            if (count1 > 3)
              count1 = 3;
            this.fantasmaadx[i] = this.dx[count1];
            this.fantasmaady[i] = this.dy[count1];
          }
        }

        this.fantasmaaspeed[0] = 1;

        this.fantasmaax[i] += this.fantasmaadx[i] * this.fantasmaaspeed[i];
        this.fantasmaay[i] += this.fantasmaady[i] * this.fantasmaaspeed[i];
      }
      desenharFantasmaA(g2d, this.fantasmaax[i] + 1, this.fantasmaay[i] + 1);
      if ((this.pacmanx <= this.fantasmaax[i] - 12) || (this.pacmanx >= this.fantasmaax[i] + 12) || (this.pacmany <= this.fantasmaay[i] - 12) || (this.pacmany >= this.fantasmaay[i] + 12) || (!this.nojogo)) {
        continue;
      }

      this.morreu = true;
      this.contadormortes = 64;
    }
  }

  public void desenharFantasma(Graphics2D g2d, int x, int y)
  {
    g2d.drawImage(this.fantasma, x, y, this);
  }

  public void desenharFantasmaA(Graphics2D g2d, int x, int y) {
    g2d.drawImage(this.fantasma2, x, y, this);
  }

  public void moverPacman()
  {
    if ((this.reqdx == -this.pacmandx) && (this.reqdy == -this.pacmandy)) {
      this.pacmandx = this.reqdx;
      this.pacmandy = this.reqdy;
      this.viewdx = this.pacmandx;
      this.viewdy = this.pacmandy;
    }
    if ((this.pacmanx % 24 == 0) && (this.pacmany % 24 == 0)) {
      int pos = this.pacmanx / 24 + 15 * (this.pacmany / 24);
      int ch = this.dadostela[pos];

      if ((ch & 0x10) != 0) {
        this.dadostela[pos] = (short)(ch & 0xF);
        this.pontos += 1;
      }

      if ((((this.reqdx != 0) || (this.reqdy != 0))) && 
        (((this.reqdx != -1) || (this.reqdy != 0) || ((ch & 0x1) == 0))) && (((this.reqdx != 1) || (this.reqdy != 0) || ((ch & 0x4) == 0))) && (((this.reqdx != 0) || (this.reqdy != -1) || ((ch & 0x2) == 0))) && ((
        (this.reqdx != 0) || (this.reqdy != 1) || ((ch & 0x8) == 0))))
      {
        this.pacmandx = this.reqdx;
        this.pacmandy = this.reqdy;
        this.viewdx = this.pacmandx;
        this.viewdy = this.pacmandy;
      }

      if (((this.pacmandx == -1) && (this.pacmandy == 0) && ((ch & 0x1) != 0)) || ((this.pacmandx == 1) && (this.pacmandy == 0) && ((ch & 0x4) != 0)) || ((this.pacmandx == 0) && (this.pacmandy == -1) && ((ch & 0x2) != 0)) || ((this.pacmandx == 0) && (this.pacmandy == 1) && ((ch & 0x8) != 0)))
      {
        this.pacmandx = 0;
        this.pacmandy = 0;
      }
    }
    this.pacmanx += 4 * this.pacmandx;
    this.pacmany += 4 * this.pacmandy;
  }

  public void desenharPacman(Graphics2D g2d)
  {
    if (this.viewdx == -1)
      desenharPacmanesq(g2d);
    else if (this.viewdx == 1)
      desenharPacmanDir(g2d);
    else if (this.viewdy == -1)
      desenharPacmanCima(g2d);
    else
      desenharPacmanbaixo(g2d);
  }

  public void desenharPacmanCima(Graphics2D g2d) {
    switch (this.pacmananimpos)
    {
    case 1:
      g2d.drawImage(this.pacman2cima, this.pacmanx + 1, this.pacmany + 1, this);
      break;
    case 2:
      g2d.drawImage(this.pacman3cima, this.pacmanx + 1, this.pacmany + 1, this);
      break;
    case 3:
      g2d.drawImage(this.pacman4cima, this.pacmanx + 1, this.pacmany + 1, this);
      break;
    default:
      g2d.drawImage(this.pacman1, this.pacmanx + 1, this.pacmany + 1, this);
    }
  }

  public void desenharPacmanbaixo(Graphics2D g2d)
  {
    switch (this.pacmananimpos)
    {
    case 1:
      g2d.drawImage(this.pacman2baixo, this.pacmanx + 1, this.pacmany + 1, this);
      break;
    case 2:
      g2d.drawImage(this.pacman3baixo, this.pacmanx + 1, this.pacmany + 1, this);
      break;
    case 3:
      g2d.drawImage(this.pacman4baixo, this.pacmanx + 1, this.pacmany + 1, this);
      break;
    default:
      g2d.drawImage(this.pacman1, this.pacmanx + 1, this.pacmany + 1, this);
    }
  }

  public void desenharPacmanesq(Graphics2D g2d)
  {
    switch (this.pacmananimpos)
    {
    case 1:
      g2d.drawImage(this.pacman2esq, this.pacmanx + 1, this.pacmany + 1, this);
      break;
    case 2:
      g2d.drawImage(this.pacman3esq, this.pacmanx + 1, this.pacmany + 1, this);
      break;
    case 3:
      g2d.drawImage(this.pacman4esq, this.pacmanx + 1, this.pacmany + 1, this);
      break;
    default:
      g2d.drawImage(this.pacman1, this.pacmanx + 1, this.pacmany + 1, this);
    }
  }

  public void desenharPacmanDir(Graphics2D g2d)
  {
    switch (this.pacmananimpos)
    {
    case 1:
      g2d.drawImage(this.pacman2dir, this.pacmanx + 1, this.pacmany + 1, this);
      break;
    case 2:
      g2d.drawImage(this.pacman3dir, this.pacmanx + 1, this.pacmany + 1, this);
      break;
    case 3:
      g2d.drawImage(this.pacman4dir, this.pacmanx + 1, this.pacmany + 1, this);
      break;
    default:
      g2d.drawImage(this.pacman1, this.pacmanx + 1, this.pacmany + 1, this);
    }
  }

  public void desenharTabuleiro(Graphics2D g2d)
  {
    short i = 0;

    for (int y = 0; y < 360; y += 24)
      for (int x = 0; x < 360; x += 24) {
        g2d.setColor(this.cortabuleiro);
        g2d.setStroke(new BasicStroke(2.0F));

        if ((this.dadostela[i] & 0x1) != 0)
        {
          g2d.drawLine(x, y, x, y + 24 - 1);
        }
        if ((this.dadostela[i] & 0x2) != 0)
        {
          g2d.drawLine(x, y, x + 24 - 1, y);
        }
        if ((this.dadostela[i] & 0x4) != 0)
        {
          g2d.drawLine(x + 24 - 1, y, x + 24 - 1, y + 24 - 1);
        }

        if ((this.dadostela[i] & 0x8) != 0)
        {
          g2d.drawLine(x, y + 24 - 1, x + 24 - 1, y + 24 - 1);
        }

        if ((this.dadostela[i] & 0x10) != 0)
        {
          g2d.setColor(this.dotcor);
          g2d.fillRect(x + 11, y + 11, 2, 2);
        }
        i = (short)(i + 1);
      }
  }

  public void iniciarJogo()
  {
    this.pacsesq = 3;
    this.pontos = 0;
    nivelIniciar();
    this.nrofantasmas = 1;
    this.nrofantasmasAZUL = 1;
    this.veloatual = 3;
  }

  public void nivelIniciar()
  {
    for (int i = 0; i < 225; ++i) {
      this.dadostela[i] = this.dadosnivel[i];
    }
    nivelContinuar();
  }

  public void nivelContinuar()
  {
    int dx = 1;

    for (short i = 0; i < this.nrofantasmas; i = (short)(i + 1)) {
      this.fantasmay[i] = ((int)(Math.random() * 6.0D + 5.0D) * 24);
      this.fantasmax[i] = ((int)(Math.random() * 6.0D + 0.0D) * 24);
      this.fantasmady[i] = 0;
      this.fantasmadx[i] = dx;
      dx = -dx;
      int random = (int)(Math.random() * (this.veloatual + 1));
      if (random > this.veloatual)
        random = this.veloatual;
      this.fantasmaspeed[i] = this.velovalidas[random];
    }

    for (int z = 0; z < this.nrofantasmasAZUL; ++z) {
      this.fantasmaay[z] = ((int)(Math.random() * 6.0D + 0.0D) * 24);
      this.fantasmaax[z] = ((int)(Math.random() * 6.0D + 9.0D) * 24);
      this.fantasmaady[z] = 0;
      this.fantasmaadx[z] = dx;
      dx = -dx;
      int random = (int)(Math.random() * (this.veloatual - 1));
      if (random > this.veloatual)
        random = this.veloatual;
      this.fantasmaaspeed[z] = this.velovalidas[random];
    }

    this.pacmanx = 216;
    this.pacmany = 312;
    this.pacmandx = 0;
    this.pacmandy = 0;
    this.reqdx = 0;
    this.reqdy = 0;
    this.viewdx = -1;
    this.viewdy = 0;
    this.morreu = false;
  }

  public void getImagens()
  {
      
    fantasma = new ImageIcon(Tabuleiro.class.getResource("Ghost1.gif")).getImage();
    fantasma2 = new ImageIcon(Tabuleiro.class.getResource("GhostScared1.gif")).getImage();
    pacman1 = new ImageIcon(Tabuleiro.class.getResource("PacMan1.gif")).getImage();
    pacman2cima = new ImageIcon(Tabuleiro.class.getResource("PacMan2up.gif")).getImage();
    pacman3cima = new ImageIcon(Tabuleiro.class.getResource("PacMan2up.gif")).getImage();
    pacman4cima = new ImageIcon(Tabuleiro.class.getResource("PacMan3up.gif")).getImage();
    pacman2baixo = new ImageIcon(Tabuleiro.class.getResource("PacMan2down.gif")).getImage();
    pacman3baixo = new ImageIcon(Tabuleiro.class.getResource("PacMan2down.gif")).getImage();
    pacman4baixo = new ImageIcon(Tabuleiro.class.getResource("PacMan3down.gif")).getImage();
    pacman2esq = new ImageIcon(Tabuleiro.class.getResource("PacMan2left.gif")).getImage();
    pacman3esq = new ImageIcon(Tabuleiro.class.getResource("PacMan2left.gif")).getImage();
    pacman4esq = new ImageIcon(Tabuleiro.class.getResource("PacMan3left.gif")).getImage();
    pacman2dir = new ImageIcon(Tabuleiro.class.getResource("PacMan2right.gif")).getImage();
    pacman3dir = new ImageIcon(Tabuleiro.class.getResource("PacMan2right.gif")).getImage();
    pacman4dir = new ImageIcon(Tabuleiro.class.getResource("PacMan3right.gif")).getImage();
  }

  public void paint(Graphics g)
  {
    super.paint(g);

    Graphics2D g2d = (Graphics2D)g;

    g2d.setColor(Color.black);
    g2d.fillRect(0, 0, this.d.width, this.d.height);

    desenharTabuleiro(g2d);
    desenharPontos(g2d);
    fazerAnimacao();
    if (this.nojogo)
      jogar(g2d);
    else {
      mostrarIntro(g2d);
    }
    g.drawImage(this.ii, 5, 5, this);
    Toolkit.getDefaultToolkit().sync();
    g.dispose();
  }
  
  class TAdapter extends KeyAdapter {
        public void keyPressed(KeyEvent e) {

          int key = e.getKeyCode();

          if (nojogo==true) //checa se o jogo ja foi iniciado
          {
            if (key == KeyEvent.VK_LEFT)
            {
              reqdx=-1;
              reqdy=0;
            }
            else if (key == KeyEvent.VK_RIGHT)
            {
              reqdx=1;
              reqdy=0;
            }
            else if (key == KeyEvent.VK_UP)
            {
              reqdx=0;
              reqdy=-1;
            }
            else if (key == KeyEvent.VK_DOWN)
            {
              reqdx=0;
              reqdy=1;
            }
            else if (key == KeyEvent.VK_ESCAPE && timer.isRunning())
            {
              nojogo=false;
            }
            else if (key == KeyEvent.VK_PAUSE) {
                if (timer.isRunning())
                    timer.stop();
                else timer.start();
            }
          }
          else
          {
            if (key == 's' || key == 'S')
          {
              nojogo=true;
              iniciarJogo();
            }
          }
      }///fim da classe

          public void keyReleased(KeyEvent e) {
              int key = e.getKeyCode();

              if (key == Event.LEFT || key == Event.RIGHT ||
                 key == Event.UP ||  key == Event.DOWN)
              {
                reqdx=0;
                reqdy=0;
              }
          }
      }




  public void actionPerformed(ActionEvent e)
  {
    repaint();
  }
}

