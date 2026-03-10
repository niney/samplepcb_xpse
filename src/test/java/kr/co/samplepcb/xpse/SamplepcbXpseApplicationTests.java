package kr.co.samplepcb.xpse;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled("DataSource 설정 없이 전체 컨텍스트 로드 불가")
class SamplepcbXpseApplicationTests {

    @Test
    void contextLoads() {
    }

}
