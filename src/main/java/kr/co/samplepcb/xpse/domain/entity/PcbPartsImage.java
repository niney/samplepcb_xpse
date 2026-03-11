package kr.co.samplepcb.xpse.domain.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "sp_pcb_parts_image")
public class PcbPartsImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parts_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_sp_pcb_parts_image_parts"))
    private PcbParts pcbParts;

    @Column(name = "upload_file_name", length = 255)
    private String uploadFileName;

    @Column(name = "origin_file_name", length = 255)
    private String originFileName;

    @Column(name = "path_token", length = 255)
    private String pathToken;

    @Column(name = "size", length = 50)
    private String size;

    // === getter / setter ===

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public PcbParts getPcbParts() { return pcbParts; }
    public void setPcbParts(PcbParts pcbParts) { this.pcbParts = pcbParts; }

    public String getUploadFileName() { return uploadFileName; }
    public void setUploadFileName(String uploadFileName) { this.uploadFileName = uploadFileName; }

    public String getOriginFileName() { return originFileName; }
    public void setOriginFileName(String originFileName) { this.originFileName = originFileName; }

    public String getPathToken() { return pathToken; }
    public void setPathToken(String pathToken) { this.pathToken = pathToken; }

    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }
}
