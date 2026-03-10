package kr.co.samplepcb.xpse.resource;

import coolib.common.CCResult;
import coolib.common.QueryParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.samplepcb.xpse.pojo.PcbColumnSearchVM;
import kr.co.samplepcb.xpse.pojo.PcbSentenceVM;
import kr.co.samplepcb.xpse.service.PcbColumnService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@Tag(name = "PCB 컬럼", description = "PCB 컬럼 검색/인덱싱 API")
@RestController
@RequestMapping("/api/pcbColumn")
public class PcbColumnResource {

    private final PcbColumnService pcbColumnService;

    public PcbColumnResource(PcbColumnService pcbColumnService) {
        this.pcbColumnService = pcbColumnService;
    }

    @Operation(summary = "컬럼 검색", description = "PCB 컬럼을 검색합니다")
    @GetMapping("/_search")
    public CCResult search(@PageableDefault Pageable pageable, QueryParam queryParam, PcbColumnSearchVM pcbColumnSearchVM) {
        return this.pcbColumnService.search(pageable, queryParam, pcbColumnSearchVM);
    }

    @Operation(summary = "문장 목록 검색", description = "문장 목록으로 PCB 컬럼을 검색합니다")
    @PostMapping("/_searchSentenceList")
    public CCResult searchSentenceList(@PageableDefault(size = 3) Pageable pageable, @RequestBody PcbSentenceVM pcbSentenceVM) {
        return this.pcbColumnService.searchSentenceList(pageable, pcbSentenceVM);
    }

    @Operation(summary = "컬럼 인덱싱", description = "PCB 컬럼을 Elasticsearch에 인덱싱합니다")
    @PostMapping("/_indexing")
    public CCResult indexing(PcbColumnSearchVM pcbColumnSearchVM) {
        return this.pcbColumnService.indexing(pcbColumnSearchVM);
    }
}
