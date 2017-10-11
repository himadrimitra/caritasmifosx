package com.finflux.common.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public class ExcelWorkbook {

    private Collection<ExcelWorksheet> sheets = new ArrayList<>();

    public void addExcelWorksheet(final ExcelWorksheet sheet) {
        this.sheets.add(sheet);
    }

    public String toJson(final boolean pretty) {
        try {
            if (pretty) { 
                return new ObjectMapper().writer().withDefaultPrettyPrinter().writeValueAsString(this); 
            }
            return new ObjectMapper().writer().withPrettyPrinter(null).writeValueAsString(this);
        } catch (JsonGenerationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JsonMappingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public Collection<ExcelWorksheet> getSheets() {
        return this.sheets;
    }

    public void setSheets(final Collection<ExcelWorksheet> sheets) {
        this.sheets = sheets;
    }
}
