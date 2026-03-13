package kr.co.samplepcb.xpse.repository;

import kr.co.samplepcb.xpse.domain.entity.SpFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpFileRepository extends JpaRepository<SpFile, Long> {

    List<SpFile> findByRefTypeAndRefId(String refType, Long refId);

    void deleteByRefTypeAndRefId(String refType, Long refId);
}
