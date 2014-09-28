
package com.lzc.pineapple.util;

import com.lzc.pineapple.entity.StartPageEntity;
import com.lzc.pineapple.volley.Response;
import com.lzc.pineapple.volley.VolleyError;


public class UrlHelper {

    public static final String URL_HOST = "http://v.html5.qq.com/";

    /**
     * 特色专区 特色专区，出现在两个地方1）首页-电影上方；2）分类-第一行
     * http://115.28.234.30:8080/ajax?m=get_recommend_video
     * 
     * {@link}RecommendVideo 对应实体类
     */
    public static final String URL_SPECILE_ZONE = "http://115.28.234.30:8080/ajax?m=get_recommend_video";

    /**
     * 首页推荐 
     * http://v.html5.qq.com/ajax?action=getStartPage
     * 
     * {@link}StartPageEntity 对应实体类
     */
    public static final String URL_HOME_PAGE = URL_HOST + "ajax?action=getStartPage";

    /**
     * 特色大片(暂无！) 
     * http://v.html5.qq.com/ajax?action=getDaPian
     */
    public static final String URL_S_DAPIAN = URL_HOST + "ajax?action=getDaPian";

    /**
     * 取某个分类接口
     * http://v.html5.qq.com/ajax?action=getGroupVideoList&groupid=2&pagenum=1&pagesize=21&seqid=2&sort=2
     * 
     * @param groupid 类别
     * 电视剧：13 电影：12 动漫：15 综艺：19 新闻：14 美女：18 娱乐：30 搞笑：17 特色大片：40
     * @param pagenum 页号
     * @param pagesize 一页视频个数
     * @param seqid 序列号，请求递增
     * @param sort 排序类型  最热：2 最新：3
     * @param siftcon id1_id2_id3, 例：116_0_0 {@link} ids.txt 分类过滤使用
     * http://v.html5.qq.com/ajax?action=getGroupVideoList&groupid=2&pagenum=0&pagesize=21&seqid=2&siftcon=116_0_0&sort=2
     * 
     * {@link}GroupListEntity 对应实体类
     */
    public static final String URL_GROUP_LIST_BYID = URL_HOST + "ajax?action=getGroupVideoList";

    /**
     * 取Video详情接口
     * http://v.html5.qq.com/ajax?action=getVideoInfo&vid=13813&actsrc=1&actkey=14&needurl=1
     * 
     * @param vid：视频ID
     * @param actsrc 暂时不传
     * @param actkey 暂时不传
     * @param needurl 暂时不传
     * 
     * {@link}VideoInfoEntity 对应实体类
     */
    public static final String URL_VIDEO_INFO = URL_HOST + "ajax?action=getVideoInfo";

    /**
     * 搜索接口
     * http://v.html5.qq.com/ajax?action=search&pagesize=10&pagenum=0&content=%E7%94%B7%E4%BA%BA%E5%9B%9B%E5%8D%81%E8%A6%81%E5%87%BA%E5%AB%81
     * 
     * @param pagenum 页号
     * @param pagesize 一页视频个数
     * @param content 搜索关键字
     * 
     * {@link}SearchResultEntity 对应实体类
     */
    public static final String URL_SEARCH_LIST = URL_HOST + "ajax?action=search";

    /**
     * 播放接口
     * http://v.html5.qq.com/redirect?vid=2000752&type=2&src=3&num=1&actsrc=3
     * 
     * @param vid 视频ID
     * @param type 1,电影；2，连续剧
     * @param src 播放来源
     * @param num 第N集
     * @param actsrc 未知
     * 
     */
    public static final String URL_PLAY_HTML = URL_HOST + "redirect?";
    
    /**
     * 使用Velloy请求首页示例
     */
    public static void request(){
        NetworkRequest.get(URL_HOME_PAGE, StartPageEntity.class, new Response.Listener<StartPageEntity>() {

            @Override
            public void onResponse(StartPageEntity response) {
                // TODO Auto-generated method stub
                
            }
            
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                
            }
            
        });
    }
//    private static final String NEW_HOST = "http://www.shipinshenqi.com:8080/";
    private static final String NEW_HOST = "http://121.42.11.156:8080/";
    /**
     * 新用户注册接口
     * http://www.shipinshenqi.com:8080/ajax?m=user_register&user_id=123&device_id=123&user_type=4&user_site=0
     * 
     * @param user_id 用户id
     * @param device_id 设备id，设备id和用户id保持一致
     * @param user_type 用户类型 视频业务为3
     * @param user_site 用户来源 自有用户为0 第三方登陆用户为非0
     * 
     */
    public static final String URL_USER_REGISTER = NEW_HOST + "ajax?m=user_register";
    /**
     * 用户信息查询
     * http://www.shipinshenqi.com:8080/ajax?m=user_search&user_id=123&device_id=123&user_type=4&user_site=0
     * @param user_id 用户id
     * @param device_id 设备id，设备id和用户id保持一致
     * @param user_type 用户类型 视频业务为3
     * @param user_site 用户来源 自有用户为0 第三方登陆用户为非0
     * 
     */
    public static final String URL_USER_SEARCH = NEW_HOST + "ajax?m=user_search";
   
	/**
     * 用户积分购买
     * http://www.shipinshenqi.com:8080/ajax?m=commit_job_video_android&user_id=123&device_id=123&user_type=4&user_site=0&src=0
     * 
     * @param user_id 用户id
     * @param device_id 设备id，设备id和用户id保持一致
     * @param user_type 用户类型 视频业务为3
     * @param user_site 用户来源 自有用户为0 第三方登陆用户为非0
     * @param src 1＝微信； 2=新浪微博； 3=QQ空间
     */
    public static final String URL_BUY_SCORE = NEW_HOST + "ajax?m=commit_job_video_android";
    /**
     * 用户支付积分看视频
     * http://http//www.shipinshenqi.com:8080/ajax?m=commit_trade&user_id=123&device_id=123&&pay_id=123&pay_score=100&pay_type=3&user_type=4&user_site=0
     * 
     * @param user_id 用户id
     * @param device_id 设备id，设备id和用户id保持一致
     * @param user_type 用户类型 视频业务为3
     * @param user_site 用户来源 自有用户为0 第三方登陆用户为非0
     * @param pay_id  视频id 可以通过 list_recommend_video接口获取视频id
     * @param pay_score 视频兑换积分可以通过 list_recommend_video接口获取视频积分
     * @param pay_type 用户类型 视频业务为3
     */
    public static final String URL_CONSUMER_SCORE = NEW_HOST +"ajax?m=commit_trade";
    
	/**
     * 特色专区列表
     * http://www.shipinshenqi.com:8080/ajax?m=list_recommend_video&user_id=E8C7423F-AFF6-47C5-B77A-560DC9A51C5B&device_id=E8C7423F-AFF6-47C5-B77A-560DC9A51C5B&user_type=3&user_site=0
     * @param user_id 用户id
     * @param device_id 设备id，设备id和用户id保持一致
     * @param user_type 用户类型 视频业务为3
     * @param user_site 用户来源 自有用户为0 第三方登陆用户为非0
     */
    public static final String URL_RECOMMEND_LIST = NEW_HOST +"ajax?m=list_recommend_video";
    /**
     * 各大视频站的h5页面的视频地址接口
     * @param url 获取的播放地址
     * http://www.shipinshenqi.com:8080/ajax?m=get_video_url&url=http://v.youku.com/v_show/id_XNTUzMDQzODky.html
     */
    public static final String URL_SOURCE_URL = NEW_HOST +"ajax?m=get_video_url";
}
