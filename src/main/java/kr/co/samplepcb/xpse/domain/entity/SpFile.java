package kr.co.samplepcb.xpse.domain.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "sp_file",
        indexes = @Index(name = "idx_sp_file_ref", columnList = "ref_type, ref_id"))
public class SpFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "ref_type", length = 50, nullable = false)
    private String refType;

    @Column(name = "ref_id", nullable = false)
    private Long refId;

    @Column(name = "upload_file_name", nullable = false)
    private String uploadFileName;

    @Column(name = "origin_file_name", nullable = false)
    private String originFileName;

    @Column(name = "path_token", length = 500, nullable = false)
    private String pathToken;

    @Column(name = "size", nullable = false)
    private long size;

    @Column(name = "write_date", nullable = false)
    private Date writeDate;

    // === getter / setter ===

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRefType() { return refType; }
    public void setRefType(String refType) { this.refType = refType; }

    public Long getRefId() { return refId; }
    public void setRefId(Long refId) { this.refId = refId; }

    public String getUploadFileName() { return uploadFileName; }
    public void setUploadFileName(String uploadFileName) { this.uploadFileName = uploadFileName; }

    public String getOriginFileName() { return originFileName; }
    public void setOriginFileName(String originFileName) { this.originFileName = originFileName; }

    public String getPathToken() { return pathToken; }
    public void setPathToken(String pathToken) { this.pathToken = pathToken; }

    public long getSize() { return size; }
    public void setSize(long size) { this.size = size; }

    public Date getWriteDate() { return writeDate; }
    public void setWriteDate(Date writeDate) { this.writeDate = writeDate; }
}
