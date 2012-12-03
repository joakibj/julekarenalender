package no.jervell.jul;

import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import javax.swing.JFrame;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import no.jervell.animation.AnimationLoop;
import no.jervell.animation.FrameCounter;
import no.jervell.awt.Image;
import no.jervell.awt.Label;
import no.jervell.awt.*;
import no.jervell.file.CSVFile;
import no.jervell.swing.ImageView;
import no.jervell.swing.WheelView;
import no.jervell.util.ImageFactory;
import no.jervell.util.SimpleLogger;

/**
 * @author Arne C. Jervell (arne@jervell.no)
 */
public class Julekarenalender implements WindowListener {

    // JCommander parameters
    @Parameter(description = "[Liste av tall med hvilke dager det skal foregå trekning]")
    private List<String> days = new ArrayList<String>();

    @Parameter(names = "-debug", description = "Start program i debug modus")
    private boolean debug = false;

    @Parameter(names = "-help", help = true, description = "Denne beskjeden")
    private boolean help;

    public static final String PROGRAM_NAME = "Julekarenalender";
    /**
     * User interface size scaler, 100=native, 200=double size, 50=half size,
     * etc
     */
    final static int scale = 125;
    PersonDAO personDAO;
    WheelView dateWheel;
    WheelView personWheel;
    WheelView bonusWheel;
    private ImageView header;
    private ImageView footer;
    private WheelSpinner personWheelSpinner;
    private WheelSpinner bonusWheelSpinner;
    private WheelAnimation personWheelAnimation;
    private WheelAnimation bonusWheelAnimation;
    private AnimationLoop loop;
    private JFrame frame;
    private Font plainFont = new Font("Times New Roman", Font.PLAIN, 50);
    private Font boldFont = new Font("Times New Roman", Font.BOLD, 50);
    private double maxVelocity = 100;
    private Color frameBackground = Color.black;
    private Paint backgroundPaint = Color.white;
    private Color textPaint = Color.black;
    private GameLogic gameLogic;

