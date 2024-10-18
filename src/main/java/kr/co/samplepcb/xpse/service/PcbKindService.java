package kr.co.samplepcb.xpse.service;

import coolib.common.CCResult;
import coolib.util.CommonUtils;
import kr.co.samplepcb.xpse.domain.PcbKindSearch;
import kr.co.samplepcb.xpse.repository.PcbKindSearchRepository;
import kr.co.samplepcb.xpse.service.common.sub.ExcelSubService;
import kr.co.samplepcb.xpse.service.common.sub.PcbPartsSubService;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
public class PcbKindService {

    private static final Logger log = LoggerFactory.getLogger(PcbKindService.class);

    // service
    private final ExcelSubService excelSubService;
    private final PcbPartsSubService pcbPartsSubService;

    // search
    private final PcbKindSearchRepository pcbKindSearchRepository;

    public PcbKindService(ExcelSubService excelSubService, PcbPartsSubService pcbPartsSubService, PcbKindSearchRepository pcbKindSearchRepository) {
        this.excelSubService = excelSubService;
        this.pcbPartsSubService = pcbPartsSubService;
        this.pcbKindSearchRepository = pcbKindSearchRepository;
    }

    /**
     * 엑셀 파일에서 지정된 시트의 데이터를 기반으로 PCB 종류 데이터를 인덱싱합니다.
     *
     * @param workbook 엑셀 파일의 XSSFWorkbook 객체
     * @param sheetAt 인덱싱할 시트의 인덱스
     * @param target 목표 타겟 값
     */
    @SuppressWarnings("rawtypes")
    private void excelIndexing(XSSFWorkbook workbook, int sheetAt, int target) {
        XSSFSheet sheet = workbook.getSheetAt(sheetAt); // 해당 엑셀파일의 시트(Sheet) 수
        int rows = sheet.getPhysicalNumberOfRows(); // 해당 시트의 행의 개수
        List<PcbKindSearch> pcbKindSearchList = new ArrayList<>();
        Map<String, PcbKindSearch> pcbKindSearchMap = new HashMap<>();
        List<Map> modifyPcbKindList = new ArrayList<>();

        for (int rowIndex = 0; rowIndex < rows; rowIndex++) {
            XSSFRow row = sheet.getRow(rowIndex); // 각 행을 읽어온다
            if (row == null) {
                continue;
            }

            String valueStr = this.excelSubService.getCellStrValue(row, 1).trim();
            if (StringUtils.isEmpty(valueStr)) {
                continue;
            }
            PcbKindSearch findPcbItem = pcbKindSearchMap.get(valueStr);
            if (findPcbItem != null) {
                continue;
            }
            findPcbItem = this.pcbKindSearchRepository.findByItemNameKeywordAndTarget(valueStr, target);
            if (findPcbItem != null) {
                continue;
            }

            PcbKindSearch pcbKindSearch = new PcbKindSearch();
            String id = this.excelSubService.getCellStrValue(row, 0).trim();
            if (StringUtils.isNotEmpty(id)) {
                Optional<PcbKindSearch> findPcbKindOpt = this.pcbKindSearchRepository.findById(id);
                if (findPcbKindOpt.isPresent()) {
                    pcbKindSearch = findPcbKindOpt.get();
                    if (!pcbKindSearch.getItemName().equals(valueStr)) {
                        Map<Object, Object> modifyInfo = new HashMap<>();
                        modifyInfo.put("target", pcbKindSearch.getTarget());
                        modifyInfo.put("from", pcbKindSearch.getItemName());
                        modifyInfo.put("to", valueStr);
                        modifyPcbKindList.add(modifyInfo);
                    }
                } else {
                    pcbKindSearch.setId(id);
                }
            }
            pcbKindSearch.setItemName(valueStr);
            pcbKindSearch.setTarget(target);

            log.info("pcb kind item prepare indexing : target={}, value={}", target, valueStr);
            pcbKindSearchList.add(pcbKindSearch);
            pcbKindSearchMap.put(valueStr, pcbKindSearch);
        }

        for (Map modifyPcbKind : modifyPcbKindList) {
            Integer modifyTarget = (Integer) modifyPcbKind.get("target");
            String from = (String) modifyPcbKind.get("from");
            String to = (String) modifyPcbKind.get("to");
            CCResult result = this.pcbPartsSubService.updateKindAllByGroup(modifyTarget, from, to);
            if (!result.isResult()) {
                log.error("pcb modify kind parts indexing error : target={}, form={}, to={}, msg={}", modifyTarget, from, to, result.getMessage());
                return;
            }
            log.info("pcb modify kind parts indexing : target={}, form={}, to={}", modifyTarget, from, to);
        }

        this.pcbKindSearchRepository.saveAll(pcbKindSearchList);
        log.info("pcb kind items indexing : target={}", target);
    }

    /**
     * 주어진 파일의 모든 시트를 재인덱스합니다.
     *
     * @param file 엑셀 파일을 나타내는 MultipartFile 객체
     */
    public void reindexAllByFile(MultipartFile file) {

        XSSFWorkbook workbook = null;
        try {
            workbook = new XSSFWorkbook(file.getInputStream());

            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                String sheetName = workbook.getSheetName(i);
                int index = Integer.parseInt(sheetName.replaceAll("^\\D*(\\d+).*$", "$1"));
                if (index == 1 || index == 2 || index == 3) {
                    // 1,2,3 대중소 분류는 패스
                    continue;
                }
                excelIndexing(workbook, i, index);
            }
        } catch (Exception e) {
            log.error(CommonUtils.getFullStackTrace(e));
        } finally {
            try {
                if (workbook != null) {
                    workbook.close();
                }
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
    }
}
