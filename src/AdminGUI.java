import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.io.File;
import java.sql.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.time.LocalDate;
import java.time.LocalDate;
import java.util.Date;
import javax.swing.*;
import javax.swing.SpinnerDateModel;



// Tambahkan class ActionButtonRenderer untuk menampilkan tombol
class ActionButtonRenderer extends JButton implements TableCellRenderer {
    public ActionButtonRenderer() {
        setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        setText((value == null) ? "Selesai" : value.toString());
        return this;
    }
}


class ScrollableCellRenderer extends JPanel implements TableCellRenderer {
    private final JTextArea textArea;

    public ScrollableCellRenderer() {
        setLayout(new BorderLayout());
        textArea = new JTextArea();
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        add(scrollPane, BorderLayout.CENTER);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        textArea.setText(value != null ? value.toString() : "");
        if (isSelected) {
            setBackground(table.getSelectionBackground());
            textArea.setBackground(table.getSelectionBackground());
        } else {
            setBackground(table.getBackground());
            textArea.setBackground(table.getBackground());
        }
        return this;
    }
}

// Tambahkan class ActionButtonEditor untuk mengatur aksi tombol
class ActionButtonEditor extends DefaultCellEditor {
    private JButton button;
    private String label;
    private boolean clicked;
    private JTable table;
    private Connection connection;

    public ActionButtonEditor(JCheckBox checkBox, JTable table, Connection connection) {
        super(checkBox);
        this.table = table;
        this.connection = connection;
        button = new JButton();
        button.setOpaque(true);

        button.addActionListener(e -> fireEditingStopped());
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        label = (value == null) ? "Selesai" : value.toString();
        button.setText(label);
        clicked = true;
        return button;
    }

