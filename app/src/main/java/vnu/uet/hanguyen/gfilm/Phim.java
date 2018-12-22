package vnu.uet.hanguyen.gfilm;

public class Phim{


        private int id;
        private String theloai;
        private String link;
        private String tenphim;
        private String link_image_phim;


    public Phim(int id, String theloai, String link, String tenphim, String link_image_phim) {
        this.id = id;
        this.theloai = theloai;
        this.link = link;
        this.tenphim = tenphim;
        this.link_image_phim = link_image_phim;
    }

    public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTheloai() {
            return theloai;
        }

        public void setTheloai(String theloai) {
            this.theloai = theloai;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public String getTenphim() {
            return tenphim;
        }

        public void setTenphim(String tenphim) {
            this.tenphim = tenphim;
        }

        public String getLink_image_phim() {
            return link_image_phim;
        }

        public void setLink_image_phim(String link_image_phim) {
            this.link_image_phim = link_image_phim;
        }

    @Override
    public String toString() {
        return "id: "+id+"tenphim: "+tenphim;
    }
}