package no.jervell.jul;

//import com.github.julekarenalender.config.ConfigurationModule;

import com.github.julekarenalender.domain.Game;
import com.github.julekarenalender.domain.GameParameters;
import com.github.julekarenalender.domain.ParticipantData;
import no.jervell.view.MainWindow;
import no.jervell.view.animation.Animation;
import no.jervell.view.animation.impl.DefaultTimer;
import no.jervell.view.animation.impl.WheelAnimation;
import no.jervell.view.animation.impl.WheelRowAnimator;
import no.jervell.view.animation.impl.WheelSpinner;
import no.jervell.view.swing.WheelView;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.github.julekarenalender.JulekarenalenderKt.currentGame;
import static com.github.julekarenalender.JulekarenalenderKt.logger;

/**
 * INIT:
 * x set first date
 * <p/>
 * LOOP:
 * x set person wheel target index
 * x set bonus wheel target index (bonus if next date is same as current, no bonus otherwise)
 * x enable person wheel
 * <p/>
 * WAIT_FOR_PERSON:
 * x ...wait for person wheel to stop
 * <p/>
 * WINNER:
 * x init text animation
 * x enable bonus wheel
 * - ...wait for person wheel OR bonus wheel to start
 * x if person wheel spinning, discard winner and select a different one
 * x if person wheel spinning, disable bonus wheel
 * x if bonus wheel spinning, disable person wheel
 * x ...wait for bonus wheel OR person wheel to start
 * x stop text animation
 * x if person wheel started, go to WAIT_FOR_PERSON
 * <p/>
 * WAIT_FOR_BONUS:
 * x ...wait for bonus wheel to stop
 * x disable bonus wheel
 * x if new date: repeat to LOOP
 * <p/>
 * FINISHED:
 *
 * @author Arne C. Jervell (arne@jervell.no)
 */
public class GameLogic implements Animation, WheelAnimation.Listener, WheelSpinner.Target {
    //    private static final klogger$ klogger = klogger$.MODULE$;
//    private ConfigurationModule configurationModule;
    private GameParameters gameParameters;
    private Script script;
    private State state;
    private SecureRandom rnd = new SecureRandom();
    private WheelView date;
    private WheelView person;
    private WheelView bonus;
    private WheelRowAnimator blink;
    public GameLogic(List<Integer> days, Game game, MainWindow mainWindow) {
//        this.configurationModule = configurationModule;
        this.gameParameters = game.getGameParameters();
        this.date = mainWindow.getDateWheel();
        this.person = mainWindow.getPersonWheel();
        this.bonus = mainWindow.getBonusWheel();
        this.blink = new WheelRowAnimator(person);
        this.script = new Script(days);
        populatePerson(mainWindow);
        setState(State.INIT);
    }

    public void redrawLast() {
        if (isBonusEnabled()) bonus.setEnabled(false);
        blink.stop();
        script.resetLastWinner();
        setState(State.LOOP);
    }

    private void populatePerson(MainWindow mainWindow) {
        List<WheelView.Row> rows = new ArrayList<WheelView.Row>(person.getRows());
        for (ParticipantData p : script.getParticipantList()) {
            rows.add(new WheelView.Row(p, mainWindow.createPersonWheelRow(p)));
        }
        person.setRows(rows);
    }

    public void init(DefaultTimer timer) {
        date.setEnabled(false);
        person.setEnabled(false);
        if (isBonusEnabled()) bonus.setEnabled(false);
        if (script.hasCurrent()) {
            date.setYOffset(script.getDay());
        }
        setState(State.LOOP);
    }

    public void move(DefaultTimer timer) {
        rotateDateWheel();
        switch (state) {
            case LOOP:
                if (script.hasCurrent()) {
                    person.setEnabled(true);
                    setState(State.WAIT_FOR_PERSON);
                } else {
                    setState(State.FINISHED);
                }
                break;

            case WINNER:
                blink.move(timer);
                break;
            case FINISHED:
                blink.move(timer);
                break;
        }
    }

    private void rotateDateWheel() {
        if (script.hasCurrent()) {
            double dateY = date.getYOffset();
            double diff = script.getDay() - dateY;
            double absDiff = Math.abs(diff);
            if (absDiff > .001) {
                double move = Math.min(.02, absDiff);
                date.setYOffset(dateY + move * Math.signum(diff));
                date.repaint();
            }
        }
    }

    public Integer getTargetIndex(WheelView view) {
        switch (state) {
            case WAIT_FOR_PERSON:
                if (view == person) {
                    return script.getParticipant();
                }
                break;

            case WINNER:
                if (view == person) {
                    return script.replaceParticipant();
                } else if (view == bonus && isBonusEnabled()) {
                    return script.randomBonusIndex();
                }
                break;
        }
        return null;
    }

