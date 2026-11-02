
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class TimerApp extends JFrame {

    private JTextField delayField;
    private JTextField periodField;
    private JTextField timeField;

    private JTextArea logArea;
    private JLabel statusLabel;

    private Timer delayTimer;
    private Timer periodTimer;
    private Timer clockTimer;

    private int counter = 0;

    public TimerApp() {
        setTitle("Process Timer Manager");
        setSize(650, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel(
                "PROCESS TIMER MANAGER",
                SwingConstants.CENTER
        );
        title.setFont(title.getFont().deriveFont(22f));

        mainPanel.add(title, BorderLayout.NORTH);

        // Panel pentru configurarea timerelor
        JPanel settingsPanel = new JPanel(new GridLayout(3, 2, 10, 10));

        settingsPanel.add(new JLabel("Intarziere (secunde):"));
        delayField = new JTextField("5");
        settingsPanel.add(delayField);

        settingsPanel.add(new JLabel("Perioada repetitiva (secunde):"));
        periodField = new JTextField("2");
        settingsPanel.add(periodField);

        settingsPanel.add(new JLabel("Ora executarii (HH:mm):"));
        timeField = new JTextField("23:59");
        settingsPanel.add(timeField);

        // Jurnalul activitatilor
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setLineWrap(true);

        JScrollPane scrollPane = new JScrollPane(logArea);

        // Butoane
        JButton startButton = new JButton("START");
        JButton stopButton = new JButton("STOP");
        JButton clearButton = new JButton("STERGE JURNALUL");

        startButton.addActionListener(e -> startTimers());
        stopButton.addActionListener(e -> stopTimers());

        clearButton.addActionListener(e -> logArea.setText(""));

        JPanel buttonPanel = new JPanel();

        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);
        buttonPanel.add(clearButton);

        statusLabel = new JLabel("Status: Oprit");

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(buttonPanel, BorderLayout.CENTER);
        bottomPanel.add(statusLabel, BorderLayout.SOUTH);

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.add(settingsPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                stopTimers();
            }
        });
    }

    // Adaugarea mesajelor in jurnal
    private void log(String message) {
        SwingUtilities.invokeLater(() -> {
            String time = LocalTime.now().format(
                    DateTimeFormatter.ofPattern("HH:mm:ss")
            );

            logArea.append("[" + time + "] " + message + "\n");
        });
    }

    // Pornirea celor trei timere
    private void startTimers() {

        stopTimers();

        try {
            int delay = Integer.parseInt(delayField.getText());
            int period = Integer.parseInt(periodField.getText());

            if (delay < 1 || period < 1) {
                throw new IllegalArgumentException();
            }

            LocalTime selectedTime = LocalTime.parse(
                    timeField.getText(),
                    DateTimeFormatter.ofPattern("HH:mm")
            );

            // TIMER 1: Executie dupa o intarziere
            delayTimer = new Timer();

            delayTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    log("Timer 1: A trecut intervalul de "
                            + delay + " secunde.");
                    delayTimer.cancel();
                }
            }, delay * 1000L);

            // TIMER 2: Executie la o ora exacta
            LocalDateTime scheduledDateTime =
                    LocalDateTime.of(LocalDate.now(), selectedTime);

            if (!scheduledDateTime.isAfter(LocalDateTime.now())) {
                scheduledDateTime = scheduledDateTime.plusDays(1);
            }

            Date scheduledDate = Date.from(
                    scheduledDateTime.atZone(
                            ZoneId.systemDefault()
                    ).toInstant()
            );

            clockTimer = new Timer();

            clockTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    log("Timer 2: Executie la ora programata "
                            + selectedTime + ".");
                    clockTimer.cancel();
                }
            }, scheduledDate);

            // TIMER 3: Executie repetata la o perioada fixa
            periodTimer = new Timer();

            counter = 0;

            periodTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    counter++;
                    log("Timer 3: Executia numarul "
                            + counter + " (la fiecare "
                            + period + " secunde).");
                }
            }, 0, period * 1000L);

            statusLabel.setText("Status: Timere pornite");

            log("Toate cele trei timere au fost pornite.");

            log("Timer 2 este programat pentru: "
                    + scheduledDateTime.format(
                            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
                    ));

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Introdu valori corecte!\n"
                    + "Intarzierea si perioada trebuie sa fie numere pozitive.\n"
                    + "Ora trebuie sa fie in formatul HH:mm.",
                    "Eroare",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // Oprirea tuturor timerelor
    private void stopTimers() {

        if (delayTimer != null) {
            delayTimer.cancel();
            delayTimer = null;
        }

        if (periodTimer != null) {
            periodTimer.cancel();
            periodTimer = null;
        }

        if (clockTimer != null) {
            clockTimer.cancel();
            clockTimer = null;
        }

        if (statusLabel != null) {
            statusLabel.setText("Status: Oprit");
        }

        log("Toate timerele au fost oprite.");
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            TimerApp app = new TimerApp();
            app.setVisible(true);
        });
    }
}
