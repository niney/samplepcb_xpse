package kr.co.samplepcb.xpse.pojo;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "외부 공급사 일괄 검색 결과 (Digikey + UniKeyIC)")
public class PcbPartsExternalBatchResult {

    @Schema(description = "Digikey 검색 결과 (partName 순서대로 누적)")
    private PcbPartsMultiSearchResult.SourceResult digikey;

    @Schema(description = "UniKeyIC 검색 결과 (partName 순서대로 누적)")
    private PcbPartsMultiSearchResult.SourceResult unikeyic;

    public PcbPartsMultiSearchResult.SourceResult getDigikey() {
        return digikey;
    }

    public void setDigikey(PcbPartsMultiSearchResult.SourceResult digikey) {
        this.digikey = digikey;
    }

    public PcbPartsMultiSearchResult.SourceResult getUnikeyic() {
        return unikeyic;
    }

    public void setUnikeyic(PcbPartsMultiSearchResult.SourceResult unikeyic) {
        this.unikeyic = unikeyic;
    }
}
