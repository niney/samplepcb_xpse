package kr.co.samplepcb.xpse.service;

import coolib.common.CCObjectResult;
import coolib.common.CCResult;
import kr.co.samplepcb.xpse.domain.PcbItemSearch;
import kr.co.samplepcb.xpse.repository.PcbItemSearchRepository;
import kr.co.samplepcb.xpse.service.common.sub.DigikeySubService;
import kr.co.samplepcb.xpse.util.DigikeyUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Set;

@Service
public class PcbItemService {

    // service
    private final DigikeySubService digikeySubService;

    // repository
    private final PcbItemSearchRepository pcbItemSearchRepository;

    public PcbItemService(DigikeySubService digikeySubService, PcbItemSearchRepository pcbItemSearchRepository) {
        this.digikeySubService = digikeySubService;
        this.pcbItemSearchRepository = pcbItemSearchRepository;
    }

    public CCResult indexing(PcbItemSearch pcbItemSearch) {

        PcbItemSearch savedPcbItemSearch;
        if (pcbItemSearch.getTarget() == null) {
            savedPcbItemSearch = this.pcbItemSearchRepository.findByItemName(pcbItemSearch.getItemName());
        } else {
            savedPcbItemSearch = this.pcbItemSearchRepository.findByItemNameAndTarget(pcbItemSearch.getItemName(), pcbItemSearch.getTarget());
        }
        if (savedPcbItemSearch != null) {
            CCResult ccResult = new CCResult();
            ccResult.setResult(false);
            ccResult.setMessage("동일한 아이템명 존재합니다.");
            return ccResult;
        }

        this.pcbItemSearchRepository.save(pcbItemSearch);
        return CCObjectResult.setSimpleData(pcbItemSearch);
    }

    public CCResult search(PcbItemSearch pcbItemSearch) {
        PcbItemSearch savedPcbItemSearch;
        if (pcbItemSearch.getTarget() == null) {
            savedPcbItemSearch = this.pcbItemSearchRepository.findByItemName(pcbItemSearch.getItemName());
        } else {
            savedPcbItemSearch = this.pcbItemSearchRepository.findByItemNameAndTarget(pcbItemSearch.getItemName(), pcbItemSearch.getTarget());
        }
        return CCObjectResult.setSimpleData(savedPcbItemSearch);
    }

    public CCResult digikeyCategoryIndexing() {
        Mono<CCObjectResult<Map<String, Object>>> categories = this.digikeySubService.getCategories();
        CCObjectResult<Map<String, Object>> resilt = categories.block();
        Set<String> extractedWords = DigikeyUtils.extractWords(resilt.getData());
        for (String extractedWord : extractedWords) {
            PcbItemSearch pcbItemSearch = new PcbItemSearch();
            pcbItemSearch.setItemName(extractedWord);
            pcbItemSearch.setTarget(14);
            this.indexing(pcbItemSearch);
        }
        return CCResult.ok();
    }
}