    public static void main(String[] args) {
        try {
            Julekarenalender julekalender = new Julekarenalender();
            julekalender.init(args);
            julekalender.build();
            julekalender.start();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private void init(String[] args) {
        JCommander jCommander = new JCommander(this, args);
        jCommander.setProgramName(PROGRAM_NAME);
        if(help) {
            jCommander.usage();
            System.exit(0);
        }
        SimpleLogger.getInstance().setDebug(debug);
        SimpleLogger.getInstance().setInfo(true);
    }

    private void build() throws IOException {
        setupDataSource();
        setupGUI();
        setupLogic();
        attachListeners();
        doLayout();
    }

    private void start() {
        frame.setVisible(true);
        loop.start();
    }

    private void setupDataSource() throws IOException {
        File resourceFile = new File(".", "julekarenalender.csv");
        System.out.println(resourceFile);
        CSVFile dataSource = new CSVFile(resourceFile, true);
        personDAO = new PersonDAO(dataSource);
    }

    private void setupGUI() {
        dateWheel = createDateWheel();
        personWheel = createPersonWheel();
        bonusWheel = createBonusWheel();
        frame = createMainFrame();
        header = createImageView(scale > 100 ? "top2x.jpg" : "top.jpg");
        footer = createImageView(scale > 100 ? "logoer2x.jpg" : "logoer.jpg");
    }

    private void setupLogic() {
        int[] days = parseDays();

        personWheelAnimation = new WheelAnimation(personWheel, maxVelocity);
        bonusWheelAnimation = new WheelAnimation(bonusWheel, maxVelocity);
        loop = new AnimationLoop();
        personWheelSpinner = new WheelSpinner(personWheelAnimation, maxVelocity);
        bonusWheelSpinner = new WheelSpinner(bonusWheelAnimation, maxVelocity);
        gameLogic = new GameLogic(days, this);

        loop.setAnimations(personWheelAnimation, bonusWheelAnimation, gameLogic);
    }

    private void attachListeners() {
        frame.addWindowListener(this);

        personWheel.addMouseListener(personWheelSpinner);
        personWheel.addMouseMotionListener(personWheelSpinner);

        bonusWheel.addMouseListener(bonusWheelSpinner);
        bonusWheel.addMouseMotionListener(bonusWheelSpinner);

        personWheelAnimation.addListener(gameLogic);
        bonusWheelAnimation.addListener(gameLogic);

        personWheelSpinner.setTarget(gameLogic);
        bonusWheelSpinner.setTarget(gameLogic);
    }

    private void doLayout() {
        dateWheel.setPreferredSize(new Dimension(dim(180), dim(235)));
        personWheel.setPreferredSize(new Dimension(dim(550), dim(235)));
        bonusWheel.setPreferredSize(new Dimension(dim(180), dim(235)));

        Container c = new Container();
        FlowLayout flowLayout = new FlowLayout();
        flowLayout.setAlignment(FlowLayout.CENTER);
        c.setLayout(flowLayout);
        c.add(dateWheel);
        c.add(personWheel);
        c.add(bonusWheel);

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
        List<WheelView.Row> rows = createBonusWheelRowList();
        wheelView.setRows(rows);
        return wheelView;
    }

    private JFrame createMainFrame() {
        JFrame frame = new JFrame();
        frame.setTitle(PROGRAM_NAME);
        frame.getContentPane().setBackground(frameBackground);
        return frame;
    }

    private ImageView createImageView(String file) {
        ImageView image = new ImageView(ImageFactory.createStaticImage(file));
        Dimension size = image.getPreferredSize();
        int imageReductionFactor = scale > 100 ? 2 : 1;
        image.setPreferredSize(new Dimension(dim((int) size.getWidth() / imageReductionFactor), dim((int) size.getHeight() / imageReductionFactor)));
        return image;
    }

    private WheelView createWheelView() {
        WheelView date = new WheelView();
        date.setVisibleRows(2);
        date.setBackground(backgroundPaint);
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
        Image first = ImageFactory.createStaticImage("spinnmeg.jpg");
        first.setAnchor(Anchor.CENTER);
        rows.add(new WheelView.Row(null, first));
        // Adding of people is handled in the gameLogic
        return rows;
    }

    ImageLabel createPersonWheelRow(Person p) {
        Image img = ImageFactory.createImage(p.getPicture());
        Label lbl = createPersonLabel(p.getName());
        lbl.setAnchor(Anchor.LEFT_CENTER);
        return new ImageLabel(img, lbl);
    }

    private List<WheelView.Row> createBonusWheelRowList() {
        // Lookup custom images
        Image bonus0 = ImageFactory.createImage("bonus0.jpg");
        Image bonus1 = ImageFactory.createImage("bonus1.jpg");
        Image bonus_1 = ImageFactory.createImage("bonus_1.jpg");

        List<WheelView.Row> rows = new ArrayList<WheelView.Row>();
        rows.add(new WheelView.Row(0, bonus0 != ImageFactory.BLANK ? bonus0 : ImageFactory.createStaticImage("lue.jpg")));
        rows.add(new WheelView.Row(1, bonus1 != ImageFactory.BLANK ? bonus1 : ImageFactory.createStaticImage("pakke.jpg")));
        rows.add(new WheelView.Row(2, bonus_1 != ImageFactory.BLANK ? bonus_1 : ImageFactory.createStaticImage("joakim_lystad.jpg")));
        return rows;
    }

    private Label createDateLabel(int date) {
        Label label = createLabel(String.valueOf(date));
        label.setFont(boldFont);
        return label;
    }

    private Label createPersonLabel(String name) {
        Label label = createLabel(name.toUpperCase());
        label.setScaling(Scaling.STRETCH);
        return label;
    }

    private Label createLabel(String text) {
        Label label = new Label(text);
        label.setPaint(textPaint);
        label.setAnchor(Anchor.CENTER);
        label.setFont(plainFont);
        return label;
    }

    private int[] parseDays() {
        if (days.size() == 0) {
            return new int[]{Calendar.getInstance().get(Calendar.DAY_OF_MONTH)};
        } else {
            int i = 0;
            int[] result = new int[days.size()];
            for(String day : days) {
                result[i] = Integer.parseInt(day);
                i++;
            }
            Arrays.sort(result);
            return result;
        }
    }

    private void center(JFrame frame) {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screen = toolkit.getScreenSize();
        frame.setLocation((int) (screen.getWidth() - frame.getWidth()) / 2, (int) (screen.getHeight() - frame.getHeight()) / 2);
    }

    private int dim(int v) {
        return (v * scale) / 100;
    }

    public void windowOpened(WindowEvent e) {
    }

    public void windowClosing(WindowEvent e) {
        loop.end();
        frame.dispose();
    }

    public void windowClosed(WindowEvent e) {
    }

    public void windowIconified(WindowEvent e) {
    }

    public void windowDeiconified(WindowEvent e) {
    }

    public void windowActivated(WindowEvent e) {
    }

    public void windowDeactivated(WindowEvent e) {
    }
}
