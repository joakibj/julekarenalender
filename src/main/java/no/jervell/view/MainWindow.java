package no.jervell.view;

import com.github.julekarenalender.App;
import com.github.julekarenalender.domain.ParticipantData;
import com.github.julekarenalender.gui.Images;
import com.github.julekarenalender.gui.LabelMaker;
import no.jervell.jul.GameLogic;
import no.jervell.view.animation.impl.AnimationLoop;
import no.jervell.view.animation.impl.FrameCounter;
import no.jervell.view.animation.impl.WheelAnimation;
import no.jervell.view.animation.impl.WheelSpinner;
import no.jervell.view.awt.Anchor;
import no.jervell.view.awt.ImageLabel;
import no.jervell.view.swing.ImageView;
import no.jervell.view.swing.WheelView;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.*;

import static com.github.julekarenalender.JulekarenalenderKt.currentGame;
import static com.github.julekarenalender.JulekarenalenderKt.logger;
import static com.github.julekarenalender.game.GameKt.listParticipants;

public class MainWindow extends JFrame {
    private static final double MAX_VELOCITY = 100;
    private static final Color FRAME_BACKGROUND = Color.black;
    private static final Paint BACKGROUND_COLOUR = Color.white;

    /**
     * User interface size scaler, 100=native, 200=double size, 50=half size,
     * etc
     */
    //TODO: Make enum?
    final static int scale = 125;
    private final MainWindow self = this;
    private AnimationLoop loop;
    private JMenuBar menuBar;
    private JDialog showParticipants;
    private Map<String, JTextField> participantTextFields;
    private JLabel participantFeedback;
    private List<Integer> days;
    private GameLogic gameLogic;

    private WheelView dateWheel;
    private WheelView personWheel;
    private WheelView bonusWheel;

    private ImageView header;
    private ImageView footer;
    private WheelSpinner personWheelSpinner;
    private WheelSpinner bonusWheelSpinner;
    private WheelAnimation personWheelAnimation;
    private WheelAnimation bonusWheelAnimation;

    private LabelMaker labelMaker = new LabelMaker();
    private Images images = new Images();

    public MainWindow() {
        this.days = currentGame.getGameParameters().getDays();
        this.participantTextFields = new HashMap<>();

        buildWindow();
    }

    private void buildWindow() {
        setupFrame();
        setupGUI();
        setupLogic();
        attachListeners();
        setupLayout();
    }

    public void display() {
        setVisible(true);
        loop.start();
    }

