package com.inn.cafe.serviceImple;

import com.inn.cafe.JWT.JwtFilter;
import com.inn.cafe.POJO.Bill;
import com.inn.cafe.constants.CafeConstant;
import com.inn.cafe.dao.BillDao;
import com.inn.cafe.service.BillService;
import com.inn.cafe.utils.CafeUtils;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.util.Map;
import java.util.stream.Stream;

@Service
@Slf4j
public class BillServiceImple implements BillService {

    @Autowired
    JwtFilter jwtFilter;
    @Autowired
    BillDao billDao;
    @Override
    public ResponseEntity<String> generateReport(Map<String, Object> reqMap) {
        log.info("Inside generate report");
        try {

            String fileName;
            if(validateRequestMap(reqMap)){
                if(reqMap.containsKey("isGenerate") && !(Boolean) reqMap.get("isGenerate")){
                    fileName = (String) reqMap.get("uuid");
                }else{
                    fileName = CafeUtils.getUuid();
                    reqMap.put("uuid",fileName);
                    insertBill(reqMap);
                }

                String data = "Name: "+reqMap.get("name")+"\n"+
                        "Contact Number: "+reqMap.get("contactNumber")+"\n"+
                        "Email: "+reqMap.get("email")+"\n"+
                        "Payment Method: "+reqMap.get("paymentMethod")+"\n";
                Document document = new Document();
                PdfWriter.getInstance(document,new FileOutputStream(CafeConstant.STORE_LOCATION_PATH+"\\"+fileName+".pdf"));
                document.open();
                setRectangleInPdf(document);

                Paragraph chunk = new Paragraph("Cafe Management System",getFont("Header"));

                chunk.setAlignment(Element.ALIGN_CENTER);

                document.add(chunk);
                Paragraph paragraph = new Paragraph(data+"\n \n",getFont("Data"));
                document.add(paragraph);

                PdfPTable table = new PdfPTable(5);
                table.setWidthPercentage(100);

                addTableHeader(table);

                JSONArray jsonArray = CafeUtils.getJSONArrayFromData((String) reqMap.get("productDetails"));

                for(int i=0;i<jsonArray.length();i++){
                    addRow(table,CafeUtils.getMapFromJson(jsonArray.getString(i)));
                }
                document.add(table);
                Paragraph footer = new Paragraph("Total: "+reqMap.get("totalAmount")+"\n"+
                       "Thank you for visiting. Please Visit again.",getFont("Data"));
                document.add(footer);
                document.close();
                return new ResponseEntity<>("{\"uuid\":\""+fileName+"\"}",HttpStatus.OK);

            }
            return CafeUtils.getResponseEntity("Required Data Not Found",HttpStatus.BAD_REQUEST);

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstant.SOMETHING_WENT_WRONG, HttpStatus.BAD_REQUEST);
    }

    private void addRow(PdfPTable table, Map<String, Object> data) {
        log.info("inside add row");
        table.addCell((String) data.get("name"));
        table.addCell((String) data.get("category"));
        table.addCell((String) data.get("quantity"));
        table.addCell(Double.toString((Double) data.get("price")));
        table.addCell(Double.toString((Double) data.get("total")));
    }

    private void addTableHeader(PdfPTable table) {
        log.info("addTableHeader");
        Stream.of("Name","Category","Quantity","Price","Sub Total")
                .forEach(columnTitle->{
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setBorderWidth(2);
                    header.setPhrase(new Phrase(columnTitle));
                    header.setBackgroundColor(BaseColor.YELLOW);
                    header.setHorizontalAlignment(Element.ALIGN_CENTER);
                    header.setVerticalAlignment(Element.ALIGN_CENTER);
                    table.addCell(header);

                });
    }

    private Font getFont(String type) {
        log.info("getFont");
        switch (type){
            case "Header":
                Font headerFont=FontFactory.getFont(FontFactory.HELVETICA_BOLDOBLIQUE,18,BaseColor.BLACK);
                headerFont.setStyle(Font.BOLD);
                return headerFont;
            case "Date":
                Font font=FontFactory.getFont(FontFactory.TIMES_ROMAN,11,BaseColor.BLACK);
                font.setStyle(Font.BOLD);
            default:
                return new Font();


        }
    }

    private void setRectangleInPdf(Document document) throws DocumentException {
        log.info("Inside setRectangleInPdf");
        Rectangle rectangle = new Rectangle(577,825,18,15);

        rectangle.enableBorderSide(1);
        rectangle.enableBorderSide(2);
        rectangle.enableBorderSide(4);
        rectangle.enableBorderSide(8);
        rectangle.setBorderColor(BaseColor.BLACK);
        rectangle.setBorderWidth(1);
        document.add(rectangle);


    }

    private void insertBill(Map<String, Object> reqMap) {
        try{
            Bill bill = new Bill();
            bill.setUUid((String) reqMap.get("uuid"));
            bill.setName((String) reqMap.get("name"));
            bill.setEmail((String) reqMap.get("email"));
            bill.setTotal(Integer.parseInt((String)reqMap.get("totalAmount")));
            bill.setContactNumber((String) reqMap.get("contactNumber"));
            bill.setCreatedBy(jwtFilter.getUserName());
            bill.setPaymentMethod((String) reqMap.get("paymentMethod"));
            bill.setProductDetails((String) reqMap.get("productDetails"));

            billDao.save(bill);

        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private boolean validateRequestMap(Map<String, Object> reqMap) {
        return reqMap.containsKey("name") &&
                reqMap.containsKey("contactNumber") &&
                reqMap.containsKey("email") &&
                reqMap.containsKey("paymentMethod") &&
                reqMap.containsKey("productDetails") &&
                reqMap.containsKey("totalAmount");
    }
}
