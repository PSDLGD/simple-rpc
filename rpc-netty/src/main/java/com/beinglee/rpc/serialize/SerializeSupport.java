package com.beinglee.rpc.serialize;

import com.beinglee.rpc.spi.ServiceSupport;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class SerializeSupport {

    /**
     * 对象序列化的时候，我们得知道当前对象用哪种序列化方式。
     * serializerMap就是记录这种映射关系的。
     */
    private static final Map<Class<?>, Serializer<?>> serializerMap = new HashMap<>();

    /**
     * 反序列化的时候，我们从byte数组里解析出当前对象的类型，然后我们得知道当前类型对应的对象class。
     * treeMap就是记录这种映射关系的。
     */
    private static final Map<Byte, Class<?>> treeMap = new HashMap<>();


    static {
        Collection<Serializer> serializers = ServiceSupport.loadAll(Serializer.class);
        registerTypes(serializers);
    }

    private static void registerTypes(Collection<Serializer> serializers) {
        for (Serializer serializer : serializers) {
            serializerMap.put(serializer.getSerializeClass(), serializer);
            treeMap.put(serializer.type(), serializer.getSerializeClass());
            log.info("Found Serializer,class:{},type:{}", serializer.getSerializeClass().getCanonicalName(), serializer.type());
        }
    }


    public static <T> byte[] serialize(T entry) {
        @SuppressWarnings("unchecked")
        Serializer<T> serializer = (Serializer<T>) serializerMap.get(entry.getClass());
        if (serializer == null) {
            throw new SerializeException();
        }
        byte[] bytes = new byte[serializer.size(entry) + 1];
        bytes[0] = serializer.type();
        serializer.serialize(entry, bytes, 1, bytes.length - 1);
        return bytes;
    }

    public static <E> E parse(byte[] bytes) {
        return parse(bytes, 0, bytes.length);
    }

    private static <E> E parse(byte[] bytes, int offset, int length) {
        byte type = bytes[0];
        @SuppressWarnings("unchecked")
        Class<E> eClass = (Class<E>) treeMap.get(type);
        if (eClass == null) {
            throw new SerializeException();
        }
        return parse(bytes, offset + 1, length - 1, eClass);
    }

    @SuppressWarnings("unchecked")
    private static <E> E parse(byte[] bytes, int offset, int length, Class eClass) {
        Serializer<?> serializer = serializerMap.get(eClass);
        if (serializer == null) {
            throw new SerializeException();
        }
        Object entry = serializer.parse(bytes, offset, length);
        if (entry.getClass().isAssignableFrom(eClass)) {
            return (E) entry;
        } else {
            throw new SerializeException();
        }
    }
}