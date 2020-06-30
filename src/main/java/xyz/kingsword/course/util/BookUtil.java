package xyz.kingsword.course.util;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import xyz.kingsword.course.pojo.Book;

/**
 * 负责将书籍接口的数据同步过来，数据仅供参考，需要老师进行修改
 * 接口文档地址：https://market.aliyun.com/products/57126001/cmapi013556.html?spm=5176.2020520132.101.1.55447218BEOgKG
 */
public class BookUtil {

    /**
     * 接口地址
     */
    private final static String URL = "http://jisuisbn.market.alicloudapi.com/isbn/query?isbn=";
    private final static String AUTHORIZATION = "APPCODE 627300f68c00416bab6ffbefdf02da3c";

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
        isbn = isbn.replace("-", "");
        HttpRequest httpRequest = HttpUtil.createGet(URL + isbn).header("Authorization", AUTHORIZATION);
        HttpResponse httpResponse = httpRequest.execute();
        String jsonString = httpResponse.body();
        JSONObject jsonObject = JSON.parseObject(jsonString);
        int status = jsonObject.getInteger("status");
        if (status != 0) {
            return book;
        }
        JSONObject bookObject = jsonObject.getJSONObject("result");
        book = new Book();
        book.setName(bookObject.getString("title"));
        book.setAuthor(bookObject.getString("author"));
        book.setPublish(bookObject.getString("publisher"));
        book.setNote(bookObject.getString("summary"));
        book.setPubDate(bookObject.getString("pubdate"));
        book.setEdition(bookObject.getString("edition"));
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

    /**
     * 根据isbn只返回书名
     *
     * @param isbn 长度：十位或十三位
     */
    public static String getBookName(String isbn) {
        HttpRequest httpRequest = HttpUtil.createGet(URL + isbn).header("Authorization", AUTHORIZATION);
        HttpResponse httpResponse = httpRequest.execute();
        String jsonString = httpResponse.body();
        JSONObject jsonObject = JSONObject.parseObject(jsonString);
        int status = jsonObject.getInteger("status");
        return status == 0 ? jsonObject.getJSONObject("result").getString("title") : "";
    }
}
