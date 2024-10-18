package kr.co.samplepcb.xpse.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import coolib.common.CCResult;
import kr.co.samplepcb.xpse.domain.PcbKindSearch;
import kr.co.samplepcb.xpse.service.PcbKindService;
import kr.co.samplepcb.xpse.util.CoolElasticUtils;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/pcbKind")
public class PcbKindResource {

    private final PcbKindService pcbKindService;

    public PcbKindResource(PcbKindService pcbKindService) {
        this.pcbKindService = pcbKindService;
    }

    @PostMapping(value = "/_uploadItemFile")
    public CCResult uploadItemFile(@RequestParam("file") MultipartFile file/*, HttpServletRequest request*/) {
        this.pcbKindService.reindexAllByFile(file);
        return CCResult.ok();
    }
}
