import java.io.Serializable;

class Tugas implements Serializable {
    private String judul;
    private String deskripsi;
    private String deadline;
    private boolean selesai;
    private String gambarPath;

    public Tugas(String judul, String deskripsi, String deadline) {
        this.judul = judul;
        this.deskripsi = deskripsi;
        this.deadline = deadline;
        this.selesai = false;
    }

    public String getJudul() {
        return judul;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public boolean isSelesai() {
        return selesai;
    }

    public void setSelesai(boolean selesai) {
        this.selesai = selesai;
    }

    public String getGambarPath() {
        return gambarPath;
    }

    public void setGambarPath(String gambarPath) {
        this.gambarPath = gambarPath;
    }
}