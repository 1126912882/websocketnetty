import handler.HttpRequestHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import org.springframework.web.context.ContextLoaderListener;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;


/**
 * Created by 吴樟 on www.haixiangzhene.xyz
 * 2017/11/13.
 */
public class NettyServer {


    public void run(){
        EventLoopGroup workgroup=new NioEventLoopGroup(8);
        EventLoopGroup bossgroup=new NioEventLoopGroup(2);
        ServerBootstrap bootstrap=new ServerBootstrap();
        bootstrap.group(bossgroup,workgroup)
                .channel(NioServerSocketChannel.class)
                //NioServerSocketChannel针对于半包发送的进行了处理
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline()
                                .addLast(new HttpServerCodec())
                                .addLast(new ChunkedWriteHandler())
                                .addLast(new HttpRequestHandler());
                    }
                });
        ChannelFuture future= null;
        try {
            future = bootstrap.bind(8081).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            workgroup.shutdownGracefully();
            bossgroup.shutdownGracefully();
        }
    }

    public static void main(String[] args){
        new NettyServer().run();
    }
}
