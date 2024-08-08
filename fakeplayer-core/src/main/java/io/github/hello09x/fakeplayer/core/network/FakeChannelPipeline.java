package io.github.hello09x.fakeplayer.core.network;

import io.netty.channel.*;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.EventExecutorGroup;
import org.jetbrains.annotations.NotNull;

import java.net.SocketAddress;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author tanyaofei
 * @since 2024/8/8
 **/
public class FakeChannelPipeline implements ChannelPipeline {

    private final Channel channel;

    public FakeChannelPipeline(Channel channel) {
        this.channel = channel;
    }

    @Override
    public ChannelPipeline addFirst(String name, ChannelHandler handler) {
        return this;
    }

    @Override
    public ChannelPipeline addFirst(EventExecutorGroup group, String name, ChannelHandler handler) {
        return this;
    }

    @Override
    public ChannelPipeline addLast(String name, ChannelHandler handler) {
        return this;
    }

    @Override
    public ChannelPipeline addLast(EventExecutorGroup group, String name, ChannelHandler handler) {
        return this;
    }

    @Override
    public ChannelPipeline addBefore(String baseName, String name, ChannelHandler handler) {
        return this;
    }

    @Override
    public ChannelPipeline addBefore(EventExecutorGroup group, String baseName, String name, ChannelHandler handler) {
        return this;
    }

    @Override
    public ChannelPipeline addAfter(String baseName, String name, ChannelHandler handler) {
        return this;
    }

    @Override
    public ChannelPipeline addAfter(EventExecutorGroup group, String baseName, String name, ChannelHandler handler) {
        return this;
    }

    @Override
    public ChannelPipeline addFirst(ChannelHandler... handlers) {
        return this;
    }

    @Override
    public ChannelPipeline addFirst(EventExecutorGroup group, ChannelHandler... handlers) {
        return this;
    }

    @Override
    public ChannelPipeline addLast(ChannelHandler... handlers) {
        return this;
    }

    @Override
    public ChannelPipeline addLast(EventExecutorGroup group, ChannelHandler... handlers) {
        return this;
    }

    @Override
    public ChannelPipeline remove(ChannelHandler handler) {
        return this;
    }

    @Override
    public ChannelHandler remove(String name) {
        return null;
    }

    @Override
    public <T extends ChannelHandler> T remove(Class<T> handlerType) {
        return null;
    }

    @Override
    public ChannelHandler removeFirst() {
        return null;
    }

    @Override
    public ChannelHandler removeLast() {
        return null;
    }

    @Override
    public ChannelPipeline replace(ChannelHandler oldHandler, String newName, ChannelHandler newHandler) {
        return this;
    }

    @Override
    public ChannelHandler replace(String oldName, String newName, ChannelHandler newHandler) {
        return null;
    }

    @Override
    public <T extends ChannelHandler> T replace(Class<T> oldHandlerType, String newName, ChannelHandler newHandler) {
        return null;
    }

    @Override
    public ChannelHandler first() {
        return null;
    }

    @Override
    public ChannelHandlerContext firstContext() {
        return null;
    }

    @Override
    public ChannelHandler last() {
        return null;
    }

    @Override
    public ChannelHandlerContext lastContext() {
        return null;
    }

    @Override
    public ChannelHandler get(String name) {
        return null;
    }

    @Override
    public <T extends ChannelHandler> T get(Class<T> handlerType) {
        return null;
    }

    @Override
    public ChannelHandlerContext context(ChannelHandler handler) {
        return null;
    }

    @Override
    public ChannelHandlerContext context(String name) {
        return null;
    }

    @Override
    public ChannelHandlerContext context(Class<? extends ChannelHandler> handlerType) {
        return null;
    }

    @Override
    public Channel channel() {
        return this.channel;
    }

    @Override
    public List<String> names() {
        return List.of();
    }

    @Override
    public Map<String, ChannelHandler> toMap() {
        return Map.of();
    }

