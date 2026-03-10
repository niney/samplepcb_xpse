package kr.co.samplepcb.xpse.security;

public class JwtUserPrincipal {

    private final String sub;
    private final String mbName;
    private final int mbLevel;
    private final long mbNo;

    public JwtUserPrincipal(String sub, String mbName, int mbLevel, long mbNo) {
        this.sub = sub;
        this.mbName = mbName;
        this.mbLevel = mbLevel;
        this.mbNo = mbNo;
    }

    public String getSub() {
        return sub;
    }

    public String getMbName() {
        return mbName;
    }

    public int getMbLevel() {
        return mbLevel;
    }

    public long getMbNo() {
        return mbNo;
    }

    @Override
    public String toString() {
        return "JwtUserPrincipal{sub='" + sub + "', mbName='" + mbName + "', mbLevel=" + mbLevel + ", mbNo=" + mbNo + '}';
    }
}
