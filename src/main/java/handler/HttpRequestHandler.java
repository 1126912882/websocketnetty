package handler;

import bean.ContentBean;
import bean.Person;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import com.alibaba.fastjson.JSON;
import sysconst.HTTPConst;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;

/**
 * Created by 吴樟 on www.haixiangzhene.xyz
 * 2017/11/13.
 */
public class HttpRequestHandler extends SimpleChannelInboundHandler<HttpMessage>{

    private static Logger logger=Logger.getLogger(HttpRequestHandler.class);

    @Override
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, HttpMessage fullHttpRequest) throws Exception {
        logger.info(fullHttpRequest.getProtocolVersion());

    }

    public DefaultFullHttpResponse flushJSON(ChannelHandlerContext ctx) throws UnsupportedEncodingException {
        ByteBuf json=ctx.alloc().buffer();
        Person person=new Person();
        person.setAge(12); person.setName("吴樟");
        String personStr=JSON.toJSONString(person);
        byte[] bytes=personStr.getBytes("utf-8");
        json.writeBytes(bytes);
        DefaultFullHttpResponse fullHttpResponse=new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1, HttpResponseStatus.OK,json
        );
        fullHttpResponse.headers()
                .add(HttpHeaders.Names.CONTENT_TYPE,"application/json;charset=UTF-8")
                .add(HttpHeaders.Names.CONTENT_LENGTH,bytes.length);
        return fullHttpResponse;
    }

    public ContentBean dispatcherUrl(HttpRequest request) throws IOException {
        String url=request.getUri();
        String path=url.substring(1);
        int suffix=url.lastIndexOf(".");
        String suff=url.substring(suffix+1,url.length());
        URL url1=this.getClass().getResource("/static/"+path);
        if (url1==null){
            url1=this.getClass().getResource("/static/404.html");
        }
        byte[] bytes=IOUtils.toByteArray(url1);
        ContentBean contentBean=new ContentBean();
        contentBean.setBytes(bytes);

            if (HTTPConst.CSSTYPE.equals(suff))
                contentBean.setContentType(HTTPConst.CSS);
            else if (HTTPConst.HTMLTYPE.equals(suff))
                contentBean.setContentType(HTTPConst.HTML);
            else if (HTTPConst.JSTYPE.equals(suff))
                contentBean.setContentType(HTTPConst.JS);
            else if (HTTPConst.SVGTYPE.equals(suff))
                contentBean.setContentType(HTTPConst.XML);
            else
                contentBean.setContentType(HTTPConst.HTML);

        return contentBean;
    }

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object msg) throws Exception {
        ByteBuf html=channelHandlerContext.alloc().buffer();
        ContentBean contentBean=null;
        if (msg instanceof DefaultHttpRequest){
            DefaultHttpRequest defaultHttpRequest= (DefaultHttpRequest) msg;
            contentBean=dispatcherUrl(defaultHttpRequest);
            html.writeBytes(contentBean.getBytes());
        }
        FullHttpResponse response=new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,HttpResponseStatus.OK,html
        );
        if (contentBean!=null) {
            response.headers().add(HttpHeaders.Names.CONTENT_TYPE, contentBean.getContentType());
        }
        channelHandlerContext.write(response);
        channelHandlerContext.flush();
        channelHandlerContext.close();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }
}
