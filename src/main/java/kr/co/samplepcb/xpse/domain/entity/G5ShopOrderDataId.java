package kr.co.samplepcb.xpse.domain.entity;

import java.io.Serializable;
import java.util.Objects;

public class G5ShopOrderDataId implements Serializable {

    private long odId;
    private long cartId;

    public G5ShopOrderDataId() {}

    public G5ShopOrderDataId(long odId, long cartId) {
        this.odId = odId;
        this.cartId = cartId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        G5ShopOrderDataId that = (G5ShopOrderDataId) o;
        return odId == that.odId && cartId == that.cartId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(odId, cartId);
    }

    public long getOdId() { return odId; }
    public void setOdId(long odId) { this.odId = odId; }
    public long getCartId() { return cartId; }
    public void setCartId(long cartId) { this.cartId = cartId; }
}
