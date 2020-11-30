package xyz.kingsword.course.util;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.util.StringUtil;
import xyz.kingsword.course.pojo.Book;

/**
 * 负责将书籍接口的数据同步过来，数据仅供参考，需要老师进行修改
 * 接口文档地址：https://market.aliyun.com/products/57126001/cmapi013556.html?spm=5176.2020520132.101.1.55447218BEOgKG
 */
public class BookUtil {

    /**
     * 接口地址
     */
    private final static String AliURL = "https://isbn.market.alicloudapi.com/ISBN?isbn=";
    private final static String BinURL = "https://api.binstd.com/isbn/query?appkey=f96709487fe923c4&isbn=";
    private final static String AUTHORIZATION = "AppCode 054484d7fd8a49a09952dba6b44f9b0d";

    /**
     * 根据ISBN封装book实体
     *
     * @param isbn 长度：十位或十三位（存在少数九位的情况，但接口仅支持十位或十三位）
     */
    public static Book getBook(String isbn) {
        Book book = null;
        if (isbn == null || isbn.isEmpty()) {
            return book;
        }
        if(isbn.contains("-")) {
            isbn = isbn.replace("-", "");
        }
        //先用阿里接口查询
        HttpResponse httpResponse = HttpUtil.createGet(AliURL + isbn).header("Authorization", AUTHORIZATION).execute();

        String jsonString = httpResponse.body();
        JSONObject jsonObject = JSON.parseObject(jsonString);
        int status = jsonObject.getInteger("error_code");

        if (status != 0) {
            //使用进制数据接口
        httpResponse = HttpUtil.createGet(BinURL + isbn).execute();
        jsonString = httpResponse.body();
        jsonObject = JSON.parseObject(jsonString);
        status = jsonObject.getInteger("status");
        if(status!=0) {
            return book;
        }else {
            JSONObject bookObject = jsonObject.getJSONObject("result");
            book = new Book();
            book.setName(bookObject.getString("title"));
            book.setAuthor(bookObject.getString("author"));
            book.setPublish(bookObject.getString("publisher"));
            book.setNote(bookObject.getString("summary"));
            book.setPubDate(bookObject.getString("pubdate"));
            book.setEdition(StringUtils.isEmpty(bookObject.getString("edition"))?"1版":bookObject.getString("edition"));
            book.setIsbn(isbn);

            String priceStr = bookObject.getString("price");
            if (priceStr != null && !priceStr.isEmpty()) {
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < priceStr.length(); i++) {
                    if (priceStr.charAt(i) >= 48 && priceStr.charAt(i) <= 57 || priceStr.charAt(i) == 46) {
                        stringBuilder.append(priceStr.charAt(i));
                    }
                }
                book.setPrice(Double.parseDouble(stringBuilder.toString()));
            }
            return book;
        }

        }else {
            JSONObject bookObject = jsonObject.getJSONObject("result");
            book = new Book();
            book.setName(bookObject.getString("title"));
            book.setAuthor(bookObject.getString("author"));
            book.setPublish(bookObject.getString("publisher"));
            book.setNote(bookObject.getString("summary"));
            book.setPubDate(bookObject.getString("pubdate"));
            book.setEdition(StringUtils.isEmpty(bookObject.getString("levelNum"))?"1版":bookObject.getString("levelNum"));
            book.setIsbn(isbn);

            String priceStr = bookObject.getString("price");
            if (priceStr != null && !priceStr.isEmpty()) {
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < priceStr.length(); i++) {
                    if (priceStr.charAt(i) >= 48 && priceStr.charAt(i) <= 57 || priceStr.charAt(i) == 46) {
                        stringBuilder.append(priceStr.charAt(i));
                    }
                }
                book.setPrice(Double.parseDouble(stringBuilder.toString()));
            }
            return book;
        }

    }

    /**
     * 根据isbn只返回书名
     *
     * @param isbn 长度：十位或十三位
     */
    public static String getBookName(String isbn) {
        HttpRequest httpRequest = HttpUtil.createGet(BinURL + isbn);
        HttpResponse httpResponse = httpRequest.execute();
        String jsonString = httpResponse.body();
        JSONObject jsonObject = JSONObject.parseObject(jsonString);
        int status = jsonObject.getInteger("status");
        return status == 0 ? jsonObject.getJSONObject("result").getString("title") : "";
    }
}
