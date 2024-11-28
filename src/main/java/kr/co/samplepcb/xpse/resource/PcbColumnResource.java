package kr.co.samplepcb.xpse.resource;

import coolib.common.CCResult;
import coolib.common.QueryParam;
import kr.co.samplepcb.xpse.pojo.PcbColumnSearchVM;
import kr.co.samplepcb.xpse.pojo.PcbSentenceVM;
import kr.co.samplepcb.xpse.service.PcbColumnService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pcbColumn")
public class PcbColumnResource {

    // service
    private final PcbColumnService pcbColumnService;

    public PcbColumnResource(PcbColumnService pcbColumnService) {
        this.pcbColumnService = pcbColumnService;
    }

    @GetMapping("/_search")
    public CCResult search(@PageableDefault Pageable pageable, QueryParam queryParam, PcbColumnSearchVM pcbColumnSearchVM) {
        return this.pcbColumnService.search(pageable, queryParam, pcbColumnSearchVM);
    }

    @PostMapping("/_searchSentenceList")
    public CCResult searchSentenceList(@PageableDefault(size = 3) Pageable pageable, @RequestBody PcbSentenceVM pcbSentenceVM) {
        return this.pcbColumnService.searchSentenceList(pageable, pcbSentenceVM);
    }

    @PostMapping("/_indexing")
    public CCResult indexing(PcbColumnSearchVM pcbColumnSearchVM) {
        return this.pcbColumnService.indexing(pcbColumnSearchVM);
    }

}