    public void spinStarted(WheelView view, double velocity) {
        logger.debug("--- spin started");
        switch (state) {
            case WINNER:
                blink.stop();
                if (view == person) {
                    if (isBonusEnabled()) bonus.setEnabled(false);
                    setState(State.WAIT_FOR_PERSON);
                } else if (view == bonus && isBonusEnabled()) {
                    person.setEnabled(false);
                    setState(State.WAIT_FOR_BONUS);
                }
                break;
        }
    }

    public void spinStopped(WheelView view) {
        logger.debug("--- spin stopped");
        switch (state) {
            case WAIT_FOR_PERSON:
                blink.start(script.getParticipant());
                person.setEnabled(false);
                if (isBonusEnabled()) {
                    bonus.setEnabled(true);
                } else {
                    person.setEnabled(true);
                }
                setState(State.WINNER);
                if (!isBonusEnabled()) rotateToNext();
                break;

            case WAIT_FOR_BONUS:
                if (isBonusEnabled()) {
                    bonus.setEnabled(false);
                    rotateToNext();
                }
                break;
        }
    }

    private void rotateToNext() {
        if (script.move()) {
            setState(State.LOOP);
        } else {
            setState(State.FINISHED);
            blink.start(script.getLastWinner());
            person.setEnabled(false);
        }
    }

    private void setState(State state) {
        logger.debug(">>> " + state);
        if (state == State.FINISHED ||
                state == State.WINNER) {
            try {
                currentGame.getParticipants().forEach(ParticipantData::save);
            } catch (Exception e) {
                logger.error("Unable to persist data.", e);
            }
        }
        this.state = state;
    }

    private boolean isBonusEnabled() {
        return gameParameters.getBonus();
    }

    private enum State {INIT, LOOP, WAIT_FOR_PERSON, WINNER, WAIT_FOR_BONUS, FINISHED}

    private class Script {
        private int pos;
        private ParticipantData lastWinner;
        private List<Integer> days;
        private ParticipantData[] winners;
        private List<ParticipantData> queue;
        private List<ParticipantData> participants;

        public Script(List<Integer> days) {
            this.days = days;
            this.winners = new ParticipantData[days.size()];
            this.pos = 0;
            init();
        }

        private void init() {
            selectWinners();
        }

        public void resetLastWinner() {
            if (pos == 0) return;

            pos--;
            winners[pos] = pickWinner(lastWinner.getDateWon(), queue);
            for (ParticipantData p : participants) {
                if (p.equals(lastWinner)) {
                    p.setDateWon(0);
                }
            }
        }

        public void selectWinners() {
            participants = getFilteredList(currentGame.getParticipants(), getFirstDay());
            queue = new ArrayList<ParticipantData>(participants);
            for (int i = 0; i < days.size(); ++i) {
                winners[i] = pickWinner(days.get(i), queue);
            }
        }

        public List<ParticipantData> getParticipantList() {
            return participants;
        }

        private List<ParticipantData> getFilteredList(Collection<ParticipantData> list, int minDay) {
            List<ParticipantData> result = new ArrayList<ParticipantData>();
            for (ParticipantData p : list) {
                if (p.getDateWon() == 0 || p.getDateWon() >= minDay) {
                    result.add(p);
                }
            }
            return result;
        }

        private int getFirstDay() {
            if (days == null || days.size() == 0) {
                return 0;
            }
            int min = days.get(0);
            for (int val : days) {
                min = Math.min(min, val);
            }
            return min;
        }

        private ParticipantData pickWinner(int day, List<ParticipantData> queue) {
            ParticipantData winner = extractRiggedWinner(day, queue);
            if (winner == null) {
                winner = extractRandomWinner(day, queue);
            }
            if (winner == null) {
                winner = new ParticipantData("null", "NOBODY", null, day);
            }
            return winner;
        }

        private ParticipantData extractRiggedWinner(int day, List<ParticipantData> queue) {
            for (ParticipantData p : queue) {
                if (p.getDateWon() == day) {
                    queue.remove(p);
                    return p;
                }
            }
            return null;
        }

        private ParticipantData extractRandomWinner(int day, List<ParticipantData> queue) {
            if (queue.size() > 0) {
                ParticipantData p = queue.remove(rnd.nextInt(queue.size()));
                p.setDateWon(day);
                return p;
            }
            return null;
        }

        public int getDay() {
            return date.getIndex(winners[pos].getDateWon());
        }

        public int getLastWinner() {
            return person.getIndex(lastWinner);
        }

        public int getParticipant() {
            return person.getIndex(winners[pos]);
        }

        public int replaceParticipant() {
            ParticipantData oldParticipant = winners[pos];
            winners[pos] = extractRandomWinner(oldParticipant.getDateWon(), queue);
            oldParticipant.setDateWon(0);
            return getParticipant();
        }

        public int randomBonusIndex() {
            return bonus.getIndex(rnd.nextInt(bonus.getRowCount()));
        }

        public boolean hasCurrent() {
            return pos < days.size();
        }

        public boolean move() {
            if (hasCurrent()) {
                lastWinner = winners[pos];
                pos++;
            }
            return hasCurrent();
        }
    }
}
