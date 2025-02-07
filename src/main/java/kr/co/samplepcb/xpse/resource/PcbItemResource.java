package kr.co.samplepcb.xpse.resource;

import coolib.common.CCResult;
import kr.co.samplepcb.xpse.domain.PcbItemSearch;
import kr.co.samplepcb.xpse.service.PcbItemService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pcbItem")
public class PcbItemResource {

    // service
    private final PcbItemService pcbItemService;

    public PcbItemResource(PcbItemService pcbItemService) {
        this.pcbItemService = pcbItemService;
    }

    @PostMapping("/_indexing")
    public CCResult indexing(PcbItemSearch pcbItemSearch)  {
        return this.pcbItemService.indexing(pcbItemSearch);
    }

    @GetMapping("/_digikeyCategoryIndexing")
    public CCResult digikeyCategoryIndexing() {
        return this.pcbItemService.digikeyCategoryIndexing();
    }

    @GetMapping("/_search")
    public CCResult search(PcbItemSearch pcbItemSearch) {
        return this.pcbItemService.search(pcbItemSearch);
    }

}
