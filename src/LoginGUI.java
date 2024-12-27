import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LoginGUI extends JFrame {
    public LoginGUI() {
        setTitle("Selamat Datang, Silahkan Login");
        setSize(500, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel gradientPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                int width = getWidth();
                int height = getHeight();
                GradientPaint gp = new GradientPaint(0, 0, new Color(255, 153, 102), width, height, new Color(102, 204, 255));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, width, height);
            }
        };
        gradientPanel.setLayout(new GridBagLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(255, 255, 255, 200));
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

        gradientPanel.add(formPanel);
        add(gradientPanel);

        registerButton.addActionListener(e -> {
            String username = JOptionPane.showInputDialog(this, "Masukkan Username:");
            String password = JOptionPane.showInputDialog(this, "Masukkan Password:");
//Penambahan data user register
            if (DatabaseHandler.registerUser(username, password, "mahasiswa")) {
                JOptionPane.showMessageDialog(this, "Registrasi berhasil!");
            } else {
                JOptionPane.showMessageDialog(this, "Registrasi gagal! Username sudah ada.");
            }
        });

        loginButton.addActionListener(e -> {
            String username = userField.getText();
            String password = new String(passField.getPassword());
            String role = DatabaseHandler.loginUser(username, password);

            if (role == null) {
                JOptionPane.showMessageDialog(this, "Username atau Password salah!");
            } else if ("admin".equals(role)) {
                JOptionPane.showMessageDialog(this, "Login sebagai Admin berhasil!");
                new AdminGUI(true).setVisible(true);
                dispose();
            } else if ("mahasiswa".equals(role)) {
                JOptionPane.showMessageDialog(this, "Login sebagai Mahasiswa berhasil!");
                new AdminGUI(false).setVisible(true);
                dispose();
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginGUI().setVisible(true));
    }
}
