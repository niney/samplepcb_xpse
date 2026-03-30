package kr.co.samplepcb.xpse.pojo;

import java.util.List;

public class PcbPartsMultiSearchResult {
    private SourceResult samplepcb;
    private SourceResult digikey;
    private SourceResult unikeyic;

    public SourceResult getSamplepcb() {
        return samplepcb;
    }

    public void setSamplepcb(SourceResult samplepcb) {
        this.samplepcb = samplepcb;
    }

    public SourceResult getDigikey() {
        return digikey;
    }

    public void setDigikey(SourceResult digikey) {
        this.digikey = digikey;
    }

    public SourceResult getUnikeyic() {
        return unikeyic;
    }

    public void setUnikeyic(SourceResult unikeyic) {
        this.unikeyic = unikeyic;
    }

    public static class SourceResult {
        private String searchType; // "exact" | "keyword"
        private List<?> items;

        public SourceResult() {
        }

        public SourceResult(String searchType, List<?> items) {
            this.searchType = searchType;
            this.items = items;
        }

        public String getSearchType() {
            return searchType;
        }

        public void setSearchType(String searchType) {
            this.searchType = searchType;
        }

        public List<?> getItems() {
            return items;
        }

        public void setItems(List<?> items) {
            this.items = items;
        }
    }
}
