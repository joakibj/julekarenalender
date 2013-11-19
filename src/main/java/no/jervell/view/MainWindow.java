package no.jervell.view;

import com.github.julekarenalender.config.ConfigurationModule;
import com.github.julekarenalender.Participant;
import com.github.julekarenalender.config.AppInfo;
import no.jervell.jul.GameLogic;
import no.jervell.util.ImageFactory;
import no.jervell.view.animation.impl.AnimationLoop;
import no.jervell.view.animation.impl.FrameCounter;
import no.jervell.view.animation.impl.WheelAnimation;
import no.jervell.view.animation.impl.WheelSpinner;
import no.jervell.view.awt.Anchor;
import no.jervell.view.awt.ImageLabel;
import no.jervell.view.awt.Scaling;
import no.jervell.view.swing.ImageView;
import no.jervell.view.swing.WheelView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class MainWindow implements WindowListener {
    private static final String FONT_NAME = "Times New Roman";
    private static final int FONT_SIZE = 50;
    private static final Font PLAIN_FONT = new Font(FONT_NAME, Font.PLAIN, FONT_SIZE);
    private static final Font BOLD_FONT = new Font(FONT_NAME, Font.BOLD, FONT_SIZE);
    private static final double MAX_VELOCITY = 100;
    private static final Color FRAME_BACKGROUND = Color.black;
    private static final Paint BACKGROUND_COLOUR = Color.white;
    private static final Color TEXT_COLOUR = Color.black;

    /**
     * User interface size scaler, 100=native, 200=double size, 50=half size,
     * etc
     */
    //TODO: Make enum?
    final static int scale = 125;
    private AnimationLoop loop;
    private JFrame frame;
    private JMenuBar menuBar;
    private List<Integer> days;
    private ConfigurationModule configurationModule;
    private GameLogic gameLogic;

    //TODO: Split up further into a seperate class encapsulating Wheel functionality
    public WheelView dateWheel;
    public WheelView personWheel;
    public WheelView bonusWheel;
    private ImageView header;
    private ImageView footer;
    private WheelSpinner personWheelSpinner;
    private WheelSpinner bonusWheelSpinner;
    private WheelAnimation personWheelAnimation;
    private WheelAnimation bonusWheelAnimation;

    public MainWindow(List<Integer> days, ConfigurationModule configurationModule) {
        this.days = days;
        this.configurationModule = configurationModule;
        buildWindow();
    }

    private void buildWindow() {
        setupGUI();
        setupLogic();
        attachListeners();
        doLayout();
    }

    public void display() {
        frame.setVisible(true);
        loop.start();
    }

    private void setupGUI() {
        dateWheel = createDateWheel();
        personWheel = createPersonWheel();
        if (isBonusEnabled()) bonusWheel = createBonusWheel();
        frame = createMainFrame();
        menuBar = createMenuBar();
        frame.setJMenuBar(menuBar);
        header = createImageView(scale > 100 ? "top2x.jpg" : "top.jpg");
        footer = createImageView(scale > 100 ? "logoer2x.jpg" : "logoer.jpg");
    }

    private void setupLogic() {
        personWheelAnimation = new WheelAnimation(personWheel, MAX_VELOCITY);
        personWheelSpinner = new WheelSpinner(personWheelAnimation, MAX_VELOCITY);
        if (isBonusEnabled()) {
            bonusWheelAnimation = new WheelAnimation(bonusWheel, MAX_VELOCITY);
            bonusWheelSpinner = new WheelSpinner(bonusWheelAnimation, MAX_VELOCITY);
        }

        gameLogic = new GameLogic(days, configurationModule, this);

        loop = new AnimationLoop();
        if(isBonusEnabled()) {
            loop.setAnimations(personWheelAnimation, bonusWheelAnimation, gameLogic);
        } else {
            loop.setAnimations(personWheelAnimation, gameLogic);
        }

    }

    private void attachListeners() {
        frame.addWindowListener(this);

        frame.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.isAltDown() && !menuBar.isVisible()) {
                    menuBar.setVisible(true);
                } else if (e.isAltDown() && menuBar.isVisible()) {
                    menuBar.setVisible(false);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });

        personWheel.addMouseListener(personWheelSpinner);
        personWheel.addMouseMotionListener(personWheelSpinner);
        personWheelAnimation.addListener(gameLogic);
        personWheelSpinner.setTarget(gameLogic);

        if (isBonusEnabled()) {
            bonusWheel.addMouseListener(bonusWheelSpinner);
            bonusWheel.addMouseMotionListener(bonusWheelSpinner);
            bonusWheelAnimation.addListener(gameLogic);
            bonusWheelSpinner.setTarget(gameLogic);
        }
    }

    private void doLayout() {
        dateWheel.setPreferredSize(new Dimension(dim(180), dim(235)));
        if (isBonusEnabled()) {
            personWheel.setPreferredSize(new Dimension(dim(550), dim(235)));
            bonusWheel.setPreferredSize(new Dimension(dim(180), dim(235)));
        } else {
            personWheel.setPreferredSize(new Dimension(dim(550 + 180), dim(235)));
        }

        Container c = new Container();
        FlowLayout flowLayout = new FlowLayout();
        flowLayout.setAlignment(FlowLayout.CENTER);
        c.setLayout(flowLayout);
        c.add(dateWheel);
        c.add(personWheel);
        if (isBonusEnabled()) {
            c.add(bonusWheel);
        }

        frame.setLayout(new BorderLayout(10, 10));
        frame.add(c, BorderLayout.CENTER);
        frame.add(header, BorderLayout.NORTH);
        frame.add(footer, BorderLayout.SOUTH);

        frame.pack();
        frame.setSize(frame.getWidth(), frame.getHeight() + dim(50));
        center(frame);
    }

    private WheelView createDateWheel() {
        WheelView date = createWheelView();
        java.util.List<WheelView.Row> rows = createDateWheelRowList();
        date.setRows(rows);
        return date;
    }

    private WheelView createPersonWheel() {
        WheelView wheelView = createWheelView();
        java.util.List<WheelView.Row> rows = createPersonWheelRowList();
        wheelView.setRows(rows);
        wheelView.setFrameCounter(new FrameCounter("Person"));
        return wheelView;
    }

    private WheelView createBonusWheel() {
        WheelView wheelView = createWheelView();
        java.util.List<WheelView.Row> rows = createBonusWheelRowList();
        wheelView.setRows(rows);
        return wheelView;
    }

    private JFrame createMainFrame() {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setTitle(AppInfo.title());
        frame.getContentPane().setBackground(FRAME_BACKGROUND);

        return frame;
    }

    private JMenuBar createMenuBar() {
        final JMenuBar menuBar = new JMenuBar();
        JMenu file = new JMenu("File");

        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                System.exit(0);
            }
        });
        file.add(exitMenuItem);
        menuBar.add(file);
        menuBar.setVisible(false);
        return menuBar;
    }

    private ImageView createImageView(String file) {
        ImageView image = new ImageView(ImageFactory.createStaticImage(file));
        Dimension size = image.getPreferredSize();
        int imageReductionFactor = scale > 100 ? 2 : 1;
        image.setPreferredSize(new Dimension(dim((int) size.getWidth() / imageReductionFactor),
                dim((int) size.getHeight() / imageReductionFactor)));
        return image;
    }

    private WheelView createWheelView() {
        WheelView date = new WheelView();
        date.setVisibleRows(2);
        date.setBackground(BACKGROUND_COLOUR);
        return date;
    }

    private java.util.List<WheelView.Row> createDateWheelRowList() {
        java.util.List<WheelView.Row> rows = new ArrayList<WheelView.Row>();
        for (int i = 1; i <= 24; ++i) {
            rows.add(new WheelView.Row(i, createDateLabel(i)));
        }
        return rows;
    }

    private java.util.List<WheelView.Row> createPersonWheelRowList() {
        java.util.List<WheelView.Row> rows = new ArrayList<WheelView.Row>();
        no.jervell.view.awt.Image first = ImageFactory.createStaticImage("spinnmeg.jpg");
        first.setAnchor(Anchor.CENTER);
        rows.add(new WheelView.Row(null, first));
        // Adding of people is handled in the gameLogic
        return rows;
    }

    public ImageLabel createPersonWheelRow(Participant p) {
        no.jervell.view.awt.Image img = ImageFactory.createImage(p.image());
        no.jervell.view.awt.Label lbl = createPersonLabel(p.name());
        lbl.setAnchor(Anchor.LEFT_CENTER);
        return new ImageLabel(img, lbl);
    }

    private java.util.List<WheelView.Row> createBonusWheelRowList() {
        // Lookup custom images
        no.jervell.view.awt.Image bonus0 = ImageFactory.createImage("bonus0.jpg");
        no.jervell.view.awt.Image bonus1 = ImageFactory.createImage("bonus1.jpg");
        no.jervell.view.awt.Image bonus2 = ImageFactory.createImage("bonus2.jpg");

        java.util.List<WheelView.Row> rows = new ArrayList<WheelView.Row>();
        rows.add(new WheelView.Row(0,
                bonus0 != ImageFactory.BLANK ? bonus0 : ImageFactory.createStaticImage("lue.jpg")));
        rows.add(new WheelView.Row(1,
                bonus1 != ImageFactory.BLANK ? bonus1 : ImageFactory.createStaticImage("pakke.jpg")));
        rows.add(new WheelView.Row(2,
                bonus2 != ImageFactory.BLANK ? bonus2 : ImageFactory.createStaticImage("lue.jpg")));
        return rows;
    }

    private no.jervell.view.awt.Label createDateLabel(int date) {
        no.jervell.view.awt.Label label = createLabel(String.valueOf(date));
        label.setFont(BOLD_FONT);
        return label;
    }

    private no.jervell.view.awt.Label createPersonLabel(String name) {
        no.jervell.view.awt.Label label = createLabel(name.toUpperCase());
        label.setScaling(Scaling.STRETCH);
        return label;
    }

    private no.jervell.view.awt.Label createLabel(String text) {
        no.jervell.view.awt.Label label = new no.jervell.view.awt.Label(text);
        label.setPaint(TEXT_COLOUR);
        label.setAnchor(Anchor.CENTER);
        label.setFont(PLAIN_FONT);
        return label;
    }

    private void center(JFrame frame) {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screen = toolkit.getScreenSize();
        frame.setLocation((int) (screen.getWidth() - frame.getWidth()) / 2,
                (int) (screen.getHeight() - frame.getHeight()) / 2);
    }

    private int dim(int v) {
        return (v * scale) / 100;
    }

    private boolean isBonusEnabled() {
        return configurationModule.config().bonus();
    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {
        loop.end();
        frame.dispose();
    }

    @Override
    public void windowClosed(WindowEvent e) {

    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }
}
