import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;

class AdminGUI extends JFrame {
    private ArrayList<Tugas> tugasList = new ArrayList<>();
    private final DefaultTableModel tableModel;

    public AdminGUI(boolean isAdmin) {
        setTitle("Aplikasi Pengelola Tugas Kuliah");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());

        // Panel Judul
        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("Program Pengelolah Data Mahasiswa");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16)); // Atur font dan ukuran
        titlePanel.add(titleLabel);

        // Panel Tabel
        String[] columns = {"Judul", "Deskripsi", "Deadline", "Status"};
        tableModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        // Panel Tombol
        JButton addButton = new JButton("Tambah");
        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Hapus");
        JButton saveButton = new JButton("Simpan");
        JButton loadButton = new JButton("Muat");
        JButton logoutButton = new JButton("Logout");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(loadButton);
        buttonPanel.add(logoutButton);

        if (!isAdmin) {
            addButton.setEnabled(false);
            editButton.setEnabled(false);
            deleteButton.setEnabled(false);
            saveButton.setEnabled(false);
        }

        // Menambahkan semua komponen ke panel utama
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Menambahkan panel utama ke frame
        add(mainPanel);

        // Event Listeners
        addButton.addActionListener(e -> tambahTugas());
        editButton.addActionListener(e -> editTugas(table.getSelectedRow()));
        deleteButton.addActionListener(e -> hapusTugas(table.getSelectedRow()));
        saveButton.addActionListener(e -> simpanData());
        loadButton.addActionListener(e -> muatData());
        logoutButton.addActionListener(e -> logout());
    }

    private void tambahTugas() {
        JTextField judulField = new JTextField();
        JTextField deskripsiField = new JTextField();
        JTextField deadlineField = new JTextField();

        Object[] fields = {
                "Judul:", judulField,
                "Deskripsi:", deskripsiField,
                "Deadline (dd-MM-yyyy):", deadlineField
        };

        int result = JOptionPane.showConfirmDialog(this, fields, "Tambah Tugas", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                if (judulField.getText().isEmpty() || deadlineField.getText().isEmpty()) {
                    throw new IllegalArgumentException("Judul dan Deadline tidak boleh kosong!");
                }
                Tugas tugas = new Tugas(judulField.getText(), deskripsiField.getText(), deadlineField.getText());
                tugasList.add(tugas);
                updateTable();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Kesalahan", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editTugas(int rowIndex) {
        if (rowIndex < 0) {
            JOptionPane.showMessageDialog(this, "Pilih tugas yang ingin diubah!", "Kesalahan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Tugas tugas = tugasList.get(rowIndex);
        JTextField judulField = new JTextField(tugas.getJudul());
        JTextField deskripsiField = new JTextField(tugas.getDeskripsi());
        JTextField deadlineField = new JTextField(tugas.getDeadline());

        Object[] fields = {
                "Judul:", judulField,
                "Deskripsi:", deskripsiField,
                "Deadline (dd-MM-yyyy):", deadlineField
        };

        int result = JOptionPane.showConfirmDialog(this, fields, "Edit Tugas", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                if (judulField.getText().isEmpty() || deadlineField.getText().isEmpty()) {
                    throw new IllegalArgumentException("Judul dan Deadline tidak boleh kosong!");
                }
                tugas.setJudul(judulField.getText());
                tugas.setDeskripsi(deskripsiField.getText());
                tugas.setDeadline(deadlineField.getText());
                updateTable();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Kesalahan", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void hapusTugas(int rowIndex) {
        if (rowIndex < 0) {
            JOptionPane.showMessageDialog(this, "Pilih tugas yang ingin dihapus!", "Kesalahan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        tugasList.remove(rowIndex);
        updateTable();
    }

    private void simpanData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("tugas.dat"))) {
            oos.writeObject(tugasList);
            JOptionPane.showMessageDialog(this, "Data berhasil disimpan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan data!", "Kesalahan", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void muatData() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("tugas.dat"))) {
            Object data = ois.readObject();
            if (data instanceof ArrayList) {
                tugasList = (ArrayList<Tugas>) data;
                updateTable();
                JOptionPane.showMessageDialog(this, "Data berhasil dimuat!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            } else {
                throw new IOException("Format file tidak valid.");
            }
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(this, "File tugas.dat tidak ditemukan, memulai dengan data kosong.", "Informasi", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data!", "Kesalahan", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTable() {
        tableModel.setRowCount(0);
        for (Tugas tugas : tugasList) {
            tableModel.addRow(new Object[]{
                    tugas.getJudul(),
                    tugas.getDeskripsi(),
                    tugas.getDeadline(),
                    tugas.isSelesai() ? "Selesai" : "Belum"
            });
        }
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin logout?", "Konfirmasi Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dispose(); // Menutup jendela saat ini
            SwingUtilities.invokeLater(() -> { // Membuka jendela login
                LoginGUI loginGUI = new LoginGUI();
                loginGUI.setVisible(true);
            });
        }
    }
}

