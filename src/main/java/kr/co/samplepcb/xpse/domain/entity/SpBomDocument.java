package kr.co.samplepcb.xpse.domain.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "sp_bom_document",
        indexes = @Index(name = "idx_mb_id", columnList = "mb_id"), uniqueConstraints = @UniqueConstraint(name = "uq_mb_content", columnNames = {"mb_id", "content_hash"}))
public class SpBomDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "mb_id", length = 100, nullable = false)
    private String mbId;

    @Column(name = "file_name", length = 255, nullable = false)
    private String fileName;

    @Column(name = "content_hash", length = 64)
    private String contentHash;

    @Lob
    @Column(name = "file_info", columnDefinition = "text")
    private String fileInfo;

    @Lob
    @Column(name = "items", columnDefinition = "longtext", nullable = false)
    private String items;

    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    @Column(name = "updated_at", nullable = false)
    private Date updatedAt;

    // === getter / setter ===

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMbId() { return mbId; }
    public void setMbId(String mbId) { this.mbId = mbId; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getContentHash() { return contentHash; }
    public void setContentHash(String contentHash) { this.contentHash = contentHash; }

    public String getFileInfo() { return fileInfo; }
    public void setFileInfo(String fileInfo) { this.fileInfo = fileInfo; }

    public String getItems() { return items; }
    public void setItems(String items) { this.items = items; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}
