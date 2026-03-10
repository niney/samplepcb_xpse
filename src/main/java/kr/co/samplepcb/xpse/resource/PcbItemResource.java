package kr.co.samplepcb.xpse.resource;

import coolib.common.CCResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.samplepcb.xpse.domain.document.PcbItemSearch;
import kr.co.samplepcb.xpse.service.PcbItemService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "PCB 아이템", description = "PCB 아이템 인덱싱/검색 API")
@RestController
@RequestMapping("/api/pcbItem")
public class PcbItemResource {

    private final PcbItemService pcbItemService;

    public PcbItemResource(PcbItemService pcbItemService) {
        this.pcbItemService = pcbItemService;
    }

    @Operation(summary = "아이템 인덱싱", description = "PCB 아이템을 Elasticsearch에 인덱싱합니다")
    @PostMapping("/_indexing")
    public CCResult indexing(PcbItemSearch pcbItemSearch) {
        return this.pcbItemService.indexing(pcbItemSearch);
    }

    @Operation(summary = "Digikey 카테고리 인덱싱", description = "Digikey 카테고리 정보를 인덱싱합니다")
    @GetMapping("/_digikeyCategoryIndexing")
    public CCResult digikeyCategoryIndexing() {
        return this.pcbItemService.digikeyCategoryIndexing();
    }

    @Operation(summary = "아이템 검색", description = "PCB 아이템을 검색합니다")
    @GetMapping("/_search")
    public CCResult search(PcbItemSearch pcbItemSearch) {
        return this.pcbItemService.search(pcbItemSearch);
    }
}
