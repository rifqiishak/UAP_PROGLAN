import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

class LoginGUI extends JFrame {
    private final ArrayList<User> users = new ArrayList<>();

    public LoginGUI() {
        setTitle("Selamat Datang, Silahkan Login");
        setSize(500, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel utama dengan gradien warna
        JPanel gradientPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                int width = getWidth();
                int height = getHeight();
                Color color1 = new Color(255, 153, 102);
                Color color2 = new Color(102, 204, 255);
                GradientPaint gp = new GradientPaint(0, 0, color1, width, height, color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, width, height);
            }
        };
        gradientPanel.setLayout(new GridBagLayout());

        // Panel form login dengan transparansi
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(255, 255, 255, 200)); // Warna putih transparan
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel userLabel = new JLabel("Username:");
        JTextField userField = new JTextField(15);
        JLabel passLabel = new JLabel("Password:");
        JPasswordField passField = new JPasswordField(15);
        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");

        // Tambahkan komponen ke form panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(userLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(userField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(passLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(passField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        formPanel.add(registerButton, gbc);

        gbc.gridy = 3;
        formPanel.add(loginButton, gbc);

        // Tambahkan efek hover ke tombol
        loginButton.setBackground(new Color(102, 204, 255));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setBorder(BorderFactory.createRaisedBevelBorder());
        loginButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                loginButton.setBackground(new Color(51, 153, 255));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                loginButton.setBackground(new Color(102, 204, 255));
            }
        });

        registerButton.setBackground(new Color(102, 204, 255));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        registerButton.setBorder(BorderFactory.createRaisedBevelBorder());
        registerButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                registerButton.setBackground(new Color(51, 153, 255));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                registerButton.setBackground(new Color(102, 204, 255));
            }
        });

        gradientPanel.add(formPanel);
        add(gradientPanel);

        registerButton.addActionListener(e -> {
            String username = JOptionPane.showInputDialog(this, "Masukkan Username:", "Register", JOptionPane.PLAIN_MESSAGE);
            if (username == null || username.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Username tidak boleh kosong!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String password = JOptionPane.showInputDialog(this, "Masukkan Password:", "Register", JOptionPane.PLAIN_MESSAGE);
            if (password == null || password.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Password tidak boleh kosong!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            users.add(new User(username, password));
            JOptionPane.showMessageDialog(this, "Akun berhasil dibuat!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
        });

        loginButton.addActionListener(e -> {
            String username = userField.getText();
            String password = new String(passField.getPassword());

            if (username.equals("admin") && password.equals("admin123")) {
                JOptionPane.showMessageDialog(this, "Login sebagai Admin berhasil!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                new AdminGUI(true).setVisible(true);
                dispose();
            } else if (isMahasiswa(username, password)) {
                JOptionPane.showMessageDialog(this, "Login sebagai Mahasiswa berhasil!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                new AdminGUI(false).setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Username atau Password salah!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private boolean isMahasiswa(String username, String password) {
        return users.stream().anyMatch(user -> user.getUsername().equals(username) && user.getPassword().equals(password));
    }
}