    private void setupFrame() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle(App.title);
        getContentPane().setBackground(FRAME_BACKGROUND);
    }

    private void setupGUI() {
        dateWheel = createDateWheel();
        personWheel = createPersonWheel();
        if (isBonusEnabled()) bonusWheel = createBonusWheel();
        menuBar = createMenuBar();
        setJMenuBar(menuBar);
        header = createImageView(scale > 100 ? "headerx2.jpg" : "header.jpg");
        footer = createImageView(scale > 100 ? "footerx2.jpg" : "footer.jpg");
    }

    private void setupLogic() {
        personWheelAnimation = new WheelAnimation(personWheel, MAX_VELOCITY);
        personWheelSpinner = new WheelSpinner(personWheelAnimation, MAX_VELOCITY);
        if (isBonusEnabled()) {
            bonusWheelAnimation = new WheelAnimation(bonusWheel, MAX_VELOCITY);
            bonusWheelSpinner = new WheelSpinner(bonusWheelAnimation, MAX_VELOCITY);
        }

        gameLogic = new GameLogic(days, currentGame, this);

        loop = new AnimationLoop();
        if (isBonusEnabled()) {
            loop.setAnimations(personWheelAnimation, bonusWheelAnimation, gameLogic);
        } else {
            loop.setAnimations(personWheelAnimation, gameLogic);
        }

    }

    private void attachListeners() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                loop.end();
                dispose();
                if (logger.isDebug()) {
                    logger.debug("Current game status:");
                    listParticipants();
                }
            }
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ALT && !menuBar.isVisible()) {
                    menuBar.setVisible(true);
                } else if (e.getKeyCode() == KeyEvent.VK_ALT && menuBar.isVisible()) {
                    menuBar.setVisible(false);
                }
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

    private void setupLayout() {
        dateWheel.setPreferredSize(new Dimension(dim(180), dim(235)));
        if (isBonusEnabled()) {
            personWheel.setPreferredSize(new Dimension(dim(550), dim(235)));
            bonusWheel.setPreferredSize(new Dimension(dim(180), dim(235)));
        } else {
            personWheel.setPreferredSize(new Dimension(dim(550 + 180), dim(235)));
        }

        Container wheels = new Container();
        FlowLayout flowLayout = new FlowLayout();
        flowLayout.setAlignment(FlowLayout.CENTER);
        wheels.setLayout(flowLayout);
        wheels.add(dateWheel);
        wheels.add(personWheel);
        if (isBonusEnabled()) {
            wheels.add(bonusWheel);
        }

        setLayout(new BorderLayout(10, 10));
        add(wheels, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);
        add(footer, BorderLayout.SOUTH);

        pack();
        setSize(getWidth(), getHeight() + dim(50));
        center();
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
        if (images.getBonusImages().size() > 0) {
            rows = createCustomBonusWheel();
        } else {
            rows = createDefaultBonusWheel();
        }

        wheelView.setRows(rows);
        return wheelView;
    }

    private JMenuBar createMenuBar() {
        final JMenuBar menuBar = new JMenuBar();
        JMenu file = new JMenu("File");
        JMenu participants = new JMenu("Participants");

        JMenuItem exit = new JMenuItem("Exit");
        JMenuItem view = new JMenuItem("View/Edit");
        JMenuItem redraw = new JMenuItem("Redraw");

        exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, KeyEvent.CTRL_MASK));
        view.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, KeyEvent.CTRL_MASK));
        redraw.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_MASK));


        exit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                System.exit(0);
            }
        });
        view.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showParticipants = createShowParticipantsDialog();
                showParticipants.setVisible(true);
            }
        });
        redraw.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int answer = JOptionPane.showConfirmDialog(self, "Are you sure you want to redraw?", "Redraw", JOptionPane.YES_NO_OPTION);
                if (answer == JOptionPane.YES_OPTION) {
                    gameLogic.redrawLast();
                }
            }
        });

        file.add(exit);
        participants.add(view);
        participants.add(redraw);

        menuBar.add(file);
        menuBar.add(participants);
        menuBar.setVisible(false);
        return menuBar;
    }

    private JDialog createShowParticipantsDialog() {
        JDialog dialog = new JDialog(SwingUtilities.windowForComponent(this), "View/Edit Participants", JDialog.ModalityType.APPLICATION_MODAL);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 0;
        dialog.add(participantScrollPane(), constraints);

        constraints.fill = GridBagConstraints.VERTICAL;
        constraints.gridx = 0;
        constraints.gridy = 1;
        participantFeedback = new JLabel("Editing...");
        dialog.add(participantFeedback, constraints);

        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = 2;
        dialog.add(participantSaveClosePane(), constraints);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(true);
        return dialog;
    }

    private JScrollPane participantScrollPane() {
        JPanel pane = participantPane();
        JScrollPane scrollPane = new JScrollPane(pane, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(new EmptyBorder(15, 15, 15, 15));
        scrollPane.setPreferredSize(new Dimension(500, 250));
        return scrollPane;
    }

    private JPanel participantPane() {
        JPanel pane = new JPanel();
        participantTextFields.clear();
        Collection<ParticipantData> participants = currentGame.getParticipants();
        GridLayout layout = new GridLayout(participants.size() + 1, 3);
//        layout.setHgap(10);
//        layout.setVgap(5);
        pane.setLayout(layout);
        int row = 0;
        for (ParticipantData p : participants) {
            pane.add(new JLabel(p.getUuid()), row, 0);
            pane.add(new JLabel(p.getName()), row, 1);
            JTextField daysWonField = new JTextField(String.valueOf(p.getDateWon()), 2);
            participantTextFields.put(p.getUuid(), daysWonField);
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
        JButton closeButton = new JButton("Close");

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean saved = saveParticipants();
                if (saved) {
                    participantFeedback.setText("Changes saved!");
                }
            }
        });
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

    private boolean saveParticipants() {
        Collection<ParticipantData> pTmp = currentGame.getParticipants();
        List<ParticipantData> participants = new ArrayList<ParticipantData>(pTmp);
        for (ParticipantData p : participants) {
            JTextField field = participantTextFields.get(p.getUuid());
            String text = field.getText();
            if (isNumeric(text)) {
                p.setDateWon(Integer.parseInt(text));
            }
        }
        return true;
    }

    private boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");
    }

    private ImageView createImageView(String file) {
        ImageView image = new ImageView(images.getStaticImg(file));
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
            rows.add(new WheelView.Row(i, labelMaker.createDateLabel(i)));
        }
        return rows;
    }

    private List<WheelView.Row> createPersonWheelRowList() {
        List<WheelView.Row> rows = new ArrayList<WheelView.Row>();
        no.jervell.view.awt.Image first = images.getStaticImg("spinnmeg.jpg");
        first.setAnchor(Anchor.CENTER);
        rows.add(new WheelView.Row(null, first));
        // Adding of people is handled in the gameLogic
        return rows;
    }

    public ImageLabel createPersonWheelRow(ParticipantData p) {
        no.jervell.view.awt.Image img = images.getLocalImg(p.getImage());
        no.jervell.view.awt.Label lbl = labelMaker.createPersonLabel(p.getName());
        lbl.setAnchor(Anchor.LEFT_CENTER);
        return new ImageLabel(img, lbl);
    }

    private List<WheelView.Row> createDefaultBonusWheel() {
        List<WheelView.Row> rows = new ArrayList<WheelView.Row>();
        rows.add(new WheelView.Row(0, images.getStaticImg("lue.jpg")));
        rows.add(new WheelView.Row(1, images.getStaticImg("pakke.jpg")));
        rows.add(new WheelView.Row(2, images.getStaticImg("lue.jpg")));
        return rows;
    }

    private List<WheelView.Row> createCustomBonusWheel() {
        List<WheelView.Row> rows = new ArrayList<WheelView.Row>();
        int bonusIndex = 0;
        for (no.jervell.view.awt.Image image : images.getBonusImages()) {
            rows.add(new WheelView.Row(bonusIndex, image));
            bonusIndex++;
        }
        return rows;
    }

    private void center() {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screen = toolkit.getScreenSize();
        setLocation((int) (screen.getWidth() - getWidth()) / 2,
                (int) (screen.getHeight() - getHeight()) / 2);
    }

    private int dim(int v) {
        return (v * scale) / 100;
    }

    private boolean isBonusEnabled() {
        return currentGame.getGameParameters().getBonus();
    }


    public WheelView getDateWheel() {
        return dateWheel;
    }

    public WheelView getPersonWheel() {
        return personWheel;
    }

    public WheelView getBonusWheel() {
        return bonusWheel;
    }
}