    @Override
    public ChannelPipeline fireChannelRegistered() {
        return this;
    }

    @Override
    public ChannelPipeline fireChannelUnregistered() {
        return this;
    }

    @Override
    public ChannelPipeline fireChannelActive() {
        return this;
    }

    @Override
    public ChannelPipeline fireChannelInactive() {
        return this;
    }

    @Override
    public ChannelPipeline fireExceptionCaught(Throwable cause) {
        return this;
    }

    @Override
    public ChannelPipeline fireUserEventTriggered(Object event) {
        return this;
    }

    @Override
    public ChannelPipeline fireChannelRead(Object msg) {
        ReferenceCountUtil.release(msg);
        return this;
    }

    @Override
    public ChannelPipeline fireChannelReadComplete() {
        return this;
    }

    @Override
    public ChannelPipeline fireChannelWritabilityChanged() {
        return this;
    }

    @Override
    public ChannelFuture bind(SocketAddress localAddress) {
        return newSucceededFuture();
    }

    @Override
    public ChannelFuture connect(SocketAddress remoteAddress) {
        return newSucceededFuture();
    }

    @Override
    public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress) {
        return newSucceededFuture();
    }

    @Override
    public ChannelFuture disconnect() {
        return newSucceededFuture();
    }

    @Override
    public ChannelFuture close() {
        return newSucceededFuture();
    }

    @Override
    public ChannelFuture deregister() {
        return newSucceededFuture();
    }

    @Override
    public ChannelFuture bind(SocketAddress localAddress, ChannelPromise promise) {
        promise.setSuccess();
        return promise;
    }

    @Override
    public ChannelFuture connect(SocketAddress remoteAddress, ChannelPromise promise) {
        promise.setSuccess();
        return promise;
    }

    @Override
    public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
        promise.setSuccess();
        return promise;
    }

    @Override
    public ChannelFuture disconnect(ChannelPromise promise) {
        promise.setSuccess();
        return promise;
    }

    @Override
    public ChannelFuture close(ChannelPromise promise) {
        promise.setSuccess();
        return promise;
    }

    @Override
    public ChannelFuture deregister(ChannelPromise promise) {
        promise.setSuccess();
        return promise;
    }

    @Override
    public ChannelOutboundInvoker read() {
        return null;
    }

    @Override
    public ChannelFuture write(Object msg) {
        ReferenceCountUtil.release(msg);
        return newSucceededFuture();
    }

    @Override
    public ChannelFuture write(Object msg, ChannelPromise promise) {
        ReferenceCountUtil.release(msg);
        promise.setSuccess();
        return promise;
    }

    @Override
    public ChannelPipeline flush() {
        return this;
    }

    @Override
    public ChannelFuture writeAndFlush(Object msg, ChannelPromise promise) {
        ReferenceCountUtil.release(msg);
        promise.setSuccess();
        return promise;
    }

    @Override
    public ChannelFuture writeAndFlush(Object msg) {
        ReferenceCountUtil.release(msg);
        return newSucceededFuture();
    }

    @Override
    public ChannelPromise newPromise() {
        return new DefaultChannelPromise(this.channel);
    }

    @Override
    public ChannelProgressivePromise newProgressivePromise() {
        return null;
    }

    @Override
    public ChannelFuture newSucceededFuture() {
        var promise = new DefaultChannelPromise(this.channel);
        promise.setSuccess(null);
        return promise;
    }

    @Override
    public ChannelFuture newFailedFuture(Throwable cause) {
        var promise = new DefaultChannelPromise(this.channel);
        promise.setFailure(cause);
        return promise;
    }

    @Override
    public ChannelPromise voidPromise() {
        var promise = new DefaultChannelPromise(this.channel);
        promise.setSuccess(null);
        return promise;
    }

    @NotNull
    @Override
    public Iterator<Map.Entry<String, ChannelHandler>> iterator() {
        return Collections.<String, ChannelHandler>emptyMap().entrySet().iterator();
    }

}
