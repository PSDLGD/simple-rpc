package com.beinglee.rpc.api;

import java.io.Closeable;
import java.net.URI;

/**
 * @author zhanglu
 * @date 2020/6/2 20:28
 */
public interface RpcAccessPoint extends Closeable {

    /**
     * 客户端获取远程服务的引用
     *
     * @param uri          远程服务地址
     * @param serviceClass 服务的接口类的Class
     * @param <T>          服务接口的类型
     * @return 远程服务引用
     */
    <T> T getRemoteService(URI uri, Class<T> serviceClass);


    /**
     * 服务端注册服务的实现实例
     *
     * @param service      实现实例
     * @param serviceClass 服务接口类的class
     * @param <T>          服务接口类型
     * @return 服务地址
     */
    <T> URI addServiceProvider(T service, Class<T> serviceClass);

    /**
     * 服务端启动RPC框架，监听接口，开始提供远程服务。
     *
     * @return 服务实例，用于程序停止的时候安全关闭服务。
     * @throws Exception 启动异常
     */
    Closeable startServer() throws Exception;

    /**
     * 获取注册中心的引用
     *
     * @param nameServiceUri 注册中心地址
     * @return 注册中心的引用
     */
    default NameService getNameService(URI nameServiceUri) {
        return null;
    }

}
