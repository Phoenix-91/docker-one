import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class WeatherGUI extends JFrame {

    // ── Search row ──────────────────────────────────────────────────────────
    private JTextField cityField;
    private JButton    searchBtn;

    // ── Weather display ──────────────────────────────────────────────────────
    private JLabel cityLabel;
    private JLabel dateLabel;
    private JLabel tempLabel;
    private JLabel conditionLabel;
    private JLabel humidityVal;
    private JLabel windVal;
    private JLabel feelsLikeVal;

    // ── Colors ───────────────────────────────────────────────────────────────
    private static final Color BLUE_DARK   = new Color(24,  95,  165);
    private static final Color BLUE_LIGHT  = new Color(230, 241, 251);
    private static final Color BG_WHITE    = new Color(255, 255, 255);
    private static final Color BG_CARD     = new Color(245, 246, 248);
    private static final Color BORDER_CLR  = new Color(220, 220, 220);
    private static final Color TEXT_MAIN   = new Color(30,  30,  30);
    private static final Color TEXT_MUTED  = new Color(120, 120, 120);

    private final WeatherService weatherService = new WeatherService();

    // ────────────────────────────────────────────────────────────────────────
    public WeatherGUI() {
        setTitle("Java Weather App");
        setSize(420, 560);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        buildUI();
        setVisible(true);
    }

    // ── Build the entire UI ──────────────────────────────────────────────────
    private void buildUI() {
        JPanel root = new JPanel();
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setBackground(BG_WHITE);

        root.add(buildTitleBar());
        root.add(buildSearchRow());
        root.add(Box.createVerticalStrut(12));
        root.add(buildCityDateBlock());
        root.add(buildTempBlock());
        root.add(buildConditionBlock());
        root.add(Box.createVerticalStrut(8));
        root.add(buildSeparator());
        root.add(Box.createVerticalStrut(8));
        root.add(buildStatsRow());
        root.add(Box.createVerticalStrut(16));

        add(root);
    }

    // ── Title bar ────────────────────────────────────────────────────────────
    private JPanel buildTitleBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 10));
        bar.setBackground(BLUE_DARK);
        bar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        // Traffic-light dots
        bar.add(colorDot(new Color(248, 113, 113)));
        bar.add(colorDot(new Color(251, 191, 36)));
        bar.add(colorDot(new Color(52,  211, 153)));

        JLabel title = new JLabel("  Java Weather App");
        title.setForeground(BLUE_LIGHT);
        title.setFont(new Font("SansSerif", Font.BOLD, 14));
        bar.add(title);
        return bar;
    }

    private JLabel colorDot(Color c) {
        JLabel dot = new JLabel() {
            protected void paintComponent(Graphics g) {
                g.setColor(c);
                g.fillOval(0, 0, 12, 12);
            }
        };
        dot.setPreferredSize(new Dimension(12, 12));
        return dot;
    }

    // ── Search row ────────────────────────────────────────────────────────────
    private JPanel buildSearchRow() {
        JPanel panel = new JPanel(new BorderLayout(8, 0));
        panel.setBackground(BG_WHITE);
        panel.setBorder(new EmptyBorder(16, 16, 0, 16));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        cityField = new JTextField("London");
        cityField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        cityField.setForeground(TEXT_MAIN);
        cityField.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_CLR, 1, true),
            new EmptyBorder(7, 12, 7, 12)
        ));

        searchBtn = new JButton("Search");
        searchBtn.setBackground(BLUE_DARK);
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        searchBtn.setFocusPainted(false);
        searchBtn.setBorderPainted(false);
        searchBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        searchBtn.setBorder(new EmptyBorder(8, 18, 8, 18));

        // Hover effect
        searchBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                searchBtn.setBackground(new Color(18, 75, 130));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                searchBtn.setBackground(BLUE_DARK);
            }
        });

        cityField.addActionListener(e -> fetchWeather());
        searchBtn.addActionListener(e -> fetchWeather());

        panel.add(cityField,  BorderLayout.CENTER);
        panel.add(searchBtn, BorderLayout.EAST);
        return panel;
    }

    // ── City + Date ───────────────────────────────────────────────────────────
    private JPanel buildCityDateBlock() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BG_WHITE);
        panel.setBorder(new EmptyBorder(12, 0, 0, 0));

        cityLabel = centeredLabel("-- , --", 22, Font.BOLD, TEXT_MAIN);
        dateLabel = centeredLabel("", 12, Font.PLAIN, TEXT_MUTED);

        panel.add(cityLabel);
        panel.add(Box.createVerticalStrut(4));
        panel.add(dateLabel);
        return panel;
    }

    // ── Big temperature ───────────────────────────────────────────────────────
    private JPanel buildTempBlock() {
        JPanel panel = new JPanel();
        panel.setBackground(BG_WHITE);
        panel.setBorder(new EmptyBorder(6, 0, 0, 0));

        tempLabel = new JLabel("--°C", SwingConstants.CENTER);
        tempLabel.setFont(new Font("SansSerif", Font.BOLD, 64));
        tempLabel.setForeground(TEXT_MAIN);
        panel.add(tempLabel);
        return panel;
    }

    // ── Condition badge ───────────────────────────────────────────────────────
    private JPanel buildConditionBlock() {
        JPanel outer = new JPanel();
        outer.setBackground(BG_WHITE);

        conditionLabel = new JLabel("", SwingConstants.CENTER);
        conditionLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        conditionLabel.setForeground(BLUE_DARK);
        conditionLabel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(180, 212, 244), 1, true),
            new EmptyBorder(4, 16, 4, 16)
        ));
        conditionLabel.setOpaque(true);
        conditionLabel.setBackground(BLUE_LIGHT);

        outer.add(conditionLabel);
        return outer;
    }

    // ── Thin separator ────────────────────────────────────────────────────────
    private JSeparator buildSeparator() {
        JSeparator sep = new JSeparator();
        sep.setForeground(BORDER_CLR);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return sep;
    }

    // ── Stats row (Humidity | Wind | Feels Like) ──────────────────────────────
    private JPanel buildStatsRow() {
        JPanel row = new JPanel(new GridLayout(1, 3, 10, 0));
        row.setBackground(BG_WHITE);
        row.setBorder(new EmptyBorder(0, 16, 0, 16));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        humidityVal  = statValueLabel("--");
        windVal      = statValueLabel("--");
        feelsLikeVal = statValueLabel("--");

        row.add(makeStatCard("Humidity",   humidityVal));
        row.add(makeStatCard("Wind",       windVal));
        row.add(makeStatCard("Feels Like", feelsLikeVal));
        return row;
    }

    private JPanel makeStatCard(String title, JLabel valueLabel) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_CLR, 1, true),
            new EmptyBorder(10, 8, 10, 8)
        ));

        JLabel titleLbl = new JLabel(title, SwingConstants.CENTER);
        titleLbl.setFont(new Font("SansSerif", Font.PLAIN, 11));
        titleLbl.setForeground(TEXT_MUTED);
        titleLbl.setAlignmentX(CENTER_ALIGNMENT);

        valueLabel.setAlignmentX(CENTER_ALIGNMENT);

        card.add(titleLbl);
        card.add(Box.createVerticalStrut(4));
        card.add(valueLabel);
        return card;
    }

    // ── Fetch weather on background thread ────────────────────────────────────
    private void fetchWeather() {
        String city = cityField.getText().trim();
        if (city.isEmpty()) return;

        searchBtn.setText("...");
        searchBtn.setEnabled(false);
        cityLabel.setText("Loading...");
        conditionLabel.setText(" ");

        new SwingWorker<WeatherData, Void>() {
            @Override
            protected WeatherData doInBackground() throws Exception {
                return weatherService.getWeather(city);
            }

            @Override
            protected void done() {
                try {
                    updateUI(get());
                } catch (Exception ex) {
                    cityLabel.setText("-- , --");
                    JOptionPane.showMessageDialog(
                        WeatherGUI.this,
                        ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                } finally {
                    searchBtn.setText("Search");
                    searchBtn.setEnabled(true);
                }
            }
        }.execute();
    }

    // ── Populate labels with fetched data ─────────────────────────────────────
    private void updateUI(WeatherData data) {
        cityLabel.setText(data.getCity());
        tempLabel.setText(String.format("%.0f°C", data.getTemperature()));
        conditionLabel.setText("  " + capitalize(data.getDescription()) + "  ");
        humidityVal.setText(data.getHumidity() + "%");
        windVal.setText(String.format("%.1f m/s", data.getWindSpeed()));
        feelsLikeVal.setText(String.format("%.0f°C", data.getFeelsLike()));

        String date = LocalDate.now()
            .format(DateTimeFormatter.ofPattern("EEEE, MMMM d"));
        dateLabel.setText(date);
    }

    // ── Small helpers ─────────────────────────────────────────────────────────
    private JLabel centeredLabel(String text, int size, int style, Color color) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setFont(new Font("SansSerif", style, size));
        lbl.setForeground(color);
        lbl.setAlignmentX(CENTER_ALIGNMENT);
        lbl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        return lbl;
    }

    private JLabel statValueLabel(String text) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 17));
        lbl.setForeground(TEXT_MAIN);
        return lbl;
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}
