package sdk;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D.Double;
import java.awt.image.BufferedImage;
import java.awt.image.DirectColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.TreeSet;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

public final class StdDraw implements ActionListener, MouseListener, MouseMotionListener, KeyListener {
	public static final Color BLACK;
	public static final Color BLUE;
	public static final Color CYAN;
	public static final Color DARK_GRAY;
	public static final Color GRAY;
	public static final Color GREEN;
	public static final Color LIGHT_GRAY;
	public static final Color MAGENTA;
	public static final Color ORANGE;
	public static final Color PINK;
	public static final Color RED;
	public static final Color WHITE;
	public static final Color YELLOW;
	public static final Color BOOK_BLUE;
	public static final Color BOOK_LIGHT_BLUE;
	public static final Color BOOK_RED;
	private static final Color DEFAULT_PEN_COLOR;
	private static final Color DEFAULT_CLEAR_COLOR;
	private static Color penColor;
	private static final int DEFAULT_SIZE = 512;
	private static int width;
	private static int height;
	private static final double DEFAULT_PEN_RADIUS = 0.002D;
	private static double penRadius;
	private static boolean defer;
	private static final double BORDER = 0.05D;
	private static final double DEFAULT_XMIN = 0.0D;
	private static final double DEFAULT_XMAX = 1.0D;
	private static final double DEFAULT_YMIN = 0.0D;
	private static final double DEFAULT_YMAX = 1.0D;
	private static double xmin;
	private static double ymin;
	private static double xmax;
	private static double ymax;
	private static Object mouseLock;
	private static Object keyLock;
	private static final Font DEFAULT_FONT;
	private static Font font;
	private static BufferedImage offscreenImage;
	private static BufferedImage onscreenImage;
	private static Graphics2D offscreen;
	private static Graphics2D onscreen;
	private static StdDraw std;
	private static JFrame frame;
	private static boolean mousePressed;
	private static double mouseX;
	private static double mouseY;
	private static LinkedList<Character> keysTyped;
	private static TreeSet<Integer> keysDown;

	private StdDraw() {
	}

	public static void setCanvasSize() {
		setCanvasSize(512, 512);
	}

	public static void setCanvasSize(int var0, int var1) {
		if (var0 >= 1 && var1 >= 1) {
			width = var0;
			height = var1;
			init();
		} else {
			throw new IllegalArgumentException("width and height must be positive");
		}
	}

	private static void init() {
		if (frame != null) {
			frame.setVisible(false);
		}

		frame = new JFrame();
		offscreenImage = new BufferedImage(width, height, 2);
		onscreenImage = new BufferedImage(width, height, 2);
		offscreen = offscreenImage.createGraphics();
		onscreen = onscreenImage.createGraphics();
		setXscale();
		setYscale();
		offscreen.setColor(DEFAULT_CLEAR_COLOR);
		offscreen.fillRect(0, 0, width, height);
		setPenColor();
		setPenRadius();
		setFont();
		clear();
		RenderingHints var0 = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		var0.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		offscreen.addRenderingHints(var0);
		ImageIcon var1 = new ImageIcon(onscreenImage);
		JLabel var2 = new JLabel(var1);
		var2.addMouseListener(std);
		var2.addMouseMotionListener(std);
		frame.setContentPane(var2);
		frame.addKeyListener(std);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(3);
		frame.setTitle("Standard Draw");
		frame.setJMenuBar(createMenuBar());
		frame.pack();
		frame.requestFocusInWindow();
		frame.setVisible(true);
	}

