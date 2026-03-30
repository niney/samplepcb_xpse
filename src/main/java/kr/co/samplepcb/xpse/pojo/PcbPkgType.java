package kr.co.samplepcb.xpse.pojo;

public enum PcbPkgType {

    UNKNOWN("unknown"),
    DIGIKEY("digikey"),
    SAMPLEPCB("samplepcb"),
    ELEPARTS("eleparts"),
    UNIKEYIC("unikeyic");

    private final String value;

    PcbPkgType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