    @Override
    public Object getCellEditorValue() {
        if (clicked) {
            int row = table.getSelectedRow(); // Dapatkan baris yang dipilih
            int id = (int) table.getValueAt(row, 0); // Dapatkan ID dari kolom pertama

            // Perbarui status di database
            String updateSQL = "UPDATE tugas SET status = 'Selesai' WHERE id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(updateSQL)) {
                pstmt.setInt(1, id);
                pstmt.executeUpdate();

                // Tampilkan pesan sukses
                JOptionPane.showMessageDialog(button, "Status tugas berhasil diubah menjadi Selesai!");

                // Perbarui data di tabel GUI (kolom Status)
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                model.setValueAt("Selesai", row, 5); // Kolom indeks 4 adalah kolom "Status"
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(button, "Gagal mengubah status tugas!", "Kesalahan", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
        clicked = false;
        return label;
    }

    @Override
    protected void fireEditingStopped() {
        super.fireEditingStopped();
    }
}


class AdminGUI extends JFrame {
    private final DefaultTableModel tableModel;
    private JTable table;
    private Connection connection;

    public AdminGUI(boolean isAdmin) {
        setTitle("Aplikasi Pengelola Tugas Kuliah");
        setSize(800, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Inisialisasi koneksi ke SQLite
        connectToDatabase();
        createTableIfNotExists();

        JPanel mainPanel = new JPanel(new BorderLayout());

        // Panel Judul
        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("Program Pengelola Data Mahasiswa");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titlePanel.add(titleLabel);

        // Panel Tabel
        String[] columns = {"ID", "Judul", "Deskripsi", "Deadline", "Tanggal Posting", "Status", "Gambar", "Sisa hari menuju deadline"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                if (!isAdmin && column == 5) {
                    // Kolom ke-5 tidak bisa diedit jika bukan admin
                    return false;
                }
                return super.isCellEditable(row, column);
            }


            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 6 ? ImageIcon.class : String.class;
            }
        };




        table = new JTable(tableModel);
        table.setRowHeight(100);
        JScrollPane scrollPane = new JScrollPane(table);

        // Konfigurasi renderer dan editor hanya untuk kolom "Status"
        table.getColumnModel().getColumn(5).setCellRenderer(new ActionButtonRenderer());
        if (isAdmin) {
            table.getColumnModel().getColumn(5).setCellEditor(new ActionButtonEditor(new JCheckBox(), table, connection));
        }

        // Panel Tombol
        JButton addButton = new JButton("Tambah");
        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Hapus");
        JButton logoutButton = new JButton("Logout");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(logoutButton);

        if (!isAdmin) {
            addButton.setEnabled(false);
            editButton.setEnabled(false);
            deleteButton.setEnabled(false);
        }

        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Event Listeners
        addButton.addActionListener(e -> tambahTugas());
        editButton.addActionListener(e -> editTugas());
        deleteButton.addActionListener(e -> hapusTugas());
        logoutButton.addActionListener(e -> logout());

        loadDataFromDatabase();
    }

    private void connectToDatabase() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:tugas.db");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal terhubung ke database!", "Kesalahan", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    private void createTableIfNotExists() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS tugas (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "judul TEXT NOT NULL, " +
                "deskripsi TEXT, " +
                "deadline TEXT NOT NULL, " +
                "tanggal_posting TEXT NOT NULL, " +
                "status TEXT NOT NULL DEFAULT 'Belum', " +
                "gambar TEXT" +
                ");"; // Pastikan ada tanda titik koma di sini
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTableSQL);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal membuat tabel database!", "Kesalahan", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }


    private void tambahTugas() {
        JTextField judulField = new JTextField();
        JTextField deskripsiField = new JTextField();

        // Spinner untuk memilih deadline
        JSpinner deadlineSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(deadlineSpinner, "yyyy-MM-dd");
        deadlineSpinner.setEditor(dateEditor);

        // Field untuk tanggal posting (otomatis terisi tanggal sekarang)
        JTextField tanggalPostingField = new JTextField(LocalDate.now().toString());
        tanggalPostingField.setEditable(false);

        JButton pilihGambarButton = new JButton("Pilih Gambar");
        JLabel gambarLabel = new JLabel();

        pilihGambarButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new FileNameExtensionFilter("Image Files", "jpg", "png", "jpeg"));
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                gambarLabel.setText(file.getAbsolutePath());
            }
        });

        Object[] fields = {
                "Judul:", judulField,
                "Deskripsi:", deskripsiField,
                "Deadline (pilih tanggal):", deadlineSpinner,
                "Tanggal Posting (otomatis):", tanggalPostingField,
                "Gambar:", pilihGambarButton, gambarLabel
        };

        int result = JOptionPane.showConfirmDialog(this, fields, "Tambah Tugas", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String judul = judulField.getText();
            String deskripsi = deskripsiField.getText();

            // Ambil nilai deadline dari spinner
            Date deadlineDate = (Date) deadlineSpinner.getValue();
            String deadline = new java.text.SimpleDateFormat("yyyy-MM-dd").format(deadlineDate);

            // Tanggal posting otomatis
            String tanggalPosting = tanggalPostingField.getText();
            String status = "Belum";
            String gambar = gambarLabel.getText();

            if (judul.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Judul tidak boleh kosong!", "Kesalahan", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String insertSQL = "INSERT INTO tugas (judul, deskripsi, deadline, tanggal_posting, status, gambar) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {
                pstmt.setString(1, judul);
                pstmt.setString(2, deskripsi);
                pstmt.setString(3, deadline);
                pstmt.setString(4, tanggalPosting);
                pstmt.setString(5, status);
                pstmt.setString(6, gambar);
                pstmt.executeUpdate();
                loadDataFromDatabase();
                JOptionPane.showMessageDialog(this, "Tugas berhasil ditambahkan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Gagal menambahkan tugas!", "Kesalahan", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }


    public void loadDataFromDatabase() {
        tableModel.setRowCount(0);
        String query = "SELECT * FROM tugas";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                String imagePath = rs.getString("gambar");
                ImageIcon imageIcon = null;
                if (imagePath != null && !imagePath.isEmpty()) {
                    File imgFile = new File(imagePath);
                    if (imgFile.exists()) {
                        imageIcon = new ImageIcon(new ImageIcon(imagePath).getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH));
                    }
                }

                // Ambil tanggal posting dan deadline
                String deadline = rs.getString("deadline");
                String tanggalPosting = rs.getString("tanggal_posting");

                // Hitung selisih hari
                long selisihHari = 0;
                if (deadline != null && tanggalPosting != null) {
                    LocalDate deadlineDate = LocalDate.parse(deadline);
                    LocalDate postingDate = LocalDate.parse(tanggalPosting);
                    selisihHari = java.time.temporal.ChronoUnit.DAYS.between(postingDate, deadlineDate);
                }

                // Tambahkan data ke tabel
                tableModel.addRow(new Object[] {
                        rs.getInt("id"),
                        rs.getString("judul"),
                        rs.getString("deskripsi"),
                        rs.getString("deadline"),
                        rs.getString("tanggal_posting"),
                        rs.getString("status"),
                        imageIcon,
                        selisihHari + " hari", // Tampilkan selisih hari
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data dari database!", "Kesalahan", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }



    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin logout?",
                "Konfirmasi Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            SwingUtilities.invokeLater(() -> new LoginGUI().setVisible(true));
        }
    }
}