	private static JMenuBar createMenuBar() {
		JMenuBar var0 = new JMenuBar();
		JMenu var1 = new JMenu("File");
		var0.add(var1);
		JMenuItem var2 = new JMenuItem(" Save...   ");
		var2.addActionListener(std);
		var2.setAccelerator(KeyStroke.getKeyStroke(83, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		var1.add(var2);
		return var0;
	}

	public static void setXscale() {
		setXscale(0.0D, 1.0D);
	}

	public static void setYscale() {
		setYscale(0.0D, 1.0D);
	}

	public static void setXscale(double var0, double var2) {
		double var4 = var2 - var0;
		synchronized(mouseLock) {
			xmin = var0 - 0.05D * var4;
			xmax = var2 + 0.05D * var4;
		}
	}

	public static void setYscale(double var0, double var2) {
		double var4 = var2 - var0;
		synchronized(mouseLock) {
			ymin = var0 - 0.05D * var4;
			ymax = var2 + 0.05D * var4;
		}
	}

	public static void setScale(double var0, double var2) {
		double var4 = var2 - var0;
		synchronized(mouseLock) {
			xmin = var0 - 0.05D * var4;
			xmax = var2 + 0.05D * var4;
			ymin = var0 - 0.05D * var4;
			ymax = var2 + 0.05D * var4;
		}
	}

	private static double scaleX(double var0) {
		return (double)width * (var0 - xmin) / (xmax - xmin);
	}

	private static double scaleY(double var0) {
		return (double)height * (ymax - var0) / (ymax - ymin);
	}

	private static double factorX(double var0) {
		return var0 * (double)width / Math.abs(xmax - xmin);
	}

	private static double factorY(double var0) {
		return var0 * (double)height / Math.abs(ymax - ymin);
	}

	private static double userX(double var0) {
		return xmin + var0 * (xmax - xmin) / (double)width;
	}

	private static double userY(double var0) {
		return ymax - var0 * (ymax - ymin) / (double)height;
	}

	public static void clear() {
		clear(DEFAULT_CLEAR_COLOR);
	}

	public static void clear(Color var0) {
		offscreen.setColor(var0);
		offscreen.fillRect(0, 0, width, height);
		offscreen.setColor(penColor);
		draw();
	}

	public static double getPenRadius() {
		return penRadius;
	}

	public static void setPenRadius() {
		setPenRadius(0.002D);
	}

	public static void setPenRadius(double var0) {
		if (var0 < 0.0D) {
			throw new IllegalArgumentException("pen radius must be nonnegative");
		} else {
			penRadius = var0;
			float var2 = (float)(var0 * 512.0D);
			BasicStroke var3 = new BasicStroke(var2, 1, 1);
			offscreen.setStroke(var3);
		}
	}

	public static Color getPenColor() {
		return penColor;
	}

	public static void setPenColor() {
		setPenColor(DEFAULT_PEN_COLOR);
	}

	public static void setPenColor(Color var0) {
		penColor = var0;
		offscreen.setColor(penColor);
	}

	public static void setPenColor(int var0, int var1, int var2) {
		if (var0 >= 0 && var0 < 256) {
			if (var1 >= 0 && var1 < 256) {
				if (var2 >= 0 && var2 < 256) {
					setPenColor(new Color(var0, var1, var2));
				} else {
					throw new IllegalArgumentException("amount of red must be between 0 and 255");
				}
			} else {
				throw new IllegalArgumentException("amount of red must be between 0 and 255");
			}
		} else {
			throw new IllegalArgumentException("amount of red must be between 0 and 255");
		}
	}

	public static Font getFont() {
		return font;
	}

	public static void setFont() {
		setFont(DEFAULT_FONT);
	}

	public static void setFont(Font var0) {
		font = var0;
	}

	public static void line(double var0, double var2, double var4, double var6) {
		offscreen.draw(new Double(scaleX(var0), scaleY(var2), scaleX(var4), scaleY(var6)));
		draw();
	}

	private static void pixel(double var0, double var2) {
		offscreen.fillRect((int)Math.round(scaleX(var0)), (int)Math.round(scaleY(var2)), 1, 1);
	}

	public static void point(double var0, double var2) {
		double var4 = scaleX(var0);
		double var6 = scaleY(var2);
		double var8 = penRadius;
		float var10 = (float)(var8 * 512.0D);
		if (var10 <= 1.0F) {
			pixel(var0, var2);
		} else {
			offscreen.fill(new java.awt.geom.Ellipse2D.Double(var4 - (double)(var10 / 2.0F), var6 - (double)(var10 / 2.0F), (double)var10, (double)var10));
		}

		draw();
	}

	public static void circle(double var0, double var2, double var4) {
		if (var4 < 0.0D) {
			throw new IllegalArgumentException("circle radius must be nonnegative");
		} else {
			double var6 = scaleX(var0);
			double var8 = scaleY(var2);
			double var10 = factorX(2.0D * var4);
			double var12 = factorY(2.0D * var4);
			if (var10 <= 1.0D && var12 <= 1.0D) {
				pixel(var0, var2);
			} else {
				offscreen.draw(new java.awt.geom.Ellipse2D.Double(var6 - var10 / 2.0D, var8 - var12 / 2.0D, var10, var12));
			}

			draw();
		}
	}

	public static void filledCircle(double var0, double var2, double var4) {
		if (var4 < 0.0D) {
			throw new IllegalArgumentException("circle radius must be nonnegative");
		} else {
			double var6 = scaleX(var0);
			double var8 = scaleY(var2);
			double var10 = factorX(2.0D * var4);
			double var12 = factorY(2.0D * var4);
			if (var10 <= 1.0D && var12 <= 1.0D) {
				pixel(var0, var2);
			} else {
				offscreen.fill(new java.awt.geom.Ellipse2D.Double(var6 - var10 / 2.0D, var8 - var12 / 2.0D, var10, var12));
			}

			draw();
		}
	}

	public static void ellipse(double var0, double var2, double var4, double var6) {
		if (var4 < 0.0D) {
			throw new IllegalArgumentException("ellipse semimajor axis must be nonnegative");
		} else if (var6 < 0.0D) {
			throw new IllegalArgumentException("ellipse semiminor axis must be nonnegative");
		} else {
			double var8 = scaleX(var0);
			double var10 = scaleY(var2);
			double var12 = factorX(2.0D * var4);
			double var14 = factorY(2.0D * var6);
			if (var12 <= 1.0D && var14 <= 1.0D) {
				pixel(var0, var2);
			} else {
				offscreen.draw(new java.awt.geom.Ellipse2D.Double(var8 - var12 / 2.0D, var10 - var14 / 2.0D, var12, var14));
			}

			draw();
		}
	}

	public static void filledEllipse(double var0, double var2, double var4, double var6) {
		if (var4 < 0.0D) {
			throw new IllegalArgumentException("ellipse semimajor axis must be nonnegative");
		} else if (var6 < 0.0D) {
			throw new IllegalArgumentException("ellipse semiminor axis must be nonnegative");
		} else {
			double var8 = scaleX(var0);
			double var10 = scaleY(var2);
			double var12 = factorX(2.0D * var4);
			double var14 = factorY(2.0D * var6);
			if (var12 <= 1.0D && var14 <= 1.0D) {
				pixel(var0, var2);
			} else {
				offscreen.fill(new java.awt.geom.Ellipse2D.Double(var8 - var12 / 2.0D, var10 - var14 / 2.0D, var12, var14));
			}

			draw();
		}
	}

	public static void arc(double var0, double var2, double var4, double var6, double var8) {
		if (var4 < 0.0D) {
			throw new IllegalArgumentException("arc radius must be nonnegative");
		} else {
			while(var8 < var6) {
				var8 += 360.0D;
			}

			double var10 = scaleX(var0);
			double var12 = scaleY(var2);
			double var14 = factorX(2.0D * var4);
			double var16 = factorY(2.0D * var4);
			if (var14 <= 1.0D && var16 <= 1.0D) {
				pixel(var0, var2);
			} else {
				offscreen.draw(new java.awt.geom.Arc2D.Double(var10 - var14 / 2.0D, var12 - var16 / 2.0D, var14, var16, var6, var8 - var6, 0));
			}

			draw();
		}
	}

	public static void square(double var0, double var2, double var4) {
		if (var4 < 0.0D) {
			throw new IllegalArgumentException("square side length must be nonnegative");
		} else {
			double var6 = scaleX(var0);
			double var8 = scaleY(var2);
			double var10 = factorX(2.0D * var4);
			double var12 = factorY(2.0D * var4);
			if (var10 <= 1.0D && var12 <= 1.0D) {
				pixel(var0, var2);
			} else {
				offscreen.draw(new java.awt.geom.Rectangle2D.Double(var6 - var10 / 2.0D, var8 - var12 / 2.0D, var10, var12));
			}

			draw();
		}
	}

	public static void filledSquare(double var0, double var2, double var4) {
		if (var4 < 0.0D) {
			throw new IllegalArgumentException("square side length must be nonnegative");
		} else {
			double var6 = scaleX(var0);
			double var8 = scaleY(var2);
			double var10 = factorX(2.0D * var4);
			double var12 = factorY(2.0D * var4);
			if (var10 <= 1.0D && var12 <= 1.0D) {
				pixel(var0, var2);
			} else {
				offscreen.fill(new java.awt.geom.Rectangle2D.Double(var6 - var10 / 2.0D, var8 - var12 / 2.0D, var10, var12));
			}

			draw();
		}
	}

	public static void rectangle(double var0, double var2, double var4, double var6) {
		if (var4 < 0.0D) {
			throw new IllegalArgumentException("half width must be nonnegative");
		} else if (var6 < 0.0D) {
			throw new IllegalArgumentException("half height must be nonnegative");
		} else {
			double var8 = scaleX(var0);
			double var10 = scaleY(var2);
			double var12 = factorX(2.0D * var4);
			double var14 = factorY(2.0D * var6);
			if (var12 <= 1.0D && var14 <= 1.0D) {
				pixel(var0, var2);
			} else {
				offscreen.draw(new java.awt.geom.Rectangle2D.Double(var8 - var12 / 2.0D, var10 - var14 / 2.0D, var12, var14));
			}

			draw();
		}
	}

	public static void filledRectangle(double var0, double var2, double var4, double var6) {
		if (var4 < 0.0D) {
			throw new IllegalArgumentException("half width must be nonnegative");
		} else if (var6 < 0.0D) {
			throw new IllegalArgumentException("half height must be nonnegative");
		} else {
			double var8 = scaleX(var0);
			double var10 = scaleY(var2);
			double var12 = factorX(2.0D * var4);
			double var14 = factorY(2.0D * var6);
			if (var12 <= 1.0D && var14 <= 1.0D) {
				pixel(var0, var2);
			} else {
				offscreen.fill(new java.awt.geom.Rectangle2D.Double(var8 - var12 / 2.0D, var10 - var14 / 2.0D, var12, var14));
			}

			draw();
		}
	}

	public static void polygon(double[] var0, double[] var1) {
		int var2 = var0.length;
		GeneralPath var3 = new GeneralPath();
		var3.moveTo((float)scaleX(var0[0]), (float)scaleY(var1[0]));

		for(int var4 = 0; var4 < var2; ++var4) {
			var3.lineTo((float)scaleX(var0[var4]), (float)scaleY(var1[var4]));
		}

		var3.closePath();
		offscreen.draw(var3);
		draw();
	}

	public static void filledPolygon(double[] var0, double[] var1) {
		int var2 = var0.length;
		GeneralPath var3 = new GeneralPath();
		var3.moveTo((float)scaleX(var0[0]), (float)scaleY(var1[0]));

		for(int var4 = 0; var4 < var2; ++var4) {
			var3.lineTo((float)scaleX(var0[var4]), (float)scaleY(var1[var4]));
		}

		var3.closePath();
		offscreen.fill(var3);
		draw();
	}

	private static Image getImage(String var0) {
		ImageIcon var1 = new ImageIcon(var0);
		URL var2;
		if (var1 == null || var1.getImageLoadStatus() != 8) {
			try {
				var2 = new URL(var0);
				var1 = new ImageIcon(var2);
			} catch (Exception var3) {
			}
		}

		if (var1 == null || var1.getImageLoadStatus() != 8) {
			var2 = StdDraw.class.getResource(var0);
			if (var2 == null) {
				throw new IllegalArgumentException("image " + var0 + " not found");
			}

			var1 = new ImageIcon(var2);
		}

		return var1.getImage();
	}

	public static void picture(double var0, double var2, String var4) {
		Image var5 = getImage(var4);
		double var6 = scaleX(var0);
		double var8 = scaleY(var2);
		int var10 = var5.getWidth((ImageObserver)null);
		int var11 = var5.getHeight((ImageObserver)null);
		if (var10 >= 0 && var11 >= 0) {
			offscreen.drawImage(var5, (int)Math.round(var6 - (double)var10 / 2.0D), (int)Math.round(var8 - (double)var11 / 2.0D), (ImageObserver)null);
			draw();
		} else {
			throw new IllegalArgumentException("image " + var4 + " is corrupt");
		}
	}

	public static void picture(double var0, double var2, String var4, double var5) {
		Image var7 = getImage(var4);
		double var8 = scaleX(var0);
		double var10 = scaleY(var2);
		int var12 = var7.getWidth((ImageObserver)null);
		int var13 = var7.getHeight((ImageObserver)null);
		if (var12 >= 0 && var13 >= 0) {
			offscreen.rotate(Math.toRadians(-var5), var8, var10);
			offscreen.drawImage(var7, (int)Math.round(var8 - (double)var12 / 2.0D), (int)Math.round(var10 - (double)var13 / 2.0D), (ImageObserver)null);
			offscreen.rotate(Math.toRadians(var5), var8, var10);
			draw();
		} else {
			throw new IllegalArgumentException("image " + var4 + " is corrupt");
		}
	}

	public static void picture(double var0, double var2, String var4, double var5, double var7) {
		Image var9 = getImage(var4);
		double var10 = scaleX(var0);
		double var12 = scaleY(var2);
		if (var5 < 0.0D) {
			throw new IllegalArgumentException("width is negative: " + var5);
		} else if (var7 < 0.0D) {
			throw new IllegalArgumentException("height is negative: " + var7);
		} else {
			double var14 = factorX(var5);
			double var16 = factorY(var7);
			if (!(var14 < 0.0D) && !(var16 < 0.0D)) {
				if (var14 <= 1.0D && var16 <= 1.0D) {
					pixel(var0, var2);
				} else {
					offscreen.drawImage(var9, (int)Math.round(var10 - var14 / 2.0D), (int)Math.round(var12 - var16 / 2.0D), (int)Math.round(var14), (int)Math.round(var16), (ImageObserver)null);
				}

				draw();
			} else {
				throw new IllegalArgumentException("image " + var4 + " is corrupt");
			}
		}
	}

	public static void picture(double var0, double var2, String var4, double var5, double var7, double var9) {
		Image var11 = getImage(var4);
		double var12 = scaleX(var0);
		double var14 = scaleY(var2);
		double var16 = factorX(var5);
		double var18 = factorY(var7);
		if (!(var16 < 0.0D) && !(var18 < 0.0D)) {
			if (var16 <= 1.0D && var18 <= 1.0D) {
				pixel(var0, var2);
			}

			offscreen.rotate(Math.toRadians(-var9), var12, var14);
			offscreen.drawImage(var11, (int)Math.round(var12 - var16 / 2.0D), (int)Math.round(var14 - var18 / 2.0D), (int)Math.round(var16), (int)Math.round(var18), (ImageObserver)null);
			offscreen.rotate(Math.toRadians(var9), var12, var14);
			draw();
		} else {
			throw new IllegalArgumentException("image " + var4 + " is corrupt");
		}
	}

	public static void text(double var0, double var2, String var4) {
		offscreen.setFont(font);
		FontMetrics var5 = offscreen.getFontMetrics();
		double var6 = scaleX(var0);
		double var8 = scaleY(var2);
		int var10 = var5.stringWidth(var4);
		int var11 = var5.getDescent();
		offscreen.drawString(var4, (float)(var6 - (double)var10 / 2.0D), (float)(var8 + (double)var11));
		draw();
	}

	public static void text(double var0, double var2, String var4, double var5) {
		double var7 = scaleX(var0);
		double var9 = scaleY(var2);
		offscreen.rotate(Math.toRadians(-var5), var7, var9);
		text(var0, var2, var4);
		offscreen.rotate(Math.toRadians(var5), var7, var9);
	}

	public static void textLeft(double var0, double var2, String var4) {
		offscreen.setFont(font);
		FontMetrics var5 = offscreen.getFontMetrics();
		double var6 = scaleX(var0);
		double var8 = scaleY(var2);
		int var10 = var5.getDescent();
		offscreen.drawString(var4, (float)var6, (float)(var8 + (double)var10));
		draw();
	}

	public static void textRight(double var0, double var2, String var4) {
		offscreen.setFont(font);
		FontMetrics var5 = offscreen.getFontMetrics();
		double var6 = scaleX(var0);
		double var8 = scaleY(var2);
		int var10 = var5.stringWidth(var4);
		int var11 = var5.getDescent();
		offscreen.drawString(var4, (float)(var6 - (double)var10), (float)(var8 + (double)var11));
		draw();
	}

	public static void show(int var0) {
		defer = false;
		draw();

		try {
			Thread.sleep((long)var0);
		} catch (InterruptedException var2) {
			System.out.println("Error sleeping");
		}

		defer = true;
	}

	public static void show() {
		defer = false;
		draw();
	}

	private static void draw() {
		if (!defer) {
			onscreen.drawImage(offscreenImage, 0, 0, (ImageObserver)null);
			frame.repaint();
		}
	}

	public static void save(String var0) {
		File var1 = new File(var0);
		String var2 = var0.substring(var0.lastIndexOf(46) + 1);
		if (var2.toLowerCase().equals("png")) {
			try {
				ImageIO.write(onscreenImage, var2, var1);
			} catch (IOException var10) {
				var10.printStackTrace();
			}
		} else if (var2.toLowerCase().equals("jpg")) {
			WritableRaster var3 = onscreenImage.getRaster();
			WritableRaster var4 = var3.createWritableChild(0, 0, width, height, 0, 0, new int[]{0, 1, 2});
			DirectColorModel var5 = (DirectColorModel)onscreenImage.getColorModel();
			DirectColorModel var6 = new DirectColorModel(var5.getPixelSize(), var5.getRedMask(), var5.getGreenMask(), var5.getBlueMask());
			BufferedImage var7 = new BufferedImage(var6, var4, false, (Hashtable)null);

			try {
				ImageIO.write(var7, var2, var1);
			} catch (IOException var9) {
				var9.printStackTrace();
			}
		} else {
			System.out.println("Invalid image file type: " + var2);
		}

	}

	public void actionPerformed(ActionEvent var1) {
		FileDialog var2 = new FileDialog(frame, "Use a .png or .jpg extension", 1);
		var2.setVisible(true);
		String var3 = var2.getFile();
		if (var3 != null) {
			save(var2.getDirectory() + File.separator + var2.getFile());
		}

	}

	public static boolean mousePressed() {
		synchronized(mouseLock) {
			return mousePressed;
		}
	}

	public static double mouseX() {
		synchronized(mouseLock) {
			return mouseX;
		}
	}

	public static double mouseY() {
		synchronized(mouseLock) {
			return mouseY;
		}
	}

	public void mouseClicked(MouseEvent var1) {
	}

	public void mouseEntered(MouseEvent var1) {
	}

	public void mouseExited(MouseEvent var1) {
	}

	public void mousePressed(MouseEvent var1) {
		synchronized(mouseLock) {
			mouseX = userX((double)var1.getX());
			mouseY = userY((double)var1.getY());
			mousePressed = true;
		}
	}

	public void mouseReleased(MouseEvent var1) {
		synchronized(mouseLock) {
			mousePressed = false;
		}
	}

	public void mouseDragged(MouseEvent var1) {
		synchronized(mouseLock) {
			mouseX = userX((double)var1.getX());
			mouseY = userY((double)var1.getY());
		}
	}

	public void mouseMoved(MouseEvent var1) {
		synchronized(mouseLock) {
			mouseX = userX((double)var1.getX());
			mouseY = userY((double)var1.getY());
		}
	}

	public static boolean hasNextKeyTyped() {
		synchronized(keyLock) {
			return !keysTyped.isEmpty();
		}
	}

	public static char nextKeyTyped() {
		synchronized(keyLock) {
			return (Character)keysTyped.removeLast();
		}
	}

	public static boolean isKeyPressed(int var0) {
		synchronized(keyLock) {
			return keysDown.contains(var0);
		}
	}

	public void keyTyped(KeyEvent var1) {
		synchronized(keyLock) {
			keysTyped.addFirst(var1.getKeyChar());
		}
	}

	public void keyPressed(KeyEvent var1) {
		synchronized(keyLock) {
			keysDown.add(var1.getKeyCode());
		}
	}

	public void keyReleased(KeyEvent var1) {
		synchronized(keyLock) {
			keysDown.remove(var1.getKeyCode());
		}
	}

	public static void main(String[] var0) {
		square(0.2D, 0.8D, 0.1D);
		filledSquare(0.8D, 0.8D, 0.2D);
		circle(0.8D, 0.2D, 0.2D);
		setPenColor(BOOK_RED);
		setPenRadius(0.02D);
		arc(0.8D, 0.2D, 0.1D, 200.0D, 45.0D);
		setPenRadius();
		setPenColor(BOOK_BLUE);
		double[] var1 = new double[]{0.1D, 0.2D, 0.3D, 0.2D};
		double[] var2 = new double[]{0.2D, 0.3D, 0.2D, 0.1D};
		filledPolygon(var1, var2);
		setPenColor(BLACK);
		text(0.2D, 0.5D, "black text");
		setPenColor(WHITE);
		text(0.8D, 0.8D, "white text");
	}

	static {
		BLACK = Color.BLACK;
		BLUE = Color.BLUE;
		CYAN = Color.CYAN;
		DARK_GRAY = Color.DARK_GRAY;
		GRAY = Color.GRAY;
		GREEN = Color.GREEN;
		LIGHT_GRAY = Color.LIGHT_GRAY;
		MAGENTA = Color.MAGENTA;
		ORANGE = Color.ORANGE;
		PINK = Color.PINK;
		RED = Color.RED;
		WHITE = Color.WHITE;
		YELLOW = Color.YELLOW;
		BOOK_BLUE = new Color(9, 90, 166);
		BOOK_LIGHT_BLUE = new Color(103, 198, 243);
		BOOK_RED = new Color(150, 35, 31);
		DEFAULT_PEN_COLOR = BLACK;
		DEFAULT_CLEAR_COLOR = WHITE;
		width = 512;
		height = 512;
		defer = false;
		mouseLock = new Object();
		keyLock = new Object();
		DEFAULT_FONT = new Font("SansSerif", 0, 16);
		std = new StdDraw();
		mousePressed = false;
		mouseX = 0.0D;
		mouseY = 0.0D;
		keysTyped = new LinkedList();
		keysDown = new TreeSet();
		init();
	}
}
