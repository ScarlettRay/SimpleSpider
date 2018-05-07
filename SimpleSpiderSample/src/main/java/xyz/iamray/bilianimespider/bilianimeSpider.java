package xyz.iamray.bilianimespider;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import xyz.iamray.bilianimespider.bean.Anime;
import xyz.iamray.common.SimpleSpider;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Properties;

/**
 * Created by liuwenrui on 2018/5/5
 * 爬取哔哩哔哩的动漫并储存到Excel中
 */
public class bilianimeSpider {

    private static final String EXCEL_XLS = "xls";
    private static final String EXCEL_XLSX = "xlsx";

    /**
     * 开始爬取
     */
    public static void main(String[] args){

        String excelPath = "/Users/liuwenrui/Documents/test.xls";

        String url = "https://bangumi.bilibili.com/web_api/season/index_global?page_size=20&version=0&is_finish=0&start_year=0&tag_id=&index_type=1&index_sort=0&quarter=0&page=";

        Properties pro = new Properties();
        int pageNum = 1;
        while(true){
            List<Anime> animes = SimpleSpider.make().setHeader(iamray.common.SpiderConstant.DefaultHeader).crawlURL(url+pageNum,null).crawl(new bilianimeSpiderAction());

            for (Anime anime : animes) {
                pro.put("anime",anime);
                SimpleSpider.make().setHeader(iamray.common.SpiderConstant.DefaultHeader).setProperty(pro)
                        .crawlURL(anime.getUrl(),null).crawl(new AnimeDetailSpiderAction());
            }

            writeExcel(animes,excelPath,Anime.class);

            if(animes.size()<20)return;
            pageNum++;
        }




    }
/*
    @Test
    public void testDetail(){
        OutputStream out = null;
            // 读取Excel文档
            File finalXlsxFile = new File("/Users/liuwenrui/Documents/test.xls");
        Workbook workBook = null;
        try {
            workBook = getWorkbok(finalXlsxFile);
            // sheet 对应一个工作页
            Sheet sheet = workBook.getSheetAt(1);
            int rowNumber = sheet.getLastRowNum();    // 第一行从0开始算
            System.out.println("原始数据总行数，除属性列：" + rowNumber);
            for (int i = 1; i <= rowNumber; i++) {
                Row row = sheet.getRow(i);
                sheet.removeRow(row);
            }
            // 创建文件输出流，准备输出电子表格：这个必须有，否则你在sheet上做的任何操作都不会有效
            out =  new FileOutputStream("/Users/liuwenrui/Documents/test.xls");
            workBook.write(out);
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            try {
                if(out != null){
                    out.flush();
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
*/
    private static <T> void writeExcel(List<T> dataList, String finalXlsxPath, Class<T> clazz){
        OutputStream out = null;
        boolean isnew = false;
        try {
            // 读取Excel文档
            File finalXlsxFile = new File(finalXlsxPath);
            Workbook workBook = getWorkbok(finalXlsxFile);
            // sheet 对应一个工作页
            Sheet sheet = workBook.getSheetAt(1);
            /**
             * 删除原有数据，除了属性列

            int rowNumber = sheet.getLastRowNum();    // 第一行从0开始算
            System.out.println("原始数据总行数，除属性列：" + rowNumber);
            for (int i = 1; i <= rowNumber; i++) {
                Row row = sheet.getRow(i);
                sheet.removeRow(row);
            }
             */
            int rowNumber = sheet.getLastRowNum();    // 第一行从0开始算
            if(rowNumber==0){
                isnew = true;
               Row row =  sheet.createRow(0);
               Field[] fields = clazz.getDeclaredFields();
                for (int i=0;i<fields.length;i++) {
                    Annotation[] an = fields[i].getAnnotations();
                    for (Annotation annotation : an) {
                        Cell cellan = (Cell) annotation;
                        org.apache.poi.ss.usermodel.Cell cell = row.createCell(i);
                        cell.setCellValue(cellan.value());
                    }
                }
            }
            // 创建文件输出流，输出电子表格：这个必须有，否则你在sheet上做的任何操作都不会有效
            out =  new FileOutputStream(finalXlsxPath);
            workBook.write(out);
            /**
             * 往Excel中写新数据
             */
            int pos = isnew ? 1:sheet.getLastRowNum()+1;
            for (int j = 0; j < dataList.size(); j++) {
                // 创建一行：从第二行开始，跳过属性列
                Row row = sheet.createRow(j + pos);
                // 得到要插入的每一条记录
                // 在一行内循环
                T anime = dataList.get(j);
                Field[] fields = clazz.getDeclaredFields();
                for(int q = 0;q<fields.length;q++){
                    org.apache.poi.ss.usermodel.Cell cell = row.createCell(q);
                    fields[q].setAccessible(true);
                    Object value  = fields[q].get(anime);
                    cell.setCellValue(value==null?"":value.toString());
                }
            }

            // 创建文件输出流，准备输出电子表格：这个必须有，否则你在sheet上做的任何操作都不会有效
            out =  new FileOutputStream(finalXlsxPath);
            workBook.write(out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            try {
                if(out != null){
                    out.flush();
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("数据导出成功");
    }

    /**
     * 判断Excel的版本,获取Workbook
     * @return
     * @throws IOException
     */
    private static Workbook getWorkbok(File file) throws IOException {
        Workbook wb = null;
        FileInputStream in = new FileInputStream(file);
        if(file.getName().endsWith(EXCEL_XLS)){     //Excel&nbsp;2003
            wb = new HSSFWorkbook(in);
        }else if(file.getName().endsWith(EXCEL_XLSX)){    // Excel 2007/2010
            wb = new XSSFWorkbook(in);
        }
        return wb;
    }

}
