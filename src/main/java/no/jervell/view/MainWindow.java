package no.jervell.view;

import com.github.julekarenalender.Participant;
import com.github.julekarenalender.config.AppInfo;
import com.github.julekarenalender.config.ConfigurationModule;
import com.github.julekarenalender.log.Logger$;
import com.github.julekarenalender.view.util.Images;
import no.jervell.jul.GameLogic;
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
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
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

    private static final Logger$ logger = Logger$.MODULE$;

    /**
     * User interface size scaler, 100=native, 200=double size, 50=half size,
     * etc
     */
    //TODO: Make enum?
    final static int scale = 125;
    private AnimationLoop loop;
    private JFrame frame;
    private JMenuBar menuBar;
    private JDialog showParticipants;
    private Map<Integer, JTextField> participantTextFields;
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
        this.participantTextFields = new HashMap<Integer, JTextField>();
        buildWindow();
    }

    private void buildWindow() {
        setupLookAndFeel();
        setupGUI();
        setupLogic();
        attachListeners();
        doLayout();
    }

    public void display() {
        frame.setVisible(true);
        loop.start();
    }

    private void setupLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            logger.error("Unable to start look and feel", e);
        } catch (InstantiationException e) {
            logger.error("Unable to start look and feel", e);
        } catch (IllegalAccessException e) {
            logger.error("Unable to start look and feel", e);
        } catch (UnsupportedLookAndFeelException e) {
            logger.error("Unable to start look and feel", e);
        }
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
        if (isBonusEnabled()) {
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
                if (e.getKeyCode() == KeyEvent.VK_ALT && !menuBar.isVisible()) {
                    menuBar.setVisible(true);
                } else if (e.getKeyCode() == KeyEvent.VK_ALT && menuBar.isVisible()) {
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
        List<WheelView.Row> rows = createDateWheelRowList();
        date.setRows(rows);
        return date;
    }

    private WheelView createPersonWheel() {
        WheelView wheelView = createWheelView();
        List<WheelView.Row> rows = createPersonWheelRowList();
        wheelView.setRows(rows);
        wheelView.setFrameCounter(new FrameCounter("Person"));
        return wheelView;
    }

    private WheelView createBonusWheel() {
        WheelView wheelView = createWheelView();
        List<WheelView.Row> rows;
        if (getBonusFiles().size() > 0) {
            rows = createCustomBonusWheel();
        } else {
            rows = createDefaultBonusWheel();
        }

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
        exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, KeyEvent.CTRL_MASK));
        exitMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                System.exit(0);
            }
        });
        file.add(exitMenuItem);

        JMenu participants = new JMenu("Participants");
        JMenuItem listParticipants = new JMenuItem("View/Edit");
        listParticipants.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showParticipants = createShowParticipantsDialog();
                showParticipants.setVisible(true);
            }
        });
        JMenuItem redrawParticipant = new JMenuItem("Redraw");
        exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_MASK));
        redrawParticipant.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        participants.add(listParticipants);
        participants.add(redrawParticipant);

        menuBar.add(file);
        menuBar.add(participants);
        menuBar.setVisible(false);
        return menuBar;
    }

    private JDialog createShowParticipantsDialog() {
        JDialog jd = new JDialog(SwingUtilities.windowForComponent(frame), "View/Edit Participants", JDialog.ModalityType.APPLICATION_MODAL);
        jd.setLocationRelativeTo(frame);
        jd.setLayout(new BorderLayout(10, 10));
        jd.add(participantPane(), BorderLayout.NORTH);
        jd.add(participantSaveClosePane(), BorderLayout.SOUTH);
        jd.pack();
        return jd;
    }

    private JPanel participantPane() {
        JPanel pane = new JPanel();
        pane.setBorder(new EmptyBorder(15, 15, 15, 15));
        participantTextFields.clear();
        Collection<Participant> participants = configurationModule.getParticipantsJava();
        GridLayout layout = new GridLayout(participants.size() + 1, 3);
        layout.setHgap(10);
        layout.setVgap(2);
        pane.setLayout(layout);
        int row = 0;
        for (Participant p : participants) {
            pane.add(new JLabel(String.valueOf(p.id())), row, 0);
            pane.add(new JLabel(p.name()), row, 1);
            JTextField daysWonField = new JTextField(String.valueOf(p.daysWon()));
            participantTextFields.put((Integer) p.id().get(), daysWonField);
            pane.add(daysWonField, row, 2);
            row++;
        }
        pane.add(new JLabel("Id"), row, 0);
        pane.add(new JLabel("Name"), row, 1);
        pane.add(new JLabel("dayWon"), row, 2);
        return pane;
    }

    private JPanel participantSaveClosePane() {
        JPanel pane = new JPanel();
        pane.setLayout(new FlowLayout());
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveParticipants();
            }
        });
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showParticipants.setVisible(false);
            }
        });
        pane.add(saveButton);
        pane.add(closeButton);
        return pane;
    }

    private void saveParticipants() {
        Collection<Participant> pTmp = configurationModule.getParticipantsJava();
        List<Participant> participants = new ArrayList<Participant>(pTmp);
        for (Participant p : participants) {
            JTextField field = participantTextFields.get(p.id().get());
            String text = field.getText();
            if (isNumeric(text)) {
                p.daysWon_$eq(Integer.parseInt(text));
            }
        }
        configurationModule.syncParticipantsJava(participants);
    }

    private boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");
    }

    private ImageView createImageView(String file) {
        ImageView image = new ImageView(Images.apply().staticImg(file));
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

    private List<WheelView.Row> createDateWheelRowList() {
        List<WheelView.Row> rows = new ArrayList<WheelView.Row>();
        for (int i = 1; i <= 24; ++i) {
            rows.add(new WheelView.Row(i, createDateLabel(i)));
        }
        return rows;
    }

    private List<WheelView.Row> createPersonWheelRowList() {
        List<WheelView.Row> rows = new ArrayList<WheelView.Row>();
        no.jervell.view.awt.Image first = Images.apply().staticImg("spinnmeg.jpg");
        first.setAnchor(Anchor.CENTER);
        rows.add(new WheelView.Row(null, first));
        // Adding of people is handled in the gameLogic
        return rows;
    }

    public ImageLabel createPersonWheelRow(Participant p) {
        no.jervell.view.awt.Image img = Images.apply().localImg(p.image());
        no.jervell.view.awt.Label lbl = createPersonLabel(p.name());
        lbl.setAnchor(Anchor.LEFT_CENTER);
        return new ImageLabel(img, lbl);
    }

    private List<WheelView.Row> createDefaultBonusWheel() {
        List<WheelView.Row> rows = new ArrayList<WheelView.Row>();
        rows.add(new WheelView.Row(0, Images.apply().staticImg("lue.jpg")));
        rows.add(new WheelView.Row(1, Images.apply().staticImg("pakke.jpg")));
        rows.add(new WheelView.Row(2, Images.apply().staticImg("lue.jpg")));
        return rows;
    }

    private List<WheelView.Row> createCustomBonusWheel() {
        List<WheelView.Row> rows = new ArrayList<WheelView.Row>();
        int bonusIndex = 0;
        for (File bonusFile : getBonusFiles()) {
            rows.add(new WheelView.Row(bonusIndex, Images.apply().image(bonusFile)));
            bonusIndex++;
        }
        return rows;
    }

    private List<File> getBonusFiles() {
        List<File> bonusImages = new ArrayList<File>();
        try {
            File[] imageFiles = new File(".", "images").listFiles();
            for (int i = 0; i < imageFiles.length; i++) {
                if (imageFiles[i].getName().contains("bonus")) {
                    bonusImages.add(imageFiles[i]);
                }
            }
            return bonusImages;
        } catch (Exception ex) {
            logger.error("Unable to open ./images folder: " + ex.getMessage());
            return bonusImages;
        }
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
